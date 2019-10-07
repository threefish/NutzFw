/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.action;

import com.alibaba.druid.filter.config.ConfigTools;
import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.util.MysqlBackUpUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.sys.entity.DatabaseBackup;
import com.nutzfw.modules.sys.service.DatabaseBackupService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import org.nutz.plugins.validation.Errors;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2019年01月17日
 * 数据库备份
 */
@IocBean
@At("/DatabaseBackup")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class DatabaseBackupAction {

    public static final  String PUBLIC_KEY  = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAI+ZK8I6WYpAnF0VeX0GdDwnwePt77b5pPhqRRz9zLkL4Ayu+7HFBnuJ6EH7hLWYjCnvBgJCeBZOcEuB3n7NeX8CAwEAAQ==";
    final static         Log    log         = Logs.get();
    private static final String PRIVATE_KEY = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAj5krwjpZikCcXRV5fQZ0PCfB4+3vtvmk+GpFHP3MuQvgDK77scUGe4noQfuEtZiMKe8GAkJ4Fk5wS4Hefs15fwIDAQABAkAOab9cpWKrX8TSCv/Ars8n4o2xhQZUhUYrsO7y8/6CbCkFDHjLY+EAy/u3Mu9sFqt4o+Rn+GP7pSH/j0ZrmlthAiEA9hij7M4Gyw5+y4aMxiw+ZWz29Mdlef9XHekrQwR6cccCIQCVYJC1Erq22KavU+LtlkTrYHKd9hqP36jgt8B2yIS6iQIhAIEAdZ2Cekki9hBWHaEcPDEKp4G0rFsBIHeLbKQaytytAiBaY7GM8IVtaVIL2/4AeKLBr34L2cUe9F8zg92BwzqVyQIgJO6lI++s3mSl3Lr6i/69axD47wUfQI0TCIWhUSYRLBM=";
    @Inject
    DatabaseBackupService databaseBackupService;

    /**
     * 管理页面
     *
     * @return
     */
    @At("/index")
    @Ok("btl:WEB-INF/view/sys/setting/DatabaseBackup/index.html")
    @RequiresPermissions("DatabaseBackup.index")
    @AutoCreateMenuAuth(name = "数据库备份管理", icon = "fa-cogs", parentPermission = "sys.monitor")
    public NutMap index() {
        NutMap data = NutMap.NEW();
        return data;
    }

    /**
     * 列表管理
     *
     * @param pageNum
     * @param pageSize
     * @param key
     * @return
     */
    @At("/list")
    @Ok("json:{nullAsEmtry:true,dateFormat:'yyyy年MM月dd日',locked:'userPass'}")
    @RequiresPermissions("DatabaseBackup.index")
    public LayuiTableDataListVO list(@Param("pageNum") int pageNum,
                                     @Param("pageSize") int pageSize,
                                     @Param("key") String key
    ) {
        Cnd cnd = Cnd.NEW();
        if (Strings.isNotBlank(key)) {
            cnd.and("name", "like", "%" + key + "%");
        }
        cnd.desc("opAt");
        return databaseBackupService.listPage(pageNum, pageSize, cnd);
    }

    /**
     * 新增、编辑保存页面
     *
     * @param uuid
     * @return
     */
    @At("/edit")
    @Ok("btl:WEB-INF/view/sys/setting/DatabaseBackup/edit.html")
    @RequiresPermissions("DatabaseBackup.index.edit")
    @AutoCreateMenuAuth(name = "新增/编辑数据库备份", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-cogs", parentPermission = "DatabaseBackup.index")
    public NutMap edit(@Param("uuid") String uuid) {
        return NutMap.NEW().setv("uuid", uuid).setv("fromDataEdit", true);
    }

    /**
     * 新增、编辑保存页面
     *
     * @param uuid
     * @return
     */
    @At("/view")
    @Ok("btl:WEB-INF/view/sys/setting/DatabaseBackup/edit.html")
    @RequiresPermissions("DatabaseBackup.index.edit")
    @AutoCreateMenuAuth(name = "查看详情", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-cogs", parentPermission = "DatabaseBackup.index")
    public NutMap view(@Param("uuid") String uuid) {
        return NutMap.NEW().setv("uuid", uuid).setv("fromDataEdit", false);
    }

    /**
     * 加载详情
     *
     * @param uuid
     * @return
     */
    @At("/details")
    @Ok("json:{nullAsEmtry:true,dateFormat:'yyyy-MM-dd',locked:'userPass'}")
    @RequiresPermissions("DatabaseBackup.index.edit")
    public AjaxResult details(@Param("uuid") String uuid) {
        return AjaxResult.sucess(databaseBackupService.fetch(uuid));
    }


    /**
     * 批量删除
     *
     * @param uuids
     * @return
     */
    @At("/del")
    @Ok("json")
    @RequiresPermissions("DatabaseBackup.index.del")
    @AutoCreateMenuAuth(name = "批量删除", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-cogs", parentPermission = "DatabaseBackup.index")
    public AjaxResult del(@Param("::uuids") String[] uuids) {
        databaseBackupService.deleteByUUIDs(uuids);
        return AjaxResult.sucess("删除成功");
    }

    /**
     * 保存
     *
     * @param data
     * @return
     */
    @At("/save")
    @Ok("json")
    @POST
    @RequiresPermissions("DatabaseBackup.index.edit")
    @Aop(TransAop.READ_UNCOMMITTED)
    public AjaxResult save(@Param("::fromData") DatabaseBackup data, Errors errors) {
        if (errors.hasError()) {
            return AjaxResult.error(errors.getErrorsList().iterator().next());
        }
        Cnd checkCnd = Cnd.NEW();
        if (Strings.isNotBlank(data.getUuid())) {
            checkCnd.and("name", "=", data.getName()).and("uuid", "!=", data.getUuid());
        } else {
            checkCnd.and("name", "=", data.getName());
        }
        int count = databaseBackupService.count(checkCnd);
        if (count > 0) {
            return AjaxResult.errorf("【{0}】名称已经存在!", data.getName());
        } else {
            try {
                String msg = MysqlBackUpUtil.testConn(Strings.splitIgnoreBlank(data.getDbNames())[0], data.getIp(), data.getPort(), data.getUserName(), data.getUserPass());
                if (msg == null) {
                    String encryptPass = ConfigTools.encrypt(PRIVATE_KEY, data.getUserPass());
                    data.setUserPass(encryptPass);
                    databaseBackupService.insertOrUpdate(data);
                    return AjaxResult.sucessMsg("保存成功");
                } else {
                    return AjaxResult.errorf("保存失败！{0}", msg);
                }
            } catch (Exception e) {
                log.error(e);
                return AjaxResult.error("保存失败！");
            }
        }
    }
}
