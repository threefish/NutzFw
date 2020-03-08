/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.common.action;

import com.nutzfw.core.common.annotation.SysLog;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.util.RsaUtils;
import com.nutzfw.core.common.util.WebUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.mvc.FristLoginProcessor;
import com.nutzfw.core.plugin.redis.RedisHelpper;
import com.nutzfw.core.plugin.shiro.LoginTypeEnum;
import com.nutzfw.core.plugin.shiro.QrCodeToken;
import com.nutzfw.core.plugin.shiro.UserPassToken;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.sys.biz.LoginBiz;
import com.nutzfw.modules.sys.vo.QrLoginVO;
import io.swagger.annotations.*;
import org.apache.commons.codec.binary.Hex;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.MvcI18n;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.*;

import java.security.interfaces.RSAPublicKey;

/**
 * @author huchuc@vip.qq.com
 * 创建时间: 2017/12/25  19:30
 */
@IocBean
@At("/management/")
@Api("/management/")
public class LoginManagementAction extends BaseAction {

    static final String LOGIN_ERROR_PREFIX = "loginError.";
    private final static int RSA_PASS_LENGTH = 256;
    @Inject
    private LoginBiz loginBiz;
    @Inject
    private RedisHelpper redisHelpper;
    @Inject
    private UserAccountService userAccountService;

    @GET
    @At("logout")
    @Ok("btl:WEB-INF/view/logout.html")
    @ApiOperation(value = "退出系统", nickname = "logout", tags = "系统登录", httpMethod = "GET", response = String.class)
    public void logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
    }

    @GET
    @At("login")
    @Ok("btl:WEB-INF/view/login.html")
    public NutMap loginPage() {
        return NutMap.NEW();
    }

    @POST
    @At("/getRsa")
    @Ok("json")
    public AjaxResult getRsa() {
        RSAPublicKey publicKey = RsaUtils.getDefaultPublicKey();
        String modulus = new String(Hex.encodeHex(publicKey.getModulus().toByteArray()));
        String exponent = new String(Hex.encodeHex(publicKey.getPublicExponent().toByteArray()));
        return AjaxResult.sucess(NutMap.NEW().setv("modulus", modulus).setv("exponent", exponent));
    }

    /**
     * 无权限提示页面
     */
    @At("unauthorized")
    @Ok("btl:WEB-INF/view/unauthorized.html")
    public void unauthorized() {
    }

    /**
     * 是否显示验证码
     */
    @At("checkshowCaptcha")
    @Ok("json")
    public AjaxResult checkshowCaptcha(@Param("username") String username) {
        String key = RedisHelpper.buildRediskey(LOGIN_ERROR_PREFIX).concat(Strings.sNull(username));
        if (Cons.optionsCach.getNeedVerificationCode() == 0) {
            return AjaxResult.sucess(true);
        } else if (redisHelpper.exists(key)) {
            int num = Integer.parseInt(redisHelpper.get(key));
            //错误次数满足条件需要展示验证码
            if (num >= Cons.optionsCach.getNeedVerificationCode()) {
                return AjaxResult.sucess(true);
            }
        }
        //不显示
        return AjaxResult.error("");
    }

    @Ok("json:{nullAsEmtry:true}")
    @POST
    @At("login")
    @ApiOperation(value = "登录接口", nickname = "login", tags = "系统登录", httpMethod = "POST", response = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", paramType = "query", value = "帐号", dataType = "string", required = true),
            @ApiImplicitParam(name = "password", paramType = "query", value = "密码", dataType = "string", required = true),
            @ApiImplicitParam(name = "isApp", paramType = "query", value = "是否是APP", dataType = "string", required = true),
            @ApiImplicitParam(name = "captcha", paramType = "query", value = "验证码", dataType = "string")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "{ok:true,msg:'登录成功',data:{needChangePass:false,userId:'',userName:'',realName:'',avatar:'',deptId:'',deptName:''}}"),
    })
    @SysLog(tag = "系统登录", template = "${args[0]}登录${re.ok?'成功':'失败'}", result = true)
    public AjaxResult login(@Param("username") String username, @Param("password") String password, @Param("captcha") String captcha, @Param("isApp") boolean isApp, @Param("rememberMe") boolean rememberMe) {
        String key = RedisHelpper.buildRediskey(LOGIN_ERROR_PREFIX).concat(Strings.sNull(username));
        int num = 0;
        try {
            AjaxResult ajaxResult = checkshowCaptcha(username);
            if (ajaxResult.isOk()) {
                String sessionCaptcha = Strings.sNull(getSessionAttribute(Cons.CAPTCHA_KEY));
                if (Strings.isEmpty(captcha)) {
                    return AjaxResult.error("验证码不能为空！");
                }
                if (!sessionCaptcha.equals(captcha)) {
                    Mvcs.getHttpSession().removeAttribute(Cons.CAPTCHA_KEY);
                    return AjaxResult.error("验证码错误！");
                }
            }
            if (redisHelpper.exists(key)) {
                num = Integer.parseInt(redisHelpper.get(key));
            }
            if (Strings.isBlank(username)) {
                return AjaxResult.error("账户不能为空");
            } else if (Strings.isBlank(password)) {
                return AjaxResult.error("密码不能为空");
            }
            if (num >= Cons.optionsCach.getErrorPassInputTimes()) {
                return AjaxResult.error("该账户登录错误次数过多，已被冻结登录！请15分钟后再尝试！");
            }
            if (password.length() == RSA_PASS_LENGTH) {
                password = RsaUtils.decryptStringByJs(password);
            }
            UserAccount userAccount = userAccountService.loginFind(username);
            if (userAccount == null) {
                if (log.isDebugEnabled()) {
                    return AjaxResult.error("帐号不存在,登录失败！");
                }
                return AjaxResult.error("登录失败！");
            }
            if (userAccount.isLocked()) {
                return AjaxResult.error("登录失败！帐号已被冻结！");
            }
            Sha256Hash sha = new Sha256Hash(password, userAccount.getSalt());
            if (!sha.toHex().equals(userAccount.getUserPass())) {
                if (log.isDebugEnabled()) {
                    return AjaxResult.error("密码错误！登录失败！");
                }
                return AjaxResult.error("登录失败！");
            }
            LoginTypeEnum loginType = isApp ? LoginTypeEnum.app : LoginTypeEnum.web;
            UserPassToken token = new UserPassToken(username, password, loginType, rememberMe, WebUtil.ip(Mvcs.getReq()));
            SecurityUtils.getSubject().login(token);
            NutMap data = loginBiz.doLogin(getSessionUserAccount(), loginType);
            if (data.getBoolean(LoginBiz.NEED_CHANGE_PASS)) {
                setSessionAttribute(FristLoginProcessor.NEED_FRIST_LOGIN, true);
            } else if (getSessionAttribute(FristLoginProcessor.NEED_FRIST_LOGIN) != null) {
                Mvcs.getHttpSession().removeAttribute(FristLoginProcessor.NEED_FRIST_LOGIN);
            }
            return AjaxResult.sucess(data, MvcI18n.message("login.sucess"));
        } catch (Exception e) {
            log.error("产生异常致登录失败：", e);
            int count = 1;
            if (redisHelpper.exists(key)) {
                redisHelpper.setString(key, String.valueOf(num + count), 15 * 60);
            } else {
                redisHelpper.setString(key, String.valueOf(count), 15 * 60);
            }
            return AjaxResult.error(MvcI18n.message("login.error"));
        }

    }

    @Ok("json:{nullAsEmtry:true}")
    @POST
    @At("qrLogin")
    public AjaxResult qrLogin() {
        String key = QrCodeLoginAction.QRCODE_LOGIN_PREFIX.concat(Mvcs.getHttpSession().getId());
        try {
            if (redisHelpper.exists(key)) {
                QrLoginVO qrLoginVO = redisHelpper.getByJson(key, QrLoginVO.class);
                if (qrLoginVO.getConfirmLogin()) {
                    QrCodeToken userToken = new QrCodeToken(qrLoginVO.getUserName(), WebUtil.ip(Mvcs.getReq()));
                    UserAccount userAccount = userAccountService.loginFind(userToken.getUserName());
                    if (Strings.isBlank(userToken.getUserName())) {
                        throw new AccountException("账户不能为空");
                    }
                    if (userAccount == null) {
                        throw new AccountException("登录失败！");
                    }
                    if (userAccount.isLocked()) {
                        throw new AccountException("登录失败！帐号已被冻结！");
                    }
                    SecurityUtils.getSubject().login(userToken);
                    NutMap data = loginBiz.doLogin(getSessionUserAccount(), LoginTypeEnum.QrLogin);
                    if (data.getBoolean(LoginBiz.NEED_CHANGE_PASS)) {
                        setSessionAttribute(FristLoginProcessor.NEED_FRIST_LOGIN, true);
                    } else if (getSessionAttribute(FristLoginProcessor.NEED_FRIST_LOGIN) != null) {
                        Mvcs.getHttpSession().removeAttribute(FristLoginProcessor.NEED_FRIST_LOGIN);
                    }
                    redisHelpper.del(key);
                    return AjaxResult.sucess(data, MvcI18n.message("login.sucess"));
                } else if (qrLoginVO.getScanning()) {
                    return AjaxResult.error("扫描成功");
                } else {
                    return AjaxResult.error("请使用APP扫码登录");
                }
            } else {
                return AjaxResult.error("二维码已过期");
            }
        } catch (Exception e) {
            log.error(e);
            return AjaxResult.error(MvcI18n.message("login.error"));
        }
    }
}
