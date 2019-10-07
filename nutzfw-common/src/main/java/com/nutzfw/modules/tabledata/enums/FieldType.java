/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.tabledata.enums;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/5
 * 描述此类：
 */
public enum FieldType {

    //字符串
    String(0),
    //数值型
    Decimal(1),
    //日期
    Date(2),
    //大文本
    Text(3),
    //单附件 varchar 26
    SingleAttach(4),
    //多附件(20个) varchar 550
    MultiAttach(5);

    private final int value;

    FieldType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

}
