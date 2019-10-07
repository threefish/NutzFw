/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.beetl;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.nutz.mvc.MvcI18n;

/**
 * Created with IntelliJ IDEA.
 *
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2018/1/24  14:25
 * 描述此类：
 */
public class I18nFn implements Function {

    @Override
    public Object call(Object[] obj, Context context) {
        if (obj.length == 1) {
            return MvcI18n.message((String) obj[0]);
        } else if (obj.length > 1) {
            String key = String.valueOf(obj[0]);
            Object[] params = new Object[obj.length - 1];
            for (int i = 1; i < obj.length; i++) {
                params[i - 1] = obj[i];
            }
            return MvcI18n.message(key, params);
        } else {
            return "";
        }
    }

}
