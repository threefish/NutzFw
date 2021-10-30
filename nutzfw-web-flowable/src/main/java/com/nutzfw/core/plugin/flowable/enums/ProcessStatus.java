package com.nutzfw.core.plugin.flowable.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2021/10/19
 * PROCESS_STATUS
 */
@Getter
@AllArgsConstructor
public enum ProcessStatus {


    UNDER_REVIEW("审核中"),
    IS_PASSED("已通过"),
    NOT_PASS("未通过");

    String value;
}
