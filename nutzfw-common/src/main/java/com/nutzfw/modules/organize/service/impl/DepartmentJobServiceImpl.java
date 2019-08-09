package com.nutzfw.modules.organize.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.organize.entity.DepartmentJob;
import com.nutzfw.modules.organize.service.DepartmentJobService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年06月01日 19时36分15秒
 */
@IocBean(name = "departmentJobService", args = {"refer:dao"})
public class DepartmentJobServiceImpl extends BaseServiceImpl<DepartmentJob> implements DepartmentJobService {
    public DepartmentJobServiceImpl(Dao dao) {
        super(dao);
    }
}
