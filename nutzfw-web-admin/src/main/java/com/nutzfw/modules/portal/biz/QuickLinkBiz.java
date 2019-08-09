package com.nutzfw.modules.portal.biz;

import com.nutzfw.core.common.vo.AjaxResult;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author panchuang
 * @date 2018/6/20
 * @description 快捷入口
 */
@IocBean
public interface QuickLinkBiz {

    /**
     * 删除快捷入口
     *
     * @param id
     * @return
     */
    AjaxResult delQuickLink(String id);

}
