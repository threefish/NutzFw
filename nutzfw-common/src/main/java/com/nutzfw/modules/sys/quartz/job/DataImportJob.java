/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:30:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.quartz.job;

import com.nutzfw.core.plugin.quartz.BaseJob;
import com.nutzfw.modules.tabledata.entity.DataImportHistory;
import com.nutzfw.modules.tabledata.service.DataImportHistoryService;
import com.nutzfw.modules.tabledata.thread.CheckDataThread;
import org.nutz.dao.Cnd;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/22
 * 描述此类：数据导入任务
 */
@IocBean(args = {"refer:$ioc"})
@DisallowConcurrentExecution
public class DataImportJob extends BaseJob {

    @Inject
    DataImportHistoryService dataImportHistoryService;

    public DataImportJob(Ioc ioc) {
        super(ioc);
    }

    @Override
    public void run(JobDataMap data) {
        if (canStartRunCheckOrImport()) {
            DataImportHistory dataImportHistory = getDataImportHistory();
            if (dataImportHistory != null) {
                CheckDataThread checkDataThread = new CheckDataThread(ioc, dataImportHistory);
                Thread thread = new Thread(checkDataThread);
                thread.start();
            }
        }
    }


    /**
     * 取得一个待检查数据
     */
    private DataImportHistory getDataImportHistory() {
        return dataImportHistoryService.fetch(Cnd.where("staus", "=", 0));
    }


    /**
     * 检查是否能够可以开始检查或导入
     */
    private synchronized boolean canStartRunCheckOrImport() {
        //检查是否有检查中或导入中的数据
        return dataImportHistoryService.count(
                Cnd.where("staus", "=", 1)
                        .or("staus", "=", 3)
        ) == 0;
    }
}
