package com.nutzfw.modules.organize.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.annotation.SysLog;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.util.StringUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.organize.entity.Job;
import com.nutzfw.modules.organize.service.DepartmentJobService;
import com.nutzfw.modules.organize.service.JobService;
import com.nutzfw.modules.sys.biz.DictBiz;
import com.nutzfw.modules.sys.biz.JobBiz;
import io.swagger.annotations.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;
import org.nutz.plugins.validation.Errors;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/1
 * 描述此类：
 */
@IocBean
@At("/sysOrganize/job")
@Api("/sysOrganize/job")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class JobAction extends BaseAction {

    @Inject
    JobService jobService;

    @Inject
    DepartmentJobService departmentJobService;

    @Inject
    JobBiz jobBiz;

    @Inject
    DictBiz dictBiz;


    @Ok("btl:WEB-INF/view/sys/organize/job/index.html")
    @GET
    @At("/index")
    @RequiresPermissions("sysOrganize.job.index")
    @AutoCreateMenuAuth(name = "岗位管理", icon = "fa-bookmark", shortNo = 2, parentPermission = "sysOrganize.index")
    public void departmentIndex() {
        setRequestAttribute("naturesName", dictBiz.getDictEnumsJson("sys_user_natures"));
        setRequestAttribute("categoryName", dictBiz.getDictEnumsJson("sys_user_category"));
    }

    @POST
    @At("/query")
    @Ok("json")
    @RequiresPermissions("sysOrganize.job.index")
    public LayuiTableDataListVO query(@Param("pageNum") int pageNum,
                                      @Param("pageSize") int pageSize,
                                      @Param("name") String name,
                                      @Param("category") int category,
                                      @Param("nature") int nature) {
        Cnd cnd = Cnd.where("delFlag", "=", 0);
        if (!StringUtil.isBlank(name)) {
            cnd.and(Cnd.exps("code", "like binary", "%" + name + "%")
                    .or("name", "like", "%" + name + "%"));
        }
        if (category != 0) {
            cnd.and("category", "=", category);
        }
        if (nature != 0) {
            cnd.and("nature", "=", nature);
        }
        return jobService.listPage(pageNum, pageSize, cnd.desc("opAt"));
    }

    /**
     * 新增，编辑岗位
     */
    @POST
    @At("/saveOrUpdate")
    @Ok("json")
    @RequiresPermissions("sysOrganize.job.saveOrUpdate")
    @AutoCreateMenuAuth(name = "岗位管理", icon = "fa-bookmark", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysOrganize.job.index")
    public AjaxResult saveOrUpdate(@Param("::data.") Job job, Errors errors) {
        if (errors.hasError()) {
            return AjaxResult.error(errors.getErrorsList().iterator().next());
        }
        try {
            return jobBiz.saveOrUpdate(job);
        } catch (Exception e) {
            log.error(e);
        }
        return AjaxResult.sucessMsg("操作失败");
    }

    /**
     * 分配部门
     */
    @POST
    @At("/allocation")
    @Ok("json")
    @RequiresPermissions("sysOrganize.job.allocation")
    @AutoCreateMenuAuth(name = "分配给部门", icon = "fa-bookmark", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysOrganize.job.index")
    public AjaxResult allocation(@Param("jobId") String jobId, @Param("deptId") String deptId, @Param("deptName") String deptName) {
        try {
            return jobBiz.allocation(jobId, deptId, deptName);
        } catch (Exception e) {
            log.error(e);
        }
        return AjaxResult.error("操作失败");
    }

    /**
     * 取消分配部门
     */
    @POST
    @At("/changeAllocation")
    @Ok("json")
    @RequiresPermissions("sysOrganize.job.changeAllocation")
    @AutoCreateMenuAuth(name = "取消分配", icon = "fa-bookmark", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysOrganize.job.index")
    public AjaxResult changeAllocation(@Param("jobId") String jobId, @Param("deptId") String deptId, @Param("deptName") String deptName) {
        try {
            return jobBiz.changeAllocation(jobId, deptId, deptName);
        } catch (Exception e) {
            log.error(e);
        }
        return AjaxResult.error("操作失败");
    }


    /**
     * 删除岗位
     */
    @POST
    @At("/del")
    @Ok("json")
    @RequiresPermissions("sysOrganize.job.del")
    @AutoCreateMenuAuth(name = "删除", icon = "fa-bookmark", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysOrganize.job.index")
    @SysLog(tag = "部门管理", template = "删除部门[${args[0]}] ${re.ok?'成功':'失败'+re.msg}", result = true, param = true)
    public AjaxResult del(@Param("id") String id) {
        try {
            return jobBiz.delJob(id);
        } catch (Exception e) {
            log.error(e);
        }
        return AjaxResult.error("操作失败");
    }

    @POST
    @At("/queryByDepartment")
    @Ok("json")
    @RequiresPermissions("sysOrganize.job.queryByDepartment")
    @AutoCreateMenuAuth(name = "根据部门查询岗位", type = AutoCreateMenuAuth.RESOURCE, permission = "sysOrganize.job.queryByDepartment", parentPermission = "sysOrganize.job.index")
    public AjaxResult queryByDepartment(@Param("deptId") String deptId) {
        try {
            return jobBiz.queryByDepartment(deptId);
        } catch (Exception e) {
            log.error(e);
        }
        return AjaxResult.error("操作失败");
    }

    @ApiOperation(value = "获取岗位下拉", nickname = "queryByDepartment", tags = "花名册", httpMethod = "POST", response = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deptId", paramType = "query", value = "部门Id,必须", dataType = "string", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "{\"ok\": true,\"msg\": \"\",\"data\": \"操作成功\"}"),
    })
    @POST
    @At("/queryJobByDepartment")
    @Ok("json")
    @RequiresPermissions("sysOrganize.job.queryByDepartment")
    public AjaxResult queryByDepartmentForMobile(@Param("deptId") String deptId) {
        try {
            return jobBiz.queryByDepartmentAll(deptId);
        } catch (Exception e) {
            log.error(e);
        }
        return AjaxResult.error("操作失败");
    }

    /**
     * 岗位分布页面跳转
     */
    @GET
    @At("/distributionPage")
    @Ok("btl:WEB-INF/view/sys/organize/job/JobDistribution.html")
    @RequiresPermissions("sysOrganize.job.distributionPage")
    @AutoCreateMenuAuth(name = "岗位分布", icon = "fa-bookmark", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysOrganize.job.index")
    public void distributionPage() {

    }


    /**
     * 岗位分布
     */
    @GET
    @At("/jobDistr")
    @Ok("json")
    @RequiresPermissions("sysOrganize.job.jobDistr")
    @AutoCreateMenuAuth(name = "岗位分布", icon = "fa-bookmark", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysOrganize.index")
    public AjaxResult jobDistr() {
        try {
            return jobBiz.jobDistr();
        } catch (Exception e) {
            log.error(e);
        }
        return AjaxResult.error("操作失败");
    }


}
