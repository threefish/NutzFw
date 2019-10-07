/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.dto;

import java.nio.file.Path;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/3/23
 * 描述此类：
 */
public class FileZipDTO {
    private String fileName;
    private Path   path;

    public FileZipDTO(String fileName, Path path) {
        this.fileName = fileName;
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "FileZipDTO{" +
                "fileName='" + fileName + '\'' +
                ", path=" + path +
                '}';
    }
}
