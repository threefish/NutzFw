package com.nutzfw.modules.portal.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.portal.entity.PortalFunction;
import com.nutzfw.modules.portal.service.PortalFunctionService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 叶世游
 * @date 2018年06月19日 14时46分10秒
 */
@IocBean(args = {"refer:dao"})
public class PortalFunctionServiceImpl extends BaseServiceImpl<PortalFunction> implements PortalFunctionService {
    public PortalFunctionServiceImpl(Dao dao) {
        super(dao);
    }
}
