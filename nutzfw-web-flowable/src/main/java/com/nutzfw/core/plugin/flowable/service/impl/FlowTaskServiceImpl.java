/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nutzfw.core.common.util.DateUtil;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.core.plugin.flowable.cmd.FindNextUserTaskNodeCmd;
import com.nutzfw.core.plugin.flowable.constant.FlowConstant;
import com.nutzfw.core.plugin.flowable.converter.CustomBpmnJsonConverter;
import com.nutzfw.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.nutzfw.core.plugin.flowable.enums.CallBackTypeEnum;
import com.nutzfw.core.plugin.flowable.enums.TaskStatusEnum;
import com.nutzfw.core.plugin.flowable.service.FlowCacheService;
import com.nutzfw.core.plugin.flowable.service.FlowProcessDefinitionService;
import com.nutzfw.core.plugin.flowable.service.FlowTaskService;
import com.nutzfw.core.plugin.flowable.util.FlowUtils;
import com.nutzfw.core.plugin.flowable.vo.FlowAttachmentVO;
import com.nutzfw.core.plugin.flowable.vo.FlowCommentVO;
import com.nutzfw.core.plugin.flowable.vo.FlowTaskHistoricVO;
import com.nutzfw.core.plugin.flowable.vo.FlowTaskVO;
import com.nutzfw.modules.flow.service.FlowTypeService;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.service.UserAccountService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Attachment;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.nutz.el.El;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.stream.StringInputStream;
import org.nutz.trans.Trans;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/11
 */
@IocBean(name = "flowTaskService")
@Slf4j
public class FlowTaskServiceImpl implements FlowTaskService {
    @Inject
    UserAccountService           userAccountService;
    @Inject
    FlowTypeService              flowTypeService;
    @Inject
    FlowProcessDefinitionService flowProcessDefinitionService;
    @Inject
    FlowCacheService             flowCacheService;
    @Inject
    RuntimeService               runtimeService;
    @Inject
    TaskService                  taskService;
    @Inject
    FormService                  formService;
    @Inject
    HistoryService               historyService;
    @Inject
    RepositoryService            repositoryService;
    @Inject
    ManagementService            managementService;

    /**
     * 获取待办\待簽收列表
     *
     * @return
     */
    @Override
    public LayuiTableDataListVO todoList(LayuiTableDataListVO layuiTableDataListVO, FlowTaskVO flowTaskVO, String userName, Set<String> roleCodes, boolean needFormData) {
        //  单人或用户组待签收/待办理
        TaskQuery todoTaskQuery = buildQuery(flowTaskVO);
        FlowUtils.buildTodoQuery(todoTaskQuery, userName, Lists.newArrayList(roleCodes));
        todoTaskQuery.orderByTaskCreateTime().desc();
        layuiTableDataListVO.setCount(todoTaskQuery.count());
        List<Task> listPage = todoTaskQuery.listPage(layuiTableDataListVO.getFirstResult(), layuiTableDataListVO.getPageSize());
        layuiTableDataListVO.setData(taskQueryFlow(listPage, needFormData));
        return layuiTableDataListVO;
    }


    private TaskQuery buildQuery(FlowTaskVO flowTaskVO) {
        //联查变量的情况下，分页功能失效
        TaskQuery query = taskService.createTaskQuery().includeProcessVariables().active();
        // 设置查询条件
        if (Strings.isNotBlank(flowTaskVO.getProcDefKey())) {
            query.taskDefinitionKey(flowTaskVO.getProcDefKey());
        }
        if (flowTaskVO.getBeginDate() != null) {
            query.taskCreatedAfter(flowTaskVO.getBeginDate());
        }
        if (flowTaskVO.getEndDate() != null) {
            query.taskCreatedBefore(flowTaskVO.getEndDate());
        }
        return query;
    }

    private List taskQueryFlow(List<Task> listPage, boolean needFormData) {
        List<FlowTaskVO> flowList = new ArrayList<>();
        for (Task task : listPage) {
            String categoryName = "未分类";
            if (!FlowConstant.DEFAULT_CATEGORY.equals(task.getCategory())) {
                categoryName = flowTypeService.fetch(task.getCategory()).getName();
            }
            String flowTitle = task.getProcessVariables().getOrDefault(FlowConstant.PROCESS_TITLE, "").toString();
            ProcessDefinition pd = flowCacheService.getProcessDefinitionCache(task.getProcessDefinitionId());
            FlowTaskVO flow = FlowTaskVO.builder()
                    .categoryName(categoryName)
                    .category(task.getCategory())
                    .taskTitle(flowTitle)
                    .delegateUserName(task.getOwner())
                    .delegateStatus(task.getDelegationState())
                    .taskId(task.getId())
                    .taskDefKey(task.getTaskDefinitionKey())
                    .taskName(task.getName())
                    .assignee(task.getAssignee())
                    .createTime(task.getCreateTime())
                    .procDefId(pd.getId())
                    .procDefname(pd.getName())
                    .procDefKey(pd.getKey())
                    .procDefversion(pd.getVersion())
                    .procInsId(task.getProcessInstanceId())
                    .claimTime(task.getClaimTime())
                    .status(Strings.isNotBlank(task.getAssignee()) ? TaskStatusEnum.TODO : TaskStatusEnum.CLAIM)
                    .build();
            if (needFormData) {
                flow.setFormData(task.getProcessVariables().getOrDefault(FlowConstant.FORM_DATA, new Object()));
            }
            flowList.add(flow);
        }
        return flowList;
    }

    /**
     * 获取已发任务
     *
     * @return
     */
    @Override
    public LayuiTableDataListVO hasSentList(LayuiTableDataListVO vo, String userId) {
        HistoricProcessInstanceQuery historyProQuery = historyService.createHistoricProcessInstanceQuery().startedBy(userId).orderByProcessInstanceStartTime().desc();
        // 查询总数
        vo.setCount(historyProQuery.count());
        // 查询列表
        List<HistoricProcessInstance> hispList = historyProQuery.listPage(vo.getFirstResult(), vo.getPageSize());
        //处理分页问题
        List<FlowTaskVO> actList = Lists.newArrayList();
        for (HistoricProcessInstance hisprocIns : hispList) {
            FlowTaskVO flow = new FlowTaskVO();
            ProcessDefinition pd = flowCacheService.getProcessDefinitionCache(hisprocIns.getProcessDefinitionId());
            Deployment deployment = flowCacheService.getDeploymentCache(pd.getDeploymentId());
            flow.setCategory(deployment.getCategory());
            flow.setCategoryName(flowTypeService.fetchCategoryName(deployment.getCategory()));
            flow.setProcDefId(pd.getId());
            flow.setProcDefname(pd.getName());
            flow.setProcDefKey(pd.getKey());
            flow.setProcDefversion(pd.getVersion());
            flow.setProcInsId(hisprocIns.getId());
            HistoricVariableInstance historicVariableInstance = historyService.createHistoricVariableInstanceQuery().processInstanceId(hisprocIns.getId()).variableName(FlowConstant.PROCESS_TITLE).singleResult();
            flow.setCreateTime(hisprocIns.getStartTime());
            flow.setEndTime(hisprocIns.getEndTime());
            flow.setTaskTitle(historicVariableInstance.getValue().toString());
            flow.setBusinessId(hisprocIns.getBusinessKey());
            flow.setHisActInsActName(hisprocIns.getName());
            flow.setProcInsId(hisprocIns.getId());
            flow.setHisProcInsId(hisprocIns.getId());
            flow.setProcessFinished(hisprocIns.getEndActivityId() != null);
            flow.setStatus(TaskStatusEnum.FINISH);
            actList.add(flow);
        }
        vo.setData(actList);
        return vo;
    }

    /**
     * 获取已办任务
     *
     * @return
     */
    @Override
    public LayuiTableDataListVO historicList(LayuiTableDataListVO vo, FlowTaskVO flowTaskVO, String userId) {
        HistoricTaskInstanceQuery histTaskQuery = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).finished()
                .includeProcessVariables().orderByHistoricTaskInstanceEndTime().desc();
        // 设置查询条件
        if (Strings.isNotBlank(flowTaskVO.getProcDefKey())) {
            histTaskQuery.processDefinitionKey(flowTaskVO.getProcDefKey());
        }
        if (flowTaskVO.getBeginDate() != null) {
            histTaskQuery.taskCompletedAfter(flowTaskVO.getBeginDate());
        }
        if (flowTaskVO.getEndDate() != null) {
            histTaskQuery.taskCompletedBefore(flowTaskVO.getEndDate());
        }
        // 查询总数
        vo.setCount(histTaskQuery.count());
        // 查询列表
        List<HistoricTaskInstance> histList = histTaskQuery.listPage(vo.getFirstResult(), vo.getPageSize());
        //处理分页问题
        List<FlowTaskVO> actList = Lists.newArrayList();
        for (HistoricTaskInstance histTask : histList) {
            String categoryName = "未分类";
            String flowTitle = histTask.getProcessVariables().getOrDefault(FlowConstant.PROCESS_TITLE, "").toString();
            ProcessDefinition pd = flowCacheService.getProcessDefinitionCache(histTask.getProcessDefinitionId());
            String category;
            if (Strings.isBlank(histTask.getCategory())) {
                Deployment deployment = flowCacheService.getDeploymentCache(pd.getDeploymentId());
                category = deployment.getCategory();
            } else {
                category = histTask.getCategory();
            }
            if (!FlowConstant.DEFAULT_CATEGORY.equals(category)) {
                categoryName = flowTypeService.fetch(category).getName();
            }
            HistoricProcessInstance historicTaskInstance = getHistoryProcIns(histTask.getProcessInstanceId());
            FlowTaskVO flow = FlowTaskVO.builder()
                    .categoryName(categoryName)
                    .category(category)
                    .taskTitle(flowTitle)
                    .delegateUserName(histTask.getOwner())
                    .taskId(histTask.getId())
                    .taskDefKey(histTask.getTaskDefinitionKey())
                    .taskName(histTask.getName())
                    .assignee(histTask.getAssignee())
                    .createTime(histTask.getCreateTime())
                    .endDate(histTask.getEndTime())
                    .procDefId(pd.getId())
                    .procDefname(pd.getName())
                    .procDefKey(pd.getKey())
                    .procDefversion(pd.getVersion())
                    .procInsId(histTask.getProcessInstanceId())
                    .hisProcInsId(histTask.getProcessInstanceId())
                    .processFinished(historicTaskInstance.getEndActivityId() != null)
                    .status(TaskStatusEnum.FINISH)
                    .build();
            actList.add(flow);
        }
        vo.setData(actList);
        return vo;
    }

    /**
     * 获取流转历史列表
     *
     * @param procInsId 流程实例
     * @param startAct  开始活动节点名称
     * @param endAct    结束活动节点名称
     */
    @Override
    public List<FlowTaskHistoricVO> histoicFlowList(String procInsId, String startAct, String endAct) {
        List<FlowTaskHistoricVO> actList = Lists.newArrayList();
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().processInstanceId(procInsId).orderByHistoricActivityInstanceStartTime().asc().list();
        boolean start = false;
        Map<String, Integer> actMap = Maps.newHashMap();
        for (int i = 0; i < list.size(); i++) {
            HistoricActivityInstance histIns = list.get(i);
            // 过滤开始节点前的节点
            if (Strings.isNotBlank(startAct) && startAct.equals(histIns.getActivityId())) {
                start = true;
            }
            if (Strings.isNotBlank(startAct) && !start) {
                continue;
            }
            if ("userTask".equals(histIns.getActivityType()) || "startEvent".equals(histIns.getActivityType()) || "endEvent".equals(histIns.getActivityType())) {
                // 给节点增加一个序号
                Integer actNum = actMap.get(histIns.getActivityId());
                if (actNum == null) {
                    actMap.put(histIns.getActivityId(), actMap.size());
                }
                FlowTaskHistoricVO flowTaskHistoricVO = new FlowTaskHistoricVO();
                flowTaskHistoricVO.setActivityName(histIns.getActivityName());
                flowTaskHistoricVO.setStartTime(histIns.getStartTime());
                flowTaskHistoricVO.setEndTime(histIns.getEndTime());
                flowTaskHistoricVO.setActivityType(histIns.getActivityType());
                flowTaskHistoricVO.setTimeConsuming(histIns.getDurationInMillis() == null ? "" : DateUtil.getDistanceTime(histIns.getDurationInMillis()));
                // 获取流程发起人名称
                if ("startEvent".equals(histIns.getActivityType())) {
                    List<HistoricProcessInstance> il = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInsId).orderByProcessInstanceStartTime().asc().list();
                    if (il.size() > 0) {
                        if (Strings.isNotBlank(il.get(0).getStartUserId())) {
                            UserAccount user = userAccountService.fetchByUserName(il.get(0).getStartUserId());
                            if (user != null) {
                                flowTaskHistoricVO.setAssignee(histIns.getAssignee());
                                flowTaskHistoricVO.setAssigneeName(MessageFormat.format("{0}({1})", user.getRealName(), user.getUserName()));
                            }
                        }
                    }
                }
                //用户任务
                if ("userTask".equals(histIns.getActivityType())) {
                    // 获取任务执行人名称
                    if (Strings.isNotBlank(histIns.getAssignee())) {
                        UserAccount user = userAccountService.fetchByUserName(histIns.getAssignee());
                        if (user != null) {
                            flowTaskHistoricVO.setAssignee(histIns.getAssignee());
                            flowTaskHistoricVO.setAssigneeName(MessageFormat.format("{0}({1})", user.getRealName(), user.getUserName()));
                        }
                    }
                    // 获取意见评论内容 和 附件
                    if (Strings.isNotBlank(histIns.getTaskId())) {
                        List<Attachment> attachmentList = taskService.getTaskAttachments(histIns.getTaskId());
                        List<Comment> commentList = taskService.getTaskComments(histIns.getTaskId());
                        if (commentList.size() > 0) {
                            Collections.reverse(commentList);
                            List<FlowCommentVO> flowComments = new ArrayList<>();
                            commentList.forEach(comment -> {
                                UserAccount user = userAccountService.fetchByUserName(comment.getUserId());
                                FlowCommentVO commentVO = FlowCommentVO.builder().time(comment.getTime()).userId(comment.getUserId()).userDesc(MessageFormat.format("{0}({1})", user.getRealName(), user.getUserName())).fullMessage(comment.getFullMessage()).build();
                                commentVO.setFlowAttachments(this.getCommentAttachmentList(comment.getUserId(), attachmentList));
                                commentVO.setHandWritingSignatureAttachment(this.getCommentHandWritingSignature(comment.getUserId(), attachmentList));
                                flowComments.add(commentVO);
                            });
                            flowTaskHistoricVO.setFlowComments(flowComments);
                        }
                    }
                }
                actList.add(flowTaskHistoricVO);
            }
            // 过滤结束节点后的节点
            if (Strings.isNotBlank(endAct) && endAct.equals(histIns.getActivityId())) {
                boolean bl = false;
                Integer actNum = actMap.get(histIns.getActivityId());
                // 该活动节点，后续节点是否在结束节点之前，在后续节点中是否存在
                for (int j = i + 1; j < list.size(); j++) {
                    HistoricActivityInstance hi = list.get(j);
                    Integer actNumA = actMap.get(hi.getActivityId());
                    boolean b = (actNumA != null && actNumA < actNum) || Strings.equals(hi.getActivityId(), histIns.getActivityId());
                    if (b) {
                        bl = true;
                    }
                }
                if (!bl) {
                    break;
                }
            }
        }
        Collections.reverse(actList);
        return actList;
    }

    private FlowAttachmentVO getCommentHandWritingSignature(String userName, List<Attachment> attachmentList) {
        if (CollectionUtils.isNotEmpty(attachmentList)) {
            for (Attachment attachment : attachmentList) {
                if (!attachment.getUserId().equals(userName)) {
                    continue;
                }
                FlowAttachmentVO flowAttachmentVO = FlowAttachmentVO.builder().contentId(attachment.getContentId()).name(attachment.getName()).type(attachment.getType()).userId(attachment.getUserId()).build();
                if (attachment.getName().equals(FlowConstant.HAND_WRITING_SIGNATURE_ATTACHMENT_NAME)) {
                    byte[] bytes = Streams.readBytesAndClose(taskService.getAttachmentContent(attachment.getId()));
                    flowAttachmentVO.setContent(new String(bytes, StandardCharsets.UTF_8));
                    return flowAttachmentVO;
                }
            }
        }
        return null;
    }

    /**
     * 取得步骤具体人员意见上传的全部附件
     *
     * @param userName
     * @param attachmentList
     * @return
     */
    private List<FlowAttachmentVO> getCommentAttachmentList(String userName, List<Attachment> attachmentList) {
        List<FlowAttachmentVO> attachmentVOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(attachmentList)) {
            for (Attachment attachment : attachmentList) {
                if (!attachment.getUserId().equals(userName) || attachment.getName().equals(FlowConstant.HAND_WRITING_SIGNATURE_ATTACHMENT_NAME)) {
                    continue;
                }
                attachmentVOS.add(FlowAttachmentVO.builder()
                        .contentId(attachment.getContentId()).name(attachment.getName()).type(attachment.getType()).userId(attachment.getUserId())
                        .build());
            }
        }
        return attachmentVOS;
    }

    /**
     * 获取流程表单（首先获取任务节点表单KEY，如果没有则取流程开始节点表单KEY）
     *
     * @return
     */
    @Override
    public String getFormKey(String procDefId, String taskDefKey) {
        String formKey = "";
        if (Strings.isNotBlank(procDefId)) {
            try {
                if (Strings.isNotBlank(taskDefKey)) {
                    formKey = formService.getTaskFormKey(procDefId, taskDefKey);
                }
            } catch (Exception e) {
                formKey = "";
            }
            if (Strings.isBlank(formKey)) {
                formKey = formService.getStartFormKey(procDefId);
            }
        }
        log.debug("getFormKey: %s", formKey);
        return formKey;
    }

    /**
     * 获取流程实例对象
     *
     * @param procInsId
     * @return
     */
    @Override
    public ProcessInstance getProcIns(String procInsId) {
        return runtimeService.createProcessInstanceQuery().processInstanceId(procInsId).singleResult();
    }

    /**
     * 获取历史流程实例对象
     *
     * @param procInsId
     * @return
     */
    @Override
    public HistoricProcessInstance getHistoryProcIns(String procInsId) {
        return historyService.createHistoricProcessInstanceQuery().processInstanceId(procInsId).singleResult();
    }

    /**
     * 启动流程
     *
     * @param procDefKey 流程定义KEY
     * @param businessId 业务表编号
     * @return 流程实例ID
     */
    @Override
    public String startProcess(String procDefKey, String businessId, String userName, String deptId, Set<String> roleCodes) {
        return startProcess(procDefKey, businessId, Maps.newHashMap(), userName, deptId, roleCodes);
    }

    /**
     * 启动流程
     *
     * @param procDefKey 流程定义KEY
     * @param businessId 业务表编号
     * @param vars       流程变量
     * @return 流程实例ID
     */
    @Override
    public String startProcess(String procDefKey, String businessId, Map<String, Object> vars, String userName, String deptId, Set<String> roleCodes) {
        // 设置流程变量
        if (vars == null) {
            vars = Maps.newHashMap();
        }
        //设置流程发起人-可以在后面流程中驳回重新办理
        vars.put(FlowConstant.SUBMITTER, userName);
        vars.put(FlowConstant.SUBMITTER_DEPT_ID, deptId);
        vars.put(FlowConstant.SUBMITTER_ROLE_CODES, roleCodes);
        // 启动流程
        return runtimeService.startProcessInstanceByKey(procDefKey, businessId, vars).getId();
    }

    /**
     * 获取任务
     *
     * @param taskId 任务ID
     */
    @Override
    public Task getTask(String taskId) {
        return taskService.createTaskQuery().taskId(taskId).singleResult();
    }

    /**
     * 获取历史任务
     *
     * @param taskId 任务ID
     */
    @Override
    public HistoricTaskInstance getHistoryTask(String taskId) {
        return historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
    }

    @Override
    public TaskInfo getTaskOrHistoryTask(String taskId) {
        TaskInfo taskInfo = getTask(taskId);
        if (taskInfo == null) {
            taskInfo = getHistoryTask(taskId);
        }
        return taskInfo;
    }

    /**
     * 获取历史任务
     *
     * @param procInsId 流程实例ID
     */
    @Override
    public HistoricTaskInstance getHistoryTaskByProcessInstanceId(String procInsId) {
        return historyService.createHistoricTaskInstanceQuery().processInstanceId(procInsId).orderByHistoricTaskInstanceEndTime().desc().list().get(0);
    }

    /**
     * 删除任务
     *
     * @param taskId       任务ID
     * @param deleteReason 删除原因
     */
    @Override
    public void deleteTask(String taskId, String deleteReason) {
        taskService.deleteTask(taskId, deleteReason);
    }

    /**
     * 签收任务
     *
     * @param taskId 任务ID
     * @param userId 签收用户ID（用户登录名）
     */
    @Override
    public void claim(String taskId, String userId) {
        taskService.claim(taskId, userId);
    }

    /**
     * 委托他人完成任务
     *
     * @param taskId 任务ID
     * @param userId 被委托人
     */
    @Override
    public void delegateTask(String taskId, String userId) {
        taskService.delegateTask(taskId, userId);
    }

    /**
     * 转派他人完成
     *
     * @param taskId 任务ID
     * @param userId 被委托人
     * @param reason 原因
     */
    @Override
    public void transferTask(String taskId, String userId, String reason) {
        Task task = getTask(taskId);
        taskService.addComment(taskId, task.getProcessInstanceId(), "[转派] " + reason);
        taskService.setAssignee(taskId, userId);
    }

    /**
     * 取消签收任务
     *
     * @param taskId 任务ID
     * @param userId 签收用户ID（用户登录名）
     */
    @Override
    public void unclaim(String taskId, String userId) {
        taskService.unclaim(taskId);
    }

    /**
     * 提交任务, 并保存意见
     *
     * @param vars 任务变量
     */
    @Override
    public void complete(FlowTaskVO flowTaskVO, Map<String, Object> vars) {
        String taskId = flowTaskVO.getTaskId();
        String comment = flowTaskVO.getComment();
        // 设置流程变量
        if (vars == null) {
            vars = Maps.newHashMap();
        }
        Task task = getTask(taskId);
        String procInsId = task.getProcessInstanceId();
        //是否委托
        boolean delegation = task.getDelegationState() != null && DelegationState.PENDING == task.getDelegationState();
        // 添加意见
        if (Strings.isNotBlank(procInsId) && Strings.isNotBlank(comment)) {
            if (delegation) {
                taskService.addComment(taskId, procInsId, "[委托] " + comment);
            } else {
                taskService.addComment(taskId, procInsId, comment);
            }
        }
        //添加流程手写签字数据
        if (Strings.isNotBlank(flowTaskVO.getHandWritingSignatureData())) {
            this.addTaskHandWritingSignatureAttachment(taskId, procInsId, flowTaskVO.getHandWritingSignatureData());
        }
        if (delegation) {
            // 完成任务委托
            taskService.resolveTask(taskId, vars);
        } else {
            // 完成提交任务
            taskService.complete(taskId, vars);
        }
    }

    /**
     * 添加任务意见
     */
    @Override
    public void addTaskComment(String taskId, String procInsId, String comment) {
        taskService.addComment(taskId, procInsId, comment);
    }

    /**
     * 添加手写签字数据
     */
    @Override
    public void addTaskHandWritingSignatureAttachment(String taskId, String procInsId, String base64data) {
        String userName = Authentication.getAuthenticatedUserId();
        List<Attachment> attachmentList = taskService.getTaskAttachments(taskId);
        Attachment attachment = attachmentList.stream().filter(attach -> attach.getUserId().equals(userName) && attach.getName().equals(FlowConstant.HAND_WRITING_SIGNATURE_ATTACHMENT_NAME)).findAny().orElse(null);
        if (attachment != null) {
            taskService.deleteAttachment(attachment.getId());
        }
        taskService.createAttachment("jpg", taskId, procInsId, FlowConstant.HAND_WRITING_SIGNATURE_ATTACHMENT_NAME, FlowConstant.HAND_WRITING_SIGNATURE_ATTACHMENT_NAME, new StringInputStream(base64data));
    }

    /**
     * 多实例加签
     * 只有多实例节点才可以加签、减签
     *
     * @param flowTaskVO
     * @param comment
     */
    @Override
    public String addMultiInstance(FlowTaskVO flowTaskVO, String comment) {
        //要加签的节点id 流程实例id
        runtimeService.addMultiInstanceExecution(flowTaskVO.getTaskDefKey(), flowTaskVO.getProcInsId(), Collections.singletonMap("assignee", flowTaskVO.getAddMultiInstanceAssignee()));
        return null;
    }

    /**
     * 流程回退
     *
     * @param flowTaskVO
     * @param userName
     */
    @Override
    public String backToStep(FlowTaskVO flowTaskVO, String userName) {
        String taskId = flowTaskVO.getTaskId();
        String comment = flowTaskVO.getComment();
        String backToTaskDefKey = flowTaskVO.getBackToTaskDefKey();
        UserTaskExtensionDTO extensionPropertyDTO = flowProcessDefinitionService.getUserTaskExtension(flowTaskVO.getTaskDefKey(), flowTaskVO.getProcDefId());
        if (extensionPropertyDTO != null) {
            if (extensionPropertyDTO.getCallBackType() == CallBackTypeEnum.NONE) {
                return "当前流程不允许回退";
            } else if (extensionPropertyDTO.getCallBackType() == CallBackTypeEnum.PREVIOUS_STEP) {
                backToTaskDefKey = extensionPropertyDTO.getCallBackNodes();
                if (Strings.isEmpty(backToTaskDefKey)) {
                    return "请设置当前流程应该回退的上一步骤！";
                }
            } else if (extensionPropertyDTO.getCallBackType() == CallBackTypeEnum.FREE_STEP) {
                List<String> nodes = Arrays.asList(extensionPropertyDTO.getCallBackNodes().split(","));
                if (Strings.isEmpty(backToTaskDefKey) || !nodes.contains(backToTaskDefKey)) {
                    return "请设置当前流程应该回退的步骤！";
                }
            }
        }
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processInstanceId = task.getProcessInstanceId();
        //保存任务信息
        task.setAssignee(userName);
        this.addTaskComment(taskId, processInstanceId, comment);
        taskService.saveTask(task);

        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        List<String> currentActivityIds = new ArrayList<>();
        tasks.forEach(t -> currentActivityIds.add(t.getTaskDefinitionKey()));
        //执行驳回操作
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(processInstanceId)
                .moveActivityIdsToSingleActivityId(currentActivityIds, backToTaskDefKey)
                .changeState();
        return null;
    }

    @Override
    public void setValuedDataObject(Map<String, Object> variables, String processDefinitionId, Object form, UserAccount userAccount, boolean update) {
        List<ValuedDataObject> ValuedDataObjects = repositoryService.getBpmnModel(processDefinitionId).getMainProcess().getDataObjects();
        if (!ValuedDataObjects.stream().filter(va -> FlowConstant.PROCESS_TITLE.equals(va.getId())).findAny().isPresent()) {
            throw Lang.makeThrow("流程应该设置标题模版数据对象，ID为 %s", FlowConstant.PROCESS_TITLE);
        }
        ValuedDataObjects.stream().forEach(valued -> {
            List<ExtensionElement> extensionElements = valued.getExtensionElements().get(CustomBpmnJsonConverter.DATA_OBJECTS_EXPRESSION);
            if (CollectionUtils.isNotEmpty(extensionElements)) {
                String expression = Strings.sNull(extensionElements.get(0).getElementText());
                if (update) {
                    //更新变量时，只有值不为空时才更新，因为可能存在提交的表单数据不完整的情况
                    try {
                        String valStr = El.render(expression, Lang.context().set("form", form).set("user", userAccount));
                        if (Strings.isNotBlank(valStr)) {
                            variables.put(valued.getId(), getVal(valued, valStr));
                        }
                    } catch (Exception e) {
                        //忽略异常，需要注意：数据不完整的情况下，可能导致变量数据不能及时更新
                    }
                } else {
                    //新增变量时，如变量无法按照规则产出，则需要抛出异常
                    variables.put(valued.getId(), getVal(valued, El.render(expression, Lang.context().set("form", form).set("user", userAccount))));
                }
            }
        });
    }

    private Object getVal(ValuedDataObject valued, String valStr) {
        Object val = null;
        if (valued instanceof StringDataObject) {
            val = valStr;
        } else if (valued instanceof IntegerDataObject) {
            val = Integer.parseInt(valStr);
        } else if (valued instanceof LongDataObject) {
            val = Long.parseLong(valStr);
        } else if (valued instanceof DoubleDataObject) {
            val = Double.parseDouble(valStr);
        } else if (valued instanceof BooleanDataObject) {
            val = Boolean.parseBoolean(valStr);
        } else if (valued instanceof DateDataObject) {
            val = DateUtil.string2date(valStr, DateUtil.YYYY_MM_DD_HH_MM_SS);
        }
        return val;
    }

    /**
     * 未经过足够的测试，谨慎使用
     *
     * @param formData
     * @param flowTaskVO
     * @param sessionUserAccount
     * @return
     * @date 2019-10-09
     */
    @Override
    public UserTask getNextNode(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        UserTask userTask;
        try {
            Task task = taskService.createTaskQuery().taskId(flowTaskVO.getTaskId()).singleResult();
            String executionId = task.getExecutionId();
            ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(executionId).singleResult();
            BpmnModel bpmnModel = repositoryService.getBpmnModel(execution.getProcessDefinitionId());
            Map<String, Object> vars = Maps.newHashMap();
            vars.put(FlowConstant.AUDIT_PASS, flowTaskVO.isPass());
            vars.put(FlowConstant.FORM_DATA, formData);
            setValuedDataObject(vars, flowTaskVO.getProcDefId(), formData, sessionUserAccount, true);
            userTask = managementService.executeCommand(new FindNextUserTaskNodeCmd(execution, bpmnModel, vars));
            //将寻找下一节点执行产生的的数据进行回滚
            Trans.rollback();
        } catch (Exception e) {
            throw new RuntimeException("事务回滚失败！" + e.getMessage());
        }
        return userTask;
    }

    /**
     * 未经过足够的测试，谨慎使用
     *
     * @param formData
     * @param flowTaskVO
     * @param sessionUserAccount
     * @return
     * @date 2019-10-09
     */
    @Override
    public UserTask previewNextNode(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) throws Exception {
        try {
            Task task = taskService.createTaskQuery().taskId(flowTaskVO.getTaskId()).singleResult();
            String executionId = task.getExecutionId();
            ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(executionId).singleResult();
            BpmnModel bpmnModel = repositoryService.getBpmnModel(execution.getProcessDefinitionId());
            Map<String, Object> vars = Maps.newHashMap();
            vars.put(FlowConstant.AUDIT_PASS, flowTaskVO.isPass());
            vars.put(FlowConstant.FORM_DATA, formData);
            Trans.begin();
            setValuedDataObject(vars, flowTaskVO.getProcDefId(), formData, sessionUserAccount, true);
            return managementService.executeCommand(new FindNextUserTaskNodeCmd(execution, bpmnModel, vars));
        } finally {
            Trans.clear(true);
        }
    }


}
