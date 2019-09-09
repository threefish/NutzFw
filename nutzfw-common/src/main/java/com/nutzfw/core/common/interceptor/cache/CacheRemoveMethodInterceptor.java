package com.nutzfw.core.common.interceptor.cache;

import com.nutzfw.core.common.annotation.CacheRemove;
import com.nutzfw.core.plugin.redis.RedisHelpper;
import lombok.extern.slf4j.Slf4j;
import org.nutz.aop.InterceptorChain;
import org.nutz.lang.Strings;

import java.lang.reflect.Method;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/9/6
 */
@Slf4j
public class CacheRemoveMethodInterceptor extends AbstractCacheMethodInterceptor {

    private CacheRemove cachePut;

    public CacheRemoveMethodInterceptor(CacheRemove cachePut, Method method, RedisHelpper redisHelpper) {
        super(method, redisHelpper);
        this.cachePut = cachePut;
    }

    @Override
    public void filter(InterceptorChain chain) throws Throwable {
        try {
            boolean conditionValue = getConditionValue(cachePut.condition(), chain);
            if (conditionValue) {
                //条件满足可以移除缓存
                String cacheName = RedisHelpper.buildRediskey(getCacheName(cachePut.value(), chain));
                if (Strings.isBlank(cacheName)) {
                    throw new RuntimeException("缓存名称不能为空");
                }
                if (redisHelpper.exists(cacheName)) {
                    //缓存存在,直接移除
                    redisHelpper.del(cacheName);
                }
                chain.doChain();
            }
        } catch (Exception e) {
            log.error("AOP移除缓存数据时出现异常:", e);
            throw e;
        }
    }
}
