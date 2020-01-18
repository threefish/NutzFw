/*
 * Copyright (c) 2019- 2020 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2020/01/18 17:07:18
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.elbeans.multiinstance;

import com.nutzfw.core.plugin.flowable.constant.FlowConstant;
import com.nutzfw.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.nutzfw.core.plugin.flowable.elbeans.IocElBeans;
import com.nutzfw.core.plugin.flowable.enums.SignPassType;
import com.nutzfw.core.plugin.flowable.service.FlowProcessDefinitionService;
import com.nutzfw.core.plugin.flowable.util.FlowUtils;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.delegate.DelegateExecution;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.IocBean;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/1/18
 */
@IocBean(name = "multiInstanceCompleteTask", args = {"refer:$ioc"})
public class MultiInstanceCompleteTask implements Serializable, IocElBeans {
    /**
     * 总实例数
     */
    final static String NUMBER_OF_INSTANCES = "nrOfInstances";
    /**
     * 已完成的实例数
     */
    final static String NUMBER_OF_COMPLETED_INSTANCES = "nrOfCompletedInstances";

    private Ioc ioc;

    public MultiInstanceCompleteTask(Ioc ioc) {
        this.ioc = ioc;
    }


    public FlowProcessDefinitionService flowProcessDefinitionService() {
        return ioc.getByType(FlowProcessDefinitionService.class);
    }


    /**
     * 是否满足判定条件
     *
     * @param execution 分配执行实例
     */
    public boolean accessCondition(DelegateExecution execution) {
        int completedInstance = getVar(execution, NUMBER_OF_COMPLETED_INSTANCES);
        int instancesAll = getVar(execution, NUMBER_OF_INSTANCES);
        if (instancesAll == 0) {
            throw new RuntimeException("多实例实例数不应为0！");
        }
        UserTaskExtensionDTO extension = getUserTaskExtension(execution);
        if (extension.getSignType() == SignPassType.SCALE) {
            return this.doScale(execution, completedInstance, instancesAll, extension.getSignScale(), extension.getSignAll());
        }
        if (extension.getSignType() == SignPassType.ONE_VOTE_ADOPT) {
            return this.doOneVoteAdopt(execution, completedInstance, instancesAll);
        }
        if (extension.getSignType() == SignPassType.ONE_VOTE_VETO) {
            return this.doOneVoteVeto(execution, completedInstance, instancesAll);
        }
        throw new RuntimeException("未设置多实例通过制度！");
    }

    /**
     * 一票通过制
     *
     * @return
     */
    private boolean doOneVoteAdopt(DelegateExecution execution, int completedInstance, int instancesAll) {
        //通过判断，一票通过
        if (execution.getVariable(FlowConstant.MULTIINSTANCE_AGREE_COUNTER) != null) {
            int agreeCounter = getVar(execution, FlowConstant.MULTIINSTANCE_AGREE_COUNTER);
            if (agreeCounter > 0) {
                //输出方向为同意
                execution.setVariable(FlowConstant.AUDIT_PASS, true);
                //一票同意其他实例没必要做，结束
                return true;
            }
        }
        //所有实例任务未全部做完则继续其他实例任务
        if (completedInstance != instancesAll) {
            return false;
        } else {
            //输出方向为拒绝
            execution.setVariable(FlowConstant.AUDIT_PASS, false);
            //所有实例都做完了，结束
            return true;
        }
    }

    /**
     * 一票否决制
     *
     * @return
     */
    private boolean doOneVoteVeto(DelegateExecution execution, int completedInstance, int instancesAll) {
        //否决判断，一票否决
        if (execution.getVariable(FlowConstant.MULTIINSTANCE_REJECT_COUNTER) != null) {
            int rejectCount = getVar(execution, FlowConstant.MULTIINSTANCE_REJECT_COUNTER);
            if (rejectCount > 0) {
                //输出方向为拒绝
                execution.setVariable(FlowConstant.AUDIT_PASS, false);
                //一票否决其他实例没必要做，结束
                return true;
            }
        }
        //所有实例任务未全部做完则继续其他实例任务
        if (completedInstance != instancesAll) {
            return false;
        } else {
            //输出方向为通过
            execution.setVariable(FlowConstant.AUDIT_PASS, true);
            //所有实例都做完了，结束
            return true;
        }
    }

    /**
     * 比例通过制
     *
     * @return
     */
    private boolean doScale(DelegateExecution execution, int completedInstance, int instancesAll, float signScale, boolean signAll) {
        int agreeCounter = getVar(execution, FlowConstant.MULTIINSTANCE_AGREE_COUNTER);
        //百分比
        float percent = (float) agreeCounter / (float) instancesAll * 100;
        boolean isAllApplyed = completedInstance == instancesAll;
        if (percent >= signScale) {
            //输出方向为通过
            execution.setVariable(FlowConstant.AUDIT_PASS, true);
            if (signAll) {
                //需要全部进行审核,是否已全部审核
                return isAllApplyed;
            }
            return true;
        } else {
            //输出方向为拒绝
            execution.setVariable(FlowConstant.AUDIT_PASS, false);
            return false;
        }
    }

    /**
     * 取得用户节点配置信息
     *
     * @param execution
     * @return
     */
    private UserTaskExtensionDTO getUserTaskExtension(DelegateExecution execution) {
        String processDefinitionId = execution.getProcessDefinitionId();
        String currentActivityId = execution.getCurrentActivityId();
        UserTask userTask = flowProcessDefinitionService().getUserTask(currentActivityId, processDefinitionId);
        return FlowUtils.getUserTaskExtension(userTask);
    }


    /**
     * 取得变量
     *
     * @param delegateTask
     * @return
     */
    private int getVar(DelegateExecution delegateTask, String varName) {
        Integer variable = (Integer) delegateTask.getVariable(varName);
        return Objects.isNull(variable) ? 0 : variable;
    }
}
