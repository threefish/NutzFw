/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.util.FileUtil;
import com.nutzfw.core.common.util.StringUtil;
import com.nutzfw.core.common.util.ViewUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.sys.entity.DataTable;
import com.nutzfw.modules.sys.entity.RoleField;
import com.nutzfw.modules.sys.entity.TableFields;
import com.nutzfw.modules.sys.enums.FieldAuth;
import com.nutzfw.modules.sys.service.DataTableService;
import com.nutzfw.modules.sys.service.FileAttachService;
import com.nutzfw.modules.sys.service.RoleFieldsService;
import com.nutzfw.modules.tabledata.biz.DataTableBiz;
import com.nutzfw.modules.tabledata.enums.TableType;
import com.nutzfw.modules.tabledata.vo.DbTreeVO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Encoding;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.*;
import org.nutz.plugins.validation.Errors;
import org.nutz.repo.Base64;

import java.net.URLEncoder;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/5
 * 描述此类：
 */
@IocBean
@At("/sysDataTable")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class DataTableAction extends BaseAction {

    @Inject
    DataTableService  tableService;
    @Inject
    DataTableBiz      dataTableBiz;
    @Inject
    FileAttachService fileAttachService;
    @Inject
    RoleFieldsService roleFieldsService;

    @At("/index")
    @Ok("btl:WEB-INF/view/sys/data/table/manager.html")
    @RequiresPermissions("sysDataTable.manager")
    @AutoCreateMenuAuth(name = "在线表单管理", icon = "fa-cogs", parentPermission = "sys.index")
    public void manager() {
    }

    @At("/logicElExpressionHelp")
    @Ok("btl:WEB-INF/view/sys/data/table/logicElExpressionHelp.html")
    public void logicElExpressionHelp() {
    }

    @At("/backupTableStructure/?")
    @Ok("raw")
    @RequiresPermissions("sysDataTable.backupTableStructure")
    @AutoCreateMenuAuth(name = "备份表结构", icon = "fa-cogs", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysDataTable.manager")
    public Object backupTableStructure(@Param("tableId") int tableId) {
        try {
            DataTable dataTable = tableService.fetchAllFields(tableId);
            String data = Json.toJson(dataTable, JsonFormat.tidy());
            byte[] encode = Base64.encodeToByte(data.getBytes(), false);
            Path path = FileUtil.writeTmpFile(encode);
            String fileName = MessageFormat.format("{0}_结构备份_当前版本{1}.bak", dataTable.getName(), dataTable.getVersion());
            Mvcs.getResp().setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, Encoding.UTF8));
            return path.toFile();
        } catch (Exception e) {
            log.error(e);
            return ViewUtil.toErrorPage("备份失败！");
        }
    }

    /**
     * 系统表只会在当前结构上增加字段
     * 用户表则是新建表及结构
     *
     * @param attachId
     * @return
     */
    @At("/revertTableStructure")
    @Ok("json")
    @RequiresPermissions("sysDataTable.revertTableStructure")
    @AutoCreateMenuAuth(name = "还原表结构", icon = "fa-cogs", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysDataTable.manager")
    @Aop(TransAop.READ_UNCOMMITTED)
    public AjaxResult revertTableStructure(@Param("attachId") String attachId) {
        try {
            Path path = fileAttachService.getPath(attachId);
            byte[] encode = Files.readBytes(path.toFile());
            byte[] decode = Base64.decodeFast(encode);
            String json = new String(decode, Encoding.UTF8);
            DataTable dataTable = Json.fromJson(DataTable.class, json);
            tableService.revertTableStructure(dataTable);
            return AjaxResult.sucess("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(e.getLocalizedMessage());
        }
    }

    @POST
    @At("/query")
    @Ok("json")
    @RequiresPermissions("sysDataTable.manager")
    public LayuiTableDataListVO query(@Param("pageNum") int pageNum,
                                      @Param("pageSize") int pageSize,
                                      @Param("status") int status,
                                      @Param("name") String name,
                                      @Param("tableType") String tableType) {
        Cnd cnd = Cnd.where("delFlag", "=", 0);
        if (status != -1) {
            cnd.and("status", "=", status);
        }
        if (!StringUtil.isBlank(name)) {
            cnd.and("name", "like", "%" + name + "%");
        }
        if (!"".equals(tableType)) {
            cnd.and("tableType", "=", tableType);
        }
        return tableService.listPage(pageNum, pageSize, cnd.desc("opAt"));
    }

    @At("/add")
    @GET
    @Ok("btl:WEB-INF/view/sys/data/table/from.html")
    @RequiresPermissions("sysDataTable.add")
    @AutoCreateMenuAuth(name = "创建表单", icon = "fa-cogs", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysDataTable.manager")
    public void add() {
        setRequestAttribute("fields", "[]");
    }

    @At("/edit")
    @GET
    @Ok("btl:WEB-INF/view/sys/data/table/from.html")
    @RequiresPermissions("sysDataTable.edit")
    @AutoCreateMenuAuth(name = "编辑表单", icon = "fa-cogs", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysDataTable.manager")
    public void edit(@Param("id") int id) {
        DataTable dataTable = tableService.fetchAllNotDelectFields(id);
        setRequestAttribute("fields", Json.toJson(dataTable.getFields(), JsonFormat.compact()));
        setRequestAttribute("table", dataTable);
    }

    @At("/synchronize")
    @POST
    @Ok("json")
    @RequiresPermissions("sysDataTable.synchronize")
    @AutoCreateMenuAuth(name = "同步数据库", icon = "fa-cogs", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysDataTable.manager")
    public AjaxResult synchronize(@Param("id") int id, @Param("synchronizeType") int synchronizeType) {
        try {
            String msg = dataTableBiz.synchronize(id, synchronizeType);
            if (null == msg) {
                return AjaxResult.sucess("同步成功");
            } else {
                return AjaxResult.error(msg);
            }
        } catch (Exception e) {
            log.error("同步数据库错误", e);
            return AjaxResult.error(e.getLocalizedMessage());
        }
    }

    @At("/save")
    @POST
    @Ok("json")
    @RequiresPermissions("sysDataTable.add")
    @Aop(TransAop.READ_UNCOMMITTED)
    public AjaxResult save(@Param("table") DataTable table, @Param("list") TableFields[] list, Errors errors) {
        if (errors.hasError()) {
            return AjaxResult.error(errors.getErrorsList().iterator().next());
        }
        if (list.length == 0) {
            return AjaxResult.error("请至少添加一个字段！");
        }
        try {
            DataTable oldTable = null;
            if (table.getId() != 0) {
                //更新检查原始对象是否存在
                oldTable = tableService.fetchAllFields(table.getId());
                if (oldTable == null) {
                    return AjaxResult.error("表对象不存在！");
                }
            }
            if (tableService.checkDuplicateByName(table.getName(), table.getId())) {
                return AjaxResult.error("表名已经存在，请使用其他名称！");
            }
            Set<String> fieldNames = new HashSet<>();
            for (TableFields fields : list) {
                if (fieldNames.contains(fields.getName())) {
                    return AjaxResult.error("字段名称[" + fields.getName() + "]存在重复，请使用其他名称！");
                } else {
                    fieldNames.add(fields.getName());
                }
            }
            fieldNames.clear();
            dataTableBiz.save(table, oldTable, list);
            return AjaxResult.sucess("操作成功");

        } catch (Exception e) {
            return AjaxResult.error("操作失败!" + e.getLocalizedMessage());
        }
    }

    @At("/del")
    @POST
    @Ok("json")
    @RequiresPermissions("sysDataTable.del")
    @AutoCreateMenuAuth(name = "删除", icon = "fa-cogs", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysDataTable.manager")
    public AjaxResult del(@Param("id") int id) {
        return AjaxResult.error("暂不提供此操作");
    }

    /**
     * 按权限取得所有能看到的数据表及字段
     *
     * @return
     */
    @At("/authDbTree")
    @POST
    @Ok("json")
    public List<DbTreeVO> authDbTree(@Param("id") int id, @Param("type") int type, @Param("tableType") String tableType) {
        List<DbTreeVO> vos = new ArrayList<>();
        if (id == 0 && type == 0) {
            Cnd cnd = Cnd.NEW();
            cnd.andEX("TableType", "=", tableType);
            List<DataTable> dataTable = tableService.query(cnd);
            dataTable.forEach(table ->
                    vos.add(new DbTreeVO(table.getId(), 0, table.getName(), table.getTableType(), table.getId(), 0, 0, true))
            );
        } else {
            DataTable dataTable = tableService.fetchAuthReadFields(id, getSessionRoleIds());
            dataTable.getFields().forEach(fields ->
                    vos.add(new DbTreeVO(fields.getId(), 1, fields.getName(), dataTable.getTableType(), dataTable.getId(), fields.getId(), fields.getFieldType(), false))
            );
        }
        return vos;
    }

    @POST
    @At("/relationTable")
    @Ok("json")
    public LayuiTableDataListVO relationTable(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize, @Param("key") String key) {
        Cnd cnd = Cnd.where("delFlag", "=", 0);
        cnd.andEX("name", "like", "%" + key + "%");
        return tableService.listPage(pageNum, pageSize, cnd.desc("opAt"));
    }

    /**
     * 按权限取得所有能看到表单
     *
     * @return
     */
    @At("/authTable")
    @POST
    @Ok("json")
    public List<DbTreeVO> authTable() {
        List<DbTreeVO> vos = new ArrayList<>();
        List<DataTable> dataTable = tableService.query(Cnd.where("TableType", "=", TableType.PrimaryTable).or("TableType", "=", TableType.Schedule));
        dataTable.forEach(table -> vos.add(new DbTreeVO(table.getId(), 0, table.getName(), table.getTableType(), table.getId(), 0, 0, false)));
        return vos;
    }

    /**
     * 按权限取得所有能看到表单
     *
     * @return
     */
    @At("/authPrimaryTable")
    @POST
    @Ok("json")
    public List<DbTreeVO> authPrimaryTable() {
        List<DbTreeVO> vos = new ArrayList<>();
        List<DataTable> dataTable = tableService.query(Cnd.where("TableType", "=", TableType.PrimaryTable));
        dataTable.forEach(table -> vos.add(new DbTreeVO(table.getId(), 0, table.getName(), table.getTableType(), table.getId(), 0, 0, false)));
        return vos;
    }

    /**
     * 按权限取得所有能看到表单
     *
     * @return
     */
    @At("/authScheduleTable")
    @POST
    @Ok("json")
    public List<DbTreeVO> authScheduleTable() {
        List<DbTreeVO> vos = new ArrayList<>();
        List<DataTable> dataTable = tableService.query(Cnd.where("TableType", "=", TableType.Schedule));
        dataTable.forEach(table -> vos.add(new DbTreeVO(table.getId(), 0, table.getName(), table.getTableType(), table.getId(), 0, 0, false)));
        return vos;
    }

    /**
     * 按权限取得所有能看到的单表数据
     *
     * @return
     */
    @At("/authSingleTable")
    @POST
    @Ok("json")
    public List<DbTreeVO> authSingleTable() {
        List<DbTreeVO> vos = new ArrayList<>();
        List<DataTable> dataTable = tableService.query(Cnd.where("TableType", "=", TableType.SingleTable));
        dataTable.forEach(table -> vos.add(new DbTreeVO(table.getId(), 0, table.getName(), table.getTableType(), table.getId(), 0, 0, false)));
        return vos;
    }

    @At("/allFileds")
    @Ok("json")
    public LayuiTableDataListVO allFileds(@Param("tableId") int tableId, @Param("roleId") String roleId) {
        if (tableId > 0) {
            DataTable dataTable = tableService.fetchAllFields(tableId);
            List<RoleField> roleFieldList = new ArrayList<>();
            if (Strings.isNotBlank(roleId) && Strings.splitIgnoreBlank(roleId).length == 1) {
                roleFieldList = roleFieldsService.query(Cnd.where("roleId", "=", roleId).and("tableId", "=", tableId));
            }
            List<RoleField> finalRoleFields = roleFieldList;
            List<RoleField> list = new ArrayList<>();
            dataTable.getFields().forEach(field -> {
                Optional<RoleField> optional = finalRoleFields.stream().filter(r -> r.getFieldId() == field.getId()).findAny();
                RoleField roleFields = optional.isPresent() ? optional.get() : RoleField.builder().auth(FieldAuth.hide).name(field.getName()).roleId(roleId).fieldId(field.getId()).tableId(tableId).build();
                roleFields.setAuths(new ArrayList<>());
                roleFields.getAuths().add(FieldAuth.hide);
                roleFields.getAuths().add(FieldAuth.r);
                if (!field.isLogic()) {
                    roleFields.getAuths().add(FieldAuth.rw);
                }
                list.add(roleFields);
            });
            return LayuiTableDataListVO.allData(list);
        }
        return LayuiTableDataListVO.noData();
    }
}
