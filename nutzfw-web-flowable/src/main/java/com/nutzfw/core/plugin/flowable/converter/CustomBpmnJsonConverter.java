package com.nutzfw.core.plugin.flowable.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.Process;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.editor.language.json.model.ModelInfo;

import java.util.List;
import java.util.Map;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/28
 */
public class CustomBpmnJsonConverter extends BpmnJsonConverter {

    public static final String EXTERNAL_FORM_EXECUTOR = "externalformexecutor";

    private static final String[] PROPERTY_KEYS = new String[]{EXTERNAL_FORM_EXECUTOR};

    static {
        convertersToBpmnMap.put(STENCIL_TASK_USER, CustomUserTaskJsonConverter.class);
        CustomUserTaskJsonConverter.customFillTypes(convertersToBpmnMap, convertersToJsonMap);
    }

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
        return this.handleConvertExpansionToJson(model, objectNode);
    }

    private ObjectNode handleConvertExpansionToJson(BpmnModel bpmnModel, ObjectNode modelNode) {
        ObjectNode propertiesNode = (ObjectNode) modelNode.get("properties");
        Process process = bpmnModel.getMainProcess();
        for (String propertyKey : PROPERTY_KEYS) {
            if (process.getExtensionElements().containsKey(propertyKey)) {
                List<ExtensionElement> extensionElements = process.getExtensionElements().get(propertyKey);
                for (ExtensionElement extensionElement : extensionElements) {
                    if (propertyKey.equals(extensionElement.getName())) {
                        propertiesNode.put(propertyKey, extensionElement.getElementText());
                    }
                }
            }
        }
        return modelNode;
    }

    public BpmnModel handleConvertExpansionToBpmnModel(BpmnModel bpmnModel, JsonNode modelNode) {
        JsonNode properties = modelNode.get("properties");
        Process process = bpmnModel.getMainProcess();
        for (String propertyKey : PROPERTY_KEYS) {
            if (!process.getExtensionElements().containsKey(propertyKey)) {
                process.addExtensionElement(buildExtensionElement(propertyKey, properties.get(propertyKey).asText()));
            }
        }
        return bpmnModel;
    }
}
