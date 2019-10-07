/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.config;

import org.flowable.engine.*;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/8
 * <p>
 * act_re_ *：代表基础仓库。带有此前缀的表包含静态信息，如流程定义和流程资源（图像、规则等）。
 * act_ru_ *：运行库。这些是运行时表，其中包含进程实例、用户任务、变量、作业等的运行时数据。流动只存储过程实例的执行过程中的运行时数据和删除记录时，一个流程实例结束。这样可以使运行时表小而快速。
 * act_hi_ *：历史库，代表历史。这些表包含历史数据，如过去的流程实例、变量、任务等。
 * act_ge_ *：常规库，这是用于各种使用案例。
 * <p>
 * <p>
 * 多实例参数介绍
 * <p>
 * 前缀nr是number单词缩写
 * <p>
 * 1.nrOfInstances  实例总数
 * 2.nrOfCompletedInstances  已经完成的实例
 * 3.loopCounter 已经循环的次数
 * 4.nrOfActiveInstances 当前活动中的实例
 * <p>
 * 示例：已经完成的实例/实例总数=2/3=0.6666666 ---- 满足3人两人通用则通过的表达式为 ${nrOfCompletedInstances/nrOfInstances >=0.6}
 */
@IocBean
public class NutzFwFlowAbleContextConfig {

    /**
     * 不要在其他地方使用，如要取得 processEngine ，请使用 getProcessEngine
     * <p>
     * 避免启动时获取流程引擎循环依赖导致启动失败
     *
     * @return
     */
    @Inject
    ProcessEngine processEngine;


    public ProcessEngine getProcessEngine() {
        return processEngine;
    }

    @IocBean
    public ProcessEngine processEngine(@Inject NutzFwProcessEngineConfiguration configuration) {
        return configuration.buildProcessEngine();
    }

    @IocBean
    public RepositoryService repositoryService() {
        return processEngine.getRepositoryService();
    }

    @IocBean
    public RuntimeService runtimeService() {
        return processEngine.getRuntimeService();
    }

    @IocBean
    public IdentityService identityService() {
        return processEngine.getIdentityService();
    }

    @IocBean
    public TaskService taskService() {
        return processEngine.getTaskService();
    }

    @IocBean
    public HistoryService historyService() {
        return processEngine.getHistoryService();
    }

    @IocBean
    public ManagementService managementService() {
        return processEngine.getManagementService();
    }

    @IocBean
    public FormService formService() {
        return processEngine.getFormService();
    }

    @IocBean
    public DynamicBpmnService dynamicBpmnService() {
        return processEngine.getDynamicBpmnService();
    }

}
