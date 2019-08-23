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
