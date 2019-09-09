package com.nutzfw.core.common.interceptor.cache;

import com.nutzfw.core.common.annotation.CachePut;
import com.nutzfw.core.plugin.redis.RedisHelpper;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.SimpleAopMaker;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/9/6
 */
@IocBean(name = "$aop_cachePutAopLoader")
public class CachePutAopLoader extends SimpleAopMaker<CachePut> {

    @Inject
    RedisHelpper redisHelpper;

    @Override
    public List<? extends MethodInterceptor> makeIt(CachePut cache, Method method, Ioc ioc) {
        return Arrays.asList(new CachePutMethodInterceptor(cache, method, redisHelpper));
    }

}