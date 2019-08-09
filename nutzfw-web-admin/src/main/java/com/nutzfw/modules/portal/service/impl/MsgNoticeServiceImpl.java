package com.nutzfw.modules.portal.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.portal.entity.MsgNotice;
import com.nutzfw.modules.portal.service.MsgNoticeService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 叶世游
 * @date 2018年06月19日 14时05分40秒
 */
@IocBean(args = {"refer:dao"})
public class MsgNoticeServiceImpl extends BaseServiceImpl<MsgNotice> implements MsgNoticeService {
    public MsgNoticeServiceImpl(Dao dao) {
        super(dao);
    }


    /**
     * 批量删除消息提醒
     *
     * @param ids
     * @return
     */
    @Override
    public int del(String[] ids) {
        return vDelete(ids, true);
    }
}
