/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.listener.handle;

import com.nutzfw.core.plugin.flowable.service.FlowCacheService;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.impl.event.FlowableEntityEventImpl;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/30
 * 为流程任务设置流程类别
 */
@IocBean(args = {"refer:$ioc"})
public class TastCreateSetCategoryHandle extends BaseEventListenerHandle {
    /**
     * 此处不能直接注入 flowCacheService 的原因是初始化时会产生循环注入，最终无法启动
     *
     * @see com.nutzfw.core.plugin.flowable.listener.EventListenerHandle
     */
    FlowCacheService flowCacheService;

    public TastCreateSetCategoryHandle(Ioc ioc) {
        super(ioc);
    }

    public void create() {
        if (flowCacheService == null) {
            this.flowCacheService = ioc.getByType(FlowCacheService.class);
        }
    }

    @Override
    public void execute(FlowableEvent event) {
        if (!(event instanceof FlowableEntityEventImpl)) {
            return;
        }
        this.create();
        FlowableEntityEventImpl entityEvent = (FlowableEntityEventImpl) event;
        TaskEntityImpl task = (TaskEntityImpl) entityEvent.getEntity();
        if (Strings.isBlank(task.getCategory())) {
            ProcessDefinition pd = flowCacheService.getProcessDefinitionCache(task.getProcessDefinitionId());
            Deployment deployment = flowCacheService.getDeploymentCache(pd.getDeploymentId());
            task.setCategory(deployment.getCategory());
        }
    }


}
