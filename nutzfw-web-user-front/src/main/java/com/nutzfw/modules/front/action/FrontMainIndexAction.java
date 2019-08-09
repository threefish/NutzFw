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
