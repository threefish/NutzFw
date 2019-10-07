/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/15
 */
@Data
@EqualsAndHashCode(callSuper=false)
public  class BaseTreeEntity extends BaseEntity implements Serializable {

    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column
    protected String id;

    @Column
    @Comment("上级节点")
    @Default("")
    protected String pid;

    @Column
    @Comment("上级节点名称")
    @Default("")
    protected String pName;

    @Column
    protected int shortNo;

    protected List children;

}
