package com.nutzfw.modules.business.leave.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.business.leave.entity.Leave;
import com.nutzfw.modules.business.leave.service.LeaveService;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2019年04月12日 10时56分55秒
 */
@IocBean(args = {"refer:dao"}, name = "leaveService")
@Slf4j
public class LeaveServiceImpl extends BaseServiceImpl<Leave> implements LeaveService {

    public LeaveServiceImpl(Dao dao) {
        super(dao);
    }

}
