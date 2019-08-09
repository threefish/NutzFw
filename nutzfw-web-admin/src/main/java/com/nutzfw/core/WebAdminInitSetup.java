package com.nutzfw.core;

import com.github.threefish.nutz.sqltpl.SqlsTplHolder;
import com.nutzfw.core.common.util.ScanerAotuCreateMenusUtil;
import com.nutzfw.core.plugin.view.BeetlViewMaker;
import org.nutz.dao.Dao;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/3/22
 */
@IocBean
public class WebAdminInitSetup extends SystemInit implements Setup {

    @Override
    public void init(NutConfig nutConfig) {
        Ioc ioc = nutConfig.getIoc();
        PropertiesProxy conf = ioc.get(PropertiesProxy.class, "conf");
        SqlsTplHolder.DEVELOPER_MODE = BeetlViewMaker.isDev;
        boolean initSystem = conf.getBoolean("initSystem", false);
        Dao dao = ioc.get(Dao.class, "dao");
        //扫描权限-菜单
        new ScanerAotuCreateMenusUtil().saveAutoScanMenus(dao, initSystem);
    }

    @Override
    public void destroy(NutConfig nc) {

    }

}
