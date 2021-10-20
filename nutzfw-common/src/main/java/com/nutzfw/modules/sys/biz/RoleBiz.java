/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.biz;

import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.modules.organize.vo.DeptJobTreeVO;
import com.nutzfw.modules.sys.entity.Role;

import java.util.List;
import java.util.Set;

/**
 * @author ysy
 * @author 黄川 huchuc@vip.qq.com
 * @data 2018/6/5 0002
 */
public interface RoleBiz {
    /**
     * 查询所有的角色
     *
     * @param roleId
     * @return
     */
    AjaxResult queryAllRoleIds(String roleId);

    /**
     * 根据状态更新角色
     *
     * @param roleIds 角色Id数组
     * @param trees   选中的部门和岗位id
     * @param menuIds
     * @param processDefIds
     * @param status  是否删除后重建1,是,2,不是  @return
     */
    AjaxResult updateAllRoles(String[] roleIds, DeptJobTreeVO[] trees, String[] menuIds,String[] processDefIds, Integer status);

    /**
     * 根据用户ID取得拥有的角色
     *
     * @param userId
     * @return
     */
    List<Role> queryRoles(String userId);

    Set<String> queryRoleIds(String userId);

    /**
     * 取得用户ID取得用户管理的所有人员
     * （根据多个不同部门下相同或不同的岗位）
     *
     * @param userId
     * @return
     */
    Set<String> queryManagerUserNames(String userId);

}
