package com.nutzfw.core.plugin.flowable.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/26
 */
@Getter
@AllArgsConstructor
public enum TaskReviewerScopeEnum {

    DEPARTMENT_HEAD("部门主管领导(如果自己就是部门主管领导则分配给再上级部门主管办理)"),

    DEPARTMENT_LEADER("部门分管领导(如果自己就是部门分管领导则分配给部门主管领导办理)"),

    FLOW_SUBMITTER("流程发起人"),

    USER_ROLE_GROUPS("候选用户角色组"),

    SINGLE_USER("分配给指定用户"),

    MULTIPLE_USERS("多个候选用户"),

    JAVA_BEAN_ASSIGNMENT("由JavaIocBean人员选择器选取审核人员或组");

    static HashMap<String, TaskReviewerScopeEnum> lookup = new HashMap<>();

    static {
        for (TaskReviewerScopeEnum typeEnum : EnumSet.allOf(TaskReviewerScopeEnum.class)) {
            lookup.put(typeEnum.toString(), typeEnum);
        }
    }

    String value;

    public static TaskReviewerScopeEnum get(String value) {
        return lookup.get(value);
    }


}
