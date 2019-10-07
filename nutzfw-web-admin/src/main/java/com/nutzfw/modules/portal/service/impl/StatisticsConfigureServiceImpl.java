/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.portal.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.core.common.util.StringUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.modules.portal.entity.StatisticsConfigure;
import com.nutzfw.modules.portal.service.StatisticsConfigureService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

import java.util.List;


/**
 * @author 叶世游
 * @date 2018年06月19日 09时59分20秒
 */
@IocBean(args = {"refer:dao"})
public class StatisticsConfigureServiceImpl extends BaseServiceImpl<StatisticsConfigure> implements StatisticsConfigureService {
    public StatisticsConfigureServiceImpl(Dao dao) {
        super(dao);
    }

    /**
     * 保存统计
     *
     * @param statisticsConfigure
     * @return
     */
    @Override
    public AjaxResult save(StatisticsConfigure statisticsConfigure) {
        if (StringUtil.isBlank(statisticsConfigure.getId())) {
            StatisticsConfigure old = fetch(Cnd.where("name", EQ, statisticsConfigure.getName()));
            if (old != null) {
                return AjaxResult.error("统计名称重复!");
            }
            insert(statisticsConfigure);
            return AjaxResult.sucess("添加成功!");
        } else {
            StatisticsConfigure old = fetch(Cnd.where("name", EQ, statisticsConfigure.getName()).and("id", NEQ, statisticsConfigure.getId()));
            if (old != null) {
                return AjaxResult.error("统计名称重复!");
            }
            update(statisticsConfigure);
            return AjaxResult.sucess("修改成功!");
        }
    }

    /**
     * 批量删除统计
     *
     * @param ids
     * @return
     */
    @Override
    public int del(String[] ids) {
        return vDelete(ids, true);
    }

    /**
     * 预览图像
     *
     * @param sqlstr
     * @return
     */
    @Override
    public AjaxResult showNow(String sqlstr) {
        Sql sql = Sqls.create(sqlstr);
        sql.setCallback(Sqls.callback.maps());
        execute(sql);
        List<NutMap> maps = sql.getList(NutMap.class);
        return AjaxResult.sucess(maps, "获取成功!");
    }
}
