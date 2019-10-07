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
public enum ControlType {

    //字符串
    Input(0),
    //下拉框（暂时好像用不到）
    Select(1),
    //多行文本框
    Textarea(2),
    //大文本
    UEContent(3),
    //密码框
    PassWord(4),
    // 日期(yyyy-MM-dd)
    Date(5),
    //日期(yyyy-MM-dd HH:mm:ss)
    DateTime(6),
    //附件
    Attach(7),
    //图片
    Img(8);

    private final int value;

    ControlType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

}
