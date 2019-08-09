package com.nutzfw.modules.common.action;

import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.util.SCaptchaUtil;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import java.awt.image.BufferedImage;

/**
 * Created with IntelliJ IDEA.
 *
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2017/12/25  19:30
 * 描述此类：
 */
@IocBean
@At("/captcha")
public class CaptchaAction extends BaseAction {

    @At("/next")
    @Ok("raw:png")
    public BufferedImage next(@Param("w") int w, @Param("h") int h) {
        //长或宽为0?重置为默认长宽.
        if (w * h < 1) {
            w = 200;
            h = 60;
        }//排除4
        char[] codeSequence = {'0', '1', '2', '3', '5', '6', '7', '8', '9'};
        SCaptchaUtil captcha = new SCaptchaUtil(w, h, codeSequence);
        String text = captcha.getCode();
        setSessionAttribute(Cons.CAPTCHA_KEY, text);
        return captcha.getBuffImg();
    }
}
