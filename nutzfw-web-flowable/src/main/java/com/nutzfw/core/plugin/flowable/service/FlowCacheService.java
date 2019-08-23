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
