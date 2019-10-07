/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.portal.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.util.StringUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.portal.biz.QuickLinkBiz;
import com.nutzfw.modules.portal.entity.QuickLink;
import com.nutzfw.modules.portal.service.QuickLinkService;
import com.nutzfw.modules.sys.entity.Menu;
import com.nutzfw.modules.sys.service.MenuService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;
import org.nutz.plugins.validation.Errors;

import java.util.List;

/**
 * @author 叶世游
 * @date: 2018/6/19
 * 描述此类：快捷功能配置
 */
@IocBean
@At("/quicklink")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class QuickLinkAction extends BaseAction {

    @Inject
    QuickLinkService quickLinkService;

    @Inject
    QuickLinkBiz quickLinkBiz;

    @Inject
    MenuService menuService;

    @At("/manager")
    @Ok("btl:WEB-INF/view/portal/quicklink.html")
    @RequiresPermissions("quicklink.manager")
    @AutoCreateMenuAuth(name = "快捷功能管理", icon = "fa-cogs", parentPermission = "portal.index", shortNo = 2)
    public void manager() {
    }

    /**
     * 查询快捷功能
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Ok("json")
    @At("/query")
    @POST
    @RequiresPermissions("quicklink.query")
    @AutoCreateMenuAuth(name = "查询统计列表", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-cogs", parentPermission = "quicklink.manager")
    public LayuiTableDataListVO query(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize) {
        return quickLinkService.listPage(pageNum, pageSize, Cnd.where("delFlag", "=", "0").asc("sort"));
    }

    /**
     * 新增/修改快捷功能
     *
     * @param quickLink 快捷功能对象
     * @param errors
     * @return
     * @author panchuang
     */
    @Ok("json")
    @At("/saveOrUpdate")
    @POST
    @RequiresPermissions("quicklink.saveOrUpdate")
    @AutoCreateMenuAuth(name = "新增/修改快捷功能", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-cogs", parentPermission = "quicklink.manager")
    public AjaxResult saveOrUpdate(@Param("::data.") QuickLink quickLink, Errors errors) {
        try {
            if (errors.hasError()) {
                return AjaxResult.error(errors.getErrorsList().iterator().next());
            }
            if (!StringUtil.isBlank(quickLink.getId())) {
                quickLinkService.updateIgnoreNull(quickLink);
            } else {
                quickLinkService.insert(quickLink);
            }
            return AjaxResult.sucess("操作成功!");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return AjaxResult.error("删除失败");
    }

    /**
     * 删除快捷功能
     *
     * @param id
     * @return
     * @author panchuang
     */
    @Ok("json")
    @At("/del")
    @POST
    @RequiresPermissions("quicklink.del")
    @AutoCreateMenuAuth(name = "删除快捷功能", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-cogs", parentPermission = "quicklink.manager")
    public AjaxResult manager(@Param("id") String id) {
        try {
            return quickLinkBiz.delQuickLink(id);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return AjaxResult.error("删除失败");
    }

    /**
     * 查询菜单树
     *
     * @return
     * @author panchuang
     */
    @Ok("json:{ignoreNull:false,locked:'createTime|updateTime'}")
    @POST
    @At("/menuTree")
    public List<Menu> tree() {
        Cnd cnd = Cnd.NEW();
        cnd.and("delFlag", "=", 0);
        cnd.and("menuType", "=", 0);
        cnd.asc("short_no");
        List<Menu> menus = menuService.query(cnd);
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

}
