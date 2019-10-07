/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
