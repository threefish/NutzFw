/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.entity;


import com.nutzfw.core.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2018/2/6  18:10
 * 描述此类：菜单管理
 */
@Table("sys_menu")
@Comment("系统菜单")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Menu extends BaseEntity implements Serializable, Comparator<Menu> {

    private static final long       serialVersionUID = 1L;
    public               boolean    chkDisabled      = false;
    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("id")
    private              String     id;
    @Column
    private              String     pid;
    @Column("menu_name")
    private              String     menuName;
    @Column("menu_target")
    @Default("")
    private              String     menuTarget;
    /**
     * 权限标识
     */
    @Column("permission")
    @ColDefine(notNull = true)
    private              String     permission;
    @Column("menu_icon")
    private              String     menuIcon;
    /**
     * 权限类型 0 菜单  1数据
     */
    @Column("menu_type")
    private              int        menuType;
    @Column("locked")
    private              boolean    locked;
    @Column("can_delect")
    private              boolean    canDelect;
    @Column("description")
    private              String     description;
    @Column("short_no")
    private              int        shortNo;
    @Column("ct")
    private              Timestamp  createTime;
    @Column("ut")
    private              Timestamp  updateTime;
    private              List<Menu> children;
    /**
     * 通过权限表示自动判断子父节点
     */
    private              String     parentPermission;
    /**
     * ztree支持
     **/
    private              String     iconSkin;

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
        this.iconSkin = "fa " + menuIcon + " ";
    }

    @Override
    public int compare(Menu m0, Menu m1) {
        if (m0.getShortNo() > m1.getShortNo()) {
            return 0;
        } else {
            return -1;
        }
    }
}
