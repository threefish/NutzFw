/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
