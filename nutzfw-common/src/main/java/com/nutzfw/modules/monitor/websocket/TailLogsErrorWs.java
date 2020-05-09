/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
@ServerEndpoint(value = "/tailLogs/error", configurator = NutWsConfigurator.class)
@IocBean
public class TailLogsErrorWs extends AbstractLogsWsSessionsEndpoint {

    @Inject("java:$conf.get('command.error')")
    String command;

    @Override
    public WsHandler createHandler(Session httpSession, EndpointConfig config) {
        if (tailLogsWsHandler == null) {
            this.tailLogsWsHandler = new TailLogsWsHandler(command, "error");
        }
        return tailLogsWsHandler;
    }


    @Override
    public String getCommand() {
        return command;
    }
}
