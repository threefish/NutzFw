package com.nutzfw.modules.common.action;

import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2017/12/25  19:30
 * 描述此类：
 */
@IocBean
@At("/main/")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class MainAction extends BaseAction {

    /**
     * 后台管理框架
     */
    @GET
    @At("platform")
    @Ok("btl:WEB-INF/view/platform.html")
    public void platform() {

    }

    /**
     * 后台管理首页
     */
    @GET
    @At("index")
    @Ok("btl:WEB-INF/view/index.html")
    public void index() {

    }

}
