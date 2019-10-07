/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:30:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.quartz.job;

import com.alibaba.druid.filter.config.ConfigTools;
import com.nutzfw.core.common.util.DateUtil;
import com.nutzfw.core.common.util.MysqlBackUpUtil;
import com.nutzfw.core.plugin.quartz.BaseJob;
import com.nutzfw.modules.sys.action.DatabaseBackupAction;
import com.nutzfw.modules.sys.entity.DatabaseBackup;
import com.nutzfw.modules.sys.service.DatabaseBackupService;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/1/17
 * 自动备份数据库
 */
@IocBean(args = {"refer:$ioc"})
@DisallowConcurrentExecution
public class DatabaseBackupJob extends BaseJob {

    @Inject
    DatabaseBackupService databaseBackupService;

    @Inject("java:$conf.get('attach.savePath')")
    private String parentPath;

    public DatabaseBackupJob(Ioc ioc) {
        super(ioc);
    }

    @Override
    public void run(JobDataMap data) throws Exception {
        String name = DateUtil.date2string(new Date(), DateUtil.YYYYMMDD);
        String path = parentPath + File.separator + "DATABASE_BACKUP" + File.separator + name;
        MysqlBackUpUtil.checkMysqlDumpExe();
        List<DatabaseBackup> list = databaseBackupService.query();
        for (DatabaseBackup databaseBackup : list) {
            String passWord = ConfigTools.decrypt(DatabaseBackupAction.PUBLIC_KEY, databaseBackup.getUserPass());
            MysqlBackUpUtil.backMysql(Strings.splitIgnoreBlank(databaseBackup.getDbNames()),
                    databaseBackup.getIp(),
                    databaseBackup.getPort(),
                    databaseBackup.getUserName(),
                    passWord,
                    path
            );
        }
    }

}
