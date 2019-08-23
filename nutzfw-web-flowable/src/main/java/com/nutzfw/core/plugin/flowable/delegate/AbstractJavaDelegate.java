package com.nutzfw.core.plugin.flowable.delegate;

import com.nutzfw.core.plugin.flowable.service.FlowProcessDefinitionService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.nutz.ioc.Ioc;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/17
 */
@Slf4j
public abstract class AbstractJavaDelegate implements JavaDelegate {

    protected Ioc ioc;

    protected FlowProcessDefinitionService flowProcessDefinitionService;

    public AbstractJavaDelegate(Ioc ioc) {
        this.ioc = ioc;
        flowProcessDefinitionService = this.ioc.getByType(FlowProcessDefinitionService.class);
    }

    @Override
    public void execute(DelegateExecution execution) {
        try {
            String businessKeyId = flowProcessDefinitionService.getBusinessInfo(execution.getProcessInstanceId());
            this.execute(execution, businessKeyId);
        } catch (Throwable e) {
            log.error("执行自动任务失败！", e);
        }
    }

    /**
     * 执行业务一系列自动化任务 -- 可以推送、发邮件、发通知、数据入库、等等
     *
     * @param execution     原始执行信息
     * @param businessKeyId 业务信息
     */
    public abstract void execute(DelegateExecution execution, String businessKeyId);
}

