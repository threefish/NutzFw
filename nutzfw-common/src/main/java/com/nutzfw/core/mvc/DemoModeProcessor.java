/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/11/29 21:15:29
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.mvc;

import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.vo.AjaxResult;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.impl.processor.AbstractProcessor;
import org.nutz.mvc.view.ForwardView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/11/29
 */
public class DemoModeProcessor extends AbstractProcessor {

    private static List<String> URL_LIST = new ArrayList<>();

    boolean demoMode;

    @Override
    public void init(NutConfig config, ActionInfo ai) {
        try {
            PropertiesProxy conf = config.getIoc().get(PropertiesProxy.class, "conf");
            demoMode = conf.getBoolean("demoMode", false);
            List<String> demoModeUrl = conf.getList("demoModeUrl", ",");
            if (demoModeUrl != null) {
                URL_LIST = demoModeUrl;
            }
        } catch (Exception e) {
        }
    }

    /**
     * 检查白名单
     *
     * @param ac
     * @return
     */
    boolean checkWhitelist(ActionContext ac) {
        String requestURI = ac.getRequest().getRequestURI();
        if (URL_LIST.contains(requestURI)) {
            return true;
        }
        return false;
    }


    @Override
    public void process(ActionContext ac) throws Throwable {
        if (demoMode && checkWhitelist(ac)) {
            //演示模式
            if (NutShiro.isAjax(ac.getRequest())) {
                NutShiro.rendAjaxResp(ac.getRequest(), ac.getResponse(), AjaxResult.error("演示模式不允许该操作！"));
            } else {
                new ForwardView(Cons.ERROR_PAGE).render(ac.getRequest(), ac.getResponse(), "演示模式不允许该操作！");
            }
        } else {
            doNext(ac);
        }
    }
}
