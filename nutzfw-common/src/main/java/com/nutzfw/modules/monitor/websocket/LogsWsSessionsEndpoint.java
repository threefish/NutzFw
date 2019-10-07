/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.monitor.websocket;

import com.nutzfw.modules.monitor.websocket.handler.TailLogsWsHandler;
import org.nutz.plugins.mvc.websocket.AbstractWsEndpoint;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/3/4
 */
public abstract class LogsWsSessionsEndpoint extends AbstractWsEndpoint {


    TailLogsWsHandler tailLogsWsHandler;

    public abstract String getCommand();


    public void stop() {
        tailLogsWsHandler.depose();
    }

    public boolean getStatus() {
        if (tailLogsWsHandler == null) {
            return false;
        }
        return tailLogsWsHandler.getProcess().isAlive();
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session, closeReason);
        tailLogsWsHandler.depose();
    }

    public ConcurrentHashMap<String, Session> getSessions() {
        return this.sessions;
    }
}
