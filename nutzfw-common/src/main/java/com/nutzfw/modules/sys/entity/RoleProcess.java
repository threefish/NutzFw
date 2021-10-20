/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2021/10/20
 * 描述此类：系统流程角色关联
 */
@Table("sys_role_process")
@PK({"roleId", "processDefId"})
@TableIndexes(@Index(fields = {"roleId", "processDefId"}, name = "pks"))
@Comment("系统菜单角色关联")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class RoleProcess extends BaseEntity {

    @Column("role_id")
    @ColDefine(width = 32, notNull = true)
    private String roleId;

    @Column("process_def_id")
    @ColDefine(width = 50, notNull = true)
    private String processDefId;
}
