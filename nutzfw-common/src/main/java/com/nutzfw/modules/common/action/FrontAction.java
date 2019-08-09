package com.nutzfw.modules.common.action;

import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.vo.AjaxResult;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

/**
 * @author huchuc@vip.qq.com
 * @date 2019-3-29
 */
@IocBean
@At("/app")
public class FrontAction extends BaseAction {

    @At("/info")
    @Ok("json")
    public AjaxResult info() {
        return AjaxResult.sucess(Cons.optionsCach);
    }
}
