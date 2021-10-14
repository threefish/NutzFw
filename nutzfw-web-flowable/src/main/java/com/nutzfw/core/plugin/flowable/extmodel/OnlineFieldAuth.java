package com.nutzfw.core.plugin.flowable.extmodel;

import com.nutzfw.modules.sys.enums.FieldAuth;
import lombok.Data;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2021/10/14
 */
@Data
public class OnlineFieldAuth {
    String field;
    FieldAuth auth;
}
