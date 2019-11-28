/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.tabledata.biz.impl;

import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.util.excel.PoiExcelUtil;
import com.nutzfw.modules.sys.entity.TableFields;
import com.nutzfw.modules.sys.service.DataTableService;
import com.nutzfw.modules.sys.service.TableFieldsService;
import com.nutzfw.modules.tabledata.biz.DataMaintainBiz;
import com.nutzfw.modules.tabledata.biz.DataQueryBiz;
import com.nutzfw.modules.tabledata.dto.DataQueryDTO;
import com.nutzfw.modules.tabledata.enums.TableType;
import com.nutzfw.modules.tabledata.vo.TableColsVO;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.lang.Strings;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/7/30
 * 描述此类：
 */
public class DataQueryBizImpl implements DataQueryBiz {

    /**
     * 查询结果列主键别名前缀
     * 例：RID_15 表示tableid等于15
     */
    static final String RID = "RID_";
    List<DataQueryDTO> dataQueryDTOS;
    int                selectTable;
    DataTableService   tableService;
    TableFieldsService fieldsService;
    DataMaintainBiz    dataMaintainBiz;
    /**
     * 所有字段
     */
    List<TableFields>  fields;
    String             sql;
    /**
     * 管理人员范围
     */
    Set<String>        sessionManagerUserNames;
    /**
     * 查询类型
     */
    TableType          tableType;

    public DataQueryBizImpl(List<DataQueryDTO> dataQueryDTOS, Integer selectTable, DataTableService tableService, TableFieldsService fieldsService, DataMaintainBiz dataMaintainBiz, TableType tableType, Set<String> sessionManagerUserNames) {
        this.dataQueryDTOS = dataQueryDTOS;
        this.selectTable = selectTable;
        this.fieldsService = fieldsService;
        this.tableService = tableService;
        this.dataMaintainBiz = dataMaintainBiz;
        this.tableType = tableType;
        this.sessionManagerUserNames = sessionManagerUserNames;
    }

    /**
     * 取得全部字段
     *
     * @return
     */
    private List<TableFields> getFields() {
        if (this.fields == null) {
            Set<Integer> fieldIdSets = new HashSet<>(dataQueryDTOS.size());
            dataQueryDTOS.forEach(dataQueryDTO -> fieldIdSets.add(dataQueryDTO.getId()));
            this.fields = fieldsService.query(Cnd.where("id", "in", fieldIdSets));
        }
        return fields;
    }


    @Override
    public String getSql() {
        if (this.sql == null) {
            StringBuilder sqlBuilder = new StringBuilder("SELECT $showFields FROM $fromTables WHERE 1=1 $whereCnds $groupBys order by $orderBys ");
            Sql querySql = Sqls.create(sqlBuilder.toString());
            querySql.vars().set("fromTables", getFromTables().toString());
            querySql.vars().set("showFields", Strings.join(",", getShowFields()));
            querySql.vars().set("whereCnds", getWhereSqlBuilder().toString());
            querySql.vars().set("orderBys", getOrderBySql());
            querySql.vars().set("groupBys", getGroupBySql());
            this.sql = querySql.toString();
        }
        return this.sql;
    }

    public String getGroupBySql() {
        HashMap<Integer, String> filedPhysicalNames = new HashMap<>(getFields().size());
        for (TableFields field : getFields()) {
            String filedPhysicalName = field.getTableName().concat(".").concat(field.getFieldName());
            filedPhysicalNames.put(field.getId(), filedPhysicalName);
        }
        List<String> strings = new ArrayList<>();
        for (DataQueryDTO dto : dataQueryDTOS) {
            if (dto.getFieldGroup() == 1) {
                strings.add(filedPhysicalNames.get(dto.getId()));
            }
        }
        if (strings.size() == 0) {
            return "";
        }
        return "GROUP BY ".concat(Strings.join(",", strings));
    }


    /**
     * 取得排序
     *
     * @return
     */
    private String getOrderBySql() {
        HashMap<Integer, String> filedPhysicalNames = new HashMap<>(getFields().size());
        for (TableFields field : getFields()) {
            String filedPhysicalName = field.getTableName().concat(".").concat(field.getFieldName());
            filedPhysicalNames.put(field.getId(), filedPhysicalName);
        }
        List<String> strings = new ArrayList<>();
        for (DataQueryDTO dto : dataQueryDTOS) {
            if (dto.getFieldDesc() == 1) {
                strings.add(filedPhysicalNames.get(dto.getId()).concat(" asc"));
            } else if (dto.getFieldDesc() == 2) {
                strings.add(filedPhysicalNames.get(dto.getId()).concat(" desc"));
            }
        }
        if (strings.size() == 0) {
            return getFirstTableName().concat(".").concat("create_by_date desc");
        }
        return Strings.join(",", strings);
    }

    /**
     * 取得需要显示的字段
     *
     * @return
     */
    public List<String> getShowFields() {
        List<String> showFields = new ArrayList<>();
        Set<String> tableIds = new HashSet<>();
        for (TableFields field : getFields()) {
            String filedPhysicalName;
            if (field.isLogic()) {
                filedPhysicalName = field.getLogicSqlExpression();
            } else {
                filedPhysicalName = field.getTableName().concat(".").concat(field.getFieldName());
            }
            showFields.add(getGroupShowFields(field, filedPhysicalName).concat(" as ").concat(field.getFieldName()));
            tableIds.add(field.getTableName() + ".id as " + RID + field.getTableId());
        }
        showFields.addAll(tableIds);
        return showFields;
    }

    public String getGroupShowFields(TableFields field, String filedPhysicalName) {
        StringBuilder sb = new StringBuilder();
        sw:
        for (DataQueryDTO dto : dataQueryDTOS) {
            if (dto.getId() == field.getId() && dto.getFieldGroup() > 0) {
                switch (dto.getFieldGroup()) {
                    case 1:
                        break;
                    case 2:
                        sb.append("SUM(");
                        sb.append(filedPhysicalName);
                        sb.append(")");
                        break;
                    case 3:
                        sb.append("AVG(");
                        sb.append(filedPhysicalName);
                        sb.append(")");
                        break;
                    case 4:
                        sb.append("MIN(");
                        sb.append(filedPhysicalName);
                        sb.append(")");
                        break;
                    case 5:
                        sb.append("MAX(");
                        sb.append(filedPhysicalName);
                        sb.append(")");
                        break;
                    case 6:
                        sb.append("COUNT(");
                        sb.append(filedPhysicalName);
                        sb.append(")");
                        break;
                    default:
                        break;
                }
                break sw;
            }
        }
        return sb.toString().length() > 0 ? sb.toString() : filedPhysicalName;
    }

    /**
     * 取得条件
     *
     * @return
     */
    public StringBuilder getWhereSqlBuilder() {
        HashMap<Integer, String> filedPhysicalNames = new HashMap<>(getFields().size());
        HashSet<String> tableNames = new HashSet<>();
        StringBuilder whereSqlBuilder = new StringBuilder();
        for (TableFields field : getFields()) {
            if (field.isLogic()) {
                filedPhysicalNames.put(field.getId(), field.getLogicSqlExpression());
            } else {
                filedPhysicalNames.put(field.getId(), field.getTableName().concat(".").concat(field.getFieldName()));
            }
            tableNames.add(field.getTableName());
        }
        if (tableType != TableType.SingleTable) {
            whereSqlBuilder.append(" and " + getFirstTableName().concat(".userName <> '" + Cons.ADMIN + "'"));
            whereSqlBuilder.append(" and FIND_IN_SET(" + getFirstTableName().concat(".userName,@sessionManagerUserNames)"));
        }

        for (DataQueryDTO dto : dataQueryDTOS) {
            String cndText = dto.getCndText().replace("{" + dto.getText() + "}", filedPhysicalNames.get(dto.getId()));
            cndText = cndText.trim();
            if (!Strings.isEmpty(cndText)) {
                if (dto.getFieldLinkType() == 0) {
                    //and
                    whereSqlBuilder.append(" and " + cndText);
                } else {
                    //OR
                    whereSqlBuilder.append(" or " + cndText);
                }
            }
        }
        return whereSqlBuilder;
    }

    /**
     * 取得主表
     *
     * @return
     */
    private String getFirstTableName() {
        for (TableFields field : getFields()) {
            if (field.getTableId() == selectTable) {
                return field.getTableName();
            }
        }
        throw new RuntimeException("主表不能为空");
    }

    /**
     * 取得查询表单
     *
     * @return
     */
    public StringBuilder getFromTables() {
        String firstTableName = "";
        Set<String> tableNames = new HashSet<>();
        for (TableFields field : getFields()) {
            if (field.getTableId() == selectTable) {
                firstTableName = field.getTableName();
            } else {
                tableNames.add(field.getTableName());
            }
        }
        StringBuilder fromTablesBuilder = new StringBuilder();
        fromTablesBuilder.append(firstTableName);
        for (String tableName : tableNames) {
            fromTablesBuilder.append(" LEFT JOIN ");
            fromTablesBuilder.append(tableName);
            fromTablesBuilder.append(" ON ");
            fromTablesBuilder.append(firstTableName);
            fromTablesBuilder.append(".userid=");
            fromTablesBuilder.append(tableName);
            fromTablesBuilder.append(".userid ");
        }
        return fromTablesBuilder;
    }


    @Override
    public TableColsVO[] getCols() {
        HashMap<Integer, TableColsVO> hashMap = new HashMap<>(getFields().size());
        getFields().forEach(field -> hashMap.put(field.getId(), new TableColsVO(field.getFieldName(), field.getName(), 100, 0, field.getFieldType(), field.getControlType())));
        List<TableColsVO> list = new ArrayList<>();
        list.add(new TableColsVO("numbers", "true"));
        /* list.add(new TableColsVO("checkbox", "true"));*/
        dataQueryDTOS.forEach(dataQueryDTO -> {
            TableColsVO vo = hashMap.get(dataQueryDTO.getId());
            if (dataQueryDTO.getFieldFixed() == 1) {
                vo.setFixed("left");
            } else if (dataQueryDTO.getFieldFixed() == 2) {
                vo.setFixed("right");
            }
            list.add(vo);
        });
        return list.toArray(new TableColsVO[0]);
    }

    @Override
    public List getData(int pageNum, int pageSize) {
        Sql querySql = Sqls.create(getSql());
        querySql.setCallback(Sqls.callback.records());
        querySql.setPager(new Pager(pageNum, pageSize));
        querySql.setParam("sessionManagerUserNames", Strings.join(",",sessionManagerUserNames));
        tableService.dao().execute(querySql);
        List<Record> listData = querySql.getList(Record.class);
        List<Record> recordList = dataMaintainBiz.coverData(listData, fields);
        return recordList;
    }

    @Override
    public int getCount() {
        long count = Daos.queryCount(tableService.dao(), Sqls.create(getSql()).setParam("sessionManagerUserNames", Strings.join(",",sessionManagerUserNames)));
        return (int) count;
    }

    @Override
    public String getErrorMsg() {
        return null;
    }

    @Override
    public File exportDataToExcle(int type, int pageNum, int pageSize) throws IOException {
        File file = File.createTempFile("数据查询导出", ".xlsx");
        PoiExcelUtil util = new PoiExcelUtil(new SXSSFWorkbook(PoiExcelUtil.rowAccessWindowSize), file);
        String sheetName = "结果导出";
        util.createSheet(sheetName);
        List<String[]> datalist = new ArrayList<>();
        List<TableColsVO> colslist = Arrays.asList(getCols());
        int headerLength = colslist.size();
        String[] header = new String[headerLength];
        String[] fields = new String[headerLength];
        int col = 0;
        for (int i = 0; i < colslist.size(); i++) {
            TableColsVO tableCols = colslist.get(i);
            if (tableCols.getField() != null && tableCols.getField().indexOf(RID) == -1) {
                header[col] = tableCols.getTitle();
                fields[col] = tableCols.getField();
                col++;
            }
        }
        //插入标题
        datalist.add(header);
        util.insetStringRowDataList(sheetName, 0, datalist);
        if (type == 0) {
            //插入当前页数据
            datalist.clear();
            datalist = getDataList(pageNum, pageSize, headerLength, fields);
            util.insetStringRowDataList(sheetName, 1, datalist);
            datalist.clear();
        } else {
            //总记录数
            int total = getCount();
            int maxPageSize = 5000;
            Pager pager = new Pager(pageNum, maxPageSize);
            pager.setRecordCount(total);
            int pageCount = pager.getPageCount();
            int rowStart = 1;
            for (int i = 1; i <= pageCount; i++) {
                datalist.clear();
                datalist = getDataList(i, maxPageSize, headerLength, fields);
                util.insetStringRowDataList(sheetName, rowStart, datalist);
                datalist.clear();
                rowStart = rowStart + maxPageSize;
            }
        }
        util.saveWirte();
        return file;
    }

    private List<String[]> getDataList(int pageNum, int pageSize, int headerLength, String[] fields) {
        List<String[]> datalist = new ArrayList<>();
        List<Record> nutMaps = getData(pageNum, pageSize);
        for (Record map : nutMaps) {
            String[] lineData = new String[headerLength];
            for (int i = 0; i < lineData.length; i++) {
                lineData[i] = map.getString(fields[i]);
            }
            datalist.add(lineData);
        }
        return datalist;
    }
}
