package com.nutzfw.modules.sys.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.annotation.TryCatchMsg;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.sys.biz.EmailBiz;
import com.nutzfw.modules.sys.entity.MailBody;
import com.nutzfw.modules.sys.service.MailBodyService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/28
 * 描述此类：邮件管理-涵盖邮件发送记录，发送状态，邮件信息，单独发送邮件
 */
@IocBean
@At("/sysEmail")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class EmailAction extends BaseAction {

    @Inject
    MailBodyService mailBodyService;

    @Inject
    EmailBiz emailBiz;

    @Ok("btl:WEB-INF/view/sys/monitor/email/index.html")
    @GET
    @At("/index")
    @RequiresPermissions("sysEmail.index")
    @AutoCreateMenuAuth(name = "邮件管理", icon = "fa-eye", parentPermission = "sys.monitor")
    public void index() {
    }

    /**
     * 邮件列表
     */
    @GET
    @POST
    @At("/emailList")
    @Ok("json")
    @RequiresPermissions("sysEmail.index")
    public LayuiTableDataListVO query(@Param("subject") String subject, @Param("bizType") int bizType, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize) {
        try {
            Cnd cnd = Cnd.NEW();
            if (Strings.isNotBlank(subject)) {
                cnd.andEX("subject", "like", subject);
            }
            if (bizType > 0) {
                cnd.andEX("bizType", "=", 0);
            }
            return emailBiz.query(cnd, pageNum, pageSize);
        } catch (Exception e) {
            log.error(e);
        }
        return new LayuiTableDataListVO();
    }

    /**
     * 重新发送
     */
    @GET
    @POST
    @At("/resend")
    @Ok("json")
    @RequiresPermissions("sysEmail.index")
    public AjaxResult resend(@Param("id") String id) {
        try {
            return emailBiz.resend(id);
        } catch (Exception e) {
            log.error(e);
        }
        return AjaxResult.error("发送失败");
    }


    @GET
    @POST
    @At("/del")
    @Ok("json")
    @RequiresPermissions("sysEmail.index")
    @TryCatchMsg("删除失败")
    public AjaxResult del(@Param("ids") String[] ids) {
        if (ids != null && ids.length > 0) {
            mailBodyService.vDelete(ids, true);
            return AjaxResult.sucess("删除成功");
        } else {
            return AjaxResult.error("参数错误");
        }
    }

    /**
     * 查看邮件
     */
    @GET
    @POST
    @At("/viewEmail")
    @Ok("json")
    @RequiresPermissions("sysEmail.index")
    public AjaxResult viewEmail(@Param("id") String id) {
        if (Strings.isBlank(id)) {
            return AjaxResult.error("id不能为空");
        }
        try {
            MailBody mailBody = mailBodyService.fetch(id);
            return AjaxResult.sucess(mailBody, "查询成功");
        } catch (Exception e) {
            log.error(e);
        }
        return AjaxResult.error("查询失败");
    }

    /**
     * 编写邮件页面跳转
     */
    @GET
    @At("/writeEmail ")
    @Ok("btl:WEB-INF/view/sys/monitor/email/writeEmail.html")
    @RequiresPermissions("sysEmail.writeEmail")
    @AutoCreateMenuAuth(name = "发送邮件", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-eye", permission = "sysEmail.writeEmail", parentPermission = "sysEmail.index")
    public void writeEmail(@Param("type") String type, @Param("id") String id) {
        setRequestAttribute("type", type);
        setRequestAttribute("id", id);
        setRequestAttribute("UE_ALL_TOOL", Cons.UE_ALL_TOOL);
    }

    /**
     * 发送邮件
     */
    @GET
    @POST
    @At("/sendEmail")
    @Ok("json")
    @RequiresPermissions("sysEmail.index")
    public AjaxResult sendEmail(@Param("::mail.") MailBody mailBody) {
        try {
            UserAccount account = getSessionUserAccount();
            return emailBiz.sendEmail(mailBody);
        } catch (Exception e) {
            log.error(e);
        }
        return AjaxResult.error("发送失败");
    }

}
