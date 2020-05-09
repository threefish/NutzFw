/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.filter;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.view.HttpStatusView;
import org.nutz.mvc.view.ServerRedirectView;
import org.nutz.mvc.view.UTF8JsonView;

import javax.servlet.http.HttpSession;

/**
 * 检查当前 Session如果存在某一属性，并且不为 null，则通过 <br>
 * 否则，返回一个 ServerRecirectView 到对应 path
 * <p>
 * 构造函数需要两个参数
 * <ul>
 * <li>第一个是， 需要检查的属性名称。如果 session 里存在这个属性，则表示通过检查
 * <li>第二个是， 需要检查的角色列表。多个使用逗号隔开
 * <li>第三个是， 检查未登录则跳转页面，默认为 NutShiro.DefaultLoginURL
 * </ul>
 *
 * @author 黄川
 */

public class CheckRoleAndSession implements ActionFilter {

    /**
     * session名称
     */
    private String sessionKey;

    /**
     * 角色名称
     */
    private String roles;

    /**
     * 跳转登录页
     */
    private String loginPageUrl = NutShiro.DefaultLoginURL;


    public CheckRoleAndSession(String sessionKey, String roles) {
        this.sessionKey = sessionKey;
        this.roles = roles;
    }

    public CheckRoleAndSession(String sessionKey, String roles, String loginPageUrl) {
        this.sessionKey = sessionKey;
        this.roles = roles;
        this.loginPageUrl = loginPageUrl;
    }

    @Override
    public View match(ActionContext context) {
        HttpSession session = context.getRequest().getSession(false);
        boolean isNotLogin = session == null || null == session.getAttribute(sessionKey);
        boolean isAjax = NutShiro.isAjax(context.getRequest());
        boolean isNutzFwFront = Strings.isNotBlank(context.getRequest().getHeader("NutzFwFront"));
        String requesturi = context.getRequest().getRequestURI();
        if (isNotLogin) {
            Logs.get().debugf("session key [%s] is not found :[%s]", sessionKey, context.getPath());
            if (isAjax) {
                return buildUtf8JsonView(NutShiro.DefaultOtherAjax.setv("loginUrl", loginPageUrl));
            } else if (isNutzFwFront) {
                //需要重新登录
                return new HttpStatusView(401);
            } else if (!loginPageUrl.equals(requesturi)) {
                return new ServerRedirectView(loginPageUrl);
            }
        } else {
            boolean hasRole = matchRole();
            if (!hasRole) {
                Logs.get().debugf("无权访问:[%s]", context.getPath());
                if (isAjax) {
                    return buildUtf8JsonView(NutShiro.DefaultUnauthenticatedAjax);
                } else if (isNutzFwFront) {
                    return new HttpStatusView(403);
                } else {
                    return new ServerRedirectView(NutShiro.DefaultNoAuthURL);
                }
            }
        }
        return null;
    }

    public UTF8JsonView buildUtf8JsonView(Object data) {
        UTF8JsonView view = new UTF8JsonView(JsonFormat.compact());
        view.setData(data);
        return view;
    }

    public boolean matchRole() {
        Subject subject = SecurityUtils.getSubject();
        String[] rs = roles.split(",");
        boolean hasRole = false;
        sw:
        for (String s : rs) {
            if (subject.hasRole(s)) {
                //有权限
                hasRole = true;
                //跳出循环
                break sw;
            }
        }
        return hasRole;
    }

}
