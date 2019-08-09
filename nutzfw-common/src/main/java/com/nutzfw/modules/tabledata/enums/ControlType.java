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
