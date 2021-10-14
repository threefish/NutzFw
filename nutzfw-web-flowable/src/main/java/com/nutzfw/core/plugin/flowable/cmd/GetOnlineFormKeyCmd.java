package com.nutzfw.core.plugin.flowable.cmd;

import com.nutzfw.core.plugin.flowable.converter.CustomUserTaskJsonConverter;
import com.nutzfw.core.plugin.flowable.extmodel.FormElementModel;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.engine.repository.ProcessDefinition;
import org.nutz.json.Json;
import org.nutz.lang.Lang;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2021/10/14
 */
public class GetOnlineFormKeyCmd implements Command<FormElementModel> {
    protected String taskDefinitionKey;
    protected String processDefinitionId;

    /**
     * Retrieves a start form key.
     */
    public GetOnlineFormKeyCmd(String processDefinitionId) {
        setProcessDefinitionId(processDefinitionId);
    }

    /**
     * Retrieves a task form key.
     */
    public GetOnlineFormKeyCmd(String processDefinitionId, String taskDefinitionKey) {
        setProcessDefinitionId(processDefinitionId);
        this.taskDefinitionKey = taskDefinitionKey;
    }

    protected void setProcessDefinitionId(String processDefinitionId) {
        if (processDefinitionId == null || processDefinitionId.length() < 1) {
            throw new FlowableIllegalArgumentException("The process definition id is mandatory, but '" + processDefinitionId + "' has been provided.");
        }
        this.processDefinitionId = processDefinitionId;
    }

    @Override
    public FormElementModel execute(CommandContext commandContext) {
        ProcessDefinition processDefinition = ProcessDefinitionUtil.getProcessDefinition(processDefinitionId);
        FormElementModel formElementModel = null;
        if (taskDefinitionKey == null) {
            formElementModel = this.getStartFormHandler(commandContext, processDefinition);
        } else {
            formElementModel = this.getTaskFormHandlder(processDefinitionId, taskDefinitionKey);
        }
        return formElementModel;
    }


    public FormElementModel getStartFormHandler(CommandContext commandContext, ProcessDefinition processDefinition) {
        org.flowable.bpmn.model.Process process = ProcessDefinitionUtil.getProcess(processDefinition.getId());
        List<ExtensionElement> extensionElements = process.getExtensionElements().get(CustomUserTaskJsonConverter.FORM_KEY_DEFINITION);
        if (Lang.isNotEmpty(extensionElements)) {
            ExtensionElement extensionElement = extensionElements.get(0);
            String elementText = extensionElement.getElementText();
            return Json.fromJson(FormElementModel.class, elementText);
        }
        return null;

    }

    public FormElementModel getTaskFormHandlder(String processDefinitionId, String taskId) {
        org.flowable.bpmn.model.Process process = ProcessDefinitionUtil.getProcess(processDefinitionId);
        FlowElement flowElement = process.getFlowElement(taskId, true);
        if (flowElement instanceof UserTask) {
            UserTask userTask = (UserTask) flowElement;
            List<ExtensionElement> extensionElements = userTask.getExtensionElements().get(CustomUserTaskJsonConverter.FORM_KEY_DEFINITION);
            if (Lang.isNotEmpty(extensionElements)) {
                ExtensionElement extensionElement = extensionElements.get(0);
                String elementText = extensionElement.getElementText();
                return Json.fromJson(FormElementModel.class, elementText);
            }
        }
        return null;
    }


}
