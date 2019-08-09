package com.nutzfw.modules.userchage.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.annotation.SysLog;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.userchage.biz.UserChangeBiz;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;

import java.util.List;

/**
 * @author 叶世游
 * @date: 2018/6/19
 * 描述此类：人员异动
 */
@IocBean
@At("/userchange")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class UserChangesAction extends BaseAction {

    @Inject
    UserChangeBiz userChangeBiz;

    @At("/index")
    @RequiresPermissions("userchange.index")
    @AutoCreateMenuAuth(name = "人员异动", icon = "fa-cogs", shortNo = -2)
    public void index() {
    }

    @At("/manager")
    @Ok("btl:WEB-INF/view/userchange/index.html")
    @RequiresPermissions("userchange.manager")
    @AutoCreateMenuAuth(name = "人员异动", icon = "fa-cogs", parentPermission = "userchange.index", shortNo = 1)
    public void manager() {
    }

    /**
     * 查询用户信息列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Ok("json:{locked:'password|salt'}")
    @At("/queryUsers")
    @POST
    @RequiresPermissions("userchange.queryUsers")
    @AutoCreateMenuAuth(name = "查询人员列表", type = 1, icon = "fa-cogs", parentPermission = "userchange.manager")
    public LayuiTableDataListVO queryUsers(@Param("pageNum") int pageNum,
                                           @Param("pageSize") int pageSize,
                                           @Param("key") String key) {
        return userChangeBiz.listUsersPage(pageNum, pageSize, key);
    }

    /**
     * 查询人员异动记录
     *
     * @param pageNum
     * @param pageSize
     * @param review
     * @param changeType
     * @param key
     * @return
     */
    @Ok("json:{locked:'password|salt',DateFormat:'yyyy-MM-dd HH:mm:ss'}")
    @At("/listPage")
    @POST
    @RequiresPermissions("userchange.listPage")
    @AutoCreateMenuAuth(name = "查询人员异动记录", type = 1, icon = "fa-cogs", parentPermission = "userchange.manager")
    public LayuiTableDataListVO listPage(@Param("pageNum") int pageNum,
                                         @Param("pageSize") int pageSize,
                                         @Param("review") int review,
                                         @Param("changeType") int changeType,
                                         @Param("key") String key) {
        return userChangeBiz.listPage(pageNum, pageSize, key, review, changeType);
    }

    /**
     * 获取部门岗位
     *
     * @param userid
     * @return
     */
    @Ok("json")
    @At("/userDeptJobInfo")
    @POST
    @RequiresPermissions("userchange.manager")
    public AjaxResult userDeptJobInfo(
            @Param("userid") String userid) {
        return userChangeBiz.userDeptJobInfo(userid);
    }


    /**
     * 保存人员异动
     *
     * @param data
     * @return
     */
    @Ok("json")
    @At("/saveUserChange")
    @POST
    @RequiresPermissions("userchange.saveUserChange")
    @AutoCreateMenuAuth(name = "查询异动历史列表", type = 1, icon = "fa-cogs", parentPermission = "userchange.manager")
    public AjaxResult saveUserChange(@Param("data") String data) {
        return userChangeBiz.saveUserChange(data, getSessionUserAccount());
    }

    /**
     * 审核人员异动
     *
     * @param review
     * @param changeId
     * @param reviewOpinion
     * @return
     */
    @Ok("json")
    @At("/review")
    @POST
    @RequiresPermissions("userchange.review")
    @AutoCreateMenuAuth(name = "审核人员异动", type = 1, icon = "fa-cogs", parentPermission = "userchange.manager")
    @SysLog(tag = "人员异动", template = "审核[${args[1]==1?'通过':'拒绝'}] 记录ID为[${args[1]}]的人员异动 ${re.ok?'成功':'失败'+re.msg}", result = true)
    public AjaxResult review(@Param("review") int review,
                             @Param("id") String changeId,
                             @Param("reviewOpinion") String reviewOpinion) {
        return userChangeBiz.review(changeId, review, reviewOpinion, getSessionUserAccount());
    }

    /**
     * 查看人员异动历史
     *
     * @param id
     * @return
     */
    @At("/history")
    @GET
    @Ok("btl:WEB-INF/view/userchange/userChangeHistory.html")
    @RequiresPermissions("userchange.history")
    @AutoCreateMenuAuth(name = "查看人员异动历史", type = 1, icon = "fa-cogs", parentPermission = "userchange.manager")
    public List<NutMap> history(@Param("id") String id) {
        return userChangeBiz.history(id);
    }

    /**
     * 查询是否存在异动未审核
     *
     * @param userid
     * @return
     */
    @At("/haveChange")
    @POST
    @Ok("json")
    @RequiresPermissions("userchange.history")
    public AjaxResult haveChange(@Param("userid") String userid) {
        return userChangeBiz.haveChange(userid);
    }
}
