/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
