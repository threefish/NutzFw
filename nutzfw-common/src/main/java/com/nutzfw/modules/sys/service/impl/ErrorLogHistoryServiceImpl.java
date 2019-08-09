package com.nutzfw.modules.sys.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.sys.entity.ErrorLogHistory;
import com.nutzfw.modules.sys.service.ErrorLogHistoryService;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年06月12日 17时18分27秒
 */
@IocBean(name = "errorLogHistoryService", args = {"refer:dao"})
public class ErrorLogHistoryServiceImpl extends BaseServiceImpl<ErrorLogHistory> implements ErrorLogHistoryService {
    public ErrorLogHistoryServiceImpl(Dao dao) {
        super(dao);
    }

    @Async
    @Override
    public void async(ErrorLogHistory history) {
        sync(history);
    }

    @Override
    public void sync(ErrorLogHistory history) {
        dao.insert(history);
    }
}
