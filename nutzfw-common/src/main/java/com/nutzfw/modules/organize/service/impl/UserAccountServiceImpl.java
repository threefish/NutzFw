/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.organize.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.service.UserAccountService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2018/1/9  17:09
 * 描述此类：
 */
@IocBean(name = "userAccountService", args = {"refer:dao"})
public class UserAccountServiceImpl extends BaseServiceImpl<UserAccount> implements UserAccountService {

    public UserAccountServiceImpl(Dao dao) {
        super(dao);
    }

    @Override
    public UserAccount fetchByUserName(String userName) {
        return fetch(Cnd.where("userName", "=", userName));
    }

    @Override
    public boolean userNameExist(String userName) {
        return count(Cnd.where("userName", "=", userName)) > 0;
    }

    @Override
    public UserAccount fetchByUserId(String userId) {
        return fetch(Cnd.where("id", "=", userId));
    }

    /**
     * 必须是已审核的账号才能登陆
     *
     * @param userName
     * @return
     */
    @Override
    public UserAccount loginFind(String userName) {
        UserAccount userAccount = fetch(Cnd.where("userName", "=", userName).and("review", "=", 1));
        fetchLinks(userAccount, null);
        return userAccount;
    }

    @Override
    public List<UserAccount> findUsersByNameFilter(String filter) {
        return query(Cnd.where("userName", "like", "%" + filter + "%").or("realName", "like", "%" + filter + "%"));
    }
}
