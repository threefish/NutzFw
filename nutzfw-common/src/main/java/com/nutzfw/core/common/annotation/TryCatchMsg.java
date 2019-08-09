package com.nutzfw.core.common.annotation;

import java.lang.annotation.*;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/29
 * ajax Mvc中捕获异常友好输出
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TryCatchMsg {

    /**
     * 消息模版内容
     * <p>
     * 如需显示错误消息：操作失败！${errorMsg}
     *
     * @return
     */
    String value() default "操作失败！";
}
