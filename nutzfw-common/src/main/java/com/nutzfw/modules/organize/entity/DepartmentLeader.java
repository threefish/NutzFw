package com.nutzfw.modules.organize.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import com.nutzfw.modules.organize.enums.LeaderTypeEnum;
import lombok.*;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/3
 */
@Table("sys_department_leader")
@PK({"deptId", "leaderType", "userName"})
@TableIndexes(@Index(fields = {"deptId", "leaderType", "userName"}, name = "pks"))
@Comment("部门领导关联信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class DepartmentLeader extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column("deptId")
    @ColDefine(width = 32, notNull = true)
    private String deptId;

    @Column("leaderType")
    @ColDefine(type = ColType.VARCHAR, width = 10, notNull = true)
    private LeaderTypeEnum leaderType;

    @Column("userName")
    @ColDefine(type = ColType.VARCHAR, width = 30, notNull = true)
    private String userName;

    @Column("realName")
    @ColDefine(width = 100, notNull = true)
    private String realName;


}
