package com.nutzfw.modules.tabledata.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.tabledata.biz.UserDataReviewBiz;
import com.nutzfw.modules.tabledata.service.UserDataChangeHistoryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/7/3
 * 描述此类：用户数据审核
 */
@IocBean
@At("/sysUserDataReview")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class UserDataReviewAction extends BaseAction {

    @Inject
    UserDataChangeHistoryService userDataChangeHistoryService;

    @Inject
    UserDataReviewBiz userDataReviewBiz;

    @At("/index")
    @Ok("btl:WEB-INF/view/sys/data/user/index.html")
    @RequiresPermissions("sys.userDataReview")
    @AutoCreateMenuAuth(name = "用户数据审核", icon = "fa-sitemap", parentPermission = "sys.dataReview")
    public NutMap userDataReview() {
        return NutMap.NEW();
    }

    @At("/listPage")
    @Ok("json")
    @RequiresPermissions("sys.userDataReview")
    public LayuiTableDataListVO listPage(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize, @Param("review") int review) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT h.uuid,u.userName,u.realName,h.`status`,h.review,h.tableId,h.addDate,(SELECT `name` FROM sys_data_table WHERE id = h.tableId) AS tableName ,(SELECT realName FROM sys_user_account WHERE userid = h.addUserId ) AS addUser,(SELECT `name` FROM sys_department WHERE id = u.deptId )AS deptName ");
        sb.append(" FROM sys_user_account u");
        sb.append(" RIGHT JOIN user_data_change_history h ON h.userId = u.userid ");
        sb.append(" where h.review=@review ");
        sb.append(" order by h.addDate desc");
        Sql sql = Sqls.create(sb.toString());
        sql.setParam("review", review);
        return userDataChangeHistoryService.listPage(pageNum, pageSize, sql);
    }

    @At("/review")
    @Ok("btl:WEB-INF/view/sys/data/user/review.html")
    @RequiresPermissions("sys.userDataReview.review")
    @AutoCreateMenuAuth(name = "审核", icon = "fa-sitemap", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sys.userDataReview")
    public NutMap review(@Param("id") String id, @Param("isview") boolean isview) {
        return userDataReviewBiz.showReviewData(id).setv("isview", isview ? "readonly" : "");
    }

    @At("/review/agreeReview")
    @Ok("json")
    @RequiresPermissions("sys.userDataReview.review")
    public AjaxResult agreeReview(@Param("ids") String[] ids, @Param("reviewOpinion") String reviewOpinion) {
        if (Strings.isBlank(reviewOpinion)) {
            return AjaxResult.error("审核意见不能为空！");
        }
        for (String id : ids) {
            userDataReviewBiz.agreeReview(id, reviewOpinion);
        }
        return AjaxResult.sucess("批量操作完成！");
    }
}
