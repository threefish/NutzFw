/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.vo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.MessageFormat;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author 黄川
 * Date Time: 2016/4/2313:32
 *
 */
@Data
@Builder
@NoArgsConstructor
public class AjaxResult<T> {
    /**
     * 是否成功
     */
    private boolean ok;

    /**
     * 错误消息提示
     */
    @Builder.Default
    private String  msg = "";
    /**
     * 返回的数据内容
     */
    private T       data;
    /**
     * 方便的返回map数据
     */
    private HashMap value;

    public AjaxResult(boolean ok, String msg) {
        this.msg = msg;
        this.ok = ok;
    }

    public AjaxResult(boolean ok, String msg, T data) {
        this.ok = ok;
        this.msg = msg;
        this.data = data;
    }

    public AjaxResult(boolean ok, String msg, T data,HashMap value) {
        this.ok = ok;
        this.msg = msg;
        this.data = data;
        this.value = value;
    }

    public static AjaxResult sucess() {
        return new AjaxResult(true, "操作成功");
    }

    /**
     * 快速返回数据而不需要新建Map
     *
     * @return
     */
    public static AjaxResult sucessMap() {
        AjaxResult result = new AjaxResult(true, "操作成功");
        result.setValue(new HashMap(6));
        return result;
    }

    /**
     * 链式调用 AjaxResult.sucessMap().setv("","").setv("","");
     *
     * @param key
     * @param val
     * @return
     */
    public AjaxResult setv(String key, Object val) {
        this.value.put(key, val);
        return this;
    }

    public static AjaxResult sucess(Object data) {
        AjaxResult result = new AjaxResult(true, "操作成功");
        result.setData(data);
        return result;
    }

    public static AjaxResult sucessMsg(String msg) {
        AjaxResult result = new AjaxResult(true, msg);
        return result;
    }

    public static AjaxResult sucess(Object data, String msg) {
        AjaxResult result = new AjaxResult(true, msg);
        result.setData(data);
        return result;
    }

    public static AjaxResult error(String msg) {
        AjaxResult result = new AjaxResult(false, msg);
        return result;
    }

    public static AjaxResult errorf(String msg, Object... object) {
        AjaxResult result = new AjaxResult(false, MessageFormat.format(msg, object));
        return result;
    }

}
