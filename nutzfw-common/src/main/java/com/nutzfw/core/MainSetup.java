package com.nutzfw.core;

import com.github.threefish.nutz.sqltpl.BeetlSqlTemplteEngineImpl;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.mvc.PreventDuplicateSubmitProcessor;
import com.nutzfw.core.plugin.beetl.GenMySqlFieldTypeFn;
import com.nutzfw.core.plugin.quartz.NutQuartzCronJobFactory;
import com.nutzfw.core.plugin.view.BeetlViewMaker;
import com.nutzfw.modules.monitor.entity.SysOperateLog;
import com.nutzfw.modules.monitor.quartz.job.ApmJob;
import com.nutzfw.modules.sys.biz.DictBiz;
import com.nutzfw.modules.sys.entity.Options;
import com.nutzfw.modules.sys.quartz.job.AutoSendMailJob;
import com.nutzfw.modules.sys.quartz.job.DataImportJob;
import com.nutzfw.modules.sys.quartz.job.DatabaseBackupJob;
import com.nutzfw.modules.sys.quartz.job.UserImportJob;
import org.nutz.Nutz;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.integration.jedis.RedisService;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Encoding;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.quartz.Scheduler;

import java.nio.charset.Charset;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2015/4/2114:20
 */
public class MainSetup extends SystemInit implements Setup {

    static {
        attachType.put("临时文件", "temp");
        attachType.put("头像", "avatar");
        attachType.put("动态数据表", "datatable");
        attachType.put("动态数据表数据导入", "dataimport");
        attachType.put("新闻", "news");
        attachType.put("人员异动", "userchange");

        quartzJobs.add(buildeQuartJob(ApmJob.class, "*/15 * * * * ?", "服务器状态监控服务", "服务器状态监控服务", false, null));
        quartzJobs.add(buildeQuartJob(AutoSendMailJob.class, "*/15 * * * * ?", "自动发送邮件定时任务", "自动发送邮件定时任务", false, null));
        quartzJobs.add(buildeQuartJob(DataImportJob.class, "*/15 * * * * ?", "数据导入任务", "自动检查是否有数据导入，有就自动执行导入", true, null));
        quartzJobs.add(buildeQuartJob(UserImportJob.class, "*/15 * * * * ?", "用户导入任务", "自动检查是否有用户导入，有就自动执行导入", true, null));
        quartzJobs.add(buildeQuartJob(DatabaseBackupJob.class, "0 0 0 3 * ? ", "数据库定时备份任务", "数据库定时备份任务", true, null));

        //这些表不需要自动创建
        tablesFilters.add(SysOperateLog.class);
    }

    private Log log = Logs.get();

    private void initSystem(boolean initSystem, Dao dao, Ioc ioc) {
        if (initSystem) {
            initTable(dao, true);
            PropertiesProxy enumsConf = ioc.get(PropertiesProxy.class, "enumsConf");
            initSystemInfo(dao, enumsConf);
        } else {
            initTable(dao, false);
        }
    }

    @Override
    public void init(NutConfig nutConfig) {
        log.debug("当前Nutz版本：" + Nutz.version());
        /**检查环境,必须运行在UTF-8环境**/
        if (!Charset.defaultCharset().name().equalsIgnoreCase(Encoding.UTF8)) {
            log.error("这个项目必须运行在UTF-8环境下, 请添加 -Dfile.encoding=UTF-8 至 JAVA_OPTS");
            throw new RuntimeException("This project must be run in UTF-8 environment, please add -Dfile.encoding=UTF-8 to JAVA_OPTS");
        } else {
            NutShiro.DefaultUnauthorizedAjax = new NutMap().setv("ok", false).setv("msg", "您需要授权才能进行操作！").setv("type", "user.require.auth");
            NutShiro.DefaultOtherAjax = new NutMap().setv("ok", false).setv("msg", "您需要登录！").setv("type", "user.require.login");
            NutShiro.DefaultUnauthenticatedAjax = new NutMap().setv("ok", false).setv("msg", "您的权限不足！").setv("type", "user.require.unauthorized");
            Ioc ioc = nutConfig.getIoc();
            PropertiesProxy conf = ioc.get(PropertiesProxy.class, "conf");
            BeetlViewMaker.isDev = conf.getBoolean("isDev");
            BeetlViewMaker.productVersion = conf.get("productVersion");
            SystemInit.cleanRedis(ioc.get(RedisService.class));
            Dao dao = ioc.get(Dao.class, "dao");
            this.initSystem(conf.getBoolean("initSystem", false), dao, ioc);
            /**缓存系统信息**/
            Cons.optionsCach = dao.fetch(Options.class, Cnd.where("id", "=", "0"));
            if (Cons.optionsCach == null) {
                throw new RuntimeException("缓存系统信息失败！");
            }
            BeetlViewMaker.updateBeetlGroupTemplate(ioc);
            String[] setups = ioc.getNamesByType(Setup.class);
            if (null != setups) {
                for (String iocName : setups) {
                    Setup setup = ioc.get(Setup.class, iocName);
                    setup.init(nutConfig);
                }
            }
            checkAttachTypeDict(ioc.get(DictBiz.class), attachType);
            addQuartJob(quartzJobs, dao);
            if (conf.getBoolean("initQuartzJob", false)) {
                ioc.get(NutQuartzCronJobFactory.class);
            }
            try {
                PreventDuplicateSubmitProcessor.redisService = ioc.getByType(RedisService.class);
            } catch (Exception e) {
                log.debug("表单防止重复提交：未启用redis,降级采用session");
            }
            BeetlSqlTemplteEngineImpl beetlSqlTemplteEngine = ioc.get(BeetlSqlTemplteEngineImpl.class);
            beetlSqlTemplteEngine.getGt().registerFunction("genMysqlFieldType", new GenMySqlFieldTypeFn());
            //循环获取一次所有IocBean 避免单个服务创建失败，导致服务不可用，错误不好定位
            for (String iocBeanName : ioc.getNames()) {
                try {
                    Class cls = ioc.getType(iocBeanName);
                    //是自己包里的IocBean
                    if (cls.getName().startsWith("com.nutzfw") && cls != NutQuartzCronJobFactory.class) {
                        ioc.get(ioc.getType(iocBeanName), iocBeanName);
                    }
                } catch (ObjectLoadException e) {
                    log.error("应用启动失败！！！", e);
                    throw new RuntimeException("应用启动失败！！！", e);
                }
            }
        }
    }


    @Override
    public void destroy(NutConfig nutConfig) {
        //支持跨模块执行
        String[] setups = nutConfig.getIoc().getNamesByType(Setup.class);
        if (null != setups) {
            for (String iocName : setups) {
                Setup setup = nutConfig.getIoc().get(Setup.class, iocName);
                setup.destroy(nutConfig);
            }
        }
        // 非mysql数据库,或多webapp共享mysql驱动的话,以下语句删掉
        log.debug("destroy service start");
        try {
            Mirror.me(Class.forName("com.mysql.jdbc.AbandonedConnectionCleanupThread")).invoke(null, "shutdown");
        } catch (Throwable e) {
        }
        // 解决com.alibaba.druid.proxy.DruidDriver和com.mysql.jdbc.Driver在reload时报warning的问题
        // 多webapp共享mysql驱动的话,以下语句删掉
        Enumeration<Driver> en = DriverManager.getDrivers();
        while (en.hasMoreElements()) {
            try {
                Driver driver = en.nextElement();
                String className = driver.getClass().getName();
                log.debug("deregisterDriver: " + className);
                DriverManager.deregisterDriver(driver);
            } catch (Exception e) {
            }
        }
        try {
            log.debug("停止定时任务");
            //停止定时任务-停止方式为等待任务执行完毕后
            nutConfig.getIoc().get(Scheduler.class).shutdown(true);
        } catch (Throwable e) {
        }
        log.debug("destroy service end");
    }
}
