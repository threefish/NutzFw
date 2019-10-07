/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.tabledata.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.util.ViewUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.sys.service.DataTableService;
import com.nutzfw.modules.sys.service.TableFieldsService;
import com.nutzfw.modules.tabledata.biz.DataMaintainBiz;
import com.nutzfw.modules.tabledata.biz.DataQueryBiz;
import com.nutzfw.modules.tabledata.biz.impl.DataQueryBizImpl;
import com.nutzfw.modules.tabledata.dto.DataQueryDTO;
import com.nutzfw.modules.tabledata.enums.TableType;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Encoding;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.*;
import org.nutz.repo.Base64;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/7/27
 * 描述此类：
 */
@IocBean
@At("/sysDataQuery")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class DataQueryAction extends BaseAction {

    @Inject
    TableFieldsService fieldsService;

    @Inject
    DataTableService tableService;

    @Inject
    DataMaintainBiz dataMaintainBiz;

    @GET
    @At("/advancedSearch")
    @Ok("btl:WEB-INF/view/sys/data/query/advancedSearch.html")
    @RequiresPermissions("sys.advancedSearch")
    @AutoCreateMenuAuth(name = "高级查询", icon = "fa-wrench", parentPermission = "sys.maintain")
    public NutMap advancedSearch(@Param("tableType") String tableType) {
        return NutMap.NEW().setv("tableType", tableType);
    }


    /**
     * 生成菜单使用
     *
     * @param tableType
     * @return
     */
    @GET
    @At("/advancedSearch?tableType=SingleTable")
    @Ok("btl:WEB-INF/view/sys/data/query/advancedSearch.html")
    @RequiresPermissions("sys.advancedSearch")
    @AutoCreateMenuAuth(name = "单表数据查询", icon = "fa-wrench", parentPermission = "sys.maintain")
    public NutMap advancedSingleTableSearch(@Param("tableType") String tableType) {
        return NutMap.NEW().setv("tableType", tableType);
    }

    @GET
    @At("/settingCondition")
    @Ok("btl:WEB-INF/view/sys/data/query/settingCondition.html")
    @RequiresPermissions("sys.advancedSearch")
    public NutMap settingCondition(@Param("filedid") int filedid) {
        return NutMap.NEW().setv("field", fieldsService.fetch(filedid));
    }

    @POST
    @At("/checkSqlCnd")
    @Ok("json")
    @RequiresPermissions("sys.advancedSearch")
    public AjaxResult checkSqlCnd(@Param("where") String where) {
        return AjaxResult.sucess("");
    }

    @POST
    @At("/getCols")
    @Ok("json")
    @RequiresPermissions("sys.advancedSearch")
    public AjaxResult getCols(@Param("cndList") String base64CndListStr, @Param("selectTable") int selectTable, @Param("tableType") TableType tableType) throws UnsupportedEncodingException {
        List<DataQueryDTO> cndList = Json.fromJsonAsList(DataQueryDTO.class, new String(Base64.decode(base64CndListStr), Encoding.UTF8));
        DataQueryBiz dataQueryBiz = new DataQueryBizImpl(cndList, selectTable, tableService, fieldsService, dataMaintainBiz, tableType, getSessionManagerUserNames());
        return AjaxResult.sucess(dataQueryBiz.getCols());
    }


    @At("/queryDataList")
    @POST
    @Ok("json:{nullAsEmtry:true}")
    public LayuiTableDataListVO queryDataList(@Param("cndList") String base64CndListStr, @Param("selectTable") int selectTable, @Param("tableType") TableType tableType, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize) {
        LayuiTableDataListVO dataListVO = new LayuiTableDataListVO();
        try {
            List<DataQueryDTO> cndList = Json.fromJsonAsList(DataQueryDTO.class, new String(Base64.decode(base64CndListStr), Encoding.UTF8));
            if (cndList != null) {
                DataQueryBiz dataQueryBiz = new DataQueryBizImpl(cndList, selectTable, tableService, fieldsService, dataMaintainBiz, tableType, getSessionManagerUserNames());
                dataListVO.setData(dataQueryBiz.getData(pageNum, pageSize));
                dataListVO.setCount(dataQueryBiz.getCount());
            }
        } catch (Exception e) {
            dataListVO.setMsg(e.getMessage());
            dataListVO.setStatus(500);
        }
        return dataListVO;
    }


    @At("/exportExcel")
    @POST
    @Ok("raw")
    public Object exportExcel(@Param("cndList") String base64CndListStr, @Param("exportType") int exportType, @Param("selectTable") int selectTable,@Param("tableType") TableType tableType, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize) throws UnsupportedEncodingException {
        try {
            List<DataQueryDTO> cndList = Json.fromJsonAsList(DataQueryDTO.class, new String(Base64.decode(base64CndListStr), Encoding.UTF8));
            DataQueryBiz dataQueryBiz = new DataQueryBizImpl(cndList, selectTable, tableService, fieldsService, dataMaintainBiz, tableType, getSessionManagerUserNames());
            File file = dataQueryBiz.exportDataToExcle(exportType, pageNum, pageSize);
            StringBuilder fileName = new StringBuilder("查询结果导出");
            if (exportType == 0) {
                fileName.append("第");
                fileName.append(pageNum);
                fileName.append("页数据");
            } else if (exportType == 1) {
                fileName.append("全部数据");
            }
            Mvcs.getResp().setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName.toString() + System.currentTimeMillis() + ".xlsx", Encoding.UTF8));
            return file;
        } catch (Exception e) {
            log.error("文件导出失败！", e);
            return ViewUtil.toErrorPage("文件打包失败！");
        }
    }
}
