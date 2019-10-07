/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.service;

import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/29
 */
public interface FlowCacheService {
    /**
     * 取得流程定义缓存
     *
     * @param processDefinitionId
     * @return
     */
    ProcessDefinition getProcessDefinitionCache(String processDefinitionId);

    /**
     * 取得部署流程缓存
     *
     * @param deploymentId
     * @return
     */
    Deployment getDeploymentCache(String deploymentId);

    void delCache();
}
