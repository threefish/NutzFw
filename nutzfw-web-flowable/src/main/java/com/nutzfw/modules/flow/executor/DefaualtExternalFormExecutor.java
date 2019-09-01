package com.nutzfw.modules.flow.executor;

import com.nutzfw.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.nutzfw.core.plugin.flowable.vo.FlowTaskVO;
import com.nutzfw.modules.organize.entity.UserAccount;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.Map;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/6/20
 * 表单执行器的默认实现
 */
@IocBean(name = "defaualtExternalFormExecutor")
public class DefaualtExternalFormExecutor implements ExternalFormExecutor {

    @Override
    public Map start(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        return formData;
    }

    @Override
    public String userAudit(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        throw new RuntimeException("你应该自己实现");
    }

    @Override
    public String backToStep(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        throw new RuntimeException("你应该自己实现");
    }

    @Override
    public String addMultiInstance(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        throw new RuntimeException("你应该自己实现");
    }

    @Override
    public Object loadFormData(FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        throw new RuntimeException("你应该自己实现");
    }

    @Override
    public Map loadFormData(String businessKeyId) {
        throw new RuntimeException("你应该自己实现");
    }

    @Override
    public Object insertOrUpdateFormData(Map formData) {
        throw new RuntimeException("你应该自己实现");
    }


    @Override
    public String getFormPage(FlowTaskVO flowTaskVO) {
        throw new RuntimeException("你应该自己实现");
    }

    @Override
    public void beforeCreateUserTask(DelegateExecution execution, UserTask userTask, UserTaskExtensionDTO dto, String processInstanceBusinessKey) {

    }

    @Override
    public void afterCreateUserTask(DelegateExecution execution, UserTask userTask, UserTaskExtensionDTO dto, String processInstanceBusinessKey, TaskEntity taskEntity) {

    }


}
