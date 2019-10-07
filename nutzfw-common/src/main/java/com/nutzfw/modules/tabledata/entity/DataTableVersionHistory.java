/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.tabledata.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/5
 * 描述此类：
 */
@Table("sys_data_version_history")
@Comment("表同步版本历史")
@PK({"tableId", "tableVersion"})
@TableIndexes(@Index(fields = {"tableId", "tableVersion"}, name = "pks"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DataTableVersionHistory extends BaseEntity {

    @Column
    @Comment("数据表ID")
    @ColDefine(notNull = true, width = 50)
    private int tableId;

    @Column
    @Comment("版本")
    @ColDefine(notNull = true, width = 50)
    private int tableVersion;


    @Column
    @Comment("版本信息")
    @ColDefine(type = ColType.TEXT, customType = "mediumblob")
    private String json;


}
