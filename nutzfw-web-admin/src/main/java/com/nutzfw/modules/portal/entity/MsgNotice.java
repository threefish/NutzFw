/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.portal.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;
import org.nutz.plugins.validation.annotation.Validations;

/**
 * @author 叶世游
 * @date 2018/6/19 14:03
 * @description 消息提醒
 */
@Table("sys_portal_msg_notice")
@Comment("消息提醒")
public class MsgNotice extends BaseEntity {
    public static final Integer FUN_TYPE = 3;
    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("uuid")
    private             String  id;

    @Column()
    @Comment("消息名称")
    @Validations(required = true, strLen = {2, 50}, errorMsg = "请填写消息名称")
    private String name;


    @Column("sort")
    @Comment("排序")
    @Validations(required = true, errorMsg = "请填写排序")
    private Integer sort;

    private Integer count;

    @Column("description")
    @Comment("描述")
    private String description;


    @Column("dateType")
    @Comment("提前时间类型(0不限1年2月3日)")
    @Validations(required = true, errorMsg = "请选择提前时间类型")
    private int dateType;

    @Column("dateNum")
    @Comment("时间提前量")
    @Validations(required = true, errorMsg = "时间提前量")
    private int    dateNum;
    @Column(value = "sqlStr")
    @Comment("查询的语句")
    @ColDefine(type = ColType.TEXT)
    private String sqlStr;
    @Column
    @Comment("表头,不包括序号")
    @ColDefine(type = ColType.TEXT)
    private String tableHead;


    public MsgNotice(String id, String name, Integer sort, Integer count, String description, int dateType, int dateNum, String sqlStr, String tableHead) {
        this.id = id;
        this.name = name;
        this.sort = sort;
        this.count = count;
        this.description = description;
        this.dateType = dateType;
        this.dateNum = dateNum;
        this.sqlStr = sqlStr;
        this.tableHead = tableHead;
    }

    public MsgNotice() {
    }

    public static Integer getFunType() {
        return FUN_TYPE;
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

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDateType() {
        return dateType;
    }

    public void setDateType(int dateType) {
        this.dateType = dateType;
    }

    public int getDateNum() {
        return dateNum;
    }

    public void setDateNum(int dateNum) {
        this.dateNum = dateNum;
    }

    public String getSqlStr() {
        return sqlStr;
    }

    public void setSqlStr(String sqlStr) {
        this.sqlStr = sqlStr;
    }

    public String getTableHead() {
        return tableHead;
    }

    public void setTableHead(String tableHead) {
        this.tableHead = tableHead;
    }
}
