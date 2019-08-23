package com.nutzfw.core.plugin.flowable.util;

import org.flowable.common.engine.impl.persistence.StrongUuidGenerator;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/15
 */
public class FlowStrongUuidGenerator extends StrongUuidGenerator {

    SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(1, 1);

    @Override
    public String getNextId() {
        return String.valueOf(snowflakeIdWorker.nextId());
    }

}
