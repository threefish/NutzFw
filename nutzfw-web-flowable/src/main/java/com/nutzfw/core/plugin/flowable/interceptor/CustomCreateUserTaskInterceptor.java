/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.interceptor;

import com.nutzfw.core.common.javascript.JsContex;
import com.nutzfw.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.nutzfw.core.plugin.flowable.service.FlowCacheService;
import com.nutzfw.core.plugin.flowable.service.FlowProcessDefinitionService;
import com.nutzfw.core.plugin.flowable.service.FlowTaskService;
import com.nutzfw.modules.flow.executor.ExternalFormExecutor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.interceptor.CreateUserTaskAfterContext;
import org.flowable.engine.interceptor.CreateUserTaskBeforeContext;
import org.flowable.engine.interceptor.CreateUserTaskInterceptor;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import java.util.Map;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/8/27
 */
@IocBean
@Slf4j
public class CustomCreateUserTaskInterceptor implements CreateUserTaskInterceptor {
    @Inject("refer:$ioc")
    Ioc ioc;
    /**
     * 此处不能直接注入 flowCacheService 的原因是初始化时会产生循环注入，最终无法启动
     *
     * @see com.nutzfw.core.plugin.flowable.listener.EventListenerHandle
     */
    FlowCacheService flowCacheService;
    FlowTaskService flowTaskService;
    FlowProcessDefinitionService flowProcessDefinitionService;
    boolean inited = false;

    public void create() {
        if (inited == false) {
            this.flowCacheService = ioc.getByType(FlowCacheService.class);
            this.flowProcessDefinitionService = ioc.getByType(FlowProcessDefinitionService.class);
            this.flowTaskService = ioc.getByType(FlowTaskService.class);
            inited = true;
        }
    }

    @Override
    public void beforeCreateUserTask(CreateUserTaskBeforeContext context) {
        this.create();
        Context ctx = new Context(
                context.getExecution().getProcessDefinitionId(),
                context.getUserTask().getId(),
                context.getExecution().getProcessInstanceBusinessKey(),
                context.getUserTask(),
                context.getExecution(),
                true,
                false,
                null);
        this.execute(ctx);
    }

    @Override
    public void afterCreateUserTask(CreateUserTaskAfterContext context) {
        Context ctx = new Context(
                context.getExecution().getProcessDefinitionId(),
                context.getUserTask().getId(),
                context.getExecution().getProcessInstanceBusinessKey(),
                context.getUserTask(),
                context.getExecution(),
                false,
                true,
                context.getTaskEntity());
        this.execute(ctx);
    }

    private void execute(Context ctx) {
        UserTaskExtensionDTO dto = flowProcessDefinitionService.getUserTaskExtension(ctx.getTaskDefinitionKey(), ctx.getProcessDefinitionId());
        ExternalFormExecutor externalFormExecutor = flowProcessDefinitionService.getExternalFormExecutor(ctx.getProcessDefinitionId());
        if (ctx.isBefore()) {
            if (Strings.isNotBlank(dto.getBeforeCreateCurrentTaskFormDataDynamicAssignment())) {
                Map formData = externalFormExecutor.loadFormData(ctx.getProcessInstanceBusinessKey());
                this.beforeEvalJs(formData, dto);
                externalFormExecutor.insertOrUpdateFormData(formData);
            }
            //创建用户任务前执行
            externalFormExecutor.beforeCreateUserTask(ctx.getExecution(), ctx.getUserTask(), dto, ctx.getProcessInstanceBusinessKey());
        }
        if (ctx.isAfter()) {
            if (Strings.isNotBlank(dto.getAfterCreateCurrentTaskFormDataDynamicAssignment())) {
                Map formData = externalFormExecutor.loadFormData(ctx.getProcessInstanceBusinessKey());
                this.afterEvalJs(formData, dto, ctx.getTaskEntity());
                externalFormExecutor.insertOrUpdateFormData(formData);
            }
            //创建用户任务前执行
            externalFormExecutor.afterCreateUserTask(ctx.getExecution(), ctx.getUserTask(), dto, ctx.getProcessInstanceBusinessKey(), ctx.getTaskEntity());
        }
    }

    /**
     * @param formData
     * @param dto
     * @return
     */
    private Map beforeEvalJs(Map formData, UserTaskExtensionDTO dto) {
        StringBuffer jsCode = new StringBuffer("function runBeforeCreateCurrentTaskFormDataDynamicAssignment(formData,dto){ " + dto.getBeforeCreateCurrentTaskFormDataDynamicAssignment() + "  return formData; }");
        try {
            JsContex.get().compile(jsCode.toString());
            JsContex.get().eval(jsCode.toString());
            Object result = JsContex.get().invokeFunction("runBeforeCreateCurrentTaskFormDataDynamicAssignment", formData, dto);
            formData = (Map) result;
        } catch (Exception e) {
            log.error("解析动态JS错误", e);
            throw new RuntimeException("解析动态JS错误");
        }
        return formData;
    }

    /**
     * @param formData
     * @param dto
     * @return
     */
    private Map afterEvalJs(Map formData, UserTaskExtensionDTO dto, TaskEntity taskEntity) {
        StringBuffer jsCode = new StringBuffer("function runAfterCreateCurrentTaskFormDataDynamicAssignment(formData,dto,task){ " + dto.getAfterCreateCurrentTaskFormDataDynamicAssignment() + "  return formData; }");
        try {
            JsContex.get().compile(jsCode.toString());
            JsContex.get().eval(jsCode.toString());
            Object result = JsContex.get().invokeFunction("runAfterCreateCurrentTaskFormDataDynamicAssignment", formData, dto, taskEntity);
            formData = (Map) result;
        } catch (Exception e) {
            log.error("解析动态JS错误", e);
            throw new RuntimeException("解析动态JS错误");
        }
        return formData;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Context {
        String processDefinitionId;
        String taskDefinitionKey;
        String processInstanceBusinessKey;
        UserTask userTask;
        DelegateExecution execution;
        boolean before;
        boolean after;
        TaskEntity taskEntity;
    }
}
