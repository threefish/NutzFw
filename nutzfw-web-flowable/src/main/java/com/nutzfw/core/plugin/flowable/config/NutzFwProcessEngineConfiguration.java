/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/11/24 12:10:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.config;

import com.nutzfw.core.plugin.flowable.config.listener.NutzFwProcessEngineLifecycleListener;
import com.nutzfw.core.plugin.flowable.elbeans.IocElBeans;
import com.nutzfw.core.plugin.flowable.factory.CustomDefaultActivityBehaviorFactory;
import com.nutzfw.core.plugin.flowable.interceptor.CustomCreateUserTaskInterceptor;
import com.nutzfw.core.plugin.flowable.listener.ProccessStratAndCompletedListener;
import com.nutzfw.core.plugin.flowable.listener.ProxyFlowableEventListener;
import com.nutzfw.core.plugin.flowable.listener.handle.TastCreateSetCategoryHandle;
import com.nutzfw.core.plugin.flowable.transaction.NutzTransactionFactory;
import com.nutzfw.core.plugin.flowable.util.FlowStrongUuidGenerator;
import com.nutzfw.modules.organize.service.DepartmentLeaderService;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.impl.history.HistoryLevel;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Encoding;

import javax.sql.DataSource;
import java.util.*;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/30
 */
@IocBean
public class NutzFwProcessEngineConfiguration extends StandaloneProcessEngineConfiguration {

    @Inject("refer:$ioc")
    Ioc ioc;
    @Inject
    DataSource dataSource;
    @Inject
    TastCreateSetCategoryHandle tastCreateSetCategoryHandle;
    @Inject
    DepartmentLeaderService departmentLeaderService;
    @Inject
    CustomCreateUserTaskInterceptor customCreateUserTaskInterceptor;

    /**
     * 变量与父类变量重名如果不覆盖 setDataSource 方法，注入 dataSource 时会导致当前类的dataSource为null
     *
     * @param dataSource
     * @return
     */
    @Override
    public ProcessEngineConfiguration setDataSource(DataSource dataSource) {
        super.dataSource = dataSource;
        this.dataSource = dataSource;
        return this;
    }

    @Override
    public ProcessEngine buildProcessEngine() {
        this.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        this.setActivityFontName("黑体");
        this.setLabelFontName("黑体");
        this.setAnnotationFontName("黑体");
        this.setXmlEncoding(Encoding.UTF8);
        this.setEnableSafeBpmnXml(false);
        this.setHistoryLevel(HistoryLevel.AUDIT);
        //设置禁用Idm引擎
        this.setDisableIdmEngine(true);
        this.setIdGenerator(new FlowStrongUuidGenerator());
        this.setTransactionsExternallyManaged(true);
        this.setTransactionFactory(new NutzTransactionFactory());
        this.setProcessEngineLifecycleListener(new NutzFwProcessEngineLifecycleListener());
        this.setEventListeners(this.getGlobalFlowableEventListener());
        //自定义行为类工厂
        this.activityBehaviorFactory = new CustomDefaultActivityBehaviorFactory(departmentLeaderService, ioc);
        this.setCreateUserTaskInterceptor(customCreateUserTaskInterceptor);
        this.initElBeans();
        return super.buildProcessEngine();
    }

    /**
     * 设置全局事件监听器
     *
     * @return
     */
    private List<FlowableEventListener> getGlobalFlowableEventListener() {
        return Arrays.asList(
                new ProxyFlowableEventListener(FlowableEngineEventType.TASK_CREATED, Arrays.asList(tastCreateSetCategoryHandle)),
                new ProccessStratAndCompletedListener()
        );
    }


    /**
     * 注册 flowable el bean
     */
    public void initElBeans() {
        String[] namesByType = ioc.getNamesByType(IocElBeans.class);
        if (Objects.nonNull(namesByType) && namesByType.length > 0) {
            Map beansMap = new HashMap(namesByType.length);
            for (String name : namesByType) {
                beansMap.put(name, ioc.get(IocElBeans.class, name));
            }
            this.setBeans(beansMap);
        }
    }

}
