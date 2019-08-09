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
