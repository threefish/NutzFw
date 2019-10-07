/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.shiro;

import com.nutzfw.modules.organize.entity.UserAccount;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author huchuc@vip.qq.com
 * @date  2016/1/22
 */
@IocBean
public class UserPassRealm extends AbstractAuthorizingRealm {

    public UserPassRealm() {
        setAuthenticationTokenClass(UserPassToken.class);
        setCredentialsMatcher(new AllowAllCredentialsMatcher());
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        this.initServices();
        UserPassToken token = (UserPassToken) authenticationToken;
        UserAccount userAccount = userAccountService.loginFind(token.getUsername());
        String password = String.valueOf(token.getPassword());
        token.setPassword(password.toCharArray());
        this.onLoginSuccess(userAccount);
        return new SimpleAuthenticationInfo(token, password, getClass().getName());
    }


}
