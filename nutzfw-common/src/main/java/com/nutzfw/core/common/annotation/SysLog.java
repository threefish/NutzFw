package com.nutzfw.core.common.annotation;

import java.lang.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/1/29
 * 描述此类：
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {

    /**
     * 日志标签
     *
     * @return
     */
    String tag();

    /**
     * 消息模版内容
     *
     * @return
     */
    String template() default "";

    /**
     * 记录参数信息
     *
     * @return
     */
    boolean param() default false;

    /**
     * 记录返回值信息
     *
     * @return
     */
    boolean result() default false;

}
