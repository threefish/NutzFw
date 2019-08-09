package com.nutzfw.modules.common.action;

import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

/**
 * Created by IntelliJ IDEA.
 * User: huchuc@vip.qq.com
 * Date: 2016/12/22 0022
 * To change this template use File | Settings | File Templates.
 */
@IocBean
@At("/setting/icon")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class IconAction extends BaseAction {

    @Ok("btl:WEB-INF/view/tool/icon.html")
    @GET
    @At("/index")
    public void index(@Param("domid") String domid) {
        setRequestAttribute("domid", domid);
    }
}
