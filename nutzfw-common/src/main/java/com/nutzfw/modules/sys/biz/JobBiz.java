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
