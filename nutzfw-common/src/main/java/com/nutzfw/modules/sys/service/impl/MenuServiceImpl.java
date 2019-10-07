/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.sys.entity.Menu;
import com.nutzfw.modules.sys.service.MenuService;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2018/2/6  18:10
 * 描述此类：
 */
@IocBean(name = "menuService", args = {"refer:dao"})
public class MenuServiceImpl extends BaseServiceImpl<Menu> implements MenuService {

    public MenuServiceImpl(Dao dao) {
        super(dao);
    }

    /**
     * 根据角色ID取得全部菜单
     *
     * @param userRoles
     * @return
     */
    @Override
    public List<Menu> querMenusByUserRoles(Set<String> userRoles) {
        Sql sql = Sqls.create("SELECT m.* FROM sys_role AS r, sys_role_menu AS rm,sys_menu AS m WHERE r.id = rm.role_id AND m.id = rm.menu_id and m.locked=0 AND FIND_IN_SET(r.id, @roleids) group by m.id");
        sql.setParam("roleids", Strings.join(",", userRoles));
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(getEntity());
        execute(sql);
        return sql.getList(getEntityClass());
    }
}
