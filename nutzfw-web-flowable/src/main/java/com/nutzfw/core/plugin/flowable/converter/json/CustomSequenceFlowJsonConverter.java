package com.nutzfw.core.plugin.flowable.converter.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.nutzfw.core.plugin.flowable.converter.element.CustomSequenceFlow;
import com.nutzfw.core.plugin.flowable.util.FlowUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.*;
import org.flowable.editor.language.json.converter.ActivityProcessor;
import org.flowable.editor.language.json.converter.BpmnJsonConverterUtil;
import org.flowable.editor.language.json.converter.SequenceFlowJsonConverter;

import java.util.Map;
import java.util.Objects;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
public class CustomSequenceFlowJsonConverter extends SequenceFlowJsonConverter {

    private static final String CONDITION_SEQUENCEFLOW_KEY = "conditionsequenceflow";

    @Override
    public void convertToJson(BaseElement baseElement, ActivityProcessor processor, BpmnModel model, FlowElementsContainer container, ArrayNode shapesArrayNode, double subProcessX, double subProcessY) {
        CustomSequenceFlow sequenceFlow = (CustomSequenceFlow) baseElement;
        ObjectNode flowNode = BpmnJsonConverterUtil.createChildShape(sequenceFlow.getId(), "SequenceFlow", 172.0D, 212.0D, 128.0D, 212.0D);
        ArrayNode dockersArrayNode = this.objectMapper.createArrayNode();
        ObjectNode dockNode = this.objectMapper.createObjectNode();
        dockNode.put("x", model.getGraphicInfo(sequenceFlow.getSourceRef()).getWidth() / 2.0D);
        dockNode.put("y", model.getGraphicInfo(sequenceFlow.getSourceRef()).getHeight() / 2.0D);
        dockersArrayNode.add(dockNode);
        if (model.getFlowLocationGraphicInfo(sequenceFlow.getId()).size() > 2) {
            for (int i = 1; i < model.getFlowLocationGraphicInfo(sequenceFlow.getId()).size() - 1; ++i) {
                GraphicInfo graphicInfo = model.getFlowLocationGraphicInfo(sequenceFlow.getId()).get(i);
                dockNode = this.objectMapper.createObjectNode();
                dockNode.put("x", graphicInfo.getX());
                dockNode.put("y", graphicInfo.getY());
                dockersArrayNode.add(dockNode);
            }
        }

        dockNode = this.objectMapper.createObjectNode();
        dockNode.put("x", model.getGraphicInfo(sequenceFlow.getTargetRef()).getWidth() / 2.0D);
        dockNode.put("y", model.getGraphicInfo(sequenceFlow.getTargetRef()).getHeight() / 2.0D);
        dockersArrayNode.add(dockNode);
        flowNode.set("dockers", dockersArrayNode);
        ArrayNode outgoingArrayNode = this.objectMapper.createArrayNode();
        outgoingArrayNode.add(BpmnJsonConverterUtil.createResourceNode(sequenceFlow.getTargetRef()));
        flowNode.set("outgoing", outgoingArrayNode);
        flowNode.set("target", BpmnJsonConverterUtil.createResourceNode(sequenceFlow.getTargetRef()));
        ObjectNode propertiesNode = this.objectMapper.createObjectNode();
        propertiesNode.put("overrideid", sequenceFlow.getId());
        if (StringUtils.isNotEmpty(sequenceFlow.getName())) {
            propertiesNode.put("name", sequenceFlow.getName());
        }

        if (StringUtils.isNotEmpty(sequenceFlow.getDocumentation())) {
            propertiesNode.put("documentation", sequenceFlow.getDocumentation());
        }

        if (StringUtils.isNotEmpty(sequenceFlow.getConditionExpression())) {
            ObjectNode conditionsequenceflowNode = this.objectMapper.createObjectNode();
            ObjectNode expressionNode = this.objectMapper.createObjectNode();

            expressionNode.put("type", sequenceFlow.getType());
            expressionNode.put("staticValue", sequenceFlow.getConditionExpression());

            conditionsequenceflowNode.set(PROPERTY_FORM_EXPRESSION, expressionNode);
            propertiesNode.set(CONDITION_SEQUENCEFLOW_KEY, conditionsequenceflowNode);
        }

        if (StringUtils.isNotEmpty(sequenceFlow.getSourceRef())) {
            FlowElement sourceFlowElement = container.getFlowElement(sequenceFlow.getSourceRef());
            if (sourceFlowElement != null) {
                String defaultFlowId = null;
                if (sourceFlowElement instanceof ExclusiveGateway) {
                    ExclusiveGateway parentExclusiveGateway = (ExclusiveGateway) sourceFlowElement;
                    defaultFlowId = parentExclusiveGateway.getDefaultFlow();
                } else if (sourceFlowElement instanceof InclusiveGateway) {
                    InclusiveGateway parentInclusiveGateway = (InclusiveGateway) sourceFlowElement;
                    defaultFlowId = parentInclusiveGateway.getDefaultFlow();
                } else if (sourceFlowElement instanceof Activity) {
                    Activity parentActivity = (Activity) sourceFlowElement;
                    defaultFlowId = parentActivity.getDefaultFlow();
                }

                if (defaultFlowId != null && defaultFlowId.equals(sequenceFlow.getId())) {
                    propertiesNode.put("defaultflow", true);
                }
            }
        }

        this.setPropertyValue("skipexpression", sequenceFlow.getSkipExpression(), propertiesNode);
        if (sequenceFlow.getExecutionListeners().size() > 0) {
            BpmnJsonConverterUtil.convertListenersToJson(sequenceFlow.getExecutionListeners(), true, propertiesNode);
        }

        flowNode.set("properties", propertiesNode);
        shapesArrayNode.add(flowNode);

    }

    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap) {
        CustomSequenceFlow customSequenceFlow = new CustomSequenceFlow();
        final SequenceFlow flowElement = (SequenceFlow) super.convertJsonToElement(elementNode, modelNode, shapeMap);
        customSequenceFlow.setValues(flowElement);
        ObjectNode properties = (ObjectNode) elementNode.get(EDITOR_SHAPE_PROPERTIES);
        if (Objects.nonNull(properties)) {
            JsonNode jsonNode = properties.get(CONDITION_SEQUENCEFLOW_KEY);
            if (jsonNode instanceof ObjectNode) {
                ObjectNode conditionsequenceflow = (ObjectNode) jsonNode;
                ObjectNode expression = (ObjectNode) conditionsequenceflow.get(PROPERTY_FORM_EXPRESSION);
                final TextNode text = (TextNode) expression.get("type");
                if (Objects.nonNull(text)) {
                    customSequenceFlow.setType(text.asText());
                }
            } else if (jsonNode instanceof TextNode) {
                customSequenceFlow.setType("static");
            }
        }
        customSequenceFlow.addAttribute(FlowUtils.createExtensionAttribute("type", customSequenceFlow.getType()));
        return customSequenceFlow;
    }


}
