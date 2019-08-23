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
public enum TaskFormStatusEnum {

    EDIT("编辑"),

    VIEW("查看"),

    AUDIT("审核");

    static HashMap<String, TaskFormStatusEnum> lookup = new HashMap<>();

    static {
        for (TaskFormStatusEnum typeEnum : EnumSet.allOf(TaskFormStatusEnum.class)) {
            lookup.put(typeEnum.toString(), typeEnum);
        }
    }

    String value;

    public static TaskFormStatusEnum get(String value) {
        return lookup.get(value);
    }


}
