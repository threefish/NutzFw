/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:29:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

import org.junit.Test;
import org.nutz.el.El;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;

import static org.junit.Assert.assertEquals;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/5/29
 */
public class ElTest {

    @Test
    public void test1() {
        String assertHtml = "您的验证码是1234";
        Context context = Lang.context();
        context.set("code", "1234");
        String html = "您的验证码是${code}";
        String str = El.render(html, context);
        assertEquals(str, assertHtml);
    }
}
