/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.mvc;

import com.nutzfw.core.mvc.interceptor.RequestInterceptor;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.impl.processor.AbstractProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author huchuc@vip.qq.com
 * @date: 2021/10/19
 */
public class RequestInterceptorProcessor extends AbstractProcessor {

    private final List<RequestInterceptor> requestInterceptors = new ArrayList<>();

    @Override
    public void process(ActionContext ac) throws Throwable {
        if (requestInterceptors.isEmpty()) {
            String[] namesByType = ac.getIoc().getNamesByType(RequestInterceptor.class);
            Arrays.stream(namesByType).forEach(name -> requestInterceptors.add(ac.getIoc().get(RequestInterceptor.class, name)));
            Collections.sort(requestInterceptors, (o1, o2) -> o1.getOrder() > o2.getOrder() ? 0 : -1);
        }
        requestInterceptors.stream().forEach(requestInterceptor -> requestInterceptor.before(ac.getRequest(), ac.getResponse()));
        doNext(ac);
        requestInterceptors.stream().forEach(requestInterceptor -> requestInterceptor.after(ac.getRequest(), ac.getResponse()));
    }
}
