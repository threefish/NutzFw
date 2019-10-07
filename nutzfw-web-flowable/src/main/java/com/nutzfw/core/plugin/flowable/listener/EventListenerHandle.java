/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.listener;

import org.flowable.common.engine.api.delegate.event.FlowableEvent;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/30
 * 可以声明为IocBean 但是不应引用流程引擎的8大service（循环依赖引用也不应该），因为在启动时这些service都还没创建好
 * 可以引用ioc再手动去获取，或继承 BaseEventListenerHandle
 */
public interface EventListenerHandle {
    /**
     * 执行事件
     *
     * @param event
     */
    void onEvent(FlowableEvent event);
}
