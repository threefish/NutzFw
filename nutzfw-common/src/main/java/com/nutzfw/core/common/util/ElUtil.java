/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
