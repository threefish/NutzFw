package com.nutzfw.modules.tabledata.service;


import com.nutzfw.core.common.service.BaseService;
import com.nutzfw.modules.tabledata.entity.UserDataChangeHistory;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年07月02日 18时41分37秒
 */
public interface UserDataChangeHistoryService extends BaseService<UserDataChangeHistory> {
    /**
     * 加载用户表待审核记录条数
     *
     * @param tableId
     * @param userid
     * @return
     */
    int loadPendingReviewCount(int tableId, String userid);
}
