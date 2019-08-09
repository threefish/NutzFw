package com.nutzfw.modules.sys.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.sys.entity.Options;
import com.nutzfw.modules.sys.service.OptionsService;
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
@IocBean(args = {"refer:dao"})
public class OptionsServiceImpl extends BaseServiceImpl<Options> implements OptionsService {
    public OptionsServiceImpl(Dao dao) {
        super(dao);
    }
}
