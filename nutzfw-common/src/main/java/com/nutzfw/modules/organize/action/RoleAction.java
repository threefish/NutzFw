/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.organize.action;


import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.util.StringUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.organize.entity.UserAccountRole;
import com.nutzfw.modules.organize.service.UserAccountRoleService;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.organize.vo.DeptJobTreeVO;
import com.nutzfw.modules.sys.biz.RoleBiz;
import com.nutzfw.modules.sys.entity.Menu;
import com.nutzfw.modules.sys.entity.Role;
import com.nutzfw.modules.sys.entity.RoleField;
import com.nutzfw.modules.sys.entity.RoleMenus;
import com.nutzfw.modules.sys.service.MenuService;
import com.nutzfw.modules.sys.service.RoleFieldsService;
import com.nutzfw.modules.sys.service.RoleMenusService;
import com.nutzfw.modules.sys.service.RoleService;
import com.nutzfw.modules.sys.util.MenuUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.*;
import org.nutz.trans.Trans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author huchuc@vip.qq.com
 * Date: 2016/12/27 0027
 * To change this template use File | Settings | File Templates.
 */
@IocBean
@At("/sysRole")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class RoleAction extends BaseAction {

    @Inject
    protected RoleService roleService;

    @Inject
    protected MenuService        menuService;
    @Inject
    protected UserAccountService userAccountService;

    @Inject
    protected RoleMenusService roleMenusService;

    @Inject
    protected UserAccountRoleService userAccountRoleService;
    @Inject
    RoleFieldsService roleFieldsService;
    @Inject
    private RoleBiz roleBiz;

    @Ok("btl:WEB-INF/view/sys/organize/role/manager.html")
    @GET
    @POST
    @At("/manager")
    @RequiresPermissions("sysRole.index")
    @AutoCreateMenuAuth(name = "角色管理", icon = "fa-lock", shortNo = 0, parentPermission = "sysOrganize.index")
    public void manager() {
    }

    @Ok("json")
    @POST
    @At("/del")
    @RequiresPermissions("sysRole.del")
    @AutoCreateMenuAuth(name = "删除", type = AutoCreateMenuAuth.RESOURCE, permission = "sysRole.del", parentPermission = "sysRole.index")
    public AjaxResult del(@Param("id") String id) {
        Role role = roleService.fetch(id);
        if (role == null) {
            return AjaxResult.error("角色不存在！");
        }
        roleService.delete(role);
        return AjaxResult.sucess("删除成功");
    }

    @Ok("json")
    @POST
    @At("/info")
    @RequiresPermissions("sysRole.index")
    public AjaxResult info(@Param("id") String id) {
        Role role = roleService.fetch(id);
        if (role == null) {
            return AjaxResult.error("角色不存在！");
        }
        return AjaxResult.sucess(role);
    }

    @Ok("json:{ignoreNull:false,locked:'createTime|updateTime'}")
    @POST
    @At("/menus/tree")
    @RequiresPermissions("sysRole.index")
    public List<Menu> menusTree() {
        Cnd condition = Cnd.NEW();
        condition.and("locked", "=", false);
        condition.asc("short_no");
        List<Menu> menus = menuService.query(condition);
        return MenuUtil.createTree(menus, "0");
    }

    /**
     * 取得当前角色拥有的菜单或数据按钮的ID列表
     *
     * @return
     */
    @Ok("json:{ignoreNull:false}")
    @POST
    @At("/roleMenus")
    @RequiresPermissions("sysRole.index")
    public AjaxResult roleMenus(@Param("id") String roleId) {
        List<RoleMenus> list = roleMenusService.query(Cnd.where("role_id", "=", roleId));
        return AjaxResult.sucess(list);
    }

    /**
     * 取得当前角色拥有的菜单或数据按钮
     *
     * @return
     */
    @Ok("json:{ignoreNull:false}")
    @POST
    @GET
    @At("/menus/showRoleTree")
    @RequiresPermissions("sysRole.index")
    public AjaxResult showRoleTree(@Param("id") String roleId) {
        List<Menu> menus = new ArrayList<>();
        try {
            Sql sql = Sqls.create("SELECT * from sys_menu m,sys_role_menu r WHERE r.menu_id=m.id and r.role_id=@roleid");
            sql.setParam("roleid", roleId);
            sql.setCallback(Sqls.callback.records());
            menuService.execute(sql);
            List<Record> records = sql.getList(Record.class);
            List<Menu> finalMenus = menus;
            records.forEach((r) -> finalMenus.add(r.toPojo(Menu.class)));
            menus = MenuUtil.createTree(menus, "0");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AjaxResult.sucess(menus, "获取成功!");
    }

    /**
     * 更新当前角色权限
     *
     * @return
     */
    @Ok("json")
    @POST
    @At("/roleMenus/update")
    @RequiresPermissions("sysRole.roleMenus.update")
    @AutoCreateMenuAuth(name = "更新角色权限", type = AutoCreateMenuAuth.RESOURCE, permission = "sysRole.roleMenus.update", parentPermission = "sysRole.index")
    public AjaxResult roleMenusUpdate(@Param("id") String roleId, @Param("ids") String[] ids) {
        Role role = roleService.fetch(roleId);
        if (role == null) {
            return AjaxResult.error("角色不存在");
        }
        List<RoleMenus> reoles = roleMenusService.query(Cnd.where("role_id", "=", roleId));

        List<RoleMenus> newRolelist = new ArrayList<>();
        for (int i = 0, len = ids.length; i < len; i++) {
            newRolelist.add(new RoleMenus(roleId, ids[i]));
        }
        Trans.exec(() -> {
            roleService.delete(reoles);
            roleService.insert(newRolelist);
        });
        return AjaxResult.sucess("操作成功");
    }

    /**
     * 获得角色(部门\菜单\岗位)权限
     *
     * @return
     */
    @Ok("json")
    @POST
    @At("/queryAllRoles")
    @RequiresPermissions("sysRole.queryAllRoles")
    @AutoCreateMenuAuth(name = "获得角色权限", type = AutoCreateMenuAuth.RESOURCE, permission = "sysRole.queryAllRoles", parentPermission = "sysRole.index")
    public AjaxResult queryAllRoles(@Param("roleId") String roleId) {
        try {
            return roleBiz.queryAllRoleIds(roleId);
        } catch (Exception e) {
            return AjaxResult.error("操作失败");
        }
    }

    /**
     * 批量更新角色(部门\菜单\岗位)权限
     * ysy
     *
     * @return
     */
    @Ok("json")
    @POST
    @At("/updateAllRoles")
    @RequiresPermissions("sysRole.updateAllRoles")
    @AdaptBy(type = JsonAdaptor.class)
    @AutoCreateMenuAuth(name = "更新角色权限", type = AutoCreateMenuAuth.RESOURCE, permission = "sysRole.updateAllRoles", parentPermission = "sysRole.index")
    public AjaxResult updateAllRoles(@Param("roleIds") String[] roleIds,
                                     @Param("trees") DeptJobTreeVO[] trees,
                                     @Param("menuIds") String[] menuIds,
                                     @Param("status") Integer status) {
        try {


            return roleBiz.updateAllRoles(roleIds, trees, menuIds, status);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("操作失败");
        }
    }

    @Ok("json")
    @POST
    @At("/saveRoleFiledList")
    @RequiresPermissions("sysRole.updateAllRoles")
    @Aop(TransAop.READ_UNCOMMITTED)
    public AjaxResult saveRoleFiledList(@Param("roleIds") String[] roleIds, @Param("tableId") int tableId, @Param("roleData") String roleData) {
        try {
            for (String roleId : roleIds) {
                List<RoleField> roleFields = Json.fromJsonAsList(RoleField.class, roleData);
                roleFields.stream().forEach(roleField -> {
                    roleField.setTableId(tableId);
                    roleField.setRoleId(roleId);
                });
                roleFieldsService.delete(Cnd.where("role_Id", "=", roleId).and("table_Id", "=", tableId));
                roleFieldsService.insert(roleFields);
            }
            return AjaxResult.sucess("保存成功");
        } catch (Exception e) {
            return AjaxResult.error("保存失败！" + e.getMessage());
        }
    }

    /**
     * 为角色添加用户
     *
     * @return
     */
    @Ok("json")
    @POST
    @At("/roleUsers/addUser")
    @RequiresPermissions("sysRole.roleUsers.addUser")
    @AutoCreateMenuAuth(name = "为角色添加用户", type = AutoCreateMenuAuth.RESOURCE, permission = "sysRole.roleUsers.addUser", parentPermission = "sysRole.roleMenus.index")
    public AjaxResult roleUsersAddUser(@Param("roleId") String roleId, @Param("ids") String[] ids) {
        Role role = roleService.fetch(roleId);
        if (role == null) {
            return AjaxResult.error("角色不存在");
        }
        List<UserAccountRole> oldRoleUsers = userAccountRoleService.query(Cnd.where("role_id", "=", roleId));
        List<String> integers = new ArrayList<>();
        for (int i = 0, len = ids.length; i < len; i++) {
            boolean b = true;
            sw:
            for (UserAccountRole accountRole : oldRoleUsers) {
                if (accountRole.getUserId().equals(ids[i])) {
                    b = false;
                    break sw;
                }
            }
            if (b) {
                integers.add(ids[i]);
            }
        }
        List<UserAccountRole> newRoleUsers = new ArrayList<>();
        for (int i = 0, len = integers.size(); i < len; i++) {
            newRoleUsers.add(new UserAccountRole(roleId, integers.get(i)));
        }
        roleService.insert(newRoleUsers);
        return AjaxResult.sucess("添加成功");
    }

    /**
     * 为角色删除用户
     *
     * @return
     */
    @Ok("json")
    @POST
    @At("/roleUsers/delUser")
    @RequiresPermissions("sysRole.roleUsers.delUser")
    @AutoCreateMenuAuth(name = "为角色删除用户", type = AutoCreateMenuAuth.RESOURCE, permission = "sysRole.roleUsers.delUser", parentPermission = "sysRole.index")
    public AjaxResult roleUsersDelUser(@Param("roleId") String roleId, @Param("ids") String[] ids) {
        Role role = roleService.fetch(roleId);
        if (role == null) {
            return AjaxResult.error("角色不存在");
        }
        List<UserAccountRole> accountRoles = new ArrayList<>();
        for (int i = 0, len = ids.length; i < len; i++) {
            accountRoles.add(new UserAccountRole(roleId, ids[i]));
        }
        roleService.delete(accountRoles);
        return AjaxResult.sucess("删除成功");
    }


    @Ok("json:{ignoreNull:false,DateFormat:'yyyy-MM-dd HH:mm:ss',locked:'opAt|opBy'}")
    @POST
    @At("/tree")
    @RequiresPermissions("sysRole.tree")
    @AutoCreateMenuAuth(name = "角色树", type = AutoCreateMenuAuth.RESOURCE, permission = "sysRole.tree", parentPermission = "sysRole.index")
    public List<Role> tree() {
        List<Role> roles = roleService.query(Cnd.where("delFlag", "=", "0").orderBy("short_no", "asc"));
        Role role = new Role();
        role.setId("0");
        role.setPid("0");
        role.setRoleName(Cons.optionsCach.getUnitName());
        role.setLocked(false);
        roles.add(role);
        return roles;
    }

    @Ok("json")
    @POST
    @At("/saveOrUpdate")
    @RequiresPermissions("sysRole.saveOrUpdate")
    @AutoCreateMenuAuth(name = "添加/修改", type = AutoCreateMenuAuth.RESOURCE, permission = "sysRole.saveOrUpdate", parentPermission = "sysRole.index")
    public AjaxResult saveOrUpdate(@Param("::data.") Role role) {
        return roleService.saveOrUpdate(role);
    }

    @Ok("json")
    @POST
    @At("/saveList")
    @RequiresPermissions("sysRole.saveList")
    @AutoCreateMenuAuth(name = "批量添加/修改", type = AutoCreateMenuAuth.RESOURCE, permission = "sysRole.saveList", parentPermission = "sysRole.index")
    public AjaxResult saveList(@Param("::data.") List<Role> roles) {
        return roleService.saveOrUpdateList(roles);
    }


    /**
     * 上移
     *
     * @param id
     * @param pid
     * @return
     */
    @Ok("json")
    @POST
    @At("/upRole")
    @RequiresPermissions("sysRole.upRole")
    @AutoCreateMenuAuth(name = "上移", type = AutoCreateMenuAuth.RESOURCE, permission = "sysRole.upRole", parentPermission = "sysRole.index")
    public AjaxResult upRole(@Param("id") String id, @Param("pid") String pid) {
        return roleService.upRole(id, pid);
    }

    /**
     * 下移
     *
     * @param id
     * @param pid
     * @return
     */
    @Ok("json")
    @POST
    @At("/downRole")
    @RequiresPermissions("sysRole.downRole")
    @AutoCreateMenuAuth(name = "下移", type = AutoCreateMenuAuth.RESOURCE, permission = "sysRole.downRole", parentPermission = "sysRole.index")
    public AjaxResult downRole(@Param("id") String id, @Param("pid") String pid) {
        return roleService.downRole(id, pid);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @Ok("json")
    @POST
    @At("/delRole")
    @RequiresPermissions("sysRole.delRole")
    @AutoCreateMenuAuth(name = "删除角色", type = AutoCreateMenuAuth.RESOURCE, permission = "sysRole.delRole", parentPermission = "sysRole.index")
    public AjaxResult delRole(@Param("id") String id) {
        return roleService.delRole(id);
    }

    /**
     * 批量删除
     *
     * @param roleIds
     * @return
     */
    @Ok("json")
    @POST
    @At("/delRoleList")
    @RequiresPermissions("sysRole.delRoleList")
    @AutoCreateMenuAuth(name = "删除角色", type = AutoCreateMenuAuth.RESOURCE, permission = "sysRole.delRoleList", parentPermission = "sysRole.index")
    public AjaxResult delRoleList(@Param("roleIds") String roleIds) {
        return roleService.delRoleList(roleIds);
    }

    /**
     * 禁用
     *
     * @param id
     * @return
     */
    @Ok("json")
    @POST
    @At("/lock")
    @RequiresPermissions("sysRole.lock")
    @AutoCreateMenuAuth(name = "禁用", type = AutoCreateMenuAuth.RESOURCE, permission = "sysRole.lock", parentPermission = "sysRole.index")
    public AjaxResult lock(@Param("id") String id) {
        return roleService.lock(id);

    }

    /**
     * 启用
     *
     * @param id
     * @return
     */
    @Ok("json")
    @POST
    @At("/unlock")
    @RequiresPermissions("sysRole.unlock")
    @AutoCreateMenuAuth(name = "启用", type = AutoCreateMenuAuth.RESOURCE, permission = "sysRole.unlock", parentPermission = "sysRole.index")
    public AjaxResult unlock(@Param("id") String id) {
        return roleService.unlock(id);

    }

    /**
     * 表格展示
     *
     * @param roleName
     * @param roleCode
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GET
    @POST
    @At("/query")
    @Ok("json")
    @RequiresPermissions("sysRole.query")
    @AutoCreateMenuAuth(name = "查询", type = AutoCreateMenuAuth.RESOURCE, permission = "sysRole.query", parentPermission = "sysRole.index")
    public LayuiTableDataListVO query(@Param("roleName") String roleName, @Param("roleCode") String roleCode,@Param("pageNum") int pageNum, @Param("pageSize") int pageSize) {
        Cnd cnd = Cnd.where("delFlag", "=", 0);
        if (!StringUtil.isBlank(roleName)) {
            cnd.and("roleName", "like", "%" + roleName + "%");
        }
        if (!StringUtil.isBlank(roleCode)) {
            cnd.and("roleCode", "like", "%" + roleCode + "%");
        }
        cnd.desc("shortNo");
        return roleService.listPage(pageNum, pageSize, cnd);
    }


    /**
     * 弹窗选择
     *
     * @param key
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GET
    @POST
    @At("/likeQuery")
    @Ok("json")
    public LayuiTableDataListVO likeQuery(@Param("key") String key, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize) {
        Cnd cnd = Cnd.where("delFlag", "=", 0)
                .andEX("roleName", "like", "%" + key + "%")
                .orEX("roleCode", "like", "%" + key + "%");
        cnd.desc("shortNo");
        return roleService.listPage(pageNum, pageSize, cnd);
    }
}
