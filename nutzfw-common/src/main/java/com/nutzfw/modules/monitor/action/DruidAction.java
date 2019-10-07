/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.monitor.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.modules.common.action.BaseAction;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/28
 * 描述此类：
 */
@IocBean
@At("/monitor/druid")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class DruidAction extends BaseAction {


    @Ok("btl:WEB-INF/view/sys/monitor/druid/index.html")
    @GET
    @At("/dashboard")
    @RequiresPermissions("sysMonitor.druid")
    @AutoCreateMenuAuth(name = "Druid 监控", icon = "fa-eye", parentPermission = "sys.monitor")
    public void dashboard() {
    }

    @At("/sqlDetail")
    @GET
    @Ok("btl:WEB-INF/view/sys/monitor/druid/sqlDetail.html")
    @RequiresPermissions("sysMonitor.druid")
    public void sqlDetail(@Param("sqlId") int sqlId) {
        setRequestAttribute("sqlId", sqlId);
    }

    @At("/connectionPool")
    @GET
    @Ok("btl:WEB-INF/view/sys/monitor/druid/connectionPool.html")
    @RequiresPermissions("sysMonitor.druid")
    public void connectionPool(@Param("id") int id) {
        setRequestAttribute("id", id);
    }

    @At("/sessionDetail")
    @GET
    @Ok("btl:WEB-INF/view/sys/monitor/druid/sessionDetail.html")
    @RequiresPermissions("sysMonitor.druid")
    public void sessionDetail(@Param("sessionId") String sessionId) {
        setRequestAttribute("sessionId", sessionId);
    }

    @At("/uriDetail")
    @GET
    @Ok("btl:WEB-INF/view/sys/monitor/druid/uriDetail.html")
    @RequiresPermissions("sysMonitor.druid")
    public void uriDetail(@Param("uri") String uri) {
        setRequestAttribute("uri", uri);
    }

}
