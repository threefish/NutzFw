/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.util.excel;

import com.nutzfw.core.common.util.DateUtil;
import com.nutzfw.core.common.util.excel.dto.PoiDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.nutz.el.El;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.*;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/1/10
 * 报表使用el表达式引擎重新写值
 * 读取整个Excle报表执行宏替换
 * 只支持2007及以上版本，不再支持2003
 */
public class ExcelTemplate {

    static final         Log    log               = Logs.get();
    static final         String NUTZ_TEMP_ROW_KEY = "nutz_temp_row_key";
    private static final String LIST              = "list";
    private static final String DATA              = "data";
    private static final String IMG               = "img";
    /**
     * 输出文件
     */
    File outFile;
    private ByteArrayInputStream inputStream;
    private Workbook             wb;

    public ExcelTemplate(File template, File outFile) throws IOException {
        if (!outFile.exists() || !template.exists()) {
            throw new RuntimeException("模版文件和输出文件不存在");
        }
        this.inputStream = new ByteArrayInputStream(Streams.readBytesAndClose(new FileInputStream(template)));
        this.wb = new XSSFWorkbook(inputStream);
        this.outFile = outFile;
    }

    public Workbook getWb() {
        return wb;
    }

    /**
     * 保存文件
     */
    public File save() {
        try (FileOutputStream outputStream = new FileOutputStream(outFile)) {
            this.wb.setForceFormulaRecalculation(true);
            this.wb.write(outputStream);
            outputStream.flush();
            log.debug("修改保存Excle文件:" + outFile.getAbsolutePath());
        } catch (Exception e) {
            log.error(e);
        } finally {
            Streams.safeClose(this.inputStream);
        }
        return outFile;
    }

    /**
     * 报表使用el表达式引擎重新写值
     * 读取整个Excle报表执行宏替换
     * 只支持2007及以上版本，不再支持2003
     *
     * @param sheetName
     * @param context
     * @return
     */
    public ExcelTemplate renderSheetMacro(String sheetName, Context context) {
        Sheet sheet = ExcelUtils.getSheetByName(wb, sheetName);
        int firstRowNum = sheet.getFirstRowNum();
        //sheet.getLastRowNum();必须实时取
        for (int i = firstRowNum; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                renderSheetOrRow(sheet, sheet.getRow(i), context);
            }
        }
        return this;
    }

    /**
     * 报表使用el表达式引擎重新写值
     *
     * @param sheet
     * @param row
     * @param context
     */
    private void renderSheetOrRow(Sheet sheet, Row row, Context context) {
        int cellSize = row.getLastCellNum();
        int firstCellNum = row.getFirstCellNum();
        for (int i = firstCellNum; i < cellSize; i++) {
            Cell cell = row.getCell(i);
            if (cell != null) {
                boolean b = true;
                //#{测试|{type:list,X:5,Y:'测试,测试2'}}
                if (cell.getCellType() == CellType.STRING) {
                    //判断是否是特殊类型
                    String val = cell.getStringCellValue();
                    if (ExcelUtils.isEspressione(val)) {
                        b = false;
                        if (val.indexOf("|") > -1) {
                            String[] ss = val.split("\\|");
                            String val1 = ss[0];
                            String val2 = ss[1];
                            String key = val1.substring(2);
                            String json = val2.substring(0, val2.length() - 1);
                            PoiDto poiDto = Json.fromJson(PoiDto.class, json);
                            renderEspressione(sheet, context, cell, key, poiDto);
                        }
                    }
                }
                if (b) {
                    ExcelUtils.renderCell(row.getCell(i), context);
                }
            }
        }
    }

    /**
     * 报表使用自定义表达式引擎重新写值
     * 支持合并单元格，暂时不支持合并行
     *
     * @param sheet
     * @param ctx
     * @param cell
     * @param key
     * @param dto
     */
    private void renderEspressione(Sheet sheet, Context ctx, Cell cell, String key, PoiDto dto) {
        int rowMax = dto.getX();
        String[] y = dto.getY();
        int col = cell.getColumnIndex();
        int rol = cell.getRowIndex();
        if (LIST.equals(dto.getType())) {
            //是报表查询返回得数据
            if (ctx.get(key) instanceof List) {
                List<Object> datalist = ctx.getList(key);
                if (datalist != null && datalist.size() != 0) {
                    rowMax = rowMax > 0 ? (datalist.size() > rowMax ? rowMax : datalist.size()) : datalist.size();
                    insertRow(dto, rowMax, sheet, rol);
                    renderRowData(datalist, rowMax, y, dto.getDateFormat(), rol, col, sheet);
                } else {
                    cell.setCellType(CellType.STRING);
                    cell.setCellValue("");
                }
            }
        } else if (DATA.equals(dto.getType())) {
            cell.setCellType(CellType.STRING);
            List<Object> datalist = ctx.getList(key);
            if (datalist != null && datalist.size() != 0) {
                renderRowData(datalist, 1, y, dto.getDateFormat(), rol, col, sheet);
            } else {
                cell.setCellType(CellType.STRING);
                cell.setCellValue("");
            }
        } else if (IMG.equals(dto.getType())) {
            File img = ctx.getAs(File.class, key);
            if (img != null && dto.getImg() != null) {
                dto.getImg().setImgFile(img);
                cell.setCellType(CellType.STRING);
                cell.setCellValue("");
                ExcelUtils.insertImgage(sheet, dto.getImg());
            }
        }
        ExcelUtils.renderDimension(this.wb);
    }

    /**
     * 插入行
     *
     * @param dto
     * @param rowMax
     * @param sheet
     * @param startRow
     */
    private void insertRow(PoiDto dto, int rowMax, Sheet sheet, int startRow) {
        if (dto.isAutoInsertStyleRow()) {
            //自动插入行
            if (rowMax > 1) {
                if (dto.isMerged()) {
                    int rows = rowMax - 1;
                    ExcelUtils.insertRow(sheet, startRow, rowMax - 1);
                    ExcelUtils.formatRows(sheet, startRow + rows, startRow, rows, true);
                }
            }
        }
    }

    /**
     * 写入行数据
     *
     * @param datalist
     * @param rowMax
     * @param cols
     * @param dateFormat
     * @param rol
     * @param col
     * @param sheet
     */
    private void renderRowData(List<Object> datalist, int rowMax, String[] cols, String dateFormat, int rol, int col, Sheet sheet) {
        for (int i = 0; i < rowMax; i++) {
            Object data = datalist.get(i);
            if (data != null) {
                Context ctx = Lang.context().set(NUTZ_TEMP_ROW_KEY, data);
                Row row = CellUtil.getRow(rol + i, sheet);
                int rangeCol = 0;
                for (int j = 0; j < cols.length; j++) {
                    Cell cell = CellUtil.getCell(row, col + rangeCol + j);
                    CellRangeAddress cellRangeAddress = ExcelUtils.getCellRangeAddress(cell);
                    //是合并单元格
                    if (cellRangeAddress != null) {
                        rangeCol = rangeCol + (cellRangeAddress.getLastColumn() - cellRangeAddress.getFirstColumn());
                    }
                    cell.setCellType(CellType.STRING);
                    String key = NUTZ_TEMP_ROW_KEY.concat(".").concat(cols[j]);
                    try {
                        Object objVal = new El(key).eval(ctx);
                        if (objVal instanceof Integer || objVal instanceof Double || objVal instanceof Float) {
                            cell.setCellType(CellType.NUMERIC);
                            if (objVal instanceof Integer) {
                                cell.setCellValue(((Integer) objVal));
                            }
                            if (objVal instanceof Double) {
                                cell.setCellValue(((Double) objVal));
                            }
                            if (objVal instanceof Float) {
                                cell.setCellValue(((Float) objVal));
                            }
                        } else {
                            cell.setCellValue(Strings.sNull(DateUtil.renderStrDate(objVal, dateFormat)));
                        }
                    } catch (Exception e) {
                        cell.setCellValue("函数执行异常：" + e.getMessage());
                        log.error(e);
                    }
                }
            }
        }
    }

    public ExcelTemplate renderAllSheetMacro(Context context) {
        List<String> sheetNames = ExcelUtils.getExcleSheetNames(getWb());
        for (String sheetName : sheetNames) {
            renderSheetMacro(sheetName, context);
        }
        return this;
    }
}
