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
