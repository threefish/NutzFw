/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.flow.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskNoticeVO {

    String title;

    String content;

    String assignee;

    /**
     * 待办
     */
    Boolean todo;
    /**
     * 待签收
     */
    Boolean claim;

    Date createTime;

    /**
     * 是否委托
     */
    Boolean delegate;

}
