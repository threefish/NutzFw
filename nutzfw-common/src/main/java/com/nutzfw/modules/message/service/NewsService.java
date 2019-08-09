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
