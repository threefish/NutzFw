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
