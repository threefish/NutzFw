package com.nutzfw.modules.organize.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/3
 */
@Getter
@AllArgsConstructor
public enum LeaderTypeEnum {

    HEAD("主管领导"),
    LEADER("分管领导");

    static HashMap<String, LeaderTypeEnum> lookup = new HashMap<>();

    static {
        for (LeaderTypeEnum typeEnum : EnumSet.allOf(LeaderTypeEnum.class)) {
            lookup.put(typeEnum.toString(), typeEnum);
        }
    }

    String value;

    public static LeaderTypeEnum get(String value) {
        return lookup.get(value);
    }

}