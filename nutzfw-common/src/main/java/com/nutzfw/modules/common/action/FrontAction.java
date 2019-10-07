/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
