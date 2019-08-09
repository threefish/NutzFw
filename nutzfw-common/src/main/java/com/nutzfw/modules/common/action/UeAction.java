package com.nutzfw.modules.common.action;

import com.nutzfw.core.common.vo.AjaxResult;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/29
 * 描述此类：避免UE编辑请求JSON配置错误
 */
@IocBean
@At("/ue")
public class UeAction {

    @At("/controller")
    @GET
    @Ok("json")
    public AjaxResult controller() {
        return AjaxResult.sucess("");
    }
}
