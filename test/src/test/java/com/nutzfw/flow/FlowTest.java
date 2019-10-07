/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.flow;

import com.nutzfw.module.TestRunner;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/8
 * 描述此类：
 */
@RunWith(TestRunner.class)
@IocBean
public class FlowTest {

    @Inject
    RepositoryService repositoryService;
    @Inject
    RuntimeService    runtimeService;
    @Inject
    TaskService       taskService;
    @Inject
    HistoryService    historyService;


    /**
     * 部署流程模型
     */
    @Test
    public void testDeploy() {
        Deployment deployment = repositoryService.createDeployment()
                .name("请假流程")
                .key("HolidayRequest")
                .addClasspathResource("bpmn/holiday-request.bpmn20.xml")
                .deploy();
        System.out.println("Deploy successfullly, deployId:" + deployment.getId() + "; deployName:" + deployment.getName());
    }


    /**
     * 查询流程定义
     */
    @Test
    public void queryProcessDefinitionTest() {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                // 用上一步中的结果
                .deploymentId("20001")
                .singleResult();
        System.out.println("Found process definition : " + processDefinition.getName() + "; key:" + processDefinition.getKey() + ";id:" + processDefinition.getId());
    }

    /**
     * 启动流程
     */
    @Test
    public void startProcessInstanceTest() {
        Map<String, Object> variables = new HashMap();
        variables.put("employee", "jack");
        variables.put("nrOfHolidays", 3);
        variables.put("description", "回家看看");
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey("holidayRequest", variables);

    }

    /**
     * 查询并完成任务
     */
    @Test
    public void queryAndCompleteTask() {
        // 查询
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("managers").list();
        System.out.println("You have " + tasks.size() + " tasks:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ") " + tasks.get(i).getName());
        }

        // 选择
        Task task = tasks.get(0);
        Map<String, Object> processVariables = taskService.getVariables(task.getId());
        System.out.println(processVariables.get("employee") + " wants " +
                processVariables.get("nrOfHolidays") + " of holidays. Do you approve this?");

        //完成
        Map<String, Object> variables = new HashMap();
        variables.put("approved", true);
        taskService.complete(task.getId(), variables);
    }

    /**
     * 查询历史数据
     */
    @Test
    public void queryHistoryData() {
        List<HistoricActivityInstance> activities =
                historyService.createHistoricActivityInstanceQuery().processInstanceId("4")
                        .orderByHistoricActivityInstanceEndTime().asc()
                        .list();

        for (HistoricActivityInstance activity : activities) {
            System.out.println(activity.getActivityId() + " took "
                    + activity.getDurationInMillis() + " milliseconds");
            System.out.println("======================================");
        }
    }

}
