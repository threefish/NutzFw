/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.quartz;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.simpl.SimpleJobFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

@IocBean(args = "refer:$ioc")
public class NutQuartzJobFactory implements JobFactory {

    private static final Log log = Logs.get();

    protected SimpleJobFactory simple = new SimpleJobFactory();

    protected Ioc ioc;

    public NutQuartzJobFactory(Ioc ioc) {
        this.ioc = ioc;
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        try {
            return ioc.get(bundle.getJobDetail().getJobClass());
        } catch (Exception e) {
            log.warn("Not ioc bean? fallback to SimpleJobFactory", e);
            return simple.newJob(bundle, scheduler);
        }
    }

}