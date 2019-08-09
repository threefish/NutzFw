package com.nutzfw.modules.sys.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.core.common.util.StringUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.modules.sys.entity.Role;
import com.nutzfw.modules.sys.service.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2018/1/9  17:09
 * 描述此类：
 */
@IocBean(args = {"refer:dao"}, name = "roleService")
public class RoleServiceImpl extends BaseServiceImpl<Role> implements RoleService {
    public RoleServiceImpl(Dao dao) {
        super(dao);
    }

    /**
     * 保存或者修改角色
     * 叶世游
     *
     * @param role
     * @return
     */
    @Override
    public AjaxResult saveOrUpdate(Role role) {
        Cnd cnd = Cnd.NEW();
        cnd.and("delFlag", "=", "0");
        if (!"".equals(role.getId())) {
            cnd.and("id", "!=", role.getId());
            cnd.and(new SqlExpressionGroup().and("role_name", "=", role.getRoleName()).or("role_code", "=", role.getRoleCode()));
            Role oldrole = fetch(cnd);
            if (oldrole != null) {
                return AjaxResult.error("角色名称或角色编码已存在");
            }
            update(role);
            return AjaxResult.sucess("修改成功");
        } else {
            cnd.and("role_name", "=", role.getRoleName());
            cnd.or("role_code", "=", role.getRoleCode());
            Role oldrole = fetch(cnd);
            if (oldrole != null) {
                return AjaxResult.error("角色名称或角色编码已存在");
            }
            Role newRole = insert(role);
            return AjaxResult.sucess(newRole, "添加成功");
        }
    }

    /**
     * 角色上移
     * 叶世游
     *
     * @param id
     * @param id
     * @return
     */
    @Override
    public AjaxResult upRole(String id, String pid) {
        List<Role> roles = query(Cnd.where("pid", "=", pid).orderBy("short_no", "asc"));
        Role role;
        Role otherRole;
        for (int i = 0; i < roles.size(); i++) {
            role = roles.get(i);
            role.setShortNo(i);
            if (role.getId().equals(id)) {
                if (i == 0) {
                    return AjaxResult.error("已经是最前面了");
                } else {
                    otherRole = roles.get(i - 1);
                    otherRole.setShortNo(i);
                    role.setShortNo(i - 1);
                    roles.set(i, otherRole);
                    roles.set(i - 1, role);
                }
            }
        }
        update(roles);
        return AjaxResult.sucessMsg("修改成功!");
    }

    /**
     * 角色下移
     *
     * @param id
     * @param pid
     * @return
     */
    @Override
    public AjaxResult downRole(String id, String pid) {
        List<Role> roles = query(Cnd.where("pid", "=", pid).orderBy("short_no", "asc"));
        Role role;
        Role otherRole;
        for (int i = 0; i < roles.size(); i++) {
            role = roles.get(i);
            role.setShortNo(i);
            if (role.getId().equals(id)) {
                if (i == roles.size() - 1) {
                    return AjaxResult.error("已经是最后面了");
                } else {
                    otherRole = roles.get(i + 1);
                    otherRole.setShortNo(i);
                    role.setShortNo(i + 1);
                    roles.set(i, otherRole);
                    roles.set(i + 1, role);
                    i++;
                }
            }
        }
        update(roles);
        return AjaxResult.sucessMsg("修改成功!");
    }

    /**
     * 角色删除
     *
     * @param id
     * @return
     */
    @Override
    public AjaxResult delRole(String id) {
        List<Role> roles = query(Cnd.where("pid", "=", id));
        if (roles.size() > 0) {
            return AjaxResult.error("还有下级,无法删除");
        } else {
            vDelete(id);
        }
        return AjaxResult.sucess("删除成功!");

    }

    /**
     * 禁用角色
     *
     * @param id
     * @return
     */
    @Override
    public AjaxResult lock(String id) {
        Role role = fetch(id);
        List<Role> roles = new ArrayList<>();
        if (role != null) {
            roles.add(role);
            rollRoles(roles, id);
            for (Role role1 : roles) {
                role1.setLocked(true);
            }
            update(roles);
        } else {
            return AjaxResult.error("没找到角色!");
        }
        return AjaxResult.sucess("禁用成功!");
    }

    /**
     * 启用角色
     *
     * @param id
     * @return
     */
    @Override
    public AjaxResult unlock(String id) {
        Role role = fetch(id);
        List<Role> roles = new ArrayList<>();
        if (role != null) {
            roles.add(role);
            rollRoles(roles, id);
            for (Role role1 : roles
            ) {
                role1.setLocked(false);
            }
            update(roles);
        } else {
            return AjaxResult.error("没找到角色!");
        }
        return AjaxResult.sucess("启用成功!");
    }

    //迭代查询角色
    private void rollRoles(List<Role> roles, String id) {
        List<Role> list = query(Cnd.where("pid", "=", id));
        if (list.size() > 0) {
            for (Role role : list
            ) {
                rollRoles(roles, role.getId());
            }
            roles.addAll(list);
        }
    }

    /**
     * 批量保存或更新角色
     *
     * @param roles
     * @return
     */
    @Override
    public AjaxResult saveOrUpdateList(List<Role> roles) {
        try {
            List<String> codes = new ArrayList<>();
            List<String> ids = new ArrayList<>();
            for (int i = 0; i < roles.size(); i++) {
                Role role = roles.get(i);
                if (StringUtil.isBlank(role.getRoleCode()) && StringUtil.isBlank(role.getRoleName())) {
                    roles.remove(role);
                    i--;
                } else if (StringUtil.isBlank(role.getRoleCode())) {
                    return AjaxResult.error("第" + (i + 1) + "行的角色编码不能为空!");
                } else if (StringUtil.isBlank(role.getRoleName())) {
                    return AjaxResult.error("第" + (i + 1) + "行的角色名字不能为空!");
                } else {
                    if (codes.indexOf(role.getRoleCode()) > -1) {
                        return AjaxResult.error("第" + (i + 1) + "行与第" + (codes.indexOf(role.getRoleCode()) + 1) + "行的角色编码冲突");
                    }
                    role.setOpBy(role.uid());
                    codes.add(role.getRoleCode());
                    ids.add(role.getId());
                }
            }
            if (roles.size() > 0) {
                List<Role> haveRoles = query(Cnd.where("roleCode", "in", codes).and("id", "not in", ids));
                if (haveRoles.size() > 0) {
                    codes.clear();
                    for (Role ro :
                            haveRoles) {
                        codes.add(ro.getRoleCode());
                    }
                    return AjaxResult.error("角色编码:" + StringUtils.join(codes, ",") + " 已存在!");
                }
                if (StringUtil.isBlank(roles.get(0).getId())) {
                    insert(roles);
                } else {
                    updateIgnoreNull(roles);
                }
            }
            return AjaxResult.sucess("保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("保存失败!");
        }
    }

    /**
     * 批量删除角色
     *
     * @param roleIds
     * @return
     */
    @Override
    public AjaxResult delRoleList(String roleIds) {
        try {
            List<Role> roles = query(Cnd.where("id", "in", roleIds.split(",")));
            for (Role role : roles) {
                role.setDelFlag(true);
            }
            update(roles);
            return AjaxResult.sucess("删除成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("删除失败!");
        }
    }


    /**
     * 根据用户ID取得拥有的角色
     *
     * @param userId
     * @return
     */
    @Override
    public List<Role> queryRoles(String userId) {
        Sql relesSql = Sqls.create("SELECT r.* from sys_user_account_role as ur,sys_role as r WHERE ur.role_id=r.id and ur.user_id=@userid");
        relesSql.setParam("userid", userId);
        relesSql.setCallback(Sqls.callback.entities());
        relesSql.setEntity(getEntity());
        execute(relesSql);
        return relesSql.getList(getEntityClass());
    }

    /**
     * 根据用户ID取得拥有的角色
     *
     * @param userId
     * @return
     */
    @Override
    public Set<String> queryRoleIds(String userId) {
        List<Role> roleList = queryRoles(userId);
        Set<String> roleIds = new HashSet<>();
        roleList.forEach(role -> roleIds.add(role.getId()));
        return roleIds;
    }

    @Override
    public List<Role> findGroupsByNameFilter(String filter) {
        return query(Cnd.where("locked", "=", false).and("role_name", "like", "%" + filter + "%").or("role_code", "like", "%" + filter + "%"));
    }

}
