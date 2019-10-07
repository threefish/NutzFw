/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:30:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.organize.service;


import com.nutzfw.core.common.service.BaseService;
import com.nutzfw.modules.organize.entity.UserAccount;

import java.util.List;

/**
 * Created with IntelliJ IDEA Code Generator
 *
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年01月02日 15时39分07秒
 * 功能描述： 人员管理
 */
public interface UserAccountService extends BaseService<UserAccount> {

    UserAccount fetchByUserName(String userName);

    boolean userNameExist(String userName);

    UserAccount fetchByUserId(String userId);

    UserAccount loginFind(String userName);

    List<UserAccount> findUsersByNameFilter(String filter);
}
