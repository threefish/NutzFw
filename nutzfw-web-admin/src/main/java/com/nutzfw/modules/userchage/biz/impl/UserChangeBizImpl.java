/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.userchage.biz.impl;

import com.nutzfw.core.common.util.DateUtil;
import com.nutzfw.core.common.util.StringUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.entity.UserAccountJob;
import com.nutzfw.modules.organize.service.DepartmentService;
import com.nutzfw.modules.organize.service.UserAccountJobService;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.userchage.biz.UserChangeBiz;
import com.nutzfw.modules.userchage.entity.UserChangeHistory;
import com.nutzfw.modules.userchage.service.UserChangeHistoryService;
import org.json.JSONException;
import org.json.JSONObject;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.trans.Trans;

import java.util.Date;
import java.util.List;

/**
 * @author 叶世游
 * @date 2018/7/10 11:43
 * @description
 */
@IocBean
public class UserChangeBizImpl implements UserChangeBiz {
    @Inject
    UserChangeHistoryService userChangeHistoryService;
    @Inject
    DepartmentService        departmentService;
    @Inject
    UserAccountJobService    userAccountJobService;
    @Inject
    UserAccountService       userAccountService;

    /**
     * 查询所有用户
     *
     * @param pageNum
     * @param pageSize
     * @param key
     * @return
     */
    @Override
    public LayuiTableDataListVO listUsersPage(int pageNum, int pageSize, String key) {
        Cnd cnd = Cnd.where("delFlag", "=", 0);
        if (!StringUtil.isBlank(key)) {
            cnd.and(
                    Cnd.exps("realName", "like", "%" + key + "%")
                            .or("userName", "like", "%" + key + "%")
            );
        }
        return userAccountService.listPage(pageNum, pageSize, cnd);
    }

    /**
     * 查询用户部门和岗位
     *
     * @param userid
     * @return
     */
    @Override
    public AjaxResult userDeptJobInfo(String userid) {
        String sqlstr = "SELECT * FROM \n" +
                "(SELECT d.`name` AS deptName  FROM sys_user_account ua,sys_department d\n" +
                "WHERE d.id = ua.deptId AND ua.id = @userid\n" +
                ")AS dn,\n" +
                "(SELECT j.`name` AS jobName FROM sys_user_account_job uaj,sys_job j\n" +
                "WHERE uaj.user_id=@userid AND j.id = uaj.job_id\n" +
                ")AS jn\n";
        try {
            Sql sql = Sqls.create(sqlstr);
            sql.setParam("userid", userid);
            sql.setCallback(Sqls.callback.map());
            userAccountService.execute(sql);
            NutMap map = sql.getObject(NutMap.class);
            return AjaxResult.sucess(map, "获取数据成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("获取部门岗位失败");
        }
    }

    /**
     * 保存人员异动
     *
     * @param data
     * @param sessionUserAccount
     * @return
     */
    @Override
    public AjaxResult saveUserChange(String data, UserAccount sessionUserAccount) {
        try {
            if (StringUtil.isBlank(data)) {
                return AjaxResult.error("数据有误");
            }
            JSONObject jsonData = new JSONObject(data);
            UserChangeHistory userChangeHistory = new UserChangeHistory();
            userChangeHistory.setUserId(jsonData.getString("userid"));
            userChangeHistory.setChangeType(jsonData.getInt("changeType"));
            userChangeHistory.setAddUserId(sessionUserAccount.getId());
            userChangeHistory.setAddDate(new Date());
            userChangeHistory.setAttachIds(jsonData.getString("attachIds"));
            userChangeHistory.setDataChangeJson(data);
            userChangeHistory.setRemark(jsonData.getString("remark"));
            userChangeHistory.setChangeDate(DateUtil.string2date(jsonData.getString("changeDate"), DateUtil.YYYY_MM_DD));
            //创建要保存的数据
            JSONObject newData = new JSONObject();
            newData.put("newDeptId", jsonData.getString("newDeptId"));
            newData.put("newJobId", jsonData.getString("newJobId"));
            newData.put("userId", jsonData.getString("userid"));
            userChangeHistory.setNewDataJson(newData.toString());

            userChangeHistoryService.insert(userChangeHistory);
            return AjaxResult.sucess("保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("保存失败");
        }
    }

    /**
     * 查询人员异动记录
     *
     * @param pageNum
     * @param pageSize
     * @param key
     * @param review
     * @param changeType
     * @return
     */
    @Override
    public LayuiTableDataListVO listPage(int pageNum, int pageSize, String key, int review, int changeType) {
        String sqlstr = "FROM sys_user_change_history AS uch\n" +
                "LEFT JOIN sys_user_account AS ua1\n" +
                "ON ua1.userid = uch.userId\n" +
                "LEFT JOIN sys_user_account AS ua2\n" +
                "ON ua2.userid = uch.addUserId\n" +
                "LEFT JOIN sys_dict dd\n" +
                "ON dd.id = uch.changeType\n" +
                "WHERE uch.delFlag =0\n";
        if (changeType != 0) {
            sqlstr += "AND uch.changeType = @changeType\n";
        }
        if (review == -1) {
            sqlstr += "AND (uch.review =2 OR uch.review = 1)\n";
        } else if (review == 0) {
            sqlstr += "AND  uch.review = 0\n";
        }
        if (!StringUtil.isBlank(key)) {
            sqlstr += "AND (ua1.realName LIKE @key OR ua1.userName LIKE @key)\n";
        }

        //查询总数
        String countStr = "SELECT count(0) " + sqlstr;
        Sql sql2 = Sqls.create(countStr);
        sql2.setParam("changeType", changeType);
        sql2.setParam("review", review);
        sql2.setParam("key", "%" + key + "%");
        sql2.setCallback(Sqls.callback.integer());
        userChangeHistoryService.execute(sql2);
        Integer sum = sql2.getInt();
        //查询数据
        String queryStr = "SELECT uch.uuid,uch.attachIds,ua1.userName,ua1.realName,dd.lable AS changeReason,\n" +
                "uch.oldDataViewJson,uch.changeDate,uch.remark,uch.review,ua2.realName \n" +
                "AS createName,uch.addDate " + sqlstr + " ORDER BY uch.review ASC,uch.addDate DESC";
        Sql sql = Sqls.create(queryStr);
        sql.setParam("changeType", changeType);
        sql.setParam("review", review);
        sql.setParam("key", "%" + key + "%");
        sql.setCallback(Sqls.callback.maps());
        Pager pager = new Pager(pageNum, pageSize);
        sql.setPager(pager);
        userChangeHistoryService.execute(sql);
        List<NutMap> data = sql.getList(NutMap.class);

        return new LayuiTableDataListVO(pageNum, pageSize, sum, data);
    }

    /**
     * 审核人员异动
     *
     * @param changeId
     * @param review
     * @param reviewOpinion
     * @param sessionUserAccount
     * @return
     */
    @Override
    public AjaxResult review(String changeId, int review, String reviewOpinion, UserAccount sessionUserAccount) {
        try {
            UserChangeHistory change = userChangeHistoryService.fetch(changeId);
            if (change != null) {
                //更新异动记录
                change.setReview(review);
                change.setReviewOpinion(reviewOpinion);
                change.setReviewDate(new Date());
                change.setReviewUserId(sessionUserAccount.getId());
                if (review == 1) {
                    //获取要异动的内容
                    JSONObject json = new JSONObject(change.getNewDataJson());
                    //获取人员/部门/人员与岗位关联
                    UserAccount user = userAccountService.fetch(json.getString("userId"));
                    UserAccountJob oldjob = userAccountJobService.fetch(Cnd.where("userId", "=", user.getId()));
                    user.setDeptId(json.getString("newDeptId"));
                    UserAccountJob userJob = new UserAccountJob();
                    userJob.setJobId(json.getString("newJobId"));
                    userJob.setUserId(user.getId());
                    Trans.exec(() -> {
                        userAccountService.update(user);
                        userAccountJobService.delete(oldjob);
                        userAccountJobService.insert(userJob);
                        userChangeHistoryService.update(change);
                    });
                } else {
                    userChangeHistoryService.update(change);
                }
                return AjaxResult.sucess("审核成功!");
            } else {
                return AjaxResult.error("没找到异动记录");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("审核失败!");
        }
    }

    /**
     * 获取人员历史
     *
     * @param id
     * @return
     */
    @Override
    public List<NutMap> history(String id) {
        String sqlstr = "SELECT uch.uuid,uch.addDate,ua2.realName AS createName,\n" +
                "dd.lable AS changeReason,uch.reviewOpinion,ua.realName AS reviewName,\n" +
                "uch.reviewDate,uch.oldDataViewJson,uch.changeDate,uch.remark,uch.review \n" +
                "FROM sys_user_change_history AS uch\n" +
                "LEFT JOIN sys_user_account AS ua\n" +
                "ON ua.id = uch.reviewUserId\n" +
                "LEFT JOIN sys_user_account AS ua2\n" +
                "ON ua2.id = uch.addUserId\n" +
                "LEFT JOIN sys_dict dd\n" +
                "ON dd.id = uch.changeType\n" +
                "WHERE uch.delFlag =0\n" +
                "AND uch.userId=@id\n" +
                "ORDER BY addDate DESC";
        Sql sql = Sqls.create(sqlstr);
        sql.setParam("id", id);
        sql.setCallback(Sqls.callback.maps());
        userChangeHistoryService.execute(sql);
        List<NutMap> maps = sql.getList(NutMap.class);
        maps.forEach(m -> {
            try {
                m.setv("changeData", new JSONObject(m.getString("oldDataViewJson")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        return maps;
    }

    /**
     * 查询人员是否有未审核的异动
     *
     * @param userid
     * @return
     */
    @Override
    public AjaxResult haveChange(String userid) {
        UserChangeHistory history = userChangeHistoryService.fetch(Cnd.where("userId", "=", userid).and("review", "=", 0));
        if (history != null) {
            return AjaxResult.error("该人员有未审核的异动,无法选择!");
        } else {
            return AjaxResult.sucess("可以选择");
        }
    }
}
