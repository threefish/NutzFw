package com.nutzfw.modules.sys.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2021/10/29
 */
@Table("sys_notice")
@Comment("站内消息通知")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class SysNotice extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("uuid")
    private String uuid;

    @Comment("接收人")
    @Column("userName")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String userName;

    @ColDefine(width = 100)
    @Comment("正文")
    @Column("content")
    private String content;


    @Comment("链接地址")
    @Column("linkUrl")
    private String linkUrl;

    @Comment("是否已读")
    @Column("is_haveRead")
    private Boolean haveRead;


}
