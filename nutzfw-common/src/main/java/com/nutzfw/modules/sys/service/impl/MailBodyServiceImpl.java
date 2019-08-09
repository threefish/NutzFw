package com.nutzfw.modules.sys.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.sys.entity.MailBody;
import com.nutzfw.modules.sys.service.MailBodyService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/29
 * 描述此类：邮件
 */
@IocBean(args = {"refer:dao"})
public class MailBodyServiceImpl extends BaseServiceImpl<MailBody> implements MailBodyService {
    public MailBodyServiceImpl(Dao dao) {
        super(dao);
    }
}
