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
