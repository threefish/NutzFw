/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.front.action;

import com.nutzfw.core.common.util.ElUtil;
import com.nutzfw.core.common.util.RegexUtil;
import com.nutzfw.core.common.util.SCaptchaUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.plugin.redis.RedisHelpper;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.sys.biz.DictBiz;
import com.nutzfw.modules.sys.entity.MailBody;
import com.nutzfw.modules.sys.service.MailBodyService;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import java.awt.image.BufferedImage;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/5/29
 */
@IocBean
@At("/front/user/")
public class UserRegisterAction extends BaseAction {

    /**
     * 注册验证码
     */
    public static final String REGISTER_CAPTCHA_KEY = "REGISTER_CAPTCHA_KEY";
    @Inject
    UserAccountService userAccountService;
    @Inject
    RedisHelpper       redisHelpper;
    @Inject
    MailBodyService    mailBodyService;
    @Inject("java:$conf.get('email.subject')")
    String             subject;
    @Inject("java:$conf.get('email.htmlTemplate')")
    String             htmlTemplate;
    @Inject
    DictBiz            dictBiz;

    @At("register")
    @Ok("btl:WEB-INF/view/modules/front/user/register.html")
    public NutMap register() {
        return NutMap.NEW();
    }

    @At("imgVerificationCode")
    @Ok("raw:png")
    public BufferedImage imgVerificationCode(@Param("w") int w, @Param("h") int h) {
        //长或宽为0?重置为默认长宽.
        if (w * h < 1) {
            w = 200;
            h = 60;
        }//排除4
        char[] codeSequence = {'0', '1', '2', '3', '5', '6', '7', '8', '9'};
        SCaptchaUtil captcha = new SCaptchaUtil(w, h, codeSequence);
        String text = captcha.getCode();
        setSessionAttribute(REGISTER_CAPTCHA_KEY, text);
        return captcha.getBuffImg();
    }

    /**
     * 发送验证码
     *
     * @return
     */
    @At("sendVerificationCode")
    @Ok("json")
    public AjaxResult sendVerificationCode(@Param("mail") String mail, @Param("imgCode") String imgCode) {
        if (!RegexUtil.isEmail(Strings.sNull(mail))) {
            return AjaxResult.error("邮箱格式错误！");
        }
        if (Strings.isBlank(imgCode) || !Strings.sNull(getSessionAttribute(REGISTER_CAPTCHA_KEY)).equals(imgCode)) {
            return AjaxResult.error("图片验证码错误！");
        }
        String verificationCode = R.UU16().substring(0, 6);
        String effectivenessKey = RedisHelpper.buildRediskey(("register:").concat(mail));
        if (redisHelpper.exists(effectivenessKey)) {
            return AjaxResult.error("获取验证码太频繁，请1分钟后再试");
        } else {
            Mvcs.getReq().getSession().removeAttribute(REGISTER_CAPTCHA_KEY);
            redisHelpper.setString(effectivenessKey, verificationCode, 5);
            String codeKey = RedisHelpper.buildRediskey(("register:").concat("code:").concat(mail));
            redisHelpper.setString(codeKey, verificationCode, 60 * 30);
            //下面逻辑可以更换为保存短信验证码信息
            mailBodyService.insert(MailBody.builder()
                    .bizType(dictBiz.getCacheDict("sys_mail_type", "register").getId())
                    .subject(subject)
                    .htmlMsg(ElUtil.render(htmlTemplate, Lang.context().set("code", verificationCode)))
                    .to(mail)
                    .build());
            return AjaxResult.sucessMsg("已发送，请注意查收");
        }
    }

    @POST
    @At("register")
    @Ok("json")
    public AjaxResult doRegister(@Param("realName") String realName,
                                 @Param("userName") String userName,
                                 @Param("userPass") String userPass,
                                 @Param("repeatPass") String repeatPass,
                                 @Param("gender") int gender,
                                 @Param("phone") String phone,
                                 @Param("mail") String mail,
                                 @Param("verificationCode") String verificationCode) {
        if (Strings.isBlank(userName) || Strings.isBlank(phone) || Strings.isBlank(mail) || Strings.isBlank(realName) || Strings.isBlank(verificationCode)) {
            return AjaxResult.error("必填项不能为空！");
        }
        if (!(Strings.isNotBlank(userPass) && Strings.isNotBlank(repeatPass) && userPass.equals(repeatPass))) {
            return AjaxResult.error("两次密码不相同！");
        }
        if (!RegexUtil.isEmail(mail)) {
            return AjaxResult.error("邮箱格式错误！");
        }
        if (!RegexUtil.isPhone(phone)) {
            return AjaxResult.error("手机号格式错误！");
        }
        if (gender < 0) {
            return AjaxResult.error("请选择性别！");
        }
        String codeKey = RedisHelpper.buildRediskey(("register:").concat("code:").concat(mail));
        String code = redisHelpper.get(codeKey);
        if (Strings.isBlank(code) || !code.equals(verificationCode)) {
            return AjaxResult.error("验证码无效");
        }
        if (userAccountService.userNameExist(userName)) {
            return AjaxResult.error("用户名已经存在！");
        } else {
            String salt = R.UU16();
            userAccountService.insert(
                    UserAccount.builder().realName(realName).userName(userName)
                            .userPass(new Sha256Hash(userPass, salt).toHex())
                            .salt(salt)
                            .review(1).gender(gender).phone(phone).mail(mail)
                            .reviewOpinion("自主注册")
                            .build());
            redisHelpper.del(codeKey);
        }
        return AjaxResult.sucess();
    }

}
