package com.nutzfw.modules.sys.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.sys.entity.RoleJob;
import com.nutzfw.modules.sys.service.RoleJobService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 叶世游
 * @date 2018年06月04日 20时12分54秒
 */
@IocBean(args = {"refer:dao"})
public class RoleJobServiceImpl extends BaseServiceImpl<RoleJob> implements RoleJobService {
    public RoleJobServiceImpl(Dao dao) {
        super(dao);
    }
}
