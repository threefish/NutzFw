package com.nutzfw.modules.common.action;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/2/13
 */
@IocBean
@At("/showChoseTableData")
public class ShowChoseTableDataAction {

    @Ok("btl:WEB-INF/view/tool/showChoseTableData.html")
    @GET
    @At("/page")
    public void index() {
    }
}
