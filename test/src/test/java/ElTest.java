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
