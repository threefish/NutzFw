package com.nutzfw.core.plugin.flowable.converter.element;

import org.flowable.bpmn.model.SequenceFlow;
import org.nutz.lang.Strings;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
public class CustomSequenceFlow extends SequenceFlow {


    protected String type;

    public static CustomSequenceFlow of(SequenceFlow sequenceFlow) {
        final CustomSequenceFlow customSequenceFlow = new CustomSequenceFlow();
        customSequenceFlow.setValues(sequenceFlow);
        return customSequenceFlow;
    }

    public String getType() {
        return Strings.isBlank(type) ? "static" : type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public CustomSequenceFlow clone() {
        CustomSequenceFlow clone = new CustomSequenceFlow();
        clone.setValues(this);
        return clone;
    }

    @Override
    public void setValues(SequenceFlow otherFlow) {
        super.setValues(otherFlow);
        setConditionExpression(otherFlow.getConditionExpression());
        setSourceRef(otherFlow.getSourceRef());
        setTargetRef(otherFlow.getTargetRef());
        setSkipExpression(otherFlow.getSkipExpression());
        if (otherFlow instanceof CustomSequenceFlow) {
            setType(((CustomSequenceFlow) otherFlow).getType());
        }
    }
}
