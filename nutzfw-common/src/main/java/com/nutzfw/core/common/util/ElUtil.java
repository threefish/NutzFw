package com.nutzfw.core.common.util;

import org.nutz.el.El;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;

import java.util.Map;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/25
 */
public class ElUtil {

    /**
     * 根据字符串模版和变量 渲染字符串
     * <p>
     * render("${user.realName} 你好",Lang.context().set("user", UserAccount.builder().realName("张三")));// 张三 你好
     *
     * @param templateText
     * @param ctx
     * @return
     */
    public static String render(String templateText, Context ctx) {
        return El.render(templateText, ctx);
    }

    /**
     * 根据字符串模版和变量 渲染字符串
     *
     * @param templateText
     * @param data
     * @return
     */
    public static String render(String templateText, Map data) {
        return render(templateText, Lang.context(data));
    }

}
