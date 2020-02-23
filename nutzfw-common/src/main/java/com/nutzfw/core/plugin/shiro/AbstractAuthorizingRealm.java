/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.shiro;

import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.sys.biz.RoleBiz;
import com.nutzfw.modules.sys.entity.Menu;
import com.nutzfw.modules.sys.entity.Role;
import com.nutzfw.modules.sys.service.MenuService;
import com.nutzfw.modules.sys.service.RoleService;
import com.nutzfw.modules.sys.util.MenuUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/3/29
 */
public abstract class AbstractAuthorizingRealm extends AuthorizingRealm {

    protected static final Log log = Logs.get();

    protected UserAccountService userAccountService;
    protected MenuService menuService;
    protected RoleService roleService;
    protected RoleBiz roleBiz;

    public void initServices() {
        getUserAccountService();
        getMenuService();
        getRoleService();
        getRoleBiz();
    }

    public UserAccountService getUserAccountService() {
        if (userAccountService == null) {
            userAccountService = Mvcs.ctx().getDefaultIoc().get(UserAccountService.class, "userAccountService");
            return userAccountService;
        }
        return userAccountService;
    }

    public MenuService getMenuService() {
        if (menuService == null) {
            menuService = Mvcs.ctx().getDefaultIoc().get(MenuService.class, "menuService");
            return menuService;
        }
        return menuService;
    }

    public RoleService getRoleService() {
        if (roleService == null) {
            roleService = Mvcs.ctx().getDefaultIoc().get(RoleService.class, "roleService");
            return roleService;
        }
        return roleService;
    }

    public RoleBiz getRoleBiz() {
        if (roleBiz == null) {
            roleBiz = Mvcs.ctx().getDefaultIoc().get(RoleBiz.class, "roleBiz");
            return roleBiz;
        }
        return roleBiz;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession(false);
        SimpleAuthorizationInfo auth = (SimpleAuthorizationInfo) session.getAttribute(Cons.SHIRO_AUTHORIZATION_INFO);
        if (!subject.isAuthenticated() || auth == null) {
            UserAccount user = (UserAccount) session.getAttribute(Cons.SESSION_USER_KEY);
            List<Role> roleList = roleService.queryRoles(user.getId());
            if (user != null) {
                auth = new SimpleAuthorizationInfo();
                auth.addRole(Cons.SESSION_USER_ROLE);
                List<Menu> menus = new ArrayList<>();
                Set<String> roles = new HashSet<>();
                Set<String> permissions = new HashSet<>();
                Set<String> roleids = new HashSet<>();
                for (Role role : roleList) {
                    roles.add(role.getRoleCode());
                    roleids.add(role.getId());
                }
                List<Menu> menuList = menuService.querMenusByUserRoles(roleids);
                if (user.getUserName().equals(Cons.ADMIN)) {
                    roles.add(Cons.SESSION_USER_ROLE_CODE);
                    List<Menu> allMenus = menuService.query();
                    allMenus.stream().filter(menu -> Strings.isNotBlank(menu.getPermission())).forEach(menu -> permissions.add(menu.getPermission()));
                    for (Menu menu : allMenus) {
                        if (menu.getMenuType() == 0) {
                            menus.add(menu);
                        }
                    }
                } else {
                    for (Menu menu : menuList) {
                        if (Strings.isNotBlank(menu.getPermission())) {
                            permissions.add(menu.getPermission());
                        }
                        if (menu.getMenuType() == 0) {
                            menus.add(menu);
                        }
                    }
                }
                session.setAttribute(Cons.SESSION_ROLES_KEY, roleList);
                menus = MenuUtil.shortTree(MenuUtil.createTree(menus, "0"));
                session.setAttribute(Cons.SESSION_MENUS, Json.toJson(MenuUtil.createAdminLiteMenus(menus, "0"), JsonFormat.compact()));
                auth.addRoles(roles);
                auth.addStringPermissions(permissions);
                session.setAttribute(Cons.SHIRO_AUTHORIZATION_INFO, auth);
            }
        }
        return auth;
    }

    protected void onLoginSuccess(UserAccount userAccount) {
        try {
            Subject subject = SecurityUtils.getSubject();
            Session session = subject.getSession(false);
            session = (session == null ? subject.getSession(true) : session);
            //登录用户可能在未退出的情况下再次登录其他账号，导致权限错乱，所以需要移除权限相关session
            session.removeAttribute(Cons.SESSION_ROLES_KEY);
            session.removeAttribute(Cons.SHIRO_AUTHORIZATION_INFO);
            session.removeAttribute(Cons.SESSION_MENUS);
            session.removeAttribute(Cons.SESSION_USER_ROLE);
            session.removeAttribute(Cons.SESSION_USER_ROLE_CODE);
            //设置登录成功的用户信息
            session.setAttribute(Cons.SESSION_USER_KEY, userAccount);
            Set<String> userNames = roleBiz.queryManagerUserNames(userAccount.getId());
            if (log.isDebugEnabled()) {
                log.debugf("%s 可以管理的人员有: %s", userNames);
                log.debug("如果你发现设置了管理部门但是还是没有可以管理的人员，那么你应该设置岗位，并将岗位赋予人员。");
            }
            session.setAttribute(Cons.SESSION_MANAGER_USER_NAMES_KEY, userNames);
        } catch (Exception e) {
            log.error("查询用户管理信息失败！", e);
            throw new AccountException("系统错误！");
        }
    }
}
