package com.nutzfw.modules.sys.action;

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
 * 描述此类：邮件管理-涵盖邮件发送记录，发送状态，邮件信息，单独发送邮件
 */
@IocBean
@At("/sysSMS")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class SmsAction extends BaseAction {


    @Ok("btl:WEB-INF/view/sys/monitor/sms/index.html")
    @GET
    @At("/index")
    @RequiresPermissions("sysSMS.index")
    @AutoCreateMenuAuth(name = "短信管理", icon = "fa-eye", parentPermission = "sys.monitor")
    public void index() {
    }

}
