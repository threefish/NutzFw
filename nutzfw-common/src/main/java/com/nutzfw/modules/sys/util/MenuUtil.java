package com.nutzfw.modules.sys.util;


import com.nutzfw.modules.sys.entity.Menu;
import com.nutzfw.modules.sys.vo.MenuVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * Date: 2016/11/11 0011
 * To change this template use File | Settings | File Templates.
 */
public class MenuUtil {

    /**
     * 迭代ID,PID树
     *
     * @param menus
     * @param parentId
     * @return
     */
    public static List<Menu> createTree(List<Menu> menus, String parentId) {
        List<Menu> childList = new ArrayList<>();
        for (Menu c : menus) {
            String id = c.getId();
            String pid = c.getPid();
            if (parentId.equals(pid)) {
                List<Menu> childs = MenuUtil.createTree(menus, id);
                c.setChildren(childs);
                childList.add(c);
            }
        }
        Collections.sort(childList, new Menu());
        return childList;
    }

    /**
     * 排序ID,PID树
     *
     * @param oldMenu
     * @return
     */
    public static List<Menu> shortTree(List<Menu> oldMenu) {
        List<Menu> shortMenu = new ArrayList<>();
        for (Menu c : oldMenu) {
            if (c.getChildren() != null && c.getChildren().size() > 0) {
                List<Menu> nenuChilds = c.getChildren();
                Collections.sort(nenuChilds, new Menu());
                nenuChilds = shortTree(nenuChilds);
                c.setChildren(nenuChilds);
            }
            shortMenu.add(c);
        }
        return shortMenu;
    }


    /**
     * 取得子菜单
     *
     * @param menus
     * @param id
     * @return
     */
    public static Menu getChilds(List<Menu> menus, String id) {
        Menu menu = null;
        sw:
        for (Menu p : menus) {
            if (p.getId().equals(id)) {
                menu = p;
                break sw;
            } else if (p.getChildren() != null && p.getChildren().size() > 0) {
                menu = getChilds(p.getChildren(), id);
                if (menu != null) {
                    break sw;
                }
            }
        }
        return menu;
    }


    /**
     * 迭代ID,PID树
     *
     * @param menus
     * @param parentId
     * @return
     */
    public static List<HashMap> createHashMap(List<Menu> menus, String parentId) {
        menus = MenuUtil.createTree(menus, parentId);
        List<HashMap> list = new ArrayList<>();
        for (Menu menu : menus) {
            HashMap map = new HashMap(9);
            map.put("id", menu.getId());
            map.put("pid", menu.getPid());
            map.put("menuName", menu.getMenuName());
            map.put("menuTarget", menu.getMenuTarget());
            map.put("menuType", menu.getMenuType());
            map.put("menuIcon", menu.getMenuIcon());
            map.put("permission", menu.getPermission());
            map.put("locked", menu.isLocked());
            map.put("hasChild", (menu.getChildren() != null && menu.getChildren().size() > 0) ? true : false);
            list.add(map);
        }
        return list;
    }

    /**
     * @param menus
     * @param parentId
     * @return
     */
    public static List<MenuVO> createAdminLiteMenus(List<Menu> menus, String parentId) {
        List<MenuVO> childList = new ArrayList<>();
        for (Menu menu : menus) {
            if (menu.getMenuType() == 1) {
                continue;
            }
            String id = menu.getId();
            String pid = menu.getPid();
            MenuVO menuVO = new MenuVO();
            menuVO.setId(id);
            menuVO.setIcon("fa " + menu.getMenuIcon());
            menuVO.setText(menu.getMenuName());
            menuVO.setUrl(menu.getMenuTarget());
            menuVO.setChildren(createAdminLiteMenus(menu.getChildren(), pid));
            childList.add(menuVO);
        }
        return childList;
    }


}
