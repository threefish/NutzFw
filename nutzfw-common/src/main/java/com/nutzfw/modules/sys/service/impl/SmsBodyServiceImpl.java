package com.nutzfw.modules.sys.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.sys.entity.SmsBody;
import com.nutzfw.modules.sys.service.SmsBodyService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年06月01日 19时35分54秒
 */
@IocBean(args = {"refer:dao"})
public class SmsBodyServiceImpl extends BaseServiceImpl<SmsBody> implements SmsBodyService {
    public SmsBodyServiceImpl(Dao dao) {
        super(dao);
    }
}
