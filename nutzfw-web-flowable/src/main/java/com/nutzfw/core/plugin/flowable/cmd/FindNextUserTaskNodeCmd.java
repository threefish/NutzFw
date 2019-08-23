package com.nutzfw.core.plugin.flowable.cmd;

import org.apache.commons.collections.CollectionUtils;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.condition.ConditionUtil;

import java.util.List;
import java.util.Map;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/9
 * 使用示例，一定要放到事务中，否则变量会入库，导致数据紊乱
 * try {
 * Trans.begin();
 * UserTask userTask = managementService.executeCommand(new FindNextUserTaskNodeCmd(execution, bpmnModel, vars));
 * System.out.println(userTask.getId());
 * } finally {
 * Trans.clear(true);
 * }
 */
public class FindNextUserTaskNodeCmd implements Command<UserTask> {

    private final ExecutionEntity execution;
    private final BpmnModel bpmnModel;
    private Map<String, Object> vars;
    /**
     * 返回下一用户节点
     */
    private UserTask nextUserTask;

    /**
     * @param execution 当前执行实例
     * @param bpmnModel 当前执行实例的模型
     * @param vars      参与计算流程条件的变量
     */
    public FindNextUserTaskNodeCmd(ExecutionEntity execution, BpmnModel bpmnModel, Map<String, Object> vars) {
        this.execution = execution;
        this.bpmnModel = bpmnModel;
        this.vars = vars;
    }

    /**
     * @param execution 当前执行实例
     * @param bpmnModel 当前执行实例的模型
     */
    public FindNextUserTaskNodeCmd(ExecutionEntity execution, BpmnModel bpmnModel) {
        this.execution = execution;
        this.bpmnModel = bpmnModel;
    }

    @Override
    public UserTask execute(CommandContext commandContext) {
        execution.setVariables(vars);
        FlowElement currentNode = bpmnModel.getFlowElement(execution.getActivityId());
        List<SequenceFlow> outgoingFlows = ((FlowNode) currentNode).getOutgoingFlows();
        if (CollectionUtils.isNotEmpty(outgoingFlows)) {
            this.findNextUserTaskNode(outgoingFlows, execution);
        }
        return nextUserTask;
    }


    void findNextUserTaskNode(List<SequenceFlow> outgoingFlows, ExecutionEntity execution) {
        sw:
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            if (ConditionUtil.hasTrueCondition(outgoingFlow, execution)) {
                if (outgoingFlow.getTargetFlowElement() instanceof ExclusiveGateway) {
                    //只有排他网关才继续
                    ExclusiveGateway exclusiveGateway = (ExclusiveGateway) outgoingFlow.getTargetFlowElement();
                    findNextUserTaskNode(exclusiveGateway.getOutgoingFlows(), execution);
                } else if (outgoingFlow.getTargetFlowElement() instanceof UserTask) {
                    nextUserTask = (UserTask) outgoingFlow.getTargetFlowElement();
                    //找到第一个符合条件的userTask就跳出循环
                    break sw;
                }
            }
        }
    }
}
