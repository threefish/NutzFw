package com.nutzfw.modules.organize.action;


import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.annotation.SysLog;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.util.RegexUtil;
import com.nutzfw.core.common.util.StringUtil;
import com.nutzfw.core.common.util.ViewUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.entity.UserAccountJob;
import com.nutzfw.modules.organize.service.UserAccountJobService;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.sys.biz.DictBiz;
import com.nutzfw.modules.sys.biz.UserAccountBiz;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Encoding;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.*;
import org.nutz.plugins.validation.Errors;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: huchuc@vip.qq.com
 * Date: 2016/11/17 0017
 * To change this template use File | Settings | File Templates.
 */
@IocBean
@At("/sysAccount")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class UserAccountAction extends BaseAction {

    @Inject
    protected UserAccountService userAccountService;
    @Inject
    DictBiz dictBiz;
    @Inject
    UserAccountBiz userAccountBiz;

    @Inject
    UserAccountJobService userAccountJobService;

    private String defaultAdmin = Cons.ADMIN;

    @Ok("btl:WEB-INF/view/sys/organize/user/index.html")
    @GET
    @At("/index")
    @RequiresPermissions("sysAccount.index")
    @AutoCreateMenuAuth(name = "用户管理", icon = "fa-users", shortNo = 3, parentPermission = "sysOrganize.index")
    public void index() {
        setRequestAttribute("defaultAdmin", defaultAdmin);
        setRequestAttribute("naturesName", dictBiz.getDictEnumsJson("sys_user_natures"));
        setRequestAttribute("categoryName", dictBiz.getDictEnumsJson("sys_user_category"));
    }

    /**
     * 用户管理——用户查询
     */
    @Ok("json:{ignoreNull:false,dateFormat:'yyyy-MM-dd HH:mm:ss',locked:'userpass|salt'}")
    @POST
    @At("/listPage")
    @RequiresPermissions("sysAccount.index")
    public LayuiTableDataListVO listPage(@Param("pageNum") int pageNum,
                                         @Param("pageSize") int pageSize,
                                         @Param("name") String name,
                                         @Param("status") int status,
                                         @Param("deptId") String deptId,
                                         @Param("jobId") String jobId,
                                         @Param("hasRole") boolean hasRole) {
        try {
            return userAccountBiz.listPage(pageNum, pageSize, name, status, -1, Strings.sNull(deptId), jobId, hasRole);
        } catch (Exception e) {
            log.error(e.getMessage());
            return LayuiTableDataListVO.error(e.getMessage());
        }
    }


    /**
     * 启用/禁用账号
     *
     * @param ids
     * @param type
     * @return
     */
    @Ok("json:{ignoreNull:false,DateFormat:'yyyy-MM-dd HH:mm:ss'}")
    @POST
    @At("/lock")
    @RequiresPermissions("sysAccount.lock")
    @AutoCreateMenuAuth(name = "启用/禁用", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysAccount.index")
    public AjaxResult enable(@Param("id") String ids, @Param("type") Integer type) {
        try {
            List<UserAccount> accounts = userAccountService.query(Cnd.where("id", "in", ids.split(",")));
            for (int i = 0; i < accounts.size(); i++) {
                accounts.get(i).setLocked(type == 1);
                if (accounts.get(i).getUserName().equals(defaultAdmin) && type == 1) {
                    return AjaxResult.error(defaultAdmin + "是系统管理员,不能被禁用!");
                }
            }
            userAccountService.update(accounts);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
        return AjaxResult.sucess("操作成功");
    }


    @Ok("json:{ignoreNull:false,DateFormat:'yyyy-MM-dd HH:mm:ss'}")
    @POST
    @At("/repass")
    @RequiresPermissions("sysAccount.repass")
    @AutoCreateMenuAuth(name = "重置密码", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysAccount.index")
    public AjaxResult update(@Param("ids") String ids) {
        try {
            List<UserAccount> accounts = userAccountService.query(Cnd.where("id", "in", ids.split(",")));
            for (int i = 0; i < accounts.size(); i++) {
                String salt = R.UU16();
                Sha256Hash sha = new Sha256Hash(Cons.DEFAULT_PASSWORD, salt);
                accounts.get(i).setUserPass(sha.toHex());
                accounts.get(i).setSalt(salt);
                if (accounts.get(i).getUserName().equals(defaultAdmin)) {
                    return AjaxResult.error(defaultAdmin + "是管理员,不能被重置密码!");
                }
            }
            userAccountService.update(accounts);
        } catch (Exception e) {
            return AjaxResult.error("密码重置失败");
        }
        return AjaxResult.sucess("密码重置成功");
    }

    @Ok("json:{ignoreNull:false,DateFormat:'yyyy-MM-dd HH:mm:ss'}")
    @POST
    @At("/add")
    @RequiresPermissions("sysAccount.add")
    @AutoCreateMenuAuth(name = "添加", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysAccount.index")
    public AjaxResult add(@Param("::data.") UserAccount userAccount, @Param("jobsId") String jobsId, Errors errors) {
        if (errors.hasError()) {
            return AjaxResult.error(errors.getErrorsList().iterator().next());
        }
        UserAccount account = userAccountService.fetch(Cnd.where("userName", "=", userAccount.getUserName()));
        if (account != null) {
            return AjaxResult.error("帐号已存在！");
        }

        if (Strings.isNotBlank(userAccount.getPhone()) && !RegexUtil.isPhone(userAccount.getPhone())) {
            return AjaxResult.error("手机号码不符合规则");
        }
        if (Strings.isNotBlank(userAccount.getMail()) && !RegexUtil.isEmail(userAccount.getMail())) {
            return AjaxResult.error("电子邮箱不符合规则");
        }
        String pass = Strings.sNull(userAccount.getUserPass()).trim();
        Sha256Hash sha;
        if (pass.length() == 0) {
            pass = Cons.DEFAULT_PASSWORD;
        } else if (pass.length() < 6 && pass.length() > 20) {
            return AjaxResult.error("密码长度不能小于6！不能大于20");
        }
        String salt = R.UU16();
        sha = new Sha256Hash(pass, salt);
        try {
            account = new UserAccount();
            account.setLocked(false);
            account.setUserPass(sha.toHex());
            account.setSalt(salt);
            account.setUserName(userAccount.getUserName());
            account.setRealName(userAccount.getRealName());
            account.setPhone(userAccount.getPhone());
            account.setMail(userAccount.getMail());
            account.setDeptId(userAccount.getDeptId());
            account.setCreateByDate(new Date(System.currentTimeMillis()));
            account.setCreateByName(getSessionUserAccount().getRealName());
            account.setCreateByUserid(getSessionUserAccount().getUserid());
            userAccountService.insert(account);
            String userId = account.getId();
            String[] jobIds = Strings.splitIgnoreBlank(",");
            List<UserAccountJob> userAccountJobList = new ArrayList<>();
            for (String jobId : jobIds) {
                userAccountJobList.add(UserAccountJob.builder().jobId(jobId).userId(userId).build());
            }
            userAccountJobService.insert(userAccountJobList);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
        return AjaxResult.sucess("操作成功");
    }

    /**
     * 新闻发布功能-接收岗位
     * by dengh
     *
     * @param query 模糊查询 岗位名称
     * @return
     */
    @At("/jobSelectList")
    @Ok("json:{ignoreNull:false,nullAsEmtry:true}")
    @RequiresPermissions("sysAccount.importData")
    @AutoCreateMenuAuth(name = "添加", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysAccount.index")
    public LayuiTableDataListVO jobSelectList(@Param("query") String query,
                                              @Param("deptId") String deptId,
                                              @Param("pageNum") int pageNumber,
                                              @Param("pageSize") int pageSize) {
        try {
            return userAccountBiz.jobSelectList(query, deptId, pageNumber, pageSize);
        } catch (Exception e) {
            return new LayuiTableDataListVO();
        }
    }

    /**
     * 解析上传的文件
     *
     * @param attachId
     * @return
     */
    @At("/checkImportData")
    @Ok("json:{ignoreNull:false,nullAsEmtry:true}")
    @RequiresPermissions("sysAccount.importData")
    @AutoCreateMenuAuth(name = "导入用户", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysAccount.index")
    @SysLog(tag = "用户管理", template = "导入用户 ${re.ok?'成功':'失败'+re.msg}", result = true, param = true)
    public AjaxResult checkImportData(@Param("attachId") String attachId) {
        try {
            if (StringUtil.isBlank(attachId)) {
                return AjaxResult.error("没有找到文件id");
            }
            return userAccountBiz.importUser(attachId);
        } catch (Exception e) {
            return AjaxResult.error("解析失败");
        }

    }

    /**
     * 模板下载
     *
     * @return
     */
    @At("/downTemplate")
    @Ok("raw")
    public Object downTemplate() {
        try {
            File file = userAccountBiz.createDownTemplate().toFile();
            Mvcs.getResp().setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("用户信息导入.xlsx", Encoding.UTF8));
            return file;
        } catch (Exception e) {
            log.error(e);
            return ViewUtil.toErrorPage("模版生成失败！" + e.getLocalizedMessage());
        }
    }

    /**
     * 保存用户和角色的关联
     *
     * @param userIds
     * @param roleIds
     * @return
     */
    @At("/saveUserRole")
    @Ok("json:{ignoreNull:false,nullAsEmtry:true}")
    @RequiresPermissions("sysAccount.saveUserRole")
    @AutoCreateMenuAuth(name = "导入用户", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysAccount.index")
    public AjaxResult saveUserRole(@Param("userIds") String userIds,
                                   @Param("roleIds") String roleIds,
                                   @Param("type") Integer type) {
        try {

            return userAccountBiz.saveUserRole(userIds, roleIds, type);
        } catch (Exception e) {
            return AjaxResult.error("保存失败!");
        }

    }
}
