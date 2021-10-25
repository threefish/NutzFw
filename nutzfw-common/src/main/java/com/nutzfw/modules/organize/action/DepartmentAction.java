/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.organize.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.annotation.SysLog;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.util.StringUtil;
import com.nutzfw.core.common.util.ViewUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.organize.entity.Department;
import com.nutzfw.modules.organize.entity.DepartmentLeader;
import com.nutzfw.modules.organize.service.DepartmentLeaderService;
import com.nutzfw.modules.organize.service.DepartmentService;
import com.nutzfw.modules.organize.vo.DeptJobTreeVO;
import com.nutzfw.modules.sys.service.FileAttachService;
import io.swagger.annotations.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Encoding;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.*;
import org.nutz.plugins.validation.Errors;

import java.io.File;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/23
 * 描述此类：
 */
@IocBean
@At("/sysOrganize/department")
@Api("/sysOrganize/department")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class DepartmentAction extends BaseAction {

    @Inject
    DepartmentService       departmentService;
    @Inject
    FileAttachService       fileAttachService;
    @Inject
    DepartmentLeaderService departmentLeaderService;

    @Ok("json")
    @POST
    @At("/loadHeadList")
    public List<DepartmentLeader> loadHeadList(@Param("deptId") String deptId, @Param("leaderType") String leaderType) {
        return departmentLeaderService.query(Cnd.where("deptId", "=", deptId).and("leaderType", "=", leaderType));
    }

    @Ok("btl:WEB-INF/view/sys/organize/department/index.html")
    @GET
    @At("/index")
    @RequiresPermissions("sysOrganize.department.index")
    @AutoCreateMenuAuth(name = "部门机构设置", icon = "fa-bookmark", shortNo = 1, parentPermission = "sysOrganize.index")
    public void departmentIndex() {
    }

    @Ok("json:{locked:'opAt|opBy'}")
    @POST
    @At("/tree")
//    @RequiresPermissions("sysOrganize.department.index")
    public List<Department> tree() {
        return departmentService.tree();
    }

    @Ok("json:{locked:'opAt|opBy'}")
    @POST
    @GET
    @At("/treeAboutJob")
    @RequiresPermissions("sysOrganize.department.treeAboutJob")
    @AutoCreateMenuAuth(name = "获取部门(包含岗位)", type = AutoCreateMenuAuth.RESOURCE, permission = "treeAboutJob", parentPermission = "sysOrganize.department.index")
    public List<DeptJobTreeVO> treeAboutJob() {
        return departmentService.treeAboutJob();
    }

    /**
     * ysy
     *
     * @return
     */
    @Ok("json:{locked:'opAt|opBy'}")
    @POST
    @At("/treeAboutJobAndCount")
    @RequiresPermissions("sysOrganize.department.treeAboutJobAndCount")
    @AutoCreateMenuAuth(name = "获取部门(包含岗位)(包含人员数量)", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysOrganize.department.index")
    public List<DeptJobTreeVO> treeAboutJobAndCount() {
        return departmentService.treeAboutJobAndCount();
    }

    /**
     * ysy
     *
     * @return
     */
    @Ok("json:{locked:'opAt|opBy'}")
    @POST
    @At("/treeAboutCount")
    @RequiresPermissions("sysOrganize.department.treeAboutCount")
    @AutoCreateMenuAuth(name = "获取部门(包含人员数量)", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysOrganize.department.index")
    public List<DeptJobTreeVO> treeAboutCount() {
        return departmentService.treeAboutCount();
    }

    /**
     * 前端获取部门下拉 by ysy
     *
     * @return
     */
    @ApiOperation(value = "获取部门下拉", nickname = "departmentSelects", tags = "花名册", httpMethod = "POST", response = String.class)
    @ApiImplicitParams({})
    @ApiResponses({
            @ApiResponse(code = 200, message = "{\"ok\": true,\"msg\": \"\",\"data\": \"操作成功\"}"),
    })
    @Ok("json:{locked:'opAt|opBy'}")
    @POST
    @At("/departmentSelects")
    @RequiresPermissions("sysOrganize.department.index")
    public AjaxResult departmentSelects() {
        return departmentService.getAllDeptSelect();
    }


    @Ok("json")
    @POST
    @At("/save")
    @RequiresPermissions("sysOrganize.department.add")
    @AutoCreateMenuAuth(name = "添加部门", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysOrganize.department.index")
    @Aop(TransAop.READ_COMMITTED)
    public AjaxResult save(@Param("::data.") Department department, @Param("::head") List<DepartmentLeader> head, @Param("::leader") List<DepartmentLeader> leader, Errors errors) {
        try {
            if (errors.hasError()) {
                return AjaxResult.error(errors.getErrorsList().iterator().next());
            }
            if ("".equals(Strings.sNull(department.getId()).trim())) {
                int maxShortNo = departmentService.func("max", "short_no", Cnd.where("pid", "=", department.getPid()));
                department.setShortNo(maxShortNo);
                departmentService.insert(department);
            } else {
                List<Department> childs = departmentService.query(Cnd.where("pid", "=", department.getId()));
                childs.forEach(d -> d.setParentName(department.getName()));
                departmentLeaderService.delete(Cnd.where("deptId", "=", department.getId()));
                departmentService.updateIgnoreNull(department);
                departmentService.update(childs);
            }
            String deptId = department.getId();
            List<DepartmentLeader> insertList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(head)) {
                head.forEach(departmentLeader -> {
                    departmentLeader.setDeptId(deptId);
                    insertList.add(departmentLeader);
                });
            }
            if (CollectionUtils.isNotEmpty(leader)) {
                leader.forEach(departmentLeader -> {
                    departmentLeader.setDeptId(deptId);
                    insertList.add(departmentLeader);
                });
            }
            departmentLeaderService.insert(insertList);
            return AjaxResult.sucess(department, "操作成功");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @Ok("json")
    @POST
    @At("/sort")
    @RequiresPermissions("sysOrganize.department.sort")
    @AutoCreateMenuAuth(name = "部门拖动排序", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysOrganize.department.index")
    public AjaxResult sort(@Param("::") NutMap map) {
        try {
            departmentService.sort(map);
            return AjaxResult.sucess("操作成功");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 模板下载
     *
     * @return
     */
    @At("/downTemplate")
    @Ok("raw")
    @GET
    public Object downTemplate() {
        try {
            File file = departmentService.createDownTemplate().toFile();
            Mvcs.getResp().setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("部门信息导入.xlsx", Encoding.UTF8));
            return file;
        } catch (Exception e) {
            return ViewUtil.toErrorPage("模版生成失败！" + e.getLocalizedMessage());
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
    @RequiresPermissions("sysOrganize.department.importData")
    @AutoCreateMenuAuth(name = "导入部门", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysOrganize.department.index")
    @SysLog(tag = "部门导入", template = "为部门${args[1]} 导入部门 ${re.ok?'成功':'失败'+re.msg}", result = true, param = true)
    public AjaxResult checkImportData(@Param("attachId") String attachId, @Param("deptId") String deptId) {
        try {
            if (StringUtil.isBlank(attachId)) {
                return AjaxResult.error("没有找到文件id");
            }
            if (StringUtil.isBlank(deptId)) {
                return AjaxResult.error("没有选择要导入的部门");
            }
            return departmentService.importDepartment(attachId, deptId);
        } catch (Exception e) {
            return AjaxResult.error("解析失败");
        }

    }

    /**
     * 模板下载
     *
     * @return
     */
    @At("/downErrFile")
    @Ok("raw")
    public Object downErrFile(@Param("attachId") String attachId) {
        try {
            Path attachPath = fileAttachService.getPath(attachId);
            File file = attachPath.toFile();
            Mvcs.getResp().setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("部门信息导入_结果.xlsx", Encoding.UTF8));
            return file;
        } catch (Exception e) {
            return ViewUtil.toErrorPage("文件下载失败！" + e.getLocalizedMessage());
        }
    }
}
