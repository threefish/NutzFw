package com.nutzfw.core.plugin.flowable.service.impl;

import com.nutzfw.core.plugin.flowable.config.NutzFwProcessEngineConfiguration;
import com.nutzfw.core.plugin.flowable.service.FlowCacheService;
import com.nutzfw.core.plugin.redis.RedisHelpper;
import org.flowable.common.engine.impl.persistence.deploy.DeploymentCache;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.impl.persistence.deploy.DeploymentManager;
import org.flowable.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.Set;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/29
 */
@IocBean
public class FlowCacheServiceImpl implements FlowCacheService {

    static final String FLOW_ABLE_CACHE = "FLOW_ABLE_CACHE:";
    @Inject
    RedisHelpper redisHelpper;
    @Inject
    RepositoryService repositoryService;

    @Inject
    NutzFwProcessEngineConfiguration processEngineConfiguration;

    @Override
    public ProcessDefinition getProcessDefinitionCache(String processDefinitionId) {
        DeploymentManager deploymentManager = processEngineConfiguration.getDeploymentManager();
        DeploymentCache<ProcessDefinitionCacheEntry> processDefinitionCache = deploymentManager.getProcessDefinitionCache();
        ProcessDefinitionCacheEntry processDefinitionCacheEntry = processDefinitionCache.get(processDefinitionId);
        if (processDefinitionCacheEntry == null) {
            return this.getProcessDefinitionRenewCache(processDefinitionId);
        }
        return processDefinitionCacheEntry.getProcessDefinition();
    }

    public ProcessDefinition getProcessDefinitionRenewCache(String processDefinitionId) {
        //服务关闭后缓存中的实例就被销毁了，需要更新下缓存
        repositoryService.getBpmnModel(processDefinitionId);
        DeploymentManager deploymentManager = processEngineConfiguration.getDeploymentManager();
        DeploymentCache<ProcessDefinitionCacheEntry> processDefinitionCache = deploymentManager.getProcessDefinitionCache();
        ProcessDefinitionCacheEntry processDefinitionCacheEntry = processDefinitionCache.get(processDefinitionId);
        return processDefinitionCacheEntry.getProcessDefinition();
    }

    @Override
    public Deployment getDeploymentCache(String deploymentId) {
        String key = RedisHelpper.buildRediskey(FLOW_ABLE_CACHE, deploymentId);
        if (redisHelpper.exists(key)) {
            return redisHelpper.getBySerializable(key);
        } else {
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
            redisHelpper.setNXSerializable(key, deployment, RedisHelpper.DEFAULT_SECOND);
            return deployment;
        }
    }

    @Override
    public void delCache() {
        String key = RedisHelpper.buildRediskey(FLOW_ABLE_CACHE, "*");
        Set<String> lists = redisHelpper.keys(key);
        if (lists.size() > 0) {
            redisHelpper.del(lists.toArray(new String[]{}));
        }
    }
}
