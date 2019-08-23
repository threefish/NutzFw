package com.nutzfw.core.plugin.flowable.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/11
 * 流程提交者信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowSubmitInfoDTO {

    String userName;

    String deptId;

}
