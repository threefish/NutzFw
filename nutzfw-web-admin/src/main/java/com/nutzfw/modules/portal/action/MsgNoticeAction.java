/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.portal.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.util.Base64Tool;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.portal.entity.MsgNotice;
import com.nutzfw.modules.portal.service.MsgNoticeService;
import com.nutzfw.modules.portal.service.PortalFunctionService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;
import org.nutz.plugins.validation.Errors;

/**
 * @author 叶世游
 * @date: 2018/6/19
 * 描述此类：消息提醒配置
 */
@IocBean
@At("/msgnotice")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class MsgNoticeAction extends BaseAction {

    @Inject
    MsgNoticeService      msgNoticeService;
    @Inject
    PortalFunctionService portalFunctionService;

    @At("/manager")
    @Ok("btl:WEB-INF/view/portal/msgnotice.html")
    @RequiresPermissions("msgnotice.manager")
    @AutoCreateMenuAuth(name = "消息提醒管理", icon = "fa-cogs", parentPermission = "portal.index", shortNo = 1)
    public void manager() {
    }


    /**
     * 查询消息提醒
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Ok("json")
    @At("/query")
    @POST
    @RequiresPermissions("msgnotice.query")
    @AutoCreateMenuAuth(name = "查询消息提醒列表", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-cogs", parentPermission = "msgnotice.manager")
    public LayuiTableDataListVO query(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize) {
        return msgNoticeService.listPage(pageNum, pageSize, Cnd.where("delFlag", "=", "0").desc("sort"));
    }

    @Ok("json:{locked:'opAt|opBy'}")
    @At("/save")
    @POST
    @RequiresPermissions("msgnotice.save")
    @AutoCreateMenuAuth(name = "保存消息提醒", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-cogs", parentPermission = "msgnotice.manager")
    public AjaxResult save(@Param("::") MsgNotice msgNotice, Errors errors) {
        if (errors.hasError()) {
            return AjaxResult.error(errors.getErrorsList().iterator().next());
        }
        try {
            msgNotice.setSqlStr(new String(Base64Tool.decode(msgNotice.getSqlStr())));
            msgNoticeService.insertOrUpdate(msgNotice);
            return AjaxResult.sucess("保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("保存失败");
        }
    }

    /**
     * 批量删除消息提醒
     *
     * @param ids
     * @return
     */
    @Ok("json:{locked:'opAt|opBy'}")
    @At("/del")
    @POST
    @RequiresPermissions("msgnotice.del")
    @AutoCreateMenuAuth(name = "批量删除消息提醒", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-cogs", parentPermission = "msgnotice.manager")
    public AjaxResult del(@Param("ids") String[] ids) {
        try {
            int count = msgNoticeService.del(ids);
            if (count > 0) {
                return AjaxResult.sucess("删除成功!");
            } else {
                return AjaxResult.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("删除失败");
        }
    }
}
