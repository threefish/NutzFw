package com.nutzfw.modules.portal.service;


import com.nutzfw.core.common.service.BaseService;
import com.nutzfw.modules.portal.entity.MsgNotice;

/**
 * @author 叶世游
 * @date 2018年06月19日 14时05分40秒
 */
public interface MsgNoticeService extends BaseService<MsgNotice> {

    /**
     * 批量删除消息提醒
     *
     * @param ids
     * @return
     */
    int del(String[] ids);
}
