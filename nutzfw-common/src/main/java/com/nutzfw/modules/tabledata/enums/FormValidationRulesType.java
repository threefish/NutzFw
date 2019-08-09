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

    private int value;
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
