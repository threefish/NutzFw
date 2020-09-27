/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/26 11:50:26
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.tabledata.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.annotation.SysLog;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.util.ViewUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.sys.entity.DataTable;
import com.nutzfw.modules.sys.entity.TableFields;
import com.nutzfw.modules.sys.service.DataTableService;
import com.nutzfw.modules.tabledata.biz.DataMaintainBiz;
import com.nutzfw.modules.tabledata.entity.DataImportHistory;
import com.nutzfw.modules.tabledata.enums.ControlType;
import com.nutzfw.modules.tabledata.enums.DictDepend;
import com.nutzfw.modules.tabledata.enums.FieldType;
import com.nutzfw.modules.tabledata.service.DataImportHistoryService;
import com.nutzfw.modules.tabledata.thread.CheckDataThread;
import com.nutzfw.modules.tabledata.vo.SingeDataMaintainQueryVO;
import com.zaxxer.hikari.util.DefaultThreadFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Encoding;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.*;

import java.io.File;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/9
 * 描述此类：动态数据表数据查询维护
 */
@IocBean
@At("/sysDataMaintain")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class DataMaintainAction extends BaseAction {

    /**
     * 数据导入线程
     */
    private static ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>(100),
            new DefaultThreadFactory("数据导入线程", false));
    @Inject
    DataTableService tableService;
    @Inject
    DataMaintainBiz dataMaintainBiz;
    @Inject
    DataImportHistoryService importHistoryService;
    @Inject("refer:$ioc")
    Ioc ioc;
    @Inject("java:$conf.get('attach.extensions')")
    private String extensions;
    @Inject("java:$conf.get('attach.extensions.imgs')")
    private String imgsExtensions;

    @GET
    @At("/index")
    @Ok("btl:WEB-INF/view/sys/data/maintain/index.html")
    @RequiresPermissions("sysDataQueryMaintain.index")
    @AutoCreateMenuAuth(name = "用户数据维护", icon = "fa-wrench", parentPermission = "sys.maintain")
    public void index() {
    }

    @GET
    @At("/singleIndex")
    @Ok("btl:WEB-INF/view/sys/data/maintain/singleIndex.html")
    @RequiresPermissions("sysDataQueryMaintain.singleIndex")
    @AutoCreateMenuAuth(name = "单表数据维护", icon = "fa-wrench", parentPermission = "sys.maintain")
    public void singleIndex() {
    }

    /**
     * 筛选条件页面
     */
    @GET
    @At("/filterBox")
    @Ok("btl:WEB-INF/view/sys/data/maintain/filterBox.html")
    public NutMap filterBox(@Param("tableId") int tableId) {
        DataTable dataTable = tableService.fetchAllFields(tableId);
        List<TableFields> list = new ArrayList<>();
        dataTable.getFields().stream().filter(fields ->
                (fields.getFieldType() != FieldType.SingleAttach.getValue() && fields.getFieldType() != FieldType.MultiAttach.getValue()) && fields.getDictDepend() == DictDepend.NONE.getValue()
        ).forEach(tableFields -> list.add(tableFields));
        List<SingeDataMaintainQueryVO> data = new ArrayList<>();
        list.forEach(fields ->
                data.add(SingeDataMaintainQueryVO.builder()
                        .id(fields.getId())
                        .name(fields.getName())
                        .fieldName(fields.getFieldName())
                        .sysCode(fields.getDictSysCode())
                        .startVal("")
                        .endVal("")
                        .val("")
                        .isDate(fields.getFieldType() == FieldType.Date.getValue())
                        .dateFormat(fields.getControlType() == ControlType.Date.getValue() ? "yyyy-MM-dd" : "")
                        .joiner("=")
                        .build())
        );
        return NutMap.NEW().setv("fields", list).setv("fieldsJson", Json.toJson(data, JsonFormat.compact())).setv("table", dataTable);
    }

    @GET
    @At("/importHistory")
    @Ok("btl:WEB-INF/view/sys/data/maintain/import/history.html")
    @RequiresPermissions("sysDynamicFrom.importHistory")
    @AutoCreateMenuAuth(name = "数据导入历史", icon = "fa-wrench", parentPermission = "sys.maintain")
    public void importData() {
    }

    @GET
    @At({"/createDownTemplate", "/createDownTemplate/?"})
    @Ok("btl:WEB-INF/view/sys/data/maintain/createDownTemplate.html")
    @RequiresPermissions("sysDynamicFrom.importData")
    public NutMap createDownTemplate(@Param("tableid") int tableId) {
        DataTable dataTable = tableService.fetchAllFields(tableId);
        List<TableFields> list = new ArrayList<>();
        dataTable.getFields().stream().filter(fields ->
                (fields.getFieldType() != FieldType.SingleAttach.getValue() && fields.getFieldType() != FieldType.MultiAttach.getValue()) && fields.getDictDepend() == DictDepend.NONE.getValue()
        ).forEach(tableFields -> list.add(tableFields));
        return NutMap.NEW().setv("fields", list).setv("tableId", tableId);
    }

    /**
     * 生成导入模版
     *
     * @param tableId
     * @param fields
     * @return
     */
    @POST
    @At("/downTemplate")
    @Ok("raw")
    @RequiresPermissions("sysDynamicFrom.importData")
    public Object downTemplate(@Param("tableId") int tableId, @Param("fields") int[] fields) {
        try {
            DataTable dataTable = tableService.fetch(tableId);
            String fileName = MessageFormat.format("{0}_版本{1}.xlsx", dataTable.getName(), dataTable.getVersion());
            File file = dataMaintainBiz.createDownTemplate(tableId, fields).toFile();
            Mvcs.getResp().setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, Encoding.UTF8));
            return file;
        } catch (Exception e) {
            return ViewUtil.toErrorPage("模版生成失败！" + e.getLocalizedMessage());
        }
    }

    /**
     * 取得表数据信息
     *
     * @param pageNum
     * @param pageSize
     * @param tableid
     * @return
     */
    @POST
    @At("/listPage")
    @Ok("json:{locked:'opat|opby|userpass|locked|salt'}")
    @RequiresPermissions("sysDynamicFrom.edit")
    public LayuiTableDataListVO listPage(@Param("pageNum") int pageNum,
                                         @Param("pageSize") int pageSize,
                                         @Param("tableid") int tableid,
                                         @Param("userNameOrRealName") String userNameOrRealName,
                                         @Param("deptIds") String[] deptIds,
                                         @Param("::fields") List<SingeDataMaintainQueryVO> list
    ) {
        Set<String> sessionManagerUserNames = getSessionManagerUserNames();
        if (CollectionUtils.isEmpty(sessionManagerUserNames)) {
            return LayuiTableDataListVO.noData();
        }
        return dataMaintainBiz.listPage(pageNum, pageSize, tableid, userNameOrRealName, deptIds, list, sessionManagerUserNames);
    }

    /**
     * 取得表数据信息
     *
     * @param pageNum
     * @param pageSize
     * @param tableid
     * @return
     */
    @POST
    @At("/listSingeTableDataPage")
    @Ok("json:{locked:'opat|opby|userpass|locked|salt'}")
    @RequiresPermissions("sysDynamicFrom.edit")
    public LayuiTableDataListVO listSingeTableDataPage(@Param("pageNum") int pageNum,
                                                       @Param("pageSize") int pageSize,
                                                       @Param("tableid") int tableid,
                                                       @Param("::fields") List<SingeDataMaintainQueryVO> list
    ) {
        return dataMaintainBiz.listSingeTableDataPage(pageNum, pageSize, tableid, list);
    }

    /**
     * 取得表格列头信息
     *
     * @param tableid
     * @return
     */
    @POST
    @At("/getCols")
    @Ok("json")
    @RequiresPermissions("sysDynamicFrom.edit")
    public AjaxResult getCols(@Param("tableid") int tableid) {
        return AjaxResult.sucess(dataMaintainBiz.getCols(tableid, getSessionRoleIds()));
    }

    /**
     * 取得表格列头信息
     *
     * @param tableid
     * @return
     */
    @POST
    @At("/getUniqueFields")
    @Ok("json")
    @RequiresPermissions("sysDynamicFrom.importData")
    public AjaxResult getUniqueFields(@Param("tableid") int tableid) {
        return AjaxResult.sucess(dataMaintainBiz.getUniqueFields(tableid));
    }

    /**
     * 导入数据进行效验
     *
     * @param tableId
     * @param importType
     * @param attachId
     * @return
     */
    @POST
    @At("/checkImportData")
    @Ok("json")
    @RequiresPermissions("sysDynamicFrom.importData")
    @AutoCreateMenuAuth(name = "数据导入相关功能", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-wrench", parentPermission = "sys.maintain")
    @SysLog(tag = "表单数据导入", template = "为表[${args[1]}] 导入数据 附件ID[${args[3]}] ${re.ok?'成功':'失败'+re.msg}", param = true)
    public AjaxResult checkImportData(@Param("tableId") int tableId, @Param("importType") int importType, @Param("uniqueField") int uniqueField, @Param("attachId") String attachId) {
        try {
            DataTable dataTable = tableService.fetch(tableId);
            DataImportHistory importHistory = DataImportHistory.builder()
                    .attachId(attachId)
                    .tableId(tableId)
                    .importType(importType)
                    .uniqueField(uniqueField)
                    .tableName(dataTable.getName())
                    .tableType(dataTable.getTableType())
                    .userid(getSessionUserAccount().getUserid())
                    .userDesc(getSessionUserAccount().getRealName())
                    //待检查
                    .staus(0)
                    .build();
            importHistoryService.insert(importHistory);
            executorService.submit(new CheckDataThread(ioc, importHistory));
            return AjaxResult.sucess(importHistory.getId(), "数据开始效验中，效验完成后自动进行导入，详情进入【数据导入历史】中查看，请稍候....");
        } catch (Exception e) {
            return AjaxResult.error(e.getLocalizedMessage());
        }
    }

    /**
     * 取得导入历史信息
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @POST
    @At("/listHistoryPage")
    @RequiresPermissions("sysDynamicFrom.importData")
    @Ok("json:{locked:'opby|userpass|username|locked|salt'}")
    public LayuiTableDataListVO listHistoryPage(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize) {
        return importHistoryService.listPage(pageNum, pageSize, Cnd.orderBy().desc("opAt"));
    }


    /**
     * 取得附件字段信息
     */
    @POST
    @At("/getAttachFields")
    @Ok("json")
    @RequiresPermissions("sysDataQueryMaintain.importAttach")
    @AutoCreateMenuAuth(name = "导入附件", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-wrench", parentPermission = "sys.maintain")
    public AjaxResult getAttachFields(@Param("tableId") int tableId) {
        DataTable dataTable = tableService.fetchAllFields(tableId);
        List<NutMap> list = new ArrayList<>();
        dataTable.getFields().stream().filter(fields -> fields.getFieldType() == FieldType.SingleAttach.getValue() || fields.getFieldType() == FieldType.MultiAttach.getValue()).forEach(tableFields -> {
                    boolean isImg = tableFields.getControlType() == ControlType.Img.getValue();
                    String suffix;
                    if (Strings.isEmpty(Strings.sNull(tableFields.getAttachSuffix()).trim())) {
                        if (isImg) {
                            suffix = imgsExtensions;
                        } else {
                            suffix = extensions;
                        }
                    } else {
                        suffix = tableFields.getAttachSuffix();
                    }
                    NutMap data = new NutMap();
                    data.setv("id", tableFields.getId())
                            .setv("text", tableFields.getName())
                            .setv("isImg", isImg)
                            .setv("suffix", suffix);
                    list.add(data);
                }
        );
        return AjaxResult.sucess(list);
    }

    /**
     * 批量导入附件数据
     *
     * @param tableId   表ID
     * @param filedId   字段ID
     * @param attachIds 附件列表
     * @return
     */
    @POST
    @At("/importAttach")
    @Ok("json")
    @RequiresPermissions("sysDataQueryMaintain.importAttach")
    public AjaxResult importFileAttach(@Param("tableId") int tableId, @Param("filedId") int filedId, @Param("uniqueField") int uniqueField, @Param("attachIds") String[] attachIds) {
        String msg = dataMaintainBiz.importFileAttach(tableId, filedId, attachIds, uniqueField, getSessionUserAccount());
        if (msg == null) {
            return AjaxResult.sucess("导入完成");
        }
        return AjaxResult.error(msg);
    }

}
