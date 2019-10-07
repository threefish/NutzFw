/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.biz.impl;

import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.modules.organize.entity.DepartmentJob;
import com.nutzfw.modules.organize.entity.Job;
import com.nutzfw.modules.organize.service.DepartmentJobService;
import com.nutzfw.modules.organize.service.DepartmentService;
import com.nutzfw.modules.organize.service.JobService;
import com.nutzfw.modules.organize.vo.DeptJobTreeVO;
import com.nutzfw.modules.sys.biz.JobBiz;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

import java.util.*;

/**
 * @author panchuang
 * @data 2018/6/2 0002
 */
@IocBean(name = "jobBiz")
public class JobBizImpl implements JobBiz {
    private static final Log log = Logs.get();

    @Inject("refer:$ioc")
    Ioc ioc;

    @Inject
    JobService jobService;

    @Inject
    DepartmentJobService departmentJobService;
    @Inject
    DepartmentService    departmentService;


    @Override
    public AjaxResult delJob(String id) {
        if (Strings.isBlank(id)) {
            return AjaxResult.error("id为空");
        }
        Job job = jobService.fetch(Cnd.where("id", "=", id).and("delFlag", "=", 0));
        if (job == null) {
            return AjaxResult.error("数据不存在");
        }
        job.setDelFlag(true);
        List<DepartmentJob> departmentJobList = departmentJobService.query(Cnd.where("jobId", "=", id).and("delFlag", "=", 0));
        for (DepartmentJob departmentJob : departmentJobList) {
            departmentJob.setDelFlag(true);
        }
        Trans.exec(() -> {
            jobService.update(job);
            departmentJobService.update(departmentJobList);
        });
        return AjaxResult.sucess("操作成功");

    }

    @Override
    public AjaxResult allocation(String jobId, String deptId, String deptName) {
        if (Strings.isBlank(jobId) || Strings.isBlank(jobId) || Strings.isBlank(deptName)) {
            return AjaxResult.error("查询异常");
        }
        String[] jobIds = jobId.split(",");
        String[] deptIds = deptId.split(",");
        List updateList = new ArrayList();
        List insertList = new ArrayList();
        DepartmentJob departmentJob;
        for (int i = jobIds.length - 1; i >= 0; i--) {
            Job job = jobService.fetch(Cnd.where("id", "=", jobIds[i]).and("delFlag", "=", 0));
            if (job == null) {
                return AjaxResult.error("岗位不存在");
            }
            String[] deptNames = (deptName + "," + job.getDeptsDesc()).split(",");
            HashSet set = new HashSet(Arrays.asList(deptNames));
            String str = StringUtils.join(set.toArray(), ",");
            job.setDeptsDesc(str);
            updateList.add(job);
            for (int j = deptIds.length - 1; j >= 0; j--) {
                departmentJob = departmentJobService.fetch(Cnd.where("jobId", "=", jobIds[i]).and("deptId", "=", deptIds[j]));
                if (departmentJob == null) {
                    /**/
                    DepartmentJob newDepartmentJob = new DepartmentJob();
                    newDepartmentJob.setJobId(jobIds[i]);
                    newDepartmentJob.setDeptId(deptIds[j]);
                    insertList.add(newDepartmentJob);
                }
            }

        }
        Trans.exec(new Atom() {
            @Override
            public void run() {
                jobService.update(updateList);
                departmentJobService.insert(insertList);
            }
        });
        return AjaxResult.sucessMsg("操作成功");
    }

    @Override
    public AjaxResult changeAllocation(String jobId, String deptId, String deptName) {
        if (Strings.isBlank(jobId) || Strings.isBlank(jobId) || Strings.isBlank(deptName)) {
            return AjaxResult.error("查询异常");
        }
        String[] jobIds = jobId.split(",");
        String[] deptIds = deptId.split(",");
        DepartmentJob departmentJob;
        List updateList = new ArrayList();
        List deleteList = new ArrayList();
        for (int i = jobIds.length - 1; i >= 0; i--) {
            Job job = jobService.fetch(Cnd.where("id", "=", jobIds[i]).and("delFlag", "=", 0));
            if (job == null) {
                return AjaxResult.error("岗位不存在");
            }
            String newDeptName = removeString(job.getDeptsDesc(), deptName);
            job.setDeptsDesc(newDeptName);
            updateList.add(job);
            for (int j = deptIds.length - 1; j >= 0; j--) {
                departmentJob = departmentJobService.fetch(Cnd.where("jobId", "=", jobIds[i]).and("deptId", "=", deptIds[j]));
                if (departmentJob != null) {
                    deleteList.add(departmentJob);
                }
            }

        }
        Trans.exec(new Atom() {
            @Override
            public void run() {
                jobService.update(updateList);
                if (deleteList.size() > 0) {
                    departmentJobService.delete(deleteList);
                }
            }
        });
        return AjaxResult.sucessMsg("操作成功");
    }

    /**
     * 移除相同元素
     */
    public String removeString(String sa, String sb) {
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(sa.split(",")));
        ArrayList<String> list1 = new ArrayList<String>(Arrays.asList(sb.split(",")));
        list.removeAll(list1);
        return StringUtils.join(list, ",");
    }

    /**
     * */
    @Override
    public AjaxResult saveOrUpdate(Job job) {
        if (job == null) {
            return AjaxResult.error("操作失败");
        }
        if (Strings.isBlank(job.getId())) {
            List<Job> jobs = jobService.query(Cnd.where("code", "=", job.getCode()).and("delFlag", "=", 0));
            if (jobs.size() > 0) {
                return AjaxResult.error("岗位编码已存在");
            }
        } else {
            Job jobOld = jobService.fetch(Cnd.where("id", "=", job.getId()).and("delFlag", "=", 0));
            if (!jobOld.getCode().equals(job.getCode())) {
                List<Job> jobs = jobService.query(Cnd.where("code", "=", job.getCode()).and("delFlag", "=", 0));
                if (jobs.size() > 0) {
                    return AjaxResult.error("岗位编码已存在");
                }
            }
        }
        jobService.insertOrUpdate(job);
        return AjaxResult.sucess("操作成功");
    }

    /**
     * 根据部门查询岗位
     *
     * @param deptId
     * @return
     */
    @Override
    public AjaxResult queryByDepartment(String deptId) {
        List<DepartmentJob> departmentJobs = departmentJobService.query(Cnd.where("deptId", "=", deptId));
        List<String> jobids = new ArrayList<>();
        departmentJobs.forEach((r) -> jobids.add(r.getJobId()));
        List<Job> jobs = jobService.query(Cnd.where("id", "in", jobids));
        return AjaxResult.sucess(jobs, "查询成功!");
    }

    /**
     * 岗位分布图
     */
    @Override
    public AjaxResult jobDistr() {
        List<DeptJobTreeVO> trees = new ArrayList<>();
        DeptJobTreeVO tree = createTree();
        trees.add(tree);
        return AjaxResult.sucess(trees, "成功");
    }

    /**
     * 创建岗位分布图
     *
     * @return
     */
    private DeptJobTreeVO createTree() {
        List<DeptJobTreeVO> deptJobTreeList = departmentService.treeAboutJob2();
        Map<String, DeptJobTreeVO> treeMap = new HashMap<>(1);
        Set<String> pids = new HashSet<>();
        deptJobTreeList.forEach(d -> {
            if ("dept".equals(d.getType())) {
                treeMap.put(d.getId(), d);
            }
            pids.add(d.getPid());
        });
        DeptJobTreeVO deptJobTree = new DeptJobTreeVO("0", "", Cons.optionsCach.getUnitName(), "dept", new ArrayList<>());
        treeMap.put("0", deptJobTree);
        while (deptJobTreeList.size() > 1) {
            Set<String> waitRemove = new HashSet<>();
            for (int i = deptJobTreeList.size() - 1; i >= 0; i--) {
                DeptJobTreeVO d = deptJobTreeList.get(i);

                if (!pids.contains(d.getId()) && (!"0".equals(d.getId()))) {
                    if ("job".equals(d.getType())) {
                        d.setName(d.getName().replace("(岗位)", ""));
                    }
                    treeMap.get(d.getPid()).getChildrens().add(d);
                    deptJobTreeList.remove(d);
                    waitRemove.add(d.getPid());
                }
            }
            pids.removeAll(waitRemove);
        }
        return treeMap.get("0");
    }

    /**
     * 根据部门查询岗位(包括-不限-)
     *
     * @param deptId
     * @return
     */
    @Override
    public AjaxResult queryByDepartmentAll(String deptId) {
        List<DepartmentJob> departmentJobs = departmentJobService.query(Cnd.where("deptId", "=", deptId));
        List<String> jobids = new ArrayList<>();
        departmentJobs.forEach((r) -> jobids.add(r.getJobId()));
        List<Job> jobs = new ArrayList<>();
        Job job = new Job();
        job.setId("0");
        job.setName("不限");
        jobs.add(job);
        jobs.addAll(jobService.query(Cnd.where("id", "in", jobids)));
        return AjaxResult.sucess(jobs, "查询成功!");
    }
}
