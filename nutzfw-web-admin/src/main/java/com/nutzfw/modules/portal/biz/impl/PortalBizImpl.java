/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.portal.biz.impl;

import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.ZtreeBeanVO;
import com.nutzfw.modules.organize.entity.Department;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.service.DepartmentService;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.portal.biz.PortalBiz;
import com.nutzfw.modules.portal.entity.*;
import com.nutzfw.modules.portal.service.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.trans.Trans;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * /**
 *
 * @author 叶世游
 * @date 2018/6/19 10:07
 * @description
 */
@IocBean
public class PortalBizImpl implements PortalBiz {
    @Inject
    StatisticsConfigureService statisticsConfigureService;
    @Inject
    MsgNoticeService           msgNoticeService;
    @Inject
    QuickLinkService           quickLinkService;
    @Inject
    PortalFunctionService      portalFunctionService;
    @Inject
    DepartmentService          departmentService;
    @Inject
    UserAccountService         userAccountService;
    @Inject
    PortalUserService          portalUserService;

    /**
     * 获取首页可以选择的功能tree
     *
     * @return
     */
    @Override
    public List<ZtreeBeanVO> tree() {
        List<ZtreeBeanVO> ztrees = new ArrayList<>();
        ZtreeBeanVO first = new ZtreeBeanVO("0", "0", "门户首页");
        ZtreeBeanVO first1 = new ZtreeBeanVO("1", "0", "快捷功能入口");
        ZtreeBeanVO first2 = new ZtreeBeanVO("2", "0", "消息提醒");
        ztrees.add(first);
        ztrees.add(first1);
        ztrees.add(first2);
        List<StatisticsConfigure> statistics = statisticsConfigureService.query(Cnd.where("delFlag", "=", "0").asc("sort"));
        statistics.forEach(s -> {
            ZtreeBeanVO bean = new ZtreeBeanVO(s.getId(), "0", s.getName());
            ztrees.add(bean);
        });
        List<MsgNotice> msgNotices = msgNoticeService.query(Cnd.where("delFlag", "=", "0").asc("sort"));
        msgNotices.forEach(m -> {
            ZtreeBeanVO bean = new ZtreeBeanVO(m.getId(), "2", m.getName());
            ztrees.add(bean);
        });
        List<QuickLink> quickLinks = quickLinkService.query(Cnd.where("delFlag", "=", "0").asc("sort"));
        quickLinks.forEach(q -> {
            ZtreeBeanVO bean = new ZtreeBeanVO(q.getId(), "1", q.getName());
            ztrees.add(bean);
        });
        return ztrees;
    }

    /**
     * 获取已选中的功能
     *
     * @param id
     * @return
     */
    @Override
    public AjaxResult getSelectedIds(String id) {
        List<PortalFunction> portalFunctions = portalFunctionService.query(Cnd.where("groupId", "=", id));
        List<String> ids = new ArrayList<>();
        portalFunctions.forEach(g -> {
            ids.add(g.getFunId());
        });
        return AjaxResult.sucess(ids, "数据获取成功!");
    }

    /**
     * 保存组和功能之间的关系
     *
     * @param groupId
     * @param ztreeBeans
     * @return
     */
    @Override
    public AjaxResult saveGroupFunction(String groupId, String ztreeBeans) {
        try {
            JSONArray jsonArray = new JSONArray(ztreeBeans);
            List<PortalFunction> portalFunctions = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if ("0".equals(jsonObject.getString("id"))
                        || "1".equals(jsonObject.getString("id"))
                        || "2".equals(jsonObject.getString("id"))) {
                    continue;
                }
                PortalFunction portalFunction = new PortalFunction();
                portalFunction.setFunId(jsonObject.getString("id"));
                portalFunction.setGroupId(groupId);
                switch (jsonObject.getInt("pid")) {
                    case 0:
                        portalFunction.setType(StatisticsConfigure.FUN_TYPE);
                        break;
                    case 1:
                        portalFunction.setType(QuickLink.FUN_TYPE);
                        break;
                    case 2:
                        portalFunction.setType(MsgNotice.FUN_TYPE);
                    default:
                        break;
                }
                portalFunctions.add(portalFunction);
            }
            Trans.exec(() -> {
                Sql sql = Sqls.create("delete from sys_portal_function where groupId =@groupId");
                sql.setParam("groupId", groupId);
                portalFunctionService.execute(sql);
                portalFunctionService.insert(portalFunctions);
            });
        } catch (JSONException e) {
            e.printStackTrace();
            return AjaxResult.error("数据格式转换失败!");
        }
        return AjaxResult.sucess("保存成功!");
    }

    /**
     * 保存组和人之间的关系
     *
     * @param groupId
     * @param userIds
     * @return
     */
    @Override
    public AjaxResult savePortalUser(String groupId, String[] userIds) {

        try {
            List<PortalUser> portalUsers = new ArrayList<>();
            for (int i = 0; i < userIds.length; i++) {
                PortalUser portalUser = new PortalUser();
                portalUser.setGroupId(groupId);
                portalUser.setUserId(userIds[i]);
                portalUsers.add(portalUser);
            }
            Trans.exec(() -> {
                Sql sql = Sqls.create("delete from sys_portal_user where groupId =@groupId");
                sql.setParam("groupId", groupId);
                portalUserService.execute(sql);
                portalUserService.insert(portalUsers);
            });
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("数据格式转换失败!");
        }
        return AjaxResult.sucess("保存成功!");
    }

    /**
     * 获取所有用户树
     *
     * @return
     */
    @Override
    public List<ZtreeBeanVO> userTree() {
        List<Department> depts = departmentService.tree();
        List<ZtreeBeanVO> ztreeBeans = new ArrayList<>();
        depts.forEach(d -> {
            ZtreeBeanVO bean = new ZtreeBeanVO(d.getId(), d.getPid(), d.getName());
            ztreeBeans.add(bean);
        });
        List<UserAccount> users = userAccountService.query(Cnd.where("delFlag", "=", 0));
        users.forEach(u -> {
            ZtreeBeanVO bean = new ZtreeBeanVO(u.getId(), u.getDeptId(), u.getRealName(), "fa fa-user user_skin");
            ztreeBeans.add(bean);
        });
        return ztreeBeans;
    }

    /**
     * 获取人和组的关系
     *
     * @param groupId
     * @return
     */
    @Override
    public AjaxResult getSelectedUserIds(String groupId) {
        List<PortalUser> portalUsers = portalUserService.query(Cnd.where("groupId", "=", groupId));
        List<String> userIds = new ArrayList<>();
        portalUsers.forEach(g -> {
            userIds.add(g.getUserId());
        });
        return AjaxResult.sucess(userIds, "数据获取成功!");
    }
}
