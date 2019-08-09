package com.nutzfw.modules.tabledata.enums;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/7/31
 * 描述此类：
 */
public enum DictDepend {
    //依赖名称
    NONE(0),
    //依赖名称
    Name(1),
    //依赖键值
    keyValue(2),
    //依赖附加值1-10
    extra1(3),
    extra2(4),
    extra3(5),
    extra4(6),
    extra5(7),
    extra6(8),
    extra7(9),
    extra8(10),
    extra9(11),
    extra10(12);

    private final int value;

    DictDepend(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }


}
