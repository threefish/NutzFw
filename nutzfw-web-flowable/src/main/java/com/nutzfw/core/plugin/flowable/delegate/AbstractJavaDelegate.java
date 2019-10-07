/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
            String businessKeyId = flowProcessDefinitionService.getBusinessKeyId(execution.getProcessInstanceId());
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

