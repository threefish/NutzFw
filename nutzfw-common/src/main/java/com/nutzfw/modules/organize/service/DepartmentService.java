/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.organize.service;

import com.github.threefish.nutz.dto.PageDataDTO;
import com.nutzfw.core.common.service.BaseService;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.modules.organize.entity.Department;
import com.nutzfw.modules.organize.vo.DeptJobTreeVO;
import org.nutz.lang.util.NutMap;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/23
 * 描述此类：
 */
public interface DepartmentService extends BaseService<Department> {
    /**
     * 获取包含job的tree
     *
     * @return
     */
    List<DeptJobTreeVO> treeAboutJob();

    /**
     * 岗位分布图 获取包含job的tree
     *
     * @return
     */
    List<DeptJobTreeVO> treeAboutJob2();

    /**
     * 获取部门岗位树(包含人员数量)
     *
     * @return
     */
    List<DeptJobTreeVO> treeAboutJobAndCount();

    /**
     * 获取所有部门树
     */
    List<Department> tree();

    /**
     * 获取所有部门,以字符的格式
     *
     * @return
     */
    String[] allDeptStrs();

    /**
     * 拖动排序
     *
     * @param map
     */
    void sort(NutMap map);

    /**
     * 模板导出
     *
     * @return
     */
    Path createDownTemplate() throws IOException;

    /**
     * 导入部门
     *
     * @param attachId
     * @param deptId
     * @return
     */
    AjaxResult importDepartment(String attachId, String deptId);

    /**
     * 部门统计数据定制
     *
     * @param customizedParams
     * @return
     */
    List<NutMap> statisticsDeptUser(String customizedParams);

    /**
     * 获取部门下拉
     *
     * @return
     */
    AjaxResult getAllDeptSelect();

    /**
     * h获取部门树,包含数量
     *
     * @return
     */
    List<DeptJobTreeVO> treeAboutCount();

    /**
     * 取得指定ID部门下所有人员信息
     * @param deptId
     * @return
     */
    List<NutMap> listUserInfo(String deptId);

    /**
     * 根据指定用户，取得用户信息
     * @param queryUserNames
     * @return
     */
    List<NutMap> listUserInfo(List<String> queryUserNames);

    /**
     * 根据部门ID查询全部子节点
     * @param deptId
     * @return
     */
    List<Department> queryAndChild(String deptId);

    /**
     * 根据条件模糊查询用户信息
     * @param query
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageDataDTO queryListUserInfo(String query, int pageNum, int pageSize);
}
