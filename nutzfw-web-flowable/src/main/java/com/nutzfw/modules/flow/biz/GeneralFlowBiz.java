/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.flow.biz;

import com.nutzfw.core.plugin.flowable.dto.FlowSubmitInfoDTO;
import com.nutzfw.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.nutzfw.core.plugin.flowable.vo.FlowTaskVO;
import com.nutzfw.modules.organize.entity.UserAccount;
import org.nutz.lang.util.NutMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/6/20
 */
public interface GeneralFlowBiz {

    String getFormPage(FlowTaskVO flowTaskVO);

    String start(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount, Set<String> sessionUserRoleCodes);

    String backToStep(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount);

    String addMultiInstance(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount);

    String userAudit(Map formData, FlowTaskVO flowTaskVO, UserAccount sessionUserAccount);

    Object loadFormData(FlowTaskVO flowTaskVO, UserAccount sessionUserAccount);

    String getFlowName(FlowTaskVO flowTaskVO);


    /**
     * 查询流程节点全部待审核人
     *
     * @param taskExtensionDTO
     * @param flowSubmitInfoDTO 流程发起者信息
     * @return
     */
    List<NutMap> listUserTaskNodeAllReviewerUser(UserTaskExtensionDTO taskExtensionDTO, FlowSubmitInfoDTO flowSubmitInfoDTO);
}
