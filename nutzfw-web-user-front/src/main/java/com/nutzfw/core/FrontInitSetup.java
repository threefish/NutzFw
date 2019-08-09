package com.nutzfw.core;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/5/29
 * 可以做一些初始化系统的事情
 */
@IocBean
public class FrontInitSetup extends SystemInit implements Setup {

    @Override
    public void init(NutConfig nutConfig) {

    }

    @Override
    public void destroy(NutConfig nc) {

    }

}
