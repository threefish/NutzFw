package com.nutzfw.modules.tabledata.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.tabledata.entity.DataImportHistory;
import com.nutzfw.modules.tabledata.service.DataImportHistoryService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年06月19日 12时11分39秒
 */
@IocBean(name = "dataImportHistoryService", args = {"refer:dao"})
public class DataImportHistoryServiceImpl extends BaseServiceImpl<DataImportHistory> implements DataImportHistoryService {
    public DataImportHistoryServiceImpl(Dao dao) {
        super(dao);
    }
}
