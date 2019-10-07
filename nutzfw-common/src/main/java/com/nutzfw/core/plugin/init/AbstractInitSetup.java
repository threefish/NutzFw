/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.init;

import com.nutzfw.core.common.entity.BaseEntity;
import com.nutzfw.modules.sys.entity.QuartzJob;

import java.util.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/4
 * 描述此类：
 */
public abstract class AbstractInitSetup implements InitSetup {
    /**
     * 字典分隔符
     */
    protected final static String                           DELIMITER     = "→→";
    /**
     * 等待初始化附件字典类型-初始化值
     */
    protected static       HashMap<String, String>          attachType    = new HashMap<>(0);
    /**
     * 等待初始化定时任务
     */
    protected static       List<QuartzJob>                  quartzJobs    = new ArrayList<>();
    /**
     * 这些表不需要自动创建
     */
    protected static       Set<Class<? extends BaseEntity>> tablesFilters = new HashSet<>();

    /**
     * 等待初始化的数据字典分组
     */
    protected static HashMap<String, String> dictGroup = new HashMap<>(6);

}
