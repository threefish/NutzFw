/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.tabledata.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.annotation.SysLog;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.util.ViewUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.sys.biz.DictBiz;
import com.nutzfw.modules.sys.entity.DataTable;
import com.nutzfw.modules.sys.entity.TableFields;
import com.nutzfw.modules.sys.service.DataTableService;
import com.nutzfw.modules.sys.service.RoleService;
import com.nutzfw.modules.sys.service.TableFieldsService;
import com.nutzfw.modules.tabledata.biz.DataMaintainBiz;
import com.nutzfw.modules.tabledata.enums.TableType;
import com.nutzfw.modules.tabledata.util.DataUtil;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.*;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/21
 * 描述此类：动态表单 人员信息维护
 */
@IocBean
@At("/sysDynamicFrom")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class DynamicFromAction extends BaseAction {


    @Inject
    TableFieldsService fieldsService;

    @Inject
    DataTableService tableService;

    @Inject
    DataMaintainBiz dataMaintainBiz;

    @Inject
    UserAccountService userAccountService;
    @Inject
    DictBiz            dictBiz;
    @Inject
    RoleService        roleService;

    /**
     * 编辑查看
     *
     * @param tableId            表ID
     * @param userid             用户
     * @param sourceId           数据表源数据ID
     * @param look               是否查看
     * @param sessionUserAccount 当前操作人员（可能是管理员或普通用户）
     * @return
     */
    @Ok("btl:WEB-INF/view/sys/dynamicfrom/edit_1.html")
    @GET
    @At("/edit")
    @RequiresPermissions("sysDynamicFrom.edit")
    @AutoCreateMenuAuth(name = "数据编辑", icon = "fa-wrench", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sys.maintain")
    public View edit(@Param("tableId") int tableId, @Param("userid") String userid, @Param("sourceId") String sourceId, @Param("look") boolean look, UserAccount sessionUserAccount) {
        DataTable dataTable;
        if (sessionUserAccount == null || sessionUserAccount.getId() == null) {
            sessionUserAccount = getSessionUserAccount();
        }
        if (look) {
            dataTable = tableService.fetchAuthReadFields(tableId, roleService.queryRoleIds(sessionUserAccount.getId()));
        } else {
            dataTable = tableService.fetchAuthReadWriteFields(tableId, roleService.queryRoleIds(sessionUserAccount.getId()));
        }
        String oldData = "";
        if (!Strings.isBlank(sourceId)) {
            JsonFormat jsonFormat = JsonFormat.tidy();
            jsonFormat.setNullAsEmtry(false);
            List<String> showFields = dataMaintainBiz.getQueryFields(dataTable);
            Sql sql = Sqls.create("SELECT $showFields from $tableName where id=@id");
            sql.setVar("tableName", dataTable.getTableName());
            sql.setVar("showFields", Strings.join(",", showFields));
            sql.setParam(dataTable.getPrimaryKey(), sourceId);
            sql.setCallback(Sqls.callback.record());
            tableService.dao().execute(sql);
            //编辑
            Record record = sql.getObject(Record.class);
            userid = record.getString("userid");
            oldData = Json.toJson(dataMaintainBiz.coverVueJsFromData(record, dataTable.getFields()), jsonFormat);
        }
        setRequestAttribute("user", new NutMap());
        //是否有可以显示的字段
        boolean hasAnyDisplay = dataTable.getFields().stream().anyMatch(TableFields::isFromDisplay);
        setRequestAttribute("hasAnyDisplay", hasAnyDisplay);
        setRequestAttribute("oldData", oldData);
        setRequestAttribute("table", dataTable);
        setRequestAttribute("userid", userid);
        if (Strings.isNotBlank(userid)) {
            //是与人员相关的数据
            UserAccount userAccount = userAccountService.fetch(userid);
            setRequestAttribute("user", userAccount);
        }
        if (look) {
            return ViewUtil.toViewPage("/sys/dynamicfrom/look_" + dataTable.getFormTemplate());
        } else {
            return ViewUtil.toViewPage("/sys/dynamicfrom/edit_" + dataTable.getFormTemplate());
        }
    }

    /**
     * 编辑查看-新增或保存后需要审核
     *
     * @param tableId  表ID
     * @param userid   用户
     * @param sourceId 数据表源数据ID
     * @return
     */
    @Ok("btl:WEB-INF/view/sys/dynamicfrom/edit_1.html")
    @GET
    @At("/editToReview")
    @RequiresPermissions("sysDynamicFrom.editToReview")
    @AutoCreateMenuAuth(name = "数据变更后需要审核", icon = "fa-wrench", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sys.maintain")
    public View editToReview(@Param("tableId") int tableId, @Param("userid") String userid, @Param("sourceId") String sourceId) {
        setRequestAttribute("needReview", true);
        return edit(tableId, userid, sourceId, false, getSessionUserAccount());
    }

    @Ok("json")
    @POST
    @At("/canAdd")
    @RequiresPermissions("sysDynamicFrom.edit")
    public AjaxResult canAdd(@Param("tableId") int tableId, @Param("userid") String userid) {
        DataTable dataTable = tableService.fetch(tableId);
        //用户表无记录
        boolean userTable = tableService.count(dataTable.getTableName(), Cnd.where("userid", "=", userid)) == 0;
        if (dataTable.getTableType() == TableType.PrimaryTable && userTable) {
            return AjaxResult.sucess("可以新增");
        } else if (dataTable.getTableType() == TableType.SingleTable || dataTable.getTableType() == TableType.Schedule) {
            return AjaxResult.sucess("可以新增");
        }
        return AjaxResult.error("主表每个人员只能存在一条记录");
    }

    /**
     * 取得用户的主表ID
     *
     * @param tableId
     * @param userid
     * @return
     */
    @Ok("json")
    @POST
    @At("/getSourceId")
    @RequiresPermissions(value = {"sysDynamicFrom.edit", "sysOrganize.userReview.agreeReview"}, logical = Logical.OR)
    public AjaxResult getSourceId(@Param("tableId") int tableId, @Param("userid") String userid) {
        DataTable dataTable = tableService.fetch(tableId);
        if (dataTable.getTableType() == TableType.PrimaryTable) {
            Record record = tableService.dao().fetch(dataTable.getTableName(), Cnd.where("userid", "=", userid), "id");
            if (null == record) {
                return AjaxResult.error("无记录存在");
            }
            return AjaxResult.sucess(record.getString("id"));
        }
        return AjaxResult.error("附表无法通过接口取得主键");
    }

    @Ok("json")
    @POST
    @At("/saveData")
    @RequiresPermissions("sysDynamicFrom.saveData")
    @AutoCreateMenuAuth(name = "数据保存", icon = "fa-wrench", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sys.maintain")
    @SysLog(tag = "数据管理", template = "直接修改模式 保存表[${args[0]}]信息 ${re.ok?'成功':'失败'+re.msg}", result = true, param = true)
    public AjaxResult saveData(@Param("tableId") int tableId, @Param("data") String dataStr, @Attr(Cons.SESSION_USER_KEY) UserAccount userAccount) {
        try {
            NutMap data = dataMaintainBiz.formJsonData(dataStr, userAccount);
            List<String> errmsg = dataMaintainBiz.checkTableData(tableId, data, DataMaintainBiz.UNIQUE_FIELD);
            if (errmsg.size() == 0) {
                dataMaintainBiz.saveTableData(tableId, data, userAccount);
                return AjaxResult.sucess("操作成功");
            } else {
                return AjaxResult.error(Strings.join("<br>\r\n", errmsg));
            }
        } catch (Exception e) {
            log.error(e);
            return AjaxResult.error("操作失败!" + e.getLocalizedMessage());
        }
    }

    @Ok("json")
    @POST
    @At("/saveReviewData")
    @RequiresPermissions("sysDynamicFrom.saveReviewData")
    @AutoCreateMenuAuth(name = "保存审核数据", icon = "fa-wrench", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sys.maintain")
    @SysLog(tag = "数据管理", template = "审核模式 保存表[${args[0]}]信息 ${re.ok?'成功':'失败'+re.msg}", result = true, param = true)
    public AjaxResult saveReviewData(@Param("tableId") int tableId, @Param("data") String dataStr, @Attr(Cons.SESSION_USER_KEY) UserAccount userAccount) {
        try {
            NutMap data = dataMaintainBiz.formJsonData(dataStr, userAccount);
            if (!"".equals(data.getString("id", ""))) {
                data = DataUtil.coverInsertData(data, userAccount);
            } else {
                data = DataUtil.coverUpdateData(data, userAccount, data.getInt("update_version", 0));
            }
            List<String> errmsg = dataMaintainBiz.checkTableData(tableId, data, DataMaintainBiz.UNIQUE_FIELD);
            if (errmsg.size() == 0) {
                dataMaintainBiz.saveReviewData(tableId, data, getSessionUserAccount().getId());
                return AjaxResult.sucess("操作成功！请等待审核！");
            } else {
                return AjaxResult.error(Strings.join("<br>\r\n", errmsg));
            }
        } catch (Exception e) {
            log.error(e);
            return AjaxResult.error("操作失败!" + e.getLocalizedMessage());
        }
    }

    @Ok("json")
    @POST
    @At("/del")
    @RequiresPermissions("sysDynamicFrom.del")
    @AutoCreateMenuAuth(name = "数据删除", icon = "fa-wrench", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sys.maintain")
    @SysLog(tag = "数据管理", template = "数据删除表[{$args[0]}]信息 记录ID[${args[1]}] ${re.ok?'成功':'失败'+re.msg}", result = true, param = true)
    public AjaxResult del(@Param("tableId") int tableId, @Param("sourceIds") String[] sourceIds) {
        try {
            DataTable dataTable = tableService.fetch(tableId);
            if (dataTable.isSystem()) {
                return AjaxResult.error("操作失败!系统表数据不允许删除！");
            }
            tableService.dao().clear(dataTable.getTableName(), Cnd.where("id", "in", sourceIds));
            return AjaxResult.sucess("操作成功");
        } catch (Exception e) {
            log.error(e);
            return AjaxResult.error("操作失败!" + e.getLocalizedMessage());
        }
    }

    @Ok("json")
    @POST
    @At("/delToReview")
    @RequiresPermissions("sysDynamicFrom.delToReview")
    @AutoCreateMenuAuth(name = "数据删除", icon = "fa-wrench", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sys.maintain")
    @SysLog(tag = "数据管理", template = "数据删除表[{$args[0]}]信息 记录ID[${args[1]}] ${re.ok?'成功':'失败'+re.msg}", result = true, param = true)
    public AjaxResult delToReview(@Param("tableId") int tableId, @Param("sourceIds") String[] sourceIds) {
        try {
            if (sourceIds != null && sourceIds.length > 0) {
                dataMaintainBiz.delToReview(tableId, sourceIds, getSessionUserAccount().getId());
                return AjaxResult.sucess("操作成功！请等待审核！");
            } else {
                return AjaxResult.error("未选择数据");
            }
        } catch (Exception e) {
            log.error(e);
            return AjaxResult.error("操作失败!" + e.getLocalizedMessage());
        }
    }

    /**
     * 变更依赖字段值的
     *
     * @param fieldId
     * @param selectDictId
     * @return
     */
    @Ok("json")
    @POST
    @At("/dictDependentChange")
    public AjaxResult del(@Param("fieldId") int fieldId, @Param("fieldName") String fieldName, @Param("selectDictId") int selectDictId) {
        TableFields tableFields;
        if (fieldId <= 0) {
            tableFields = fieldsService.fetch(Cnd.where("fieldName", "=", fieldName));
        } else {
            tableFields = fieldsService.fetch(fieldId);
        }
        List<TableFields> dictDependFieldIdList = fieldsService.query(Cnd.where("tableId", "=", tableFields.getTableId()).and("dictDependFieldId", "=", tableFields.getId()));
        return AjaxResult.sucess(dictBiz.dictDependentChangeList(selectDictId, tableFields.getDictSysCode(), dictDependFieldIdList));
    }

}
