/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
