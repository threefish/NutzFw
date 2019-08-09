package com.nutzfw.modules.portal.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.interceptor.annotation.PrevInsert;

/**
 * @author 叶世游
 * @date 2018/6/19 14:39
 * @description 快捷功能和组合的绑定
 */
@Table("sys_portal_function")
@Comment("快捷功能和组合的绑定")
public class PortalFunction extends BaseEntity {

    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("uuid")
    private String id;

    @Column("groupId")
    @Comment("组合id")
    @ColDefine(width = 32, notNull = true)
    private String groupId;

    @Column("funId")
    @Comment("功能id")
    @ColDefine(width = 32, notNull = true)
    private String funId;

    @Column("type")
    @Comment("功能类型:1统计2快捷功能3消息提醒")
    private Integer type;

    public PortalFunction(String groupId, String funId, Integer type) {
        this.groupId = groupId;
        this.funId = funId;
        this.type = type;
    }

    public PortalFunction(String id, String groupId, String funId, Integer type) {
        this(groupId, funId, type);
        this.id = id;
    }

    public PortalFunction() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getFunId() {
        return funId;
    }

    public void setFunId(String funId) {
        this.funId = funId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
