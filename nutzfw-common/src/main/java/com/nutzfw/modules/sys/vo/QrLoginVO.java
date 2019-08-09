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
    String userName;
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
