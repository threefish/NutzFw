/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.util.excel.dto;

public class PoiInsertCellDataDTO {
    private int    row;
    private int    col;
    private String sheetName = "";
    private String val       = "";

    public PoiInsertCellDataDTO(int row, int col, String sheetName) {
        this.row = row;
        this.col = col;
        this.sheetName = sheetName;
    }

    public PoiInsertCellDataDTO(int row, int col, String sheetName, String val) {
        this.row = row;
        this.col = col;
        this.sheetName = sheetName;
        this.val = val;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "PoiInsertCellDataDTO{" +
                "row=" + row +
                ", col=" + col +
                ", sheetName='" + sheetName + '\'' +
                ", val='" + val + '\'' +
                '}';
    }
}
