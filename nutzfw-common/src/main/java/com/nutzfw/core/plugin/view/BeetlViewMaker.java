/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.view;

import com.google.common.collect.Sets;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.plugin.beetl.DictFn;
import com.nutzfw.core.plugin.beetl.I18nFn;
import com.nutzfw.core.plugin.beetl.ShiroExt;
import com.nutzfw.core.plugin.beetl.Utils;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.misc.BeetlUtil;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/12/17
 */
public class BeetlViewMaker implements ViewMaker {

    private static final Log log = Logs.get();

    private static final Set<String> BTL_PREFIX = Sets.newHashSet("btl", "beetl");

    private static final String DEFAUALT_WEBINF_PATH = "WEB-INF/view";

    public static boolean isDev = false;

    public static String BEETL_GROUPTEMPLATE_IOC_NAME = "$beetlGroupTemplate";

    public static String productVersion = "1.0";

    public GroupTemplate groupTemplate;

    public NutzFwWebRender render;

    public BeetlViewMaker() throws IOException {
        // 主动设置webroot, 解决maven项目下,Beetl无法找到正确的webapp路径的问题
        String webroot;
        if (Mvcs.getServletContext() != null) {
            webroot = Mvcs.getServletContext().getRealPath("/");
            if (!Strings.isBlank(webroot)) {
                BeetlUtil.setWebroot(webroot);
            }
        }
        init();
    }

    public static GroupTemplate updateBeetlGroupTemplate(Ioc ioc) {
        GroupTemplate beetlGroupTemplate = ioc.get(GroupTemplate.class, BEETL_GROUPTEMPLATE_IOC_NAME);
        beetlGroupTemplate.getSharedVars().put("sys", Cons.optionsCach);
        beetlGroupTemplate.getSharedVars().put("props", System.getProperties());
        beetlGroupTemplate.getSharedVars().put("cfg", ioc.get(PropertiesProxy.class, "conf"));
        beetlGroupTemplate.getSharedVars().put("application", Mvcs.getServletContext());
        ioc.addBean(BEETL_GROUPTEMPLATE_IOC_NAME, beetlGroupTemplate);
        return beetlGroupTemplate;
    }

    public void init() throws IOException {
        Properties prop = new Properties();
        try (InputStream ins = Configuration.class.getResourceAsStream("/view/beetl.properties")) {
            if (ins != null) {
                prop.load(ins);
            }
        }
        Configuration cfg = new Configuration(prop);
        cfg.setDirectByteOutput(false);
        cfg.setErrorHandlerClass(BeetlWebErrorHandler.class.getName());
        groupTemplate = new GroupTemplate(cfg);
        groupTemplate.registerFunctionPackage("so", new ShiroExt());
        groupTemplate.registerFunctionPackage("fn", new Utils());
        groupTemplate.registerFunction("i18n", new I18nFn());
        groupTemplate.registerFunctionPackage("Strings", Strings.class);
        groupTemplate.registerFunctionPackage("Times", Times.class);
        groupTemplate.setClassLoader(Thread.currentThread().getContextClassLoader());
        render = new NutzFwWebRender(groupTemplate);
        log.debug("beetl init complete");
    }

    public void depose() {
        if (groupTemplate != null) {
            groupTemplate.close();
        }
    }

    @Override
    public View make(Ioc ioc, String type, String value) {
        if (BTL_PREFIX.contains(type)) {
            if (!ioc.has(BEETL_GROUPTEMPLATE_IOC_NAME)) {
                groupTemplate.registerFunction("dict", ioc.get(DictFn.class));
                ioc.addBean(BEETL_GROUPTEMPLATE_IOC_NAME, groupTemplate);
            }
            if (value.startsWith(DEFAUALT_WEBINF_PATH)) {
                value = value.replaceAll(DEFAUALT_WEBINF_PATH, "");
            }
            return new BeetlView(render, value);
        }
        return null;
    }
}
