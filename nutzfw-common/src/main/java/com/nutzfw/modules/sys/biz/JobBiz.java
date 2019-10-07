/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.biz;

import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.modules.organize.entity.Job;

/**
 * @author panchuang
 * @data 2018/6/2 0002
 */
public interface JobBiz {
    AjaxResult delJob(String id);

    AjaxResult allocation(String jobId, String deptId, String deptName);

    AjaxResult changeAllocation(String jobId, String deptId, String deptName);

    AjaxResult saveOrUpdate(Job job);

    AjaxResult jobDistr();

    /**
     * @param deptId
     * @return
     */
    AjaxResult queryByDepartment(String deptId);

    /**
     * 根据部门查询岗位(包括-不限-)
     *
     * @param deptId
     * @return
     */
    AjaxResult queryByDepartmentAll(String deptId);
}
