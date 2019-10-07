/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.front.entity;

import com.nutzfw.core.common.annotation.NutzFw;
import com.nutzfw.core.common.entity.BaseEntity;
import com.nutzfw.modules.organize.entity.UserAccount;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;
import org.nutz.lang.Strings;
import org.nutz.plugins.validation.annotation.Validations;

import java.util.Date;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/6/6
 */
@Table("Test_User")
@Comment("请假表")
@Data
@EqualsAndHashCode(callSuper=false)
public class TestLeave extends BaseEntity {

    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column
    private String id;
    /**
     * 请假原因
     */
    @Column
    @Comment("请假原因")
    @ColDefine(width = 2000)
    @NutzFw(required = true, text = NutzFw.TEXT_RICH)
    private String reason;
    /**
     * 流程实例编号
     */
    @Column
    @Comment("流程实例编号")
    @Validations(custom = "ckprocInsId", errorMsg = "请输入流程实例编号")
    private String procInsId;
    /**
     * 请假开始日期
     */
    @Column
    @Comment("请假开始日期")
    private Date   startTime;
    /**
     * 请假结束日期
     */
    @Column
    @Comment("请假结束日期")
    private Date   endTime;
    /**
     * 假期类别
     */
    @Column
    @Comment("假期类别")
    @NutzFw(required = true, placeholder = "xxxxx", maxLength = 1000, dictCode = "holiday_types")
    private int    leaveType;
    @Column
    @Comment("证明文件")
    @NutzFw(required = true, attachment = true, attachmentAllIsImg = true, attachmentMultiple = true, attachSuffix = "png,jpg")
    private String attach;
    @Column
    @Comment("部门审核意见")
    @NutzFw(show = false)
    private String deptLeadText;
    @Column
    @Comment("人事审核意见")
    @NutzFw(show = false)
    private String hrText;

    @Comment("和我一起请假的兄弟")
    @Column
    @Validations(required = true, errorMsg = "请输入和我一起请假的兄弟")
    @NutzFw(required = true, placeholder = "和我一起请假的兄弟", oneOne = UserAccount.class, oneOneField = "otherLeaveBrotherId")
    private String otherLeaveBrother;

    @Comment("兄弟ID")
    @Column
    @Validations(required = true, errorMsg = "请输入和我一起请假的兄弟")
    @NutzFw(show = false)
    private String otherLeaveBrotherId;


    public boolean ckprocInsId() {
        return Strings.isNotBlank(this.procInsId);
    }
}
