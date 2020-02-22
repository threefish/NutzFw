/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.validator;

import com.nutzfw.core.plugin.flowable.converter.CustomUserTaskJsonConverter;
import com.nutzfw.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.nutzfw.core.plugin.flowable.enums.TaskReviewerScopeEnum;
import org.apache.commons.collections.CollectionUtils;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.validation.ValidationError;
import org.flowable.validation.validator.Problems;
import org.flowable.validation.validator.impl.UserTaskValidator;
import org.nutz.json.Json;
import org.nutz.lang.Strings;

import java.util.List;
import java.util.Objects;


/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/2
 */
public class CustomUserTaskValidator extends UserTaskValidator {

    @Override
    protected void executeValidation(BpmnModel bpmnModel, Process process, List<ValidationError> errors) {
        List<UserTask> userTasks = process.findFlowElementsOfType(UserTask.class);
        for (UserTask userTask : userTasks) {
            if (userTask.getTaskListeners() != null) {
                for (FlowableListener listener : userTask.getTaskListeners()) {
                    if (listener.getImplementation() == null || listener.getImplementationType() == null) {
                        addError(errors, Problems.USER_TASK_LISTENER_IMPLEMENTATION_MISSING, process, userTask, "Element 'class' or 'expression' is mandatory on executionListener");
                    }
                }
            }
            List<ExtensionElement> extensionElements = userTask.getExtensionElements().get(CustomUserTaskJsonConverter.USER_TASK_EXTENSION_ELEMENT_NAME);
            if (CollectionUtils.isNotEmpty(extensionElements)) {
                ExtensionElement extensionElement = extensionElements.stream().filter(element -> Objects.equals(element.getName(), CustomUserTaskJsonConverter.USER_TASK_EXTENSION_ELEMENT_NAME)).findFirst().orElse(null);
                if (extensionElement == null) {
                    addError(errors, Problems.USER_TASK_LISTENER_IMPLEMENTATION_MISSING, process, userTask, "自定义扩展属性必须设置");
                } else {
                    String elementTextValue = extensionElement.getElementText();
                    try {
                        checkTaskReviewerScope(errors, process, userTask, Json.fromJson(UserTaskExtensionDTO.class, elementTextValue));
                    } catch (Exception e) {
                        addError(errors, Problems.USER_TASK_LISTENER_IMPLEMENTATION_MISSING, process, userTask, e.getLocalizedMessage());
                    }
                }
            }
        }
    }

    private void checkTaskReviewerScope(List<ValidationError> errors, Process process, UserTask userTask, UserTaskExtensionDTO dto) {
        if (dto.getTaskReviewerScope() == null) {
            addError(errors, Problems.USER_TASK_LISTENER_IMPLEMENTATION_MISSING, process, userTask, "请指定当前用户步骤任务审核人范围");
            return;
        }
        if (dto.getTaskReviewerScope() == TaskReviewerScopeEnum.SINGLE_USER && Strings.isBlank(dto.getAssignee())) {
            addError(errors, Problems.USER_TASK_LISTENER_IMPLEMENTATION_MISSING, process, userTask, "指定用户不能为空");
            return;
        }
        if (dto.getTaskReviewerScope() == TaskReviewerScopeEnum.MULTIPLE_USERS && CollectionUtils.isEmpty(dto.getCandidateUsers())) {
            addError(errors, Problems.USER_TASK_LISTENER_IMPLEMENTATION_MISSING, process, userTask, "候选用户不能为空");
            return;
        }
        if (dto.getTaskReviewerScope() == TaskReviewerScopeEnum.USER_ROLE_GROUPS && CollectionUtils.isEmpty(dto.getCandidateGroups())) {
            addError(errors, Problems.USER_TASK_LISTENER_IMPLEMENTATION_MISSING, process, userTask, "候选用户角色组不能为空");
            return;
        }
        if (dto.getTaskReviewerScope() == TaskReviewerScopeEnum.JAVA_BEAN_ASSIGNMENT && Strings.isBlank(dto.getIocFlowAssignment())) {
            addError(errors, Problems.USER_TASK_LISTENER_IMPLEMENTATION_MISSING, process, userTask, "JavaIocBean人员选择器不能为空");
            return;
        }
    }
}
