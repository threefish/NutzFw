/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.organize.entity;

import com.nutzfw.core.common.annotation.NutzFw;
import com.nutzfw.core.common.entity.BaseEntity;
import com.nutzfw.core.common.util.RegexUtil;
import lombok.*;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;
import org.nutz.json.JsonField;
import org.nutz.plugins.validation.annotation.Validations;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018/2/6  18:10
 * 描述此类：系统用户
 */
@Table("sys_user_account")
@Comment("系统用户")
@TableIndexes({@Index(name = "userName_unique", fields = {"userName"})})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class UserAccount extends BaseEntity implements Serializable {

    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("id")
    private String id;

    @ColDefine(width = 32, notNull = true)
    @Comment("复制主键")
    @Column("userid")
    private String userid;

    @Column("userName")
    @ColDefine(type = ColType.VARCHAR, width = 30, notNull = true)
    @Validations(required = true, strLen = {4, 30}, custom = "checkUserName", errorMsg = "用户名字母开头，允许字母数字下划线，长度4-30")
    private String userName;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 66, notNull = true)
    @JsonField(ignore = true)
    private String userPass;

    @Column
    @Comment("审核状态 0待审核 1已审核")
    @Default("0")
    private int review;

    @Column
    @Comment("审核意见")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String reviewOpinion;

    @Column
    @Comment("是否冻结")
    private boolean locked;

    @Column
    @Comment("真实姓名")
    @Validations(required = true, strLen = {2, 100}, errorMsg = "真实姓名不能为空,长度2-100")
    private String realName;

    @Column
    @ColDefine(type = ColType.VARCHAR, width = 66, notNull = true)
    @JsonField(ignore = true)
    private String salt;

    @Column
    @Comment("头像")
    private String avatar;

    @Column
    @Comment("部门")
    private String deptId;

    @Column
    @Comment("手机号")
    @ColDefine(width = 20)
    private String phone;

    @Column
    @Comment("电子邮箱")
    private String mail;

    @Column("create_by_date")
    @Comment("创建日期")
    @ColDefine(notNull = true)
    @PrevInsert(now = true)
    private Date createByDate;

    @Column("create_by_name")
    @Comment("创建人名称")
    @ColDefine(width = 50)
    private String createByName;

    @Column("create_by_userid")
    @Comment("创建人")
    @ColDefine(width = 32)
    private String createByUserid;

    @Column("update_by_date")
    @Comment("更新日期")
    private Date updateByDate;

    @Column("update_by_name")
    @Comment("更新人名称")
    @ColDefine(width = 50)
    private String updateByName;

    @Column("update_by_userid")
    @Comment("更新人")
    @ColDefine(width = 32)
    private String updateByUserid;

    @Column("index_page_sort")
    @Comment("首页模块排序")
    private String indexSort;


    @Column("gender")
    @Comment("性别")
    @NutzFw(dictCode = "sys_user_sex")
    private int gender;

    @Column("update_version")
    @Comment("更新版本-主要控制数据导入")
    @ColDefine(width = 11)
    @Default("0")
    private int        updateVersion;
    /**
     * 一对一
     */
    @One(target = Department.class, field = "deptId")
    private Department dept;

    /**
     * 一对多关系
     */
    @Many(field = "roleId")
    private List<UserAccountRole> userAccountRoles;

    public void setId(String id) {
        this.id = id;
        this.userid = id;
    }

    public boolean checkUserName() {
        return RegexUtil.isAccount(userName);
    }

}
