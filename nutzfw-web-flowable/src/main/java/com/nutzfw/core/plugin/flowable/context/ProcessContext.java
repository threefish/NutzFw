package com.nutzfw.core.plugin.flowable.context;

import com.nutzfw.core.plugin.flowable.enums.ProcessStatus;
import com.nutzfw.core.plugin.flowable.vo.FlowTaskVO;
import lombok.Data;

import java.util.Map;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2021/10/19
 */
@Data
public class ProcessContext {
    public ProcessContext() {
    }

    private ProcessStatus processStatus;
    private String processInstanceId;
    private String processDefId;
    private String processDefKey;
    private String businessId;
    private String initiator;
    private FlowTaskVO flowTaskVO;
    private Map<String, Object> formData;
    private boolean processCompleted = false;
    private ProcessContext childProcessContext;

    public boolean isChildProcessContext() {
        return childProcessContext != null;
    }
}
