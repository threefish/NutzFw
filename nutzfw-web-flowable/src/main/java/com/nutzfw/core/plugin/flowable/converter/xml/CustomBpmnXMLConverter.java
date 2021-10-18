package com.nutzfw.core.plugin.flowable.converter.xml;

import org.flowable.bpmn.converter.BpmnXMLConverter;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@SuppressWarnings("ALL")
public class CustomBpmnXMLConverter extends BpmnXMLConverter {

    static {
        addConverter(new CustomSequenceFlowXMLConverter());
    }

}
