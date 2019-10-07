/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.util;

import com.nutzfw.core.MainModule;
import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.modules.sys.entity.Menu;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Modules;
import org.nutz.resource.Scans;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/14
 * 描述此类：
 */
public class ScanerAotuCreateMenusUtil {


    private final static Comparator CHINA_COMPARE = Collator.getInstance(java.util.Locale.CHINA);
    private              List<Menu> menuList      = new ArrayList<>();

    private List<Menu> scanPackage() {
        for (String scanPackage : MainModule.class.getAnnotation(Modules.class).packages()) {
            List<Class<?>> classList = Scans.me().scanPackage(scanPackage);
            classList.stream().filter(aClass -> aClass.getAnnotation(At.class) != null).forEach(aClass -> scanClass(aClass));
        }
        Collections.sort(menuList, (o1, o2) -> CHINA_COMPARE.compare(o1.getMenuName(), o2.getMenuName()));
        return createTree(menuList, "");
    }


    /**
     * 插入自动扫描的菜单
     *
     * @return
     */
    public void saveAutoScanMenus(Dao dao, boolean cleanData) {
        List<Menu> menuList = scanPackage();
        if (cleanData) {
            dao.create(Menu.class, true);
            saveChildren(dao, menuList);
        } else {
            saveChildren2(dao, menuList);
        }
    }

    /**
     * 初始化菜单
     *
     * @param dao
     * @param childrens
     */
    private void saveChildren(Dao dao, List<Menu> childrens) {
        childrens = dao.insert(childrens);
        List<Menu> update = new ArrayList<>();
        childrens.forEach(menu -> {
            if (Strings.isNotBlank(menu.getParentPermission())) {
                Menu menu0 = dao.fetch(Menu.class, Cnd.where("permission", "=", menu.getParentPermission()));
                menu.setPid(menu0.getId());
                update.add(menu);
            }
            if (menu.getChildren().size() > 0) {
                saveChildren(dao, menu.getChildren());
            }
        });
        dao.update(update);
    }

    /**
     * 忽略已有菜单
     *
     * @param dao
     * @param menuList
     */
    private void saveChildren2(Dao dao, List<Menu> menuList) {
        menuList.forEach(menu -> {
            List<Menu> newChild = new ArrayList<>();
            List<Menu> oldChild = new ArrayList<>();
            Menu menu0 = dao.fetch(Menu.class, Cnd.where("permission", "=", menu.getPermission()));
            if (menu0 == null) {
                dao.insert(menu);
            } else {
                menu.setId(menu0.getId());
                menu.setPid(menu0.getPid());
            }
            menu.getChildren().forEach(menu1 -> {
                Menu menu2 = dao.fetch(Menu.class, Cnd.where("permission", "=", menu1.getPermission()));
                if (menu2 == null) {
                    menu1.setPid(menu.getId());
                    newChild.add(menu1);
                } else {
                    menu2.setChildren(menu1.getChildren());
                    oldChild.add(menu2);
                }
            });
            dao.insert(newChild);
            menu.setChildren(newChild);
            menu.getChildren().addAll(oldChild);
            if (menu.getChildren() != null && menu.getChildren().size() > 0) {
                saveChildren2(dao, menu.getChildren());
            }
        });
    }


    /**
     * 迭代权限树
     *
     * @param menus
     * @param parentPermission
     * @return
     */
    private List<Menu> createTree(List<Menu> menus, String parentPermission) {
        List<Menu> childList = new ArrayList<>();
        for (Menu c : menus) {
            String permission = c.getPermission();
            String pid = c.getParentPermission();
            if (parentPermission.equals(pid)) {
                List<Menu> childs = createTree(menus, permission);
                c.setChildren(childs);
                childList.add(c);
            }
        }
        return childList;
    }


    /**
     * 取得子菜单
     *
     * @param menus
     * @param permission
     * @return
     */
    private Menu getChilds(List<Menu> menus, String permission) {
        Menu menu = null;
        sw:
        for (Menu p : menus) {
            if (p.getPermission().equals(permission)) {
                menu = p;
                break sw;
            } else if (p.getChildren() != null && p.getChildren().size() > 0) {
                menu = getChilds(p.getChildren(), permission);
                if (menu != null) {
                    break sw;
                }
            }
        }
        return menu;
    }


    private void scanClass(Class<?> klass) {
        Method[] methods = klass.getMethods();
        String classAtPath = getAtPath(klass.getAnnotation(At.class), klass, null);
        for (Method method : methods) {
            AutoCreateMenuAuth autoCreateMenuAuth = method.getAnnotation(AutoCreateMenuAuth.class);
            RequiresPermissions requiresPermissions = method.getAnnotation(RequiresPermissions.class);
            At methodAt = method.getAnnotation(At.class);
            if (autoCreateMenuAuth == null) {
                continue;
            }
            if (autoCreateMenuAuth != null && requiresPermissions == null) {
                throw new RuntimeException(MessageFormat.format("{0} 请设置@RequiresPermissions", method.toGenericString()));
            }
            String atPath = classAtPath + getAtPath(methodAt, null, method);
            Menu menu = new Menu();
            menu.setMenuName(autoCreateMenuAuth.name());
            if (!"".equals(autoCreateMenuAuth.atPath())) {
                menu.setMenuTarget(autoCreateMenuAuth.atPath());
            } else if (autoCreateMenuAuth.type() == AutoCreateMenuAuth.MENU) {
                menu.setMenuTarget(atPath);
            }
            menu.setMenuType(autoCreateMenuAuth.type());
            menu.setCanDelect(false);
            menu.setLocked(false);
            menu.setPid("0");
            menu.setShortNo(autoCreateMenuAuth.shortNo());
            menu.setMenuIcon(autoCreateMenuAuth.icon());
            menu.setDescription("");
            menu.setPermission(requiresPermissions.value()[0]);
            menu.setParentPermission(autoCreateMenuAuth.parentPermission());
            menu.setCreateTime(new Timestamp(System.currentTimeMillis()));
            menuList.add(menu);
        }
    }

    private String getAtPath(At at, Class<?> klass, Method method) {
        if (at == null || at.value().length == 0) {
            if (klass == null) {
                return Strings.lowerFirst(method.getName());
            } else {
                return Strings.lowerFirst(klass.getSimpleName());
            }
        } else {
            return at.value()[0];
        }
    }
}
