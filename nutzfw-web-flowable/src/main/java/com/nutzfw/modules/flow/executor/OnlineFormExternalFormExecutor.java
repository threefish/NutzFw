/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.flow.executor;

import com.nutzfw.core.plugin.flowable.FlowServiceSupport;
import com.nutzfw.core.plugin.flowable.cmd.GetOnlineFormKeyCmd;
import com.nutzfw.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.nutzfw.core.plugin.flowable.extmodel.FormElementModel;
import com.nutzfw.core.plugin.flowable.vo.FlowTaskVO;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.sys.entity.DataTable;
import com.nutzfw.modules.sys.service.DataTableService;
import com.nutzfw.modules.sys.service.RoleService;
import com.nutzfw.modules.tabledata.biz.DataMaintainBiz;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import java.util.List;
import java.util.Map;

/**
 * @author huchuc@vip.qq.com
 * @date: 2021/10/08
 * 在线表单执行器
 */
@Slf4j
@IocBean(name = "onlineFormExternalFormExecutor")
public class OnlineFormExternalFormExecutor implements ExternalFormExecutor {


    @Inject
    DataMaintainBiz dataMaintainBiz;
    @Inject
    DataTableService dataTableService;
    @Inject
    RoleService roleService;

    @Override
    public Map start(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        FormElementModel formElementModel = this.getFormElementModel(flowTaskVO);
        int tableId = Integer.parseInt(formElementModel.getTableId());
        try {
            NutMap data = dataMaintainBiz.formJsonData(Json.toJson(formData), sessionUserAccount);
            List<String> errmsg = dataMaintainBiz.checkTableData(tableId, data, DataMaintainBiz.UNIQUE_FIELD);
            if (errmsg.size() == 0) {
                dataMaintainBiz.saveTableData(tableId, data, sessionUserAccount);
                return data;
            } else {
                throw new RuntimeException(Strings.join("<br>\r\n", errmsg));
            }
        } catch (Exception e) {
            log.error("异常", e);
            throw e;
        }
    }

    @Override
    public String userAudit(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        FormElementModel formElementModel = this.getFormElementModel(flowTaskVO);
        int tableId = Integer.parseInt(formElementModel.getTableId());
        try {
            NutMap data = dataMaintainBiz.formJsonData(Json.toJson(formData), sessionUserAccount);
            List<String> errmsg = dataMaintainBiz.checkTableData(tableId, data, DataMaintainBiz.UNIQUE_FIELD);
            if (errmsg.size() == 0) {
                //TODO  使用 writeBackProccessStatusField 获取回写字段名，并设置流程状态
                dataMaintainBiz.saveTableData(tableId, data, sessionUserAccount);
                return null;
            } else {
                throw new RuntimeException(Strings.join("<br>\r\n", errmsg));
            }
        } catch (Exception e) {
            log.error("异常", e);
            throw e;
        }
    }

    @Override
    public String backToStep(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        throw new RuntimeException("你应该自己实现");
    }

    @Override
    public String addMultiInstance(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        throw new RuntimeException("你应该自己实现");
    }

    @Override
    public Object loadFormData(FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        FormElementModel formElementModel = this.getFormElementModel(flowTaskVO);
        int tableId = Integer.parseInt(formElementModel.getTableId());
        if (Strings.isNotBlank(flowTaskVO.getBusinessId())) {
            DataTable dataTable = dataTableService.fetchAuthReadWriteFields(tableId, roleService.queryRoleIds(sessionUserAccount.getId()));
            List<String> showFields = dataMaintainBiz.getQueryFields(dataTable);
            Sql sql = Sqls.create("SELECT $showFields from $tableName where id=@id");
            sql.setVar("tableName", dataTable.getTableName());
            sql.setVar("showFields", Strings.join(",", showFields));
            sql.setParam(dataTable.getPrimaryKey(), flowTaskVO.getBusinessId());
            sql.setCallback(Sqls.callback.record());
            dataTableService.dao().execute(sql);
            return dataMaintainBiz.coverVueJsFromData(sql.getObject(Record.class), dataTable.getFields());
        }
        //TODO 返回一个基类，有发起人发起时间什么的
        return new Object();
    }

    @Override
    public Map loadFormData(String businessKeyId) {
        return new NutMap();
    }

    @Override
    public Object insertOrUpdateFormData(Map formData) {
        throw new RuntimeException("你应该自己实现");
    }


    @Override
    public String getFormPage(FlowTaskVO flowTaskVO) {
        throw new RuntimeException("你应该自己实现");
    }

    @Override
    public FormElementModel getFormElementModel(FlowTaskVO flowTaskVO) {
        return FlowServiceSupport.managementService().executeCommand(new GetOnlineFormKeyCmd(flowTaskVO.getProcDefId(), flowTaskVO.getTaskDefKey()));
    }

    @Override
    public void beforeCreateUserTask(DelegateExecution execution, UserTask userTask, UserTaskExtensionDTO dto, String processInstanceBusinessKey) {

    }

    @Override
    public void afterCreateUserTask(DelegateExecution execution, UserTask userTask, UserTaskExtensionDTO dto, String processInstanceBusinessKey, TaskEntity taskEntity) {

    }

    @Override
    public String getUniqueName() {
        return "在线表单执行器";
    }


}
