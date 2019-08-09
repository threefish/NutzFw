package com.nutzfw.core;

import com.nutzfw.modules.sys.biz.DictBiz;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/3/22
 */
@IocBean
public class OaInitSetup extends SystemInit implements Setup {


    @Override
    public void init(NutConfig nutConfig) {
        //创建系统数据字典，如果不存在则创建
        this.ifNotExistCreateDictGroup(nutConfig.getIoc().get(DictBiz.class), "holiday_type", "假期类型");
    }

    @Override
    public void destroy(NutConfig nc) {
    }

}
