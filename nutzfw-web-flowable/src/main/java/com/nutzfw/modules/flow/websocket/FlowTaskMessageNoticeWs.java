/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.flow.websocket;

import com.nutzfw.modules.flow.websocket.handler.FlowTaskMessageNoticeWsHandler;
import org.apache.commons.collections.CollectionUtils;
import org.flowable.engine.TaskService;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.plugins.mvc.websocket.AbstractWsEndpoint;
import org.nutz.plugins.mvc.websocket.NutWsConfigurator;
import org.nutz.plugins.mvc.websocket.WsHandler;

import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/7/27
 */
@ServerEndpoint(value = "/flow/task/MessageNotice", configurator = NutWsConfigurator.class)
@IocBean(args = {"refer:$ioc"})
public class FlowTaskMessageNoticeWs extends AbstractWsEndpoint {
    /**
     * 不要直接注入，会导致循环依赖
     */
    TaskService taskService;

    private Ioc ioc;

    public FlowTaskMessageNoticeWs(Ioc ioc) {
        this.ioc = ioc;
    }

    public void init() {
        if (this.taskService == null) {
            taskService = ioc.get(TaskService.class);
        }
    }

    /**
     * 根据WebSocket会话创建一个WsHandler. 注意,
     * 该实例还得实现MessageHandler.Whole或MessageHandler.Partial接口!!!
     */
    @Override
    public WsHandler createHandler(Session session, EndpointConfig config) {
        return new FlowTaskMessageNoticeWsHandler(taskService);
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.init();
        super.onOpen(session, config);
        MessageHandler messageHandler = this.handlers.get(session.getId());
        if (messageHandler instanceof FlowTaskMessageNoticeWsHandler) {
            FlowTaskMessageNoticeWsHandler handler = (FlowTaskMessageNoticeWsHandler) messageHandler;
            handler.queryTaskNotice(NutMap.NEW());
        }
    }

    /**
     * 在线用户数
     *
     * @return
     */
    public int getOnlineSeesionSize() {
        return this.sessions.size();
    }

    public ConcurrentHashMap<String, WsHandler> getHandlers() {
        return this.handlers;
    }

    /**
     * 通知这些人员需要发送流程通知信息
     *
     * @param userNames
     */
    public void sendMessageNotice(List<String> userNames) {
        if (CollectionUtils.isNotEmpty(userNames)) {
            if (this.getOnlineSeesionSize() > 0) {
                ConcurrentHashMap<String, WsHandler> handlers = this.getHandlers();
                handlers.forEachValue(20, wsHandler -> {
                    FlowTaskMessageNoticeWsHandler handler = (FlowTaskMessageNoticeWsHandler) wsHandler;
                    //当前用户是在通知列表中
                    if (userNames.contains(handler.getUserName())) {
                        handler.sendHasNotice();
                    }
                });
            }
        }
    }

}
