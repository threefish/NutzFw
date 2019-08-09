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
