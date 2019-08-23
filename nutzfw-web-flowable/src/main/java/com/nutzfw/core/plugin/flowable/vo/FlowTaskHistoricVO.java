package com.nutzfw.core.plugin.flowable.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/22
 * 流程流转轨迹
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlowTaskHistoricVO {

    String activityName;
    String activityType;
    Date startTime;
    Date endTime;
    String timeConsuming;
    String assignee;
    String assigneeName;
    List<FlowCommentVO> flowComments;

}
