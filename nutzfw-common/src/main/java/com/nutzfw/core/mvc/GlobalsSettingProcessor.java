/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.mvc;

import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.modules.organize.entity.UserAccount;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.impl.processor.AbstractProcessor;
import org.slf4j.MDC;

/**
 * Created with IntelliJ IDEA.
 * User: 黄川
 * Date Time: 2017/2/2117:33
 * To change this template use File | Settings | File Templates.
 * 设置基础全局基础参数
 */
public class GlobalsSettingProcessor extends AbstractProcessor {

    public static String TRACE_ID = "traceId";

    public static String TRACE_USER = "traceUser";

    @Override
    public void process(ActionContext ac) throws Throwable {
        String lang = ac.getRequest().getParameter("lang");
        if (!Strings.isEmpty(lang)) {
            Mvcs.setLocalizationKey(lang);
        } else {
            lang = Mvcs.getDefaultLocalizationKey();
        }
        ac.getRequest().setAttribute("lang", lang);
        //可以预设变量
        ac.getRequest().setAttribute("base", Mvcs.getReq().getContextPath());
        try {
            MDC.put(TRACE_ID, R.UU16());
            final UserAccount attribute = (UserAccount) ac.getRequest().getSession().getAttribute(Cons.SESSION_USER_KEY);
            if (attribute != null) {
                MDC.put(TRACE_USER, attribute.getUserName());
            }
            doNext(ac);
        } finally {
            MDC.remove(TRACE_ID);
            MDC.remove(TRACE_USER);
        }
    }
}
