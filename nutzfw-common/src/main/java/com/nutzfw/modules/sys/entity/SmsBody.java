package com.nutzfw.modules.sys.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/29
 * 描述此类：
 */
@Table("sys_sms_body")
@Comment("短信信息表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class SmsBody extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("uuid")
    private String uuid;

}
