/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.organize.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.organize.entity.Job;
import com.nutzfw.modules.organize.service.JobService;
import com.nutzfw.modules.organize.service.UserAccountJobService;
import com.nutzfw.modules.organize.service.UserAccountService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年06月01日 19时36分19秒
 */
@IocBean(args = {"refer:dao"})
public class JobServiceImpl extends BaseServiceImpl<Job> implements JobService {
    @Inject
    UserAccountService    userAccountService;
    @Inject
    UserAccountJobService userAccountJobService;

    public JobServiceImpl(Dao dao) {
        super(dao);
    }

    /**
     * 获取所有的岗位,并拼接
     *
     * @return
     */
    @Override
    public String[] allJobStrs() {
        List<Job> jobs = query(Cnd.where("delFlag", EQ, 0));
        String[] jobstr = new String[jobs.size()];
        for (int i = 0; i < jobs.size(); i++) {
            jobstr[i] = jobs.get(i).getName() + "→" + jobs.get(i).getId();
        }
        return jobstr;
    }

    /**
     * 岗位人数统计
     *
     * @param customizedParams
     * @return
     */
    @Override
    public List<NutMap> statisticsJobUser(String customizedParams) {
        String slqstr = "SELECT j.`name` AS `name`,COUNT(ua.id) AS value,j.id FROM sys_user_account ua,sys_user_account_job uj,sys_job j " +
                "WHERE ua.id = uj.user_id " +
                "AND j.id = uj.job_id " +
                "AND j.code in (@codes) " +
                "AND j.isStatistics =1 " +
                "GROUP BY j.id";
        Sql sql = Sqls.create(slqstr);
        sql.setParam("codes", customizedParams.split(","));
        sql.setCallback(Sqls.callback.maps());
        execute(sql);
        return sql.getList(NutMap.class);

    }
}
