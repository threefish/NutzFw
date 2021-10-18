package com.nutzfw.core.plugin.flowable;

import com.nutzfw.core.plugin.flowable.service.FlowProcessDefinitionService;
import com.nutzfw.core.plugin.flowable.service.FlowTaskService;
import org.flowable.engine.*;
import org.nutz.dao.Dao;
import org.nutz.mvc.Mvcs;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2021/10/14
 */
public abstract class FlowServiceSupport {


    public static final RuntimeService runtimeService() {
        return Mvcs.getActionContext().getIoc().get(RuntimeService.class);
    }

    public static final TaskService taskService() {
        return Mvcs.getActionContext().getIoc().get(TaskService.class);
    }

    public static final FormService formService() {
        return Mvcs.getActionContext().getIoc().get(FormService.class);
    }

    public static final HistoryService historyService() {
        return Mvcs.getActionContext().getIoc().get(HistoryService.class);
    }

    public static final RepositoryService repositoryService() {
        return Mvcs.getActionContext().getIoc().get(RepositoryService.class);
    }

    public static final ManagementService managementService() {
        return Mvcs.getActionContext().getIoc().get(ManagementService.class);
    }

    public static final IdentityService identityService() {
        return Mvcs.getActionContext().getIoc().get(IdentityService.class);
    }

    public static final DynamicBpmnService dynamicBpmnService() {
        return Mvcs.getActionContext().getIoc().get(DynamicBpmnService.class);
    }

    public static final FlowProcessDefinitionService flowProcessDefinitionService() {
        return Mvcs.getActionContext().getIoc().get(FlowProcessDefinitionService.class);
    }

    public static final FlowTaskService flowTaskService() {
        return Mvcs.getActionContext().getIoc().get(FlowTaskService.class);
    }

    public static final Dao dao() {
        return Mvcs.getActionContext().getIoc().get(Dao.class);
    }

}
