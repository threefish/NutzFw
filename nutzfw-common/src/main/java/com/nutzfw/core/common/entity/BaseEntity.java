/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.entity;

import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.modules.organize.entity.UserAccount;
import lombok.Getter;
import lombok.Setter;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2018/2/6  18:10
 * 描述此类：所有实体类的基类
 * 所有属性会自动赋值，不需要编码赋值
 */
@Getter
@Setter
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column
    @Comment("创建人ID")
    @PrevInsert(els = @EL("$me.uid()"))
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String opBy;

    @Column
    @Comment("创建时间")
    @PrevInsert(now = true)
    @ColDefine(type = ColType.TIMESTAMP)
    private Timestamp opAt;


    @Column
    @Comment("创建人")
    @PrevInsert(els = @EL("$me.uName()"))
    private String opByDesc;

    @Column
    @Comment("删除标记")
    @PrevInsert(els = @EL("$me.flag()"))
    @ColDefine(type = ColType.BOOLEAN)
    @Default("0")
    private Boolean delFlag;

    @Override
    public String toString() {
        return String.format("/*%s*/%s", super.toString(), Json.toJson(this, JsonFormat.compact()));
    }

    /**
     * 默认未删除
     *
     * @return
     */
    public Boolean flag() {
        return false;
    }

    /**
     * 取得当前操作用户ID
     *
     * @return
     */
    public String uid() {
        try {
            UserAccount user = (UserAccount) Mvcs.getHttpSession().getAttribute(Cons.SESSION_USER_KEY);
            return String.valueOf(user.getId());
        } catch (Exception e) {
        }
        return Strings.sNull(this.opBy);
    }

    /**
     * 取得当前操作用户姓名
     * realName
     *
     * @return
     */
    public String uName() {
        try {
            UserAccount user = (UserAccount) Mvcs.getHttpSession().getAttribute(Cons.SESSION_USER_KEY);
            return String.valueOf(user.getRealName());
        } catch (Exception e) {
        }
        return Strings.sNull(this.opByDesc);
    }

    /**
     * 取得当前操作用户姓名
     *
     * @return
     */
    public String userName() {
        try {
            UserAccount user = (UserAccount) Mvcs.getHttpSession().getAttribute(Cons.SESSION_USER_KEY);
            return String.valueOf(user.getUserName());
        } catch (Exception e) {
        }
        return Strings.sNull(this.opByDesc);
    }
}
