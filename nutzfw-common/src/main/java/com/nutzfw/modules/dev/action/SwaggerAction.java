/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.dev.action;

import com.nutzfw.core.MainModule;
import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import io.swagger.models.Info;
import io.swagger.models.Swagger;
import io.swagger.servlet.Reader;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.*;
import org.nutz.resource.Scans;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2017/12/25  19:30
 */
@IocBean(create = "init")
@At("/swagger")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class SwaggerAction {

    private static String[] PACKAGES = MainModule.class.getAnnotation(Modules.class).packages();

    Swagger swagger = new Swagger();

    @Ok("btl:WEB-INF/view/dev/swagger/index.html")
    @At("/index")
    @RequiresRoles(Cons.SESSION_USER_ROLE_CODE)
    @RequiresPermissions("swagger.index")
    @AutoCreateMenuAuth(name = "swagger API", icon = "fa-eye", parentPermission = "sys.index")
    public void index() {
    }


    @Ok("void")
    @At("/swagger")
    @RequiresRoles(Cons.SESSION_USER_ROLE_CODE)
    public void swagger(HttpServletRequest request, HttpServletResponse response, @Param("force") boolean force) throws Exception {
        String swJson = "/swagger.json";
        if (force) {
            //强制刷新
            init();
        }
        final String pathInfo = request.getRequestURI();
        if (pathInfo.endsWith(swJson)) {
            response.setContentType("application/json");
            response.getWriter().println(Json.toJson(swagger, JsonFormat.compact()));
        } else {
            response.setStatus(404);
        }
    }

    public void init() {
        Info info = new Info();
        info.title("API接口文档");
        info.setDescription("此文档主要针对APP客户端");
        info.setVersion("APP 1.0");
        swagger.info(info);
        swagger.setBasePath(Mvcs.getServletContext().getContextPath());
        HashSet<Class<?>> classes = new HashSet<>();
        for (String packages : PACKAGES) {
            for (Class<?> klass : Scans.me().scanPackage(packages)) {
                classes.add(klass);
            }
        }
        Reader.read(swagger, classes);
    }
}