/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
