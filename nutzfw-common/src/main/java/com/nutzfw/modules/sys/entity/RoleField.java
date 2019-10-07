/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.entity;

import com.nutzfw.modules.sys.enums.FieldAuth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.*;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/3/14
 */
@Table("sys_role_fields")
@PK({"roleId", "fieldId"})
@TableIndexes(@Index(fields = {"roleId", "fieldId"}, name = "pks"))
@Comment("字段和角色关联")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleField {

    @Column("role_id")
    @ColDefine(width = 32, notNull = true)
    private String roleId;

    @Column("table_id")
    private int tableId;

    @Column("field_id")
    private int fieldId;

    @Column("name")
    private String name;

    @Comment("不可见 只读  可读写")
    @Column("auth")
    private FieldAuth auth;

    /**
     * 可以拥有的权限
     */
    private List<FieldAuth> auths;
}
