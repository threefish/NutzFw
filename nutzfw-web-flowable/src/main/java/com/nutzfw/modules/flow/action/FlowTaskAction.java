package com.nutzfw.modules.flow.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.annotation.TryCatchMsg;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.nutzfw.core.plugin.flowable.service.FlowProcessDefinitionService;
import com.nutzfw.core.plugin.flowable.service.FlowTaskService;
import com.nutzfw.core.plugin.flowable.vo.FlowTaskVO;
import com.nutzfw.modules.common.action.BaseAction;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.flowable.common.engine.impl.identity.Authentication;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/11
 * 流程个人任务相关
 */
@At("/flowTask")
@IocBean
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class FlowTaskAction extends BaseAction {

    @Inject
    FlowTaskService flowTaskService;

    @Inject
    FlowProcessDefinitionService flowProcessDefinitionService;

    @At("/index")
    @RequiresPermissions("sys.flow.task")
    @AutoCreateMenuAuth(name = "我的任务", icon = "fa-tasks")
    public void task() {
    }

    @GET
    @At("/todo")
    @Ok("btl:WEB-INF/view/modules/flow/task/todo.html")
    @RequiresPermissions("sys.flow.task.todo")
    @AutoCreateMenuAuth(name = "待办任务", icon = "fa-tasks", parentPermission = "sys.flow.task")
    public void todo() {
    }

    @GET
    @At("/historic")
    @Ok("btl:WEB-INF/view/modules/flow/task/historic.html")
    @RequiresPermissions("sys.flow.task.historic")
    @AutoCreateMenuAuth(name = "已办任务", icon = "fa-tasks", parentPermission = "sys.flow.task")
    public void historic() {
    }

    @GET
    @At("/hasSent")
    @Ok("btl:WEB-INF/view/modules/flow/task/hasSent.html")
    @RequiresPermissions("sys.flow.task.hasSent")
    @AutoCreateMenuAuth(name = "已发任务", icon = "fa-tasks", parentPermission = "sys.flow.task")
    public void hasSent() {
    }

    @GET
    @At("/process")
    @Ok("btl:WEB-INF/view/modules/flow/task/process.html")
    @RequiresPermissions("sys.flow.task.process")
    @AutoCreateMenuAuth(name = "发起任务", icon = "fa-tasks", parentPermission = "sys.flow.task")
    public void process() {
    }

    @Ok("json")
    @At("/todoData")
    @POST
    public LayuiTableDataListVO todoData(@Param("::") FlowTaskVO flowTaskVO, HttpServletRequest request) {
        //联查变量的情况下，分页功能失效
        return flowTaskService.todoList(LayuiTableDataListVO.get(request), flowTaskVO, getSessionUserAccount().getUserName(), getSessionRoleCodes(), false);
    }

    @At("/diagramViewer")
    @Ok("btl:WEB-INF/view/modules/flow/task/flowDiagramViewer.html")
    public void diagramViewer(@Param("definitionId") String definitionId, @Param("instanceId") String instanceId, @Param("hisInsId") String hisInsId) {
        setRequestAttribute("definitionId", Strings.sNull(definitionId));
        setRequestAttribute("instanceId", Strings.sNull(instanceId));
        setRequestAttribute("hisInsId", Strings.sNull(hisInsId));
    }

    @Ok("json")
    @At("/historicData")
    public LayuiTableDataListVO historicDataList(FlowTaskVO flowTaskVO, HttpServletRequest request, HttpServletResponse response) {
        return flowTaskService.historicList(LayuiTableDataListVO.get(request), flowTaskVO, getSessionUserAccount().getUserName());
    }

    @Ok("json")
    @At("/hasSentData")
    public LayuiTableDataListVO hasSentDataList(FlowTaskVO flowTaskVO, HttpServletRequest request, HttpServletResponse response) {
        return flowTaskService.hasSentList(LayuiTableDataListVO.get(request), getSessionUserAccount().getUserName());
    }

    /**
     * 获取流转历史列表
     *
     * @param procInsId 流程实例
     * @param startAct  开始活动节点名称
     * @param endAct    结束活动节点名称
     */
    @At("/histoicFlow")
    @Ok("btl:WEB-INF/view/modules/flow/task/flowTaskHistoricFlow.html")
    public NutMap histoicFlow(@Param("procInsId") String procInsId, @Param("startAct") String startAct, @Param("endAct") String endAct) {
        NutMap data = NutMap.NEW();
        if (Strings.isNotBlank(procInsId)) {
            data.setv("histoicFlowList", flowTaskService.histoicFlowList(procInsId, startAct, endAct));
        }
        return data;
    }

    /**
     * 获取流程列表
     *
     * @param category 流程分类
     */
    @At("/processDataList")
    @Ok("json")
    public LayuiTableDataListVO processDataList(String category, HttpServletRequest request) {
        return flowProcessDefinitionService.processList(LayuiTableDataListVO.get(request), category);
    }


    /**
     * 获取当前流程节点扩展属性信息
     *
     * @param taskDefKey 当前节点key
     * @param procDefId  流程定义ID
     */
    @At("/getUserTaskExtension")
    @Ok("json:{locked:'formDataDynamicAssignment',nullAsEmtry:true,nullBooleanAsFalse:true}")
    public UserTaskExtensionDTO getUserTaskExtension(String taskDefKey, String procDefId) {
        //节点流程信息
        UserTaskExtensionDTO dto = flowProcessDefinitionService.getUserTaskExtension(taskDefKey, procDefId);
        if (dto == null) {
            return UserTaskExtensionDTO.NEW();
        }
        return dto;
    }

    /**
     * 启动流程
     *
     * @param flowTaskVO FlowEntity
     */
    @At("/start")
    @Ok("json")
    @POST
    @TryCatchMsg("启动流程失败!")
    public AjaxResult start(FlowTaskVO flowTaskVO) {
        Authentication.setAuthenticatedUserId(getSessionUserAccount().getUserName());
        flowTaskService.startProcess(flowTaskVO.getProcDefKey(), flowTaskVO.getBusinessId(), getSessionUserAccount().getUserName(), getSessionUserAccount().getDeptId(), getSessionRoleCodes());
        return AjaxResult.sucess();
    }

    /**
     * 签收任务
     *
     * @param taskId 任务ID
     */
    @At("/claim")
    @Ok("json")
    @POST
    @TryCatchMsg("该任务已被其他人,签收失败!")
    public AjaxResult claim(String taskId) {
        flowTaskService.claim(taskId, getSessionUserAccount().getUserName());
        return AjaxResult.sucess("签收成功");
    }

    /**
     * 委托任务
     *
     * @param taskId 任务ID
     */
    @At("/delegate")
    @Ok("json")
    @POST
    @TryCatchMsg("委托失败!")
    public AjaxResult delegate(String taskId, String userName) {
        if (Strings.isBlank(userName) || Strings.isBlank(taskId)) {
            return AjaxResult.error("参数异常");
        }
        flowTaskService.delegateTask(taskId, userName);
        return AjaxResult.sucess("委托成功");
    }

    /**
     * 取消签收任务
     *
     * @param taskId 任务ID
     */
    @At("/unclaim")
    @Ok("json")
    @POST
    @TryCatchMsg("取消签收失败！")
    public AjaxResult unclaim(String taskId) {
        flowTaskService.unclaim(taskId, getSessionUserAccount().getUserName());
        return AjaxResult.sucess("取消签收成功");
    }


    /**
     * 转派任务
     *
     * @param taskId   任务ID
     * @param userName 接收人
     * @param reason   原因
     */
    @At("/transfer")
    @Ok("json")
    @POST
    @TryCatchMsg("转派失败!")
    public AjaxResult transferTask(String taskId, String userName, String reason) {
        if (Strings.isBlank(reason)) {
            return AjaxResult.error("请输入转派原因");
        }
        if (Strings.isBlank(userName) || Strings.isBlank(taskId)) {
            return AjaxResult.error("参数异常");
        }
        // 设置当前流程任务办理人
        Authentication.setAuthenticatedUserId(getSessionUserAccount().getUserName());
        flowTaskService.transferTask(taskId, userName, reason);
        return AjaxResult.sucess("委托成功");
    }

    /**
     * 完成任务
     *
     * @param flowTaskVO FlowEntity
     */
    @At("/complete")
    @POST
    @Ok("json")
    public AjaxResult complete(FlowTaskVO flowTaskVO) {
        // 设置当前流程任务办理人
        Authentication.setAuthenticatedUserId(getSessionUserAccount().getUserName());
        flowTaskService.complete(flowTaskVO, null);
        return AjaxResult.sucess();
    }

    /**
     * 删除任务
     *
     * @param taskId 流程实例ID
     * @param reason 删除原因
     */
    @At("/deleteTask")
    @Ok("json")
    @POST
    @TryCatchMsg("删除失败！${errorMsg}")
    public AjaxResult deleteTask(String taskId, String reason) {
        if (Strings.isBlank(reason)) {
            return AjaxResult.error("删除失败，请填写删除原因！");
        } else {
            flowTaskService.deleteTask(taskId, reason);
        }
        return AjaxResult.sucess("删除任务成功，任务ID=" + taskId);
    }
}
