/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.flow.executor;

import com.nutzfw.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.nutzfw.core.plugin.flowable.enums.FormType;
import com.nutzfw.core.plugin.flowable.extmodel.FormElementModel;
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
     * 加载表单页面配置
     *
     * @param flowTaskVO
     * @return 表单路径
     */
   default FormElementModel getFormElementModel(FlowTaskVO flowTaskVO){
       FormElementModel formElementModel = new FormElementModel();
       formElementModel.setFormKey(getFormPage(flowTaskVO));
       formElementModel.setFormType(FormType.DEVELOP);
       return formElementModel;
   }

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

    /**
     * 前端设计器中进行直接选取使用
     * 唯一
     * @return
     */
    String getUniqueName();


}
