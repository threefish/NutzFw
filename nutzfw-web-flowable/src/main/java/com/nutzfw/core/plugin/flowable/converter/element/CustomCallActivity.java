package com.nutzfw.core.plugin.flowable.converter.element;

import com.nutzfw.core.plugin.flowable.converter.json.CustomCallActivityJsonConverter;
import com.nutzfw.core.plugin.flowable.dto.ChildFlowExtensionDTO;
import lombok.Getter;
import org.flowable.bpmn.model.CallActivity;
import org.nutz.json.Json;

/**
 * @author 黄川 huchuc@vip.qq.com
 */

public class CustomCallActivity extends CallActivity {

    @Getter
    private ChildFlowExtensionDTO extension;

    public static CustomCallActivity of(CallActivity callActivity) {
        final CustomCallActivity customCallActivity = new CustomCallActivity();
        customCallActivity.setValues(callActivity);
        return customCallActivity;
    }

    @Override
    public void setValues(CallActivity otherFlow) {
        super.setValues(otherFlow);
        otherFlow.getExtensionElements().values().stream().forEach(list -> list.forEach(extensionElement -> {
            if (extensionElement.getName().equals(CustomCallActivityJsonConverter.CALLACTIVITY_SETTING)) {
                String elementText = extensionElement.getElementText();
                extension = Json.fromJson(ChildFlowExtensionDTO.class, elementText);
            }
        }));
    }

}
