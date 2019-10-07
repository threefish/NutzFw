/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.common.action;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.sys.entity.Role;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;

import java.util.List;
import java.util.Set;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 创建时间: 2017/12/25  19:32
 * 此类禁止添加任何代码
 */
public abstract class BaseAction {


    protected static final Log    log  = Logs.get();
    /**
     * 项目相对根路径
     */
    protected static final String BASE = Mvcs.getServletContext().getContextPath();

    /**
     * 给request设置临时值
     *
     * @param requestAttr
     * @param val
     */
    protected final void setRequestAttribute(String requestAttr, Object val) {
        Mvcs.getReq().setAttribute(requestAttr, val);
    }

    /**
     * 获取request值-注意值可能为NUll
     *
     * @param requestAttr
     */
    protected final <T> T getRequestAttribute(String requestAttr) {
        T t = (T) Mvcs.getReq().getAttribute(requestAttr);
        if (t == null) {
            log.warn(String.format("%s 在session中不存在", requestAttr));
        }
        return t;
    }

    /**
     * 给session设置值
     *
     * @param sessionAttr
     * @param val
     */
    protected final void setSessionAttribute(String sessionAttr, Object val) {
        Mvcs.getReq().getSession().setAttribute(sessionAttr, val);
    }

    /**
     * 获取session值-注意值可能为NUll
     *
     * @param sessionAttr
     */
    protected final <T> T getSessionAttribute(String sessionAttr) {
        T t = (T) Mvcs.getReq().getSession().getAttribute(sessionAttr);
        if (t == null) {
            log.warn(String.format("%s 在session中不存在", sessionAttr));
        }
        return t;
    }

    /**
     * 取得当前登录人员信息
     *
     * @return
     */
    protected final UserAccount getSessionUserAccount() {
        return getSessionAttribute(Cons.SESSION_USER_KEY);
    }

    /**
     * 取得当前登录人员角色ID信息
     *
     * @return
     */
    protected final Set<String> getSessionRoleIds() {
        Set<String> codes = Sets.newHashSet();
        getSessionRoles().forEach(role -> codes.add(role.getId()));
        return codes;
    }

    /**
     * 取得当前登录人员角色code信息
     *
     * @return
     */
    protected final Set<String> getSessionRoleCodes() {
        Set<String> codes = Sets.newHashSet();
        getSessionRoles().forEach(role -> codes.add(role.getRoleCode()));
        return codes;
    }

    /**
     * 取得当前登录人员角色ID信息
     *
     * @return
     */
    protected final List<Role> getSessionRoles() {
        List<Role> roles = getSessionAttribute(Cons.SESSION_ROLES_KEY);
        if (roles == null) {
            return Lists.newArrayList();
        }
        return roles;
    }

    /**
     * 取得当前登录人员可以管理的所有人员
     *
     * @return
     */
    protected final Set<String> getSessionManagerUserNames() {
        Set<String> userNames = getSessionAttribute(Cons.SESSION_MANAGER_USER_NAMES_KEY);
        if (userNames == null) {
            return Sets.newHashSet();
        }
        return userNames;
    }

}
