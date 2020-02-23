package com.nutzfw.modules.business.leave.executor;

import com.nutzfw.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.nutzfw.core.plugin.flowable.service.FlowTaskService;
import com.nutzfw.core.plugin.flowable.vo.FlowTaskVO;
import com.nutzfw.modules.business.leave.entity.Leave;
import com.nutzfw.modules.business.leave.service.LeaveService;
import com.nutzfw.modules.flow.executor.ExternalFormExecutor;
import com.nutzfw.modules.organize.entity.UserAccount;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

import java.util.Map;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2020/2/23
 */
@IocBean(name = "leaveExternalFormExecutor")
public class LeaveExecutor implements ExternalFormExecutor {

    @Inject
    LeaveService leaveService;

    @Inject
    FlowTaskService flowTaskService;

    @Override
    public Map start(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        Leave leave = Lang.map2Object(formData, Leave.class);
        leaveService.insert(leave);
        return Lang.obj2map(leave);
    }

    @Override
    public String userAudit(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        Leave leave = Lang.map2Object(formData, Leave.class);
        //自行组织逻辑，按照流程步骤，确定是否更新某些字段-也可以在 NutzFw扩展步骤属性 -> 步骤设置——> 表单数据动态赋值 中使用动态JS脚步设置
        // 部门领导审核环节
        if ("deptLeaderAudit".equals(flowTaskVO.getTaskDefKey())) {
            leave.setDeptLeadText(flowTaskVO.getComment());
        }
        // 人事审核环节
        else if ("hrAudit".equals(flowTaskVO.getTaskDefKey())) {
            leave.setHrText(flowTaskVO.getComment());
        }
        //表单提交数据可能受权限限制数据可能不完整，所以不更新null数据
        leaveService.updateIgnoreNull(leave);
        return null;
    }

    @Override
    public String backToStep(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        return null;
    }

    @Override
    public String addMultiInstance(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        return null;
    }

    @Override
    public Object loadFormData(FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        //自行组织数据，按照流程步骤，确定是否显示某些字段
        if (Strings.isNotBlank(flowTaskVO.getBusinessId())) {
            return loadFormData(flowTaskVO.getBusinessId());
        }
        //一定是新发起的，返回一个空对象回去避免报错
        return new Leave();

    }

    @Override
    public Map loadFormData(String businessKeyId) {
        return Lang.obj2map(leaveService.fetch(businessKeyId));
    }

    @Override
    public Object insertOrUpdateFormData(Map formData) {
        Leave leave = Lang.map2Object(formData, Leave.class);
        return leaveService.insert(leave);
    }

    @Override
    public String getFormPage(FlowTaskVO flowTaskVO) {
        String viewPage = "view.html";
        if (!flowTaskVO.isFinishTask()) {
            viewPage = flowTaskService.getFormKey(flowTaskVO.getProcDefId(), flowTaskVO.getTaskDefKey());
        }
        if (Strings.isBlank(viewPage)) {
            viewPage = "form.html";
        }
        return "/modules/business/general/leave/" + viewPage;
    }

    @Override
    public void beforeCreateUserTask(DelegateExecution execution, UserTask userTask, UserTaskExtensionDTO dto, String processInstanceBusinessKey) {

    }

    @Override
    public void afterCreateUserTask(DelegateExecution execution, UserTask userTask, UserTaskExtensionDTO dto, String processInstanceBusinessKey, TaskEntity taskEntity) {

    }
}