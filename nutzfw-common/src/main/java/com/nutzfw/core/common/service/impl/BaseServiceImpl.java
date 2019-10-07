/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.service.impl;

import com.github.threefish.nutz.dto.PageDataDTO;
import com.nutzfw.core.common.service.BaseService;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import org.apache.commons.lang3.math.NumberUtils;
import org.nutz.dao.*;
import org.nutz.dao.entity.Record;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.service.EntityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2017/12/6  16:02
 * 描述此类：
 */
public class BaseServiceImpl<T> extends EntityService<T> implements BaseService<T> {
    private static  int    DEFAULT_PAGE_NUMBER = 10;
    protected final String IN                  = "in";
    protected final String EQ                  = "=";
    protected final String NEQ                 = "!=";
    protected final String BIG_THAN            = ">";
    protected final String LESS_THAN           = "<";
    protected       Dao    dao;

    public BaseServiceImpl(Dao dao) {
        super(dao);
        this.dao = dao;
    }

    /**
     * 统计符合条件的对象表条数
     *
     * @param cnd
     * @return
     */
    @Override
    public int count(Condition cnd) {
        return this.dao().count(this.getEntityClass(), cnd);
    }

    /**
     * 统计对象表条数
     *
     * @return
     */
    @Override
    public int count() {
        return this.dao().count(this.getEntityClass());
    }

    /**
     * 统计符合条件的记录条数
     *
     * @param tableName
     * @param cnd
     * @return
     */
    @Override
    public int count(String tableName, Condition cnd) {
        return this.dao().count(tableName, cnd);
    }

    /**
     * 统计表记录条数
     *
     * @param tableName
     * @return
     */
    @Override
    public int count(String tableName) {
        return this.dao().count(tableName);
    }

    /**
     * 通过数字型主键查询对象
     *
     * @param id
     * @return
     */
    @Override
    public T fetch(long id) {
        return this.dao().fetch(this.getEntityClass(), id);
    }

    /**
     * 通过字符型主键查询对象
     *
     * @param id
     * @return
     */
    @Override
    public T fetch(String id) {
        return this.dao().fetch(this.getEntityClass(), id);
    }


    /**
     * 通过字符型主键查询对象
     *
     * @param id
     * @return
     */
    @Override
    public T fetchByUUID(String id) {
        return this.dao().fetch(this.getEntityClass(), Cnd.where("uuid", "=", id));
    }

    /**
     * 查询关联表
     *
     * @param obj   数据对象,可以是普通对象或集合,但不是类
     * @param regex 为null查询全部,支持通配符 ^(a|b)$
     * @return
     */
    @Override
    public <T> T fetchLinks(T obj, String regex) {
        return this.dao().fetchLinks(obj, regex);
    }

    /**
     * 查询关联表
     *
     * @param obj   数据对象,可以是普通对象或集合,但不是类
     * @param regex 为null查询全部,支持通配符 ^(a|b)$
     * @param cnd   关联字段的过滤(排序,条件语句,分页等)
     * @return
     */
    @Override
    public <T> T fetchLinks(T obj, String regex, Condition cnd) {
        return this.dao().fetchLinks(obj, regex, cnd);
    }


    @Override
    public <T> T fetchByJoin(Condition cnd) {
        return fetchByJoin(null, cnd);
    }

    @Override
    public <T> T fetchByJoin(String regex, Condition cnd) {
        return this.dao().fetchByJoin((Class<T>) this.getEntityClass(), regex, cnd);
    }

    @Override
    public <T> T fetchByJoin(String regex, long id) {
        return this.dao().fetchByJoin((Class<T>) this.getEntityClass(), regex, id);
    }

    @Override
    public <T> T fetchByJoin(String regex, String name) {
        return this.dao().fetchByJoin((Class<T>) this.getEntityClass(), regex, name);
    }

    /**
     * 查出符合条件的第一条记录
     *
     * @param cnd 查询条件
     * @return 实体, 如不存在则为null
     */
    @Override
    public T fetch(Condition cnd) {
        return dao().fetch(getEntityClass(), cnd);
    }

    /**
     * 复合主键专用
     *
     * @param pks 键值
     * @return 对象 T
     */
    @Override
    public T fetchx(Object... pks) {
        return dao().fetchx(getEntityClass(), pks);
    }

    /**
     * 复合主键专用
     *
     * @param pks 键值
     * @return 对象 T
     */
    @Override
    public boolean exists(Object... pks) {
        return null != fetchx(pks);
    }

    /**
     * 将一个对象插入到一个数据库
     *
     * @param obj 要被插入的对象
     *            它可以是：
     *            普通 POJO
     *            集合
     *            数组
     *            Map
     *            注意：如果是集合，数组或者 Map，所有的对象必须类型相同，否则可能会出错
     * @return 插入后的对象
     */
    @Override
    public <T> T insert(T obj) {
        return this.dao().insert(obj);
    }

    /**
     * 将一个对象按FieldFilter过滤后,插入到一个数据源。
     * <p/>
     * <code>dao.insert(pet, FieldFilter.create(Pet.class, FieldMatcher.create(false)));</code>
     *
     * @param obj    要被插入的对象
     * @param filter 字段过滤器, 其中FieldMatcher.isIgnoreId生效
     * @return 插入后的对象
     * @see Dao#insert(Object)
     */
    @Override
    public <T> T insert(T obj, FieldFilter filter) {
        return this.dao().insert(obj, filter);
    }

    /**
     * 根据对象的主键(@Id/@Name/@Pk)先查询, 如果存在就更新, 不存在就插入
     *
     * @param obj 对象
     * @return 原对象
     */
    @Override
    public <T> T insertOrUpdate(T obj) {
        return this.dao().insertOrUpdate(obj);
    }

    /**
     * 根据对象的主键(@Id/@Name/@Pk)先查询, 如果存在就更新, 不存在就插入
     *
     * @param obj               对象
     * @param insertFieldFilter 插入时的字段过滤, 可以是null
     * @param updateFieldFilter 更新时的字段过滤,可以是null
     * @return 原对象
     */
    @Override
    public <T> T insertOrUpdate(T obj, FieldFilter insertFieldFilter, FieldFilter updateFieldFilter) {
        return this.dao().insertOrUpdate(obj, insertFieldFilter, updateFieldFilter);
    }

    /**
     * 自由的向一个数据表插入一条数据
     *
     * @param tableName 表名
     * @param chain     数据名值链
     */
    @Override
    public void insert(String tableName, Chain chain) {
        this.dao().insert(tableName, chain);
    }

    /**
     * 快速插入一个对象,对象的 '@Prev' 以及 '@Next' 在这个函数里不起作用
     *
     * @param obj
     * @return
     */
    @Override
    public <T> T fastInsert(T obj) {
        return this.dao().fastInsert(obj);
    }

    /**
     * 将对象插入数据库同时，也将符合一个正则表达式的所有关联字段关联的对象统统插入相应的数据库
     * <p>
     * 关于关联字段更多信息，请参看 '@One' | '@Many' | '@ManyMany' 更多的描述
     *
     * @param obj   数据对象
     * @param regex 正则表达式，描述了什么样的关联字段将被关注。如果为 null，则表示全部的关联字段都会被插入
     * @return 数据对象本身
     * @see org.nutz.dao.entity.annotation.One
     * @see org.nutz.dao.entity.annotation.Many
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    @Override
    public <T> T insertWith(T obj, String regex) {
        return this.dao().insertWith(obj, regex);
    }

    /**
     * 根据一个正则表达式，仅将对象所有的关联字段插入到数据库中，并不包括对象本身
     *
     * @param obj   数据对象
     * @param regex 正则表达式，描述了什么样的关联字段将被关注。如果为 null，则表示全部的关联字段都会被插入
     * @return 数据对象本身
     * @see org.nutz.dao.entity.annotation.One
     * @see org.nutz.dao.entity.annotation.Many
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    @Override
    public <T> T insertLinks(T obj, String regex) {
        return this.dao().insertLinks(obj, regex);
    }

    /**
     * 将对象的一个或者多个，多对多的关联信息，插入数据表
     *
     * @param obj   对象
     * @param regex 正则表达式，描述了那种多对多关联字段将被执行该操作
     * @return 对象自身
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    @Override
    public <T> T insertRelation(T obj, String regex) {
        return this.dao().insertRelation(obj, regex);
    }

    /**
     * 更新数据
     *
     * @param obj
     * @return
     */
    @Override
    public int update(Object obj) {
        return this.dao().update(obj);
    }

    /**
     * 更新数据忽略值为null的字段
     *
     * @param obj
     * @return
     */
    @Override
    public int updateIgnoreNull(Object obj) {
        return this.dao().updateIgnoreNull(obj);
    }

    /**
     * 部分更新实体表
     *
     * @param chain
     * @param cnd
     * @return
     */
    @Override
    public int update(Chain chain, Condition cnd) {
        return this.dao().update(this.getEntityClass(), chain, cnd);
    }

    /**
     * 部分更新表
     *
     * @param tableName
     * @param chain
     * @param cnd
     * @return
     */
    @Override
    public int update(String tableName, Chain chain, Condition cnd) {
        return this.dao().update(tableName, chain, cnd);
    }

    /**
     * 将对象更新的同时，也将符合一个正则表达式的所有关联字段关联的对象统统更新
     * <p>
     * 关于关联字段更多信息，请参看 '@One' | '@Many' | '@ManyMany' 更多的描述
     *
     * @param obj   数据对象
     * @param regex 正则表达式，描述了什么样的关联字段将被关注。如果为 null，则表示全部的关联字段都会被更新
     * @return 数据对象本身
     * @see org.nutz.dao.entity.annotation.One
     * @see org.nutz.dao.entity.annotation.Many
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    @Override
    public <T> T updateWith(T obj, String regex) {
        return this.dao().updateWith(obj, regex);
    }

    /**
     * 根据一个正则表达式，仅更新对象所有的关联字段，并不包括对象本身
     *
     * @param obj   数据对象
     * @param regex 正则表达式，描述了什么样的关联字段将被关注。如果为 null，则表示全部的关联字段都会被更新
     * @return 数据对象本身
     * @see org.nutz.dao.entity.annotation.One
     * @see org.nutz.dao.entity.annotation.Many
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    @Override
    public <T> T updateLinks(T obj, String regex) {
        return this.dao().updateLinks(obj, regex);
    }

    /**
     * 多对多关联是通过一个中间表将两条数据表记录关联起来。
     * <p>
     * 而这个中间表可能还有其他的字段，比如描述关联的权重等
     * <p>
     * 这个操作可以让你一次更新某一个对象中多个多对多关联的数据
     *
     * @param classOfT 对象类型
     * @param regex    正则表达式，描述了那种多对多关联字段将被执行该操作
     * @param chain    针对中间关联表的名值链。
     * @param cnd      针对中间关联表的 WHERE 条件
     * @return 共有多少条数据被更新
     * @see org.nutz.dao.entity.annotation.ManyMany
     */
    @Override
    public int updateRelation(Class<?> classOfT, String regex, Chain chain, Condition cnd) {
        return this.dao().updateRelation(classOfT, regex, chain, cnd);
    }

    /**
     * 基于版本的更新，版本不一样无法更新到数据
     *
     * @param obj 需要更新的对象, 必须有version属性
     * @return 若更新成功, 大于0, 否则小于0
     */
    @Override
    public int updateWithVersion(Object obj) {
        return this.dao().updateWithVersion(obj);
    }

    /**
     * 基于版本的更新，版本不一样无法更新到数据
     *
     * @param obj         需要更新的对象, 必须有version属性
     * @param fieldFilter 需要过滤的字段设置
     * @return 若更新成功, 大于0, 否则小于0
     */
    @Override
    public int updateWithVersion(Object obj, FieldFilter fieldFilter) {
        return this.dao().updateWithVersion(obj, fieldFilter);
    }

    /**
     * 乐观锁, 以特定字段的值作为限制条件,更新对象,并自增该字段.
     * <p/>
     * 执行的sql如下:
     * <p/>
     * <code>update t_user set age=30, city="广州", version=version+1 where name="wendal" and version=124;</code>
     *
     * @param obj         需要更新的对象, 必须带@Id/@Name/@Pk中的其中一种.
     * @param fieldFilter 需要过滤的属性. 若设置了哪些字段不更新,那务必确保过滤掉fieldName的字段
     * @param fieldName   参考字段的Java属性名.默认是"version",可以是任意数值字段
     * @return 若更新成功, 返回值大于0, 否则小于等于0
     */
    @Override
    public int updateAndIncrIfMatch(Object obj, FieldFilter fieldFilter, String fieldName) {
        return this.dao().updateAndIncrIfMatch(obj, fieldFilter, fieldName);
    }

    /**
     * 获取某个对象,最大的 ID 值,这个对象必须声明了 '@Id'
     *
     * @return
     */
    @Override
    public int getMaxId() {
        return this.dao().getMaxId(this.getEntityClass());
    }

    /**
     * 批量删除
     *
     * @param obj
     * @param <T>
     * @return
     */
    @Override
    public <T> int delete(T obj) {
        return this.dao().delete(obj);
    }

    @Override
    public int delete(Cnd cnd) {
        Sql sql = Sqls.create("DELETE FROM $tableName $condition");
        sql.setVar("tableName", this.getEntityClass().getAnnotation(Table.class).value());
        sql.setCondition(cnd);
        sql.setCallback(Sqls.callback.integer());
        this.dao().execute(sql);
        return sql.getInt(0);
    }

    /**
     * 通过long主键删除数据
     *
     * @param id
     * @return
     */
    @Override
    public int delete(long id) {
        return this.dao().delete(this.getEntityClass(), id);
    }

    /**
     * 通过int主键删除数据
     *
     * @param id
     * @return
     */
    @Override
    public int delete(int id) {
        return this.dao().delete(this.getEntityClass(), id);
    }

    /**
     * 通过string主键删除数据
     *
     * @param id
     * @return
     */
    @Override
    public int delete(String id) {
        return this.dao().delete(this.getEntityClass(), id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @param isUuid
     */
    @Override
    public void delete(Integer[] ids, boolean isUuid) {
        if (isUuid) {
            this.dao().clear(this.getEntityClass(), Cnd.where("uuid", "in", ids));
        } else {
            this.dao().clear(this.getEntityClass(), Cnd.where("id", "in", ids));
        }
    }

    /**
     * 批量删除
     *
     * @param ids
     * @param isUuid
     */
    @Override
    public int delete(Long[] ids, boolean isUuid) {
        if (isUuid) {
            return this.dao().clear(this.getEntityClass(), Cnd.where("uuid", "in", ids));
        } else {
            return this.dao().clear(this.getEntityClass(), Cnd.where("id", "in", ids));
        }
    }

    /**
     * 批量删除
     *
     * @param ids
     * @param isUuid
     */
    @Override
    public int delete(String[] ids, boolean isUuid) {
        if (isUuid) {
            return this.dao().clear(this.getEntityClass(), Cnd.where("uuid", "in", ids));
        } else {
            return this.dao().clear(this.getEntityClass(), Cnd.where("id", "in", ids));
        }

    }


    /**
     * 伪删除
     *
     * @param id
     * @return
     * @Id
     */
    @Override
    public int vDelete(long id) {
        return this.dao().update(this.getEntityClass(), Chain.make("delFlag", true), Cnd.where("id", "=", id));
    }

    /**
     * 伪删除
     *
     * @param id
     * @return
     * @Name
     */
    @Override
    public int vDelete(String id) {
        return this.dao().update(this.getEntityClass(), Chain.make("delFlag", true), Cnd.where("uuid", "=", id));
    }


    /**
     * 伪删除
     *
     * @param id
     * @param isUuid
     * @return
     */
    @Override
    public int vDelete(String id, boolean isUuid) {
        if (isUuid) {
            return this.dao().update(this.getEntityClass(), Chain.make("delFlag", true), Cnd.where("uuid", "=", id));
        } else {
            return this.dao().update(this.getEntityClass(), Chain.make("delFlag", true), Cnd.where("id", "=", id));
        }
    }

    /**
     * 批量伪删除
     *
     * @param ids
     * @param isUuid
     * @return
     */
    @Override
    public int vDelete(String[] ids, boolean isUuid) {
        if (isUuid) {
            return this.dao().update(this.getEntityClass(), Chain.make("delFlag", true), Cnd.where("uuid", "in", ids));
        } else {
            return this.dao().update(this.getEntityClass(), Chain.make("delFlag", true), Cnd.where("id", "in", ids));
        }
    }

    /**
     * 清空表
     *
     * @return
     */
    @Override
    public int clear() {
        return this.dao().clear(this.getEntityClass());
    }

    /**
     * 清空表
     *
     * @return
     */
    @Override
    public int clear(String tableName) {
        return this.dao().clear(tableName);
    }

    /**
     * 按条件清除一组数据
     *
     * @return
     */
    @Override
    public int clear(Condition cnd) {
        return this.dao().clear(this.getEntityClass(), cnd);
    }

    /**
     * 按条件清除一组数据
     *
     * @return
     */
    @Override
    public int clear(String tableName, Condition cnd) {
        return this.dao().clear(tableName, cnd);
    }


    /**
     * 通过LONG主键获取部分字段值
     *
     * @param fieldName
     * @param id
     * @return
     */
    @Override
    public T getField(String fieldName, long id) {
        return Daos.ext(this.dao(), FieldFilter.create(this.getEntityClass(), fieldName))
                .fetch(this.getEntityClass(), id);
    }

    /**
     * 通过INT主键获取部分字段值
     *
     * @param fieldName
     * @param id
     * @return
     */
    @Override
    public T getField(String fieldName, int id) {
        return Daos.ext(this.dao(), FieldFilter.create(this.getEntityClass(), fieldName))
                .fetch(this.getEntityClass(), id);
    }


    /**
     * 通过NAME主键获取部分字段值
     *
     * @param fieldName 支持通配符 ^(a|b)$
     * @param name
     * @return
     */
    @Override
    public T getField(String fieldName, String name) {
        return Daos.ext(this.dao(), FieldFilter.create(this.getEntityClass(), fieldName))
                .fetch(this.getEntityClass(), name);
    }

    /**
     * 通过NAME主键获取部分字段值
     *
     * @param fieldName 支持通配符 ^(a|b)$
     * @param cnd
     * @return
     */
    @Override
    public T getField(String fieldName, Condition cnd) {
        return Daos.ext(this.dao(), FieldFilter.create(this.getEntityClass(), fieldName))
                .fetch(this.getEntityClass(), cnd);
    }

    /**
     * 查询获取部分字段
     *
     * @param fieldName 支持通配符 ^(a|b)$
     * @param cnd
     * @return
     */
    @Override
    public List<T> query(String fieldName, Condition cnd) {
        return Daos.ext(this.dao(), FieldFilter.create(this.getEntityClass(), fieldName))
                .query(this.getEntityClass(), cnd);
    }

    /**
     * 查询一组对象。你可以为这次查询设定条件
     *
     * @param cnd WHERE 条件。如果为 null，将获取全部数据，顺序为数据库原生顺序<br>
     *            只有在调用这个函数的时候， cnd.limit 才会生效
     * @return 对象列表
     */
    @Override
    public List<T> query(Condition cnd) {
        return dao().query(getEntityClass(), cnd);
    }

    @Override
    public List<T> query(Pager pager) {
        return dao().query(getEntityClass(), null, pager);
    }

    /**
     * 根据查询条件获取所有对象.<b>注意: 条件语句需要加上表名!!!</b>
     * <p/>
     * 这个方法是让@One关联的属性,通过left join一次性取出. 与query+fetchLinks是等价的
     *
     * @param cnd 查询条件,必须带表名!!!
     * @return 实体对象的列表, 符合regex的关联属性也会取出
     */
    @Override
    public List<T> queryByJoin(Condition cnd) {
        return queryByJoin(cnd, null);
    }

    /**
     * 根据查询条件获取所有对象.<b>注意: 条件语句需要加上表名!!!</b>
     * <p/>
     * 这个方法是让@One关联的属性,通过left join一次性取出. 与query+fetchLinks是等价的
     *
     * @param regex 需要过滤的关联属性,可以是null,取出全部关联属性.
     * @param cnd   查询条件,必须带表名!!!
     * @return 实体对象的列表, 符合regex的关联属性也会取出
     */
    @Override
    public List<T> queryByJoin(Condition cnd, String regex) {
        return dao().queryByJoin(getEntityClass(), regex, cnd);
    }

    /**
     * 获取全部数据
     *
     * @return
     */
    @Override
    public List<T> query() {
        return dao().query(getEntityClass(), null);
    }


    @Override
    public List<T> query(Sql sql) {
        return query(sql, null);
    }

    @Override
    public void deleteByUUIDs(String[] delectIds) {
        if (delectIds != null && delectIds.length > 0) {
            this.dao().clear(this.getEntityClass(), Cnd.where("uuid", "in", delectIds));
        }
    }

    @Override
    public void deleteByIds(Integer[] delectIds) {
        if (delectIds != null && delectIds.length > 0) {
            this.dao().clear(this.getEntityClass(), Cnd.where("id", "in", delectIds));
        }
    }

    @Override
    public List<T> query(Sql sql, int pageNum, int pageSiz) {
        return query(sql, new Pager(pageNum, pageSiz));
    }

    @Override
    public List<T> query(Sql sql, Pager pager) {
        sql.setPager(pager);
        sql.setEntity(dao().getEntity(getEntityClass()));
        sql.setCallback(Sqls.callback.entities());
        dao().execute(sql);
        return sql.getList(getEntityClass());
    }


    /**
     * @param cnd      查询条件
     * @param linkName 关联字段，支持正则 ^(a|b)$
     * @return
     */
    @Override
    public List<T> query(Condition cnd, String linkName) {
        List<T> list = this.dao().query(this.getEntityClass(), cnd);
        if (!Strings.isBlank(linkName)) {
            this.dao().fetchLinks(list, linkName);
        }
        return list;
    }

    /**
     * 获取全部数据
     *
     * @param linkName 关联字段，支持正则 ^(a|b)$
     * @return
     */
    @Override
    public List<T> query(String linkName) {
        return this.query(null, linkName);
    }


    /**
     * 分页关联字段查询
     *
     * @param cnd      查询条件
     * @param linkName 关联字段，支持正则 ^(a|b)$
     * @param pager    分页对象
     * @return
     */
    @Override
    public List<T> query(Condition cnd, String linkName, Pager pager) {
        List<T> list = this.dao().query(this.getEntityClass(), cnd, pager);
        if (!Strings.isBlank(linkName)) {
            this.dao().fetchLinks(list, linkName);
        }
        return list;
    }

    /**
     * 分页查询
     *
     * @param cnd   查询条件
     * @param pager 分页对象
     * @return
     */
    @Override
    public List<T> query(Condition cnd, Pager pager) {
        return dao().query(getEntityClass(), cnd, pager);
    }

    /**
     * 计算子节点ID
     *
     * @param tableName
     * @param cloName
     * @param value
     * @return
     */
    @Override
    public String getSubPath(String tableName, String cloName, String value) {
        final String val = Strings.sNull(value);
        Sql sql = Sqls.create("select " + cloName + " from " + tableName
                + " where " + cloName + " like '" + val + "____' order by "
                + cloName + " desc");
        sql.setCallback((conn, rs, sql1) -> {
            String rsvalue = val + "0001";
            if (rs != null && rs.next()) {
                rsvalue = rs.getString(1);
                int newvalue = NumberUtils.toInt(rsvalue
                        .substring(rsvalue.length() - 4)) + 1;
                rsvalue = rsvalue.substring(0, rsvalue.length() - 4)
                        + new java.text.DecimalFormat("0000")
                        .format(newvalue);
            }
            return rsvalue;
        });
        this.dao().execute(sql);
        return sql.getString();

    }

    /**
     * 自定义SQL统计
     *
     * @param sql
     * @return
     */
    @Override
    public int count(Sql sql) {
        return (int) Daos.queryCount(this.dao(), sql);
    }

    @Override
    public void execute(Sql sql) {
        this.dao().execute(sql);
    }

    /**
     * 自定义SQL返回Record记录集，Record是个MAP但不区分大小写
     * 别返回Map对象，因为MySql和Oracle中字段名有大小写之分
     *
     * @param sql
     * @return
     */
    @Override
    public List<Record> list(Sql sql) {
        sql.setCallback(Sqls.callback.records());
        this.dao().execute(sql);
        return sql.getList(Record.class);

    }

    /**
     * 自定义sql获取map key-value
     *
     * @param sql
     * @return
     */
    @Override
    public Map getMap(Sql sql) {
        sql.setCallback((conn, rs, sql1) -> {
            Map<String, String> map = new HashMap<>(1);
            while (rs.next()) {
                map.put(Strings.sNull(rs.getString(1)), Strings.sNull(rs.getString(2)));
            }
            return map;
        });
        this.dao().execute(sql);
        return sql.getObject(Map.class);
    }

    /**
     * 对某一个对象字段，进行计算。
     *
     * @param funcName  计算函数名，请确保你的数据库是支持这个函数的
     * @param fieldName 对象 java 字段名
     * @return 计算结果
     */
    @Override
    public int func(String funcName, String fieldName) {
        return this.dao().func(this.getEntityClass(), funcName, fieldName);
    }

    /**
     * 对某一个对象字段，进行计算。
     *
     * @param funcName  计算函数名，请确保你的数据库是支持这个函数的
     * @param fieldName 对象 java 字段名
     * @return 计算结果
     */
    @Override
    public Object func2(String funcName, String fieldName) {
        return this.dao().func2(this.getEntityClass(), funcName, fieldName);
    }

    /**
     * 对某一个数据表字段，进行计算。
     *
     * @param tableName 表名
     * @param funcName  计算函数名，请确保你的数据是支持库这个函数的
     * @param colName   数据库字段名
     * @return 计算结果
     */
    @Override
    public int func(String tableName, String funcName, String colName) {
        return this.dao().func(tableName, funcName, colName);
    }

    /**
     * 对某一个数据表字段，进行计算。
     *
     * @param tableName 表名
     * @param funcName  计算函数名，请确保你的数据是支持库这个函数的
     * @param colName   数据库字段名
     * @return 计算结果
     */
    @Override
    public Object func2(String tableName, String funcName, String colName) {
        return this.dao().func2(tableName, funcName, colName);
    }

    /**
     * 对某一个对象字段，进行计算。
     *
     * @param funcName  计算函数名，请确保你的数据库是支持这个函数的
     * @param fieldName 对象 java 字段名
     * @param cnd       过滤条件
     * @return 计算结果
     */
    @Override
    public int func(String funcName, String fieldName, Condition cnd) {
        return this.dao().func(this.getEntityClass(), funcName, fieldName, cnd);
    }

    /**
     * 对某一个对象字段，进行计算。
     *
     * @param funcName  计算函数名，请确保你的数据库是支持这个函数的
     * @param fieldName 对象 java 字段名
     * @param cnd       过滤条件
     * @return 计算结果
     */
    @Override
    public Object func2(String funcName, String fieldName, Condition cnd) {
        return this.dao().func2(this.getEntityClass(), funcName, fieldName, cnd);
    }

    /**
     * 对某一个数据表字段，进行计算。
     *
     * @param tableName 表名
     * @param funcName  计算函数名，请确保你的数据库是支持这个函数的
     * @param colName   数据库字段名
     * @param cnd       过滤条件
     * @return 计算结果
     */
    @Override
    public int func(String tableName, String funcName, String colName, Condition cnd) {
        return this.dao().func(tableName, funcName, colName, cnd);
    }

    /**
     * 对某一个数据表字段，进行计算。
     *
     * @param tableName 表名
     * @param funcName  计算函数名，请确保你的数据库是支持这个函数的
     * @param colName   数据库字段名
     * @param cnd       过滤条件
     * @return 计算结果
     */
    @Override
    public Object func2(String tableName, String funcName, String colName, Condition cnd) {
        return this.dao().func2(tableName, funcName, colName, cnd);
    }

    /**
     * 分页查询
     *
     * @param pageNumber
     * @param cnd
     * @return
     */
    @Override
    public LayuiTableDataListVO listPage(Integer pageNumber, Condition cnd) {
        return listPage(pageNumber, DEFAULT_PAGE_NUMBER, cnd);
    }

    /**
     * 分页查询
     *
     * @param pageNumber
     * @param sql
     * @return
     */
    @Override
    public LayuiTableDataListVO listPage(Integer pageNumber, Sql sql) {
        return listPage(pageNumber, DEFAULT_PAGE_NUMBER, sql);
    }

    /**
     * 分页查询
     *
     * @param pageNumber
     * @param tableName
     * @param cnd
     * @return
     */
    @Override
    public LayuiTableDataListVO listPage(Integer pageNumber, String tableName, Condition cnd) {
        return listPage(pageNumber, DEFAULT_PAGE_NUMBER, tableName, cnd);
    }

    /**
     * 分页查询(cnd)
     *
     * @param pageNumber
     * @param pageSize
     * @param cnd
     * @return
     */
    @Override
    public LayuiTableDataListVO listPage(Integer pageNumber, int pageSize, Condition cnd) {
        PageDataDTO pageDataDTO = listPageDto(pageNumber, pageSize, cnd);
        return LayuiTableDataListVO.pageByData(pageDataDTO.getData(), pageDataDTO.getCount().intValue());
    }

    /**
     * 分页查询,获取部分字段(cnd)
     *
     * @param pageNumber
     * @param pageSize
     * @param cnd
     * @param fieldName  支持通配符 ^(a|b)$
     * @return
     */
    @Override
    public LayuiTableDataListVO listPage(Integer pageNumber, int pageSize, Condition cnd, String fieldName) {
        PageDataDTO pageDataDTO = listPageDto(pageNumber, pageSize, cnd, fieldName);
        return LayuiTableDataListVO.pageByData(pageDataDTO.getData(), pageDataDTO.getCount().intValue());
    }

    /**
     * 分页查询(tabelName)
     *
     * @param pageNumber
     * @param pageSize
     * @param tableName
     * @param cnd
     * @return
     */
    @Override
    public LayuiTableDataListVO listPage(Integer pageNumber, int pageSize, String tableName, Condition cnd) {
        PageDataDTO pageDataDTO = listPageDto(pageNumber, pageSize, tableName, cnd);
        return LayuiTableDataListVO.pageByData(pageDataDTO.getData(), pageDataDTO.getCount().intValue());
    }

    /**
     * 分页查询(sql)
     *
     * @param pageNumber
     * @param pageSize
     * @param sql
     * @return
     */
    @Override
    public LayuiTableDataListVO listPage(Integer pageNumber, int pageSize, Sql sql) {
        PageDataDTO pageDataDTO = listPageDto(pageNumber, pageSize, sql);
        return LayuiTableDataListVO.pageByData(pageDataDTO.getData(), pageDataDTO.getCount().intValue());
    }

    /**
     * 分页查询
     *
     * @param pageNumber
     * @param cnd
     * @return
     */
    @Override
    public PageDataDTO listPageDto(Integer pageNumber, Condition cnd) {
        return listPageDto(pageNumber, DEFAULT_PAGE_NUMBER, cnd);
    }

    /**
     * 分页查询
     *
     * @param pageNumber
     * @param sql
     * @return
     */
    @Override
    public PageDataDTO listPageDto(Integer pageNumber, Sql sql) {
        return listPageDto(pageNumber, DEFAULT_PAGE_NUMBER, sql);
    }

    /**
     * 分页查询
     *
     * @param pageNumber
     * @param tableName
     * @param cnd
     * @return
     */
    @Override
    public PageDataDTO listPageDto(Integer pageNumber, String tableName, Condition cnd) {
        return listPageDto(pageNumber, DEFAULT_PAGE_NUMBER, tableName, cnd);
    }

    /**
     * 分页查询(cnd)
     *
     * @param pageNumber
     * @param pageSize
     * @param cnd
     * @return
     */
    @Override
    public PageDataDTO listPageDto(Integer pageNumber, int pageSize, Condition cnd) {
        pageNumber = getPageNumber(pageNumber);
        pageSize = getPageSize(pageSize);
        Pager pager = this.dao().createPager(pageNumber, pageSize);
        List<T> list = this.dao().query(this.getEntityClass(), cnd, pager);
        pager.setRecordCount(this.dao().count(this.getEntityClass(), cnd));
        return new PageDataDTO((long) pager.getRecordCount(), list);
    }

    /**
     * 分页查询,获取部分字段(cnd)
     *
     * @param pageNumber
     * @param pageSize
     * @param cnd
     * @param fieldName  支持通配符 ^(a|b)$
     * @return
     */
    @Override
    public PageDataDTO listPageDto(Integer pageNumber, int pageSize, Condition cnd, String fieldName) {
        pageNumber = getPageNumber(pageNumber);
        pageSize = getPageSize(pageSize);
        Pager pager = this.dao().createPager(pageNumber, pageSize);
        List<T> list = Daos.ext(this.dao(), FieldFilter.create(this.getEntityClass(), fieldName)).query(this.getEntityClass(), cnd, pager);
        pager.setRecordCount(this.dao().count(this.getEntityClass(), cnd));
        return new PageDataDTO((long) pager.getRecordCount(), list);
    }

    /**
     * 分页查询(tabelName)
     *
     * @param pageNumber
     * @param pageSize
     * @param tableName
     * @param cnd
     * @return
     */
    @Override
    public PageDataDTO listPageDto(Integer pageNumber, int pageSize, String tableName, Condition cnd) {
        pageNumber = getPageNumber(pageNumber);
        pageSize = getPageSize(pageSize);
        Pager pager = this.dao().createPager(pageNumber, pageSize);
        List<Record> list = this.dao().query(tableName, cnd, pager);
        pager.setRecordCount(this.dao().count(tableName, cnd));
        return new PageDataDTO((long) pager.getRecordCount(), list);
    }

    /**
     * 分页查询(sql)
     *
     * @param pageNumber
     * @param pageSize
     * @param sql
     * @return
     */
    @Override
    public PageDataDTO listPageDto(Integer pageNumber, int pageSize, Sql sql) {
        pageNumber = getPageNumber(pageNumber);
        pageSize = getPageSize(pageSize);
        Pager pager = this.dao().createPager(pageNumber, pageSize);
        // 记录数需手动设置
        pager.setRecordCount((int) Daos.queryCount(this.dao(), sql));
        sql.setPager(pager);
        sql.setCallback(Sqls.callback.records());
        dao().execute(sql);
        return new PageDataDTO((long) pager.getRecordCount(), sql.getList(Record.class));
    }

    /**
     * 默认页码
     *
     * @param pageNumber
     * @return
     */
    protected int getPageNumber(Integer pageNumber) {
        return Lang.isEmpty(pageNumber) ? 1 : pageNumber;
    }

    /**
     * 默认页大小
     *
     * @param pageSize
     * @return
     */
    protected int getPageSize(int pageSize) {
        return pageSize == 0 ? DEFAULT_PAGE_NUMBER : pageSize;
    }
}
