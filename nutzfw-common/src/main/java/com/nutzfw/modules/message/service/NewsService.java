/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.message.service;

import com.nutzfw.core.common.service.BaseService;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.message.entity.News;
import com.nutzfw.modules.organize.entity.UserAccount;

import java.util.List;
import java.util.Set;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2019年07月30日
 * 新闻
 */
public interface NewsService extends BaseService<News> {
    /**
     * 首页新闻查询
     * @return
     * @param sessionUserAccount
     * @param sessionRoleIds
     */
    List<News> queryIndexNewsList(UserAccount sessionUserAccount, Set<String> sessionRoleIds);

    List<News> queryIndexImgNewsList(UserAccount sessionUserAccount, Set<String> sessionRoleIds);

    LayuiTableDataListVO queryLookMoreList(UserAccount sessionUserAccount, Set<String> sessionRoleIds, int pageNum, int pageSize, String key);
}
