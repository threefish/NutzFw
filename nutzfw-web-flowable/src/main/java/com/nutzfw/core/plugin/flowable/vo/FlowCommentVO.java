package com.nutzfw.core.plugin.flowable.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/22.
 * 流程意见
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlowCommentVO {
    String userId;
    String userDesc;
    String fullMessage;
    Date time;
    FlowAttachmentVO handWritingSignatureAttachment;
    List<FlowAttachmentVO> flowAttachments;
}
