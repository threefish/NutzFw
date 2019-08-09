package com.nutzfw.modules.portal.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.portal.entity.QuickLink;
import com.nutzfw.modules.portal.service.QuickLinkService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 叶世游
 * @date 2018年06月19日 14时22分49秒
 */
@IocBean(args = {"refer:dao"})
public class QuickLinkServiceImpl extends BaseServiceImpl<QuickLink> implements QuickLinkService {
    public QuickLinkServiceImpl(Dao dao) {
        super(dao);
    }
}
