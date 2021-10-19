package com.nutzfw.core.plugin.flowable.cmd;

import com.nutzfw.core.plugin.flowable.converter.json.CustomUserTaskJsonConverter;
import com.nutzfw.core.plugin.flowable.extmodel.FormElementModel;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

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
        FormElementModel proccessFormHandler = this.getProccessFormHandler();
        if (Strings.isBlank(taskDefinitionKey)) {
            return proccessFormHandler;
        }
        // 取当前节点的表单定义配置
        final FormElementModel taskFormHandlder = this.getTaskFormHandlder();
        if (taskFormHandlder == null) {
            // 兼容一下取流程定义中的表单配置
            return proccessFormHandler;
        }
        if (Strings.isBlank(taskFormHandlder.getTableId())) {
            taskFormHandlder.setTableId(proccessFormHandler.getTableId());
        }
        if (Strings.isBlank(taskFormHandlder.getFormKey())) {
            taskFormHandlder.setFormKey(proccessFormHandler.getFormKey());
        }
        if (taskFormHandlder == null) {
            taskFormHandlder.setFormType(proccessFormHandler.getFormType());
        }
        if (taskFormHandlder.getFieldAuths() == null || taskFormHandlder.getFieldAuths().isEmpty()) {
            taskFormHandlder.setFieldAuths(proccessFormHandler.getFieldAuths());
        }
        taskFormHandlder.setWriteBackProccessStatusField(proccessFormHandler.getWriteBackProccessStatusField());
        return taskFormHandlder;
    }


    public FormElementModel getProccessFormHandler() {
        org.flowable.bpmn.model.Process process = ProcessDefinitionUtil.getProcess(processDefinitionId);
        List<ExtensionElement> extensionElements = process.getExtensionElements().get(CustomUserTaskJsonConverter.FORM_KEY_DEFINITION);
        if (Lang.isNotEmpty(extensionElements)) {
            ExtensionElement extensionElement = extensionElements.get(0);
            String elementText = extensionElement.getElementText();
            return Json.fromJson(FormElementModel.class, elementText);
        }
        return null;

    }

    public FormElementModel getTaskFormHandlder() {
        org.flowable.bpmn.model.Process process = ProcessDefinitionUtil.getProcess(processDefinitionId);
        FlowElement flowElement = process.getFlowElement(taskDefinitionKey, true);
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
