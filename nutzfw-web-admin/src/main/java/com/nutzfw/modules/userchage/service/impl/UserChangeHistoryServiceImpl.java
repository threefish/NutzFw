package com.nutzfw.modules.userchage.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.userchage.entity.UserChangeHistory;
import com.nutzfw.modules.userchage.service.UserChangeHistoryService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 叶世游
 * @date 2018年07月09日 18时09分03秒
 */
@IocBean(args = {"refer:dao"})
public class UserChangeHistoryServiceImpl extends BaseServiceImpl<UserChangeHistory> implements UserChangeHistoryService {
    public UserChangeHistoryServiceImpl(Dao dao) {
        super(dao);
    }
}
