package com.nutzfw.core.plugin.flowable.service;

import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.core.plugin.flowable.dto.UserTaskExtensionDTO;
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

    String getBusinessInfo(String procInsId);

    FlowElement findEndFlowElement(String processDefId);

    Process findMainProcess(String processDefId);

    String findExternalFormExecutor(String processDefId);

    FlowElement getFlowElementByActivityIdAndProcessDefinitionId(String activityId, String processDefinitionId);

    UserTask getUserTask(String activityId, String processDefinitionId);

    UserTaskExtensionDTO getUserTaskExtension(String activityId, String processDefinitionId);
}
