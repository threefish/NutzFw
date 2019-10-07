/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
