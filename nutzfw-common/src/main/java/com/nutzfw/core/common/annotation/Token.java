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
 * @date: 2018/8/14
 * 描述此类：
 * Token必须是成对出现的，一个创建，一个移除
 * <p>
 * 在Remove类型下捕获到任何异常都将恢复Token
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Token {
    /**
     * 类型：创建Token或者移除Token
     *
     * @return
     */
    TypeEnum type() default TypeEnum.CREATE;

    /**
     * 访问地址
     * <p>
     * 为空则自动获取当前地址，创建Token和移除Token的地址必须一致
     *
     * @return
     */
    String path() default "";

}
