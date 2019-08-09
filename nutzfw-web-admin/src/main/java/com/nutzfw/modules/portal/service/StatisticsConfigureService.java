package com.nutzfw.modules.portal.service;


import com.nutzfw.core.common.service.BaseService;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.modules.portal.entity.StatisticsConfigure;

/**
 * @author 叶世游
 * @date 2018年06月19日 09时59分20秒
 */
public interface StatisticsConfigureService extends BaseService<StatisticsConfigure> {
    /**
     * 保存统计
     *
     * @param statisticsConfigure
     * @return
     */
    AjaxResult save(StatisticsConfigure statisticsConfigure);

    /**
     * 批量删除统计
     *
     * @param ids
     * @return
     */
    int del(String[] ids);

    /**
     * 预览图像
     *
     * @param string
     * @return
     */
    AjaxResult showNow(String string);
}
