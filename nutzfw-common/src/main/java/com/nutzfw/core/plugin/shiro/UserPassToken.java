/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * @author huchuc@vip.qq.com
 * @date 2017/9/4  18:17
 */
public class UserPassToken extends UsernamePasswordToken {

    private static final long          serialVersionUID = -1L;
    /**
     * 登录类型
     */
    private              LoginTypeEnum loginType;

    public UserPassToken(String username, String password, LoginTypeEnum loginType, boolean rememberMe, String host) {
        super(username, password, rememberMe, host);
        this.loginType = loginType;
    }

    public LoginTypeEnum getLoginType() {
        return loginType;
    }

    public void setLoginType(LoginTypeEnum loginType) {
        this.loginType = loginType;
    }

}
