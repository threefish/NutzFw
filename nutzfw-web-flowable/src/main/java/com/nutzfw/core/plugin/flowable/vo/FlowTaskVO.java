/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.vo;

import com.nutzfw.core.common.util.DateUtil;
import com.nutzfw.core.plugin.flowable.enums.TaskStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flowable.task.api.DelegationState;
import org.nutz.lang.Strings;

import java.util.Date;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/29
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlowTaskVO {
    /**
     * 任务编号
     */
    String          taskId;
    /**
     * 任务名称
     */
    String          taskName;
    /**
     * 任务标题
     */
    String          taskTitle;
    /**
     * 任务定义Key（任务环节标识）
     */
    String          taskDefKey;
    /**
     * 任务执行人编号
     */
    String          assignee;
    /**
     * 任务执行人名称
     */
    String          assigneeName;
    /**
     * 签收时间
     */
    Date            claimTime;
    /**
     * 流程分类ID
     */
    String          category;
    /**
     * 流程分类名称
     */
    String          categoryName;
    Date            createTime;
    Date            endTime;
    /**
     * 执行ID
     */
    String          executionId;
    /**
     * 流程实例ID
     */
    String          procInsId;
    /**
     * 已办任务流程实例ID 查看流程图会用到
     */
    String          hisProcInsId;
    /**
     * 流程实例是否结束(true:结束，false:未结束)
     */
    Boolean         processFinished;
    /**
     * 自由回退节点key
     */
    String          backToTaskDefKey;
    /**
     * 选择的加签给谁
     */
    String          addMultiInstanceAssignee;
    /**
     * ProcessDefinition
     * 流程定义ID
     * processDefinitionId
     */
    String          procDefId;
    String          procDefKey;
    String          procDefname;
    Integer         procDefversion;
    String          proceDefDesc;
    /**
     * HistoricActivityInstance
     */
    String          hisActInsActName;
    /**
     * 历史活动耗时
     */
    String          hisActInsDuTime;
    /**
     * 业务绑定ID
     */
    String          businessId;
    /**
     * 任务状态 todo/claim/finish
     */
    TaskStatusEnum  status;
    /**
     * 任务意见
     */
    String          comment;
    /**
     * 用户业务意见-不同于常见的  同意，拒绝
     */
    String          businessComment;
    /**
     * 意见状态
     */
    Boolean         pass;
    /**
     * 连线驳回
     */
    Boolean         turnDown;
    /**
     * 开始查询日期
     */
    Date            beginDate;
    /**
     * 结束查询日期
     */
    Date            endDate;
    /**
     * 自定义选择的下一步流程审核人
     */
    String          flowNextReviewerAssignee;
    /**
     * 手写签字数据
     */
    String          handWritingSignatureData;
    /**
     * 委托人
     */
    String          delegateUserName;
    /**
     * 委托状态
     */
    DelegationState delegateStatus;
    /**
     * 列表显示时使用
     */
    Object          formData;
    /**
     * 是否重申
     * 流程驳回后用户使用start接口重新申请流程使用
     */
    Boolean         reaffirm;

    public void setHisActInsDuTime(Long duTime) {
        if (duTime == null) {
            this.hisActInsDuTime = "";
        } else {
            hisActInsDuTime = DateUtil.getDistanceTime(duTime);
        }
    }

    public boolean isPass() {
        return pass == null ? false : pass;
    }

    public boolean getReaffirm() {
        return reaffirm == null ? false : reaffirm;
    }

    public boolean getTurnDown() {
        return turnDown == null ? false : turnDown;
    }

    /**
     * 是否是一个待办任务
     *
     * @return
     */
    public boolean isTodoTask() {
        return TaskStatusEnum.TODO == status || TaskStatusEnum.CLAIM == status;
    }


    /**
     * 是否是已签收任务
     *
     * @return
     */
    public boolean isClaimTask() {
        return claimTime != null;
    }

    /**
     * 是否是已完成任务
     *
     * @return
     */
    public boolean isFinishTask() {
        return TaskStatusEnum.FINISH == status;
    }

    public void setComment(String comment) {
        if (Strings.isNotBlank(comment)) {
            this.comment = Strings.escapeHtml(comment);
        } else {
            this.comment = comment;
        }
    }
}
