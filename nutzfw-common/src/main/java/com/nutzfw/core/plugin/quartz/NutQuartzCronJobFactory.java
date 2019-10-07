/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.quartz;

import com.nutzfw.modules.sys.entity.QuartzJob;
import com.nutzfw.modules.sys.service.QuartzJobService;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.quartz.*;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2015/4/2114:20
 */
@IocBean(create = "init")
public class NutQuartzCronJobFactory {

    private static final Log log = Logs.get();

    @Inject
    protected PropertiesProxy conf;

    @Inject
    protected Scheduler scheduler;

    @Inject
    protected QuartzJobService quartzJobService;

    public void init() throws Exception {
        List<QuartzJob> jobList = quartzJobService.query();
        for (QuartzJob qjob : jobList) {
            /**
             * 只启动随服务启动的任务
             */
            if (qjob.getJobType() == 0) {
                String jobKlass = qjob.getJobKlass();
                String jobCron = qjob.getJobCorn();
                log.debugf("job define jobKlass=%s jobCron=%s", jobKlass, jobCron);
                final String msg = "代码中已经没有这个任务了！请检查！";
                Class<?> klass;
                try {
                    if (jobKlass.contains(".")) {
                        klass = Class.forName(jobKlass);
                    } else {
                        klass = Class.forName(getClass().getPackage().getName() + ".job." + jobKlass);
                    }
                    if (msg.equals(qjob.getJobDesc())) {
                        qjob.setJobDesc(qjob.getJobName());
                    }
                } catch (ClassNotFoundException e) {
                    //不启动数据中存在的任务，但是在代码中被删掉的任务
                    qjob.setJobDesc(msg);
                    qjob.setJobStatus("NONE");
                    quartzJobService.update(qjob);
                    continue;
                }
                JobDetail job = JobBuilder.newJob((Class<? extends Job>) klass).build();
                job.getJobDataMap().put(BaseJob.KEY_JOB_ENTITY, qjob);
                NutMap argsData = new NutMap();
                if (Json.fromJson(Strings.sNull(qjob.getArgs())) instanceof NutMap) {
                    argsData = Json.fromJson(NutMap.class, qjob.getArgs());
                }
                job.getJobDataMap().put(BaseJob.KEY_JOB_RUN_ARGS, argsData);
                CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobKlass)
                        .withSchedule(CronScheduleBuilder.cronSchedule(jobCron))
                        .build();
                scheduler.scheduleJob(job, trigger);
                /**
                 * 更新状态
                 */
                JobKey jobKey = trigger.getJobKey();
                qjob.setJobRunName(jobKey.getName());
                qjob.setJobGroup(jobKey.getGroup());
                Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
                qjob.setJobStatus(triggerState.name());
                quartzJobService.update(qjob);
            } else {
                qjob.setJobStatus("NONE");
                quartzJobService.update(qjob);
            }
        }
    }
}