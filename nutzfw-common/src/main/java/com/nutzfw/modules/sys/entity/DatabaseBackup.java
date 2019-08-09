package com.nutzfw.modules.sys.entity;

import com.nutzfw.core.common.annotation.NutzFw;
import com.nutzfw.core.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;
import org.nutz.plugins.validation.annotation.Validations;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/1/17
 */
@Table("sys_database_backup")
@Comment("数据库备份")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DatabaseBackup extends BaseEntity {

    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column
    private String uuid;

    @Column
    @Comment("名称")
    @Validations(required = true, errorMsg = "名称必填")
    @NutzFw(required = true, placeholder = "名称必填")
    private String name;

    @Column
    @Comment("数据库用户名")
    @Validations(required = true, errorMsg = "用户名必填")
    @NutzFw(required = true, placeholder = "用户名必填")
    private String userName;

    @Column
    @Comment("数据库密码")
    @Validations(required = true, errorMsg = "密码必填")
    @NutzFw(required = true, placeholder = "密码必填")
    @ColDefine(width = 500)
    private String userPass;


    @Column
    @Comment("数据库IP")
    @Validations(required = true, errorMsg = "数据库IP必填")
    @NutzFw(required = true, placeholder = "数据库IP必填")
    private String ip;

    @Column
    @Comment("数据库端口")
    @Validations(required = true, errorMsg = "数据库端口必填")
    @NutzFw(required = true, placeholder = "数据库端口必填")
    private String port;

    @Column
    @Comment("数据库名称")
    @Validations(required = true, errorMsg = "数据库名称必填，多个采用 , 号隔开")
    @NutzFw(required = true, placeholder = "数据库名称必填,多个采用 , 号隔开")
    private String dbNames;
}
