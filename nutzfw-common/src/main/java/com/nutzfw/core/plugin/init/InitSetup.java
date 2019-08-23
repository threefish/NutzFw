package com.nutzfw.core.plugin.init;

import com.nutzfw.core.common.entity.BaseEntity;
import com.nutzfw.core.plugin.quartz.BaseJob;
import com.nutzfw.modules.sys.entity.QuartzJob;
import org.nutz.mvc.Setup;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/8/16
 * <p>
 * 执行顺序
 * <p>
 * setup.addAttachType(attachType);
 * setup.addDictGroup(dictGroup);
 * setup.addQuartzJob(quartzJobs);
 * setup.addTablesFilters(tablesFilters);
 * setup.init(nutConfig);
 */
public interface InitSetup extends Setup {

    /**
     * 添加附件类型
     * <p>
     * 例子：addAttachType("临时文件", "temp");
     *
     * @param attachType
     */
    void addAttachType(HashMap<String, String> attachType);

    /**
     * 添加数据字典分组
     * <p>
     * 例子：dictGroup.put("holiday_type", "假期类型")
     *
     * @param dictGroup
     */
    void addDictGroup(HashMap<String, String> dictGroup);

    /**
     * 添加定时任务,任务不存在会自动创建的，任务存在则忽略
     * <p>
     * 例子：quartzJobs.add(buildeQuartJob(ApmJob.class, "15 * * * * ?", "服务器状态监控服务", "服务器状态监控服务", false, null));
     *
     * @param quartzJobs
     */
    void addQuartzJob(List<QuartzJob> quartzJobs);

    /**
     * 添加不需要自动创建的表
     *
     * @param tablesFilters
     */
    void addTablesFilters(Set<Class<? extends BaseEntity>> tablesFilters);


    /**
     * 创建定时任务实体对象
     *
     * @param jobClass
     * @param corn
     * @param name
     * @param desc
     * @param autoStartRun
     * @param args
     * @return
     */
    default QuartzJob buildeQuartJob(Class<? extends BaseJob> jobClass, String corn, String name, String desc, boolean autoStartRun, String args) {
        return QuartzJob.builder().jobName(name).jobShort(0).jobCorn(corn).jobKlass(jobClass.getName()).jobLastStatus(true)
                //0随服务启动|1手动启动
                .jobType(autoStartRun ? 0 : 1).args(args == null ? "{}" : args).jobLastStatus(true).jobDesc(desc)
                .build();
    }
}
