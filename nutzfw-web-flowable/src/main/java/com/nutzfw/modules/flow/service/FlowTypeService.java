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
