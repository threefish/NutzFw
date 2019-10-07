/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.sys.entity.DataTable;
import com.nutzfw.modules.sys.entity.TableFields;
import com.nutzfw.modules.sys.enums.FieldAuth;
import com.nutzfw.modules.sys.service.DataTableService;
import com.nutzfw.modules.sys.service.TableFieldsService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年06月05日 20时33分50秒
 */
@IocBean(name = "dataTableService", args = {"refer:dao", "refer:noWallDao"})
public class DataTableServiceImpl extends BaseServiceImpl<DataTable> implements DataTableService {
    @Inject
    TableFieldsService fieldsService;
    @Inject
    TableFieldsService tableFieldsService;
    private Log log = Logs.get();
    private Dao noWallDao;

    public DataTableServiceImpl(Dao dao, Dao noWallDao) {
        super(dao);
        this.noWallDao = noWallDao;
    }

    @Override
    public void executeSql(Sql sqls) {
        noWallDao.execute(sqls);
    }

    @Override
    public boolean existTable(String tableName) {
        return noWallDao.exists(tableName);
    }

    @Override
    public void dropTable(String tableName) {
        if (noWallDao.exists(tableName)) {
            Sql sqls = Sqls.create("DROP TABLE $tableName");
            sqls.vars().set("tableName", Sqls.escapeSqlFieldValue(tableName));
            noWallDao.execute(sqls);
        }
    }

    /**
     * 取得表及所有字段
     *
     * @param tableId
     * @return
     */
    @Override
    public DataTable fetchAllFields(int tableId) {
        DataTable dataTable = dao.fetch(getEntityClass(), tableId);
        dao.fetchLinks(dataTable, null, Cnd.orderBy().asc("shortNo"));
        return dataTable;
    }

    /**
     * 取得表及所有可读字段
     *
     * @param tableId
     * @return
     */
    @Override
    public DataTable fetchAuthReadFields(int tableId, Set<String> roleIds) {
        DataTable dataTable = fetch(tableId);
        List<TableFields> fields = tableFieldsService.fetchAuthFields("fetchAuthReadFields", NutMap.NEW().setv("tableId", tableId).setv("roleIds", Strings.join(",", roleIds)));
        HashMap<String, TableFields> sets = new HashMap<>(0);
        fields.stream().forEach(tableFields -> {
            String key = tableFields.getAuth().getValue() + tableFields.getId();
            String rwKey = FieldAuth.rw.getValue() + tableFields.getId();
            String rKey = FieldAuth.r.getValue() + tableFields.getId();
            boolean hasRw = sets.containsKey(rwKey);
            boolean hasR = sets.containsKey(rKey);
            //没有读写
            if (!hasRw) {
                //有读权限 并且当前权限是 读写的
                if (hasR && tableFields.getAuth() == FieldAuth.rw) {
                    //移除读权限 放入读写权限
                    sets.remove(rKey);
                    sets.put(rwKey, tableFields);
                } else if (!hasR) {
                    sets.put(key, tableFields);
                }
            }
        });
        List<TableFields> fieldsList = new ArrayList<>();
        sets.forEach((s, tableFields) -> fieldsList.add(tableFields));
        fieldsList.sort(Comparator.comparingInt(obj -> obj.getShortNo()));
        dataTable.setFields(fieldsList);
        return dataTable;
    }

    /**
     * 取得表及所有可写字段
     *
     * @param tableId
     * @return
     */
    @Override
    public DataTable fetchAuthReadWriteFields(int tableId, Set<String> roleIds) {
        DataTable dataTable = fetch(tableId);
        dataTable.setFields(tableFieldsService.fetchAuthFields("fetchAuthReadWriteFields", NutMap.NEW().setv("tableId", tableId).setv("roleIds", Strings.join(",", roleIds))));
        return dataTable;
    }

    /**
     * 取得表及过滤掉删除的字段
     *
     * @param tableId
     * @return
     */
    @Override
    public DataTable fetchAllNotDelectFields(int tableId) {
        DataTable dataTable = dao.fetch(getEntityClass(), tableId);
        dao.fetchLinks(dataTable, null, Cnd.where("delectStatus", "=", false).asc("shortNo"));
        return dataTable;
    }

    /**
     * 取得表及所有字段
     *
     * @param tableName
     * @return
     */
    @Override
    public DataTable fetchByTableName(String tableName) {
        DataTable dataTable = dao.fetch(getEntityClass(), Cnd.where("tableName", "=", tableName));
        dao.fetchLinks(dataTable, null, Cnd.orderBy().asc("shortNo"));
        return dataTable;
    }

    /**
     * 根据表中文名取得表及所有字段
     *
     * @param tableName
     * @return
     */
    @Override
    public DataTable fetchByChineseName(String tableName) {
        DataTable dataTable = dao.fetch(getEntityClass(), Cnd.where("name", "=", tableName));
        dao.fetchLinks(dataTable, null, Cnd.orderBy().asc("shortNo"));
        return dataTable;
    }

    /**
     * 根据表中文名查重
     *
     * @param name
     * @return
     */
    @Override
    public boolean checkDuplicateByName(String name, int id) {
        return dao.count(getEntityClass(), Cnd.where("name", "=", name).and("id", "!=", id)) > 0;
    }

    @Override
    public void revertTableStructure(DataTable dataTable) {
        List<TableFields> tableFields = new ArrayList<>();
        DataTable old = fetchByChineseName(dataTable.getName());
        if (!dataTable.isSystem()) {
            //是用户表，不是系统表
            if (old == null) {
                old = new DataTable();
                old.setStatus(0);
                old.setSystem(false);
                old.setName(dataTable.getName());
                old.setComment(dataTable.getComment());
                old.setTableType(dataTable.getTableType());
                old.setFormTemplate(dataTable.getFormTemplate());
                old.setFields(new ArrayList<>());
                dao.insert(old);
            }
            //重新赋值表名
            old.setTableName(DataTableService.TABLE_PREFIX + old.getId());
        }
        DataTable oldTable = old;
        //字段名重复的忽略,系统字段忽略
        dataTable.getFields().stream().filter(newFiled -> !oldTable.getFields().stream().anyMatch(oldField -> oldField.getName().equals(newFiled.getName())) && !newFiled.isSystem()).forEach(fields -> {
            //需要新增的字段
            fields.setId(0);
            fields.setTableId(oldTable.getId());
            fields.setTableName(oldTable.getTableName());
            fields.setFieldName(null);
            tableFields.add(fields);
        });
        oldTable.setStatus(0);
        dao.update(oldTable);
        fieldsService.insert(tableFields);
        //取得全部字段
        tableFields.stream().filter(fields -> !fields.isSystem()).forEach(field -> field.setFieldName(DataTableService.FIELD_PREFIX + field.getId()));
        fieldsService.update(tableFields);
    }

    @Override
    public void deleteField(String tableName, Set<String> columns) {
        if (columns.size() > 0 && noWallDao.exists(tableName)) {
            Set<String> columnNames = queryTableColumns(tableName);
            List<Sql> sqls = new ArrayList<>();
            //循环要删除的字段
            for (String colName : columns) {
                if (!columnNames.contains(colName)) {
                    //数据库中不存在这个字段就跳出
                    continue;
                }
                Sql sql = Sqls.create("ALTER table $table DROP column $name");
                sql.vars().set("table", tableName);
                sql.vars().set("name", colName);
                sqls.add(sql);
            }
            sqls.forEach(sql -> noWallDao.execute(sql));
        }
    }

    @Override
    public Set<String> queryTableColumns(String tableName) {
        Set<String> columnNames = new HashSet<>();
        if (noWallDao.exists(tableName)) {
            noWallDao.run(conn -> {
                Statement stat = null;
                ResultSet rs = null;
                ResultSetMetaData meta;
                try {
                    // 获取数据库元信息
                    stat = conn.createStatement();
                    rs = stat.executeQuery("select * from ".concat(tableName).concat(" where 1 != 1"));
                    meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        columnNames.add(meta.getColumnName(i).toLowerCase());
                    }
                } catch (SQLException e) {
                    log.debug(e);
                } finally {
                    Daos.safeClose(stat, rs);
                }
            });
        }
        return columnNames;
    }

    @Override
    public List<DataTable> queryAllFileds(Cnd cnd) {
        List<DataTable> tables = query(cnd);
        tables.stream().forEach(dataTable ->
                dao.fetchLinks(dataTable, null, Cnd.orderBy().asc("shortNo"))
        );
        return tables;
    }

}
