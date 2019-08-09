package com.nutzfw.modules.portal.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.util.Base64Tool;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.portal.entity.StatisticsConfigure;
import com.nutzfw.modules.portal.service.StatisticsConfigureService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;
import org.nutz.plugins.validation.Errors;

/**
 * @author 叶世游
 * @date: 2018/6/19
 * 描述此类：统计功能配置
 */
@IocBean
@At("/statistics")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class StatisticsConfigureAction extends BaseAction {

    @Inject
    StatisticsConfigureService statisticsConfigureService;

    @At("/manager")
    @Ok("btl:WEB-INF/view/portal/statistics.html")
    @RequiresPermissions("statistics.manager")
    @AutoCreateMenuAuth(name = "统计管理", icon = "fa-cogs", parentPermission = "portal.index", shortNo = 3)
    public void manager() {
    }

    /**
     * 查询统计列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Ok("json:{locked:'opAt|opBy'}")
    @At("/query")
    @POST
    @RequiresPermissions("statistics.query")
    @AutoCreateMenuAuth(name = "查询统计列表", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-cogs", parentPermission = "statistics.manager")
    public LayuiTableDataListVO query(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize) {
        return statisticsConfigureService.listPage(pageNum, pageSize, Cnd.where("delFlag", "=", "0").asc("sort"));
    }

    /**
     * 保存统计
     *
     * @param statisticsConfigure
     * @return
     */
    @Ok("json:{locked:'opAt|opBy'}")
    @At("/save")
    @POST
    @RequiresPermissions("statistics.save")
    @AutoCreateMenuAuth(name = "保存统计", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-cogs", parentPermission = "statistics.manager")
    public AjaxResult save(@Param("::") StatisticsConfigure statisticsConfigure, Errors errors) {
        if (errors.hasError()) {
            return AjaxResult.error(errors.getErrorsList().iterator().next());
        }
        try {

            String sql = new String(Base64Tool.decode(statisticsConfigure.getSqlStr()));
            statisticsConfigure.setSqlStr(sql);

            return statisticsConfigureService.save(statisticsConfigure);

        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("保存失败");
        }
    }

    /**
     * 批量删除统计
     *
     * @param ids
     * @return
     */
    @Ok("json:{locked:'opAt|opBy'}")
    @At("/del")
    @POST
    @RequiresPermissions("statistics.del")
    @AutoCreateMenuAuth(name = "批量删除统计", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-cogs", parentPermission = "statistics.manager")
    public AjaxResult del(@Param("ids") String[] ids) {
        try {
            int count = statisticsConfigureService.del(ids);
            if (count > 0) {
                return AjaxResult.sucess("删除成功!");
            } else {
                return AjaxResult.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("删除失败");
        }
    }

    /**
     * 图像预览
     *
     * @param sqlStr
     * @return
     */
    @Ok("json:{locked:'opAt|opBy'}")
    @At("/showNow")
    @POST
    public AjaxResult showNow(@Param("sqlStr") String sqlStr) {
        try {
            String sql = new String(Base64Tool.decode(sqlStr));
            return statisticsConfigureService.showNow(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("查询语句错误,请检查");
        }
    }

}
