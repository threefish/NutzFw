/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.flow.entity;

import com.nutzfw.core.common.entity.BaseTreeEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;


/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/15
 * 流程分类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("sys_flow_type")
@EqualsAndHashCode(callSuper = false)
public class FlowType extends BaseTreeEntity {

    @Column
    @Comment("类型名称")
    private String name;

    @Column
    @Comment("虚拟节点不可选")
    private boolean virtualNode;

}
