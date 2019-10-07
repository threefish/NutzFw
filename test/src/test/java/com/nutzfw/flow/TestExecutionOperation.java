/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.flow;

import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.agenda.AbstractOperation;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/9
 */
public class TestExecutionOperation extends AbstractOperation {

    private final CommandContext  commandContext;
    private final ExecutionEntity execution;

    public TestExecutionOperation(CommandContext commandContext, ExecutionEntity execution) {
        this.commandContext = commandContext;
        this.execution = execution;
    }

    @Override
    public void run() {
        FlowElement currentFlowElement = getCurrentFlowElement(execution);
        if (currentFlowElement instanceof FlowNode) {
            System.out.println(currentFlowElement);


        }
    }
}
