/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.portal.biz.impl;

import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.modules.portal.biz.QuickLinkBiz;
import com.nutzfw.modules.portal.entity.QuickLink;
import com.nutzfw.modules.portal.service.QuickLinkService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;


/**
 * @author panchuang
 * @date 2018/6/20
 * @description
 */
@IocBean
public class QuickLinkBizImpl implements QuickLinkBiz {
    @Inject
    QuickLinkService quickLinkService;

    @Override
    public AjaxResult delQuickLink(String id) {
        QuickLink quickLink = quickLinkService.fetch(id);
        quickLink.setDelFlag(true);
        boolean flag = quickLinkService.update(quickLink) > 0;
        if (flag) {
            return AjaxResult.sucessMsg("删除成功");
        }
        return AjaxResult.error("删除失败");
    }

}
