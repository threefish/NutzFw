package com.nutzfw.modules.organize.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.organize.entity.UserImportHistory;
import com.nutzfw.modules.organize.service.UserImportHistoryService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 叶世游
 * @date 2018年06月22日 20时26分43秒
 */
@IocBean(name = "userImportHistoryService", args = {"refer:dao"})
public class UserImportHistoryServiceImpl extends BaseServiceImpl<UserImportHistory> implements UserImportHistoryService {
    public UserImportHistoryServiceImpl(Dao dao) {
        super(dao);
    }
}
