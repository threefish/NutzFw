/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
