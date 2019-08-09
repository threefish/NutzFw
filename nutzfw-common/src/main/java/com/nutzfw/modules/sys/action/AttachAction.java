package com.nutzfw.modules.sys.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.common.action.FileAction;
import com.nutzfw.modules.sys.biz.DictBiz;
import com.nutzfw.modules.sys.entity.FileAttach;
import com.nutzfw.modules.sys.service.FileAttachService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/28
 * 描述此类：附件管理
 */
@IocBean
@At("/sysAttach")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class AttachAction extends BaseAction {

    @Inject
    DictBiz dictBiz;
    @Inject
    private FileAttachService fileAttachService;

    @Ok("btl:WEB-INF/view/sys/setting/attach/index.html")
    @GET
    @At({"", "/index"})
    @RequiresPermissions("sysAttach.index")
    @AutoCreateMenuAuth(name = "附件管理", icon = "fa-eye", parentPermission = "sys.monitor")
    public NutMap index() {
        return NutMap.NEW().setv(FileAction.SYS_ATTACH_TYPE, dictBiz.getDictByValueEnumsJson(FileAction.SYS_ATTACH_TYPE));
    }

    @GET
    @POST
    @At("/query")
    @Ok("json")
    @RequiresPermissions("sysAttach.index")
    public LayuiTableDataListVO query(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize) {
        LayuiTableDataListVO tableDataListVO = fileAttachService.listPage(pageNum, pageSize, Cnd.orderBy().desc("addtime"));
        List<FileAttach> voData = tableDataListVO.getData();
        List<NutMap> dataList = new ArrayList<>();
        voData.forEach(fileAttach -> {
            dataList.add(NutMap.NEW()
                    .setv("id", fileAttach.getId())
                    .setv("attachtype", fileAttach.getAttachtype())
                    .setv("fileName", fileAttach.getFileName())
                    .setv("ext", Files.getSuffixName(fileAttach.getFileName()))
                    .setv("filesize", Strings.formatSizeForReadBy1024(fileAttach.getFilesize()))
                    .setv("adduser", fileAttach.getAdduser())
                    .setv("addtime", fileAttach.getAddtime())
                    .setv("opBy", fileAttach.getOpBy())
                    .setv("md5", fileAttach.getMd5())
                    .setv("opByDesc", fileAttach.getOpByDesc())
                    .setv("opAt", fileAttach.getOpAt())
                    .setv("delFlag", fileAttach.getDelFlag())
            );
            fileAttach.getFilesize();
        });
        tableDataListVO.setData(dataList);
        return tableDataListVO;
    }

}
