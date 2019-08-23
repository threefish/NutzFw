package com.nutzfw.core.plugin.flowable.config;

import com.nutzfw.core.plugin.flowable.config.listener.NutzFwProcessEngineLifecycleListener;
import com.nutzfw.core.plugin.flowable.factory.CustomDefaultActivityBehaviorFactory;
import com.nutzfw.core.plugin.flowable.listener.ProxyFlowableEventListener;
import com.nutzfw.core.plugin.flowable.listener.handle.TaskMessageNoticeHandle;
import com.nutzfw.core.plugin.flowable.listener.handle.TastCreateSetCategoryHandle;
import com.nutzfw.core.plugin.flowable.transaction.NutzTransactionFactory;
import com.nutzfw.core.plugin.flowable.util.FlowStrongUuidGenerator;
import com.nutzfw.modules.organize.service.DepartmentLeaderService;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.impl.cfg.TransactionState;
import org.flowable.common.engine.impl.history.HistoryLevel;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Encoding;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/4/30
 */
@IocBean
public class NutzFwProcessEngineConfiguration extends StandaloneProcessEngineConfiguration {

    @Inject("refer:$ioc")
    protected Ioc ioc;
    @Inject
    DataSource dataSource;
    @Inject
    TastCreateSetCategoryHandle tastCreateSetCategoryHandle;
    @Inject
    TaskMessageNoticeHandle taskMessageNoticeHandle;
    @Inject
    DepartmentLeaderService departmentLeaderService;

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
        return super.buildProcessEngine();
    }


    /**
     * 设置全局事件监听器
     *
     * @return
     */
    private List<FlowableEventListener> getGlobalFlowableEventListener() {
        List<FlowableEventListener> list = new ArrayList<>();
        list.add(new ProxyFlowableEventListener(FlowableEngineEventType.TASK_CREATED, Arrays.asList(tastCreateSetCategoryHandle)));
        list.add(new ProxyFlowableEventListener(FlowableEngineEventType.TASK_ASSIGNED, TransactionState.COMMITTED, Arrays.asList(taskMessageNoticeHandle)));
        list.add(new ProxyFlowableEventListener(FlowableEngineEventType.TASK_COMPLETED, TransactionState.COMMITTED, Arrays.asList(taskMessageNoticeHandle)));
        list.add(new ProxyFlowableEventListener(FlowableEngineEventType.TASK_OWNER_CHANGED, TransactionState.COMMITTED, Arrays.asList(taskMessageNoticeHandle)));
        return list;
    }


}
