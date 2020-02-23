/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core;

import com.nutzfw.core.common.entity.BaseEntity;
import com.nutzfw.core.plugin.init.InitSetup;
import com.nutzfw.modules.sys.entity.QuartzJob;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.NutConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/3/22
 */
@IocBean
public class OaInitSetup implements InitSetup {

    @Override
    public void init(NutConfig nutConfig) {
    }

    @Override
    public void destroy(NutConfig nc) {
    }

    @Override
    public void addAttachType(HashMap<String, String> attachType) {
    }

    @Override
    public void addDictGroup(HashMap<String, String> dictGroup) {
        dictGroup.put("holiday_type", "假期类型");
    }

    @Override
    public void addTablesFilters(Set<Class<? extends BaseEntity>> tablesFilters) {
    }

    @Override
    public void addQuartzJob(List<QuartzJob> quartzJobs) {
    }
}
