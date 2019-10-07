/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.tabledata.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/9
 * 描述此类：
 */
@Getter
@AllArgsConstructor
public enum FormValidationRulesType {
    STRING6_18(9, "邮政编码"),
    POSTAL(8, "邮政编码"),
    CHINESE(7, "中文"),
    URL(6, "网址"),
    EMAIL(5, "电子邮件"),
    PHONE(4, "手机号码"),
    LETTER(3, "字母"),
    NUMBER(2, "数字"),
    NON_EMPTY(1, "非空"),
    UNIQUE(0, "唯一效验"),
    NONE(-1, "无");

    private int    value;
    private String lable;

    public static FormValidationRulesType valueOf(int value) {
        for (FormValidationRulesType rulesType : FormValidationRulesType.values()) {
            if (value == rulesType.getValue()) {
                return rulesType;
            }
        }
        return null;
    }

}
