package com.nutzfw.modules.sys.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;
import org.nutz.plugins.validation.annotation.Validations;

import java.sql.Timestamp;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/21
 * 描述此类：系统配置信息
 */
@Table("sys_options")
@Comment("系统配置信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Options extends BaseEntity {

    @Name
    @Comment("唯一ID")
    @Default("0")
    private String id;

    @Column
    @Default("0")
    @Comment("第一次登录后需要修改密码")
    private boolean fristLoginNeedChangePass;

    @Default("3")
    @Column
    @Comment("密码定时过期-按月-3个月过期提醒 0永不过期")
    private int passExpired;


    @Default("0")
    @Column
    @Comment("0无验证  1字母加数字不小于6位  2字母大小写都有加数字  3字母大小写都有加数字加特殊字符")
    private int passStrength;

    @Default("5")
    @Column
    @Comment("登录错误次数限制 输入次数-冻结15分钟")
    private int errorPassInputTimes;

    @Default("1")
    @Column
    @Comment("登录错误多少次需要输入验证码 -1 不需要验证码 0一直需要")
    private int needVerificationCode;

    @Column
    @Comment("系统版本号")
    @Validations(required = true, strLen = {3, 50}, errorMsg = "系统版本号不可为空，并且长度在3-50之间")
    private String version;

    @Column
    @Comment("注册时间")
    private Timestamp registrationTime;

    @Column
    @Comment("注册失效时间")
    private Timestamp registrationExpirationTime;

    @Column
    @Comment("单位名称-当单位名称修改后注册码失效-需要重新注册")
    @Validations(required = true, strLen = {2, 128}, errorMsg = "单位名称不可为空，并且长度在2-128之间")
    private String unitName;

    @Column
    @Comment("系统名称")
    @Validations(required = true, strLen = {2, 128}, errorMsg = "系统名称不可为空，并且长度在2-128之间")
    private String productLongName;

    @Column
    @Comment("系统英文名称")
    @Validations(required = true, strLen = {2, 128}, errorMsg = "系统英文名称不可为空，并且长度在2-128之间")
    private String productEnLongName;

    @Column
    @Comment("系统logo图片")
    private String productLogo;


    @Column
    @Comment("系统默认主题")
    @Default("1")
    private int theme;

    @Column
    @Default("0")
    @Comment("开启扫描登录")
    private boolean qrCodeLogin;

    @Column
    @Comment("系统简称")
    @Validations(required = true, strLen = {1, 50}, errorMsg = "系统简称不可为空，并且长度在1-50之间")
    private String productSortName;

}

