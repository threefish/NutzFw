package com.nutzfw.modules.flow.vo;

import com.nutzfw.core.plugin.flowable.dto.UserTaskExtensionDTO;
import lombok.Data;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@Data
public class NextNodeConfigVO {

    private String taskDefId;
    private String taskDefName;
    private UserTaskExtensionDTO extension;

}
