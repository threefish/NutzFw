/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.portal.biz.impl;

import com.nutzfw.core.common.util.DateUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.service.DepartmentService;
import com.nutzfw.modules.organize.service.JobService;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.portal.biz.IndexBiz;
import com.nutzfw.modules.portal.entity.*;
import com.nutzfw.modules.portal.service.*;
import com.nutzfw.modules.sys.biz.DictBiz;
import com.nutzfw.modules.sys.entity.Dict;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 叶世游
 * @date 2018/6/20 14:49
 * @description
 */
@IocBean
public class IndexBizImpl implements IndexBiz {
    @Inject
    MsgNoticeService           msgNoticeService;
    @Inject
    StatisticsConfigureService statisticsConfigureService;
    @Inject
    PortalFunctionService      portalFunctionService;
    @Inject
    PortalUserService          portalUserService;
    @Inject
    QuickLinkService           quickLinkService;
    @Inject
    DepartmentService          departmentService;
    @Inject
    UserAccountService         userAccountService;
    @Inject
    JobService                 jobService;
    @Inject
    DictBiz                    dictBiz;

    /**
     * 获取当前用户信息
     *
     * @return
     */
    @Override
    public UserAccount getUserInfo(String id) {
        return userAccountService.fetch(id);
    }

    /**
     * 获取当前用户可视的消息提醒
     *
     * @param account
     */
    @Override
    public AjaxResult msgNotices(UserAccount account) {
        List<PortalUser> portalUsers = portalUserService.query(
                Cnd.where("userId", "=", account.getUserid())
                        .and("delFlag", "=", 0));
        List<String> groupIds = new ArrayList<>();
        portalUsers.forEach(g -> {
            groupIds.add(g.getGroupId());
        });
        if (groupIds.size() > 0) {
            //通过组获取所有的消息提醒关联
            List<PortalFunction> portalFunctions = portalFunctionService.query(Cnd.where("groupId", "in", groupIds)
                    .and("type", "=", MsgNotice.FUN_TYPE).and("delFlag", "=", 0));
            List<String> msgIds = new ArrayList<>();
            portalFunctions.forEach(g -> {
                msgIds.add(g.getFunId());
            });
            if (msgIds.size() > 0) {
                //取出所有的消息提醒
                List<MsgNotice> msgNotices = msgNoticeService.query(Cnd.where("id", "in", msgIds)
                        .and("delFlag", "=", 0).desc("sort"));
                msgNotices.forEach(m -> {
                    getCount(m);
                });
                return AjaxResult.sucess(msgNotices, "获取成功");
            }
        }
        return AjaxResult.error("获取失败");
    }

    /**
     * 获取提醒消息的条数
     *
     * @param m
     * @return
     */
    private void getCount(MsgNotice m) {
        try {
            String sqlstr = m.getSqlStr();
            Date[] dates = new Date[2];
            if (m.getDateType() == 3) {
                dates = DateUtil.addDays(m.getDateNum());
            } else if (m.getDateType() == 2) {
                dates = DateUtil.addMonths(m.getDateNum());
            } else if (m.getDateType() == 1) {
                dates = DateUtil.addYears(m.getDateNum());
            }
            sqlstr = "select count(0) from (" + sqlstr + ") as a";
            Sql sql = Sqls.create(sqlstr);
            if (m.getDateType() != 0) {
                sql.setParam("start", dates[0]);
                sql.setParam("end", dates[1]);
            }
            sql.setCallback(Sqls.callback.integer());
            msgNoticeService.execute(sql);
            Integer count = sql.getInt();
            m.setName(m.getName().replace("{count}", count.toString()));
        } catch (Exception e) {
            m.setName(m.getName().replace("{count}", "0"));
//            e.printStackTrace();
        }
    }

    /**
     * 获取当前用户可视的统计信息
     *
     * @param account
     * @return
     */
    @Override
    public AjaxResult statisticsConfigures(UserAccount account) {
        //获取和人想关联的组
        List<PortalUser> portalUsers = portalUserService.query(Cnd.where("userId", "=", account.getUserid()).and("delFlag", "=", 0));
        List<String> groupIds = new ArrayList<>();
        portalUsers.forEach(g -> {
            groupIds.add(g.getGroupId());
        });
        if (groupIds.size() > 0) {
            //通过组获取所有的统计信息关联
            List<PortalFunction> portalFunctions = portalFunctionService.query(Cnd.where("groupId", "in", groupIds)
                    .and("type", "=", StatisticsConfigure.FUN_TYPE).and("delFlag", "=", 0));
            List<String> configids = new ArrayList<>();
            portalFunctions.forEach(g -> {
                configids.add(g.getFunId());
            });
            if (configids.size() > 0) {
                //取出所有的统计信息
                List<StatisticsConfigure> configures = statisticsConfigureService.query(Cnd.where("id", "in", configids).and("delFlag", "=", 0).asc("sort"));
                try {
                    configures.forEach(c -> {
                        List<NutMap> maps = null;
                        if (c.isCustomized()) {
                            switch (c.getCustomizedType()) {
                                case "dept_user":
                                    maps = departmentService.statisticsDeptUser(c.getCustomizedParams());
                                    break;
                                case "job_user":
                                    maps = jobService.statisticsJobUser(c.getCustomizedParams());
                                default: {
                                    break;
                                }
                            }
                        } else {
                            Sql sql = Sqls.create(c.getSqlStr());
                            sql.setCallback(Sqls.callback.maps());
                            statisticsConfigureService.execute(sql);
                            maps = sql.getList(NutMap.class);
                        }
                        c.setData(maps);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    return AjaxResult.error("获取失败");
                }
                return AjaxResult.sucess(configures, "获取成功!");
            }
        }
        return AjaxResult.error("获取失败!");
    }

    /**
     * 获取当前用户可视的快捷操作
     *
     * @param account
     * @return
     */
    @Override
    public AjaxResult quickLinks(UserAccount account) {
        //获取和人想关联的组
        List<PortalUser> portalUsers = portalUserService.query(Cnd.where("userId", "=", account.getUserid()).and("delFlag", "=", 0));
        List<String> groupIds = new ArrayList<>();
        portalUsers.forEach(g -> {
            groupIds.add(g.getGroupId());
        });
        if (groupIds.size() > 0) {
            //通过组获取所有的快捷连接
            List<PortalFunction> portalFunctions = portalFunctionService.query(Cnd.where("groupId", "in", groupIds)
                    .and("type", "=", QuickLink.FUN_TYPE)
                    .and("delFlag", "=", 0));
            List<String> quickLinkIds = new ArrayList<>();
            portalFunctions.forEach(g -> {
                quickLinkIds.add(g.getFunId());
            });
            if (quickLinkIds.size() > 0) {
                //取出所有的统计信息
                List<QuickLink> quickLinks = quickLinkService.query(Cnd.where("uuid", "in", quickLinkIds).and("delFlag", "=", 0).asc("sort"));
                return AjaxResult.sucess(quickLinks, "获取成功!");
            }
        }
        return AjaxResult.sucess(new ArrayList<>(), "获取成功!");
    }

    /**
     * 获取消息详情
     *
     * @param mid
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public LayuiTableDataListVO msgDetail(String mid, int pageNum, int pageSize) {
        MsgNotice m = msgNoticeService.fetch(mid);
        String sqlstr = m.getSqlStr();
        Date[] dates = new Date[2];
        if (m.getDateType() == 3) {
            dates = DateUtil.addDays(m.getDateNum());
        } else if (m.getDateType() == 2) {
            dates = DateUtil.addMonths(m.getDateNum());
        } else if (m.getDateType() == 1) {
            dates = DateUtil.addYears(m.getDateNum());
        }
        String sqlstr2 = "select count(0) from (" + sqlstr + ") as a";
        Sql sql = Sqls.create(sqlstr);
        Sql sql2 = Sqls.create(sqlstr2);
        if (m.getDateType() != 0) {
            sql.setParam("start", dates[0]);
            sql.setParam("end", dates[1]);
            sql2.setParam("start", dates[0]);
            sql2.setParam("end", dates[1]);
        }

        Pager pager = new Pager();
        pager.setPageSize(pageSize);
        pager.setPageNumber(pageNum);
        sql.setPager(pager);
        sql.setCallback(Sqls.callback.maps());
        msgNoticeService.execute(sql);
        List<NutMap> maps = sql.getList(NutMap.class);

        sql2.setCallback(Sqls.callback.integer());
        msgNoticeService.execute(sql2);
        Integer count = sql2.getInt();

        return new LayuiTableDataListVO(pageSize, pageNum, count, maps);
    }

    /**
     * 获取消息信息
     *
     * @param mid
     * @return
     */
    @Override
    public MsgNotice getMsgNotice(String mid) {
        return msgNoticeService.fetch(mid);
    }

    /**
     * 修改用户信息
     *
     * @param account
     */
    @Override
    public void updateUser(UserAccount account) {
        userAccountService.update(account);
    }

    /**
     * 获取字典信息
     *
     * @param dictId
     * @param sysCode
     * @return
     */
    @Override
    public Dict getDict(int dictId, String sysCode) {
        if (dictId == 0) {
            return null;
        }
        return dictBiz.getCacheDict(dictId, sysCode);
    }
}
