package com.nutzfw.core.plugin.flowable.behavior;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.behavior.NoneEndEventActivityBehavior;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/8/23
 */
@Slf4j
public class CustomNoneEndEventActivityBehavior extends NoneEndEventActivityBehavior {

    @Override
    public void execute(DelegateExecution execution) {
        this.process(execution);
        super.execute(execution);
    }

    public void process(DelegateExecution execution) {
        if (log.isDebugEnabled()) {
            log.debug("流程结束" + execution.getProcessInstanceId());
        }
    }
}
