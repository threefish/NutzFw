/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.service;


import com.nutzfw.core.common.service.BaseService;
import com.nutzfw.modules.sys.entity.RoleDepartment;

import java.util.List;
import java.util.Set;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年06月04日 20时12分39秒
 */
public interface RoleDepartmentService extends BaseService<RoleDepartment> {
    /**
     * 查询自己管理的所有部门
     *
     * @param roleids
     * @return
     */
    List<RoleDepartment> queryManagerDepartment(Set<String> roleids);
}
