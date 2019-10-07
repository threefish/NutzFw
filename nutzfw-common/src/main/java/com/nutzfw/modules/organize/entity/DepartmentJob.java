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

import java.io.Serializable;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/1
 * 描述此类：
 */
@Table("sys_department_job")
@PK({"deptId", "jobId"})
@TableIndexes(@Index(fields = {"deptId", "jobId"}, name = "pks"))
@Comment("部门岗位关联信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class DepartmentJob extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column("dept_id")
    @ColDefine(width = 32, notNull = true)
    private String deptId;


    @Column("job_id")
    @ColDefine(width = 32, notNull = true)
    private String jobId;

}
