package com.nutzfw.core.common.interceptor.cache;

import com.nutzfw.core.plugin.redis.RedisHelpper;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.el.El;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

import java.lang.reflect.Method;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/9/9
 */
public abstract class AbstractCacheMethodInterceptor implements MethodInterceptor {

    protected Method method;

    protected RedisHelpper redisHelpper;

    public AbstractCacheMethodInterceptor(Method method, RedisHelpper redisHelpper) {
        this.method = method;
        this.redisHelpper = redisHelpper;
    }

    /**
     * 取得表达式值
     *
     * @param chain
     * @return
     */
    protected boolean getConditionValue(String condition, InterceptorChain chain) {
        if (Strings.isNotBlank(condition)) {
            return Boolean.parseBoolean(El.render(condition, Lang.context().set("arg", chain.getArgs())));
        }
        return true;
    }

    /**
     * 取得缓存名称
     *
     * @param chain
     * @return
     */
    protected String getCacheName(String cacheNameEl, InterceptorChain chain) {
        return El.render(cacheNameEl, Lang.context().set("arg", chain.getArgs()));
    }

}
