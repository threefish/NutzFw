package com.nutzfw.modules.sys.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.sys.entity.JobRunHistory;
import com.nutzfw.modules.sys.service.JobRunHistoryService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * Created with IntelliJ IDEA.
 *
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2018/1/9  17:09
 * 描述此类：
 */
@IocBean(name = "jobRunHistoryService", args = {"refer:dao"})
public class JobRunHistoryImpl extends BaseServiceImpl<JobRunHistory> implements JobRunHistoryService {
    public JobRunHistoryImpl(Dao dao) {
        super(dao);
    }
}
