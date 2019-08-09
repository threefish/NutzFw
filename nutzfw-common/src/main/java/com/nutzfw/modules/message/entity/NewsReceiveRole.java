package com.nutzfw.modules.message.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2019/08/1  18:10
 * 描述此类：新闻接收角色
 */
@Table("news_receive_role")
@PK({"newsId", "roleId"})
@TableIndexes(@Index(fields = {"newsId", "roleId"}, name = "pks"))
@Comment("新闻接收角色关联")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class NewsReceiveRole extends BaseEntity {

    @Column
    @ColDefine(width = 32, notNull = true)
    private String newsId;

    @Column
    @ColDefine(width = 32, notNull = true)
    private String roleId;

    @Column
    @ColDefine(width = 32, notNull = true)
    private String roleName;
}
