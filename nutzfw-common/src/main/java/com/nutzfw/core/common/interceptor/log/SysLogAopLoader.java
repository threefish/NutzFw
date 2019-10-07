/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.interceptor.log;

import com.nutzfw.core.common.annotation.SysLog;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.SimpleAopMaker;
import org.nutz.ioc.loader.annotation.IocBean;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/1/29
 * 描述此类：
 */
@IocBean(name = "$aop_sysLogAopLoader")
public class SysLogAopLoader extends SimpleAopMaker<SysLog> {

    @Override
    public List<? extends MethodInterceptor> makeIt(SysLog sysLog, Method method, Ioc ioc) {
        return Arrays.asList(new SysLogMethodInterceptor(sysLog, method, ioc));
    }
}