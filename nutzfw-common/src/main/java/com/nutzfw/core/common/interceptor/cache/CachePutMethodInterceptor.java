/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.interceptor.cache;

import com.nutzfw.core.common.annotation.CachePut;
import com.nutzfw.core.plugin.redis.RedisHelpper;
import lombok.extern.slf4j.Slf4j;
import org.nutz.aop.InterceptorChain;
import org.nutz.lang.Strings;

import java.lang.reflect.Method;
import java.util.concurrent.ThreadLocalRandom;

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
                    //缓存增加随机失效时间
                    redisHelpper.setNXSerializable(cacheName, chain.getReturn(), RedisHelpper.DEFAULT_SECOND + ThreadLocalRandom.current().nextInt(500));
                }
            }
        } catch (Exception e) {
            log.error("AOP缓存数据时出现异常:", e);
            throw e;
        }
    }
}
