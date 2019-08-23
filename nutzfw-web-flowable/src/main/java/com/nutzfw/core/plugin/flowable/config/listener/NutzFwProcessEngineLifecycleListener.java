package com.nutzfw.core.plugin.flowable.config.listener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineLifecycleListener;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/6/28
 */
@Slf4j
public class NutzFwProcessEngineLifecycleListener implements ProcessEngineLifecycleListener {

    @Override
    public void onProcessEngineBuilt(ProcessEngine processEngine) {
        log.info("流程引擎已经构建完毕！");
    }

    @Override
    public void onProcessEngineClosed(ProcessEngine processEngine) {
        log.info("流程引擎已经关闭！");
    }
}
