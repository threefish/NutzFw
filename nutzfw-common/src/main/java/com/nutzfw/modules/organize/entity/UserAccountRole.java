package com.nutzfw.modules.organize.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018/2/6  18:10
 * 描述此类：系统用户角色关联
 */
@Table("sys_user_account_role")
@PK({"roleId", "userId"})
@TableIndexes(@Index(fields = {"roleId", "userId"}, name = "pks"))
@Comment("用户角色关联信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class UserAccountRole extends BaseEntity {

    @Column("role_id")
    @ColDefine(width = 32, notNull = true)
    private String roleId;

    @Column("user_id")
    @ColDefine(width = 32, notNull = true)
    private String userId;

}
