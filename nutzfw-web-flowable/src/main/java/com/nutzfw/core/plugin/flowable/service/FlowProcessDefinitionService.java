/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.service;

import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.nutzfw.modules.flow.executor.ExternalFormExecutor;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.UserTask;

import java.io.InputStream;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/17
 */
public interface FlowProcessDefinitionService {
    /**
     * 流程定义列表
     */
    LayuiTableDataListVO processList(LayuiTableDataListVO vo, String category);
    LayuiTableDataListVO processList(LayuiTableDataListVO vo, String category,String roleId);

    /**
     * 读取资源，通过部署ID
     *
     * @param processDefinitionId 流程定义ID
     * @param processInstanceId   流程实例ID
     * @param resourceType        资源类型(xml|image)
     */
    InputStream resourceRead(String processDefinitionId, String processInstanceId, String resourceType);

    /**
     * 挂起、激活流程实例
     *
     * @param state
     * @param procDefId
     * @return
     */
    String updateState(String state, String procDefId);

    /**
     * 删除部署的流程，级联删除流程实例
     *
     * @param deploymentId 流程部署ID
     */
    void deleteDeployment(String deploymentId);

    /**
     * 取得业务表信息
     *
     * @param procInsId
     * @return
     */
    String getBusinessKeyId(String procInsId);

    /**
     * 获取end节点
     *
     * @param processDefId
     * @return FlowElement
     */
    FlowElement findEndFlowElement(String processDefId);

    /**
     * 获取MainProcess
     *
     * @param processDefId
     * @return FlowElement
     */
    Process findMainProcess(String processDefId);

    /**
     * 获取外部表单执行器表达式
     *
     * @param processDefId
     * @return FlowElement
     */
    String findExternalFormExecutor(String processDefId);

    /**
     * 获取外部表单执行器
     *
     * @param processDefId
     * @return ExternalFormExecutor
     */
    ExternalFormExecutor getExternalFormExecutor(String processDefId);

    /**
     * 获取指定节点的节点信息
     *
     * @param activityId          当前步骤ID
     * @param processDefinitionId 流程定义ID
     * @return
     */
    FlowElement getFlowElementByActivityIdAndProcessDefinitionId(String activityId, String processDefinitionId);

    /**
     * 取得用户节点
     *
     * @param activityId
     * @param processDefinitionId
     * @return
     */
    UserTask getUserTask(String activityId, String processDefinitionId);

    /**
     * 取得用户节点
     *
     * @param activityId
     * @param processDefinitionId key:version:id
     * @return
     */
    UserTaskExtensionDTO getUserTaskExtension(String activityId, String processDefinitionId);
}
