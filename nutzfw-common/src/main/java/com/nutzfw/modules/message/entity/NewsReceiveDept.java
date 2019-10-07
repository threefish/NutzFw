/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.message.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2019/08/1  18:10
 * 描述此类：新闻接收部门
 */
@Table("news_receive_dept")
@PK({"newsId", "deptId"})
@TableIndexes(@Index(fields = {"newsId", "deptId"}, name = "pks"))
@Comment("新闻接收部门关联")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class NewsReceiveDept extends BaseEntity {

    @Column
    @ColDefine(width = 32, notNull = true)
    private String newsId;

    @Column
    @ColDefine(width = 32, notNull = true)
    private String deptId;

    @Column
    @ColDefine(width = 32, notNull = true)
    private String deptName;
}
