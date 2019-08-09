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