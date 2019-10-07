/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/29
 * 描述此类：
 */
@Table("sys_mail_body")
@Comment("邮件信息表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class MailBody extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("uuid")
    private String uuid;

    @Comment("业务类型")
    @Column("bizType")
    private int bizType;

    @Comment("业务编号-在业务中回查邮件信息")
    @Column("bizuuid")
    private String bizuuid;

    @Column("subject")
    @Comment("主题")
    @ColDefine(notNull = true)
    private String subject;

    @Column("htmlMsg")
    @Comment("消息内容")
    @ColDefine(type = ColType.TEXT, notNull = true)
    private String htmlMsg;

    @Comment("主送")
    @Column("toSend")
    @ColDefine(width = 2000, notNull = true)
    private String to;

    @Comment("抄送")
    @Column
    @ColDefine(width = 2000)
    private String cc;

    @Comment("密送")
    @Column
    @ColDefine(width = 2000)
    private String bcc;

    @Comment("定时发送时间-为null立即发送")
    @Column
    private Timestamp taskTime;

    @Comment("发送状态0待发 1已发 -1失败")
    @Column("status")
    private int status;

    @Comment("是否可以重发")
    @Default("true")
    @Column("reSend")
    private boolean reSend;

    @Comment("重新发送次数限制-最大不超过5次")
    @ColDefine(width = 1)
    @Column("maxSendNum")
    private int maxSendNum;

    @Comment("已发送次数")
    @Default("0")
    @Column("sendNum")
    private int sendNum;

    @Column("errorMsg")
    @Comment("错误信息内容")
    @ColDefine(type = ColType.TEXT)
    private String errorMsg;


}
