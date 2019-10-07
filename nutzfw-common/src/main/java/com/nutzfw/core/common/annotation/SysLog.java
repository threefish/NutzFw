/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
