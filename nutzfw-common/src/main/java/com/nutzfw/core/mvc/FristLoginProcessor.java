/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.mvc;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.impl.processor.AbstractProcessor;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;


/**
 * @author huchuc@vip.qq.com
 * @date: 2017/2/2117:33
 * 第一次登录强制用户修改密码
 */
public class FristLoginProcessor extends AbstractProcessor {

    /**
     * 第一次登录-修改密码界面
     */
    public static final String NEED_FRIST_LOGIN = "NEED_FRIST_LOGIN";

    public static final String FRIST_LOGIN_PATH = "/manage/user/fristLogin/ChangePass";

    /**
     * 白名单
     */
    final static List<String> WHITELIST = Arrays.asList(FRIST_LOGIN_PATH, "/management/logout", "/management/login");

    private static final Log log = Logs.get();

    @Override
    public void process(ActionContext ac) throws Throwable {
        HttpServletRequest request = ac.getRequest();
        Object needFristObj = request.getSession().getAttribute(NEED_FRIST_LOGIN);
        boolean needFristChecked = needFristObj != null ? (boolean) needFristObj : false;
        if (WHITELIST.contains(request.getServletPath())) {
            needFristChecked = false;
        }
        if (needFristChecked) {
            Mvcs.getResp().sendRedirect(request.getContextPath() + FRIST_LOGIN_PATH);
        } else {
            doNext(ac);
        }
    }
}
