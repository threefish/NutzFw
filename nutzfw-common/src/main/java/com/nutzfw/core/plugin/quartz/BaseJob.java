/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.quartz;

import com.nutzfw.core.common.util.DateUtil;
import com.nutzfw.core.common.util.StringUtil;
import com.nutzfw.modules.sys.entity.JobRunHistory;
import com.nutzfw.modules.sys.entity.QuartzJob;
import com.nutzfw.modules.sys.service.JobRunHistoryService;
import com.nutzfw.modules.sys.service.QuartzJobService;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.quartz.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2017/12/15  14:54
 * 描述此类：
 * @PersistJobDataAfterExecution 保存在JobDataMap传递的参数
 * @DisallowConcurrentExecution 保证多个任务间不会同时执行.所以在多任务执行时最好加上
 */
@PersistJobDataAfterExecution
public abstract class BaseJob implements Job {
    /**
     * 任务缓存
     */
    public static final String KEY_JOB_ENTITY   = "KEY_JOB_ENTITY";
    /**
     * 任务运行参数
     * 修改后需要先停止再启动，才能生效
     */
    public static final String KEY_JOB_RUN_ARGS = "KEY_JOB_RUN_ARGS";

    protected static final Log log = Logs.get();

    protected Ioc ioc;

    private JobRunHistoryService jobRunHistoryService;

    private QuartzJobService quartzJobService;

    public BaseJob(Ioc ioc) {
        this.ioc = ioc;
        this.quartzJobService = ioc.get(QuartzJobService.class, "quartzJobService");
        this.jobRunHistoryService = ioc.get(JobRunHistoryService.class, "jobRunHistoryService");
    }

    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> when a <code>{@link Trigger}</code>
     * fires that is associated with the <code>Job</code>.
     * </p>
     * <p>
     * <p>
     * The implementation may wish to set a
     * {@link JobExecutionContext#setResult(Object) result} object on the
     * {@link JobExecutionContext} before this method exits.  The result itself
     * is meaningless to Quartz, but may be informative to
     * <code>{@link JobListener}s</code> or
     * <code>{@link TriggerListener}s</code> that are watching the job's
     * execution.
     * </p>
     *
     * @param context
     */
    @Override
    final public void execute(JobExecutionContext context) {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        QuartzJob quartzJob = (QuartzJob) data.get(KEY_JOB_ENTITY);
        if (null != quartzJob) {
            //上次状态
            boolean lastStatus = quartzJob.isJobLastStatus();
            long startTime = System.currentTimeMillis();
            try {
                quartzJob.setJobStatus("RUNING");
                run(data);
                quartzJob.setJobStatus("NORMAL");
                quartzJob.setJobLastStatus(true);
                //考虑了下不记录成功日志
            } catch (Exception e) {
                log.error(e);
                JobRunHistory history = new JobRunHistory();
                history.setJobId(quartzJob.getUuid());
                quartzJob.setJobStatus("NORMAL");
                quartzJob.setJobLastStatus(false);
                history.setStatus(false);
                history.setErrorLog(Strings.escapeHtml(StringUtil.throwableToString(e)));
                long endTime = System.currentTimeMillis();
                history.setConsuming(DateUtil.getDistanceTime(startTime, endTime, "{H}小时{M}分{S}秒{MS}毫秒"));
                jobRunHistoryService.insert(history);
            } finally {
                long endTime = System.currentTimeMillis();
                quartzJob.setLastConsuming(DateUtil.getDistanceTime(startTime, endTime, "{H}小时{M}分{S}秒{MS}毫秒"));
                if (lastStatus != quartzJob.isJobLastStatus()) {
                    //当前状态和上次状态不一样
                    quartzJobService.update(quartzJob);
                }
            }
        } else {
            try {
                //没有将任务对象加载进任务缓存
                run(data);
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    /**
     * 任务运行逻辑处理
     *
     * @param data
     * @throws Exception
     */
    public abstract void run(JobDataMap data) throws Exception;
}
