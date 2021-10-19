package com.nutzfw.core.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2021/10/19
 */
public interface RequestInterceptor {
    /**
     * 请求执行前执行
     *
     * @param request
     * @param response
     */
    void before(HttpServletRequest request, HttpServletResponse response);

    /**
     * 请求执行完成后执行
     *
     * @param request
     * @param response
     */
    void after(HttpServletRequest request, HttpServletResponse response);

    /**
     * 拦截器顺序
     *
     * @return
     */
    default int getOrder() {
        return Integer.MIN_VALUE;
    }

}
