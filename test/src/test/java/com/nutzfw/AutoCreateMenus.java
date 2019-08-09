package com.nutzfw;

import com.nutzfw.core.common.util.ScanerAotuCreateMenusUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/14
 * 描述此类：
 */
@RunWith(TestRunner.class)
@IocBean
public class AutoCreateMenus {

    @Inject
    Dao dao;

    /**
     * 自动创建菜单
     */
    @Test
    public void scanPackage() {
        ScanerAotuCreateMenusUtil util = new ScanerAotuCreateMenusUtil();
        //扫描权限-菜单
        util.saveAutoScanMenus(dao, true);
    }

}
