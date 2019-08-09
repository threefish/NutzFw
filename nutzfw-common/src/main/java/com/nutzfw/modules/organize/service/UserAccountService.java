package com.nutzfw.modules.organize.service;


import com.nutzfw.core.common.service.BaseService;
import com.nutzfw.modules.organize.entity.UserAccount;
import org.nutz.lang.util.NutMap;

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
