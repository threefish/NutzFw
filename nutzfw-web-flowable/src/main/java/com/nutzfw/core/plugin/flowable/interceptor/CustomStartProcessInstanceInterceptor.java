package com.nutzfw.core.plugin.flowable.interceptor;

import com.google.common.collect.Maps;
import com.nutzfw.core.plugin.flowable.constant.FlowConstant;
import com.nutzfw.core.plugin.flowable.context.CustomStartSubProcessInstanceBeforeContext;
import com.nutzfw.core.plugin.flowable.context.ProcessContext;
import com.nutzfw.core.plugin.flowable.context.ProcessContextHolder;
import com.nutzfw.core.plugin.flowable.enums.ProcessStatus;
import com.nutzfw.core.plugin.flowable.service.FlowProcessDefinitionService;
import com.nutzfw.core.plugin.flowable.service.FlowTaskService;
import com.nutzfw.core.plugin.flowable.vo.FlowTaskVO;
import com.nutzfw.modules.flow.executor.ExternalFormExecutor;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.service.UserAccountService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.CallActivity;
import org.flowable.engine.interceptor.*;
import org.flowable.engine.repository.ProcessDefinition;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import java.util.Map;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@IocBean
@Slf4j
public class CustomStartProcessInstanceInterceptor implements StartProcessInstanceInterceptor {

    @Inject
    FlowProcessDefinitionService flowProcessDefinitionService;
    @Inject
    FlowTaskService flowTaskService;
    @Inject
    UserAccountService userAccountService;

    /**
     * 启动流程实例之前
     *
     * @param instanceContext
     */
    @Override
    public void beforeStartProcessInstance(StartProcessInstanceBeforeContext instanceContext) {

    }

    /**
     * 启动流程实例之后
     *
     * @param instanceContext
     */
    @Override
    public void afterStartProcessInstance(StartProcessInstanceAfterContext instanceContext) {

    }

    /**
     * 启动子流程之前
     *
     * @param startSubProcessInstanceBeforeContext
     */
    @Override
    public void beforeStartSubProcessInstance(StartSubProcessInstanceBeforeContext startSubProcessInstanceBeforeContext) {
        CustomStartSubProcessInstanceBeforeContext instanceContext = ((CustomStartSubProcessInstanceBeforeContext) startSubProcessInstanceBeforeContext);

        final CallActivity callActivity = instanceContext.getCallActivity();


        final ProcessContext processContext = ProcessContextHolder.get();
        final ProcessContext childprocessContext = new ProcessContext();
        final UserAccount userAccount = userAccountService.fetchByUserName(processContext.getInitiator());
        final ProcessDefinition childProcessDefinition = instanceContext.getProcessDefinition();
        FlowTaskVO flowTaskVO = new FlowTaskVO();
        flowTaskVO.setProcDefId(childProcessDefinition.getId());
        flowTaskVO.setProcDefKey(childProcessDefinition.getKey());
        ExternalFormExecutor childFormExecutor = flowProcessDefinitionService.getExternalFormExecutor(childProcessDefinition.getId());
        Map<String, Object> variables = Maps.newHashMap();
        Map<String, Object> formData = Maps.newHashMap();
        // 根据模型设计器上绑定的映射关系计算新表单数据


        flowTaskVO.setFormData(formData);
        flowTaskService.setValuedDataObject(variables, childProcessDefinition.getId(), formData, userAccount, false);
        formData = childFormExecutor.start(formData, flowTaskVO, userAccount);
        variables.put(FlowConstant.FORM_DATA, formData);
        variables.put(FlowConstant.AUDIT_PASS, true);
        String primaryKeyId = formData.getOrDefault(FlowConstant.PRIMARY_KEY, "").toString();
        if (Strings.isBlank(primaryKeyId)) {
            throw new RuntimeException("业务ID不能为空");
        }
        instanceContext.setVariables(variables);
        instanceContext.setBusinessKey(primaryKeyId);
        instanceContext.setProcessInstanceName("子流程：" + variables.getOrDefault(FlowConstant.PROCESS_TITLE, "默认子流程标题").toString());

        childprocessContext.setProcessStatus(ProcessStatus.UNDER_REVIEW);
        childprocessContext.setProcessDefId(childProcessDefinition.getId());
        childprocessContext.setProcessDefKey(childProcessDefinition.getKey());
        childprocessContext.setBusinessId(primaryKeyId);
        childprocessContext.setFlowTaskVO(flowTaskVO);
        childprocessContext.setFormData(formData);
        processContext.setChildProcessContext(childprocessContext);
    }

    /**
     * 启动子流程后
     *
     * @param instanceContext
     */
    @Override
    public void afterStartSubProcessInstance(StartSubProcessInstanceAfterContext instanceContext) {
        throw new RuntimeException("xxx");
    }
}
