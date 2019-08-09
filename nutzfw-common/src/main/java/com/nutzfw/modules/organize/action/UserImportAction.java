package com.nutzfw.modules.organize.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.util.ViewUtil;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.organize.service.UserImportHistoryService;
import com.nutzfw.modules.sys.service.FileAttachService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Encoding;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.*;

import java.io.File;
import java.net.URLEncoder;
import java.nio.file.Path;

/**
 * @author 叶世游
 * @date 2018/6/23 14:33
 * @description 用户导入记录查询
 */

@IocBean
@At("/sysUserImport")
public class UserImportAction extends BaseAction {
    @Inject
    UserImportHistoryService userImportHistoryService;
    @Inject
    FileAttachService fileAttachService;

    @Ok("btl:WEB-INF/view/sys/organize/user/userImportHistory.html")
    @GET
    @At("/index")
    @RequiresPermissions("sysUserImport.index")
    @AutoCreateMenuAuth(name = "用户导入历史", icon = "fa-users", shortNo = 4, parentPermission = "sysOrganize.index")
    public void index() {
    }

    /**
     * 获取导入历史
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Ok("json:{ignoreNull:false,DateFormat:'yyyy-MM-dd HH:mm:ss'}")
    @POST
    @At("/listPage")
    @RequiresPermissions("sysUserImport.index")
    public LayuiTableDataListVO listPage(@Param("pageNum") int pageNum,
                                         @Param("pageSize") int pageSize
    ) {
        try {
            return userImportHistoryService.listPage(pageNum, Cnd.where("delFlag", "=", 0).desc("opAt"));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new LayuiTableDataListVO();
    }

    /**
     * 模板下载
     *
     * @return
     */
    @At("/downTemplate")
    @Ok("raw")
    public Object downTemplate(@Param("attachId") String attachId) {
        try {
            Path attachPath = fileAttachService.getPath(attachId);
            File file = attachPath.toFile();
            Mvcs.getResp().setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("用户信息导入_结果.xlsx", Encoding.UTF8));
            return file;
        } catch (Exception e) {
            return ViewUtil.toErrorPage("文件下载失败！" + e.getLocalizedMessage());
        }
    }
}
