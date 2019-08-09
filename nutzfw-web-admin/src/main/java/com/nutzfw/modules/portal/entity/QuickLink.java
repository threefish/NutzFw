package com.nutzfw.modules.portal.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;
import org.nutz.plugins.validation.annotation.Validations;

/**
 * @author 叶世游
 * @date 2018/6/19 14:17
 * @description 快捷功能入口
 */
@Table("sys_portal_quick_link")
@Comment("快捷功能")
public class QuickLink extends BaseEntity {
    public static final Integer FUN_TYPE = 2;
    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("uuid")
    private String id;

    @Column()
    @Comment("快捷功能名称")
    @Validations(required = true, strLen = {2, 100}, errorMsg = "快捷功能名称不能为空,长度2-100")
    private String name;


    @Column("openType")
    @Comment("打开方式,1弹窗,2新开")
    @Validations(required = true, errorMsg = "请选择打开方式")
    private Integer openType = 1;

    @Column("icon")
    @Comment("图标")
    @Validations(required = true, errorMsg = "请选择图标")
    private String icon;

    @Column("type")
    @Comment("链接类型(menu|link)")
    @Validations(required = true, errorMsg = "链接类型不能为空")
    private String type;

    @Column("link")
    @Comment("链接内容")
    @Validations(required = true, errorMsg = "链接地址不能为空")
    private String link;

    @Column("sort")
    @Comment("排序")
    @Validations(required = true, errorMsg = "排序不能为空")
    private Integer sort;


    @Column("description")
    @Comment("描述")
    private String description;

    public QuickLink() {
    }

    public QuickLink(String name, Integer openType, String icon, String type, String link, Integer sort, String description) {
        this.name = name;
        this.openType = openType;
        this.icon = icon;
        this.type = type;
        this.link = link;
        this.sort = sort;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOpenType() {
        return openType;
    }

    public void setOpenType(Integer openType) {
        this.openType = openType;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
