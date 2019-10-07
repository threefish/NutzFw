/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.core.plugin.quartz.BaseJob;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.sys.entity.QuartzJob;
import com.nutzfw.modules.sys.service.JobRunHistoryService;
import com.nutzfw.modules.sys.service.QuartzJobService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;
import org.quartz.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: 黄川
 * Date Time: 2015/9/1513:37
 */
@IocBean
@At("/monitor/jobs")
@Ok("json:{ignoreNull:false}")
@Fail("http:500")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class QuartzJobAction extends BaseAction {
    /**
     * 默认任务运行组
     */
    private static final String JOB_GROUP = "DEFAULT";

    @Inject
    private QuartzJobService quartzJobService;

    @Inject
    private JobRunHistoryService jobRunHistoryService;

    @Inject
    private Scheduler scheduler;

    @At
    public int count() {
        return quartzJobService.count();
    }


    @GET
    @At(value = {"/", "/index"})
    @Ok("btl:WEB-INF/view/sys/monitor/quartz/manager.html")
    @RequiresPermissions("sysMonitor.quartz")
    @AutoCreateMenuAuth(name = "定时任务管理", icon = "fa-tasks", parentPermission = "sys.monitor")
    public void index() {
    }

    @GET
    @At(value = "/jobRunHistory ")
    @Ok("btl:WEB-INF/view/sys/monitor/quartz/jobRunHistory.html")
    @RequiresPermissions("sysMonitor.jobRunHistory")
    @AutoCreateMenuAuth(name = "错误记录", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-tasks", parentPermission = "sysMonitor.quartz")
    public void jobRunHistory(@Param("id") String id) {
        QuartzJob job = quartzJobService.fetchByUUID(id);
        setRequestAttribute("job", job);
    }

    @POST
    @At("/changeJobType")
    @Ok("json")
    @RequiresPermissions("sysMonitor.changeJobType")
    @AutoCreateMenuAuth(name = "修改任务启动类型", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-tasks", parentPermission = "sysMonitor.quartz")
    public AjaxResult changeJobType(@Param("id") String id, @Param("jobType") int jobType) {
        QuartzJob job = quartzJobService.fetchByUUID(id);
        job.setJobType(jobType);
        quartzJobService.update(job);
        return AjaxResult.sucess("操作成功！");
    }


    /**
     * 修改任务排序号
     */
    @At("/updateShortNo")
    @Ok("json")
    @POST
    @RequiresPermissions("sysMonitor.updateShortNo")
    @AutoCreateMenuAuth(name = "修改任务排序号", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysMonitor.index")
    public AjaxResult updateShortNo(@Param("id") String id, @Param("shortNo") int shortNo) {
        try {
            QuartzJob qjob = quartzJobService.fetchByUUID(id);
            qjob.setJobShort(shortNo);
            quartzJobService.update(qjob);
            return new AjaxResult(true, "修改成功");
        } catch (Exception e) {
            log.error(e);
            return new AjaxResult(false, "修改失败", e.getMessage());
        }
    }

    @GET
    @POST
    @At("/jobRunHistoryListData")
    @Ok("json")
    @RequiresPermissions("sysMonitor.jobRunHistory")
    public LayuiTableDataListVO jobRunHistoryListData(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize, @Param("jobId") String jobId) {
        return jobRunHistoryService.listPage(pageNum, pageSize, Cnd.where("jobId", "=", jobId).desc("opAt"));
    }

    @GET
    @POST
    @At("/query")
    @Ok("json:{nullAsEmtry:true}")
    @RequiresPermissions("sysMonitor.quartz")
    public LayuiTableDataListVO query(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize) {
        LayuiTableDataListVO tableDataListVO = quartzJobService.listPage(pageNum, pageSize, Cnd.orderBy().desc("job_short"));
        List<QuartzJob> quartzJobs = tableDataListVO.getData();
        List<QuartzJob> quartzJobList = new ArrayList<>();
        for (QuartzJob quartzJob : quartzJobs) {
            try {
                TriggerKey triggerKey = TriggerKey.triggerKey(quartzJob.getJobKlass(), JOB_GROUP);
                Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
                quartzJob.setJobStatus(triggerState.name());
                if (!"NONE".equals(triggerState.name())) {
                    JobKey job = JobKey.jobKey(quartzJob.getJobRunName(), JOB_GROUP);
                    QuartzJob runQuartzJob = (QuartzJob) scheduler.getJobDetail(job).getJobDataMap().get(BaseJob.KEY_JOB_ENTITY);
                    quartzJob.setJobLastStatus(runQuartzJob.isJobLastStatus());
                    quartzJob.setLastConsuming(runQuartzJob.getLastConsuming());
                }
            } catch (Exception e) {
                log.error(e);
            }
            String[] jobKlass = quartzJob.getJobKlass().split("\\.");
            quartzJob.setJobKlass(jobKlass[jobKlass.length - 1]);
            quartzJobList.add(quartzJob);
        }
        tableDataListVO.setData(quartzJobList);
        return tableDataListVO;
    }

    /**
     * 暂停任务
     */
    @At("/pauseJob")
    @Ok("json")
    @POST
    @RequiresPermissions("sysMonitor.pauseJob")
    @AutoCreateMenuAuth(name = "暂停任务", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysMonitor.quartz")
    public AjaxResult pauseJob(@Param("id") String id) {
        try {
            QuartzJob qjob = quartzJobService.fetchByUUID(id);
            TriggerKey triggerKey = TriggerKey.triggerKey(qjob.getJobKlass(), JOB_GROUP);
            scheduler.pauseTrigger(triggerKey);
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            qjob.setJobStatus(triggerState.name());
            quartzJobService.update(qjob);
            return new AjaxResult(true, "暂停成功");
        } catch (SchedulerException e) {
            log.error(e);
            return new AjaxResult(false, "暂停失败", e.getMessage());
        }
    }

    /**
     * 修改运行参数
     */
    @At("/updateArgs")
    @Ok("json")
    @POST
    @RequiresPermissions("sysMonitor.updateArgs")
    @AutoCreateMenuAuth(name = "修改运行参数", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysMonitor.quartz")
    public AjaxResult updateArgs(@Param("id") String id, @Param("args") String args) {
        try {
            QuartzJob qjob = quartzJobService.fetchByUUID(id);
            if (Strings.isNotBlank(args) && Json.fromJson(args) instanceof NutMap) {
                qjob.setArgs(args);
                quartzJobService.update(qjob);
                return new AjaxResult(true, "修改成功，请停止任务，重启任务生效!");
            } else {
                return new AjaxResult(false, "参数错误或参数为空");
            }
        } catch (Exception e) {
            log.error(e);
            return new AjaxResult(false, "修改失败", e.getMessage());
        }
    }


    /**
     * 修改运行表达式
     */
    @At("/updatejobCorn")
    @Ok("json")
    @POST
    @RequiresPermissions("sysMonitor.updatejobCorn")
    @AutoCreateMenuAuth(name = "修改运行表达式", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysMonitor.quartz")
    public AjaxResult updatejobCorn(@Param("id") String id, @Param("jobCorn") String jobCorn) {
        try {
            QuartzJob qjob = quartzJobService.fetchByUUID(id);
            if (Strings.isNotBlank(jobCorn)) {
                qjob.setJobCorn(jobCorn);
                quartzJobService.update(qjob);
                return new AjaxResult(true, "修改成功，请停止任务，重启任务生效!");
            } else {
                return new AjaxResult(false, "参数错误或参数为空");
            }
        } catch (Exception e) {
            log.error(e);
            return new AjaxResult(false, "修改失败", e.getMessage());
        }
    }

    /**
     * 恢复任务
     */
    @At("/resumJob")
    @Ok("json")
    @POST
    @RequiresPermissions("sysMonitor.resumJob")
    @AutoCreateMenuAuth(name = "恢复任务", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysMonitor.quartz")
    public AjaxResult resumJob(@Param("id") String id) {
        try {
            QuartzJob qjob = quartzJobService.fetchByUUID(id);
            TriggerKey triggerKey = TriggerKey.triggerKey(qjob.getJobKlass(), JOB_GROUP);
            scheduler.resumeTrigger(triggerKey);
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            qjob.setJobStatus(triggerState.name());
            quartzJobService.update(qjob);
            return new AjaxResult(true, "恢复成功");
        } catch (SchedulerException e) {
            log.error(e);
            return new AjaxResult(false, "恢复失败", e.getMessage());
        }
    }

    /**
     * 立即执行一次任务
     */
    @At("/atOnceJob")
    @Ok("json")
    @POST
    @RequiresPermissions("sysMonitor.atOnceJob")
    @AutoCreateMenuAuth(name = "立即执行一次任务", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysMonitor.quartz")
    public AjaxResult atOnceJob(@Param("id") String id) {
        try {
            QuartzJob qjob = quartzJobService.fetchByUUID(id);
            List<JobExecutionContext> list = scheduler.getCurrentlyExecutingJobs();
            for (JobExecutionContext context : list) {
                if (context.getJobDetail().getKey().toString().equals(JOB_GROUP + "." + qjob.getJobRunName())) {
                    return new AjaxResult(false, "当前任务正在执行中！请耐心等待任务完成！");
                }
            }
            JobKey job = JobKey.jobKey(qjob.getJobRunName());
            scheduler.triggerJob(job);
            scheduler.getJobDetail(job).getJobDataMap().put(BaseJob.KEY_JOB_ENTITY, qjob);
            TriggerKey triggerKey = TriggerKey.triggerKey(qjob.getJobKlass(), JOB_GROUP);
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            qjob.setJobStatus(triggerState.name());
            quartzJobService.update(qjob);
            return new AjaxResult(true, "任务开始执行");
        } catch (SchedulerException e) {
            log.error(e);
            return new AjaxResult(false, "执行失败", e.getMessage());
        }
    }


    /**
     * 启动任务
     */
    @At("/startJob")
    @Ok("json")
    @POST
    @RequiresPermissions("sysMonitor.startJob")
    @AutoCreateMenuAuth(name = "启动任务", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysMonitor.quartz")
    public AjaxResult startJob(@Param("id") String id) {
        try {
            QuartzJob qjob = quartzJobService.fetchByUUID(id);
            TriggerKey triggerKey = TriggerKey.triggerKey(qjob.getJobKlass(), JOB_GROUP);
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            if (!"NORMAL".equals(triggerState.name())) {
                Class<?> klass = Class.forName(qjob.getJobKlass());
                JobDetail job = JobBuilder.newJob((Class<? extends Job>) klass).build();
                job.getJobDataMap().put(BaseJob.KEY_JOB_ENTITY, qjob);
                NutMap argsData = new NutMap();
                if (Json.fromJson(Strings.sNull(qjob.getArgs())) instanceof NutMap) {
                    argsData = Json.fromJson(NutMap.class, qjob.getArgs());
                }
                job.getJobDataMap().put(BaseJob.KEY_JOB_RUN_ARGS, argsData);
                CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(qjob.getJobKlass())
                        .withSchedule(CronScheduleBuilder.cronSchedule(qjob.getJobCorn()))
                        .build();
                scheduler.scheduleJob(job, trigger);
                triggerState = scheduler.getTriggerState(triggerKey);
                JobKey jobKey = trigger.getJobKey();
                qjob.setJobRunName(jobKey.getName());
            }
            qjob.setJobGroup(JOB_GROUP);
            qjob.setJobStatus(triggerState.name());
            quartzJobService.update(qjob);
            return new AjaxResult(true, "启动成功");
        } catch (Exception e) {
            log.error(e);
            return new AjaxResult(false, "启动失败", e.getMessage());
        }
    }


    /**
     * 停止/删除 任务
     */
    @At("/stopJob")
    @Ok("json")
    @RequiresPermissions("sysMonitor.stopJob")
    @AutoCreateMenuAuth(name = "停止/删除 任务", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysMonitor.quartz")
    public AjaxResult delJob(@Param("id") String id) {
        try {
            QuartzJob qjob = quartzJobService.fetchByUUID(id);
            TriggerKey triggerKey = TriggerKey.triggerKey(qjob.getJobKlass(), JOB_GROUP);
            JobKey job = JobKey.jobKey(qjob.getJobKlass(), JOB_GROUP);
            // 暂停触发器
            scheduler.pauseTrigger(triggerKey);
            //删除触发器
            scheduler.unscheduleJob(triggerKey);
            // 删除任务
            scheduler.deleteJob(job);
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            qjob.setJobStatus(triggerState.name());
            quartzJobService.update(qjob);
            return new AjaxResult(true, "停止成功");
        } catch (SchedulerException e) {
            log.error(e);
            return new AjaxResult(true, "停止失败", e.getMessage());
        }
    }


    /**
     * 添加任务
     */
    @At("/addJob")
    @Ok("json")
    @RequiresPermissions("sysMonitor.addJob")
    @AutoCreateMenuAuth(name = "添加任务", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysMonitor.quartz")
    public AjaxResult addJob(@Param("id") String id) {
        try {
            QuartzJob qjob = quartzJobService.fetchByUUID(id);
            Class<?> klass = Class.forName(qjob.getJobKlass());
            JobDetail job = JobBuilder.newJob((Class<? extends Job>) klass).build();
            job.getJobDataMap().put(BaseJob.KEY_JOB_ENTITY, qjob);
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(qjob.getJobKlass())
                    .withSchedule(CronScheduleBuilder.cronSchedule(qjob.getJobCorn()))
                    .build();
            scheduler.scheduleJob(job, trigger);
            Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
            qjob.setJobStatus(triggerState.name());
            quartzJobService.update(qjob);
            return new AjaxResult(true, "添加成功");
        } catch (Exception e) {
            log.error(e);
            return new AjaxResult(true, "添加失败", e.getMessage());
        }
    }

}
