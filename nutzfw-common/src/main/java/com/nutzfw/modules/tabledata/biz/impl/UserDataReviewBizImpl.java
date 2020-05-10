/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.tabledata.biz.impl;

import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.sys.service.DataTableService;
import com.nutzfw.modules.tabledata.biz.UserDataReviewBiz;
import com.nutzfw.modules.tabledata.entity.UserDataChangeHistory;
import com.nutzfw.modules.tabledata.service.UserDataChangeHistoryService;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.trans.Trans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/7/3
 * 描述此类：
 */
@IocBean(name = "userDataReviewBiz")
public class UserDataReviewBizImpl implements UserDataReviewBiz {

    private static final Log log = Logs.get();

    @Inject
    UserDataChangeHistoryService userDataChangeHistoryService;

    @Inject
    UserAccountService userAccountService;

    @Inject
    DataTableService tableService;

    @Override
    public NutMap showReviewData(String id) {
        UserDataChangeHistory userDataChangeHistory = userDataChangeHistoryService.fetch(id);
        NutMap data = NutMap.NEW().addv("data", userDataChangeHistory);
        UserAccount userAccount = userAccountService.fetch(userDataChangeHistory.getUserId());
        userAccountService.fetchLinks(userAccount, null);
        data.put("user", userAccount);
        if (userDataChangeHistory.getStatus() == 2) {
            //是删除操作
            data.put("delList", Json.fromJson(userDataChangeHistory.getDelIdsJson()));
        }
        data.put("changeList", Json.fromJson(userDataChangeHistory.getDataChangeJson()));
        return data;
    }

    @Override
    public AjaxResult agreeReview(String id, String reviewOpinion) {
        try {
            UserDataChangeHistory userDataChangeHistory = userDataChangeHistoryService.fetch(id);

            //带前缀的是转换时候将依赖值给转换进去了，在这里过滤掉
            String prefix = "fromData.";
            NutMap newData = NutMap.WRAP(userDataChangeHistory.getNewDataJson());
            Set<String> keys=new HashSet<>();
            newData.forEach((key, val) -> {
                if (key.startsWith(prefix)) {
                    keys.add(key);
                }
            });
            keys.forEach(key -> newData.remove(key));
            Trans.exec(() -> {
                if (userDataChangeHistory.getStatus() == 0) {
                    userDataChangeHistoryService.dao().insert(newData);
                } else if (userDataChangeHistory.getStatus() == 1) {
                    userDataChangeHistoryService.dao().update(newData);
                } else if (userDataChangeHistory.getStatus() == 2) {
                    List<String> sourceIds = Json.fromJson(List.class, userDataChangeHistory.getDelIdsJson());
                    String tableName = tableService.fetch(userDataChangeHistory.getTableId()).getTableName();
                    userDataChangeHistoryService.dao().clear(tableName, Cnd.where("id", "in", sourceIds));
                }
                // 1通过审核
                userDataChangeHistory.setReview(1);
                userDataChangeHistory.setReviewOpinion(reviewOpinion);
                userDataChangeHistoryService.update(userDataChangeHistory);
            });
        } catch (Exception e) {
            log.error(e);
            return AjaxResult.error("审核失败！" + e.getLocalizedMessage());
        }
        return AjaxResult.sucess("审核成功！");
    }
}
