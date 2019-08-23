package com.nutzfw.core.plugin.flowable.factory;

import com.nutzfw.core.plugin.flowable.behavior.CustomNoneEndEventActivityBehavior;
import com.nutzfw.core.plugin.flowable.behavior.CustomNoneStartEventActivityBehavior;
import com.nutzfw.core.plugin.flowable.behavior.CustomUserTaskActivityBehavior;
import com.nutzfw.modules.organize.service.DepartmentLeaderService;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.ServiceTask;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.impl.bpmn.behavior.NoneEndEventActivityBehavior;
import org.flowable.engine.impl.bpmn.behavior.NoneStartEventActivityBehavior;
import org.flowable.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.flowable.engine.impl.bpmn.helper.ClassDelegate;
import org.flowable.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;
import org.nutz.ioc.Ioc;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/3
 */
public class CustomDefaultActivityBehaviorFactory extends DefaultActivityBehaviorFactory {

    DepartmentLeaderService departmentLeaderService;

    CustomClassDelegateFactory classDelegateFactory;

    Ioc ioc;

    public CustomDefaultActivityBehaviorFactory(DepartmentLeaderService departmentLeaderService, Ioc ioc) {
        this.ioc = ioc;
        this.departmentLeaderService = departmentLeaderService;
        this.classDelegateFactory = new CustomClassDelegateFactory(ioc);
        if (this.departmentLeaderService == null) {
            throw new RuntimeException("departmentLeaderService 不能为null!");
        }
    }

    @Override
    public UserTaskActivityBehavior createUserTaskActivityBehavior(UserTask userTask) {
        return new CustomUserTaskActivityBehavior(userTask, departmentLeaderService);
    }

    /**
     * 无特定触发器的启动事件
     *
     * @param startEvent
     * @return
     */
    @Override
    public NoneStartEventActivityBehavior createNoneStartEventActivityBehavior(StartEvent startEvent) {
        return new CustomNoneStartEventActivityBehavior();
    }

    /**
     * 无特定触发器的结束事件
     *
     * @param endEvent
     * @return
     */
    @Override
    public NoneEndEventActivityBehavior createNoneEndEventActivityBehavior(EndEvent endEvent) {
        return new CustomNoneEndEventActivityBehavior();
    }


    @Override
    public ClassDelegate createClassDelegateServiceTask(ServiceTask serviceTask) {
        return classDelegateFactory.create(serviceTask.getId(), serviceTask.getImplementation(),
                createFieldDeclarations(serviceTask.getFieldExtensions()),
                serviceTask.isTriggerable(),
                getSkipExpressionFromServiceTask(serviceTask), serviceTask.getMapExceptions());
    }

}
