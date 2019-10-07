/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.business.leave.delegate;

import com.nutzfw.core.plugin.flowable.delegate.AbstractJavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/8
 * 自动化任务-- 可以推送、发邮件、发通知、数据入库、等等一系列自动化任务 -- 此类是一个demo 仅供参考
 */
@IocBean(args = {"refer:$ioc"}, name = "leaveAutomatedTaskJava")
public class LeaveAutomatedTaskJavaDelegate extends AbstractJavaDelegate {

    public LeaveAutomatedTaskJavaDelegate(Ioc ioc) {
        super(ioc);
    }

    /**
     * @param execution     原始执行信息
     * @param businessKeyId 业务信息
     */
    @Override
    public void execute(DelegateExecution execution, String businessKeyId) {
        System.out.println(businessKeyId);
    }

}
