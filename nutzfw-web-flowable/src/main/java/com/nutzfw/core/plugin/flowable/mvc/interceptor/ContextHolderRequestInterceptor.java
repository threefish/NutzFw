package com.nutzfw.core.plugin.flowable.mvc.interceptor;

import com.nutzfw.core.mvc.interceptor.RequestInterceptor;
import com.nutzfw.core.plugin.flowable.context.ProcessContextHolder;
import org.nutz.ioc.loader.annotation.IocBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2021/10/19
 */
@IocBean
public class ContextHolderRequestInterceptor implements RequestInterceptor {
    @Override
    public void before(HttpServletRequest request, HttpServletResponse response) {
        ProcessContextHolder.remove();
    }

    @Override
    public void after(HttpServletRequest request, HttpServletResponse response) {
        ProcessContextHolder.remove();
    }
}
