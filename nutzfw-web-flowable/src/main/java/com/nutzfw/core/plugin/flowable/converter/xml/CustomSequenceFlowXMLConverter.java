package com.nutzfw.core.plugin.flowable.converter.xml;

import com.nutzfw.core.plugin.flowable.converter.element.CustomSequenceFlow;
import com.nutzfw.core.plugin.flowable.util.FlowUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.converter.SequenceFlowXMLConverter;
import org.flowable.bpmn.converter.util.BpmnXMLUtil;
import org.flowable.bpmn.model.BaseElement;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.SequenceFlow;
import org.nutz.lang.Strings;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


/**
 * @author 黄川 huchuc@vip.qq.com
 */
@SuppressWarnings("ALL")
public class CustomSequenceFlowXMLConverter extends SequenceFlowXMLConverter {

    private static final String TYPE = "type";

    @Override
    public Class<? extends BaseElement> getBpmnElementType() {
        return CustomSequenceFlow.class;
    }

    @Override
    protected String getXMLElementName() {
        return ELEMENT_SEQUENCE_FLOW;
    }

    @Override
    protected BaseElement convertXMLToElement(XMLStreamReader xtr, BpmnModel model) throws Exception {
        CustomSequenceFlow sequenceFlow = new CustomSequenceFlow();
        BpmnXMLUtil.addXMLLocation(sequenceFlow, xtr);
        sequenceFlow.setSourceRef(xtr.getAttributeValue(null, "sourceRef"));
        sequenceFlow.setTargetRef(xtr.getAttributeValue(null, "targetRef"));
        sequenceFlow.setName(xtr.getAttributeValue(null, "name"));
        sequenceFlow.setSkipExpression(xtr.getAttributeValue(null, "skipExpression"));
        sequenceFlow.setType(xtr.getAttributeValue(null, TYPE));
        this.parseChildElements(this.getXMLElementName(), sequenceFlow, model, xtr);
        return sequenceFlow;
    }

    @Override
    protected void writeAdditionalAttributes(BaseElement element, BpmnModel model, XMLStreamWriter xtw) throws Exception {
        CustomSequenceFlow sequenceFlow = (CustomSequenceFlow) element;
        writeDefaultAttribute(ATTRIBUTE_FLOW_SOURCE_REF, sequenceFlow.getSourceRef(), xtw);
        writeDefaultAttribute(ATTRIBUTE_FLOW_TARGET_REF, sequenceFlow.getTargetRef(), xtw);
        writeDefaultAttribute(TYPE, sequenceFlow.getType(), xtw);
        if (StringUtils.isNotEmpty(sequenceFlow.getSkipExpression())) {
            writeDefaultAttribute(ATTRIBUTE_FLOW_SKIP_EXPRESSION, sequenceFlow.getSkipExpression(), xtw);
        }
    }


}
