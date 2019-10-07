/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
