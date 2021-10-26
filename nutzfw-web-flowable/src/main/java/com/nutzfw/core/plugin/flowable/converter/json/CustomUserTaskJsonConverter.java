/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.converter.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nutzfw.core.plugin.flowable.util.FlowUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BaseElement;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.editor.language.json.converter.UserTaskJsonConverter;

import java.util.Map;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/24
 */
@Slf4j
public class CustomUserTaskJsonConverter extends UserTaskJsonConverter {

    /**
     * 扩展属性设置
     */
    public static final String USER_TASK_EXPANSION = "usertaskexpansion";
    public static final String FORM_KEY_DEFINITION = "formkeydefinition";
    public static final String EXPANSION_PROPERTIES = "expansionProperties";
    public static final String USER_TASK_EXTENSION_ELEMENT_NAME = "user-task-expansion";


    @Override
    protected void convertElementToJson(ObjectNode propertiesNode, BaseElement baseElement) {
        super.convertElementToJson(propertiesNode, baseElement);
        if (baseElement instanceof UserTask) {
            ObjectNode usertaskexpansionNode = objectMapper.createObjectNode();
            baseElement.getExtensionElements().forEach((s, elements) -> elements.forEach(extensionElement -> {
                if (extensionElement.getName().equals(USER_TASK_EXTENSION_ELEMENT_NAME)) {
                    JsonNode expansionPropertiesNode = FlowUtils.convertPropertiesElementToJson(extensionElement);
                    if (expansionPropertiesNode.size() > 0) {
                        usertaskexpansionNode.set(EXPANSION_PROPERTIES, expansionPropertiesNode);
                    }
                }
                if (extensionElement.getName().equals(FORM_KEY_DEFINITION)) {
                    JsonNode expansionPropertiesNode = FlowUtils.convertPropertiesElementToJson(extensionElement);
                    if (expansionPropertiesNode.size() > 0) {
                        propertiesNode.set(FORM_KEY_DEFINITION, expansionPropertiesNode);
                    }
                }
            }));
            propertiesNode.set(USER_TASK_EXPANSION, usertaskexpansionNode);
        }
    }


    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap) {
        return super.convertJsonToElement(elementNode, modelNode, shapeMap);
    }

    @Override
    protected void convertJsonToFormProperties(JsonNode objectNode, BaseElement element) {
        super.convertJsonToFormProperties(objectNode, element);
        if (element instanceof UserTask) {
            JsonNode userTaskExpansion = getProperty(USER_TASK_EXPANSION, objectNode);
            JsonNode formkeydefinition = getProperty(FORM_KEY_DEFINITION, objectNode);
            addExpansionPropertiesElement((UserTask) element, userTaskExpansion);
            addFormPropertiesElement((UserTask) element, formkeydefinition);
        }
    }


    /**
     * 添加扩展属性
     *
     * @param flowElement
     * @param userTaskExpansion
     */
    private void addExpansionPropertiesElement(FlowElement flowElement, JsonNode userTaskExpansion) {
        if (userTaskExpansion != null && userTaskExpansion instanceof ObjectNode) {
            JsonNode jsonNode = userTaskExpansion.get(EXPANSION_PROPERTIES);
            if (jsonNode != null && jsonNode instanceof ObjectNode) {
                ExtensionElement element = FlowUtils.buildExtensionElement(USER_TASK_EXTENSION_ELEMENT_NAME, jsonNode.toString());
                flowElement.addExtensionElement(element);
            }
        }

    }

    /**
     * 添加扩展属性
     *
     * @param userTask
     */
    private void addFormPropertiesElement(UserTask userTask, JsonNode formkeydefinition) {
        if (formkeydefinition != null && formkeydefinition instanceof ObjectNode) {
            if ("DEVELOP".equals(formkeydefinition.get("formType").asText())) {
                userTask.setFormKey(formkeydefinition.get("formKey").asText());
            }
            ExtensionElement element = FlowUtils.buildExtensionElement(FORM_KEY_DEFINITION, formkeydefinition.toString());
            userTask.addExtensionElement(element);
        }
    }

}
