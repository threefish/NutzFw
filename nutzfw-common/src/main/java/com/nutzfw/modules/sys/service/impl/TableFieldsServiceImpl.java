/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.service.impl;

import com.github.threefish.nutz.sqltpl.ISqlDaoExecuteService;
import com.github.threefish.nutz.sqltpl.SqlsTplHolder;
import com.github.threefish.nutz.sqltpl.SqlsXml;
import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.sys.entity.TableFields;
import com.nutzfw.modules.sys.service.TableFieldsService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年06月05日 20时33分41秒
 */
@IocBean(name = "tableFieldsService", args = {"refer:dao"})
@SqlsXml("TableFieldsServiceImpl.xml")
public class TableFieldsServiceImpl extends BaseServiceImpl<TableFields> implements TableFieldsService, ISqlDaoExecuteService {

    private SqlsTplHolder sqlsTplHolder;

    public TableFieldsServiceImpl(Dao dao) {
        super(dao);
    }

    @Override
    public SqlsTplHolder getSqlsTplHolder() {
        return sqlsTplHolder;
    }

    @Override
    public Dao getDao() {
        return this.dao;
    }

    @Override
    public List<TableFields> fetchAuthFields(String fetchAuthFields, NutMap setv) {
        return queryEntityBySql(fetchAuthFields, setv);
    }
}
