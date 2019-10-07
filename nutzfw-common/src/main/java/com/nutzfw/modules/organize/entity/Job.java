/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.organize.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;
import org.nutz.plugins.validation.annotation.Validations;

import java.io.Serializable;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/1
 * 描述此类：
 */
@Table("sys_job")
@Comment("岗位信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Job extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("id")
    private String id;

    @Column
    @Comment("岗位编号")
    @ColDefine(width = 32, notNull = true)
    @Validations(required = true, errorMsg = "请填写岗位编号")
    private String code;

    @Column
    @Comment("岗位名称")
    @ColDefine(width = 50, notNull = true)
    @Validations(required = true, strLen = {2, 50}, errorMsg = "请填写岗位名称")
    private String name;

    @Column
    @Comment("岗位性质")
    @ColDefine(width = 3, notNull = true)
    @Validations(el = "value>0", errorMsg = "请选择岗位性质")
    private int nature;

    @Column
    @Comment("岗位分类")
    @ColDefine(width = 3, notNull = true)
    @Validations(el = "value>0", errorMsg = "请选择岗位分类")
    private int category;

    @Column
    @Comment("是否加入统计")
    @Default("1")
    @Validations(required = true, el = "value==0||value==1", errorMsg = "请选择是否加入统计")
    private int isStatistics;

    @Column
    @Comment("分配部门")
    @ColDefine(width = 5000)
    private String deptsDesc;

}
