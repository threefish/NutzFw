/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
