/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
