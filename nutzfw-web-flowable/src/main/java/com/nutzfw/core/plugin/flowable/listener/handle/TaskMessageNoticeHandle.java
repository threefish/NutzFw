/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/11/24 12:10:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.listener.handle;

import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import java.util.HashSet;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/30
 * 当前任务相关人员都会收到通知
 */
@IocBean(args = {"refer:$ioc"})
public class TaskMessageNoticeHandle extends BaseEventListenerHandle {

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
        //TODO 未来将采用新的方式进行通知，如消息队列进行异步，不能使用websocket
//        HashSet<String> userNames = new HashSet<>();
//        if (event instanceof FlowableEngineEntityEvent) {
//            FlowableEngineEntityEvent entityEvent = (FlowableEngineEntityEvent) event;
//            TaskEntityImpl entity = (TaskEntityImpl) entityEvent.getEntity();
//            VariableInstance submitterVar = entity.getVariableInstance(FlowConstant.SUBMITTER);
//            VariableInstance nextReviewerVar = entity.getVariableInstance(FlowConstant.NEXT_REVIEWER);
//            if (submitterVar != null) {
//                this.addUser(userNames, submitterVar.getTextValue());
//            }
//            if (nextReviewerVar != null) {
//                this.addUser(userNames, nextReviewerVar.getTextValue());
//            }
//            this.addUser(userNames, entity.getAssignee());
//            this.addUser(userNames, entity.getOriginalAssignee());
//            this.addUser(userNames, entity.getOwner());
//        }
//        this.addUser(userNames, Authentication.getAuthenticatedUserId());

    }

    private void addUser(HashSet<String> userNames, String userName) {
        if (Strings.isNotBlank(userName)) {
            userNames.add(userName);
        }
    }
}
