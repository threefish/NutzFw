package com.nutzfw.core.plugin.flowable.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/26
 */
@Getter
@AllArgsConstructor
public enum CallBackTypeEnum {

    NONE("不允许回退"),

    PREVIOUS_STEP("回退至上一步"),

    FREE_STEP("自由选择回退节点");

    static HashMap<String, CallBackTypeEnum> lookup = new HashMap<>();

    static {
        for (CallBackTypeEnum typeEnum : EnumSet.allOf(CallBackTypeEnum.class)) {
            lookup.put(typeEnum.toString(), typeEnum);
        }
    }

    String value;

    public static CallBackTypeEnum get(String value) {
        return lookup.get(value);
    }


}
