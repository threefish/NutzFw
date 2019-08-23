package com.nutzfw.modules.flow.service;

import com.nutzfw.core.plugin.flowable.dto.FlowSubmitInfoDTO;
import org.nutz.lang.util.NutMap;

import java.util.List;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/11
 * 流程信息自定义查询
 */
public interface FlowCustomQueryService {

    FlowSubmitInfoDTO getFlowSubmitInfo(String taskId);

    List<NutMap> listUserTaskNodeAllReviewerUser(List<String> candidateUserNames);
}
