/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.biz;

import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import org.nutz.lang.util.NutMap;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @author panchuang
 * @data 2018/6/14 0014
 */
public interface UserAccountBiz {

    LayuiTableDataListVO jobSelectList(String query, String deptId, int pageNumber, int pageSize);

    /**
     * 人员管理页面列表
     *
     * @param pageNum
     * @param pageSize
     * @param name
     * @param status
     * @param review
     * @param deptId
     * @param jobId
     * @param hasRole
     * @return
     */
    LayuiTableDataListVO listPage(int pageNum, int pageSize, String name, int status, int review, String deptId, String jobId, boolean hasRole);

    /**
     * 创建导出模板
     *
     * @return
     */
    Path createDownTemplate() throws IOException;

    /**
     * 解析上传的文件
     *
     * @param attachId
     * @return
     */
    AjaxResult importUser(String attachId);

    /**
     * 保存人员和角色的关系
     *
     * @param userIds
     * @param roleIds
     * @param type
     * @return
     */
    AjaxResult saveUserRole(String userIds, String roleIds, Integer type);

    /**
     * 根据条件获取用户
     *
     * @param jobId
     * @param id
     * @param sort
     */
    AjaxResult queryByJob(String jobId, String id, Integer sort);

    /**
     * 后管通讯录查询用户
     *
     * @param key
     * @param pageNum
     * @param pageSize @return
     */
    List<NutMap> userSearch(String key, int pageNum, int pageSize);

    /**
     * 通过部门查询人员
     *
     * @param deptId
     */
    List<NutMap> usersByDeptId(String deptId);

    /**
     * 查询角色编码下的全部人员
     * @param roleCodes
     * @return
     */
    List<String> listUserNameByRoleCodes(List<String> roleCodes);
}
