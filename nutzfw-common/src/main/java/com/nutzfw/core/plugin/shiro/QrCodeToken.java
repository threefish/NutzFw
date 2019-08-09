package com.nutzfw.core.plugin.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author huchuc@vip.qq.com
 * @date 2019/3/29  18:17
 */
public class QrCodeToken implements AuthenticationToken {

    private static final long serialVersionUID = -1L;

    private String userName;

    private String host;

    public QrCodeToken(String userName, String host) {
        this.userName = userName;
        this.host = host;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public Object getPrincipal() {
        return userName;
    }

    @Override
    public Object getCredentials() {
        return userName;
    }
}
