package com.nutzfw.modules.flow.executor;

import com.nutzfw.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.nutzfw.core.plugin.flowable.vo.FlowTaskVO;
import com.nutzfw.modules.organize.entity.UserAccount;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import java.util.Map;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/6/20
 */
public interface ExternalFormExecutor {

    /**
     * 开始流程
     *
     * @param formData
     * @param flowTaskVO
     * @param sessionUserAccount
     * @return businessId 业务流水号
     */
    Map start(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount);


    /**
     * 用户审核
     *
     * @param formData
     * @param flowTaskVO
     * @param sessionUserAccount
     * @return 错误消息
     */
    String userAudit(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount);

    /**
     * 回退
     *
     * @param formData
     * @param flowTaskVO
     * @param sessionUserAccount
     * @return 错误消息
     */
    String backToStep(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount);

    /**
     * 加签
     *
     * @param formData
     * @param flowTaskVO
     * @param sessionUserAccount
     * @return 错误消息
     */
    String addMultiInstance(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount);

    /**
     * 加载表单数据
     *
     * @param flowTaskVO
     * @param sessionUserAccount
     * @return 表单数据
     */
    Object loadFormData(FlowTaskVO flowTaskVO, UserAccount sessionUserAccount);

    /**
     * 加载表单数据
     *
     * @param businessKeyId
     * @return 表单数据
     */
    Map loadFormData(String businessKeyId);

    /**
     * 插入表单数据
     *
     * @param formData
     * @return 表单数据
     */
    Object insertOrUpdateFormData(Map formData);

    /**
     * 加载表单页面
     *
     * @param flowTaskVO
     * @return 表单路径
     */
    String getFormPage(FlowTaskVO flowTaskVO);

    /**
     * 创建用户任务前执行-也可以使用动态脚本实现
     *
     * @param execution
     * @param userTask
     * @param dto
     * @param processInstanceBusinessKey
     */
    void beforeCreateUserTask(DelegateExecution execution, UserTask userTask, UserTaskExtensionDTO dto, String processInstanceBusinessKey);

    /**
     * 创建用户任务后执行-也可以使用动态脚本实现
     *
     * @param execution
     * @param userTask
     * @param dto
     * @param processInstanceBusinessKey
     */
    void afterCreateUserTask(DelegateExecution execution, UserTask userTask, UserTaskExtensionDTO dto, String processInstanceBusinessKey, TaskEntity taskEntity);
}
