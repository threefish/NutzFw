package com.nutzfw.modules.sys.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author 创建人：叶世游
 * 创建时间: 2018/6/4  18:10
 * 描述此类：岗位角色关联
 */
@Table("sys_role_job")
@PK({"roleId", "jobId", "deptId"})
@TableIndexes(@Index(fields = {"roleId", "jobId", "deptId"}, name = "pks"))
@Comment("系统部门角色关联")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class RoleJob extends BaseEntity {

    @Column("role_id")
    @ColDefine(width = 32, notNull = true)
    private String roleId;

    @Column("job_id")
    @ColDefine(width = 32, notNull = true)
    private String jobId;

    @ColDefine(width = 32, notNull = true)
    @Column("dept_id")
    private String deptId;
}
