package com.nutzfw.core.plugin.flowable.converter.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.bpmn.model.BaseElement;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.editor.language.json.converter.CallActivityJsonConverter;

import java.util.Map;

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
        return super.convertJsonToElement(elementNode, modelNode, shapeMap);
    }
}
