package com.nutzfw.modules.portal.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.portal.entity.Portal;
import com.nutzfw.modules.portal.service.PortalService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 叶世游
 * @date 2018年06月15日 20时00分30秒
 */
@IocBean(args = {"refer:dao"})
public class PortalServiceImpl extends BaseServiceImpl<Portal> implements PortalService {
    public PortalServiceImpl(Dao dao) {
        super(dao);
    }
}
