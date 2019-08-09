package com.nutzfw.modules.sys.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018/2/6  18:10
 * 描述此类：系统菜单角色关联
 */
@Table("sys_role_menu")
@PK({"roleId", "menuId"})
@TableIndexes(@Index(fields = {"roleId", "menuId"}, name = "pks"))
@Comment("系统菜单角色关联")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class RoleMenus extends BaseEntity {

    @Column("role_id")
    @ColDefine(width = 32, notNull = true)
    private String roleId;

    @Column("menu_id")
    @ColDefine(width = 32, notNull = true)
    private String menuId;
}
