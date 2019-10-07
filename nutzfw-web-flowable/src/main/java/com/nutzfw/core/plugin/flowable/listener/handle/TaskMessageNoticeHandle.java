/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.listener.handle;

import com.nutzfw.core.plugin.flowable.constant.FlowConstant;
import com.nutzfw.modules.flow.websocket.FlowTaskMessageNoticeWs;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.flowable.variable.api.persistence.entity.VariableInstance;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/30
 * 当前任务相关人员都会收到通知
 */
@IocBean(args = {"refer:$ioc"})
public class TaskMessageNoticeHandle extends BaseEventListenerHandle {

    @Inject
    FlowTaskMessageNoticeWs flowTaskMessageNoticeWs;

    public TaskMessageNoticeHandle(Ioc ioc) {
        super(ioc);
    }

    /**
     * 当前任务相关人员都会收到通知
     *
     * @param event
     */
    @Override
    public void execute(FlowableEvent event) {
        HashSet<String> userNames = new HashSet<>();
        if (event instanceof FlowableEngineEntityEvent) {
            FlowableEngineEntityEvent entityEvent = (FlowableEngineEntityEvent) event;
            TaskEntityImpl entity = (TaskEntityImpl) entityEvent.getEntity();
            VariableInstance submitterVar = entity.getVariableInstance(FlowConstant.SUBMITTER);
            VariableInstance nextReviewerVar = entity.getVariableInstance(FlowConstant.NEXT_REVIEWER);
            if (submitterVar != null) {
                this.addUser(userNames, submitterVar.getTextValue());
            }
            if (nextReviewerVar != null) {
                this.addUser(userNames, nextReviewerVar.getTextValue());
            }
            this.addUser(userNames, entity.getAssignee());
            this.addUser(userNames, entity.getOriginalAssignee());
            this.addUser(userNames, entity.getOwner());
        }
        this.addUser(userNames, Authentication.getAuthenticatedUserId());
        flowTaskMessageNoticeWs.sendMessageNotice(Arrays.asList(userNames.toArray(new String[0])));
    }

    private void addUser(HashSet<String> userNames, String userName) {
        if (Strings.isNotBlank(userName)) {
            userNames.add(userName);
        }
    }
}
