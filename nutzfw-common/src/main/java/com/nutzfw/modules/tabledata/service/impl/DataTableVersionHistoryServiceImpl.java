package com.nutzfw.modules.tabledata.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.tabledata.entity.DataTableVersionHistory;
import com.nutzfw.modules.tabledata.service.DataTableVersionHistoryService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年06月11日 15时42分30秒
 */
@IocBean(args = {"refer:dao"})
public class DataTableVersionHistoryServiceImpl extends BaseServiceImpl<DataTableVersionHistory> implements DataTableVersionHistoryService {
    public DataTableVersionHistoryServiceImpl(Dao dao) {
        super(dao);
    }
}
