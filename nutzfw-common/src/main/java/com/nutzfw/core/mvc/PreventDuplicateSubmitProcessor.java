/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.mvc;

import com.nutzfw.core.common.annotation.Token;
import com.nutzfw.core.common.annotation.TypeEnum;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.error.PreventDuplicateSubmitException;
import org.nutz.integration.jedis.RedisService;
import org.nutz.lang.Strings;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.impl.processor.AbstractProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/8/14
 * 描述此类：
 */
public class PreventDuplicateSubmitProcessor extends AbstractProcessor {

    final static String PREVENT_DUPLICATE_SUBMIT_KEY = Cons.REDIS_KEY_PREFIX.concat("PREVENT_DUPLICATE_SUBMIT_KEY.");

    /**
     * 允许直接传递 RedisService
     */
    public static RedisService redisService;

    /**
     * RedisKey 时效性 TIMELINESS 秒
     */
    public static long TIMELINESS = 30 * 60;


    @Override
    public void process(ActionContext ac) throws Throwable {
        Token token = ac.getMethod().getAnnotation(Token.class);
        if (token != null) {
            String key = PREVENT_DUPLICATE_SUBMIT_KEY
                    .concat(ac.getRequest().getSession().getId()).concat(".")
                    .concat(Strings.isEmpty(token.path()) ? ac.getPath() : token.path());
            boolean isSessionModel = redisService == null;
            if (token.type() == TypeEnum.CREATE && isSessionModel) {
                createSessionToken(ac, key);
            } else if (token.type() == TypeEnum.CREATE && !isSessionModel) {
                createRedisToken(ac, key);
            } else if (token.type() == TypeEnum.REMOVE && isSessionModel) {
                removeSessionToken(ac, key);
            } else if (token.type() == TypeEnum.REMOVE && !isSessionModel) {
                removeRedisToken(ac, key);
            }
            try {
                doNext(ac);
            } catch (Throwable ex) {
                //恢复Token
                if (token.type() == TypeEnum.REMOVE) {
                    if (isSessionModel) {
                        createSessionToken(ac, key);
                    } else {
                        createRedisToken(ac, key);
                    }
                }
                throw ex;
            }
        } else {
            doNext(ac);
        }
    }

    private void removeSessionToken(ActionContext ac, String key) throws PreventDuplicateSubmitException {
        HttpServletRequest request = ac.getRequest();
        HttpSession session = request.getSession();
        //检查是否存在键值
        Object obj = session.getAttribute(key);
        if (obj != null) {
            //存在键值进行移除即可
            session.removeAttribute(key);
        } else {
            //不存在键值
            throw new PreventDuplicateSubmitException("检测到重复提交数据或非法提交数据");
        }
    }

    private void removeRedisToken(ActionContext ac, String rediskey) throws PreventDuplicateSubmitException {
        boolean hasToken = redisService.exists(rediskey);
        if (hasToken) {
            //存在键值进行移除即可
            redisService.del(rediskey);
        } else {
            //不存在键值
            throw new PreventDuplicateSubmitException("检测到重复提交数据或非法提交数据");
        }
    }

    private void createRedisToken(ActionContext ac, String rediskey) {
        boolean hasToken = redisService.exists(rediskey);
        /**
         * 如果取NX，则只有当key不存在是才进行set，如果取XX，则只有当key已经存在时才进行set
         * EX代表秒，PX代表毫秒
         */
        if (hasToken) {
            redisService.set(rediskey, ac.getPath(), "XX", Cons.REDIS_EXPX, TIMELINESS);
        } else {
            redisService.set(rediskey, ac.getPath(), "NX", Cons.REDIS_EXPX, TIMELINESS);
        }
    }

    private void createSessionToken(ActionContext ac, String key) {
        HttpServletRequest request = ac.getRequest();
        HttpSession session = request.getSession();
        //直接覆盖可能存在的键值
        session.setAttribute(key, ac.getPath());
    }


}
