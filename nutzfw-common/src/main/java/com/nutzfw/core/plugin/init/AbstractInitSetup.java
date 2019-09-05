package com.nutzfw.core.plugin.init;

import com.nutzfw.core.common.entity.BaseEntity;
import com.nutzfw.modules.sys.entity.QuartzJob;
import org.nutz.mvc.NutConfig;

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
    protected final static String DELIMITER = "→→";
    /**
     * 等待初始化附件字典类型-初始化值
     */
    protected static HashMap<String, String> attachType = new HashMap<>(0);
    /**
     * 等待初始化定时任务
     */
    protected static List<QuartzJob> quartzJobs = new ArrayList<>();
    /**
     * 这些表不需要自动创建
     */
    protected static Set<Class<? extends BaseEntity>> tablesFilters = new HashSet<>();

    /**
     * 等待初始化的数据字典分组
     */
    protected static HashMap<String, String> dictGroup = new HashMap<>(6);

}
