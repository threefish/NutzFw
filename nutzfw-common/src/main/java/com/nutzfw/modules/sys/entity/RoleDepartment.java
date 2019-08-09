package com.nutzfw.modules.sys.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author 创建人：叶世游
 * 创建时间: 2018/6/4  18:10
 * 描述此类：部门角色关联
 */
@Table("sys_role_department")
@PK({"roleId", "deptId"})
@TableIndexes(@Index(fields = {"roleId", "deptId"}, name = "pks"))
@Comment("系统菜单角色关联")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class RoleDepartment extends BaseEntity {

    @Column("role_id")
    @ColDefine(width = 32, notNull = true)
    private String roleId;

    @Column("dept_id")
    @ColDefine(width = 32, notNull = true)
    private String deptId;
}
