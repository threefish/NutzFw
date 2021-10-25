package com.nutzfw.core.plugin.flowable.converter.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.flowable.bpmn.model.BaseElement;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.editor.language.json.converter.CallActivityJsonConverter;

import java.util.Map;
import java.util.Objects;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
public class CustomCallActivityJsonConverter extends CallActivityJsonConverter {

    @Override
    protected void convertElementToJson(ObjectNode propertiesNode, BaseElement baseElement) {
        super.convertElementToJson(propertiesNode, baseElement);
    }

    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap) {
        FlowElement flowElement = super.convertJsonToElement(elementNode, modelNode, shapeMap);
        ObjectNode properties = (ObjectNode) elementNode.get(EDITOR_SHAPE_PROPERTIES);
        if (Objects.nonNull(properties)) {
            JsonNode callactivitysetting = properties.get("callactivitysetting");
            if(callactivitysetting instanceof TextNode){
                String json = callactivitysetting.asText();
                //TODO 黄川
            }
        }
        return flowElement;
    }
}
