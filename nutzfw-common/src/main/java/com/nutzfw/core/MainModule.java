/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core;

import com.nutzfw.core.mvc.NutzFwNutActionChainMaker;
import com.nutzfw.core.plugin.view.BeetlViewMaker;
import org.nutz.integration.shiro.ShiroSessionProvider;
import org.nutz.mvc.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2015/4/2114:20
 */
@SetupBy(value = MainSetup.class)
@IocBy(args = {
        "*js", "ioc/",
        "*anno", "com.nutzfw",
        "*tx",
        "*jedis",
        "*async",
        "*com.nutzfw.core.plugin.quartz.QuartzIocLoader"
})
@Modules(packages = "com.nutzfw.modules")
@ChainBy(type = NutzFwNutActionChainMaker.class, args = "mvc/web-mvc-chain.js")
@Ok("json:{nullAsEmtry:true}")
@Fail("btl:/error/500.html")
@Views(BeetlViewMaker.class)
@Localization(value = "localization", defaultLocalizationKey = "zh_CN")
@SessionBy(ShiroSessionProvider.class)
public class MainModule {
}
