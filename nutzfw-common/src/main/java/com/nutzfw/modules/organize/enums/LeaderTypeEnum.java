/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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