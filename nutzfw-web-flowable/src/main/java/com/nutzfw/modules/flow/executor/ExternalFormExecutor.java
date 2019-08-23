package com.nutzfw.modules.flow.executor;

import com.nutzfw.core.plugin.flowable.vo.FlowTaskVO;
import com.nutzfw.modules.organize.entity.UserAccount;

import java.util.Map;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/6/20
 */
public interface ExternalFormExecutor {

    /**
     * 开始流程
     *
     * @param formData
     * @param flowTaskVO
     * @param sessionUserAccount
     * @return businessId 业务流水号
     */
    String start(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount);


    /**
     * 用户审核
     *
     * @param formData
     * @param flowTaskVO
     * @param sessionUserAccount
     * @return 错误消息
     */
    String userAudit(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount);

    /**
     * 回退
     *
     * @param formData
     * @param flowTaskVO
     * @param sessionUserAccount
     * @return 错误消息
     */
    String backToStep(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount);

    /**
     * 加签
     *
     * @param formData
     * @param flowTaskVO
     * @param sessionUserAccount
     * @return 错误消息
     */
    String addMultiInstance(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount);

    /**
     * 加载表单数据
     *
     * @param flowTaskVO
     * @param sessionUserAccount
     * @return 表单数据
     */
    Object loadFormData(FlowTaskVO flowTaskVO, UserAccount sessionUserAccount);

    /**
     * 加载表单页面
     *
     * @param flowTaskVO
     * @return 表单路径
     */
    String getFormPage(FlowTaskVO flowTaskVO);

}
