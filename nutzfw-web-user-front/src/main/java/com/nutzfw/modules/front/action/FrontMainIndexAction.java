/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.front.action;

import com.nutzfw.modules.common.action.BaseAction;
import org.apache.shiro.SecurityUtils;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/5/29
 */
@IocBean
@At("/front/")
public class FrontMainIndexAction extends BaseAction {

    @At({"index", ""})
    @Ok("btl:WEB-INF/view/modules/front/index.html")
    public NutMap index() {
        return NutMap.NEW();
    }


    @GET
    @At("login")
    @Ok("btl:WEB-INF/view/modules/front/login.html")
    public NutMap login() {
        return NutMap.NEW();
    }

    @GET
    @At("logout")
    @Ok("btl:WEB-INF/view/modules/front/login.html")
    public void logout() {
        SecurityUtils.getSubject().logout();
    }

}
