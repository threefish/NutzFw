/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.monitor.service.impl;

import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.core.common.util.WebUtil;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.monitor.entity.SysOperateLog;
import com.nutzfw.modules.monitor.service.SysOperateLogService;
import com.nutzfw.modules.organize.entity.UserAccount;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.util.Daos;
import org.nutz.el.El;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.MethodParamNamesScaner;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年06月12日 15时55分14秒
 */
@IocBean(name = "sysOperateLogService", args = {"refer:dao"}, create = "init", depose = "depose")
public class SysOperateLogServiceImpl extends BaseServiceImpl<SysOperateLog> implements SysOperateLogService {

    protected static final Log                                log    = Logs.get();
    /**
     * 按月分表的dao实例
     */
    protected              Map<String, Dao>                   ymDaos = new HashMap<>();
    /**
     * 线程池
     */
    private                ScheduledThreadPoolExecutor        executorService;
    /**
     * 消息队列
     */
    private                LinkedBlockingQueue<SysOperateLog> queue  = new LinkedBlockingQueue();

    public SysOperateLogServiceImpl(Dao dao) {
        super(dao);
    }

    public static SysOperateLog makeLog(String type, String tag, String source, String msg, String consuming, String param, String result) {
        SysOperateLog sysLog = new SysOperateLog();
        if (type == null || tag == null) {
            throw new RuntimeException("type/tag can't null");
        }
        if (source == null) {
            StackTraceElement[] tmp = Thread.currentThread().getStackTrace();
            if (tmp.length > 2) {
                source = tmp[2].getClassName() + "#" + tmp[2].getMethodName();
            } else {
                source = "main";
            }
        }
        String queryString = Mvcs.getReq().getQueryString();
        if (Strings.isEmpty(queryString)) {
            sysLog.setPath(Mvcs.getActionContext().getPath());
        } else {
            sysLog.setPath(Mvcs.getActionContext().getPath() + "?" + queryString);
        }
        sysLog.setType(type);
        sysLog.setTag(tag);
        sysLog.setSource(source);
        sysLog.setMsg(msg);
        sysLog.setParam(param);
        sysLog.setResult(result);
        sysLog.setConsuming(consuming);
        if (Mvcs.getReq() != null) {
            sysLog.setMethod(Mvcs.getReq().getMethod());
            sysLog.setIp(WebUtil.ip(Mvcs.getReq()));
            UserAccount userAccount = (UserAccount) Mvcs.getReq().getSession().getAttribute(Cons.SESSION_USER_KEY);
            if (userAccount != null) {
                sysLog.setUserName(userAccount.getUserName());
                sysLog.setOpByDesc(userAccount.getRealName());
                sysLog.setOpBy(userAccount.getUserid());
                sysLog.setDeptId(userAccount.getDeptId());
                if (userAccount.getDept() != null) {
                    sysLog.setDeptId(userAccount.getDept().getName());
                    sysLog.setDeptDesc(userAccount.getDept().getName());
                }
            }
            try {
                UserAgent userAgent = UserAgent.parseUserAgentString(Mvcs.getReq().getHeader("User-Agent"));
                sysLog.setOs(userAgent.getOperatingSystem().getName());
                sysLog.setBrowser(userAgent.getBrowser().getName());
            } catch (Exception e) {
                log.error("UserAgent:", e);
            }
        }
        return sysLog;
    }

    public void init() {
        ThreadFactory factory = new BasicThreadFactory.Builder().namingPattern("operateLog-schedule-pool-%d").daemon(true).build();
        executorService = new ScheduledThreadPoolExecutor(1, factory);
        /**
         * 每隔5秒 将缓存在队列中的日志进行批量入库
         */
        executorService.scheduleAtFixedRate(() -> {
            try {
                int length = this.queue.size();
                if (length > 0) {
                    List<SysOperateLog> list = new ArrayList<>();
                    for (int i = 0; i < length; i++) {
                        list.add(this.queue.poll(1, TimeUnit.SECONDS));
                    }
                    sync(list);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public void depose() throws InterruptedException {
        this.queue.clear();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    /**
     * 异步插入日志
     *
     * @param operateLog 日志对象
     */
    @Override
    public void async(SysOperateLog operateLog) {
        try {
            queue.offer(operateLog, 10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error(e);
        }
    }

    /**
     * 获取按月分表的Dao实例,即当前日期的dao实例
     *
     * @return
     */
    public Dao ymDao() {
        Calendar cal = Calendar.getInstance();
        String key = String.format("%d%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
        return dao(key);
    }

    /**
     * 同步插入日志
     *
     * @param operateLog 日志对象
     */
    @Override
    public void sync(SysOperateLog operateLog) {
        try {
            ymDao().fastInsert(operateLog);
        } catch (Throwable e) {
            log.error("insert syslog sync fail", e);
        }
    }

    /**
     * 同步插入日志
     *
     * @param operateLogList 日志对象
     */
    @Override
    public void sync(List<SysOperateLog> operateLogList) {
        try {
            ymDao().fastInsert(operateLogList);
        } catch (Throwable e) {
            log.error("insert syslog sync fail", e);
        }
    }

    /**
     * 获取特定月份的Dao实例
     *
     * @param key
     * @return
     */
    public Dao dao(String key) {
        Dao dao = ymDaos.get(key);
        if (dao == null) {
            synchronized (this) {
                synchronized (this) {
                    dao = ymDaos.get(key);
                    if (dao == null) {
                        dao = Daos.ext(this.dao, key);
                        dao.create(SysOperateLog.class, false);
                        ymDaos.put(key, dao);
                        try {
                            Daos.migration(dao, SysOperateLog.class, true, false);
                        } catch (Throwable e) {
                        }
                    }
                }
            }
        }
        if (ymDaos.size() > 1) {
            //移除过期key
            for (Map.Entry<String, Dao> daoEntry : ymDaos.entrySet()) {
                if (!key.equals(daoEntry.getKey())) {
                    ymDaos.remove(daoEntry.getKey());
                }
            }
        }
        return dao;
    }

    /**
     * 本方法通常由aop拦截器调用.
     *
     * @param type   类型
     * @param tag    标签
     * @param source 源码位置
     * @param els    消息模板的EL表达式预处理表
     * @param param  记录参数
     * @param result 记录返回值
     * @param args   方法参数
     * @param re     方法返回值
     * @param method 方法实例
     * @param obj    被拦截的对象
     * @param e      异常对象
     */
    @Override
    public void log(String type, String tag, String source, CharSegment seg,
                    Map<String, El> els, boolean param, boolean result, String consuming,
                    Object[] args, Object re, Method method, Object obj,
                    Throwable e) {
        try {

            String msg;
            if (seg.hasKey()) {
                Context ctx = Lang.context();
                List<String> names = MethodParamNamesScaner.getParamNames(method);
                if (names != null) {
                    for (int i = 0; i < names.size() && i < args.length; i++) {
                        ctx.set(names.get(i), args[i]);
                    }
                }
                ctx.set("obj", obj);
                ctx.set("args", args);
                ctx.set("re", re);
                ctx.set("return", re);
                ctx.set("req", Mvcs.getReq());
                ctx.set("resp", Mvcs.getResp());
                Context context = Lang.context();
                for (String key : seg.keys()) {
                    context.set(key, els.get(key).eval(ctx));
                }
                msg = seg.render(context).toString();
            } else {
                msg = seg.getOrginalString();
            }
            String param_ = "";
            String result_ = "";
            if (param && args != null) {
                try {
                    param_ = Json.toJson(args, JsonFormat.compact());
                } catch (Exception e1) {
                    param_ = "传参不能转换为JSON格式";
                }
            }
            if (result && re != null) {
                try {
                    result_ = Json.toJson(re, JsonFormat.compact());
                } catch (Exception e1) {
                    param_ = "返回对象不能转换为JSON格式";
                }
            }
            log(type, tag, source, consuming, msg, param_, result_);
        } catch (Throwable te) {
            log.errorf("@SysLog 日志记录出现错误 %s", source, te);
        }
    }

    @Override
    public LayuiTableDataListVO listPage(int pageNum, int pageSize, Cnd cnd) {
        pageNum = getPageNumber(pageNum);
        pageSize = getPageSize(pageSize);
        Pager pager = ymDao().createPager(pageNum, pageSize);
        List<SysOperateLog> list = ymDao().query(this.getEntityClass(), cnd, pager);
        pager.setRecordCount(ymDao().count(this.getEntityClass(), cnd));
        return new LayuiTableDataListVO(pageNum, pageSize, pager.getRecordCount(), list);
    }

    private void log(String type, String tag, String source, String consuming, String msg, String param, String result) {
        async(makeLog(type, tag, source, msg, consuming, param, result));
    }
}
