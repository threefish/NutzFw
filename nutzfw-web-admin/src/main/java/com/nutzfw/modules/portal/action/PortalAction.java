package com.nutzfw.modules.portal.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.util.StringUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.core.common.vo.ZtreeBeanVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.portal.biz.PortalBiz;
import com.nutzfw.modules.portal.entity.Portal;
import com.nutzfw.modules.portal.service.PortalService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;
import org.nutz.plugins.validation.Errors;

import java.util.List;

/**
 * @author 叶世游
 * @date: 2018/6/15
 * 描述此类：系统管理
 */
@IocBean
@At("/portal")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class PortalAction extends BaseAction {

    @Inject
    PortalService portalService;
    @Inject
    PortalBiz portalBiz;

    @At("/index")
    @RequiresPermissions("portal.index")
    @AutoCreateMenuAuth(name = "门户管理", icon = "fa-cogs", shortNo = -1)
    public void index() {
    }

    @At("/manager")
    @Ok("btl:WEB-INF/view/portal/index.html")
    @RequiresPermissions("portal.manager")
    @AutoCreateMenuAuth(name = "门户管理", icon = "fa-cogs", parentPermission = "portal.index", shortNo = 0)
    public void manager() {
    }

    /**
     * 组合列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Ok("json")
    @At("/query")
    @POST
    @RequiresPermissions("portal.query")
    @AutoCreateMenuAuth(name = "查询组合列表", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-cogs", parentPermission = "portal.manager")
    public LayuiTableDataListVO manager(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize) {
        return portalService.listPage(pageNum, pageSize, Cnd.where("delFlag", "=", "0").asc("groupCode"));
    }

    /**
     * 增加或者修改组合
     *
     * @param group
     * @param errors
     * @return
     */
    @At("/save")
    @POST
    @Ok("json")
    @RequiresPermissions("portal.save")
    @AutoCreateMenuAuth(name = "添加和修改组合", icon = "fa-cogs", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "portal.manager")
    public AjaxResult save(@Param("::data.") Portal group, Errors errors) {
        try {
            if (errors.hasError()) {
                return AjaxResult.error(errors.getErrorsList().iterator().next());
            }
            Portal p = portalService.fetch(Cnd.where("groupCode", "=", group.getGroupCode()).and("delFlag", "=", 0));
            if (!StringUtil.isBlank(group.getId())) {
                if (p != null && !group.getId().equals(p.getId())) {
                    return AjaxResult.error("组合编号不能重复!");
                }
                portalService.updateIgnoreNull(group);
            } else {
                if (p != null) {
                    return AjaxResult.error("组合编号不能重复!");
                }
                portalService.insert(group);
            }
            return AjaxResult.sucess("操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("操作失败!");
        }
    }

    @At("/del")
    @POST
    @Ok("json")
    @RequiresPermissions("portal.del")
    @AutoCreateMenuAuth(name = "删除组合", icon = "fa-cogs", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "portal.manager")
    public AjaxResult del(@Param("ids") String ids) {
        try {
            List<Portal> groups = portalService.query(Cnd.where("id", "in", ids.split(",")));
            groups.forEach(g -> g.setDelFlag(true));
            portalService.update(groups);
            return AjaxResult.sucess("删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("删除失败");
        }
    }

    /**
     * 获取所有可配置的功能树
     *
     * @return
     */
    @At("/tree")
    @POST
    @Ok("json")
    @RequiresPermissions("portal.tree")
    @AutoCreateMenuAuth(name = "获取功能树", icon = "fa-cogs", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "portal.manager")
    public List<ZtreeBeanVO> tree() {
        try {
            return portalBiz.tree();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取已经配置的功能id
     *
     * @param id
     * @return
     */
    @At("/getSelectedIds")
    @POST
    @Ok("json")
    @RequiresPermissions("portal.getSelectedIds")
    @AutoCreateMenuAuth(name = "获取已选择功能", icon = "fa-cogs", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "portal.manager")
    public AjaxResult getSelectedIds(@Param("id") String id) {
        try {
            return portalBiz.getSelectedIds(id);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("获取失败");
        }
    }

    /**
     * 保存组和功能之间的关系
     *
     * @param ids
     * @param groupId
     * @return
     */
    @At("/saveGroupFunction")
    @POST
    @Ok("json")
    @RequiresPermissions("portal.saveGroupFunction")
    @AutoCreateMenuAuth(name = "保存组和功能之间的关系", icon = "fa-cogs", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "portal.manager")
    public AjaxResult saveGroupFunction(@Param("ids") String ids, @Param("groupId") String groupId) {
        try {
            return portalBiz.saveGroupFunction(groupId, ids);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("保存失败");
        }
    }

    /**
     * 获取所有人的tree
     *
     * @return
     */
    @Ok("json:{locked:'opAt|opBy'}")
    @POST
    @At("/userTree")
    @RequiresPermissions("portal.userTree")
    @AutoCreateMenuAuth(name = "获取用户树", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "portal.manager")
    public List<ZtreeBeanVO> userTree() {
        return portalBiz.userTree();
    }

    /**
     * 保存组和人的关系
     *
     * @param userIds
     * @param groupId
     * @return
     */
    @At("/savePortalUsers")
    @POST
    @Ok("json")
    @RequiresPermissions("portal.savePortalUsers")
    @AutoCreateMenuAuth(name = "保存组和人员之间的关系", icon = "fa-cogs", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "portal.manager")
    public AjaxResult savePortalUsers(@Param("userIds") String[] userIds, @Param("groupId") String groupId) {
        try {
            return portalBiz.savePortalUser(groupId, userIds);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("保存失败");
        }
    }

    /**
     * 获取已经配置人员id
     *
     * @param groupId
     * @return
     */
    @At("/getSelectedUserIds")
    @POST
    @Ok("json")
    @RequiresPermissions("portal.getSelectedUserIds")
    @AutoCreateMenuAuth(name = "获取已配置人员", icon = "fa-cogs", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "portal.manager")
    public AjaxResult getSelectedUserIds(@Param("groupId") String groupId) {
        try {
            return portalBiz.getSelectedUserIds(groupId);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("获取失败");
        }
    }
}
