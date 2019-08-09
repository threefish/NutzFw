package com.nutzfw.modules.organize.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.annotation.SysLog;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.sys.biz.UserAccountBiz;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/25
 * 描述此类：审核新增人员
 */
@IocBean
@At("/userReview/review")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class UserReviewAction extends BaseAction {


    @Inject
    UserAccountService userAccountService;
    @Inject
    UserAccountBiz userAccountBiz;

    @Ok("btl:WEB-INF/view/sys/organize/userReview/index.html")
    @GET
    @At("/index")
    @RequiresPermissions("sysOrganize.userReview")
    @AutoCreateMenuAuth(name = "审核新增人员", icon = "fa-bookmark", shortNo = 1, parentPermission = "sysOrganize.index")
    public void sysUserReviewIndex() {
    }

    @POST
    @At("/listPage")
    @Ok("json")
    @RequiresPermissions("sysOrganize.userReview")
    public LayuiTableDataListVO listPage(@Param("pageNum") int pageNum,
                                         @Param("pageSize") int pageSize,
                                         @Param("review") int review,
                                         @Param("key") String key) {
        return userAccountBiz.listPage(pageNum, pageSize, key, -1, review, "", "", false);
    }

    /**
     * 通过审核
     *
     * @param ids
     * @return
     */
    @POST
    @At("/agreeReview")
    @Ok("json")
    @RequiresPermissions("sysOrganize.userReview.agreeReview")
    @AutoCreateMenuAuth(name = "通过审核", icon = "fa-bookmark", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysOrganize.userReview")
    @SysLog(tag = "用户管理", template = "通过用户审核${args[0]}", result = true, param = true)
    public AjaxResult agreeReview(@Param("ids") String[] ids, @Param("reviewOpinion") String reviewOpinion) {
        if (Strings.isBlank(reviewOpinion)) {
            return AjaxResult.error("请输入意见");
        }
        List<UserAccount> userAccountList = userAccountService.query(Cnd.where("userid", "in", ids));
        List<UserAccount> accounts = new ArrayList<>();
        userAccountList.forEach(userAccount -> {
            userAccount.setReview(1);
            userAccount.setReviewOpinion(reviewOpinion);
            accounts.add(userAccount);
        });
        userAccountService.update(accounts);
        return AjaxResult.sucess("审核成功");
    }

    /**
     * 撤销审核
     *
     * @param ids
     * @return
     */
    @POST
    @At("/undoReview")
    @Ok("json")
    @RequiresPermissions("sysOrganize.userReview.undoReview")
    @AutoCreateMenuAuth(name = "撤销审核", icon = "fa-bookmark", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysOrganize.userReview")
    @SysLog(tag = "用户管理", template = "撤销审核用户${args[0]}", result = true, param = true)
    public AjaxResult undoReview(@Param("ids") String[] ids) {
        List<UserAccount> userAccountList = userAccountService.query(Cnd.where("userid", "in", ids));
        List<UserAccount> accounts = new ArrayList<>();
        userAccountList.stream().filter(userAccount -> !userAccount.getUserName().equals(Cons.ADMIN)).forEach(userAccount -> {
            userAccount.setReview(0);
            userAccount.setReviewOpinion("");
            accounts.add(userAccount);
        });
        userAccountService.update(accounts);
        return AjaxResult.sucess("撤销成功");
    }

}
