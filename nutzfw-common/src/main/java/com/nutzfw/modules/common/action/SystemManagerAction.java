package com.nutzfw.modules.common.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/28
 * 描述此类：系统管理
 */
@IocBean
@At("/sys")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class SystemManagerAction {

    /***一级菜单配置----开始***/
    @At("/index")
    @RequiresPermissions("sys.index")
    @AutoCreateMenuAuth(name = "系统管理", icon = "fa-cogs")
    public void index() {
    }

    @At("/monitor")
    @RequiresPermissions("sys.monitor")
    @AutoCreateMenuAuth(name = "服务器监控", icon = "fa-tachometer")
    public void monitor() {
    }

    @At("/monitoring")
    @RequiresPermissions("sys.javaMelodyMonitoring")
    @AutoCreateMenuAuth(name = "JavaMelody监控", atPath = "/monitoring", icon = "fa-tachometer", parentPermission = "sys.monitor")
    public void javaMelodyMonitoring() {
    }

    @At("/organize")
    @RequiresPermissions("sysOrganize.index")
    @AutoCreateMenuAuth(name = "组织架构管理", icon = "fa-sitemap")
    public void organize() {
    }

    @At("/maintain")
    @RequiresPermissions("sys.maintain")
    @AutoCreateMenuAuth(name = "数据维护", icon = "fa-sitemap")
    public void maintain() {
    }


    @At("/dataReview")
    @RequiresPermissions("sys.dataReview")
    @AutoCreateMenuAuth(name = "信息审核", icon = "fa-sitemap")
    public void dataReview() {
    }
    /***一级菜单配置----结束***/

}
