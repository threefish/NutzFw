package com.nutzfw.core.plugin.flowable.context;

import lombok.Data;
import org.flowable.bpmn.model.CallActivity;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.IOParameter;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.interceptor.StartSubProcessInstanceBeforeContext;
import org.flowable.engine.repository.ProcessDefinition;

import java.util.List;
import java.util.Map;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@Data
public class CustomStartSubProcessInstanceBeforeContext extends StartSubProcessInstanceBeforeContext {

    CallActivity callActivity;

    public CustomStartSubProcessInstanceBeforeContext(String businessKey, String processInstanceName, Map<String, Object> variables, ExecutionEntity callActivityExecution, List<IOParameter> inParameters, boolean inheritVariables, String initialActivityId, FlowElement initialFlowElement, Process process, ProcessDefinition processDefinition) {
        super(businessKey, processInstanceName, variables, callActivityExecution, inParameters, inheritVariables, initialActivityId, initialFlowElement, process, processDefinition);
    }

}
