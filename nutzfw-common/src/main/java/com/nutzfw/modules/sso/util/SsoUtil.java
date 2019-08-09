package com.nutzfw.modules.sso.util;

import com.nutzfw.core.common.util.HttpClientUtil;
import com.nutzfw.modules.sso.vo.SsoResultVO;
import org.nutz.json.Json;
import org.nutz.lang.Strings;

import java.text.MessageFormat;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/3/29
 */
public class SsoUtil {

    /**
     * 发送http验证请求取得单点登录人员信息
     *
     * @param validatorUrl
     * @param token
     * @param uniqueCode
     * @return
     */
    public static Object httpAuth(String validatorUrl, String token, String uniqueCode) {
        try {
            String url = MessageFormat.format(validatorUrl, token, uniqueCode);
            HttpClientUtil.Response res = HttpClientUtil.get(url);
            if (res.isOk()) {
                SsoResultVO result = Json.fromJson(SsoResultVO.class, res.getContent());
                if (result.isSuccess()) {
                    if (Strings.isBlank(result.getUserName())) {
                        return "用户名不存在";
                    } else {
                        return result;
                    }
                } else {
                    return result.getErrorMessage();
                }
            } else {
                return "发送认证请求失败！可能是对方服务器无应答！";
            }
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }
}
