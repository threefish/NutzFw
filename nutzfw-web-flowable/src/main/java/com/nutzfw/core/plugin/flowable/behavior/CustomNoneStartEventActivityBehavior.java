package com.nutzfw.core.plugin.flowable.behavior;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.behavior.NoneStartEventActivityBehavior;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/8/23
 */
@Slf4j
public class CustomNoneStartEventActivityBehavior extends NoneStartEventActivityBehavior {

    @Override
    public void execute(DelegateExecution execution) {
        this.process(execution);
        super.leave(execution);
    }

    public void process(DelegateExecution execution) {
        if (log.isDebugEnabled()) {
            log.debug("流程开始" + execution.getProcessInstanceId());
        }
    }
}
