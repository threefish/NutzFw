/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.message.entity;

import com.nutzfw.core.common.annotation.NutzFw;
import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;
import org.nutz.plugins.validation.annotation.Validations;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/30
 * 描述此类：
 */
@Table("news_body")
@Comment("新闻表")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class News extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("uuid")
    private String                uuid;
    @Column("categoryid")
    @Comment("新闻类别")
    @ColDefine(notNull = true)
    @Validations(required = true, el = "value>0", errorMsg = "新闻类别不能为空")
    @NutzFw(dictCode = "sys_news_category", required = true)
    private int                   categoryId;
    @Column
    @Comment("新闻标题")
    @ColDefine(width = 100)
    @Validations(required = true, strLen = {2, 100}, errorMsg = "新闻标题不能为空,长度2-100")
    @NutzFw
    private String                title;
    @Column
    @Comment("新闻内容")
    @ColDefine(type = ColType.TEXT)
    @Validations(required = true, strLen = {2, 65535}, errorMsg = "新闻内容不能为空,长度2-65535")
    @NutzFw(required = true, text = NutzFw.TEXT_RICH)
    private String                content;
    @Column
    @Comment("发布人")
    @ColDefine(width = 32, notNull = true)
    @NutzFw(show = false)
    private String                userid;
    @Column
    @Comment("新闻来源")
    @NutzFw
    private String                newsFrom;
    @Column
    @Comment("置顶")
    @ColDefine(notNull = true)
    @NutzFw
    private boolean               toped;
    @Column
    @Comment("冻结")
    @ColDefine(notNull = true)
    private boolean               locked;
    @Column
    @Comment("级别")
    @ColDefine(notNull = true)
    @Validations(required = true, el = "value>0", errorMsg = "新闻级别不能为空")
    @NutzFw(dictCode = "sys_news_level", required = true)
    private int                   level;
    @Column
    @Comment("定时发布")
    @NutzFw
    private Timestamp             publishTime;
    @Column("isrecomm")
    @Comment("新闻是否被推荐")
    @Default("0")
    @NutzFw
    private Boolean               isrecomm;
    @Comment("推荐图")
    @Column("recommimg")
    @ColDefine(width = 32)
    @NutzFw(attachment = true, maxLength = 32, attachmentAllIsImg = true)
    private String                recommimg;
    @Column("attachidlist")
    @Comment("新闻附件id集合")
    @ColDefine(width = 500)
    @NutzFw(attachment = true, maxLength = 500, attachmentMultiple = true)
    private String                attachidlist;
    /**
     * 接收部门
     */
    @Many(field = "newsId")
    private List<NewsReceiveDept> toDepts;
    /**
     * 角色接收
     */
    @Many(field = "newsId")
    private List<NewsReceiveRole> toRoles;
}
