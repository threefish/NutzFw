package com.nutzfw.modules.flow.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.util.ViewUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.core.plugin.flowable.service.FlowProcessDefinitionService;
import com.nutzfw.modules.common.action.BaseAction;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/9
 */
@IocBean
@At("/flow/process")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class FlowProcessAction extends BaseAction {


    @Inject
    FlowProcessDefinitionService flowProcessDefinitionService;

    @GET
    @At("/index")
    @Ok("btl:WEB-INF/view/modules/flow/process/index.html")
    @RequiresPermissions("sys.flow.process")
    @AutoCreateMenuAuth(name = "流程管理", icon = "fa-tasks", parentPermission = "sys.flow")
    public NutMap index() {
        return NutMap.NEW();
    }

    @POST
    @At("/listPage")
    @Ok("json:{nullAsEmtry:true,locked:'metaInfo|originalPersistentState'}")
    @RequiresPermissions("sys.flow.process")
    public LayuiTableDataListVO listPage(HttpServletRequest request) {
        return flowProcessDefinitionService.processList(LayuiTableDataListVO.get(request), "");
    }

    @POST
    @At("/deleteDeployment")
    @Ok("json")
    @RequiresPermissions("sys.flow.process.deleteDeployment")
    @AutoCreateMenuAuth(name = "删除部署流程", icon = "fa-tasks", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sys.flow.process")
    public AjaxResult deleteDeployment(@Param("deploymentId") String deploymentId) {
        flowProcessDefinitionService.deleteDeployment(deploymentId);
        return AjaxResult.sucess();
    }

    @POST
    @At("/update/?")
    @Ok("json")
    @RequiresPermissions("sys.flow.process.update")
    @AutoCreateMenuAuth(name = "激活、挂起", icon = "fa-tasks", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sys.flow.process")
    public AjaxResult deleteDeployment(String state, @Param("procDefId") String procDefId) {
        return AjaxResult.sucessMsg(flowProcessDefinitionService.updateState(state, procDefId));
    }


    /**
     * 读取资源，通过部署ID
     *
     * @param procDefId 流程定义ID
     * @param proInsId  流程实例ID
     * @param resType   资源类型(xml|image)
     * @param response
     * @throws Exception
     */
    @At("/resource/read")
    @GET
    @Ok("raw")
    public Object export(String procDefId, String proInsId, String resType, HttpServletResponse response) {
        try {
            InputStream resourceAsStream = flowProcessDefinitionService.resourceRead(procDefId, proInsId, resType);
            final String xmlResType = "xml";
            if (xmlResType.equals(resType)) {
                response.setHeader("Content-Type", "text/xml");
            } else {
                response.setHeader("Content-Type", "image/jpeg");
            }
            int cache = 1024;
            byte[] b = new byte[cache];
            int len;
            while ((len = resourceAsStream.read(b, 0, cache)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
            return null;
        } catch (Exception e) {
            log.error("读取资源文件失败", e);
            return ViewUtil.toErrorPage("查看{1}文件失败：procDefId={2} 错误信息：{3}" + procDefId, resType, e.getLocalizedMessage());
        }
    }

}
