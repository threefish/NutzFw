/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
