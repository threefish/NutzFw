package com.nutzfw.modules.sso.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/3/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SsoUserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userName;

    private String idCard;

}
