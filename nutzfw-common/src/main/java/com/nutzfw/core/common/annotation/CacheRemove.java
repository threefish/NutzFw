package com.nutzfw.core.common.annotation;

import java.lang.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/9/06
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheRemove {

    /**
     * cacheName
     * el 表达式生成缓存名称
     *
     * @return
     */
    String value();

    /**
     * el 表达式计算条件
     * true 移除缓存
     * false 不移除
     *
     * @return
     */
    String condition() default "";
}
