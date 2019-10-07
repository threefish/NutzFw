/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.monitor.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.monitor.service.SysOperateLogService;
import com.nutzfw.modules.sys.service.ErrorLogHistoryService;
import com.nutzfw.modules.sys.service.UserLoginHistoryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/28
 * 描述此类：日志查看
 */
@IocBean
@At("/sysLogs")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class LogsAction extends BaseAction {

    @Inject
    UserLoginHistoryService userLoginHistoryService;

    @Inject
    ErrorLogHistoryService errorHistoryService;

    @Inject
    SysOperateLogService sysOperateLogService;


    @At("/index")
    @RequiresPermissions("sysLogs.index")
    @AutoCreateMenuAuth(name = "日志查看", icon = "fa-eye", parentPermission = "sys.monitor")
    public void index() {
    }


    @Ok("btl:WEB-INF/view/sys/monitor/logs/errorLogs.html")
    @GET
    @At("/errorLogs")
    @RequiresPermissions("sysLogs.errorLogs")
    @AutoCreateMenuAuth(name = "系统错误日志", icon = "fa-eye", parentPermission = "sysLogs.index")
    public void errorLogs() {
    }

    @Ok("btl:WEB-INF/view/sys/monitor/logs/loginsLogs.html")
    @GET
    @At("/loginsLogs")
    @RequiresPermissions("sysLogs.loginsLogs")
    @AutoCreateMenuAuth(name = "用户登录日志", icon = "fa-eye", parentPermission = "sysLogs.index")
    public void loginsLogs() {
    }

    @Ok("btl:WEB-INF/view/sys/monitor/logs/sysLogs.html")
    @GET
    @At("/sysLogs")
    @RequiresPermissions("sysLogs.sysLogs")
    @AutoCreateMenuAuth(name = "用户操作记录", icon = "fa-eye", parentPermission = "sysLogs.index")
    public void sysLogs() {
    }

    /**
     * 登录日志列表
     * by dengh
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GET
    @POST
    @At("/loginLogListData")
    @Ok("json")
    @RequiresPermissions("sysLogs.loginsLogs")
    public LayuiTableDataListVO loginLogListData(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize) {
        Sql sql = Sqls.create("SELECT h.*,u.userName FROM sys_user_login_history h,sys_user_account u WHERE h.uid = u.id AND h.delFlag = 0 ORDER BY h.opAt DESC");
        return userLoginHistoryService.listPage(pageNum, pageSize, sql);
    }

    /**
     * 用户操作记录
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @POST
    @At("/sysLogListData")
    @Ok("json:{nullAsEmtry:true,dateFormat:'yyyy年MM月dd日 HH时mm分ss秒'}")
    @RequiresPermissions("sysLogs.loginsLogs")
    public LayuiTableDataListVO sysLogListData(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize, @Param("key") String key) {
        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(key)) {
            key = "%" + key + "%";
            cnd.or("userName", "like", key);
            cnd.or("opByDesc", "like", key);
            cnd.or("tag", "like", key);
            cnd.or("deptDesc", "like", key);
        }
        cnd.desc("opAt");
        return sysOperateLogService.listPage(pageNum, pageSize, cnd);
    }

    /**
     * 错误日志列表
     * by dengh
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GET
    @POST
    @At("/errorLogListData")
    @Ok("json")
    @RequiresPermissions("sysLogs.errorLogs")
    public LayuiTableDataListVO errorLogListData(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize) {
        return errorHistoryService.listPage(pageNum, pageSize, Cnd.where("delFlag", "=", 0).desc("ct"));
    }
}
