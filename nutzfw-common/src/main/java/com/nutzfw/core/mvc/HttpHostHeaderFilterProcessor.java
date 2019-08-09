package com.nutzfw.core.mvc;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.impl.processor.AbstractProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Http Host 头攻击拦截
 *
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/06/17
 */
public class HttpHostHeaderFilterProcessor extends AbstractProcessor {

    private List<String> whitelist;

    @Override
    public void init(NutConfig config, ActionInfo ai) {
        try {
            PropertiesProxy conf = config.getIoc().get(PropertiesProxy.class, "conf");
            whitelist = conf.getList("http_host_header_white_list", ",") == null ? new ArrayList<>() : conf.getList("http_host_header_white_list", ",");
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
        // 头攻击检测
        String requestHost = ac.getRequest().getHeader("host");
        if (requestHost != null && !whitelist.contains(requestHost)) {
            return true;
        }
        return false;
    }

    @Override
    public void process(ActionContext ac) throws Throwable {
        if (checkWhitelist(ac)) {
            ac.getResponse().setStatus(403);
            ac.getResponse().setHeader("Content-Type", "application/html;charset=UTF-8");
            ac.getResponse().getWriter().println("访问地址不在白名单中，无法访问！");
            return;
        } else {
            doNext(ac);
        }
    }
}