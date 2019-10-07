/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/7/19
 * 描述此类：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QrLoginVO {
    /**
     * 用户名
     */
    String  userName;
    /**
     * 是否已扫码
     */
    Boolean scanning;

    /**
     * 是否确认登录
     */
    Boolean confirmLogin;

    public static QrLoginVO create() {
        return new QrLoginVO(null, false, false);
    }
}
