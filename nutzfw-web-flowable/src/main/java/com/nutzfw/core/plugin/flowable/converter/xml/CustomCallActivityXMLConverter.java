package com.nutzfw.core.plugin.flowable.converter.xml;

import com.nutzfw.core.plugin.flowable.converter.element.CustomCallActivity;
import org.flowable.bpmn.converter.CallActivityXMLConverter;
import org.flowable.bpmn.model.BaseElement;
import org.flowable.bpmn.model.BpmnModel;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import static org.flowable.editor.constants.StencilConstants.STENCIL_CALL_ACTIVITY;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@SuppressWarnings("all")
public class CustomCallActivityXMLConverter extends CallActivityXMLConverter {

    @Override
    public Class<? extends BaseElement> getBpmnElementType() {
        return CustomCallActivity.class;
    }

    @Override
    protected String getXMLElementName() {
        return STENCIL_CALL_ACTIVITY;
    }

    @Override
    protected BaseElement convertXMLToElement(XMLStreamReader xtr, BpmnModel model) throws Exception {
        return super.convertXMLToElement(xtr, model);
    }

    @Override
    protected void writeAdditionalAttributes(BaseElement element, BpmnModel model, XMLStreamWriter xtw) throws Exception {
        super.writeAdditionalAttributes(element, model, xtw);
    }
}
