package com.nutzfw.core.plugin.flowable.listener.handle;

import com.nutzfw.core.plugin.flowable.listener.EventListenerHandle;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.*;
import org.nutz.ioc.Ioc;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/30
 * 帮助把流程需要的service初始化好
 */
public abstract class BaseEventListenerHandle implements EventListenerHandle {

    protected Ioc ioc;

    protected RuntimeService runtimeService;

    protected IdentityService identityService;

    protected TaskService taskService;

    protected HistoryService historyService;

    protected ManagementService managementService;

    protected FormService formService;

    protected DynamicBpmnService dynamicBpmnService;

    private boolean inited = false;

    public BaseEventListenerHandle(Ioc ioc) {
        this.ioc = ioc;
    }

    public void init() {
        if (this.inited == false) {
            this.runtimeService = ioc.getByType(RuntimeService.class);
            this.identityService = ioc.getByType(IdentityService.class);
            this.taskService = ioc.getByType(TaskService.class);
            this.historyService = ioc.getByType(HistoryService.class);
            this.managementService = ioc.getByType(ManagementService.class);
            this.formService = ioc.getByType(FormService.class);
            this.dynamicBpmnService = ioc.getByType(DynamicBpmnService.class);
            this.inited = true;
        }
    }

    @Override
    public void onEvent(FlowableEvent event) {
        this.init();
        this.execute(event);
    }

    /**
     * 执行事件
     *
     * @param event
     */
    public abstract void execute(FlowableEvent event);
}
