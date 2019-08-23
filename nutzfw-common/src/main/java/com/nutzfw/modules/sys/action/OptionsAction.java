package com.nutzfw.modules.sys.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.license.LicenseConfig;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.plugin.view.BeetlViewMaker;
import com.nutzfw.modules.sys.entity.Options;
import com.nutzfw.modules.sys.service.OptionsService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.*;
import org.nutz.plugins.validation.Errors;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/21
 * 描述此类：
 */
@IocBean
@At("/sysOptions")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class OptionsAction {


    @Inject
    LicenseConfig licenseConfig;
    @Inject
    private OptionsService optionsService;

    @Ok("btl:WEB-INF/view/sys/setting/options/index.html")
    @GET
    @At("/manager")
    @RequiresPermissions("sysOptions.index")
    @AutoCreateMenuAuth(name = "系统设置", icon = "fa-wrench", parentPermission = "sys.index")
    public void manager() {
    }

    @POST
    @At("/changeProductLogo")
    @Ok("json")
    @RequiresPermissions("sys.changeProductLogo")
    @AutoCreateMenuAuth(name = "修改系统信息", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-wrench", parentPermission = "sysOptions.index")
    public AjaxResult changeProductLogo(@Param("id") String id) {
        Cons.optionsCach.setProductLogo(id);
        optionsService.updateIgnoreNull(Cons.optionsCach);
        BeetlViewMaker.updateBeetlGroupTemplate(Mvcs.getIoc());
        return AjaxResult.sucess("更换成功");
    }

    @Ok("json")
    @POST
    @At("/update")
    @RequiresPermissions("sysOptions.update")
    @AutoCreateMenuAuth(name = "修改系统信息", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysOptions.index")
    public AjaxResult update(@Param("::data.") Options options, Errors errors) {
        try {
            if (errors.hasError()) {
                return AjaxResult.error(errors.getErrorsList().iterator().next());
            }
            options.setId("0");
            optionsService.updateIgnoreNull(options);
            Cons.optionsCach = optionsService.fetch("0");
            BeetlViewMaker.updateBeetlGroupTemplate(Mvcs.getIoc());
            licenseConfig.install();
            return AjaxResult.sucess(Cons.optionsCach, "修改成功");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

}
