package com.nutzfw.modules.sys.biz;

import com.nutzfw.core.plugin.shiro.LoginTypeEnum;
import com.nutzfw.modules.organize.entity.UserAccount;
import org.nutz.lang.util.NutMap;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/7/19
 * 描述此类：
 */
public interface LoginBiz {

    String NEED_CHANGE_PASS = "needChangePass";

    NutMap doLogin(UserAccount userAccount, LoginTypeEnum loginType);
}
