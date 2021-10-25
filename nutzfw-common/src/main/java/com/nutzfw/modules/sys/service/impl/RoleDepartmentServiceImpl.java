/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.core.common.util.StreamUtils;
import com.nutzfw.modules.sys.entity.RoleDepartment;
import com.nutzfw.modules.sys.service.RoleDepartmentService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年06月04日 20时12分39秒
 */
@IocBean(args = {"refer:dao"})
public class RoleDepartmentServiceImpl extends BaseServiceImpl<RoleDepartment> implements RoleDepartmentService {

    public RoleDepartmentServiceImpl(Dao dao) {
        super(dao);
    }

    @Override
    public List<RoleDepartment> queryManagerDepartment(Set<String> roleids) {
        return query(Cnd.NEW().andEX("role_id", "in", roleids)).stream().filter(StreamUtils.distinctByKey(RoleDepartment::getDeptId)).collect(Collectors.toList());
    }
}
