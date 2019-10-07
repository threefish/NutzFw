/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.service;


import com.nutzfw.core.common.service.BaseService;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.modules.sys.entity.Role;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA Code Generator
 *
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年01月02日 15时39分07秒
 * 功能描述： 角色管理
 */
public interface RoleService extends BaseService<Role> {
    /**
     * 保存或者修改角色
     * 叶世游
     *
     * @param role
     * @return
     */
    AjaxResult saveOrUpdate(Role role);

    /**
     * 角色上移
     * 叶世游
     *
     * @param id
     * @param pid
     * @return
     */
    AjaxResult upRole(String id, String pid);

    /**
     * 角色下移
     *
     * @param id
     * @param pid
     * @return
     */
    AjaxResult downRole(String id, String pid);

    /**
     * 角色删除
     *
     * @param id
     * @return
     */
    AjaxResult delRole(String id);

    /**
     * 禁用角色
     *
     * @param id
     * @return
     */
    AjaxResult lock(String id);

    /**
     * 启用角色
     *
     * @param id
     * @return
     */
    AjaxResult unlock(String id);

    /**
     * 批量保存或更新角色
     *
     * @param roles
     * @return
     */
    AjaxResult saveOrUpdateList(List<Role> roles);

    /**
     * 批量删除角色
     *
     * @param roleIds
     * @return
     */
    AjaxResult delRoleList(String roleIds);

    /**
     * 取得用户角色信息
     *
     * @param userId
     * @return
     */
    List<Role> queryRoles(String userId);

    Set<String> queryRoleIds(String userId);

    List<Role> findGroupsByNameFilter(String filter);


}
