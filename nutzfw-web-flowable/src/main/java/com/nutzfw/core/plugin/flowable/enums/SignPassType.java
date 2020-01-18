/*
 * Copyright (c) 2019- 2020 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2020/01/03 21:01:03
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/1/18
 */
@Getter
@AllArgsConstructor
public enum SignPassType {
    /**
     * 比例通过制
     */
    SCALE("比例通过制"),
    /**
     * 一票通过制
     */
    ONE_VOTE_ADOPT("一票通过制"),
    /**
     * 一票否决制
     */
    ONE_VOTE_VETO("一票否决制");
    /**
     * 描述信息
     */
    String value;
}
