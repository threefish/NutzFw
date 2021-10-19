package com.nutzfw.core.plugin.flowable.context;

import com.nutzfw.core.plugin.flowable.enums.ProcessStatus;
import lombok.Data;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2021/10/19
 */
@Data
public class ProcessContext {
    private ProcessStatus processStatus;
    private String processInstanceId;
    private String processDefId;
    private String processDefKey;
    private String businessId;
    private boolean processCompleted = false;
}
