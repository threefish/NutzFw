/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.mvc;

import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.vo.AjaxResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.util.AntPathMatcher;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.impl.processor.AbstractProcessor;
import org.nutz.mvc.view.ForwardView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * SQL XSS拦截
 * Created by wizzer on 2016/7/1.
 *
 * @author 黄川 huchuc@vip.qq.com
 */
public class XssSqlFilterProcessor extends AbstractProcessor {

    private static final Log log = Logs.get();


    private List<String> whitelist;

    @Override
    public void init(NutConfig config, ActionInfo ai) throws Throwable {
        try {
            PropertiesProxy conf = config.getIoc().get(PropertiesProxy.class, "conf");
            whitelist = conf.getList("xss_sql_white_list", ",") == null ? new ArrayList<>() : conf.getList("xss_sql_white_list", ",");
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
        final AntPathMatcher antPathMatcher = new AntPathMatcher();
        String path = ac.getPath();
        for (String urlpattern : whitelist) {
            boolean match = antPathMatcher.match(urlpattern, path);
            if (match) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void process(ActionContext ac) throws Throwable {
        if (checkWhitelist(ac)) {
            if (checkParams(ac)) {
                if (NutShiro.isAjax(ac.getRequest())) {
                    ac.getResponse().addHeader("loginStatus", "paramsDenied");
                    NutShiro.rendAjaxResp(ac.getRequest(), ac.getResponse(), AjaxResult.error("表单数据存在：XSS关键字或SQL关键字。为防止攻击，当前表单数据未能提交，请检查后再提交。"));
                } else {
                    new ForwardView(Cons.ERROR_PAGE).render(ac.getRequest(), ac.getResponse(), "表单数据存在：XSS关键字或SQL关键字。为防止攻击，当前表单数据未能提交，请检查后再提交。");
                }
                return;
            }
        }
        doNext(ac);
    }

    /**
     * 效验参数
     *
     * @param ac
     * @return
     */
    protected boolean checkParams(ActionContext ac) {
        HttpServletRequest req = ac.getRequest();
        // 获取所有的表单参数
        Iterator<String[]> values = req.getParameterMap().values().iterator();
        // 因为是游标所以要重新获取
        Iterator<String[]> values2 = req.getParameterMap().values().iterator();
        boolean isError = false;
        String regExSql = "select|update|and|or|delete|insert|trancate|char|chr|into|substr|ascii|declare|exec|count|master|drop|execute";
        String regExXss = "script|iframe";
        //SQL过滤
        while (values.hasNext()) {
            String[] valueArray = values.next();
            for (int i = 0; i < valueArray.length; i++) {
                String value = valueArray[i].toLowerCase();
                //分拆关键字
                String[] injStra = StringUtils.split(regExSql, "|");
                for (int j = 0; j < injStra.length; j++) {
                    // 判断如果路径参数值中含有关键字则返回true,并且结束循环
                    if ("and".equals(injStra[j]) || "or".equals(injStra[j]) || "into".equals(injStra[j])) {
                        if (value.contains(" " + injStra[j] + " ")) {
                            isError = true;
                            log.debugf("[%-4s]URI=%s %s", req.getMethod(), req.getRequestURI(), "SQL关键字过滤:" + value);
                            break;
                        }
                    } else {
                        if (value.contains(" " + injStra[j] + " ")
                                || value.contains(
                                injStra[j] + " ")) {
                            isError = true;
                            log.debugf("[%-4s]URI=%s %s", req.getMethod(), req.getRequestURI(), "SQL关键字过滤:" + value);
                            break;
                        }
                    }
                }
                if (isError) {
                    break;
                }
            }
            if (isError) {
                break;
            }
        }
        if (!isError) {
            // XSS漏洞过滤
            while (values2.hasNext()) {
                String[] valueArray = values2.next();
                for (int i = 0; i < valueArray.length; i++) {
                    String value = valueArray[i].toLowerCase();
                    // 分拆关键字
                    String[] injStra = StringUtils.split(regExXss, "|");
                    for (int j = 0; j < injStra.length; j++) {
                        // 判断如果路径参数值中含有关键字则返回true,并且结束循环
                        if (value.contains("<" + injStra[j] + ">")
                                || value.contains("<" + injStra[j])
                                || value.contains(injStra[j] + ">")) {
                            log.debugf("[%-4s]URI=%s %s", req.getMethod(), req.getRequestURI(), "XSS关键字过滤:" + value);
                            isError = true;
                            break;
                        }
                    }
                    if (isError) {
                        break;
                    }
                }
                if (isError) {
                    break;
                }
            }
        }
        return isError;
    }
}
