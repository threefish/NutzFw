/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.biz.impl;

import com.github.threefish.nutz.sqltpl.SqlsTplHolder;
import com.github.threefish.nutz.sqltpl.annotation.SqlsXml;
import com.github.threefish.nutz.sqltpl.service.ISqlDaoExecuteService;
import com.github.threefish.nutz.sqltpl.service.ISqlTpl;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.modules.organize.entity.Department;
import com.nutzfw.modules.organize.service.DepartmentService;
import com.nutzfw.modules.organize.service.JobService;
import com.nutzfw.modules.organize.service.UserAccountJobService;
import com.nutzfw.modules.organize.vo.DeptJobTreeVO;
import com.nutzfw.modules.sys.biz.RoleBiz;
import com.nutzfw.modules.sys.entity.*;
import com.nutzfw.modules.sys.service.*;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.entity.Entity;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.nutz.trans.Trans;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ysy
 * @data 2018/6/5
 */
@IocBean(name = "roleBiz")
@SqlsXml
public class RoleBizImpl implements RoleBiz, ISqlDaoExecuteService, ISqlTpl {


    @Inject
    private UserAccountJobService userAccountJobService;
    @Inject
    private Dao dao;
    @Inject
    private RoleDepartmentService roleDepartmentService;
    @Inject
    private RoleJobService roleJobService;
    @Inject
    private DepartmentService departmentService;
    @Inject
    private JobService jobService;
    @Inject
    private RoleService roleService;
    @Inject
    private MenuService menuService;
    @Inject
    private RoleMenusService roleMenusService;
    private SqlsTplHolder sqlsTplHolder;

    @Override
    public SqlsTplHolder getSqlTplHolder() {
        return sqlsTplHolder;
    }

    @Override
    public void setSqlTpl(SqlsTplHolder sqlsTplHolder) {
        this.sqlsTplHolder = sqlsTplHolder;
    }

    /**
     * 查询所有的角色
     *
     * @param roleId
     * @return
     */
    @Override
    public AjaxResult queryAllRoleIds(String roleId) {
        List<RoleDepartment> roleDepartments = roleDepartmentService.query(Cnd.where("roleId", "=", roleId));
        List<RoleJob> roleJobs = roleJobService.query(Cnd.where("roleId", "=", roleId));
        List<DeptJobTreeVO> trees = new ArrayList<>();
        roleDepartments.forEach((r) -> trees.add(new DeptJobTreeVO(r.getDeptId(), "0", "", "dept")));
        roleJobs.forEach((r) -> trees.add(new DeptJobTreeVO(r.getJobId(), r.getDeptId(), "", "job")));
        return AjaxResult.sucess(trees, "查询成功!");
    }

    /**
     * 根据状态更新角色
     *
     * @param roleIds 角色Id数组
     * @param trees   选中的部门和岗位id
     * @param menuIds
     * @param status  是否删除后重建1,是,2,不是  @return
     */
    @Override
    public AjaxResult updateAllRoles(String[] roleIds, DeptJobTreeVO[] trees, String[] menuIds, Integer status) {
        List<Role> roles = roleService.query(Cnd.where("id", "in", roleIds));
        List<String> deptIds = new ArrayList<>();
        List<DeptJobTreeVO> jobTrees = new ArrayList<>();
        for (DeptJobTreeVO tree : trees) {
            if ("dept".equals(tree.getType())) {
                deptIds.add(tree.getId());
            } else if ("job".equals(tree.getType())) {
                jobTrees.add(tree);
            }
        }
        List<Department> depts = departmentService.query(Cnd.where("id", "in", deptIds));
        List<Menu> menus = menuService.query(Cnd.where("id", "in", menuIds));
        List<RoleJob> roleJobs = new ArrayList<>();
        List<RoleDepartment> roleDepartments = new ArrayList<>();
        List<RoleMenus> roleMenus = new ArrayList<>();
        for (Role role : roles) {
            for (Department dept : depts) {
                roleDepartments.add(new RoleDepartment(role.getId(), dept.getId()));
            }
            for (DeptJobTreeVO job : jobTrees) {
                roleJobs.add(new RoleJob(role.getId(), job.getId(), job.getPid()));
            }
            for (Menu menu : menus) {
                roleMenus.add(new RoleMenus(role.getId(), menu.getId()));
            }
        }
        Trans.exec(() -> {
            if (status == 2) {
                //单个修改
                List<RoleDepartment> roleDepartmentList = roleDepartmentService.query(Cnd.where("roleId", "in", roleIds));
                roleDepartmentService.delete(roleDepartmentList);
                List<RoleJob> roleJobsList = roleJobService.query(Cnd.where("roleId", "in", roleIds));
                roleJobService.delete(roleJobsList);
                List<RoleMenus> roleMenus1 = roleMenusService.query(Cnd.where("roleId", "in", roleIds));
                roleMenusService.delete(roleMenus1);
            } else if (status == 3) {
                //批量删除
                roleDepartmentService.delete(roleDepartments);
                roleJobService.delete(roleJobs);
                roleMenusService.delete(roleMenus);
            }
            if (status != 3) {
                for (RoleJob roleJob : roleJobs) {
                    roleJobService.insertOrUpdate(roleJob, FieldFilter.create(RoleJob.class, false), FieldFilter.locked(RoleJob.class, "delFlag"));
                }
                for (RoleDepartment roleDepartment : roleDepartments) {
                    roleDepartmentService.insertOrUpdate(roleDepartment, FieldFilter.create(RoleJob.class, false), FieldFilter.locked(RoleDepartment.class, "delFlag"));
                }
                for (RoleMenus rolemenu : roleMenus) {
                    roleMenusService.insertOrUpdate(rolemenu, FieldFilter.create(RoleMenus.class, false), FieldFilter.locked(RoleMenus.class, "delFlag"));
                }
            }
        });
        return AjaxResult.sucess("操作成功!");
    }

    @Override
    public List<Role> queryRoles(String userId) {
        return roleService.queryRoles(userId);
    }

    @Override
    public Set<String> queryManagerUserNames(String userId) {
        Set<String> userNames = new HashSet<>();
        //查询用户管理的所有部门
        List<RoleDepartment> roleDepartments = roleDepartmentService.queryManagerDepartment(queryRoleIds(userId));
        //查询用户管理的部门岗位
        roleDepartments.forEach(roleDepartment -> {
            List<String> userNameList = queryStrsBySql("queryManagerDeptJobUserNames", NutMap.NEW().setv("deptid", roleDepartment.getDeptId()));
            userNameList.forEach(userName -> userNames.add(userName));
        });
        return userNames;
    }

    @Override
    public Set<String> queryRoleIds(String userId) {
        return roleService.queryRoleIds(userId);
    }

    @Override
    public SqlsTplHolder getSqlsTplHolder() {
        return sqlsTplHolder;
    }

    @Override
    public Dao getDao() {
        return dao;
    }

    @Override
    public Entity getEntity() {
        throw Lang.makeThrow("当前类不应该调用这个方法");
    }

    @Override
    public Class getEntityClass() {
        throw Lang.makeThrow("当前类不应该调用这个方法");
    }
}
