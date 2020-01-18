/*
 * Copyright (c) 2019- 2020 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2020/01/18 17:19:18
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.elbeans.multiinstance.listener;

import com.nutzfw.core.plugin.flowable.constant.FlowConstant;
import com.nutzfw.core.plugin.flowable.elbeans.IocElBeans;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.service.delegate.DelegateTask;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/1/18
 */
@IocBean(args = {"refer:$ioc"})
public class MultiInstanceCompleteTaskListener implements TaskListener, IocElBeans {

    private Ioc ioc;

    public MultiInstanceCompleteTaskListener(Ioc ioc) {
        this.ioc = ioc;
    }

    private RuntimeService runtimeService() {
        return ioc.getByType(RuntimeService.class);
    }


    @Override
    public void notify(DelegateTask delegateTask) {
        Execution execution = runtimeService().createExecutionQuery().executionId(delegateTask.getExecutionId()).singleResult();
        String parentExecutionId = execution.getParentId();
        HashMap localVar = new HashMap(1);
        Boolean result = (Boolean) delegateTask.getVariable(FlowConstant.AUDIT_PASS);
        int rejectedCount = getVar(delegateTask, FlowConstant.MULTIINSTANCE_REJECT_COUNTER);
        int agreeCount = getVar(delegateTask, FlowConstant.MULTIINSTANCE_AGREE_COUNTER);
        if (Boolean.FALSE.equals(result)) {
            localVar.put(FlowConstant.MULTIINSTANCE_REJECT_COUNTER, ++rejectedCount);
        } else {
            localVar.put(FlowConstant.MULTIINSTANCE_AGREE_COUNTER, ++agreeCount);
        }
        runtimeService().setVariablesLocal(parentExecutionId, localVar);
    }


    /**
     * 取得变量
     *
     * @param delegateTask
     * @return
     */
    private int getVar(DelegateTask delegateTask, String varName) {
        Integer variable = (Integer) delegateTask.getVariable(varName);
        return Objects.isNull(variable) ? 0 : variable;
    }

}

