/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.tabledata.biz.impl;

import com.github.threefish.nutz.sqltpl.SqlsTplHolder;
import com.github.threefish.nutz.sqltpl.SqlsXml;
import com.nutzfw.modules.sys.entity.DataTable;
import com.nutzfw.modules.sys.entity.TableFields;
import com.nutzfw.modules.sys.service.DataTableService;
import com.nutzfw.modules.sys.service.TableFieldsService;
import com.nutzfw.modules.tabledata.biz.DataTableBiz;
import com.nutzfw.modules.tabledata.entity.DataTableVersionHistory;
import com.nutzfw.modules.tabledata.service.DataTableVersionHistoryService;
import org.nutz.dao.Sqls;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.trans.Trans;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/7
 * 描述此类：
 */
@IocBean(create = "init")
@SqlsXml
public class DataTableBizImpl implements DataTableBiz {

    final Log log = Logs.get();

    SqlsTplHolder sqlsTplHolder;

    @Inject
    DataTableService               tableService;
    @Inject
    TableFieldsService             fieldsService;
    @Inject
    DataTableVersionHistoryService versionHistoryService;

    public void init() {

    }


    /**
     * 同步数据库
     *
     * @param id
     * @return
     */
    @Override
    public String synchronize(int id, int synchronizeType) {
        String error = null;
        //取得全部字段
        DataTable dataTable = tableService.fetchAllFields(id);
        if (dataTable.isSystem() && synchronizeType == 1) {
            return "系统表不允许使用删除后新建模式进行同步！";
        }
        boolean isAddTable = synchronizeType == 1 && !dataTable.isSystem();
        if (!dataTable.isSystem()) {
            dataTable.setTableName(DataTableService.TABLE_PREFIX + id);
        }
        if (isAddTable) {
            //删除表
            tableService.dropTable(dataTable.getTableName());
        }
        Set<String> fieldNames = new HashSet<>();
        //需要在同步后删除的字段记录
        List<TableFields> delTableFields = new ArrayList<>();
        dataTable.getFields().stream().filter(fields -> !fields.isSystem() && fields.isDelectStatus() && fields.getFieldName() != null).forEach(fields -> {
            //需要删除的字段
            fieldNames.add(fields.getFieldName());
            delTableFields.add(fields);
        });
        //删除字段
        tableService.deleteField(dataTable.getTableName(), fieldNames);
        //删除字段记录信息
        fieldsService.delete(delTableFields);
        //过滤掉已经删除的字段
        dataTable = tableService.fetchAllNotDelectFields(id);
        if (!dataTable.isSystem()) {
            dataTable.setTableName(DataTableService.TABLE_PREFIX + id);
        }
        DataTable finalDataTable = dataTable;
        dataTable.getFields().forEach(fields1 -> {
            fields1.setFieldName(DataTableService.FIELD_PREFIX + fields1.getId());
            fields1.setTableName(finalDataTable.getTableName());
        });
        if (!tableService.existTable(dataTable.getTableName())) {
            //新建表
            error = createTable(dataTable);
        } else {
            //修改表
            error = updateTable(dataTable);
        }
        if (error == null) {
            //记录当前版本
            versionHistoryService.insert(DataTableVersionHistory.builder()
                    .tableId(dataTable.getId())
                    .tableVersion(dataTable.getVersion())
                    .json(Json.toJson(dataTable, JsonFormat.compact())).build());
        }
        return error;
    }

    @Override
    public void save(DataTable newTable, DataTable oldTable, TableFields[] fields) {
        List<TableFields> allFields = new ArrayList<>();
        if (newTable.getId() == 0) {
            allFields.addAll(insertTableAndFields(newTable, fields));
        } else {
            oldTable.setStatus(0);
            oldTable.setName(newTable.getName());
            oldTable.setComment(newTable.getComment());
            oldTable.setTableType(newTable.getTableType());
            oldTable.setFormTemplate(newTable.getFormTemplate());
            if (!oldTable.isSystem()) {
                oldTable.setTableName(DataTableService.TABLE_PREFIX + oldTable.getId());
            }
            //编辑
            tableService.update(oldTable);
            //新增的字段
            List<TableFields> insetFields = new ArrayList<>();
            //更新字段
            List<TableFields> updateFields = new ArrayList<>();
            for (TableFields tableFields : fields) {
                tableFields.setComment(Strings.isBlank(tableFields.getComment()) ? tableFields.getName() : tableFields.getComment());
                tableFields.setFromLable(Strings.isBlank(tableFields.getFromLable()) ? tableFields.getName() : tableFields.getFromLable());
                tableFields.setLogicSqlExpression("");
                tableFields.setTableName(oldTable.getTableName());
                if (tableFields.getId() == 0) {
                    tableFields.setTableId(oldTable.getId());
                    insetFields.add(tableFields);
                } else {
                    if (!tableFields.isSystem()) {
                        tableFields.setFieldName(DataTableService.FIELD_PREFIX + tableFields.getId());
                    }
                    updateFields.add(tableFields);
                }
            }
            //被删除的字段
            List<TableFields> delFields = new ArrayList<>();
            //上传的数据字段
            List<TableFields> postFields = Arrays.asList(fields);
            oldTable.getFields().stream().filter(field -> !field.isSystem()).forEach(field -> {
                //上传的数据中字段还在否？
                boolean status = postFields.stream().anyMatch(postField -> field.getId() == postField.getId());
                if (!status) {
                    //不在了 设置为待删除
                    field.setDelectStatus(true);
                    delFields.add(field);
                }
            });
            updateFields.addAll(delFields);
            fieldsService.insert(insetFields);
            insetFields.forEach(fields1 -> {
                fields1.setFieldName(DataTableService.FIELD_PREFIX + fields1.getId());
                updateFields.add(fields1);
            });
            fieldsService.updateIgnoreNull(updateFields);
            allFields.addAll(updateFields);
        }

        HashMap<String, String> notLogicFieldNames = new HashMap<>();
        allFields.stream().filter(fields1 -> !fields1.isLogic()).forEach(fields1 ->
                notLogicFieldNames.put(fields1.getName(), fields1.getTableName() + "." + fields1.getFieldName())
        );
        //缓存逻辑字段
        HashMap<Integer, TableFields> logicFields = new HashMap<>();
        allFields.stream().filter(TableFields::isLogic).forEach(fields1 ->
                logicFields.put(fields1.getId(), fields1)
        );
        //最多循环 MAX 次，防止程序死循环
        int max = 2000;
        do {
            logicFields.forEach((integer, fields1) -> {
                String logicSqlExpression = calculationLogicSqlExpression(fields1, notLogicFieldNames);
                if (!Strings.isEmpty(logicSqlExpression)) {
                    notLogicFieldNames.put(fields1.getName(), logicSqlExpression);
                    fields1.setLogicSqlExpression(logicSqlExpression);
                    logicFields.put(fields1.getId(), fields1);
                }
            });
            max--;
        }
        while (allFields.stream().filter(TableFields::isLogic).anyMatch(fields1 -> Strings.isEmpty(fields1.getLogicSqlExpression())) && max > 0);
        if (max <= 0) {
            throw new RuntimeException("逻辑表达式有误，循环引用或无效引用导致了死循环！请检查！");
        }
        List<TableFields> updateList = new ArrayList<>();
        logicFields.forEach((integer, fields1) -> updateList.add(fields1));
        fieldsService.updateIgnoreNull(updateList);
        HashMap<String, Integer> dictDependMap = new HashMap<>();
        for (TableFields tableFields : allFields) {
            dictDependMap.put(tableFields.getName(), tableFields.getId());
        }
        List<TableFields> dictDependFieldFields = new ArrayList<>();
        //是依赖字段
        allFields.stream().filter(tableFields -> tableFields.getDictDepend() > 0).forEach(tableFields -> {
            String dictDependFieldDesc = tableFields.getDictDependFieldDesc();
            Integer integer = dictDependMap.get(dictDependFieldDesc);
            if (integer == 0 || integer == null) {
                throw new RuntimeException(MessageFormat.format("依赖字段 {0} 不存在！", dictDependFieldDesc));
            }
            tableFields.setDictDependFieldId(dictDependMap.get(dictDependFieldDesc));
            dictDependFieldFields.add(tableFields);
        });
        fieldsService.updateIgnoreNull(dictDependFieldFields);
    }

    /**
     * 计算逻辑sql表达式
     *
     * @param field
     * @return
     */
    private String calculationLogicSqlExpression(TableFields field, HashMap<String, String> notLogicFieldNames) {
        String logicSqlExpression = field.getLogicElExpression();
        final String regStart = "{";
        final String regEnd = "}";
        for (HashMap.Entry entry : notLogicFieldNames.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            logicSqlExpression = logicSqlExpression.replace(regStart + key + regEnd, value);
        }
        if (logicSqlExpression.indexOf(regStart) > -1 || logicSqlExpression.indexOf(regEnd) > -1) {
            logicSqlExpression = "";
        } else {
            logicSqlExpression = "(" + logicSqlExpression + ")";
        }
        return logicSqlExpression;
    }

    /**
     * 新增表及字段
     *
     * @param newTable
     * @param fields
     */
    private List<TableFields> insertTableAndFields(DataTable newTable, TableFields[] fields) {
        //新增
        newTable.setStatus(0);
        newTable.setVersion(0);
        tableService.insert(newTable);
        if (!newTable.isSystem()) {
            newTable.setTableName(DataTableService.TABLE_PREFIX + newTable.getId());
        }
        tableService.update(newTable);
        List<TableFields> insertFields = new ArrayList<>();
        for (TableFields tableFields : fields) {
            tableFields.setTableId(newTable.getId());
            tableFields.setTableName(newTable.getTableName());
            tableFields.setComment(Strings.isBlank(tableFields.getComment()) ? tableFields.getName() : tableFields.getComment());
            tableFields.setFromLable(Strings.isBlank(tableFields.getFromLable()) ? tableFields.getName() : tableFields.getFromLable());
            insertFields.add(tableFields);
        }
        fieldsService.insert(insertFields);
        List<TableFields> updateTableFields = new ArrayList<>();
        insertFields.forEach(fields1 -> {
            fields1.setFieldName(DataTableService.FIELD_PREFIX + fields1.getId());
            updateTableFields.add(fields1);
        });
        fieldsService.updateIgnoreNull(updateTableFields);
        return insertFields;
    }

    /**
     * 更新表信息
     *
     * @param dataTable 新版本
     * @return
     */
    private String updateTable(DataTable dataTable) {
        //非系统字段
        List<TableFields> notSystemFields = dataTable.getFields().stream().filter(f -> !f.isSystem()).collect(Collectors.toList());
        List<TableFields> addlist = new ArrayList<>();
        List<TableFields> modifylist = new ArrayList<>();
        //此表已经存在的字段
        Set<String> columnNames = tableService.queryTableColumns(dataTable.getTableName());
        notSystemFields.forEach(fields -> {
            fields.setTableName(dataTable.getTableName());
            String columnName = fields.getFieldName();
            fields.setFieldName(columnName);
            if (columnNames.contains(columnName)) {
                //字段已经存在
                modifylist.add(fields);
            } else {
                addlist.add(fields);
            }
        });
        HashMap data = new HashMap(3);
        data.put("t", dataTable);
        data.put("addlist", addlist);
        data.put("modifylist", modifylist);
        List<TableFields> allFields = new ArrayList<>();
        allFields.addAll(addlist);
        allFields.addAll(modifylist);
        data.put("list", allFields);
        try {
            String alertTableSql = sqlsTplHolder.renderSql("alertTable", data);
            log.debug(alertTableSql);
            tableService.executeSql(Sqls.create(alertTableSql));
            dataTable.setStatus(1);
            Trans.exec(() -> {
                dataTable.setVersion(dataTable.getVersion() + 1);
                tableService.updateIgnoreNull(dataTable);
                fieldsService.updateIgnoreNull(addlist);
            });
        } catch (Exception e) {
            log.error(e);
            return "表更新失败：" + e.getLocalizedMessage();
        }
        return null;
    }

    /**
     * 创建表
     *
     * @param dataTable
     * @return
     */
    private String createTable(DataTable dataTable) {
        HashMap data = new HashMap(2);
        List<TableFields> fields = dataTable.getFields();
        data.put("t", dataTable);
        data.put("list", fields);
        try {
            String createTableSql = sqlsTplHolder.renderSql("createTable", data);
            log.debug(createTableSql);
            tableService.executeSql(Sqls.create(createTableSql));
            dataTable.setStatus(1);
            Trans.exec(() -> {
                dataTable.setVersion(dataTable.getVersion() + 1);
                tableService.updateIgnoreNull(dataTable);
            });
        } catch (Exception e) {
            log.error(e);
            if (!dataTable.isSystem()) {
                //表可能已经创建成功了，尝试删除
                tableService.dropTable(dataTable.getTableName());
                return "表创建失败：" + e.getLocalizedMessage();
            }
        }
        return null;
    }
}
