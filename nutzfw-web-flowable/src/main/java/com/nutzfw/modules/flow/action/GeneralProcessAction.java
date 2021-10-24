/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.flow.action;

import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.core.plugin.flowable.context.ProcessContext;
import com.nutzfw.core.plugin.flowable.context.ProcessContextHolder;
import com.nutzfw.core.plugin.flowable.dto.FlowSubmitInfoDTO;
import com.nutzfw.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.nutzfw.core.plugin.flowable.enums.FormType;
import com.nutzfw.core.plugin.flowable.enums.ProcessStatus;
import com.nutzfw.core.plugin.flowable.enums.TaskFormStatusEnum;
import com.nutzfw.core.plugin.flowable.extmodel.FormElementModel;
import com.nutzfw.core.plugin.flowable.service.FlowProcessDefinitionService;
import com.nutzfw.core.plugin.flowable.service.FlowTaskService;
import com.nutzfw.core.plugin.flowable.util.FlowUtils;
import com.nutzfw.core.plugin.flowable.vo.FlowTaskVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.flow.biz.GeneralFlowBiz;
import com.nutzfw.modules.flow.service.FlowCustomQueryService;
import com.nutzfw.modules.flow.vo.NextNodeConfigVO;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.sys.entity.DataTable;
import com.nutzfw.modules.sys.entity.TableFields;
import com.nutzfw.modules.sys.service.DataTableService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;

import java.util.Map;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/6/20
 * 通用流程
 */
@IocBean
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
@At("/general/flow/process")
public class GeneralProcessAction extends BaseAction {

    @Inject
    GeneralFlowBiz generalFlowBiz;
    @Inject
    FlowProcessDefinitionService flowProcessDefinitionService;
    @Inject
    FlowCustomQueryService flowCustomQueryService;
    @Inject
    FlowTaskService flowTaskService;
    @Inject
    RepositoryService repositoryService;
    @Inject
    DataTableService dataTableService;

    @At("/form")
    @GET
    @Ok("btl:WEB-INF/view/modules/flow/general/flowAudit.html")
    public NutMap form(@Param("::flow.") FlowTaskVO flowTaskVO, @Attr(Cons.SESSION_USER_KEY) UserAccount sessionUserAccount) {
        if (Strings.isNotBlank(flowTaskVO.getTaskId())) {
            FlowUtils.setFlowTaskVo(flowTaskVO, flowTaskService.getTaskOrHistoryTask(flowTaskVO.getTaskId()), sessionUserAccount.getUserName());
            if (flowTaskVO.getProcInsId() != null) {
                // 设置业务表ID
                flowTaskVO.setBusinessId(flowProcessDefinitionService.getBusinessKeyId(flowTaskVO.getProcInsId()));
            }
        }
        if (Strings.isBlank(flowTaskVO.getTaskId()) && Strings.isNotBlank(flowTaskVO.getProcDefKey())) {
            //没有任务ID，是采用最新版本新增流程
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(flowTaskVO.getProcDefKey()).latestVersion().singleResult();
            flowTaskVO.setProcDefId(processDefinition.getId());
            flowTaskVO.setProcDefversion(processDefinition.getVersion());
        }
        NutMap nutMap = new NutMap();
        FormElementModel formElementModel = generalFlowBiz.getFormPage(flowTaskVO);
        if (Strings.isBlank(formElementModel.getFormKey())) {
            throw new RuntimeException("表单不能为空");
        }
        Object formData = generalFlowBiz.loadFormData(flowTaskVO, sessionUserAccount);
        nutMap.put("formElementModel", formElementModel);
        nutMap.put("formPage", formElementModel.getFormKey());
        nutMap.put("flow", flowTaskVO);
        nutMap.put("title", generalFlowBiz.getFlowName(flowTaskVO));
        nutMap.put("formData", formData);
        nutMap.put("formDataJson", Json.toJson(formData));
        nutMap.put("status", TaskFormStatusEnum.EDIT);
        if (flowTaskVO.isFinishTask()) {
            nutMap.put("status", TaskFormStatusEnum.VIEW);
        } else if (Strings.isBlank(flowTaskVO.getTaskId())) {
            nutMap.put("status", TaskFormStatusEnum.EDIT);
        } else {
            nutMap.put("status", TaskFormStatusEnum.AUDIT);
        }

        if (formElementModel.getFormType() == FormType.ONLINE) {
            DataTable dataTable = dataTableService.fetchAllFields(Integer.parseInt(formElementModel.getTableId()));
            if (sessionUserAccount == null || sessionUserAccount.getId() == null) {
                sessionUserAccount = getSessionUserAccount();
            }
            //是否有可以显示的字段
            setRequestAttribute("hasAnyDisplay", dataTable.getFields().stream().anyMatch(TableFields::isFromDisplay));
            setRequestAttribute("table", dataTable);
            setRequestAttribute("userid", sessionUserAccount.getUserid());
            setRequestAttribute("user", sessionUserAccount);
        }
        return nutMap;
    }

    /**
     * 取得流程下一节点
     *
     * @param formData
     * @param flowTaskVO
     * @return
     */
    @At("/getNextNode")
    @POST
    @Ok("json")
    public AjaxResult getNextNode(@Param("::form") Map formData, @Param("::flow") FlowTaskVO flowTaskVO) {
        try {
            NextNodeConfigVO nodeConfigVO = flowTaskService.previewNextNode(formData, flowTaskVO, getSessionUserAccount());
            if (nodeConfigVO != null) {
                return AjaxResult.sucess(nodeConfigVO);
            }
            return AjaxResult.error("下一步不是用户节点");
        } catch (Exception e) {
            throw new RuntimeException("事务无法打开！");
        }
    }

    /**
     * 展示下一步流程审核人选择
     *
     * @param flowTaskVO
     * @return
     */
    @At("/choiceNextReviewerUser")
    @POST
    @Ok("json")
    public LayuiTableDataListVO choiceNextReviewerUser(@Param("::flow") FlowTaskVO flowTaskVO, @Param("nextNodeId") String nextNodeId) {
        UserTaskExtensionDTO dto = flowProcessDefinitionService.getUserTaskExtension(flowTaskVO.getTaskDefKey(), flowTaskVO.getProcDefId());
        if (dto.isDynamicFreeChoiceNextReviewerMode()) {
            if (Strings.isNotBlank(nextNodeId)) {
                dto = flowProcessDefinitionService.getUserTaskExtension(nextNodeId, flowTaskVO.getProcDefId());
                FlowSubmitInfoDTO submitInfoDTO = flowCustomQueryService.getFlowSubmitInfo(flowTaskVO.getTaskId());
                return LayuiTableDataListVO.allData(generalFlowBiz.listUserTaskNodeAllReviewerUser(dto, submitInfoDTO));
            }
        }
        return LayuiTableDataListVO.noData();
    }

    /**
     * 流程回退
     *
     * @param formData
     * @param flowTaskVO
     */
    @At("/backToStep")
    @POST
    @Ok("json")
    @Aop(TransAop.READ_COMMITTED)
    public AjaxResult backToStep(@Param("::form") Map formData, @Param("::flow") FlowTaskVO flowTaskVO, @Attr(Cons.SESSION_USER_KEY) UserAccount sessionUserAccount) {
        if (formData != null && flowTaskVO != null) {
            String message = generalFlowBiz.backToStep(formData, flowTaskVO, sessionUserAccount);
            if (Strings.isNotBlank(message)) {
                return AjaxResult.error(message);
            }
            return AjaxResult.sucessMsg("回退成功");
        } else {
            return AjaxResult.error("参数异常");
        }
    }

    /**
     * 加签
     *
     * @param formData
     * @param flowTaskVO
     */
    @At("/addMultiInstance")
    @POST
    @Ok("json")
    @Aop(TransAop.READ_COMMITTED)
    public AjaxResult addMultiInstance(@Param("::form") Map formData, @Param("::flow") FlowTaskVO flowTaskVO, @Attr(Cons.SESSION_USER_KEY) UserAccount sessionUserAccount) {
        if (formData != null && flowTaskVO != null) {
            String message = generalFlowBiz.addMultiInstance(formData, flowTaskVO, sessionUserAccount);
            if (Strings.isNotBlank(message)) {
                return AjaxResult.error(message);
            }
            return AjaxResult.sucessMsg("加签成功");
        } else {
            return AjaxResult.error("参数异常");
        }
    }


    /**
     * 启动流程--工单执行（完成任务）
     *
     * @param formData
     * @param flowTaskVO
     * @return
     */
    @At("/saveAudit")
    @Ok("json")
    @Aop(TransAop.REPEATABLE_READ)
    public AjaxResult saveAudit(@Param("::form") Map formData, @Param("::flow") FlowTaskVO flowTaskVO, @Attr(Cons.SESSION_USER_KEY) UserAccount sessionUserAccount) {
        if (formData != null && flowTaskVO != null) {
            if (flowTaskVO.getTurnDown() == true && Strings.isBlank(flowTaskVO.getComment())) {
                return AjaxResult.error("驳回意见不能为空！");
            }
            ProcessContext processContext = new ProcessContext();
            processContext.setProcessStatus(ProcessStatus.UNDER_REVIEW);
            processContext.setProcessInstanceId(flowTaskVO.getProcInsId());
            processContext.setProcessDefId(flowTaskVO.getProcDefId());
            processContext.setProcessDefKey(flowTaskVO.getProcDefKey());
            processContext.setBusinessId(flowTaskVO.getBusinessId());
            ProcessContextHolder.set(processContext);
            if (Strings.isNotBlank(flowTaskVO.getBusinessId())) {
                String message = generalFlowBiz.userAudit(formData, flowTaskVO, sessionUserAccount);
                if (message != null) {
                    return AjaxResult.error(message);
                }
            } else if (Strings.isBlank(sessionUserAccount.getDeptId())) {
                return AjaxResult.error("流程发起人不存在任何部门中！");
            } else {
                String message = generalFlowBiz.start(formData, flowTaskVO, sessionUserAccount, getSessionRoleCodes());
                return AjaxResult.sucessMsg(message);
            }
            return AjaxResult.sucessMsg("操作成功！");
        } else {
            return AjaxResult.error("参数异常");
        }
    }


    /**
     * 加载表单信息
     *
     * @param flowTaskVO
     */
    @At("/loadFormData")
    @POST
    @Ok("json:{dateFormat:'yyyy-MM-dd HH:mm',nullListAsEmpty:true}")
    public AjaxResult loadFormData(@Param("::flow") FlowTaskVO flowTaskVO, @Attr(Cons.SESSION_USER_KEY) UserAccount sessionUserAccount) {
        Object formData = generalFlowBiz.loadFormData(flowTaskVO, sessionUserAccount);
        if (formData == null) {
            return AjaxResult.error("数据不存在，可能是新增");
        }
        return AjaxResult.sucess(formData);
    }
}
