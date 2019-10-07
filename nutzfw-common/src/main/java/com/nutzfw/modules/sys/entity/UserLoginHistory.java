/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
