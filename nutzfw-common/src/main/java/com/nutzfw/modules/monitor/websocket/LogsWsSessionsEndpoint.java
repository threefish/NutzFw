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
