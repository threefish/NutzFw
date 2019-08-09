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
