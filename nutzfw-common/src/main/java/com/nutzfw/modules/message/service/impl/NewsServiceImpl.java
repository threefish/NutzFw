/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.message.service.impl;

import com.github.threefish.nutz.dto.PageDataDTO;
import com.github.threefish.nutz.sqltpl.ISqlDaoExecuteService;
import com.github.threefish.nutz.sqltpl.SqlsTplHolder;
import com.github.threefish.nutz.sqltpl.SqlsXml;
import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.message.entity.News;
import com.nutzfw.modules.message.service.NewsService;
import com.nutzfw.modules.organize.entity.UserAccount;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

import java.util.List;
import java.util.Set;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2019年07月30日
 * 新闻
 */
@IocBean(args = {"refer:dao"}, name = "newsService")
@SqlsXml
public class NewsServiceImpl extends BaseServiceImpl<News> implements NewsService, ISqlDaoExecuteService {

    SqlsTplHolder sqlsTplHolder;

    public NewsServiceImpl(Dao dao) {
        super(dao);
    }

    @Override
    public List<News> queryIndexNewsList(UserAccount sessionUserAccount, Set<String> sessionRoleIds) {
        return queryEntityBySql("queryIndexNewsList", NutMap.NEW(),
                Cnd.where(Cnd.exps("r.roleId", "in", sessionRoleIds).or("d.deptId", "=", sessionUserAccount.getDeptId())),
                new Pager(1, 10)
        ).getData();
    }

    @Override
    public List<News> queryIndexImgNewsList(UserAccount sessionUserAccount, Set<String> sessionRoleIds) {
        return queryEntityBySql("queryIndexNewsList", NutMap.NEW(),
                Cnd.where("n.isrecomm", "=", true)
                        .and(Cnd.exps("r.roleId", "in", sessionRoleIds).or("d.deptId", "=", sessionUserAccount.getDeptId())),
                new Pager(1, 10)
        ).getData();
    }

    @Override
    public LayuiTableDataListVO queryLookMoreList(UserAccount sessionUserAccount, Set<String> sessionRoleIds, int pageNum, int pageSize, String key) {
        PageDataDTO pageDataDTO = queryEntityBySql("queryIndexNewsList",
                NutMap.NEW(),
                Cnd.where("n.title", "like", "%" + key + "%")
                        .and(Cnd.exps("r.roleId", "in", sessionRoleIds).or("d.deptId", "=", sessionUserAccount.getDeptId())),
                new Pager(pageNum, pageSize)
        );
        return LayuiTableDataListVO.pageByData(pageDataDTO.getData(), pageDataDTO.getCount());
    }

    @Override
    public SqlsTplHolder getSqlsTplHolder() {
        return sqlsTplHolder;
    }

    @Override
    public Dao getDao() {
        return this.dao;
    }
}
