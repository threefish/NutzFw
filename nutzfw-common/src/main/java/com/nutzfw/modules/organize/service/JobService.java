package com.nutzfw.modules.organize.service;


import com.nutzfw.core.common.service.BaseService;
import com.nutzfw.modules.organize.entity.Job;
import org.nutz.lang.util.NutMap;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年06月01日 19时36分19秒
 */
public interface JobService extends BaseService<Job> {
    /**
     * 获取所有的岗位,并拼接
     *
     * @return
     */
    String[] allJobStrs();

    /**
     * 岗位人数统计
     *
     * @param customizedParams
     * @return
     */
    List<NutMap> statisticsJobUser(String customizedParams);
}
