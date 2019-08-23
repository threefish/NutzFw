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
public enum TaskStatusEnum {

    TODO("待办"),

    CLAIM("待签收"),

    FINISH("已办");

    static HashMap<String, TaskStatusEnum> lookup = new HashMap<>();

    static {
        for (TaskStatusEnum typeEnum : EnumSet.allOf(TaskStatusEnum.class)) {
            lookup.put(typeEnum.toString(), typeEnum);
        }
    }

    String value;

    public static TaskStatusEnum get(String value) {
        return lookup.get(value);
    }


}
