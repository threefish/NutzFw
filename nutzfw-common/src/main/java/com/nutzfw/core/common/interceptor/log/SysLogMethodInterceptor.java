package com.nutzfw.core.common.interceptor.log;

import com.nutzfw.core.common.annotation.SysLog;
import com.nutzfw.core.common.util.DateUtil;
import com.nutzfw.modules.monitor.service.SysOperateLogService;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.el.El;
import org.nutz.ioc.Ioc;
import org.nutz.lang.segment.CharSegment;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/1/29
 * 描述此类：
 */
public class SysLogMethodInterceptor implements MethodInterceptor {
    protected SysOperateLogService sysOperateLogService;
    protected Ioc ioc;
    protected String source;
    protected String tag;
    protected CharSegment seg;
    protected Map<String, El> els;
    protected boolean param;
    protected boolean result;

    public SysLogMethodInterceptor(SysLog sysLog, Method method, Ioc ioc) {
        this.seg = new CharSegment(sysLog.template());
        if (seg.hasKey()) {
            els = new HashMap();
            for (String key : seg.keys()) {
                els.put(key, new El(key));
            }
        }
        this.ioc = ioc;
        this.source = method.getDeclaringClass().getName() + "#" + method.getName();
        this.tag = sysLog.tag();
        this.param = sysLog.param();
        this.result = sysLog.result();
        SysLog _s = method.getDeclaringClass().getAnnotation(SysLog.class);
        if (_s != null) {
            this.tag = _s.tag() + "," + this.tag;
        }
    }

    @Override
    public void filter(InterceptorChain chain) throws Throwable {
        if (sysOperateLogService == null) {
            sysOperateLogService = ioc.get(SysOperateLogService.class, "sysOperateLogService");
        }
        long startTime = System.currentTimeMillis();
        try {
            chain.doChain();
            sysOperateLogService.log("aop.after", tag, source, seg, els, param, result, getConsuming(startTime), chain.getArgs(), chain.getReturn(), chain.getCallingMethod(), chain.getCallingObj(), null);
        } catch (Throwable e) {
            sysOperateLogService.log("aop.after", tag, source, seg, els, param, result, getConsuming(startTime), chain.getArgs(), chain.getReturn(), chain.getCallingMethod(), chain.getCallingObj(), e);
            throw e;
        }
    }

    private String getConsuming(long startTime) {
        long endTime = System.currentTimeMillis();
        return DateUtil.getDistanceTime(startTime, endTime, "{M}分{S}秒{MS}毫秒");
    }
}