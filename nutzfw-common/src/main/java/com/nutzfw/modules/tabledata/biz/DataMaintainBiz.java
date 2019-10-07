/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.tabledata.biz;

import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.sys.entity.DataTable;
import com.nutzfw.modules.sys.entity.TableFields;
import com.nutzfw.modules.tabledata.enums.TableType;
import com.nutzfw.modules.tabledata.vo.SingeDataMaintainQueryVO;
import com.nutzfw.modules.tabledata.vo.TableColsVO;
import org.nutz.dao.entity.Record;
import org.nutz.lang.util.NutMap;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/9
 * 描述此类：
 */
public interface DataMaintainBiz {

    /***
     * 字典分隔符
     */
    String DELIMITER = "→";

    String UNIQUE_FIELD = "userid";

    List<TableColsVO> getCols(int tableid, Set<String> sessionRoleIds);

    /**
     * 不固定列
     *
     * @param tableid
     * @param sessionRoleIds
     * @return
     */
    List<TableColsVO> getColsNotFix(int tableid, Set<String> sessionRoleIds);

    LayuiTableDataListVO listUserDataPage(int pageNum, int pageSize, int tableid, String userid,Set<String> sessionManagerUserNames);

    LayuiTableDataListVO listPage(int pageNum, int pageSize, int tableid, String userNameOrRealName, String[] deptIds, List<SingeDataMaintainQueryVO> list, Set<String> sessionManagerUserNames);

    /**
     * 取得表数据 字段KEY
     *
     * @param tableName
     * @param sourceId
     * @return
     */
    Record fetchData(String tableName, String sourceId);

    /**
     * 取得表数据 字段KEY
     *
     * @param sourceId
     * @param dataTable
     * @return
     */
    Record fetchData(String sourceId, DataTable dataTable);

    /**
     * 取得表数据 中文KEY
     *
     * @param tableName
     * @param sourceId
     * @return
     */
    NutMap fetchDataToView(String tableName, String sourceId);

    /**
     * 取得用户表数据
     *
     * @param tableid
     * @param userName
     * @return
     */
    List<Record> queryUserDataList(int tableid, String userName,Set<String> sessionManagerUserNames);


    boolean isMyData(String tableName, String userName, String sourceId);

    LayuiTableDataListVO listDataByCnd(int pageNum, int pageSize, int tableid, String userid, String userName, String likeUserNameOrRealName, String[] deptIds, List<SingeDataMaintainQueryVO> list, Set<String> sessionManagerUserNames);

    List<String> getQueryFields(DataTable dataTable);

    List<Record> coverData(List<Record> recordList, List<TableFields> tableFieldsList);

    Record coverData(Record record, List<TableFields> tableFieldsList);

    NutMap coverData(NutMap data, List<TableFields> tableFieldsList);

    Record coverDataToView(Record record, List<TableFields> tableFieldsList);

    Record coverDataToView(NutMap nutMap, List<TableFields> tableFieldsList);

    Record coverVueJsFromData(Record record, List<TableFields> fieldList);

    List<String> checkTableData(int tableId, NutMap data, String uniqueField);

    List<String> checkImportTableData(List<TableFields> fieldList, NutMap data, String uniqueField, int importType, TableType tableType);

    void saveTableData(int tableId, NutMap data, UserAccount userAccount);

    void saveReviewData(int tableId, NutMap data, String addUserId);

    void delToReview(int tableId, String[] sourceIds, String delUserId);

    NutMap coverSaveTableData(List<TableFields> tableFields, NutMap data);

    String getFieldsRegx(List<TableFields> fields);

    Path createDownTemplate(int tableId, int[] fields) throws IOException;

    String importFileAttach(int tableId, int filedId, String[] attachIds, int uniqueField, UserAccount userAccount);

    LayuiTableDataListVO listSingeTableDataPage(int pageNum, int pageSize, int tableid, List<SingeDataMaintainQueryVO> list);

    List<NutMap> getUniqueFields(int tableid);

    NutMap formJsonData(String dataStr, UserAccount userAccount);

    /**
     * 只针对用户主表，从表慎用
     *
     * @param tableName
     * @param userName
     * @return
     */
    String getSourceId(String tableName, String userName);

}
