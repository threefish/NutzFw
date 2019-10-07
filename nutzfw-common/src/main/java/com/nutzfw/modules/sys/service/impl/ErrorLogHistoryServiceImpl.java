/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
