package com.nutzfw.modules.sys.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import com.nutzfw.core.plugin.shiro.LoginTypeEnum;
import lombok.*;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/28
 * 描述此类：
 */
@Table("sys_user_login_history")
@Comment("用户登录历史表")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserLoginHistory extends BaseEntity {

    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("uuid")
    private String uuid;

    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("uid")
    private String uid;

    @Comment("登陆设备")
    @Column("type")
    private LoginTypeEnum type;

    @Comment("登陆IP")
    @Column("ip")
    private String ip;

    @Comment("浏览器")
    @Column("browser")
    private String browser;

    @Comment("操作系统")
    @Column("os")
    private String os;


}
