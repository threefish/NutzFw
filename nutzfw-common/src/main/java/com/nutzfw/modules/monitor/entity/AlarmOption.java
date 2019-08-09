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
