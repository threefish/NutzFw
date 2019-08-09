package com.nutzfw.modules.sys.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/3/14
 */
@Getter
@AllArgsConstructor
public enum FieldAuth {
    hide("不可见", "hide"),
    r("只读", "r"),
    rw("读写", "rw");
    private final String lable;
    private final String value;
}
