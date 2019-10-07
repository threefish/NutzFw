/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.interceptor.cache;

import com.nutzfw.core.common.annotation.CacheRemove;
import com.nutzfw.core.plugin.redis.RedisHelpper;
import lombok.extern.slf4j.Slf4j;
import org.nutz.aop.InterceptorChain;
import org.nutz.lang.Strings;

import java.lang.reflect.Method;
import java.util.Set;

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
                Set<String> keys = redisHelpper.keys(cacheName);
                if (keys.size() > 0) {
                    redisHelpper.del(keys.toArray(new String[0]));
                }
                chain.doChain();
            }
        } catch (Exception e) {
            log.error("AOP移除缓存数据时出现异常:", e);
            throw e;
        }
    }
}
