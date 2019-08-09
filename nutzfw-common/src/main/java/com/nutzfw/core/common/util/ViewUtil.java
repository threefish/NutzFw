package com.nutzfw.core.common.util;

import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.plugin.view.BeetlView;
import com.nutzfw.core.plugin.view.BeetlViewMaker;
import com.nutzfw.core.plugin.view.NutzFwWebRender;
import org.beetl.core.GroupTemplate;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;

import java.text.MessageFormat;
import java.util.HashMap;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/12/17
 */
public class ViewUtil {
    /**
     * 默认后缀
     */
    final static String SUFFIX = ".html";

    /**
     * 返回错误页面
     *
     * @param msg
     * @return
     */
    public static View toErrorPage(String msg) {
        return toViewPage(Cons.ERROR_PAGE, new NutMap().setv(Cons.ERROR_INFO, msg));
    }

    /**
     * 返回错误页面
     *
     * @param msg
     * @return
     */
    public static View toErrorPage(String msg, Object... param) {
        return toViewPage(Cons.ERROR_PAGE, new NutMap().setv(Cons.ERROR_INFO, MessageFormat.format(msg, param)));
    }


    /**
     * 返回指定页面
     *
     * @param templateFilePath 模版文件
     * @return
     */
    public static View toViewPage(String templateFilePath, HashMap<String, Object> data) {
        if (!templateFilePath.endsWith(SUFFIX)) {
            templateFilePath = templateFilePath.concat(SUFFIX);
        }
        GroupTemplate gt = Mvcs.getIoc().get(GroupTemplate.class, BeetlViewMaker.BEETL_GROUPTEMPLATE_IOC_NAME);
        return new BeetlView(new NutzFwWebRender(gt, data), templateFilePath);
    }

    /**
     * 返回指定页面
     *
     * @param templateFilePath 模版文件
     * @return
     */
    public static View toViewPage(String templateFilePath) {
        return toViewPage(templateFilePath, new HashMap<>(0));
    }

}
