/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.monitor.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2016/11/21 0021
 */
@Table("sys_alarm_option")
@Comment("监控项目")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class AlarmOption extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column("alarmType")
    @Name
    private String alarmType;

    @Column("percent")
    private double percent;

    @Column("email")
    private boolean email;

    @Column("sms")
    private boolean sms;

    @Column("listeners")
    private String listeners;

    @Column("listenersDesc")
    private String listenersDesc;


}
