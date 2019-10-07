/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.flowable.bpmn.model.*;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.persistence.entity.HistoricActivityInstanceEntityImpl;
import org.flowable.engine.runtime.Execution;
import org.flowable.ui.modeler.service.mapper.InfoMapper;
import org.flowable.ui.modeler.service.mapper.ServiceTaskInfoMapper;
import org.flowable.ui.modeler.service.mapper.UserTaskInfoMapper;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import java.util.*;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/5
 */
@Slf4j
@IocBean
public class FlowDiagramUtils {

    @Inject
    RepositoryService repositoryService;
    @Inject
    RuntimeService    runtimeService;
    @Inject
    HistoryService    historyService;
    List<String>            eventElementTypes = new ArrayList<>();
    Map<String, InfoMapper> propertyMappers   = new HashMap<>();

    public FlowDiagramUtils() {
        eventElementTypes.add("StartEvent");
        eventElementTypes.add("EndEvent");
        eventElementTypes.add("BoundaryEvent");
        eventElementTypes.add("IntermediateCatchEvent");
        eventElementTypes.add("ThrowEvent");
        propertyMappers.put("ServiceTask", new ServiceTaskInfoMapper());
        propertyMappers.put("UserTask", new UserTaskInfoMapper());
    }


    public NutMap getHistoryProcessInstanceModelJSON(String processInstanceId, String processDefinitionId) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode displayNode = objectMapper.createObjectNode();
        BpmnModel pojoModel = repositoryService.getBpmnModel(processDefinitionId);
        if (!pojoModel.getLocationMap().isEmpty()) {
            List<HistoricActivityInstance> hiActInsList = historyService.createHistoricActivityInstanceQuery()
                    .processDefinitionId(processDefinitionId)
                    .processInstanceId(processInstanceId)
                    .orderByHistoricActivityInstanceStartTime()
                    .asc()
                    .list();
            List<String> completedActivityInstances = new ArrayList<>();
            for (HistoricActivityInstance hiActIns : hiActInsList) {
                HistoricActivityInstanceEntityImpl hiActInsImpl = (HistoricActivityInstanceEntityImpl) hiActIns;
                completedActivityInstances.add(hiActInsImpl.getActivityId());
            }
            Execution execution = runtimeService.createExecutionQuery().executionId(processInstanceId).singleResult();
            List<String> currentActivityinstances = new ArrayList<>();
            if (execution != null) {
                currentActivityinstances = runtimeService.getActiveActivityIds(execution.getId());
            }
            //收集完成的节点
            List<String> completedFlows = gatherCompletedFlows(completedActivityInstances, currentActivityinstances, pojoModel);
            try {
                GraphicInfo diagramInfo = new GraphicInfo();
                Set<String> completedElements = new HashSet<>(completedActivityInstances);
                completedElements.addAll(completedFlows);
                Set<String> currentElements = new HashSet<>(currentActivityinstances);
                processProcessElements(pojoModel, displayNode, diagramInfo, completedElements, currentElements);
                displayNode.put("diagramBeginX", diagramInfo.getX());
                displayNode.put("diagramBeginY", diagramInfo.getY());
                displayNode.put("diagramWidth", diagramInfo.getWidth());
                displayNode.put("diagramHeight", diagramInfo.getHeight());
                if (completedActivityInstances != null) {
                    ArrayNode completedActivities = displayNode.putArray("completedActivities");
                    for (String completed : completedActivityInstances) {
                        completedActivities.add(completed);
                    }
                }
                if (currentActivityinstances != null) {
                    ArrayNode currentActivities = displayNode.putArray("currentActivities");
                    for (String current : currentActivityinstances) {
                        currentActivities.add(current);
                    }
                }
                if (completedFlows != null) {
                    ArrayNode completedSequenceFlows = displayNode.putArray("completedSequenceFlows");
                    for (String current : completedFlows) {
                        completedSequenceFlows.add(current);
                    }
                }
            } catch (Exception e) {
                log.error("创建模型JSON时出错", e);
            }
        }
        return FlowUtils.toNutMap(displayNode);
    }


    protected List<String> gatherCompletedFlows(List<String> completedActivityInstances,
                                                List<String> currentActivityinstances, BpmnModel pojoModel) {

        List<String> completedFlows = new ArrayList<>();
        List<String> activities = new ArrayList<>(completedActivityInstances);
        if (currentActivityinstances != null) {
            activities.addAll(currentActivityinstances);
        }
        // TODO: not a robust way of checking when parallel paths are active, should be revisited
        // 浏览所有活动并检查是否可以匹配任何与活动相关的传出路径
        for (FlowElement activity : pojoModel.getMainProcess().getFlowElements()) {
            if (activity instanceof FlowNode) {
                int index = activities.indexOf(activity.getId());
                if (index >= 0 && index + 1 < activities.size()) {
                    List<SequenceFlow> outgoingFlows = ((FlowNode) activity).getOutgoingFlows();
                    for (SequenceFlow flow : outgoingFlows) {
                        String destinationFlowId = flow.getTargetRef();
                        if (destinationFlowId.equals(activities.get(index + 1))) {
                            completedFlows.add(flow.getId());
                        }
                    }
                }
            }
        }
        return completedFlows;
    }

    protected void fillGraphicInfo(ObjectNode elementNode, GraphicInfo graphicInfo, boolean includeWidthAndHeight) {
        commonFillGraphicInfo(elementNode, graphicInfo.getX(),
                graphicInfo.getY(), graphicInfo.getWidth(),
                graphicInfo.getHeight(), includeWidthAndHeight);
    }

    protected void commonFillGraphicInfo(ObjectNode elementNode, double x,
                                         double y, double width, double height, boolean includeWidthAndHeight) {

        elementNode.put("x", x);
        elementNode.put("y", y);
        if (includeWidthAndHeight) {
            elementNode.put("width", width);
            elementNode.put("height", height);
        }
    }

    protected void fillDiagramInfo(GraphicInfo graphicInfo, GraphicInfo diagramInfo) {
        double rightX = graphicInfo.getX() + graphicInfo.getWidth();
        double bottomY = graphicInfo.getY() + graphicInfo.getHeight();
        double middleX = graphicInfo.getX() + (graphicInfo.getWidth() / 2);
        if (middleX < diagramInfo.getX()) {
            diagramInfo.setX(middleX);
        }
        if (graphicInfo.getY() < diagramInfo.getY()) {
            diagramInfo.setY(graphicInfo.getY());
        }
        if (rightX > diagramInfo.getWidth()) {
            diagramInfo.setWidth(rightX);
        }
        if (bottomY > diagramInfo.getHeight()) {
            diagramInfo.setHeight(bottomY);
        }
    }

    protected void processProcessElements(BpmnModel pojoModel, ObjectNode displayNode, GraphicInfo diagramInfo, Set<String> completedElements, Set<String> currentElements) throws Exception {
        if (pojoModel.getLocationMap().isEmpty()) {
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode elementArray = objectMapper.createArrayNode();
        ArrayNode flowArray = objectMapper.createArrayNode();
        ArrayNode collapsedArray = objectMapper.createArrayNode();
        if (CollectionUtils.isNotEmpty(pojoModel.getPools())) {
            ArrayNode poolArray = objectMapper.createArrayNode();
            boolean firstElement = true;
            for (Pool pool : pojoModel.getPools()) {
                ObjectNode poolNode = objectMapper.createObjectNode();
                poolNode.put("id", pool.getId());
                poolNode.put("name", pool.getName());
                GraphicInfo poolInfo = pojoModel.getGraphicInfo(pool.getId());
                fillGraphicInfo(poolNode, poolInfo, true);
                org.flowable.bpmn.model.Process process = pojoModel.getProcess(pool.getId());
                if (process != null && CollectionUtils.isNotEmpty(process.getLanes())) {
                    ArrayNode laneArray = objectMapper.createArrayNode();
                    for (Lane lane : process.getLanes()) {
                        ObjectNode laneNode = objectMapper.createObjectNode();
                        laneNode.put("id", lane.getId());
                        laneNode.put("name", lane.getName());
                        fillGraphicInfo(laneNode, pojoModel.getGraphicInfo(lane.getId()), true);
                        laneArray.add(laneNode);
                    }
                    poolNode.set("lanes", laneArray);
                }
                poolArray.add(poolNode);
                double rightX = poolInfo.getX() + poolInfo.getWidth();
                double bottomY = poolInfo.getY() + poolInfo.getHeight();
                double middleX = poolInfo.getX() + (poolInfo.getWidth() / 2);
                if (firstElement || middleX < diagramInfo.getX()) {
                    diagramInfo.setX(middleX);
                }
                if (firstElement || poolInfo.getY() < diagramInfo.getY()) {
                    diagramInfo.setY(poolInfo.getY());
                }
                if (rightX > diagramInfo.getWidth()) {
                    diagramInfo.setWidth(rightX);
                }
                if (bottomY > diagramInfo.getHeight()) {
                    diagramInfo.setHeight(bottomY);
                }
                firstElement = false;
            }
            displayNode.set("pools", poolArray);
        } else {
            // in initialize with fake x and y to make sure the minimal
            // values are set
            diagramInfo.setX(9999);
            diagramInfo.setY(1000);
        }
        for (org.flowable.bpmn.model.Process process : pojoModel.getProcesses()) {
            processElements(process.getFlowElements(), pojoModel, elementArray, flowArray,
                    collapsedArray, diagramInfo, completedElements, currentElements, null);
        }
        displayNode.set("elements", elementArray);
        displayNode.set("flows", flowArray);
        displayNode.set("collapsed", collapsedArray);
    }

    protected void fillEventTypes(String className, FlowElement element, ObjectNode elementNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (eventElementTypes.contains(className)) {
            Event event = (Event) element;
            if (CollectionUtils.isNotEmpty(event.getEventDefinitions())) {
                EventDefinition eventDef = event.getEventDefinitions().get(0);
                ObjectNode eventNode = objectMapper.createObjectNode();
                if (eventDef instanceof TimerEventDefinition) {
                    TimerEventDefinition timerDef = (TimerEventDefinition) eventDef;
                    eventNode.put("type", "timer");
                    if (Strings.isNotBlank(timerDef.getTimeCycle())) {
                        eventNode.put("timeCycle", timerDef.getTimeCycle());
                    }
                    if (Strings.isNotBlank(timerDef.getTimeDate())) {
                        eventNode.put("timeDate", timerDef.getTimeDate());
                    }
                    if (Strings.isNotBlank(timerDef.getTimeDuration())) {
                        eventNode.put("timeDuration", timerDef.getTimeDuration());
                    }

                } else if (eventDef instanceof ErrorEventDefinition) {
                    ErrorEventDefinition errorDef = (ErrorEventDefinition) eventDef;
                    eventNode.put("type", "error");
                    if (Strings.isNotBlank(errorDef.getErrorCode())) {
                        eventNode.put("errorCode", errorDef.getErrorCode());
                    }

                } else if (eventDef instanceof SignalEventDefinition) {
                    SignalEventDefinition signalDef = (SignalEventDefinition) eventDef;
                    eventNode.put("type", "signal");
                    if (Strings.isNotBlank(signalDef.getSignalRef())) {
                        eventNode.put("signalRef", signalDef.getSignalRef());
                    }

                } else if (eventDef instanceof MessageEventDefinition) {
                    MessageEventDefinition messageDef = (MessageEventDefinition) eventDef;
                    eventNode.put("type", "message");
                    if (Strings.isNotBlank(messageDef.getMessageRef())) {
                        eventNode.put("messageRef", messageDef.getMessageRef());
                    }
                }
                elementNode.set("eventDefinition", eventNode);
            }
        }
    }

    protected void processElements(Collection<FlowElement> elementList,
                                   BpmnModel model, ArrayNode elementArray, ArrayNode flowArray, ArrayNode collapsedArray,
                                   GraphicInfo diagramInfo, Set<String> completedElements, Set<String> currentElements, ObjectNode collapsedNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        for (FlowElement element : elementList) {
            ObjectNode elementNode = objectMapper.createObjectNode();
            if (completedElements != null) {
                elementNode.put("completed", completedElements.contains(element.getId()));
            }
            if (currentElements != null) {
                elementNode.put("current", currentElements.contains(element.getId()));
            }
            if (element instanceof SequenceFlow) {
                SequenceFlow flow = (SequenceFlow) element;
                elementNode.put("id", flow.getId());
                elementNode.put("type", "sequenceFlow");
                elementNode.put("sourceRef", flow.getSourceRef());
                elementNode.put("targetRef", flow.getTargetRef());
                elementNode.put("name", flow.getName());
                List<GraphicInfo> flowInfo = model.getFlowLocationGraphicInfo(flow.getId());
                ArrayNode waypointArray = objectMapper.createArrayNode();
                for (GraphicInfo graphicInfo : flowInfo) {
                    ObjectNode pointNode = objectMapper.createObjectNode();
                    fillGraphicInfo(pointNode, graphicInfo, false);
                    waypointArray.add(pointNode);
                    fillDiagramInfo(graphicInfo, diagramInfo);
                }
                elementNode.set("waypoints", waypointArray);
                if (collapsedNode != null) {
                    ((ArrayNode) collapsedNode.get("flows")).add(elementNode);
                } else {
                    flowArray.add(elementNode);
                }
            } else {
                elementNode.put("id", element.getId());
                elementNode.put("name", element.getName());
                if (element instanceof FlowNode) {
                    FlowNode flowNode = (FlowNode) element;
                    ArrayNode incomingFlows = objectMapper.createArrayNode();
                    for (SequenceFlow flow : flowNode.getIncomingFlows()) {
                        incomingFlows.add(flow.getId());
                    }
                    elementNode.set("incomingFlows", incomingFlows);
                }
                GraphicInfo graphicInfo = model.getGraphicInfo(element.getId());
                if (graphicInfo != null) {
                    fillGraphicInfo(elementNode, graphicInfo, true);
                    fillDiagramInfo(graphicInfo, diagramInfo);
                }
                String className = element.getClass().getSimpleName();
                elementNode.put("type", className);
                fillEventTypes(className, element, elementNode);
                if (element instanceof ServiceTask) {
                    ServiceTask serviceTask = (ServiceTask) element;
                    if (ServiceTask.MAIL_TASK.equals(serviceTask.getType())) {
                        elementNode.put("taskType", "mail");
                    }
                }
                if (propertyMappers.containsKey(className)) {
                    elementNode.set("org/flowable/db/properties", propertyMappers.get(className).map(element));
                }
                if (collapsedNode != null) {
                    ((ArrayNode) collapsedNode.get("elements")).add(elementNode);
                } else {
                    elementArray.add(elementNode);
                }
                if (element instanceof SubProcess) {
                    SubProcess subProcess = (SubProcess) element;
                    ObjectNode newCollapsedNode = collapsedNode;
                    // skip collapsed sub processes
                    if (graphicInfo != null && graphicInfo.getExpanded() != null && !graphicInfo.getExpanded()) {
                        elementNode.put("collapsed", "true");
                        newCollapsedNode = objectMapper.createObjectNode();
                        newCollapsedNode.put("id", subProcess.getId());
                        newCollapsedNode.putArray("elements");
                        newCollapsedNode.putArray("flows");
                        collapsedArray.add(newCollapsedNode);
                    }
                    processElements(subProcess.getFlowElements(), model, elementArray, flowArray, collapsedArray,
                            diagramInfo, currentElements, currentElements, newCollapsedNode);
                }
            }
        }
    }
}
