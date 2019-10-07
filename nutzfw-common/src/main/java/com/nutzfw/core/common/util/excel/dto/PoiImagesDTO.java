/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.util.excel.dto;

import java.io.File;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/3/23
 * 描述此类：
 */
public class PoiImagesDTO {

    private int   dx1;
    private int   dy1;
    private int   dx2;
    private int   dy2;
    private short col1;
    private int   row1;
    private short col2;
    private int   row2;

    private File imgFile;

    public int getDx1() {
        return dx1;
    }

    public void setDx1(int dx1) {
        this.dx1 = dx1;
    }

    public int getDy1() {
        return dy1;
    }

    public void setDy1(int dy1) {
        this.dy1 = dy1;
    }

    public int getDx2() {
        return dx2;
    }

    public void setDx2(int dx2) {
        this.dx2 = dx2;
    }

    public int getDy2() {
        return dy2;
    }

    public void setDy2(int dy2) {
        this.dy2 = dy2;
    }

    public short getCol1() {
        return col1;
    }

    public void setCol1(short col1) {
        this.col1 = col1;
    }

    public int getRow1() {
        return row1;
    }

    public void setRow1(int row1) {
        this.row1 = row1;
    }

    public short getCol2() {
        return col2;
    }

    public void setCol2(short col2) {
        this.col2 = col2;
    }

    public int getRow2() {
        return row2;
    }

    public void setRow2(int row2) {
        this.row2 = row2;
    }

    public File getImgFile() {
        return imgFile;
    }

    public void setImgFile(File imgFile) {
        this.imgFile = imgFile;
    }

    @Override
    public String toString() {
        return "PoiImagesDTO{" +
                "dx1=" + dx1 +
                ", dy1=" + dy1 +
                ", dx2=" + dx2 +
                ", dy2=" + dy2 +
                ", col1=" + col1 +
                ", row1=" + row1 +
                ", col2=" + col2 +
                ", row2=" + row2 +
                ", imgFile=" + imgFile +
                '}';
    }
}
