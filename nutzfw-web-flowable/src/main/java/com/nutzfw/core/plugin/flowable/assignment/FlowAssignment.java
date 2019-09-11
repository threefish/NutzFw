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
     *
     * @param taskService
     * @param flowSubmitInfoDTO
     * @param assignee
     * @param owner
     * @param candidateUsers
     * @param candidateGroups
     * @param task
     * @param execution
     * @return
     */
    String getAssignee(TaskService taskService, FlowSubmitInfoDTO flowSubmitInfoDTO, String assignee, String owner, List<String> candidateUsers, List<String> candidateGroups, TaskEntity task, DelegateExecution execution);

    /**
     *
     * @param taskService
     * @param flowSubmitInfoDTO
     * @param assignee
     * @param owner
     * @param candidateUsers
     * @param candidateGroups
     * @param task
     * @param execution
     * @return
     */
    List<String> getCandidateUsers(TaskService taskService, FlowSubmitInfoDTO flowSubmitInfoDTO, String assignee, String owner, List<String> candidateUsers, List<String> candidateGroups, TaskEntity task, DelegateExecution execution);

    /**
     *
     * @param taskService
     * @param flowSubmitInfoDTO
     * @param assignee
     * @param owner
     * @param candidateUsers
     * @param candidateGroups
     * @param task
     * @param execution
     * @return
     */
    List<String> getCandidateGroups(TaskService taskService, FlowSubmitInfoDTO flowSubmitInfoDTO, String assignee, String owner, List<String> candidateUsers, List<String> candidateGroups, TaskEntity task, DelegateExecution execution);
}
