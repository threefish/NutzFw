/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/14
 * 描述此类：自动生成菜单或资源
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoCreateMenuAuth {

    int MENU = 0;

    int RESOURCE = 1;

    /**
     * 菜单名称 或者 权限名称
     *
     * @return
     */
    String name();

    /**
     * 0 菜单  1 按钮或资源
     *
     * @return
     */
    int type() default AutoCreateMenuAuth.MENU;

    /**
     * 图标
     *
     * @return
     */
    String icon() default "fa-th-list";

    /**
     * 权限标识
     * <p>
     * 留空将自动扫描shiro的RequiresPermissions标签
     *
     * @return
     */
    String permission() default "";


    /**
     * 上级权限的标识
     * <p>
     * 留空表明自己是根节点
     *
     * @return
     */
    String parentPermission() default "";


    /**
     * 菜单地址，默认读取@At
     *
     * @return
     */
    String atPath() default "";

    /**
     * 菜单排序
     *
     * @return
     */
    int shortNo() default 0;
}
