/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/6/15
 */
@Getter
@AllArgsConstructor
public enum DictType {
    select(0),
    radio(1),
    checkbox(2),
    tree(3);
    private final int value;

    public static DictType valueOf(int value) {
        for (DictType rulesType : DictType.values()) {
            if (value == rulesType.getValue()) {
                return rulesType;
            }
        }
        return null;
    }
}
