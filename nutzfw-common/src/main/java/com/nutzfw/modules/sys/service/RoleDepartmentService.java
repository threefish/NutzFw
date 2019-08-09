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
