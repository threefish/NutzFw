package com.nutzfw.modules.monitor.websocket;


import com.nutzfw.modules.monitor.websocket.handler.TailLogsWsHandler;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.mvc.websocket.NutWsConfigurator;
import org.nutz.plugins.mvc.websocket.WsHandler;

import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/3/1
 * 实时日志监控通道
 */
@ServerEndpoint(value = "/tailLogs/info", configurator = NutWsConfigurator.class)
@IocBean
public class TailLogsInfoWs extends LogsWsSessionsEndpoint {

    @Inject("java:$conf.get('command.info')")
    String command;

    @Override
    public WsHandler createHandler(Session httpSession, EndpointConfig config) {
        if (tailLogsWsHandler == null) {
            this.tailLogsWsHandler = new TailLogsWsHandler(command, "info");
        }
        return tailLogsWsHandler;
    }

    @Override
    public String getCommand() {
        return command;
    }
}