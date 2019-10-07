/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:30:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.quartz.job;

import com.nutzfw.core.plugin.quartz.BaseJob;
import com.nutzfw.modules.organize.entity.UserImportHistory;
import com.nutzfw.modules.organize.service.UserImportHistoryService;
import com.nutzfw.modules.organize.thread.CheckUserDataThread;
import org.nutz.dao.Cnd;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;

import java.util.List;

/**
 * @author 叶世游
 * @date 2018/6/22 20:54
 * @description 用户导入任务
 */
@IocBean(args = {"refer:$ioc"})
@DisallowConcurrentExecution
public class UserImportJob extends BaseJob {
    @Inject
    UserImportHistoryService userImportHistoryService;

    public UserImportJob(Ioc ioc) {
        super(ioc);
    }

    /**
     * 任务运行逻辑处理
     *
     * @param data
     * @throws Exception
     */
    @Override
    public void run(JobDataMap data) throws Exception {
        List<UserImportHistory> histories = userImportHistoryService.query(Cnd.where("staus", "=", 0));
        if (histories.size() > 0) {
            histories.forEach(h -> {
                CheckUserDataThread checkDataThread = new CheckUserDataThread(ioc, h);
                Thread thread = new Thread(checkDataThread);
                thread.start();
                h.setStaus(1);
                userImportHistoryService.update(h);
            });
        }
    }
}
