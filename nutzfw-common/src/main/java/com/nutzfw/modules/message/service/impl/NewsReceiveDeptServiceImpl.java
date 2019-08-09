package com.nutzfw.modules.message.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.message.entity.NewsReceiveDept;
import com.nutzfw.modules.message.service.NewsReceiveDeptService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2019年08月01日
 * 新闻接收部门关联
 */
@IocBean(args = {"refer:dao"}, name = "newsReceiveDeptService")
public class NewsReceiveDeptServiceImpl extends BaseServiceImpl<NewsReceiveDept>  implements NewsReceiveDeptService {
public NewsReceiveDeptServiceImpl(Dao dao){super(dao);}}
