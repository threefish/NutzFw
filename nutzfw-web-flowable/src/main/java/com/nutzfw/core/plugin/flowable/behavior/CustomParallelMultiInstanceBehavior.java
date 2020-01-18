/*
 * Copyright (c) 2019- 2020 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2020/01/18 18:46:18
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.behavior;

import org.flowable.bpmn.model.Activity;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.flowable.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/1/18
 */
public class CustomParallelMultiInstanceBehavior extends ParallelMultiInstanceBehavior {

    CustomMultiInstanceBehaviorHelper customMultiInstanceBehaviorHelper;

    public CustomParallelMultiInstanceBehavior(Activity activity, AbstractBpmnActivityBehavior originalActivityBehavior) {
        super(activity, originalActivityBehavior);
        customMultiInstanceBehaviorHelper = new CustomMultiInstanceBehaviorHelper(this, activity);
    }

    /**
     * Handles the parallel case of spawning the instances. Will create child executions accordingly for every instance needed.
     */
    @Override
    protected int createInstances(DelegateExecution multiInstanceRootExecution) {
        customMultiInstanceBehaviorHelper.createMultiInstances(multiInstanceRootExecution);
        return super.createInstances(multiInstanceRootExecution);
    }


    /**
     * 多实例执行完成，已满足条件
     * 后端执行完成当前任务后脚本
     *
     * @param execution
     */
    @Override
    protected void cleanupMiRoot(DelegateExecution execution) {
        customMultiInstanceBehaviorHelper.runBackstageCompletingCurrentTaskAfterAndUpdateFormData(execution);
        super.cleanupMiRoot(execution);
    }

}
