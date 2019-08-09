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
