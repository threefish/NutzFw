package com.nutzfw.modules.message.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.message.entity.NewsReceiveRole;
import com.nutzfw.modules.message.service.NewsReceiveRoleService;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2019年08月01日
 * 新闻接收角色关联
 */
@IocBean(args = {"refer:dao"}, name = "newsReceiveRoleService")
public class NewsReceiveRoleServiceImpl extends BaseServiceImpl<NewsReceiveRole>  implements NewsReceiveRoleService {
public NewsReceiveRoleServiceImpl(Dao dao){super(dao);}}
