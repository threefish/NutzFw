/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.flow.service;

import com.nutzfw.core.common.service.BaseService;
import com.nutzfw.modules.flow.entity.FlowType;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2019年04月15日 17时07分04秒
 */
public interface FlowTypeService extends BaseService<FlowType> {

    String fetchCategoryName(String categoryId);
}
