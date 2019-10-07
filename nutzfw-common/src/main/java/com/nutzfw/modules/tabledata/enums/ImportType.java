/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.tabledata.enums;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/19
 * 描述此类：
 */
public enum ImportType {

    //导入全部记录
    ALL(1),
    //记录存在则更新，不存在则导入
    existsUpdate_ImportNotExists(2),
    //记录存在则更新，不存在则忽略
    existsUpdate_IgnoreNotExists(3),
    //记录存在则忽略，不存在则导入
    iGnoreExists_ImportNotExists(4);

    private final int value;

    ImportType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }


}
