/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.action;


import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.sys.entity.Menu;
import com.nutzfw.modules.sys.service.MenuService;
import com.nutzfw.modules.sys.util.MenuUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: huchuc@vip.qq.com
 * Date: 2016/11/17 0017
 * To change this template use File | Settings | File Templates.
 * 菜单管理模块
 */
@IocBean
@At("/sysMenu")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class MenuAction extends BaseAction {


    @Inject
    protected MenuService menuService;

    @Ok("btl:WEB-INF/view/sys/setting/menu/index.html")
    @GET
    @At("/index")
    @RequiresPermissions("sysMenu.index")
    @AutoCreateMenuAuth(name = "菜单管理", icon = "fa-eye", parentPermission = "sys.index")
    public List<HashMap> index() {
        List<Menu> menus = menuService.query(Cnd.orderBy().asc("short_no"));
        return MenuUtil.createHashMap(menus, "0");
    }

    @Ok("btl:WEB-INF/view/sys/setting/menu/child.html")
    @POST
    @At("/child")
    @RequiresPermissions("sysMenu.index")
    public List<HashMap> child(@Param("pid") String pid) {
        List<Menu> menus = menuService.query(Cnd.orderBy().asc("short_no"));
        return MenuUtil.createHashMap(menus, pid);
    }


    @Ok("json:{ignoreNull:false,locked:'createTime|updateTime'}")
    @POST
    @At("/tree")
    @RequiresPermissions("sysMenu.index")
    public List<Menu> tree() {
        List<Menu> menus = menuService.query(Cnd.orderBy().asc("short_no"));
        Menu menu = new Menu();
        menu.setId("0");
        menu.setPid("0");
        menu.setMenuName("根节点");
        menu.setLocked(false);
        menu.setDescription("");
        menu.setMenuTarget("");
        menus.add(menu);
        return menus;
    }

    /**
     * 排除不可以删除的菜单
     *
     * @return
     */
    @Ok("json:{ignoreNull:false,locked:'createTime|updateTime'}")
    @POST
    @At("/getTree")
    @RequiresPermissions("sysMenu.index")
    public List<Menu> getTree() {
        List<Menu> menus = menuService.query(Cnd.where("can_delect", "=", true).asc("short_no"));
        Menu menu = new Menu();
        menu.setId("0");
        menu.setPid("0");
        menu.setMenuName("根节点");
        menu.setLocked(false);
        menu.setDescription("");
        menu.setMenuTarget("");
        menus.add(menu);
        return menus;
    }

    @Ok("json")
    @POST
    @At("/modify")
    @RequiresPermissions("sysMenu.lock")
    @AutoCreateMenuAuth(name = "锁定解锁", type = AutoCreateMenuAuth.RESOURCE, permission = "sysMenu.lock", parentPermission = "sysMenu.index")
    public AjaxResult modify(@Param("id") String id, @Param("action") String action) {
        Menu uMenu = menuService.fetch(id);
        try {
            switch (action) {
                case "lock":
                    uMenu.setLocked(true);
                    menuService.update(uMenu);
                    return AjaxResult.sucess(uMenu, "修改成功");
                case "unlock":
                    uMenu.setLocked(false);
                    menuService.update(uMenu);
                    return AjaxResult.sucess(uMenu, "修改成功");
                default:
                    break;
            }
            return AjaxResult.error("参数不符");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


    @Ok("json")
    @POST
    @At("/update")
    @RequiresPermissions("sysMenu.update")
    @AutoCreateMenuAuth(name = "修改", type = AutoCreateMenuAuth.RESOURCE, permission = "sysMenu.update", parentPermission = "sysMenu.index")
    public AjaxResult update(@Param("::data.") Menu menu) {
        if (menu.getId().equals(menu.getPid())) {
            return AjaxResult.error("不能选择自己作为自己的上级菜单");
        }
        Menu uMenu = menuService.fetch(menu.getId());
        uMenu.setMenuName(menu.getMenuName());
        uMenu.setLocked(menu.isLocked());
        uMenu.setPid(menu.getPid());
        uMenu.setMenuTarget(menu.getMenuTarget());
        uMenu.setMenuIcon(menu.getMenuIcon());
        uMenu.setMenuType(menu.getMenuType());
        uMenu.setPermission(menu.getPermission());
        uMenu.setDescription(menu.getDescription());
        uMenu.setCanDelect(menu.isCanDelect());
        uMenu.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        try {
            menuService.update(uMenu);
            return AjaxResult.sucess(menu, "修改成功");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @Ok("json")
    @POST
    @At("/move")
    @RequiresPermissions("sysMenu.move")
    @AutoCreateMenuAuth(name = "移动", type = AutoCreateMenuAuth.RESOURCE, permission = "sysMenu.move", parentPermission = "sysMenu.index")
    public AjaxResult move(@Param("id") String id, @Param("type") String type) {
        if (!Strings.isBlank(type)) {
            Menu uMenu = menuService.fetch(id);
            Cnd cnd = Cnd.where("pid", "=", uMenu.getPid());
            cnd.asc("short_no");
            //取出同级菜单
            List<Menu> menuList = menuService.query(cnd);
            //重新整理顺序
            List<Menu> oldMenuList = new ArrayList<>();
            for (int i = 0; i < menuList.size(); i++) {
                Menu menu = menuList.get(i);
                menu.setShortNo(i);
                oldMenuList.add(menu);
            }
            //上移
            if ("up".equals(type)) {
                //升级后的菜单
                List<Menu> upMenuList = new ArrayList<>();
                for (Menu menu : oldMenuList) {
                    if (menu.getId().equals(id)) {
                        if (menu.getShortNo() == 0) {
                            return AjaxResult.error("已经是置顶了！");
                        } else {
                            menu.setShortNo(menu.getShortNo() - 1);
                        }
                    }
                    upMenuList.add(menu);
                }
                Collections.sort(upMenuList, new Menu());
                //重新整理顺序
                List<Menu> newMenuList = new ArrayList<>();
                for (int i = 0; i < upMenuList.size(); i++) {
                    Menu menu = upMenuList.get(i);
                    menu.setShortNo(i);
                    newMenuList.add(menu);
                }
                menuService.update(newMenuList);
            } else {//下移
                //降级级后的菜单
                List<Menu> upMenuList = new ArrayList<>();
                int last = 1;
                for (Menu menu : oldMenuList) {
                    if (menu.getId().equals(id)) {
                        if (last == oldMenuList.size()) {
                            return AjaxResult.error("已经是置底了！");
                        } else {
                            menu.setShortNo(menu.getShortNo() + 1);
                        }
                    }
                    last++;
                    upMenuList.add(menu);
                }
                Collections.sort(upMenuList, new Menu());
                //重新整理顺序
                List<Menu> newMenuList = new ArrayList<>();
                for (int i = 0; i < upMenuList.size(); i++) {
                    Menu menu = upMenuList.get(i);
                    menu.setShortNo(i);
                    newMenuList.add(menu);
                }
                menuService.update(newMenuList);
            }
        }
        return AjaxResult.sucess("修改成功");
    }

    @Ok("json")
    @POST
    @At("/add")
    @RequiresPermissions("sysMenu.add")
    @AutoCreateMenuAuth(name = "添加", type = AutoCreateMenuAuth.RESOURCE, permission = "sysMenu.add", parentPermission = "sysMenu.index")
    public AjaxResult add(@Param("::data.") Menu menu) {
        try {
            menu.setCreateTime(new Timestamp(System.currentTimeMillis()));
            menuService.insert(menu);
            return AjaxResult.sucess(menu, "添加成功");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 自动生成的
     *
     * @param menu
     * @return
     */
    @Ok("json")
    @POST
    @At("/autoAdd")
    @RequiresPermissions("sysMenu.autoAdd")
    @AutoCreateMenuAuth(name = "自动添加", type = AutoCreateMenuAuth.RESOURCE, permission = "sysMenu.autoAdd", parentPermission = "sysMenu.index")
    public AjaxResult autoAdd(@Param("::data.") Menu menu) {
        try {
            menu.setCreateTime(new Timestamp(System.currentTimeMillis()));
            menu.setCanDelect(true);
            menuService.insert(menu);
            return AjaxResult.sucess(menu, "添加成功");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @Ok("json")
    @POST
    @At("/del")
    @RequiresPermissions("sysMenu.del")
    @AutoCreateMenuAuth(name = "删除", type = AutoCreateMenuAuth.RESOURCE, permission = "sysMenu.del", parentPermission = "sysMenu.index")
    public AjaxResult del(@Param("::data.") Menu menu) {
        try {
            Menu uMenu = menuService.fetch(menu.getId());
            if (uMenu.isCanDelect()) {
                List<Menu> menuList = menuService.query(Cnd.where("pid", "=", uMenu.getId()));
                if (menuList.size() == 0) {
                    int flag = menuService.delete(menu);
                    return AjaxResult.sucess(menu, flag > 0 ? "删除成功" : "删除成功");
                } else {
                    return AjaxResult.error("当前菜单下还有子菜单不允许删除");
                }
            } else {
                return AjaxResult.error("系统菜单不允许删除");
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
