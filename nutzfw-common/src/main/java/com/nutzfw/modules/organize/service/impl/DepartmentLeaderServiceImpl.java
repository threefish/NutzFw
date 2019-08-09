package com.nutzfw.modules.organize.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.organize.entity.Department;
import com.nutzfw.modules.organize.entity.DepartmentLeader;
import com.nutzfw.modules.organize.enums.LeaderTypeEnum;
import com.nutzfw.modules.organize.service.DepartmentLeaderService;
import com.nutzfw.modules.organize.service.DepartmentService;
import org.apache.commons.collections4.CollectionUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2019年07月03日
 * 部门领导关联信息
 */
@IocBean(args = {"refer:dao"})
public class DepartmentLeaderServiceImpl extends BaseServiceImpl<DepartmentLeader> implements DepartmentLeaderService {

    @Inject
    DepartmentService departmentService;

    public DepartmentLeaderServiceImpl(Dao dao) {
        super(dao);
    }

    @Override
    public List<String> queryUserNames(String deptId, LeaderTypeEnum typeEnum) {
        List<String> list = new ArrayList<>();
        query(Cnd.where("deptId", "=", deptId).and("LeaderType", "=", typeEnum)).forEach(departmentLeader -> list.add(departmentLeader.getUserName()));
        return list;
    }

    @Override
    public List<String> queryIterationUserNames(String deptId, LeaderTypeEnum typeEnum) {
        Department department;
        List<String> userNames = null;
        do {
            department = departmentService.fetch(Cnd.where("id", "=", deptId));
            if (department != null) {
                deptId = department.getPid();
                userNames = queryUserNames(deptId, typeEnum);
            }
        } while (CollectionUtils.isEmpty(userNames) && department != null);
        return userNames;
    }
}
