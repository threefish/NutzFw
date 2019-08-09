package com.nutzfw.modules.monitor.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.util.Date;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2016/11/21 0021
 */
@Table("sys_apm_alarm")
@Comment("警告信息表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class APMAlarm extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("id")
    private String id;

    @Column("alarmType")
    private String type;

    @PrevInsert(now = true)
    @Column("alarmTime")
    private Date alarmTime;

    @Column("msg")
    private String msg;

    @Column("ip")
    private String ip;

    @Column("title")
    private String title;

    @Column("device")
    private String device;

    @Column("alarmUsage")
    private double usage;

    @Column("point")
    private double alarm;

    /**
     * 取得当前时间
     *
     * @return
     */
    public Date nowDate() {
        return new Date(System.currentTimeMillis());
    }
}