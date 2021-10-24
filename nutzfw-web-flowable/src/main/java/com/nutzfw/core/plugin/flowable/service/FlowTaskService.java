/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.service;

import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.core.plugin.flowable.vo.FlowTaskHistoricVO;
import com.nutzfw.core.plugin.flowable.vo.FlowTaskVO;
import com.nutzfw.modules.flow.vo.NextNodeConfigVO;
import com.nutzfw.modules.organize.entity.UserAccount;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/17
 */
public interface FlowTaskService {
    /**
     * 获取待办、待签收列表
     *
     * @param layuiTableDataListVO
     * @param flowTaskVO
     * @param userName
     * @param sessionRoleCodes
     * @return
     */
    LayuiTableDataListVO todoList(LayuiTableDataListVO layuiTableDataListVO, FlowTaskVO flowTaskVO, String userName, Set<String> sessionRoleCodes, boolean needFormData);

    /**
     * 获取已发任务
     *
     * @param vo
     * @param userName
     * @return
     */
    LayuiTableDataListVO hasSentList(LayuiTableDataListVO vo, String userName);

    /**
     * 获取已办任务
     *
     * @param vo
     * @param flowTaskVO
     * @param userName
     * @return
     */
    LayuiTableDataListVO historicList(LayuiTableDataListVO vo, FlowTaskVO flowTaskVO, String userName);

    /**
     * 获取流转历史列表
     *
     * @param procInsId
     * @param startAct
     * @param endAct
     * @return
     */
    List<FlowTaskHistoricVO> histoicFlowList(String procInsId, String startAct, String endAct);

    /**
     * 获取流程表单（首先获取任务节点表单KEY，如果没有则取流程开始节点表单KEY）
     *
     * @param procDefId
     * @param taskDefKey
     * @return
     */
    String getFormKey(String procDefId, String taskDefKey);

    /**
     * 获取流程实例对象
     *
     * @param procInsId
     * @return
     */
    ProcessInstance getProcIns(String procInsId);

    /**
     * 获取历史流程实例对象
     *
     * @param procInsId
     * @return
     */
    HistoricProcessInstance getHistoryProcIns(String procInsId);

    /**
     * 启动流程
     *
     * @param procDefKey 流程定义KEY
     * @param businessId 业务表ID
     * @param userName
     * @return
     */
    String startProcess(String procDefKey, String businessId, String userName, String deptId, Set<String> roleCodes);

    /**
     * 启动流程
     *
     * @param procDefKey 流程定义KEY
     * @param businessId 业务表ID
     * @param userName
     * @return
     */
    String startProcess(String procDefKey, String businessId, Map<String, Object> vars, String userName, String deptId, Set<String> roleCodes);


    /**
     * 获取任务
     *
     * @param taskId 任务ID
     * @return
     */
    Task getTask(String taskId);

    /**
     * 获取历史任务
     *
     * @param taskId 任务ID
     * @return
     */
    HistoricTaskInstance getHistoryTask(String taskId);

    /**
     * 获取任务
     *
     * @param taskId 任务ID
     * @return
     */
    TaskInfo getTaskOrHistoryTask(String taskId);


    /**
     * 获取历史任务
     *
     * @param procInsId 流程实例ID
     * @return
     */
    HistoricTaskInstance getHistoryTaskByProcessInstanceId(String procInsId);

    /**
     * 删除任务
     *
     * @param taskId       任务ID
     * @param deleteReason 删除原因
     */
    void deleteTask(String taskId, String deleteReason);

    /**
     * 结束流程实例
     *
     * @param processInstanceId 流程实例ID
     * @param stopReason        停止原因
     */
    void stopProcessInstance(String processInstanceId, String stopReason);

    /**
     * 签收任务
     *
     * @param taskId   任务ID
     * @param userName 签收用户ID（用户登录名）
     */
    void claim(String taskId, String userName);

    /**
     * 委托任务
     *
     * @param taskId
     * @param userId
     */
    void delegateTask(String taskId, String userId);


    /**
     * 转派
     *
     * @param taskId
     * @param userId
     * @param reason
     */
    void transferTask(String taskId, String userId, String reason);

    /**
     * 取消签收任务
     *
     * @param taskId   任务ID
     * @param userName 签收用户ID（用户登录名）
     */
    void unclaim(String taskId, String userName);

    /**
     * 提交任务, 并保存意见
     *
     * @param flowTaskVO
     * @param vars       任务变量
     */
    void complete(FlowTaskVO flowTaskVO, Map<String, Object> vars);

    /**
     * 添加任务意见
     *
     * @param taskId
     * @param procInsId
     * @param comment
     */
    void addTaskComment(String taskId, String procInsId, String comment);

    /**
     * 添加手写签字数据
     *
     * @param taskId
     * @param procInsId
     * @param base64data
     */
    void addTaskHandWritingSignatureAttachment(String taskId, String procInsId, String base64data);

    /**
     * 加签
     *
     * @param flowTaskVO
     * @param comment
     */
    String addMultiInstance(FlowTaskVO flowTaskVO, String comment);


    /**
     * 回退
     *
     * @param userName
     * @return
     */
    String backToStep(FlowTaskVO flowTaskVO, String userName);


    /**
     * 设置流程变量（不是步骤变量）
     *
     * @param variables
     * @param processDefinitionId
     * @param form
     * @param userAccount
     * @param update
     */
    void setValuedDataObject(Map<String, Object> variables, String processDefinitionId, Object form, UserAccount userAccount, boolean update);

    /**
     * 在流程完成时取得下一节点信息，在调用此方法前的所有事务都不生效
     *
     * @param formData
     * @param flowTaskVO
     * @return
     * @throws Exception
     */
    UserTask getNextNode(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) throws Exception;

    /**
     * 预览下一步流程节点，会执行事务回滚保证数据安全
     *
     * @param formData
     * @param flowTaskVO
     * @param sessionUserAccount
     * @return
     * @throws Exception
     */
    NextNodeConfigVO previewNextNode(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) throws Exception;

}
