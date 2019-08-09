package com.nutzfw.modules.organize.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.organize.entity.UserAccountJob;
import com.nutzfw.modules.organize.service.UserAccountJobService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author panchuang
 * @data 2018/6/14 0014
 */
@IocBean(name = "userAccountJobService", args = {"refer:dao"})
public class UserAccountJobServiceImpl extends BaseServiceImpl<UserAccountJob> implements UserAccountJobService {
    public UserAccountJobServiceImpl(Dao dao) {
        super(dao);
    }
}
