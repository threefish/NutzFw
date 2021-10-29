package com.nutzfw.modules.sys.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.sys.entity.SysNotice;
import com.nutzfw.modules.sys.service.SysNoticeService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2021/10/29
 */
@IocBean(args = {"refer:dao"})
public class SysNoticeServiceImpl extends BaseServiceImpl<SysNotice> implements SysNoticeService {


    public SysNoticeServiceImpl(Dao dao) {
        super(dao);
    }


}
