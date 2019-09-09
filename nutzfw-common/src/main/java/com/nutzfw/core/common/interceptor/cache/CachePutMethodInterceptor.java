package com.nutzfw.core.common.interceptor.cache;

import com.nutzfw.core.common.annotation.CachePut;
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
public class CachePutMethodInterceptor extends AbstractCacheMethodInterceptor {

    private CachePut cachePut;

    public CachePutMethodInterceptor(CachePut cachePut, Method method, RedisHelpper redisHelpper) {
        super(method, redisHelpper);
        this.cachePut = cachePut;
    }

    @Override
    public void filter(InterceptorChain chain) throws Throwable {
        try {
            boolean conditionValue = getConditionValue(cachePut.condition(), chain);
            if (conditionValue) {
                //条件满足可以进行缓存
                String cacheName = RedisHelpper.buildRediskey(getCacheName(cachePut.value(), chain));
                if (Strings.isBlank(cacheName)) {
                    throw new RuntimeException("缓存名称不能为空");
                }
                if (redisHelpper.exists(cacheName)) {
                    //缓存存在,直接返回
                    chain.setReturnValue(redisHelpper.getBySerializable(cacheName));
                } else {
                    //不存在,执行源方法,取得返回值并存入缓存
                    chain.doChain();
                    redisHelpper.setNXSerializable(cacheName, chain.getReturn(), RedisHelpper.DEFAULT_SECOND);
                }
            }
        } catch (Exception e) {
            log.error("AOP缓存数据时出现异常:", e);
            throw e;
        }
    }
}
