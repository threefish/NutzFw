/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nutzfw.core.plugin.flowable.util.FlowUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BaseElement;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.editor.language.json.converter.BaseBpmnJsonConverter;
import org.flowable.editor.language.json.converter.UserTaskJsonConverter;
import org.nutz.lang.Strings;

import java.io.IOException;
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
    public static final String EXPANSION_PROPERTIES = "expansionProperties";
    public static final String USER_TASK_EXTENSION_ELEMENT_NAME = "user-task-expansion";

    public static void customFillTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap, Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        fillJsonTypes(convertersToBpmnMap);
        fillBpmnTypes(convertersToJsonMap);
    }

    public static void fillJsonTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap) {
        convertersToBpmnMap.put(STENCIL_TASK_USER, CustomUserTaskJsonConverter.class);
    }

    public static void fillBpmnTypes(Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        convertersToJsonMap.put(UserTask.class, CustomUserTaskJsonConverter.class);
    }

    @Override
    protected void convertElementToJson(ObjectNode propertiesNode, BaseElement baseElement) {
        super.convertElementToJson(propertiesNode, baseElement);
        if (baseElement instanceof UserTask) {
            ObjectNode usertaskexpansionNode = objectMapper.createObjectNode();
            baseElement.getExtensionElements().forEach((s, elements) -> elements.forEach(extensionElement -> {
                if (extensionElement.getName().equals(USER_TASK_EXTENSION_ELEMENT_NAME)) {
                    JsonNode expansionPropertiesNode = convertExpansionPropertiesElementToJson(extensionElement);
                    if (expansionPropertiesNode.size() > 0) {
                        usertaskexpansionNode.set(EXPANSION_PROPERTIES, expansionPropertiesNode);
                    }
                }
            }));
            propertiesNode.set(USER_TASK_EXPANSION, usertaskexpansionNode);
        }
    }

    /**
     * 导入xml时会用到
     *
     * @param extensionElement
     * @return
     */
    private JsonNode convertExpansionPropertiesElementToJson(ExtensionElement extensionElement) {
        String jsonText = extensionElement.getElementText();
        if (Strings.isNotBlank(jsonText)) {
            try {
                return objectMapper.readTree(jsonText);
            } catch (IOException e) {
                log.error("json序列化失败", e);
                throw new RuntimeException("json序列化失败");
            }
        }
        return objectMapper.createObjectNode();
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
            if (userTaskExpansion != null && userTaskExpansion instanceof ObjectNode) {
                addExpansionPropertiesElement((UserTask) element, userTaskExpansion);
            }
        }
    }


    /**
     * 添加扩展属性
     *
     * @param userTask
     * @param userTaskExpansion
     */
    private void addExpansionPropertiesElement(UserTask userTask, JsonNode userTaskExpansion) {
        JsonNode jsonNode = userTaskExpansion.get(EXPANSION_PROPERTIES);
        if (jsonNode != null && jsonNode instanceof ObjectNode) {
            ExtensionElement element = FlowUtils.buildExtensionElement(CustomUserTaskJsonConverter.USER_TASK_EXTENSION_ELEMENT_NAME, jsonNode.toString());
            userTask.addExtensionElement(element);
        }
    }

}
