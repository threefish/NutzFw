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
