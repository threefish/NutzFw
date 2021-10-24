/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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

    FREE_CHOICE("自由选择"),

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
