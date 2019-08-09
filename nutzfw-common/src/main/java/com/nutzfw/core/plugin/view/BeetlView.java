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