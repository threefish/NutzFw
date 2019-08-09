package com.nutzfw.modules.portal.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;
import org.nutz.lang.util.NutMap;
import org.nutz.plugins.validation.annotation.Validations;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ysy on 2018/6/19 19:27.
 */
@Table("sys_portal_statistics_configure")
@Comment("首页统计配置")
public class StatisticsConfigure extends BaseEntity implements Serializable {
    public static final Integer FUN_TYPE = 1;
    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("uuid")
    private String id;

    @Column()
    @Comment("统计名称")
    @Validations(required = true, strLen = {2, 10}, errorMsg = "请填写统计名称")
    private String name;


    @Column("sort")
    @Comment("排序")
    private Integer sort;

    @Column("description")
    @Comment("描述")
    private String description;

    @Column("type")
    @Comment("图标类型(1饼图2柱状图")
    private Integer type;

    @Column("customized")
    @Comment("是否定制")
    private boolean customized = true;

    @Column("customizedType")
    @Comment("定制的类型")
    private String customizedType;
    @Column("customizedParams")
    @Comment("定制的参数")
    @ColDefine(type = ColType.TEXT)
    private String customizedParams;
    @Column(value = "sqlStr")
    @Comment("查询的语句")
    @ColDefine(type = ColType.TEXT)
    private String sqlStr;

    private List<NutMap> data;

    public StatisticsConfigure() {
    }

    public StatisticsConfigure(String id, String name, Integer order, String description, Integer sort, String sqlStr) {
        this.id = id;
        this.name = name;
        this.sort = order;
        this.description = description;
        this.type = sort;
        this.sqlStr = sqlStr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSqlStr() {
        return sqlStr;
    }

    public void setSqlStr(String sqlStr) {
        this.sqlStr = sqlStr;
    }

    public List<NutMap> getData() {
        return data;
    }

    public void setData(List<NutMap> data) {
        this.data = data;
    }

    public boolean isCustomized() {
        return customized;
    }

    public void setCustomized(boolean customized) {
        this.customized = customized;
    }

    public String getCustomizedType() {
        return customizedType;
    }

    public void setCustomizedType(String customizedType) {
        this.customizedType = customizedType;
    }

    public String getCustomizedParams() {
        return customizedParams;
    }

    public void setCustomizedParams(String customizedParams) {
        this.customizedParams = customizedParams;
    }
}