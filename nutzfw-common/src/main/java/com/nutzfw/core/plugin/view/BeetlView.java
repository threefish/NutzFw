/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.view;

import org.beetl.ext.nutz.LazyResponseWrapper;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.view.AbstractPathView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/12/17
 */
public class BeetlView extends AbstractPathView {

    protected NutzFwWebRender render;

    public BeetlView(NutzFwWebRender render, String dest) {
        super(dest);
        this.render = render;
    }

    @Override
    public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable {
        String child = evalPath(req, obj);
        if (child == null) {
            child = Mvcs.getActionContext().getPath();
        }
        if (resp.getContentType() == null) {
            resp.setContentType("text/html");
        }
        render.render(child, req, new LazyResponseWrapper(resp), obj);
    }
}