package com.nutzfw.modules.sys.service;


import com.nutzfw.core.common.service.BaseService;
import com.nutzfw.modules.sys.entity.ErrorLogHistory;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年06月12日 17时18分27秒
 */
public interface ErrorLogHistoryService extends BaseService<ErrorLogHistory> {

    void async(ErrorLogHistory history);

    void sync(ErrorLogHistory history);
}
