package com.nutzfw.core.plugin.shiro;

/**
 * @author huchuc@vip.qq.com
 * @date 2019/3/29  18:17
 */
public class SsoToken extends QrCodeToken {

    public SsoToken(String userName, String host) {
        super(userName, host);
    }
}
