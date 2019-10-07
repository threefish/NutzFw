/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.portal.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;
import org.nutz.plugins.validation.annotation.Validations;

import java.io.Serializable;

/**
 * Created by ysy on 2018/6/15 19:27.
 */
@Table("sys_portal")
@Comment("首页组合")
@TableIndexes(@Index(fields = {"groupCode"}, name = "pks"))
public class Portal extends BaseEntity implements Serializable {
    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("uuid")
    private String id;

    @Column("groupName")
    @Comment("组合名称")
    @ColDefine(notNull = true)
    @Validations(required = true, errorMsg = "组合名称不能为空")
    private String groupName;

    @Column("groupCode")
    @Comment("组合编号")
    @ColDefine(notNull = true)
    @Validations(required = true, errorMsg = "组合编号不能为空")
    private String groupCode;

    @Column
    @Comment("冻结(1是,0否")
    @ColDefine(notNull = true)
    private boolean locked;


    public Portal() {
    }

    public Portal(String id, String groupName, String groupCode, boolean locked) {
        this.id = id;
        this.groupName = groupName;
        this.groupCode = groupCode;
        this.locked = locked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

}
