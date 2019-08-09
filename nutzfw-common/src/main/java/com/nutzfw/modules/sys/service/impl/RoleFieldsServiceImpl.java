package com.nutzfw.modules.sys.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.sys.entity.RoleField;
import com.nutzfw.modules.sys.service.RoleFieldsService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2019年03月15日 12时06分06秒
 */
@IocBean(args = {"refer:dao"}, name = "roleFieldsService")
public class RoleFieldsServiceImpl extends BaseServiceImpl<RoleField> implements RoleFieldsService {
    public RoleFieldsServiceImpl(Dao dao) {
        super(dao);
    }
}
