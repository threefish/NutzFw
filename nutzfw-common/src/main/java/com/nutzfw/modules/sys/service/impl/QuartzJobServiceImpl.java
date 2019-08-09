package com.nutzfw.modules.sys.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.sys.entity.QuartzJob;
import com.nutzfw.modules.sys.service.QuartzJobService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/3/12
 * 描述此类：
 */
@IocBean(name = "quartzJobService", args = {"refer:dao"})
public class QuartzJobServiceImpl extends BaseServiceImpl<QuartzJob> implements QuartzJobService {
    public QuartzJobServiceImpl(Dao dao) {
        super(dao);
    }
}
