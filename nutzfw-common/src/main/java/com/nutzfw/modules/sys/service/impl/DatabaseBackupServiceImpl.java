package com.nutzfw.modules.sys.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.sys.entity.DatabaseBackup;
import com.nutzfw.modules.sys.service.DatabaseBackupService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2019年01月17日 19时30分41秒
 */
@IocBean(args = {"refer:dao"}, name = "databaseBackupService")
public class DatabaseBackupServiceImpl extends BaseServiceImpl<DatabaseBackup> implements DatabaseBackupService {
    public DatabaseBackupServiceImpl(Dao dao) {
        super(dao);
    }
}
