/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.nutzfw.core.plugin.flowable.util.FlowUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.ValuedDataObject;
import org.flowable.common.engine.impl.util.CollectionUtil;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.editor.language.json.model.ModelInfo;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.nutzfw.core.plugin.flowable.converter.CustomUserTaskJsonConverter.FORM_KEY_DEFINITION;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/28
 */
public class CustomBpmnJsonConverter extends BpmnJsonConverter {

    public static final String EXTERNAL_FORM_EXECUTOR = "externalformexecutor";
    public static final String DATA_OBJECTS_EXPRESSION = "expression";
    private static final String[] PROPERTY_KEYS = new String[]{EXTERNAL_FORM_EXECUTOR, FORM_KEY_DEFINITION};

    static {
        convertersToBpmnMap.put(STENCIL_TASK_USER, CustomUserTaskJsonConverter.class);
        CustomUserTaskJsonConverter.customFillTypes(convertersToBpmnMap, convertersToJsonMap);
    }

    ObjectMapper objectMapper = new ObjectMapper();

    public static ExtensionElement buildExtensionElement(String name, String value) {
        ExtensionElement extensionElement = new ExtensionElement();
        extensionElement.setNamespacePrefix("nutzfw");
        extensionElement.setNamespace(BpmnJsonConverter.MODELER_NAMESPACE);
        extensionElement.setName(name);
        extensionElement.setElementText(value);
        return extensionElement;
    }

    @Override
    public BpmnModel convertToBpmnModel(JsonNode modelNode) {
        BpmnModel bpmnModel = super.convertToBpmnModel(modelNode);
        return this.handleConvertExpansionToBpmnModel(bpmnModel, modelNode);
    }

    @Override
    public BpmnModel convertToBpmnModel(JsonNode modelNode, Map<String, String> formKeyMap, Map<String, String> decisionTableKeyMap) {
        BpmnModel bpmnModel = super.convertToBpmnModel(modelNode, formKeyMap, decisionTableKeyMap);
        bpmnModel = this.handleConvertDataObjectsToBpmnModel(bpmnModel, modelNode);
        return this.handleConvertExpansionToBpmnModel(bpmnModel, modelNode);
    }

    @Override
    public ObjectNode convertToJson(BpmnModel model) {
        ObjectNode objectNode = super.convertToJson(model);
        return this.handleConvertExpansionToJson(model, objectNode);
    }

    @Override
    public ObjectNode convertToJson(BpmnModel model, Map<String, ModelInfo> formKeyMap, Map<String, ModelInfo> decisionTableKeyMap) {
        ObjectNode objectNode = super.convertToJson(model, formKeyMap, decisionTableKeyMap);
        objectNode = this.handleConvertDataObjectsToJson(model.getMainProcess(), objectNode);
        return this.handleConvertExpansionToJson(model, objectNode);
    }

    private ObjectNode handleConvertExpansionToJson(BpmnModel bpmnModel, ObjectNode modelNode) {
        ObjectNode propertiesNode = (ObjectNode) modelNode.get(EDITOR_SHAPE_PROPERTIES);
        Process process = bpmnModel.getMainProcess();
        for (String propertyKey : PROPERTY_KEYS) {
            if (process.getExtensionElements().containsKey(propertyKey)) {
                List<ExtensionElement> extensionElements = process.getExtensionElements().get(propertyKey);
                for (ExtensionElement extensionElement : extensionElements) {
                    if (propertyKey.equals(extensionElement.getName())) {
                        if (FORM_KEY_DEFINITION.equals(extensionElement.getName())) {
                            propertiesNode.set(propertyKey, FlowUtils.convertPropertiesElementToJson(extensionElement));
                        } else {
                            propertiesNode.put(propertyKey, extensionElement.getElementText());
                        }
                    }
                }
            }
        }
        return modelNode;
    }

    public BpmnModel handleConvertExpansionToBpmnModel(BpmnModel bpmnModel, JsonNode modelNode) {
        JsonNode properties = modelNode.get(EDITOR_SHAPE_PROPERTIES);
        Process process = bpmnModel.getMainProcess();
        for (String propertyKey : PROPERTY_KEYS) {
            if (!process.getExtensionElements().containsKey(propertyKey)) {
                JsonNode jsonNode = properties.get(propertyKey);
                if (Objects.nonNull(properties.get(propertyKey))) {
                    if (jsonNode instanceof TextNode) {
                        process.addExtensionElement(buildExtensionElement(propertyKey, jsonNode.asText()));
                    } else {
                        process.addExtensionElement(buildExtensionElement(propertyKey, jsonNode.toString()));
                    }
                }
            }
        }
        return bpmnModel;
    }

    /**
     * 自定义数据对象表达式----json转xml
     *
     * @param bpmnModel
     * @param modelNode
     * @return
     */
    public BpmnModel handleConvertDataObjectsToBpmnModel(BpmnModel bpmnModel, JsonNode modelNode) {
        JsonNode jsonNode = modelNode.get(EDITOR_SHAPE_PROPERTIES).get(PROPERTY_DATA_PROPERTIES);
        JsonNode itemsArrayNode = modelNode.get(EDITOR_SHAPE_PROPERTIES).get(PROPERTY_DATA_PROPERTIES).get(EDITOR_PROPERTIES_GENERAL_ITEMS);
        if (itemsArrayNode == null && jsonNode instanceof TextNode) {
            try {
                itemsArrayNode = objectMapper.readTree(jsonNode.asText()).get(EDITOR_PROPERTIES_GENERAL_ITEMS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Process process = bpmnModel.getMainProcess();
        List<ValuedDataObject> dataObjects = process.getDataObjects();
        if (itemsArrayNode != null) {
            for (JsonNode dataNode : itemsArrayNode) {
                JsonNode dataIdNode = dataNode.get(PROPERTY_DATA_ID);
                if (dataIdNode != null && StringUtils.isNotEmpty(dataIdNode.asText())) {
                    String nodeId = dataIdNode.asText();
                    dataObjects.stream().filter(valuedDataObject -> valuedDataObject.getId().equals(nodeId)).forEach(valuedDataObject -> {
                        JsonNode expressionNode = dataNode.get("dataproperty_expression");
                        if (expressionNode != null && StringUtils.isNotEmpty(expressionNode.asText())) {
                            valuedDataObject.addExtensionElement(FlowUtils.buildExtensionElement(DATA_OBJECTS_EXPRESSION, expressionNode.asText()));
                        }
                    });
                }
            }
        }
        return bpmnModel;
    }

    /**
     * 自定义数据对象表达式----xml转json
     *
     * @param process
     * @param modelNode
     * @return
     */
    public ObjectNode handleConvertDataObjectsToJson(Process process, ObjectNode modelNode) {
        JsonNode itemsArrayNode = modelNode.get(EDITOR_SHAPE_PROPERTIES).get(PROPERTY_DATA_PROPERTIES).get(EDITOR_PROPERTIES_GENERAL_ITEMS);
        if (itemsArrayNode != null) {
            List<ValuedDataObject> dataObjects = process.getDataObjects();
            for (JsonNode dataNode : itemsArrayNode) {
                if (dataNode instanceof ObjectNode) {
                    ObjectNode objectNode = (ObjectNode) dataNode;
                    JsonNode dataIdNode = dataNode.get(PROPERTY_DATA_ID);
                    if (dataIdNode != null && StringUtils.isNotEmpty(dataIdNode.asText())) {
                        String nodeId = dataIdNode.asText();
                        dataObjects.stream().filter(valuedDataObject -> valuedDataObject.getId().equals(nodeId)).forEach(valuedDataObject -> {
                            List<ExtensionElement> extensionElements = valuedDataObject.getExtensionElements().get(DATA_OBJECTS_EXPRESSION);
                            if (CollectionUtil.isNotEmpty(extensionElements)) {
                                extensionElements.forEach(element -> objectNode.put("dataproperty_expression", element.getElementText()));
                            }
                        });
                    }
                }
            }
        }
        return modelNode;
    }
}
