/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.service.impl;

import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.modules.sys.entity.FileAttach;
import com.nutzfw.modules.sys.service.FileAttachService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created with IntelliJ IDEA.
 *
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2018/1/9  17:09
 * 描述此类：
 */
@IocBean(name = "fileAttachService", args = {"refer:dao"})
public class FileAttachServiceImpl extends BaseServiceImpl<FileAttach> implements FileAttachService {

    @Inject("java:$conf.get('attach.savePath')")
    private String parentPath;

    public FileAttachServiceImpl(Dao dao) {
        super(dao);
    }

    @Override
    public Path getPath(String id) {
        return Paths.get(parentPath, fetch(id).getSavedPath());
    }

    /**
     * 取得原始被引用的MD5附件记录
     * @param md5
     * @return
     */
    @Override
    public FileAttach fetchByMd5(String md5) {
        return this.fetch(Cnd.where("md5", "=", md5).and("referenceId", "is", null));
    }

    /**
     * 不允许在其他地方删除这个真实附件
     *
     * @param attach
     */
    @Override
    public void deleteFile(FileAttach attach) {
        //逻辑删除附件
        this.vDelete(attach.getId());
    }
}
