package com.nutzfw.modules.flow.executor;

import com.nutzfw.core.plugin.flowable.vo.FlowTaskVO;
import com.nutzfw.modules.organize.entity.UserAccount;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.Map;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/6/20
 * 表单执行器的默认实现
 */
@IocBean(name = "defaualtExternalFormExecutor")
public class DefaualtExternalFormExecutor implements ExternalFormExecutor {

    @Override
    public String start(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        return null;
    }

    @Override
    public String userAudit(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        return null;
    }

    @Override
    public String backToStep(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        return null;
    }

    @Override
    public String addMultiInstance(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        return null;
    }

    @Override
    public Object loadFormData(FlowTaskVO flowTaskVO, UserAccount sessionUserAccount) {
        return null;
    }


    @Override
    public String getFormPage(FlowTaskVO flowTaskVO) {
        return null;
    }


}
