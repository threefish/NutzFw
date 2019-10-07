/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.delegate;

import org.flowable.bpmn.model.MapExceptionEntry;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.bpmn.behavior.ServiceTaskJavaDelegateActivityBehavior;
import org.flowable.engine.impl.bpmn.helper.ClassDelegate;
import org.flowable.engine.impl.bpmn.parser.FieldDeclaration;
import org.flowable.engine.impl.delegate.ActivityBehavior;
import org.nutz.ioc.Ioc;

import java.util.List;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/8/8
 */
public class CustomClassDelegate extends ClassDelegate {

    private Ioc ioc;

    public CustomClassDelegate(Ioc ioc, String id, String className, List<FieldDeclaration> fieldDeclarations, boolean triggerable, Expression skipExpression, List<MapExceptionEntry> mapExceptions) {
        super(id, className, fieldDeclarations, triggerable, skipExpression, mapExceptions);
        this.ioc = ioc;
    }

    public CustomClassDelegate(Ioc ioc, String className, List<FieldDeclaration> fieldDeclarations) {
        super(className, fieldDeclarations);
        this.ioc = ioc;
    }

    @Override
    protected ActivityBehavior getActivityBehaviorInstance() {
        Object delegateInstance;
        if (className.startsWith("$ioc:")) {
            try {
                delegateInstance = ioc.get(JavaDelegate.class, className.substring(5));
            } catch (Exception e) {
                throw new RuntimeException("Ioc中找不到" + className);
            }
        } else {
            delegateInstance = instantiateDelegate(className, fieldDeclarations);
        }
        if (delegateInstance instanceof ActivityBehavior) {
            return determineBehaviour((ActivityBehavior) delegateInstance);
        } else if (delegateInstance instanceof JavaDelegate) {
            return determineBehaviour(new ServiceTaskJavaDelegateActivityBehavior((JavaDelegate) delegateInstance, triggerable, skipExpression));
        } else {
            throw new FlowableIllegalArgumentException(delegateInstance.getClass().getName() + " doesn't implement " + JavaDelegate.class.getName() + " nor " + ActivityBehavior.class.getName());
        }
    }
}
