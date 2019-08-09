package com.nutzfw.core.plugin.shiro;

import com.nutzfw.core.common.vo.AjaxResult;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.lang.Strings;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 直接穿透
 *
 * @author wendal
 */
public class NutzFwAuthenticationFilter extends AuthenticationFilter {

    @Override
    protected boolean isLoginRequest(ServletRequest request, ServletResponse response) {
        return false;
    }

    protected boolean isLoginSubmission(ServletRequest request, ServletResponse response) {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        if (NutShiro.isAjax(request)) {
            if (subject.getPrincipal() == null) {
                NutShiro.rendAjaxResp(request, response, AjaxResult.error("您尚未登录或登录时间过长,请重新登录!"));
            } else {
                NutShiro.rendAjaxResp(request, response, AjaxResult.error("您没有足够的权限执行该操作"));
            }
        } else {
            if (subject.getPrincipal() == null) {
                saveRequestAndRedirectToLogin(request, response);
            } else {
                String unauthorizedUrl = NutShiro.DefaultNoAuthURL;
                if (Strings.isNotBlank(unauthorizedUrl)) {
                    WebUtils.issueRedirect(request, response, unauthorizedUrl);
                } else {
                    WebUtils.toHttp(response).sendError(401);
                }
            }
        }
        return false;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (pathsMatch(getLoginUrl(), request)) {
            return true;
        }
        return super.isAccessAllowed(request, response, mappedValue);
    }

    @Override
    public void setLoginUrl(String loginUrl) {
        super.setLoginUrl(loginUrl);
        NutShiro.DefaultLoginURL = loginUrl;
    }

    public void setUnauthorizedUrl(String unauthorizedUrl) {
        NutShiro.DefaultNoAuthURL = unauthorizedUrl;
    }
}
