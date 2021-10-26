package com.nutzfw.core.plugin.flowable.converter.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nutzfw.core.plugin.flowable.converter.element.CustomCallActivity;
import com.nutzfw.core.plugin.flowable.util.FlowUtils;
import org.flowable.bpmn.model.BaseElement;
import org.flowable.bpmn.model.CallActivity;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.editor.language.json.converter.CallActivityJsonConverter;

import java.util.Map;
import java.util.Objects;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
public class CustomCallActivityJsonConverter extends CallActivityJsonConverter {

    public static final String CALLACTIVITY_SETTING = "callactivitysetting";

    @Override
    protected void convertElementToJson(ObjectNode propertiesNode, BaseElement baseElement) {
        super.convertElementToJson(propertiesNode, baseElement);
        baseElement.getExtensionElements().forEach((s, elements) -> elements.forEach(extensionElement -> {
            if (extensionElement.getName().equals(CALLACTIVITY_SETTING)) {
                JsonNode expansionPropertiesNode = FlowUtils.convertPropertiesElementToJson(extensionElement);
                if (expansionPropertiesNode.size() > 0) {
                    propertiesNode.set(CALLACTIVITY_SETTING, expansionPropertiesNode);
                }
            }
        }));

    }

    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap) {
        FlowElement flowElement = super.convertJsonToElement(elementNode, modelNode, shapeMap);
        ObjectNode properties = (ObjectNode) elementNode.get(EDITOR_SHAPE_PROPERTIES);
        if (Objects.nonNull(properties)) {
            JsonNode callactivitysetting = properties.get(CALLACTIVITY_SETTING);
            if (callactivitysetting instanceof ObjectNode) {
                ExtensionElement element = FlowUtils.buildExtensionElement(CALLACTIVITY_SETTING, callactivitysetting.toString());
                flowElement.addExtensionElement(element);
            }
        }
        CustomCallActivity customCallActivity = CustomCallActivity.of((CallActivity) flowElement);
        return flowElement;
    }
}
