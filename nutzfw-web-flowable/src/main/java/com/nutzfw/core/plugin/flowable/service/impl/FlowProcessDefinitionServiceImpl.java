/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */
package com.nutzfw.core.plugin.flowable.service.impl;

import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.core.plugin.flowable.config.NutzFwProcessEngineConfiguration;
import com.nutzfw.core.plugin.flowable.converter.CustomBpmnJsonConverter;
import com.nutzfw.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.nutzfw.core.plugin.flowable.service.FlowCacheService;
import com.nutzfw.core.plugin.flowable.service.FlowProcessDefinitionService;
import com.nutzfw.core.plugin.flowable.service.FlowTaskService;
import com.nutzfw.core.plugin.flowable.util.FlowUtils;
import com.nutzfw.core.plugin.flowable.vo.ProcessDefinitionEntitVO;
import com.nutzfw.modules.flow.executor.ExternalFormExecutor;
import com.nutzfw.modules.flow.service.FlowTypeService;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntityImpl;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/10
 */
@IocBean
public class FlowProcessDefinitionServiceImpl implements FlowProcessDefinitionService {

    @Inject
    FlowTypeService                  flowTypeService;
    @Inject
    NutzFwProcessEngineConfiguration nutzFwProcessEngineConfiguration;
    @Inject
    FlowTaskService                  flowTaskService;
    @Inject
    FlowCacheService                 flowCacheService;
    @Inject
    RepositoryService                repositoryService;
    @Inject
    RuntimeService                   runtimeService;
    @Inject("refer:$ioc")
    Ioc                              ioc;

    /**
     * 流程定义列表
     */
    @Override
    public LayuiTableDataListVO processList(LayuiTableDataListVO vo, String category) {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().latestVersion().orderByProcessDefinitionKey().asc();
        if (Strings.isNotBlank(category)) {
            processDefinitionQuery.processDefinitionCategory(category);
        }
        vo.setCount((int) processDefinitionQuery.count());
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.listPage(vo.getFirstResult(), vo.getPageSize());
        for (ProcessDefinition processDefinition : processDefinitionList) {
            String deploymentId = processDefinition.getDeploymentId();
            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
            ProcessDefinitionEntitVO processDefinitionEntitVO = new ProcessDefinitionEntitVO((ProcessDefinitionEntityImpl) processDefinition);
            processDefinitionEntitVO.setCategory(deployment.getCategory());
            processDefinitionEntitVO.setCategoryName(flowTypeService.fetchCategoryName(processDefinitionEntitVO.getCategory()));
            processDefinitionEntitVO.setDeploymentTime(deployment.getDeploymentTime());
            vo.getData().add(processDefinitionEntitVO);
        }
        return vo;
    }

    /**
     * 读取资源，通过部署ID
     *
     * @param processDefinitionId 流程定义ID
     * @param processInstanceId   流程实例ID
     * @param resourceType        资源类型(xml|image)
     */
    @Override
    public InputStream resourceRead(String processDefinitionId, String processInstanceId, String resourceType) {
        InputStream resourceAsStream = null;
        if (Objects.equals("image", resourceType)) {
            String fontName = nutzFwProcessEngineConfiguration.getAnnotationFontName();
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
            DefaultProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
            resourceAsStream = diagramGenerator.generateDiagram(bpmnModel, "png", fontName, fontName, fontName,
                    null, 1.0, true);
        } else if (Objects.equals("xml", resourceType)) {
            if (Strings.isBlank(processDefinitionId)) {
                ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
                processDefinitionId = processInstance.getProcessDefinitionId();
            }
            ProcessDefinition processDefinition = flowCacheService.getProcessDefinitionCache(processDefinitionId);
            resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getResourceName());
        }
        return resourceAsStream;
    }

    /**
     * 挂起、激活流程实例
     */
    @Override
    public String updateState(String state, String procDefId) {
        if (state.equals("active")) {
            repositoryService.activateProcessDefinitionById(procDefId, true, null);
            return "已激活ID为[" + procDefId + "]的流程定义。";
        } else if (state.equals("suspend")) {
            repositoryService.suspendProcessDefinitionById(procDefId, true, null);
            return "已挂起ID为[" + procDefId + "]的流程定义。";
        }
        return "无操作";
    }

    /**
     * 删除部署的流程，级联删除流程实例
     *
     * @param deploymentId 流程部署ID
     */
    @Override
    public void deleteDeployment(String deploymentId) {
        repositoryService.deleteDeployment(deploymentId, true);
    }


    /**
     * @param procInsId
     * @return businessTable = ss[0];  businessId = ss[1];
     */
    @Override
    public String getBusinessKeyId(String procInsId) {
        ProcessInstance procIns = flowTaskService.getProcIns(procInsId);
        if (procIns != null) {
            return procIns.getBusinessKey();
        } else {
            HistoricProcessInstance history = flowTaskService.getHistoryProcIns(procInsId);
            if (history != null) {
                return history.getBusinessKey();
            }
        }
        return null;
    }

    /**
     * 获取end节点
     *
     * @param processDefId
     * @return FlowElement
     */
    @Override
    public FlowElement findEndFlowElement(String processDefId) {
        Process process = repositoryService.getBpmnModel(processDefId).getMainProcess();
        Collection<FlowElement> list = process.getFlowElements();
        for (FlowElement f : list) {
            if (f instanceof EndEvent) {
                return f;
            }
        }
        return null;
    }


    /**
     * 获取MainProcess
     *
     * @param processDefId
     * @return FlowElement
     */
    @Override
    public Process findMainProcess(String processDefId) {
        return repositoryService.getBpmnModel(processDefId).getMainProcess();
    }

    /**
     * 获取外部表单执行器表达式
     *
     * @param processDefId
     * @return FlowElement
     */
    @Override
    public String findExternalFormExecutor(String processDefId) {
        Process process = findMainProcess(processDefId);
        AtomicReference<String> externalFormExecutor = new AtomicReference<>();
        process.getExtensionElements().forEach((key, elements) -> {
            if (CustomBpmnJsonConverter.EXTERNAL_FORM_EXECUTOR.equals(key)) {
                elements.stream().filter(extensionElement -> CustomBpmnJsonConverter.EXTERNAL_FORM_EXECUTOR.equals(extensionElement.getName())).forEach(extensionElement -> {
                    externalFormExecutor.set(extensionElement.getElementText());
                });
            }
        });
        return externalFormExecutor.get();
    }

    /**
     * 获取外部表单执行器
     *
     * @param processDefId
     * @return ExternalFormExecutor
     */
    @Override
    public ExternalFormExecutor getExternalFormExecutor(String processDefId) {
        ExternalFormExecutor executor;
        try {
            String externalFormExecutor = findExternalFormExecutor(processDefId);
            if (externalFormExecutor.startsWith("$ioc:")) {
                executor = ioc.get(ExternalFormExecutor.class, externalFormExecutor.substring(5));
            } else {
                throw new RuntimeException("表达式错误！");
            }
        } catch (Exception e) {
            throw new RuntimeException("获取外部表单执行器失败！", e);
        }
        return executor;
    }


    /**
     * 获取指定节点的节点信息
     *
     * @param activityId          当前步骤ID
     * @param processDefinitionId 流程定义ID
     * @return
     */
    @Override
    public FlowElement getFlowElementByActivityIdAndProcessDefinitionId(String activityId, String processDefinitionId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        List<Process> processes = bpmnModel.getProcesses();
        if (CollectionUtils.isNotEmpty(processes)) {
            for (Process process : processes) {
                FlowElement flowElement = process.getFlowElement(activityId);
                if (flowElement != null) {
                    return flowElement;
                }
            }
        }
        return null;
    }

    /**
     * 取得用户节点
     *
     * @param activityId
     * @param processDefinitionId
     * @return
     */
    @Override
    public UserTask getUserTask(String activityId, String processDefinitionId) {
        FlowElement flowElement = getFlowElementByActivityIdAndProcessDefinitionId(activityId, processDefinitionId);
        if (flowElement != null && flowElement instanceof UserTask) {
            return (UserTask) flowElement;
        }
        return null;
    }

    /**
     * 取得用户节点
     *
     * @param activityId
     * @param processDefinitionId
     * @return
     */
    @Override
    public UserTaskExtensionDTO getUserTaskExtension(String activityId, String processDefinitionId) {
        UserTask userTask = getUserTask(activityId, processDefinitionId);
        return FlowUtils.getUserTaskExtension(userTask);
    }

}
