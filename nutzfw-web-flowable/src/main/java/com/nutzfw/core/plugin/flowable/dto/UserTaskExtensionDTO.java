package com.nutzfw.core.plugin.flowable.dto;

import com.nutzfw.core.plugin.flowable.enums.CallBackTypeEnum;
import com.nutzfw.core.plugin.flowable.enums.TaskReviewerScopeEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/26
 */
@Data
@Builder
public class UserTaskExtensionDTO {
    /**
     * userTask 原始信息
     */
    String                   userTaskFormKey;
    String                   userTaskName;
    String                   userTaskId;
    String                   userTaskDocumentation;
    /**
     * 是否允许批复意见
     */
    boolean                  replyOpinion;
    /**
     * 是否允许手写签字
     */
    boolean                  handwritingSignature;
    /**
     * 同意按钮文字显示
     */
    String                   agreeButtonName;
    /**
     * 拒绝按钮文字显示
     */
    String                   refuseButtonName;
    /**
     * 批复意见框提示文字
     */
    String                   replyOpinionName;
    /**
     * 连线回退（驳回）
     */
    boolean                  connectionCallBack;
    /**
     * 回退类型
     */
    CallBackTypeEnum         callBackType;
    /**
     * 可回退节点
     */
    String                   callBackNodes;
    String                   callBackNodesDesc;
    /**
     * 是否是多实例节点
     */
    boolean                  multiInstanceNode;
    /**
     * 是否允许加签
     */
    boolean                  addMultiInstance;
    /**
     * 是否允许减签
     */
    boolean                  delMultiInstance;
    /**
     * 减签后触发父实例完成判断
     */
    boolean                  delMultiInstanceExecutionIsCompleted;
    /**
     * 创建当前任务之前，执行表单数据动态赋值（动态代码逻辑）-应该是一段动态js，或者动态是绑定策略。
     * 不能返回给前台
     */
    String                   beforeCreateCurrentTaskFormDataDynamicAssignment;
    /**
     * 创建当前任务之后，执行表单数据动态赋值（动态代码逻辑）-应该是一段动态js，或者动态是绑定策略。
     * 不能返回给前台
     */
    String                   afterCreateCurrentTaskFormDataDynamicAssignment;
    /**
     * 完成当前任务后立即执行表单数据动态赋值（动态代码逻辑）-应该是一段动态js，或者动态是绑定策略。
     * 不能返回给前台
     */
    String                   formDataDynamicAssignment;
    /**
     * 指定当前用户步骤任务审核人范围
     */
    TaskReviewerScopeEnum    taskReviewerScope;
    /**
     * 自由选择下一步审核人(下一步流程要确保能通过流程条件正确跳转至用户任务节点)
     */
    boolean                  dynamicFreeChoiceNextReviewerMode;
    /**
     * 指定的下一步审核人范围-用户可以在其中任选一个或多选
     */
    List<CandidateUsersDTO>  nextReviewerCandidateUsers;
    /**
     * 分配给指定用户
     * TaskReviewerScopeEnum.SINGLE_USER 时生效
     */
    String                   assignee;
    /**
     * 候选用户角色组
     * TaskReviewerScopeEnum.USER_ROLE_GROUPS 时生效
     */
    List<CandidateGroupsDTO> candidateGroups;
    /**
     * 多个候选用户
     * TaskReviewerScopeEnum.MULTIPLE_USERS 时生效
     */
    List<CandidateUsersDTO>  candidateUsers;
    /**
     * JavaIocBean人员选择器
     */
    String                   iocFlowAssignment;

    public static UserTaskExtensionDTO NEW() {
        return UserTaskExtensionDTO.builder().callBackType(CallBackTypeEnum.NONE).addMultiInstance(false).build();
    }

    public String getAgreeButtonName() {
        return agreeButtonName == null ? "同意" : agreeButtonName;
    }

    public String getRefuseButtonName() {
        return refuseButtonName == null ? "拒绝" : refuseButtonName;
    }
}
