/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.flow.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.core.plugin.flowable.constant.FlowConstant;
import com.nutzfw.modules.flow.entity.FlowType;
import com.nutzfw.modules.flow.service.FlowTypeService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2019年04月15日 17时07分04秒
 */
@IocBean(args = {"refer:dao"}, name = "flowTypeService")
public class FlowTypeServiceImpl extends BaseServiceImpl<FlowType> implements FlowTypeService {
    public FlowTypeServiceImpl(Dao dao) {
        super(dao);
    }

    @Override
    public String fetchCategoryName(String categoryId) {
        if (FlowConstant.DEFAULT_CATEGORY.equals(categoryId)) {
            return "未分类";
        }
        return fetch(categoryId).getName();
    }
}
