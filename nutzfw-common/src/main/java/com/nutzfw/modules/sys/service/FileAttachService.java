/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.service;


import com.nutzfw.core.common.service.BaseService;
import com.nutzfw.modules.sys.entity.FileAttach;

import java.nio.file.Path;

/**
 * Created with IntelliJ IDEA Code Generator
 *
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年01月02日 15时39分07秒
 * 功能描述： 附件管理
 */
public interface FileAttachService extends BaseService<FileAttach> {

    /**
     * 取得附件地址
     * @param id
     * @return
     */
    Path getPath(String id);

    /**
     * 取得原始被引用的MD5附件记录
     * @param md5
     * @return
     */
    FileAttach fetchByMd5(String md5);

    /**
     * 逻辑删除附件记录
     * @param attach
     */
    void deleteFile(FileAttach attach);
}
