/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.monitor.websocket;


import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.plugins.mvc.websocket.AbstractWsEndpoint;
import org.nutz.plugins.mvc.websocket.NutWsConfigurator;

import javax.websocket.server.ServerEndpoint;


/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/5/15
 */
@ServerEndpoint(value = "/apm/dashboard", configurator = NutWsConfigurator.class)
@IocBean
public class ApmDashboardWs extends AbstractWsEndpoint {

    static final String ROOM_NAME = "APMDASHBOARD";

    public void sendJson(NutMap data) {
        each(roomPrefix + ROOM_NAME, (index, session, length) -> {
            try {
                this.sendJson(session.getId(), data);
            } catch (Exception e) {
            }
        });
    }
}