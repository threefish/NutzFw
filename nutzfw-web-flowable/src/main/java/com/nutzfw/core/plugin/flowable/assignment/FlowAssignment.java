package com.nutzfw.core.plugin.flowable.assignment;

import com.nutzfw.core.plugin.flowable.dto.FlowSubmitInfoDTO;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.task.service.TaskService;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import java.util.List;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/8/29
 */
public interface FlowAssignment {
    /**
     * 初始化流程相关变量
     *
     * @param taskService
     * @param flowSubmitInfoDTO
     * @param assignee
     * @param owner
     * @param candidateUsers
     * @param candidateGroups
     * @param task
     * @param execution
     */
    void init(TaskService taskService, FlowSubmitInfoDTO flowSubmitInfoDTO, String assignee, String owner, List<String> candidateUsers, List<String> candidateGroups, TaskEntity task, DelegateExecution execution);

    String getAssignee();

    List<String> getCandidateUsers();

    List<String> getCandidateGroups();
}
