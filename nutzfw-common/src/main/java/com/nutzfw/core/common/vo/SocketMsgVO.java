/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/3/1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocketMsgVO<T> {
    /**
     * 请求方法名
     */
    private String action;

    /**
     * 是否成功
     */
    private Boolean ok;

    /**
     * 编码
     */
    private String charSet;

    /**
     * 返回的数据内容
     */
    private T data;
}
