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
