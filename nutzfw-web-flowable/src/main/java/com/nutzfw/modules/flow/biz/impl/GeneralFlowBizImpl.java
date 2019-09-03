package com.nutzfw.modules.flow.biz.impl;

import com.google.common.collect.Maps;
import com.nutzfw.core.common.javascript.JsContex;
import com.nutzfw.core.plugin.flowable.constant.FlowConstant;
import com.nutzfw.core.plugin.flowable.dto.CandidateGroupsDTO;
import com.nutzfw.core.plugin.flowable.dto.CandidateUsersDTO;
import com.nutzfw.core.plugin.flowable.dto.FlowSubmitInfoDTO;
import com.nutzfw.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.nutzfw.core.plugin.flowable.enums.TaskReviewerScopeEnum;
import com.nutzfw.core.plugin.flowable.service.FlowCacheService;
import com.nutzfw.core.plugin.flowable.service.FlowProcessDefinitionService;
import com.nutzfw.core.plugin.flowable.service.FlowTaskService;
import com.nutzfw.core.plugin.flowable.util.FlowUtils;
import com.nutzfw.core.plugin.flowable.vo.FlowTaskVO;
import com.nutzfw.modules.flow.biz.GeneralFlowBiz;
import com.nutzfw.modules.flow.executor.ExternalFormExecutor;
import com.nutzfw.modules.flow.service.FlowCustomQueryService;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.enums.LeaderTypeEnum;
import com.nutzfw.modules.organize.service.DepartmentLeaderService;
import com.nutzfw.modules.sys.biz.UserAccountBiz;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.impl.identity.Authentication;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/6/20
 */
@Slf4j
@IocBean
public class GeneralFlowBizImpl implements GeneralFlowBiz {

    @Inject("refer:$ioc")
    Ioc                          ioc;
    @Inject
    FlowTaskService              flowTaskService;
    @Inject
    FlowCacheService             flowCacheService;
    @Inject
    FlowProcessDefinitionService flowProcessDefinitionService;
    @Inject
    DepartmentLeaderService      departmentLeaderService;
    @Inject
    UserAccountBiz               userAccountBiz;
    @Inject
    FlowCustomQueryService       flowCustomQueryService;

    @Override
    public String getFormPage(FlowTaskVO flowTaskVO) {
        ExternalFormExecutor executor = getExternalFormExecutor(flowTaskVO.getProcDefId());
        return executor.getFormPage(flowTaskVO);
    }

    @Override
    public String start(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount, Set<String> roleCodes) {
        // 设置当前流程任务办理人
        Authentication.setAuthenticatedUserId(sessionUserAccount.getUserName());
        ExternalFormExecutor executor = getExternalFormExecutor(flowTaskVO.getProcDefId());
        Map<String, Object> variables = Maps.newHashMap();
        variables.put(FlowConstant.FORM_DATA, formData);
        variables.put(FlowConstant.AUDIT_PASS, flowTaskVO.isPass());
        flowTaskService.setValuedDataObject(variables,flowTaskVO.getProcDefId(), formData, sessionUserAccount);
        if (Strings.isBlank(formData.getOrDefault(FlowConstant.PRIMARY_KEY, "").toString()) && Strings.isBlank(flowTaskVO.getTaskId())) {
            flowTaskVO.setComment("[发起任务]");
            formData = executor.start(formData, flowTaskVO, sessionUserAccount);
            //存储最新的formData
            variables.put(FlowConstant.FORM_DATA, formData);
            String primaryKeyId = formData.getOrDefault(FlowConstant.PRIMARY_KEY, "").toString();
            if (Strings.isBlank(primaryKeyId)) {
                throw new RuntimeException("业务ID不能为空");
            }
            String procIns = flowTaskService.startProcess(flowTaskVO.getProcDefKey(), primaryKeyId, variables, sessionUserAccount.getUserName(), sessionUserAccount.getDeptId(), roleCodes);
            return "流程已启动！流水号：" + procIns;
        } else {
            flowTaskVO.setComment(flowTaskVO.isPass() ? "[重申] " : "[销毁] " + flowTaskVO.getComment());
            // 完成流程任务
            flowTaskService.complete(flowTaskVO, variables);
            return MessageFormat.format("流程已[0]", (flowTaskVO.isPass() ? "[重申] " : "[销毁] "));
        }
    }

    @Override
    public String backToStep(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        // 设置当前流程任务办理人
        Authentication.setAuthenticatedUserId(sessionUserAccount.getUserName());
        ExternalFormExecutor executor = getExternalFormExecutor(flowTaskVO.getProcDefId());
        flowTaskVO.setComment("[回退] " + flowTaskVO.getComment());
        String errorMsg = executor.backToStep(formData, flowTaskVO, sessionUserAccount);
        if (errorMsg != null) {
            return errorMsg;
        }
        return flowTaskService.backToStep(flowTaskVO, sessionUserAccount.getUserName());
    }

    @Override
    public String addMultiInstance(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        // 设置当前流程任务办理人
        Authentication.setAuthenticatedUserId(sessionUserAccount.getUserName());
        ExternalFormExecutor executor = getExternalFormExecutor(flowTaskVO.getProcDefId());
        flowTaskVO.setComment("[加签] " + flowTaskVO.getComment());
        String errorMsg = executor.addMultiInstance(formData, flowTaskVO, sessionUserAccount);
        if (errorMsg != null) {
            return errorMsg;
        }
        return flowTaskService.addMultiInstance(flowTaskVO, flowTaskVO.getComment());
    }

    @Override
    public String userAudit(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        // 设置当前流程任务办理人
        Authentication.setAuthenticatedUserId(sessionUserAccount.getUserName());
        FlowUtils.setFlowTaskVo(flowTaskVO, flowTaskService.getTaskOrHistoryTask(flowTaskVO.getTaskId()));
        ExternalFormExecutor executor = getExternalFormExecutor(flowTaskVO.getProcDefId());
        if (Strings.isNotBlank(flowTaskVO.getComment())) {
            flowTaskVO.setComment((flowTaskVO.isPass() ? "[通过] " : "[拒绝] ") + flowTaskVO.getComment());
        } else {
            flowTaskVO.setComment(flowTaskVO.getBusinessComment());
        }
        Map<String, Object> vars = Maps.newHashMap();
        vars.put(FlowConstant.AUDIT_PASS, flowTaskVO.isPass());
        vars.put(FlowConstant.FORM_DATA, formData);
        UserTaskExtensionDTO dto = flowProcessDefinitionService.getUserTaskExtension(flowTaskVO.getTaskDefKey(), flowTaskVO.getProcDefId());
        if (dto.isDynamicFreeChoiceNextReviewerMode() && flowTaskVO.getDelegateStatus() == null) {
            boolean needCheckFlowNextReviewerAssignee = false;
            try {
                //此方法前不要操作数据库，该方法会回滚数据库的
                UserTask userTask = flowTaskService.getNextNode(formData, flowTaskVO);
                if (userTask != null) {
                    //下一节点存在，需要选择审核人
                    needCheckFlowNextReviewerAssignee = true;
                }
            } catch (Exception e) {
                throw new RuntimeException("事务无法打开！");
            }
            if (needCheckFlowNextReviewerAssignee && Strings.isBlank(flowTaskVO.getFlowNextReviewerAssignee())) {
                return "请选择下一步流程审核人！";
            }
            if (Strings.isNotBlank(flowTaskVO.getFlowNextReviewerAssignee())) {
                vars.put(FlowConstant.NEXT_REVIEWER, flowTaskVO.getFlowNextReviewerAssignee());
            }
        }
        formData = evalJavaScriptByModifyForm(formData, flowTaskVO, dto);
        String errorMsg = executor.userAudit(formData, flowTaskVO, sessionUserAccount);
        if (errorMsg != null) {
            return errorMsg;
        }
        //变量别修改过了，所以从新设置下
        vars.put(FlowConstant.FORM_DATA, formData);
        flowTaskService.complete(flowTaskVO, vars);
        return null;
    }

    @Override
    public Object loadFormData(FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        return getExternalFormExecutor(flowTaskVO.getProcDefId()).loadFormData(flowTaskVO, sessionUserAccount);
    }

    @Override
    public String getFlowName(FlowTaskVO flowTaskVO) {
        return flowCacheService.getProcessDefinitionCache(flowTaskVO.getProcDefId()).getName();
    }

    @Override
    public List<NutMap> listUserTaskNodeAllReviewerUser(UserTaskExtensionDTO taskExtensionDTO, FlowSubmitInfoDTO flowSubmitInfoDTO) {
        List<String> candidateUserNames = new ArrayList<>();
        String flowSubmitterUserName = flowSubmitInfoDTO.getUserName();
        String flowSubmitterDeptId = flowSubmitInfoDTO.getDeptId();
        switch (taskExtensionDTO.getTaskReviewerScope()) {
            case SINGLE_USER:
                candidateUserNames.add(taskExtensionDTO.getAssignee());
                break;
            case FLOW_SUBMITTER:
                candidateUserNames.add(flowSubmitterUserName);
                break;
            case MULTIPLE_USERS:
                candidateUserNames = taskExtensionDTO.getCandidateUsers().stream().map(CandidateUsersDTO::getUserName).collect(Collectors.toList());
                break;
            case USER_ROLE_GROUPS:
                List<String> roleCodes = taskExtensionDTO.getCandidateGroups().stream().map(CandidateGroupsDTO::getRoleCode).collect(Collectors.toList());
                candidateUserNames = userAccountBiz.listUserNameByRoleCodes(roleCodes);
                break;
            case DEPARTMENT_HEAD:
            case DEPARTMENT_LEADER:
                if (taskExtensionDTO.getTaskReviewerScope() == TaskReviewerScopeEnum.DEPARTMENT_HEAD) {
                    //查询部门主管领导
                    candidateUserNames = departmentLeaderService.queryUserNames(flowSubmitterDeptId, LeaderTypeEnum.HEAD);
                    if (candidateUserNames.contains(flowSubmitterUserName)) {
                        //如果自己就是部门主管领导则分配给再上级部门主管办理
                        candidateUserNames = departmentLeaderService.queryIterationUserNames(flowSubmitterDeptId, LeaderTypeEnum.HEAD);
                    }
                } else if (taskExtensionDTO.getTaskReviewerScope() == TaskReviewerScopeEnum.DEPARTMENT_LEADER) {
                    //查询部门分管领导
                    candidateUserNames = departmentLeaderService.queryUserNames(flowSubmitterDeptId, LeaderTypeEnum.LEADER);
                    if (candidateUserNames.contains(flowSubmitterUserName)) {
                        //如果自己就是部门分管领导则分配给部门主管领导办理
                        candidateUserNames = departmentLeaderService.queryUserNames(flowSubmitterDeptId, LeaderTypeEnum.HEAD);
                    }
                }
                break;
            default:
                break;
        }
        return flowCustomQueryService.listUserTaskNodeAllReviewerUser(candidateUserNames);
    }

    private ExternalFormExecutor getExternalFormExecutor(String procDefId) {
        return flowProcessDefinitionService.getExternalFormExecutor(procDefId);
    }

    /**
     * 表单数据在审核后执行数据库更新前进行动态赋值
     *
     * @param formData
     * @param flowTaskVO
     * @param dto
     * @return
     */
    private Map evalJavaScriptByModifyForm(Map formData, FlowTaskVO flowTaskVO, UserTaskExtensionDTO dto) {
        if (Strings.isNotBlank(dto.getFormDataDynamicAssignment())) {
            StringBuffer jsCode = new StringBuffer("function modifyForm(formData,auditPass,flowTask){ " + dto.getFormDataDynamicAssignment() + "  return formData; }");
            try {
                JsContex.get().compile(jsCode.toString());
                JsContex.get().eval(jsCode.toString());
                Object result = JsContex.get().invokeFunction("modifyForm", formData, flowTaskVO.isPass(), flowTaskVO);
                formData = (Map) result;
            } catch (Exception e) {
                log.error("解析动态JS错误", e);
                throw new RuntimeException("解析动态JS错误");
            }
        }
        return formData;
    }
}
