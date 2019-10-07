/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
