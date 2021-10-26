package com.nutzfw.core.plugin.flowable.converter.element;

import org.flowable.bpmn.model.CallActivity;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
public class CustomCallActivity extends CallActivity {


    public static CustomCallActivity of(CallActivity callActivity) {
        final CustomCallActivity customCallActivity = new CustomCallActivity();
        customCallActivity.setValues(callActivity);
        return customCallActivity;
    }


    @Override
    public void setValues(CallActivity otherFlow) {
        super.setValues(otherFlow);
        if (otherFlow instanceof CustomCallActivity) {

        }
    }

}
