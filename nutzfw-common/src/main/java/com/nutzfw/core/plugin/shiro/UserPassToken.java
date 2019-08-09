package com.nutzfw.core.plugin.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * @author huchuc@vip.qq.com
 * @date 2017/9/4  18:17
 */
public class UserPassToken extends UsernamePasswordToken {

    private static final long serialVersionUID = -1L;
    /**
     * 登录类型
     */
    private LoginTypeEnum loginType;

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
