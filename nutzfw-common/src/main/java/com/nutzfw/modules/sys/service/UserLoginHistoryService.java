package com.nutzfw.modules.sys.service;


import com.nutzfw.core.common.service.BaseService;
import com.nutzfw.modules.sys.entity.UserLoginHistory;
import org.nutz.aop.interceptor.async.Async;

/**
 * Created with IntelliJ IDEA Code Generator
 *
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年01月02日 15时39分07秒
 * 功能描述： 人员登陆历史
 */
public interface UserLoginHistoryService extends BaseService<UserLoginHistory> {

    /**
     * 是否有登陆历史
     *
     * @return
     */
    boolean hasHistory(String userid);

    @Async
    void async(UserLoginHistory userLoginHistory);

    void sync(UserLoginHistory userLoginHistory);
}
