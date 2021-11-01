package com.nutzfw.core.plugin.flowable.listener;

import com.nutzfw.core.plugin.flowable.constant.FlowConstant;
import com.nutzfw.core.plugin.flowable.context.ProcessContext;
import com.nutzfw.core.plugin.flowable.context.ProcessContextHolder;
import com.nutzfw.core.plugin.flowable.enums.ProcessStatus;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.delegate.event.FlowableProcessStartedEvent;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.engine.delegate.event.impl.FlowableProcessStartedEventImpl;

import java.util.Objects;

/**
 * @author huchuc@vip.qq.com
 * @date: 2021/10/19
 */
@Slf4j
public class ProccessStratAndCompletedListener extends AbstractFlowableEngineEventListener {

    /**
     * 流程启动时
     *
     * @param event
     */
    @Override
    protected void processStarted(FlowableProcessStartedEvent event) {
        if (event instanceof FlowableProcessStartedEventImpl) {
            FlowableProcessStartedEventImpl entityEvent = (FlowableProcessStartedEventImpl) event;
            final DelegateExecution execution = entityEvent.getExecution();
            log.debug("流程开始: {}", entityEvent.getProcessInstanceId());
            execution.setVariable(FlowConstant.PROCESS_STATUS, ProcessStatus.UNDER_REVIEW);
        }
    }

    /**
     * 流程完成时
     *
     * @param event
     */
    @Override
    protected void processCompleted(FlowableEngineEntityEvent event) {
        if (event instanceof FlowableEntityEventImpl) {
            FlowableEntityEventImpl entityEvent = (FlowableEntityEventImpl) event;
            final DelegateExecution execution = entityEvent.getExecution();
            Object variable = entityEvent.getExecution().getVariable(FlowConstant.AUDIT_PASS);
            ProcessContext processContext = ProcessContextHolder.get();
            processContext.setProcessStatus(ProcessStatus.UNDER_REVIEW);
            processContext.setProcessCompleted(true);
            if (Objects.nonNull(variable)) {
                if (variable instanceof Boolean) {
                    if ((Boolean) variable) {
                        processContext.setProcessStatus(ProcessStatus.IS_PASSED);
                    } else {
                        processContext.setProcessStatus(ProcessStatus.NOT_PASS);
                    }
                }
            }
            execution.setVariable(FlowConstant.PROCESS_STATUS, processContext.getProcessStatus());
            if (log.isDebugEnabled()) {
                log.debug("流程正常结束 {}", processContext.getProcessStatus());
            }
        }
    }


}
