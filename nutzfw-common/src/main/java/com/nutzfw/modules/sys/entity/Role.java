package com.nutzfw.modules.sys.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 *
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2018/2/6  18:10
 * 描述此类：系统角色
 */
@Table("sys_role")
@Comment("系统角色")
@TableIndexes({@Index(name = "roleCode_nuique", fields = {"roleCode"})})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Role extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("id")
    private String id;

    @Comment("父ID")
    @Column("pid")
    @Default("0")
    private String pid;

    @Column("role_name")
    @ColDefine(type = ColType.VARCHAR, width = 18, notNull = true)
    private String roleName;

    @Column("role_code")
    @ColDefine(type = ColType.VARCHAR, width = 18, notNull = true)
    private String roleCode;

    @Column("locked")
    private boolean locked;

    @Column("short_no")
    private int shortNo;

}
