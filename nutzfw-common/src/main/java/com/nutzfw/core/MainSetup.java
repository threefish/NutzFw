package com.nutzfw.core;

import com.github.threefish.nutz.sqltpl.BeetlSqlTemplteEngineImpl;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.entity.BaseEntity;
import com.nutzfw.core.mvc.PreventDuplicateSubmitProcessor;
import com.nutzfw.core.plugin.beetl.GenMySqlFieldTypeFn;
import com.nutzfw.core.plugin.init.AbstractInitSetup;
import com.nutzfw.core.plugin.init.InitSetup;
import com.nutzfw.core.plugin.quartz.NutQuartzCronJobFactory;
import com.nutzfw.core.plugin.redis.RedisHelpper;
import com.nutzfw.core.plugin.view.BeetlViewMaker;
import com.nutzfw.modules.common.action.FileAction;
import com.nutzfw.modules.monitor.entity.AlarmOption;
import com.nutzfw.modules.monitor.entity.SysOperateLog;
import com.nutzfw.modules.monitor.quartz.job.ApmJob;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.sys.biz.DictBiz;
import com.nutzfw.modules.sys.entity.*;
import com.nutzfw.modules.sys.quartz.job.AutoSendMailJob;
import com.nutzfw.modules.sys.quartz.job.DataImportJob;
import com.nutzfw.modules.sys.quartz.job.DatabaseBackupJob;
import com.nutzfw.modules.sys.quartz.job.UserImportJob;
import com.nutzfw.modules.tabledata.entity.DataTableVersionHistory;
import com.nutzfw.modules.tabledata.enums.FieldType;
import com.nutzfw.modules.tabledata.enums.TableType;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.nutz.Nutz;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.integration.jedis.RedisService;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Encoding;
import org.nutz.lang.Mirror;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.resource.Scans;
import org.quartz.Scheduler;

import java.nio.charset.Charset;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2015/4/2114:20
 */
public class MainSetup extends AbstractInitSetup implements Setup {

    static final String PACKAGE = "com.nutzfw";
    /**
     * 子模块初始化启动器
     */
    List<InitSetup> initSetups = new ArrayList<>();
    private Log log = Logs.get();

    @Override
    public void init(NutConfig nutConfig) {
        log.debug("当前Nutz版本：" + Nutz.version());
        if (!Charset.defaultCharset().name().equalsIgnoreCase(Encoding.UTF8)) {
            log.error("这个项目必须运行在UTF-8环境下, 请添加 -Dfile.encoding=UTF-8 至 JAVA_OPTS");
            throw new RuntimeException("This project must be run in UTF-8 environment, please add -Dfile.encoding=UTF-8 to JAVA_OPTS");
        }
        Ioc ioc = nutConfig.getIoc();
        DictBiz dictBiz = ioc.get(DictBiz.class);
        Dao dao = ioc.get(Dao.class, "dao");
        PropertiesProxy conf = ioc.get(PropertiesProxy.class, "conf");
        PropertiesProxy enumsConf = ioc.get(PropertiesProxy.class, "enumsConf");
        boolean initSystem = conf.getBoolean("initSystem", false);
        //取得执行子模块的Setup
        this.initSubModuleSetup(nutConfig);
        //通过配置文件设置常量
        this.setUpConstant(conf);
        //清除NutzFw所有redis缓存
        this.cleanRedis(ioc.get(RedisService.class));
        //取得子模块中的待初始化数据
        this.addAttachType(attachType);
        this.addQuartzJob(quartzJobs);
        this.addTablesFilters(tablesFilters);
        this.addDictGroup(dictGroup);
        initSetups.forEach(setup -> {
            setup.addAttachType(attachType);
            setup.addDictGroup(dictGroup);
            setup.addQuartzJob(quartzJobs);
            setup.addTablesFilters(tablesFilters);
        });
        //初始化系统 数据表
        this.initSystemTableAndData(initSystem, dao, enumsConf);
        //缓存系统信息
        this.cacheSystemInformation(dao);
        //创建附件类型
        this.checkAttachTypeDict(dictBiz);
        //创建附件分组
        this.checkDictGroup(dictBiz);
        //初始化定时任务
        this.initQuartzJobs(ioc, dao, conf);
        //设置表单防止重复提交
        this.setUpPreventDuplicateSubmit(ioc);
        initSetups.forEach(setup -> setup.init(nutConfig));
        //nutz-sqltpl 注册下自定义方法
        this.setUpBeetlSqlTemplateFunction(ioc);
        //循环获取一次所有IocBean 避免单个服务创建失败，导致服务不可用，错误不好定位
        this.fetchAllIocBean(ioc);
        BeetlViewMaker.updateBeetlGroupTemplate(ioc);
    }

    /**
     * 服务停止前执行
     *
     * @param nutConfig
     */
    @Override
    public void destroy(NutConfig nutConfig) {
        log.debug("destroy service start");
        initSetups.forEach(setup -> setup.destroy(nutConfig));
        // 非mysql数据库,或多webapp共享mysql驱动的话,以下语句删掉
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


    @Override
    public void addAttachType(HashMap<String, String> attachType) {
        attachType.put("临时文件", "temp");
        attachType.put("头像", "avatar");
        attachType.put("动态数据表", "datatable");
        attachType.put("动态数据表数据导入", "dataimport");
        attachType.put("新闻", "news");
        attachType.put("人员异动", "userchange");
    }

    @Override
    public void addDictGroup(HashMap<String, String> dictGroup) {
    }

    @Override
    public void addTablesFilters(Set<Class<? extends BaseEntity>> tablesFilters) {
        tablesFilters.add(SysOperateLog.class);
    }

    @Override
    public void addQuartzJob(List<QuartzJob> quartzJobs) {
        quartzJobs.add(buildeQuartJob(ApmJob.class, "*/15 * * * * ?", "服务器状态监控服务", "服务器状态监控服务", false, null));
        quartzJobs.add(buildeQuartJob(AutoSendMailJob.class, "*/15 * * * * ?", "自动发送邮件定时任务", "自动发送邮件定时任务", false, null));
        quartzJobs.add(buildeQuartJob(DataImportJob.class, "*/15 * * * * ?", "数据导入任务", "自动检查是否有数据导入，有就自动执行导入", true, null));
        quartzJobs.add(buildeQuartJob(UserImportJob.class, "*/15 * * * * ?", "用户导入任务", "自动检查是否有用户导入，有就自动执行导入", true, null));
        quartzJobs.add(buildeQuartJob(DatabaseBackupJob.class, "0 0 0 3 * ? ", "数据库定时备份任务", "数据库定时备份任务", true, null));
    }

    /**
     * 创建附件分组
     *
     * @param dictBiz
     */
    private void checkDictGroup(DictBiz dictBiz) {
        dictGroup.forEach((key, val) -> this.ifNotExistCreateDictGroup(dictBiz, key, val));
    }

    /**
     * 初始化系统 数据表
     *
     * @param initSystem
     * @param dao
     * @param enumsConf
     */
    private void initSystemTableAndData(boolean initSystem, Dao dao, PropertiesProxy enumsConf) {
        if (initSystem) {
            initTable(dao, true);
            initSystemInfo(dao, enumsConf);
        } else {
            initTable(dao, false);
        }
    }


    /**
     * 自动创建表
     *
     * @param dao
     */
    private void initTable(Dao dao, boolean force) {
        //自动创建表
        Daos.createTablesInPackage(dao, PACKAGE, force, (klass, table) -> !tablesFilters.contains(klass));
        //统一据库表结构-不删除已有字段避免已经创建的动态字段被删
        for (Class<?> klass : Scans.me().scanPackage(PACKAGE)) {
            if (klass.getAnnotation(Table.class) != null && !tablesFilters.contains(klass)) {
                Daos.migration(dao, klass, true, false, false, null);
            }
        }
    }

    /**
     * 通过配置文件设置常量
     *
     * @param conf
     */
    private void setUpConstant(PropertiesProxy conf) {
        NutShiro.DefaultUnauthorizedAjax = new NutMap().setv("ok", false).setv("msg", "您需要授权才能进行操作！").setv("type", "user.require.auth");
        NutShiro.DefaultOtherAjax = new NutMap().setv("ok", false).setv("msg", "您需要登录！").setv("type", "user.require.login");
        NutShiro.DefaultUnauthenticatedAjax = new NutMap().setv("ok", false).setv("msg", "您的权限不足！").setv("type", "user.require.unauthorized");
        BeetlViewMaker.isDev = conf.getBoolean("isDev");
        BeetlViewMaker.productVersion = conf.get("productVersion");
    }

    /**
     * 清除Redis缓存
     */
    private void cleanRedis(RedisService redisService) {
        //清除字典缓存
        Set<String> lists = redisService.keys(RedisHelpper.buildRediskey("*"));
        if (lists.size() > 0) {
            redisService.del(lists.toArray(new String[]{}));
        }
    }

    /**
     * 取得子模块
     */
    private void initSubModuleSetup(NutConfig nutConfig) {
        Ioc ioc = nutConfig.getIoc();
        String[] setups = ioc.getNamesByType(InitSetup.class);
        if (null != setups) {
            for (String iocName : setups) {
                initSetups.add(ioc.get(InitSetup.class, iocName));
            }
        }
    }


    /**
     * 缓存系统信息
     *
     * @param dao
     */
    private void cacheSystemInformation(Dao dao) {
        Cons.optionsCach = dao.fetch(Options.class, Cnd.where("id", "=", "0"));
        if (Cons.optionsCach == null) {
            throw new RuntimeException("缓存系统信息失败！");
        }
    }

    /**
     * 创建附件类型
     *
     * @param dictBiz
     */
    private void checkAttachTypeDict(DictBiz dictBiz) {
        Dict parentDict = ifNotExistCreateDictGroup(dictBiz, FileAction.SYS_ATTACH_TYPE, "附件类型");
        attachType.forEach((name, value) -> {
            if (dictBiz.getDict(parentDict.getSysCode(), value) == null) {
                dictBiz.addDict(Dict.builder().sysCode(parentDict.getSysCode()).pid(parentDict.getId())
                        .lable(name).val(value).showType(0).grouping(false)
                        .internal(true).shortNo(0).mark("").defaultVal(false)
                        .edit(false)
                        .build());
            }
        });
    }

    /**
     * 如果不存在则返回新建的字典分组
     *
     * @param dictBiz
     * @param sysCode
     * @param lable
     * @return
     */
    private Dict ifNotExistCreateDictGroup(DictBiz dictBiz, String sysCode, String lable) {
        Dict dict = dictBiz.getDict(sysCode);
        if (dict == null) {
            dict = dictBiz.addDict(Dict.builder().lable(lable).grouping(true).internal(true).sysCode(sysCode).val("").edit(false).build());
        }
        return dict;
    }

    /**
     * 添加定时任务-已经存在的任务会忽略导入
     *
     * @param jobList
     * @param dao
     * @return
     */
    private void addQuartJob(List<QuartzJob> jobList, Dao dao) {
        List<QuartzJob> jobs = new ArrayList<>();
        Sql sql = Sqls.create("select job_klass from ".concat(QuartzJob.class.getAnnotation(Table.class).value()));
        sql.setCallback(Sqls.callback.strList());
        dao.execute(sql);
        List<String> list = sql.getList(String.class);
        jobList.stream().filter(quartzJob -> !list.contains(quartzJob.getJobKlass())).forEach(quartzJob -> jobs.add(quartzJob));
        dao.insert(jobs);
    }

    /**
     * 初始化定时任务
     *
     * @param ioc
     */
    private void initQuartzJobs(Ioc ioc, Dao dao, PropertiesProxy conf) {
        final String initQuartzJob = "initQuartzJob";
        addQuartJob(quartzJobs, dao);
        if (conf.getBoolean(initQuartzJob, false)) {
            ioc.get(NutQuartzCronJobFactory.class);
        }
    }

    /**
     * 设置表单防止重复提交
     *
     * @param ioc
     */
    private void setUpPreventDuplicateSubmit(Ioc ioc) {
        try {
            PreventDuplicateSubmitProcessor.redisService = ioc.getByType(RedisService.class);
        } catch (Exception e) {
            log.debug("表单防止重复提交：未启用redis,降级采用session");
        }
    }

    /**
     * nutz-sqltpl 注册下自定义方法
     *
     * @param ioc
     */
    private void setUpBeetlSqlTemplateFunction(Ioc ioc) {
        BeetlSqlTemplteEngineImpl beetlSqlTemplteEngine = ioc.get(BeetlSqlTemplteEngineImpl.class);
        beetlSqlTemplteEngine.getGt().registerFunction("genMysqlFieldType", new GenMySqlFieldTypeFn());
    }


    /**
     * 循环获取一次所有IocBean 避免单个服务创建失败，导致服务不可用，错误不好定位
     *
     * @param ioc
     */
    private void fetchAllIocBean(Ioc ioc) {
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


    /**
     * 创建系统基础数据
     *
     * @param dao
     */
    private void initSystemInfo(Dao dao, PropertiesProxy enumsConf) {
        //创建管理员
        String salt = R.UU16();
        Sha256Hash sha = new Sha256Hash(Cons.DEFAULT_PASSWORD, salt);
        UserAccount userAccount = UserAccount.builder()
                .locked(false).salt(salt).userName(Cons.ADMIN).userPass(sha.toHex())
                .createByDate(new Date(System.currentTimeMillis()))
                .createByName("自动创建").realName("超级管理员").review(1).reviewOpinion("无").build();
        dao.insert(userAccount);
        //创建超级管理员角色
        Role role = Role.builder().locked(false).roleName("超级管理员").roleCode("superadmin").shortNo(0).build();
        dao.insert(role);
        /**
         * 初始化系统信息
         */
        Options options = Options.builder()
                .errorPassInputTimes(5).fristLoginNeedChangePass(false)
                .unitName("某某集团")
                .version("3.2.1").productLongName("NutzFw快速开发框架")
                .productSortName("NutzFw").productLogo("")
                .productEnLongName("NutzFw SOFT PLATFORM")
                .passExpired(3).passStrength(0).theme(2)
                .needVerificationCode(1)
                .registrationTime(new Timestamp(System.currentTimeMillis()))
                .build();
        dao.insert(options);
        /**
         * 初始化监控信息
         */
        List<AlarmOption> alarmOptionList = new ArrayList<>();
        List<String> listenerTypes = Arrays.asList("CPU", "DISK", "JVM", "RAM", "SWAP");
        for (String listenerType : listenerTypes) {
            AlarmOption alarmOption = new AlarmOption();
            alarmOption.setAlarmType(listenerType);
            alarmOption.setSms(true);
            alarmOption.setEmail(true);
            alarmOption.setPercent(90.00);
            alarmOption.setListeners(userAccount.getId());
            alarmOption.setListenersDesc(userAccount.getUserName());
            alarmOptionList.add(alarmOption);
        }
        dao.insert(alarmOptionList);
        /**
         * 初始化系统字典
         */
        createInternalDictDetails(dao, enumsConf);
        /**
         * 初始化系统表信息
         */
        createSystemDataTable(dao);
    }

    /**
     * 通过配置文件创建字典键值
     *
     * @param parentDict
     * @param val
     * @return
     */
    private List<Dict> createInternalDictDetail(Dict parentDict, String val) {
        List<Dict> list = new ArrayList<>();
        String[] ss = val.split(",");
        for (int i = 0; i < ss.length; i++) {
            String s = ss[i];
            String[] temp = s.split("=");
            Dict detail = Dict.builder()
                    .pid(parentDict.getId()).grouping(false).internal(true).defaultVal(false)
                    .sysCode(parentDict.getSysCode()).lable(temp[0].trim()).val(temp[1].trim()).shortNo(i).mark("")
                    .build();
            list.add(detail);
        }
        return list;
    }

    /**
     * 通过配置文件创建字典键值
     *
     * @param dao
     * @param enumsConf
     */
    private void createInternalDictDetails(Dao dao, PropertiesProxy enumsConf) {
        List<Dict> dictDetails = new ArrayList<>();
        enumsConf.forEach((key, val) -> {
            key = key.trim();
            int start = val.indexOf(DELIMITER);
            String name = val.substring(0, start).trim();
            val = val.substring(start + 2).trim();
            Dict parentDict = Dict.builder().sysCode(key).pid(0).lable(name).val("").showType(0).grouping(true)
                    .internal(true).shortNo(0).mark("").defaultVal(false)
                    .build();
            dao.insert(parentDict);
            dictDetails.addAll(createInternalDictDetail(parentDict, val));
        });
        dao.insert(dictDetails);
    }

    /**
     * 创建系统表
     *
     * @param dao
     */
    private void createSystemDataTable(Dao dao) {
        DataTable dataTable = DataTable.builder()
                .name("个人基本信息表").tableType(TableType.PrimaryTable).comment("个人基本信息表").status(1).system(true)
                .formTemplate(1).tableName(Cons.USER_ACCOUNT_TABLE_NAME).build();
        if (dao.count(DataTable.class.getAnnotation(Table.class).value()) == 0) {
            dao.insert(dataTable);
            List<TableFields> fields = new ArrayList<>();
            fields.add(TableFields.builder()
                    .tableId(dataTable.getId()).tableName(dataTable.getTableName()).system(true).controlType(0)
                    .fieldName("username").comment("用户名").fromLable("用户名").name("用户名").length(50)
                    .nullValue(false).defaultValue("NULL").fieldType(FieldType.String.getValue()).fromDisplay(true).validationRulesType(-1)
                    .build());
            fields.add(TableFields.builder()
                    .tableId(dataTable.getId()).tableName(dataTable.getTableName()).system(true).controlType(0)
                    .fieldName("realname").comment("真实姓名").fromLable("真实姓名").name("真实姓名").length(50)
                    .nullValue(false).defaultValue("NULL").fieldType(FieldType.String.getValue()).fromDisplay(true).validationRulesType(-1)
                    .build());
            fields.add(TableFields.builder()
                    .tableId(dataTable.getId()).tableName(dataTable.getTableName()).system(true).controlType(0)
                    .fieldName("phone").comment("手机号").fromLable("手机号").name("手机号").length(20)
                    .nullValue(false).defaultValue("NULL").fieldType(FieldType.String.getValue()).fromDisplay(true).validationRulesType(4)
                    .build());
            fields.add(TableFields.builder()
                    .tableId(dataTable.getId()).tableName(dataTable.getTableName()).system(true).controlType(0)
                    .fieldName("mail").comment("电子邮箱").fromLable("电子邮箱").name("电子邮箱").length(20)
                    .nullValue(false).defaultValue("NULL").fieldType(FieldType.String.getValue()).fromDisplay(true).validationRulesType(5)
                    .build());
            fields.add(TableFields.builder()
                    .tableId(dataTable.getId()).tableName(dataTable.getTableName()).system(true).controlType(0)
                    .fieldName("gender").comment("性别").fromLable("性别").name("性别").length(10)
                    .nullValue(false).defaultValue("NULL").fieldType(FieldType.String.getValue()).dictSysCode("sys_user_sex")
                    .dictSysCodeDesc("性别").fromDisplay(true).validationRulesType(-1)
                    .build());
            dao.insert(fields);
            //建立版本历史信息
            dataTable.setFields(fields);
            dao.insert(new DataTableVersionHistory(dataTable.getId(), dataTable.getVersion(), Json.toJson(dataTable, JsonFormat.compact())));
        }
    }


}
