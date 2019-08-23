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
