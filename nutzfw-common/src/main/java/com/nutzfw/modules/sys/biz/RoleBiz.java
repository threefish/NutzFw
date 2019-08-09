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
     * @param status  是否删除后重建1,是,2,不是  @return
     */
    AjaxResult updateAllRoles(String[] roleIds, DeptJobTreeVO[] trees, String[] menuIds, Integer status);

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
