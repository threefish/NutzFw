/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.business.leave.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.util.Date;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/11
 */
@Data
@Table("oa_leave")
@EqualsAndHashCode(callSuper = false)
public class Leave extends BaseEntity {
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
    private String reason;
    /**
     * 请假开始日期
     */
    @Column
    private Date   startTime;
    /**
     * 请假结束日期
     */
    @Column
    private Date   endTime;
    /**
     * 假期类别
     */
    @Column
    private String leaveType;
    /**
     * 部门领导意见
     */
    @Column
    private String deptLeadText;
    /**
     * 人事部门意见
     */
    @Column
    private String hrText;

    /**
     * 人事部门意见
     */
    @Column
    private String generalMmanagerText;
}
