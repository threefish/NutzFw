package com.nutzfw.core.plugin.flowable.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/22
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlowAttachmentVO {
    /**
     * 附件名称
     */
    String name;
    /**
     * 上传用户
     */
    String userId;
    /**
     * 附件ID
     */
    String contentId;
    /**
     * 附件内容
     */
    String content;
    /**
     * 附件类型
     */
    String type;
}
