/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.organize.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;
import org.nutz.plugins.validation.annotation.Validations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/23
 * 描述此类：组织架构
 */
@Table("sys_department")
@Comment("部门机构")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Department extends BaseEntity implements Serializable, Comparator<Department> {

    private static final long serialVersionUID = 1L;

    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("id")
    private String id;

    @Column
    @ColDefine(width = 32, notNull = true)
    @Validations(required = true, errorMsg = "上级部门为必填项")
    private String pid;

    @Column
    @Comment("岗位编码(非必填)")
    private String code;

    @Column
    @Comment("部门名称")
    @Validations(required = true, strLen = {2, 50}, errorMsg = "部门名称不可为空，并且长度在2-50之间")
    private String name;

    @Column
    @Comment("编制数量")
    @ColDefine(width = 32)
    private int plaitNum;

    @Column
    @Comment("上级部门名称")
    @Validations(required = true, strLen = {2, 50}, errorMsg = "上级部门名称不可为空，并且长度在2-50之间")
    private String parentName;

    @Column
    @Comment("部门电话")
    @ColDefine(width = 100)
    private String telphone;

    @Column
    @Comment("传真")
    @ColDefine(width = 100)
    private String fax;

    @Column
    @Comment("部门地址")
    @ColDefine(width = 200)
    private String address;

    @Column
    @Comment("部门职能")
    @ColDefine(width = 200)
    private String functions;

    @Column("short_no")
    @Comment("排序号")
    private int shortNo;

    /**
     * 一对多关系
     */
    @Many(field = "deptId")
    private List<DepartmentJob> departmentJobList;
    @Builder.Default
    private List<Department>    children    = new ArrayList<>();
    /**
     * ztree支持
     **/
    private String              iconSkin;
    @Builder.Default
    private boolean             chkDisabled = false;

    @Override
    public int compare(Department o1, Department o2) {
        if (o1.getShortNo() > o2.getShortNo()) {
            return 0;
        } else {
            return -1;
        }
    }
}
