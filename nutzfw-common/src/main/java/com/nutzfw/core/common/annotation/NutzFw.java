/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.annotation;

import java.lang.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/1/5
 * 仅供代码生成器使用
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface NutzFw {

    /**
     * 输入框
     */
    int TEXT_INPUT    = 2;
    /**
     * 多行文本框
     */
    int TEXT_TEXTAREA = 3;
    /**
     * 富文本框 百度UE
     */
    int TEXT_RICH     = 4;

    /**
     * 必填必选字段
     *
     * @return
     */
    boolean required() default false;


    /**
     * 网页中是否显示
     *
     * @return
     */
    boolean show() default true;

    /**
     * 表关联
     */
    Class<?> oneOne() default Object.class;

    /**
     * 表关联字段
     *
     * @return
     */
    String oneOneField() default "";

    /**
     * 网页样式-默认为输入框
     *
     * @return
     */
    int text() default TEXT_INPUT;

    /**
     * 是附件类型
     *
     * @return
     */
    boolean attachment() default false;

    /**
     * 附件类型-多附件-单附件
     *
     * @return
     */
    boolean attachmentMultiple() default false;

    /**
     * 附件全部是图片
     *
     * @return
     */
    boolean attachmentAllIsImg() default false;

    /**
     * 附件格式
     *
     * @return
     */
    String attachSuffix() default "";

    /**
     * 枚举字典CODE
     * <p>
     * 值若不为空将默认为字典类型
     *
     * @return
     */
    String dictCode() default "";

    /**
     * 多选字典
     *
     * @return
     */
    boolean multiDict() default false;

    /**
     * 提示信息
     *
     * @return
     */
    String placeholder() default "";

    /**
     * 文本最大长度
     *
     * @return
     */
    int maxLength() default 50;


}
