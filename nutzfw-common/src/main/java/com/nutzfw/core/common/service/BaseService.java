package com.nutzfw.core.common.service;

import com.github.threefish.nutz.dto.PageDataDTO;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import org.nutz.dao.*;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2017/12/6  15:38
 * 描述此类：
 */
public interface BaseService<T> {
    Dao dao();

    int count(Condition cnd);

    int count();

    int count(String tableName, Condition cnd);

    int count(String tableName);

    T fetch(long id);

    T fetch(String id);

    T fetchByUUID(String id);

    <T> T fetchLinks(T obj, String regex);

    <T> T fetchLinks(T obj, String regex, Condition cnd);

    /**
     * 根据查询条件获取一个对象.<b>注意: 条件语句需要加上表名!!!</b>
     * <p/>
     * 这个方法是让@One关联的属性,通过left join一次性取出. 与fetch+fetchLinks是等价的
     *
     * @param cnd 查询条件,必须带表名!!!
     * @return 实体对象, 符合regex的关联属性也会取出
     */
    <T> T fetchByJoin(Condition cnd);

    /**
     * 根据查询条件获取一个对象.<b>注意: 条件语句需要加上表名!!!</b>
     * <p/>
     * 这个方法是让@One关联的属性,通过left join一次性取出. 与fetch+fetchLinks是等价的
     *
     * @param regex 需要过滤的关联属性,可以是null,取出全部关联属性.
     * @param cnd   查询条件,必须带表名!!!
     * @return 实体对象, 符合regex的关联属性也会取出
     */
    <T> T fetchByJoin(String regex, Condition cnd);

    /**
     * 根据对象 ID 获取一个对象。它只会获取这个对象，关联对象不会被获取。
     * <p>
     * 你的对象必须在某个字段声明了注解 '@Id'，否则本操作会抛出一个运行时异常
     * <p/>
     * 这个方法是让@One关联的属性,通过left join一次性取出. 与fetch+fetchLinks是等价的
     *
     * @param regex 需要取出的关联属性,是正则表达式哦,匹配的是Java属性名
     * @param id    对象id
     * @return 实体
     */
    <T> T fetchByJoin(String regex, long id);

    /**
     * 根据对象 NAME 获取一个对象。它只会获取这个对象，关联对象不会被获取。
     * <p>
     * 你的对象必须在某个字段声明了注解 '@Name'，否则本操作会抛出一个运行时异常
     * <p/>
     * 这个方法是让@One关联的属性,通过left join一次性取出. 与fetch+fetchLinks是等价的
     *
     * @param regex 需要取出的关联属性,是正则表达式哦,匹配的是Java属性名
     * @param name  对象name
     * @return 实体
     */
    <T> T fetchByJoin(String regex, String name);

    T fetch(Condition cnd);

    T fetchx(Object... pks);

    boolean exists(Object... pks);

    <T> T insert(T obj);

    <T> T insert(T obj, FieldFilter filter);

    <T> T insertOrUpdate(T obj);

    <T> T insertOrUpdate(T obj, FieldFilter insertFieldFilter, FieldFilter updateFieldFilter);

    void insert(String tableName, Chain chain);

    <T> T fastInsert(T obj);

    <T> T insertWith(T obj, String regex);

    <T> T insertLinks(T obj, String regex);

    <T> T insertRelation(T obj, String regex);

    int update(Object obj);

    int updateIgnoreNull(Object obj);

    int update(Chain chain, Condition cnd);

    int update(String tableName, Chain chain, Condition cnd);

    <T> T updateWith(T obj, String regex);

    <T> T updateLinks(T obj, String regex);

    int updateRelation(Class<?> classOfT, String regex, Chain chain, Condition cnd);

    int updateWithVersion(Object obj);

    int updateWithVersion(Object obj, FieldFilter filter);

    int updateAndIncrIfMatch(Object obj, FieldFilter fieldFilter, String fieldName);

    int getMaxId();

    <T> int delete(T obj);

    int delete(Cnd cnd);

    int delete(long id);

    int delete(int id);

    int delete(String id);

    void delete(Integer[] ids, boolean isUuid);

    int vDelete(long id);

    int vDelete(String id);

    int vDelete(String id, boolean isUuid);

    int vDelete(String[] ids, boolean isUuid);

    int delete(Long[] ids, boolean isUuid);

    int delete(String[] ids, boolean isUuid);

    int clear();

    int clear(String tableName);

    int clear(Condition cnd);

    int clear(String tableName, Condition cnd);

    T getField(String fieldName, long id);

    T getField(String fieldName, int id);

    T getField(String fieldName, String name);

    T getField(String fieldName, Condition cnd);

    List<T> query(String fieldName, Condition cnd);

    List<T> query(Condition cnd);

    List<T> query(Pager pager);

    List<T> queryByJoin(Condition cnd);

    List<T> queryByJoin(Condition cnd, String regex);

    List<T> query();

    List<T> query(Sql sql, int pageNum, int pageSiz);

    List<T> query(Sql sql, Pager pager);

    List<T> query(Condition cnd, String linkName);

    List<T> query(String linkName);

    List<T> query(Condition cnd, String linkName, Pager pager);

    List<T> query(Condition cnd, Pager pager);

    String getSubPath(String tableName, String cloName, String value);

    int count(Sql sql);

    void execute(Sql sql);

    List<Record> list(Sql sql);

    Map getMap(Sql sql);

    /**
     * 对某一个对象字段，进行计算。
     *
     * @param funcName  计算函数名，请确保你的数据库是支持这个函数的
     * @param fieldName 对象 java 字段名
     * @return 计算结果
     */
    int func(String funcName, String fieldName);

    /**
     * 对某一个对象字段，进行计算。
     *
     * @param funcName  计算函数名，请确保你的数据库是支持这个函数的
     * @param fieldName 对象 java 字段名
     * @return 计算结果
     */
    Object func2(String funcName, String fieldName);

    /**
     * 对某一个数据表字段，进行计算。
     *
     * @param tableName 表名
     * @param funcName  计算函数名，请确保你的数据是支持库这个函数的
     * @param colName   数据库字段名
     * @return 计算结果
     */
    int func(String tableName, String funcName, String colName);

    /**
     * 对某一个数据表字段，进行计算。
     *
     * @param tableName 表名
     * @param funcName  计算函数名，请确保你的数据是支持库这个函数的
     * @param colName   数据库字段名
     * @return 计算结果
     */
    Object func2(String tableName, String funcName, String colName);

    /**
     * 对某一个对象字段，进行计算。
     *
     * @param funcName  计算函数名，请确保你的数据库是支持这个函数的
     * @param fieldName 对象 java 字段名
     * @param cnd       过滤条件
     * @return 计算结果
     */
    int func(String funcName, String fieldName, Condition cnd);

    /**
     * 对某一个对象字段，进行计算。
     *
     * @param funcName  计算函数名，请确保你的数据库是支持这个函数的
     * @param fieldName 对象 java 字段名
     * @param cnd       过滤条件
     * @return 计算结果
     */
    Object func2(String funcName, String fieldName, Condition cnd);

    /**
     * 对某一个数据表字段，进行计算。
     *
     * @param tableName 表名
     * @param funcName  计算函数名，请确保你的数据库是支持这个函数的
     * @param colName   数据库字段名
     * @param cnd       过滤条件
     * @return 计算结果
     */
    int func(String tableName, String funcName, String colName, Condition cnd);

    /**
     * 对某一个数据表字段，进行计算。
     *
     * @param tableName 表名
     * @param funcName  计算函数名，请确保你的数据库是支持这个函数的
     * @param colName   数据库字段名
     * @param cnd       过滤条件
     * @return 计算结果
     */
    Object func2(String tableName, String funcName, String colName, Condition cnd);

    LayuiTableDataListVO listPage(Integer pageNumber, Condition cnd);

    LayuiTableDataListVO listPage(Integer pageNumber, Sql sql);

    LayuiTableDataListVO listPage(Integer pageNumber, String tableName, Condition cnd);

    LayuiTableDataListVO listPage(Integer pageNumber, int pageSize, Condition cnd);

    LayuiTableDataListVO listPage(Integer pageNumber, int pageSize, Condition cnd, String fieldName);

    LayuiTableDataListVO listPage(Integer pageNumber, int pageSize, String tableName, Condition cnd);

    LayuiTableDataListVO listPage(Integer pageNumber, int pageSize, Sql sql);


    PageDataDTO listPageDto(Integer pageNumber, Condition cnd);
    PageDataDTO listPageDto(Integer pageNumber, Sql sql);
    PageDataDTO listPageDto(Integer pageNumber, String tableName, Condition cnd);
    PageDataDTO listPageDto(Integer pageNumber, int pageSize, Condition cnd);
    PageDataDTO listPageDto(Integer pageNumber, int pageSize, Condition cnd, String fieldName);
    PageDataDTO listPageDto(Integer pageNumber, int pageSize, String tableName, Condition cnd);
    PageDataDTO listPageDto(Integer pageNumber, int pageSize, Sql sql);


    List<T> query(Sql sql);

    void deleteByUUIDs(String[] delectIds);

    void deleteByIds(Integer[] delectIds);
}
