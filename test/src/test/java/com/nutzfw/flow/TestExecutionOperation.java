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

    private final CommandContext commandContext;
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
