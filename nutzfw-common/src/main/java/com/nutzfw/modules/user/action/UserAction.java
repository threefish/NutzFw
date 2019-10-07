/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.user.action;

import com.google.common.collect.Sets;
import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.annotation.SysLog;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.util.RegexUtil;
import com.nutzfw.core.common.util.ViewUtil;
import com.nutzfw.core.common.util.WebUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.core.plugin.shiro.LoginTypeEnum;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.sys.biz.UserAccountBiz;
import com.nutzfw.modules.sys.entity.DataTable;
import com.nutzfw.modules.sys.entity.TableFields;
import com.nutzfw.modules.sys.entity.UserLoginHistory;
import com.nutzfw.modules.sys.service.DataTableService;
import com.nutzfw.modules.sys.service.UserLoginHistoryService;
import com.nutzfw.modules.tabledata.action.DynamicFromAction;
import com.nutzfw.modules.tabledata.biz.DataMaintainBiz;
import com.nutzfw.modules.tabledata.service.UserDataChangeHistoryService;
import io.swagger.annotations.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.dao.entity.Record;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.*;
import org.nutz.plugins.validation.Errors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/3/26
 * 描述此类：个人资料修改信息管理
 */
@IocBean
@At("/manage/user/")
@Api("/manage/user/")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class UserAction extends BaseAction {

    @Inject
    UserAccountService accountService;
    @Inject
    UserAccountBiz     userAccountBiz;

    @Inject
    UserLoginHistoryService userLoginHistoryService;

    @Inject
    DataTableService tableService;

    @Inject
    DynamicFromAction dynamicFromAction;

    @Inject
    DataMaintainBiz              dataMaintainBiz;
    @Inject
    DataTableService             dataTableService;
    @Inject
    DataMaintainBiz              userInfoMaintainBiz;
    @Inject
    UserDataChangeHistoryService userDataChangeHistoryService;

    @GET
    @At("changePass")
    @Ok("btl:WEB-INF/view/sys/user/changePass.html")
    public void changePass() {
        setRequestAttribute("userinfo", getSessionUserAccount());
    }

    /**
     * 个人资料
     *
     * @return
     */
    @GET
    @At("personalInfo")
    @Ok("btl:WEB-INF/view/sys/user/personalInfo.html")
    @RequiresPermissions("sysPersonal.index")
    @AutoCreateMenuAuth(name = "个人资料", icon = "fa-wrench")
    public NutMap personalInfo() {
        String tableName = UserAccount.class.getAnnotation(Table.class).value();
        String sourceId = dataMaintainBiz.getSourceId(tableName, getSessionUserAccount().getUserName());
        DataTable table = dataTableService.fetch(Cnd.where("tableName", "=", tableName));
        return NutMap.NEW().setv("sourceId", sourceId).setv("tableId", table.getId());
    }

    @Ok("json")
    @POST
    @At("/saveReviewData")
    @RequiresPermissions("sysPersonal.editToReview")
    @SysLog(tag = "修改个人资料", template = "修改个人资料 保存表[${args[0]}]信息 ${re.ok?'成功':'失败'+re.msg}", result = true, param = true)
    public AjaxResult saveReviewData(@Param("tableId") int tableId, @Param("data") String dataStr, @Attr(Cons.SESSION_USER_KEY) UserAccount userAccount) {
        NutMap data = dataMaintainBiz.formJsonData(dataStr, userAccount);
        if (userAccount.getUserid().equals(data.getString("userid", ""))) {
            return dynamicFromAction.saveReviewData(tableId, dataStr, userAccount);
        } else {
            return AjaxResult.error("非法操作！");
        }
    }

    @POST
    @At("loadPendingReviewCount")
    @Ok("json")
    @RequiresPermissions("sysPersonal.index")
    public AjaxResult loadPendingReviewCount(@Param("tableId") int tableId, @Attr(Cons.SESSION_USER_KEY) UserAccount userAccount) {
        return AjaxResult.sucess(userDataChangeHistoryService.loadPendingReviewCount(tableId, userAccount.getUserid()));
    }

    /**
     * 编辑查看-新增或保存后需要审核
     *
     * @param tableId  表ID
     * @param sourceId 数据表源数据ID
     * @return
     */
    @Ok("btl:WEB-INF/view/sys/dynamicfrom/edit_1.html")
    @GET
    @At("/editPersonalDataToReview")
    @RequiresPermissions("sysPersonal.editToReview")
    @AutoCreateMenuAuth(name = "个人资料修改", icon = "fa-wrench", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysPersonal.index")
    public View editPersonalDataToReview(@Param("tableId") int tableId, @Param("sourceId") String sourceId) {
        setRequestAttribute("needReview", true);
        String userId = getSessionUserAccount().getId();
        if (Strings.isNotBlank(sourceId)) {
            boolean isMyData = dataMaintainBiz.isMyData(UserAccount.class.getAnnotation(Table.class).value(), getSessionUserAccount().getUserName(), sourceId);
            if (isMyData) {
                return dynamicFromAction.edit(tableId, userId, sourceId, false, getSessionUserAccount());
            } else {
                return ViewUtil.toErrorPage("非法操作！");
            }
        } else {
            return dynamicFromAction.edit(tableId, userId, "", false, getSessionUserAccount());
        }
    }

    /**
     * 编辑查看-新增或保存后需要审核
     *
     * @param tableId  表ID
     * @param sourceId 数据表源数据ID
     * @return
     */
    @Ok("btl:WEB-INF/view/sys/dynamicfrom/edit_1.html")
    @GET
    @At("/viewPersonalData")
    @RequiresPermissions("sysPersonal.view")
    @AutoCreateMenuAuth(name = "个人资料查看", icon = "fa-wrench", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysPersonal.index")
    public View viewPersonalData(@Param("tableId") int tableId, @Param("sourceId") String sourceId) {
        setRequestAttribute("needReview", true);
        String userId = getSessionUserAccount().getId();
        if (Strings.isNotBlank(sourceId)) {
            boolean isMyData = dataMaintainBiz.isMyData(UserAccount.class.getAnnotation(Table.class).value(), getSessionUserAccount().getUserName(), sourceId);
            if (isMyData) {
                return dynamicFromAction.edit(tableId, userId, sourceId, true, getSessionUserAccount());
            } else {
                return ViewUtil.toErrorPage("非法操作！");
            }
        } else {
            return dynamicFromAction.edit(tableId, userId, "", true, getSessionUserAccount());
        }
    }

    /**
     * 人员附表信息
     */
    @GET
    @At("/scheduleIndex")
    @Ok("btl:WEB-INF/view/sys/user/scheduleIndex.html")
    @RequiresPermissions("sysPersonal.view")
    public void userManager(@Attr(Cons.SESSION_USER_KEY) UserAccount userAccount) {
        setRequestAttribute("user", userAccount);
    }


    @Ok("json")
    @POST
    @At("/canAdd")
    @RequiresPermissions("sysPersonal.editToReview")
    public AjaxResult canAdd(@Param("tableId") int tableId) {
        return dynamicFromAction.canAdd(tableId, getSessionUserAccount().getId());
    }

    @POST
    @At("/queryUserDatalistPage")
    @Ok("json:{locked:'opat|opby|userpass|locked|salt'}")
    @RequiresPermissions("sysPersonal.view")
    public LayuiTableDataListVO queryUserDatalistPage(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize, @Param("tableid") int tableid) {
        return userInfoMaintainBiz.listUserDataPage(pageNum, pageSize, tableid, getSessionUserAccount().getId(), Sets.newHashSet(getSessionUserAccount().getUserName()));
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
    @RequiresPermissions("sysPersonal.view")
    public AjaxResult getCols(@Param("tableid") int tableid) {
        return AjaxResult.sucess(userInfoMaintainBiz.getColsNotFix(tableid, getSessionRoleIds()));
    }


    @Ok("json")
    @POST
    @At("/delToReview")
    @RequiresPermissions("sysPersonal.delToReview")
    @AutoCreateMenuAuth(name = "个人资料记录删除", icon = "fa-wrench", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysPersonal.index")
    @SysLog(tag = "修改个人资料", template = "数据删除表[{$args[0]}]信息 记录ID[${args[1]}] ${re.ok?'成功':'失败'+re.msg}", result = true, param = true)
    public AjaxResult delToReview(@Param("tableId") int tableId, @Param("sourceIds") String[] sourceIds) {
        return dynamicFromAction.delToReview(tableId, sourceIds);
    }


    @GET
    @At("getUserInfo")
    @Ok("json:{nullAsEmtry:true}")
    @ApiOperation(value = "个人基本信息", nickname = "getUserInfo", tags = "账户信息", httpMethod = "GET", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "{\"ok\": true,\"msg\": \"\",\"data\": {\"fieldType\": 0,\"fieldName\": \"realname\",\"fieldLable\": \"\",\"val\": \"\"},{\"fieldType\": 0,\"fieldName\": \"phone\",\"fieldLable\": \"\",....}}"),
    })
    public AjaxResult getUserInfo() {
        try {
            DataTable dataTable = tableService.fetchAllFields(1);
            Record queryRecord = tableService.dao().fetch(dataTable.getTableName(),
                    Cnd.where("userid", "=", getSessionUserAccount().getUserid()),
                    dataMaintainBiz.getFieldsRegx(dataTable.getFields())
            );
            Record record = dataMaintainBiz.coverData(queryRecord, dataTable.getFields());
            List<NutMap> dataList = new ArrayList<>();
            dataTable.getFields().stream().filter(TableFields::isFromDisplay).forEach(tableFields -> {
                dataList.add(new NutMap()
                        .setv("fieldType", tableFields.getFieldType())
                        .setv("fieldName", tableFields.getFieldName())
                        .setv("fieldLable", tableFields.getFromLable())
                        .setv("val", record.get(tableFields.getFieldName())));
            });
            return AjaxResult.sucess(dataList);
        } catch (Exception e) {
            return AjaxResult.error("错误！" + e.getLocalizedMessage());
        }
    }


    @GET
    @At("/fristLogin/ChangePass")
    @Ok("btl:WEB-INF/view/sys/user/fristLoginChangePass.html")
    public void fristLoginChangePass() {
        setRequestAttribute("userinfo", getSessionUserAccount());
    }

    @ApiOperation(value = "第一次登陆被强制要求修改密码", nickname = "fristLogin/ChangePass", tags = "账户信息", httpMethod = "POST", response = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldPass", paramType = "query", value = "旧密码", dataType = "string", required = true),
            @ApiImplicitParam(name = "newPass", paramType = "query", value = "新密码", dataType = "string", required = true),
            @ApiImplicitParam(name = "repeatNewPass", paramType = "query", value = "重复新密码", dataType = "string", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "{\"ok\": true,\"msg\": \"\",\"data\": \"修改成功\"}"),
    })
    @POST
    @At("/fristLogin/ChangePass")
    @Ok("json")
    public AjaxResult changePass(@Param("oldPass") String oldPass,
                                 @Param("newPass") String newPass,
                                 @Param("repeatNewPass") String repeatNewPass) {
        if (Strings.isEmpty(oldPass) || Strings.isEmpty(newPass) || Strings.isEmpty(repeatNewPass)) {
            return AjaxResult.error("密码不能为空");
        }
        newPass = newPass.trim();
        repeatNewPass = repeatNewPass.trim();
        if (newPass.length() < 6 || newPass.length() > 20) {
            return AjaxResult.error("密码长度不能小于6！不能大于20!");
        }
        if (!newPass.equals(repeatNewPass)) {
            return AjaxResult.error("新密码两次不相同！请检查！");
        }
        if (newPass.equals(oldPass)) {
            return AjaxResult.error("新旧密码不能相同！");
        }
        UserAccount account = getSessionUserAccount();
        Sha256Hash sha = new Sha256Hash(oldPass, account.getSalt());
        if (!sha.toHex().equals(account.getUserPass())) {
            return AjaxResult.error("密码错误！");
        }
        String salt = R.UU16();
        Sha256Hash newsha = new Sha256Hash(newPass, salt);
        account.setUserPass(newsha.toHex());
        account.setSalt(salt);
        accountService.update(account);

        //第一次登陆修改密码后保存一次登陆记录，不然下次登陆还会提示需要修改密码，进入死循环
        UserLoginHistory userLoginHistory = new UserLoginHistory();
        userLoginHistory.setUid(account.getId());
        userLoginHistory.setIp(WebUtil.ip(Mvcs.getReq()));
        userLoginHistory.setType(LoginTypeEnum.web);
        userLoginHistoryService.insert(userLoginHistory);

        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return AjaxResult.sucess("修改成功！");
    }


    @ApiOperation(value = "修改头像", nickname = "changeAvatar", tags = "账户信息", httpMethod = "POST", response = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "avatar", paramType = "query", value = "头像文件ID", dataType = "string", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "{\"ok\": true,\"msg\": \"\",\"data\": \"头像更换成功\"}"),
    })
    @POST
    @At("changeAvatar")
    @Ok("json")
    public AjaxResult changeAvatar(@Param("avatar") String avatar) {
        UserAccount account = getSessionUserAccount();
        account.setAvatar(avatar);
        accountService.update(account);
        return AjaxResult.sucess("头像更换成功");
    }


    @ApiOperation(value = "修改个人信息", nickname = "update", tags = "账户信息", httpMethod = "POST", response = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "avatar", paramType = "query", value = "头像文件ID，不传就表示不更新", dataType = "string"),
            @ApiImplicitParam(name = "oldPass", paramType = "query", value = "旧密码", dataType = "string", required = true),
            @ApiImplicitParam(name = "newPass", paramType = "query", value = "新密码", dataType = "string", required = true),
            @ApiImplicitParam(name = "repeatNewPass", paramType = "query", value = "重复新密码", dataType = "string", required = true),
            @ApiImplicitParam(name = "avatar", paramType = "query", value = "头像ID", dataType = "string", required = true),
            @ApiImplicitParam(name = "phone", paramType = "query", value = "手机号", dataType = "string", required = true),
            @ApiImplicitParam(name = "mail", paramType = "query", value = "邮箱", dataType = "string", required = true),
            @ApiImplicitParam(name = "realName", paramType = "query", value = "姓名", dataType = "string", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "{\"ok\": true,\"msg\": \"\",\"data\": \"操作成功\"}"),
    })
    @POST
    @At("update")
    @Ok("json")
    @Aop(TransAop.READ_COMMITTED)
    public AjaxResult update(@Param("oldPass") String oldPass,
                             @Param("newPass") String newPass,
                             @Param("repeatNewPass") String repeatNewPass,
                             @Param("avatar") String avatar,
                             @Param("phone") String phone,
                             @Param("mail") String mail,
                             @Param("realName") String realName,
                             Errors errors) {
        if (errors.hasError()) {
            return AjaxResult.error(errors.getErrorsList().iterator().next());
        }
        UserAccount account = getSessionUserAccount();
        //如果修改了密码需要重新登录
        boolean chanPass = false;
        if (!(Strings.isEmpty(oldPass) && Strings.isEmpty(newPass) && Strings.isEmpty(repeatNewPass))) {
            if (Strings.isEmpty(oldPass) || Strings.isEmpty(newPass) || Strings.isEmpty(repeatNewPass)) {
                return AjaxResult.error("密码不能为空");
            }
            if (newPass.length() < 6) {
                return AjaxResult.error("密码长度不能小于6！");
            }
            if (!newPass.equals(repeatNewPass)) {
                return AjaxResult.error("新密码两次不相同！请检查！");
            }
            if (newPass.equals(oldPass)) {
                return AjaxResult.error("新旧密码不能相同！");
            }
            Sha256Hash sha = new Sha256Hash(oldPass, account.getSalt());
            if (!sha.toHex().equals(account.getUserPass())) {
                return AjaxResult.error("密码错误！");
            }
        }
        if (Strings.isNotBlank(avatar)) {
            account.setAvatar(avatar);
        }
        if (!RegexUtil.isPhone(phone)) {
            return AjaxResult.error("手机号格式不正确！");
        }
        if (!RegexUtil.isEmail(mail)) {
            return AjaxResult.error("邮箱格式不正确！");
        }
        if (Strings.isEmpty(realName) || Strings.sNull(realName).length() < 2 || Strings.sNull(realName).length() > 100) {
            return AjaxResult.error("真实姓名不能为空,长度2-100！");
        }
        account.setPhone(phone);
        account.setMail(mail);
        account.setRealName(realName);
        if (chanPass){
            String salt = R.UU16();
            Sha256Hash newsha = new Sha256Hash(newPass, salt);
            account.setUserPass(newsha.toHex());
            account.setSalt(salt);
        }
        accountService.updateIgnoreNull(account);
        if (chanPass) {
            Subject subject = SecurityUtils.getSubject();
            subject.logout();
        } else {
            setSessionAttribute(Cons.SESSION_USER_KEY, account);
        }
        return AjaxResult.sucess(chanPass, "操作成功");
    }

    /**
     * 根据岗位查询人员
     *
     * @param jobId
     * @return
     */
    @ApiOperation(value = "根据条件查询人员", nickname = "queryByDeptOrJob", tags = "花名册", httpMethod = "POST", response = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deptId", paramType = "query", value = "部门id", dataType = "string", required = true),
            @ApiImplicitParam(name = "jobId", paramType = "query", value = "岗位id", dataType = "string", required = true),
            @ApiImplicitParam(name = "sort", paramType = "query", value = "排序", dataType = "integer", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "{\"ok\": true,\"msg\": \"用户获取成功!\",\"data\": \"\"}"),
    })
    @POST
    @At("queryByDeptOrJob")
    @Ok("json:{locked:'userpass|salt',ignoreNull:false,DateFormat:'yyyy-MM-dd HH:mm:ss'}")
    @RequiresPermissions("mobileInterface.functions.roster.queryByDeptOrJob")
    @AutoCreateMenuAuth(name = "根据筛选条件查询用户信息", type = 2, icon = "fa-cogs", parentPermission = "mobileInterface.functions.roster")
    public AjaxResult queryByDeptOrJob(@Param("jobId") String jobId, @Param("deptId") String deptId, @Param("sort") Integer sort) {
        return userAccountBiz.queryByJob(deptId, jobId, sort);
    }

    @POST
    @At("getUserInfoById")
    @Ok("json:{nullAsEmtry:true}")
    @ApiOperation(value = "人员基本信息", nickname = "getUserInfoById", tags = "花名册", httpMethod = "POST", response = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", paramType = "query", value = "用户id", dataType = "string", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "{\"ok\": true,\"msg\": \"\",\"data\": {\"fieldType\": 0,\"fieldName\": \"realname\",\"fieldLable\": \"\",\"val\": \"\"},{\"fieldType\": 0,\"fieldName\": \"phone\",\"fieldLable\": \"\",....}}"),
    })
    @RequiresPermissions("mobileInterface.functions.roster.getUserInfoById")
    @AutoCreateMenuAuth(name = "根据用户Id查看用户信息", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-cogs", parentPermission = "mobileInterface.functions.roster")
    public AjaxResult getUserInfoById(@Param("userId") String userId) {
        try {
            DataTable dataTable = tableService.fetchAllFields(1);
            Record queryRecord = tableService.dao().fetch(dataTable.getTableName(),
                    Cnd.where("userid", "=", userId),
                    dataMaintainBiz.getFieldsRegx(dataTable.getFields())
            );
            Record record = dataMaintainBiz.coverData(queryRecord, dataTable.getFields());
            List<NutMap> dataList = new ArrayList<>();
            dataTable.getFields().stream().filter(TableFields::isFromDisplay).forEach(tableFields -> {
                dataList.add(new NutMap()
                        .setv("fieldType", tableFields.getFieldType())
                        .setv("fieldName", tableFields.getFieldName())
                        .setv("fieldLable", tableFields.getFromLable())
                        .setv("val", record.get(tableFields.getFieldName())));
            });
            return AjaxResult.sucess(dataList);
        } catch (Exception e) {
            return AjaxResult.error("错误！" + e.getLocalizedMessage());
        }
    }

    /**
     * 后管通讯录查询用户
     *
     * @param key
     * @return
     */
    @POST
    @At("user_search")
    @Ok("json:{nullAsEmtry:true}")
    public AjaxResult user_search(@Param("key") String key,
                                  @Param("pageSize") int pageSize,
                                  @Param("pageNum") int pageNum) {
        try {
            List<NutMap> nutMaps = userAccountBiz.userSearch(key, pageNum, pageSize);
            return AjaxResult.sucess(nutMaps);
        } catch (Exception e) {
            return AjaxResult.error("错误！" + e.getLocalizedMessage());
        }
    }

    /**
     * 查询部门下人员
     *
     * @param deptId
     * @return
     */
    @POST
    @At("usersByDept")
    @Ok("json:{nullAsEmtry:true}")
    public AjaxResult usersByDept(@Param("deptId") String deptId) {
        try {
            List<NutMap> nutMaps = userAccountBiz.usersByDeptId(deptId);
            return AjaxResult.sucess(nutMaps);
        } catch (Exception e) {
            return AjaxResult.error("错误！" + e.getLocalizedMessage());
        }
    }

    @POST
    @At("permissions")
    @Ok("json")
    @ApiOperation(value = "权限信息", nickname = "permissions", tags = "账户信息", httpMethod = "POST", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "{ok: true,msg:'',data: {roles: ['superadmin'],stringPermissions: ['sys.index']}}"),
    })
    public AjaxResult permissions() {
        SimpleAuthorizationInfo auth = getSessionAttribute(Cons.SHIRO_AUTHORIZATION_INFO);
        if (auth != null) {
            return AjaxResult.sucess(auth);
        } else {
            return AjaxResult.error("未授权");
        }
    }

    @GET
    @At("userInfo")
    @Ok("json:{nullAsEmtry:true,dateFormat:'yyyy年MM月dd日',locked:'userPass|salt'}")
    @ApiOperation(value = "用户信息", nickname = "userInfo", tags = "账户信息", httpMethod = "POST", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "{ok: true,msg:'',data: {user:{},auth:{roles: ['superadmin'],stringPermissions: ['sys.index']}}}}"),
    })
    public AjaxResult userInfo() {
        return AjaxResult.sucess(NutMap.NEW().setv("user", getSessionUserAccount()).setv("auth", getSessionAttribute(Cons.SHIRO_AUTHORIZATION_INFO)));
    }
}
