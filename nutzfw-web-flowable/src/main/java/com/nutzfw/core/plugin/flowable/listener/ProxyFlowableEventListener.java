/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.listener;

import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.impl.cfg.TransactionState;

import java.util.List;
import java.util.Objects;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/30
 * 代理事件处理器（同一个事件可能会有不同的事件需要执行）
 */
public class ProxyFlowableEventListener implements FlowableEventListener {

    List<EventListenerHandle> listeners;

    FlowableEngineEventType flowableEngineEventType;

    /**
     * @see TransactionState
     */
    String onTransaction;

    public ProxyFlowableEventListener(FlowableEngineEventType flowableEngineEventType, List<EventListenerHandle> listeners) {
        this.flowableEngineEventType = flowableEngineEventType;
        this.listeners = listeners;
    }

    public ProxyFlowableEventListener(FlowableEngineEventType flowableEngineEventType, TransactionState transactionState, List<EventListenerHandle> listeners) {
        this.flowableEngineEventType = flowableEngineEventType;
        this.listeners = listeners;
        this.onTransaction = transactionState.name();
    }

    @Override
    public void onEvent(FlowableEvent event) {
        if (event.getType().name().equals(flowableEngineEventType.name())) {
            listeners.stream().filter(Objects::nonNull).forEach(flowableEventListener -> flowableEventListener.onEvent(event));
        }
    }

    @Override
    public boolean isFailOnException() {
        return true;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }

    @Override
    public String getOnTransaction() {
        return onTransaction;
    }
}
