package com.nutzfw.modules.portal.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.portal.entity.PortalUser;
import com.nutzfw.modules.portal.service.PortalUserService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 叶世游
 * @date 2018年06月15日 20时00分16秒
 */
@IocBean(args = {"refer:dao"})
public class PortalUserServiceImpl extends BaseServiceImpl<PortalUser> implements PortalUserService {
    public PortalUserServiceImpl(Dao dao) {
        super(dao);
    }
}
