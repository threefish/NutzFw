package com.nutzfw.modules.sys.dto;

import java.nio.file.Path;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/3/23
 * 描述此类：
 */
public class FileZipDTO {
    private String fileName;
    private Path path;

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
