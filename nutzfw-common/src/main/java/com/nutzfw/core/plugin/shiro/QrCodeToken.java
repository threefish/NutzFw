/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
