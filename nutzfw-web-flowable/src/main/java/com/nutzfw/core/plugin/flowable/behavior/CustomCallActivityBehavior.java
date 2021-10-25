/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.behavior;

import com.nutzfw.core.plugin.flowable.context.CustomStartSubProcessInstanceBeforeContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEventDispatcher;
import org.flowable.common.engine.api.scope.ScopeTypes;
import org.flowable.common.engine.impl.el.ExpressionManager;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.event.impl.FlowableEventBuilder;
import org.flowable.engine.impl.bpmn.behavior.CallActivityBehavior;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.EntityLinkUtil;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.engine.interceptor.StartSubProcessInstanceAfterContext;
import org.flowable.engine.interceptor.StartSubProcessInstanceBeforeContext;
import org.flowable.engine.repository.ProcessDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huchuc@vip.qq.com
 * @date: 2021/10/25
 */
@SuppressWarnings("all")
@Slf4j
public class CustomCallActivityBehavior extends CallActivityBehavior {


   private CallActivity callActivity;

    public CustomCallActivityBehavior(CallActivity callActivity,String processDefinitionKey, String calledElementType, Boolean fallbackToDefaultTenant, List<MapExceptionEntry> mapExceptions) {
        super(processDefinitionKey, calledElementType, fallbackToDefaultTenant, mapExceptions);
    }

    public CustomCallActivityBehavior(CallActivity callActivity,Expression processDefinitionExpression, String calledElementType, List<MapExceptionEntry> mapExceptions, Boolean fallbackToDefaultTenant) {
        super(processDefinitionExpression, calledElementType, mapExceptions, fallbackToDefaultTenant);
    }

    @Override
    public void execute(DelegateExecution execution) {

        ExecutionEntity executionEntity = (ExecutionEntity) execution;
        CallActivity callActivity = (CallActivity) executionEntity.getCurrentFlowElement();

        CommandContext commandContext = CommandContextUtil.getCommandContext();

        ProcessEngineConfigurationImpl processEngineConfiguration = CommandContextUtil.getProcessEngineConfiguration(commandContext);

        ProcessDefinition processDefinition = getProcessDefinition(execution, callActivity, processEngineConfiguration);

        // Get model from cache
        Process subProcess = ProcessDefinitionUtil.getProcess(processDefinition.getId());
        if (subProcess == null) {
            throw new FlowableException("Cannot start a sub process instance. Process model " + processDefinition.getName() + " (id = " + processDefinition.getId() + ") could not be found");
        }

        FlowElement initialFlowElement = subProcess.getInitialFlowElement();
        if (initialFlowElement == null) {
            throw new FlowableException("No start element found for process definition " + processDefinition.getId());
        }

        // Do not start a process instance if the process definition is suspended
        if (ProcessDefinitionUtil.isProcessDefinitionSuspended(processDefinition.getId())) {
            throw new FlowableException("Cannot start process instance. Process definition " + processDefinition.getName() + " (id = " + processDefinition.getId() + ") is suspended");
        }

        ExecutionEntityManager executionEntityManager = CommandContextUtil.getExecutionEntityManager(commandContext);
        ExpressionManager expressionManager = processEngineConfiguration.getExpressionManager();

        String businessKey = null;
        if (!StringUtils.isEmpty(callActivity.getBusinessKey())) {
            Expression expression = expressionManager.createExpression(callActivity.getBusinessKey());
            businessKey = expression.getValue(execution).toString();

        } else if (callActivity.isInheritBusinessKey()) {
            ExecutionEntity processInstance = executionEntityManager.findById(execution.getProcessInstanceId());
            businessKey = processInstance.getBusinessKey();
        }

        Map<String, Object> variables = new HashMap<>();

        CustomStartSubProcessInstanceBeforeContext instanceBeforeContext = new CustomStartSubProcessInstanceBeforeContext(businessKey, callActivity.getProcessInstanceName(),
                variables, executionEntity, callActivity.getInParameters(), callActivity.isInheritVariables(),
                initialFlowElement.getId(), initialFlowElement, subProcess, processDefinition);
        instanceBeforeContext.setCallActivity(callActivity);
        if (processEngineConfiguration.getStartProcessInstanceInterceptor() != null) {
            processEngineConfiguration.getStartProcessInstanceInterceptor().beforeStartSubProcessInstance(instanceBeforeContext);
        }

        ExecutionEntity subProcessInstance = CommandContextUtil.getExecutionEntityManager(commandContext).createSubprocessInstance(
                instanceBeforeContext.getProcessDefinition(), instanceBeforeContext.getCallActivityExecution(),
                instanceBeforeContext.getBusinessKey(), instanceBeforeContext.getInitialActivityId());

        FlowableEventDispatcher eventDispatcher = processEngineConfiguration.getEventDispatcher();
        if (eventDispatcher != null && eventDispatcher.isEnabled()) {
            CommandContextUtil.getProcessEngineConfiguration().getEventDispatcher().dispatchEvent(
                    FlowableEventBuilder.createEntityEvent(FlowableEngineEventType.PROCESS_CREATED, subProcessInstance));
        }

        // process template-defined data objects
        //重构变量生成 subProcessInstance.setVariables(processDataObjects(subProcess.getDataObjects()));

        if (instanceBeforeContext.isInheritVariables()) {
            Map<String, Object> executionVariables = execution.getVariables();
            for (Map.Entry<String, Object> entry : executionVariables.entrySet()) {
                instanceBeforeContext.getVariables().put(entry.getKey(), entry.getValue());
            }
        }

        // copy process variables
        for (IOParameter inParameter : instanceBeforeContext.getInParameters()) {

            Object value = null;
            if (StringUtils.isNotEmpty(inParameter.getSourceExpression())) {
                Expression expression = expressionManager.createExpression(inParameter.getSourceExpression().trim());
                value = expression.getValue(execution);

            } else {
                value = execution.getVariable(inParameter.getSource());
            }

            String variableName = null;
            if (StringUtils.isNotEmpty(inParameter.getTargetExpression())) {
                Expression expression = expressionManager.createExpression(inParameter.getTargetExpression());
                Object variableNameValue = expression.getValue(execution);
                if (variableNameValue != null) {
                    variableName = variableNameValue.toString();
                } else {
                    log.warn("In parameter target expression {} did not resolve to a variable name, this is most likely a programmatic error",
                            inParameter.getTargetExpression());
                }

            } else if (StringUtils.isNotEmpty(inParameter.getTarget())){
                variableName = inParameter.getTarget();

            }

            instanceBeforeContext.getVariables().put(variableName, value);
        }

        if (!instanceBeforeContext.getVariables().isEmpty()) {
            initializeVariables(subProcessInstance, instanceBeforeContext.getVariables());
        }

        // Process instance name is resolved after setting the variables on the process instance, so they can be used in the expression
        String processInstanceName = null;
        if (StringUtils.isNotEmpty(instanceBeforeContext.getProcessInstanceName())) {
            Expression processInstanceNameExpression = expressionManager.createExpression(instanceBeforeContext.getProcessInstanceName());
            processInstanceName = processInstanceNameExpression.getValue(subProcessInstance).toString();
            subProcessInstance.setName(processInstanceName);
        }

        if (eventDispatcher != null && eventDispatcher.isEnabled()) {
            eventDispatcher.dispatchEvent(FlowableEventBuilder.createEntityEvent(FlowableEngineEventType.ENTITY_INITIALIZED, subProcessInstance));
        }

        if (processEngineConfiguration.isEnableEntityLinks()) {
            EntityLinkUtil.copyExistingEntityLinks(execution.getProcessInstanceId(), subProcessInstance.getId(), ScopeTypes.BPMN);
            EntityLinkUtil.createNewEntityLink(execution.getProcessInstanceId(), subProcessInstance.getId(), ScopeTypes.BPMN);
        }

        CommandContextUtil.getActivityInstanceEntityManager(commandContext).recordSubProcessInstanceStart(executionEntity, subProcessInstance);

        // Create the first execution that will visit all the process definition elements
        ExecutionEntity subProcessInitialExecution = executionEntityManager.createChildExecution(subProcessInstance);
        subProcessInitialExecution.setCurrentFlowElement(instanceBeforeContext.getInitialFlowElement());

        CommandContextUtil.getAgenda().planContinueProcessOperation(subProcessInitialExecution);

        if (eventDispatcher != null && eventDispatcher.isEnabled()) {
            eventDispatcher.dispatchEvent(FlowableEventBuilder.createProcessStartedEvent(subProcessInitialExecution, instanceBeforeContext.getVariables(), false));
        }

        if (processEngineConfiguration.getStartProcessInstanceInterceptor() != null) {
            StartSubProcessInstanceAfterContext instanceAfterContext = new StartSubProcessInstanceAfterContext(subProcessInstance, subProcessInitialExecution,
                    instanceBeforeContext.getVariables(), instanceBeforeContext.getCallActivityExecution(), instanceBeforeContext.getInParameters(),
                    instanceBeforeContext.getInitialFlowElement(), instanceBeforeContext.getProcess(), instanceBeforeContext.getProcessDefinition());

            processEngineConfiguration.getStartProcessInstanceInterceptor().afterStartSubProcessInstance(instanceAfterContext);
        }
    }

}
