package com.nutzfw.modules.sys.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.sys.entity.UserLoginHistory;
import com.nutzfw.modules.sys.service.UserLoginHistoryService;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2018/1/9  17:09
 * 描述此类：
 */
@IocBean(name = "userLoginHistoryService", args = {"refer:dao"})
public class UserLoginHistoryServiceImpl extends BaseServiceImpl<UserLoginHistory> implements UserLoginHistoryService {

    public UserLoginHistoryServiceImpl(Dao dao) {
        super(dao);
    }

    @Override
    public boolean hasHistory(String userid) {
        return count(Cnd.where("uid", "=", userid)) > 0;
    }

    @Async
    @Override
    public void async(UserLoginHistory userLoginHistory) {
        sync(userLoginHistory);
    }

    @Override
    public void sync(UserLoginHistory userLoginHistory) {
        dao.insert(userLoginHistory);
    }
}
