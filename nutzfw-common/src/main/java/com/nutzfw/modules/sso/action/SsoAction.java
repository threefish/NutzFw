package com.nutzfw.modules.sso.action;

import com.nutzfw.core.common.util.WebUtil;
import com.nutzfw.core.plugin.redis.RedisHelpper;
import com.nutzfw.core.plugin.shiro.SsoToken;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.sso.util.SsoUtil;
import com.nutzfw.modules.sso.vo.SsoResultVO;
import com.nutzfw.modules.sso.vo.SsoUserVO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.UUID;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/3/29
 */
@IocBean
@At("/SSO")
public class SsoAction extends BaseAction {


    @Inject
    RedisHelpper redisHelpper;
    @Inject
    UserAccountService userAccountService;
    @Inject("java:$conf.get('sso.uniqueCode')")
    private String uniqueCode;
    @Inject("java:$conf.get('sso.validatorUrl')")
    private String validatorUrl;
    @Inject("java:$conf.get('sso.authUrl')")
    private String authUrl;

    /**
     * 主系统--- 根据当前登录账户的信息 前往第三方系统
     */
    @GET
    @At("/createToken")
    public void createToken() throws IOException {
        //session中取得用户信息 并生成 短效token
        String token = UUID.randomUUID().toString();
        //将token放入redis中设置时间1分钟失效
        redisHelpper.setJsonString(token, new SsoUserVO(getSessionUserAccount().getUserName(), ""), 60 * 1000);
        Mvcs.getResp().sendRedirect(MessageFormat.format(authUrl, token, uniqueCode));
    }

    /**
     * 单点登录服务器请求认证的地址
     * 主系统--- 验证并返回用户名
     */
    @At("/Validator")
    @GET
    @Ok("json")
    public SsoResultVO validator(@Param(value = "token") String tokenid,
                                 @Param(value = "uniqueCode") String uniqueCode) {
        if ("".equals(tokenid) || "".equals(uniqueCode)) {
            return SsoResultVO.error("参数不完整");
        }
        if (!redisHelpper.exists(tokenid)) {
            return SsoResultVO.error("验证信息已过期！");
        }
        SsoUserVO user = redisHelpper.getByJson(tokenid, SsoUserVO.class);
        //执行验证tokenid逻辑
        return SsoResultVO.sucess(user.getUserName(), user.getIdCard());
    }

    /**
     * 单点登录用户认证的地址
     * 从系统请求我系统-我系统得到token通过后台http访问从系统进行认证，认证成功执行登录逻辑，认证失败返回消息
     */
    @At("/auth")
    @GET
    @Ok("btl:WEB-INF/view/error/ssoAuthFail.html")
    public NutMap auth(@Param(value = "token") String token, @Param(value = "uniqueCode") String uniqueCode) throws IOException {
        NutMap data = new NutMap();
        if ("".equals(token) || "".equals(uniqueCode)) {
            data.setv("errmsg", "参数不完整");
        } else {
            Object re = SsoUtil.httpAuth(validatorUrl, token, uniqueCode);
            if (re instanceof SsoResultVO) {
                SsoResultVO result = (SsoResultVO) re;
                UserAccount userAccount = userAccountService.loginFind(result.getUserName());
                if (userAccount == null) {
                    data.setv("errmsg", "登录失败！单点登录帐号信息不符！");
                } else if (userAccount.isLocked()) {
                    data.setv("errmsg", "登录失败！帐号已被冻结！");
                } else {
                    SsoToken ssoToken = new SsoToken(result.getUserName(), WebUtil.ip(Mvcs.getReq()));
                    Subject subject = SecurityUtils.getSubject();
                    subject.login(ssoToken);
                    Mvcs.getResp().sendRedirect("/main/platform");
                    return null;
                }
            } else {
                data.setv("errmsg", re);
            }
        }
        return data;
    }
}
