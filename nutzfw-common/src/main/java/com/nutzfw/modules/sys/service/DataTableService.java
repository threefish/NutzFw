package com.nutzfw.modules.sys.service;


import com.nutzfw.core.common.service.BaseService;
import com.nutzfw.modules.sys.entity.DataTable;
import org.nutz.dao.Cnd;
import org.nutz.dao.sql.Sql;

import java.util.List;
import java.util.Set;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年06月05日 20时33分50秒
 */
public interface DataTableService extends BaseService<DataTable> {


    /**
     * 表前缀
     */
    String TABLE_PREFIX = "table_";
    /**
     * 字段前缀
     */
    String FIELD_PREFIX = "f_";

    /**
     * 执行sql
     * @param sqls
     * @return
     */
    void executeSql(Sql sqls);

    boolean existTable(String tableName);

    /**
     * 删除动态表
     *
     * @param tableName
     * @return
     */
    void dropTable(String tableName);

    /**
     * 取得表及所有字段
     *
     * @param tableId
     * @return
     */
    DataTable fetchAllFields(int tableId);

    /**
     * 取得表及
     * 可读权限字段(包含读写字段)
     *
     * @param tableId
     * @return
     */
    DataTable fetchAuthReadFields(int tableId, Set<String> roleIds);

    /**
     * 取得表及
     * 可读写权限字段
     *
     * @param tableId
     * @return
     */
    DataTable fetchAuthReadWriteFields(int tableId, Set<String> roleIds);

    /**
     * 取得表及过滤掉删除的字段
     *
     * @param tableId
     * @return
     */
    DataTable fetchAllNotDelectFields(int tableId);

    /**
     * 根据物理表名取得表及字段全部信息
     *
     * @param tableName
     * @return
     */
    DataTable fetchByTableName(String tableName);

    /**
     * 根据用户表名取得表及字段全部信息
     *
     * @param tableName
     * @return
     */
    DataTable fetchByChineseName(String tableName);


    boolean checkDuplicateByName(String name, int id);

    /**
     * 系统表只会在当前结构上增加字段
     * 用户表则是新建表及结构
     *
     * @param dataTable
     */
    void revertTableStructure(DataTable dataTable);


    /**
     * 删除指定表指定字段
     *
     * @param columns
     */
    void deleteField(String tableName, Set<String> columns);

    /**
     * 取得表已经存在的字段
     *
     * @param tableName
     * @return
     */
    Set<String> queryTableColumns(String tableName);

    /**
     * 批量取得表及字段-无权限控制
     *
     * @param aNew
     * @return
     */
    List<DataTable> queryAllFileds(Cnd aNew);
}
