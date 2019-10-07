/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.tabledata.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.sys.service.DataTableService;
import com.nutzfw.modules.sys.service.TableFieldsService;
import com.nutzfw.modules.tabledata.biz.DataMaintainBiz;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/8
 * 描述此类：人员信息维护-维护自动生成的表单与人员相关的所有表数据
 */
@IocBean
@At("/sysUserMaintain")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class UserInfoMaintainAction extends BaseAction {

    @Inject
    UserAccountService accountService;

    @Inject
    DataTableService tableService;

    @Inject
    TableFieldsService fieldsService;

    @Inject
    DataMaintainBiz userInfoMaintainBiz;

    @GET
    @At("/index")
    @Ok("btl:WEB-INF/view/sys/data/maintain/user/manager.html")
    @RequiresPermissions("sysDynamicFrom.edit")
    @AutoCreateMenuAuth(name = "人员信息维护", icon = "fa-wrench", parentPermission = "sys.maintain")
    public void index() {

    }

    @GET
    @At({"/userManager", "/userManager/?"})
    @Ok("btl:WEB-INF/view/sys/data/maintain/user/userManager.html")
    @RequiresPermissions("sysDynamicFrom.edit")
    public void userManager(@Param("userid") String userid) {
        UserAccount userAccount = accountService.fetch(userid);
        setRequestAttribute("user", userAccount);
    }


    @POST
    @At("/listPage")
    @Ok("json")
    @RequiresPermissions("sysDynamicFrom.edit")
    public LayuiTableDataListVO listPage(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize) {
        return accountService.listPage(pageNum, pageSize, Cnd.where("delFlag", "=", 0).and("userName","in", Strings.join(",",getSessionManagerUserNames())));
    }

    /**
     * 取得表格列头信息
     *
     * @param tableid
     * @return
     */
    @POST
    @At("/getCols")
    @Ok("json")
    @RequiresPermissions("sysDynamicFrom.edit")
    public AjaxResult getCols(@Param("tableid") int tableid) {
        return AjaxResult.sucess(userInfoMaintainBiz.getCols(tableid, getSessionRoleIds()));
    }

    @POST
    @At("/queryUserDatalistPage")
    @Ok("json:{locked:'opat|opby|userpass|locked|salt'}")
    @RequiresPermissions("sysDynamicFrom.edit")
    public LayuiTableDataListVO queryUserDatalistPage(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize, @Param("tableid") int tableid, @Param("userid") String userid) {
        return userInfoMaintainBiz.listUserDataPage(pageNum, pageSize, tableid, userid,getSessionManagerUserNames());
    }

}
