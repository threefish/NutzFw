/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.util.excel;

import com.nutzfw.core.common.util.DateUtil;
import com.nutzfw.core.common.util.RegexUtil;
import com.nutzfw.core.common.util.excel.dto.*;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.*;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by IntelliJ IDEA.
 * User: huchuc@vip.qq.com
 * Date: 2016/9/30 0030
 * To change this template use File | Settings | File Templates.
 * <p>
 * 请记得调用saveWirte()方法
 */
public class PoiExcelUtil {

    public static  int          rowAccessWindowSize = 1000;
    private static String       LIST                = "list";
    private static String       DATA                = "data";
    /**
     * 默认版本
     */
    public         int          version             = 0;
    public         String       suffix              = ".xlsx";
    private        Log          log                 = Logs.get();
    private        Workbook     wb;
    private        OutputStream out;
    private        String       filePath;

    /**
     * 支持百万级数据导出
     *
     * @param sxssfWorkbook
     * @param file
     */
    public PoiExcelUtil(SXSSFWorkbook sxssfWorkbook, File file) throws IOException {
        this.wb = sxssfWorkbook;
        this.version = 2010;
        this.suffix = ".xlsx";
        this.out = new FileOutputStream(file);
    }

    /**
     * 纯新建
     */
    private PoiExcelUtil() {
        this.wb = new XSSFWorkbook();
        this.version = 2010;
        this.suffix = ".xlsx";
    }

    /**
     * @param file 输入excle文件
     */
    public PoiExcelUtil(File file) {
        ByteArrayOutputStream outStream = readExcelFile(file);
        this.filePath = file.getAbsolutePath();
        try {
            //2007
            this.wb = new XSSFWorkbook(new ByteArrayInputStream(outStream.toByteArray()));
            this.version = 2010;
            this.suffix = ".xlsx";
        } catch (Exception e) {
            try {
                //2003
                this.wb = new HSSFWorkbook(new ByteArrayInputStream(outStream.toByteArray()));
                this.version = 2003;
                this.suffix = ".xls";
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    /**
     * @param inputStream 输入excle文件流
     */
    public PoiExcelUtil(InputStream inputStream) {
        ByteArrayOutputStream outStream = readExcelFile(inputStream);
        try {
            this.wb = new XSSFWorkbook(new ByteArrayInputStream(outStream.toByteArray()));
            this.version = 2010;
            this.suffix = ".xlsx";
        } catch (Exception e) {
            try {
                this.wb = new HSSFWorkbook(new ByteArrayInputStream(outStream.toByteArray()));
                this.version = 2003;
                this.suffix = ".xls";
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * @param excel 输入excel文件二进制
     * @param out   文件输出流
     */
    public PoiExcelUtil(byte[] excel, OutputStream out) {
        this.out = out;
        try {
            this.wb = new XSSFWorkbook(new ByteArrayInputStream(excel));
            this.version = 2010;
            this.suffix = ".xlsx";
        } catch (Exception e) {
            try {
                this.wb = new HSSFWorkbook(new ByteArrayInputStream(excel));
                this.version = 2003;
                this.suffix = ".xls";
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * @param file 输入excle文件
     * @param out  文件输出流
     */
    public PoiExcelUtil(File file, OutputStream out) {
        this.out = out;
        ByteArrayOutputStream outStream = readExcelFile(file);
        try {
            this.wb = new XSSFWorkbook(new ByteArrayInputStream(outStream.toByteArray()));
            this.version = 2010;
            this.suffix = ".xlsx";
        } catch (Exception e) {
            try {
                this.wb = new HSSFWorkbook(new ByteArrayInputStream(outStream.toByteArray()));
                this.version = 2003;
                this.suffix = ".xls";
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * @param file 输入excle文件
     * @param out  文件输出
     */
    public PoiExcelUtil(File file, File out) {
        this.filePath = out.getAbsolutePath();
        ByteArrayOutputStream outStream = readExcelFile(file);
        try {
            this.wb = new XSSFWorkbook(new ByteArrayInputStream(outStream.toByteArray()));
            this.version = 2010;
            this.suffix = ".xlsx";
        } catch (Exception e) {
            try {
                this.wb = new HSSFWorkbook(new ByteArrayInputStream(outStream.toByteArray()));
                this.version = 2003;
                this.suffix = ".xls";
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 纯新建
     */
    public static PoiExcelUtil createNewExcel() {
        return new PoiExcelUtil();
    }

    /**
     * 复制单元格
     *
     * @param srcCell
     * @param distCell
     * @param copyValueFlag true则连同cell的内容一起复制
     */
    public static void copyCell(Cell srcCell, Cell distCell, boolean copyValueFlag) {
        ExcelUtils.copyCell(srcCell, distCell, copyValueFlag);
    }

    /**
     * 复制一个单元格样式到目的单元格样式
     *
     * @param fromStyle
     * @param toStyle
     */
    public static void copyCellStyle(CellStyle fromStyle, CellStyle toStyle) {
        toStyle.setAlignment(fromStyle.getAlignment());
        //边框和边框颜色
        toStyle.setBorderBottom(fromStyle.getBorderBottom());
        toStyle.setBorderLeft(fromStyle.getBorderLeft());
        toStyle.setBorderRight(fromStyle.getBorderRight());
        toStyle.setBorderTop(fromStyle.getBorderTop());
        toStyle.setTopBorderColor(fromStyle.getTopBorderColor());
        toStyle.setBottomBorderColor(fromStyle.getBottomBorderColor());
        toStyle.setRightBorderColor(fromStyle.getRightBorderColor());
        toStyle.setLeftBorderColor(fromStyle.getLeftBorderColor());

        //背景和前景
        toStyle.setFillBackgroundColor(fromStyle.getFillBackgroundColor());
        toStyle.setFillForegroundColor(fromStyle.getFillForegroundColor());

        toStyle.setDataFormat(fromStyle.getDataFormat());
        toStyle.setFillPattern(fromStyle.getFillPattern());
        toStyle.setHidden(fromStyle.getHidden());
        //首行缩进
        toStyle.setIndention(fromStyle.getIndention());
        toStyle.setLocked(fromStyle.getLocked());
        //旋转
        toStyle.setRotation(fromStyle.getRotation());
        toStyle.setVerticalAlignment(fromStyle.getVerticalAlignment());
        toStyle.setWrapText(fromStyle.getWrapText());

    }

    /**
     * 判断是否是日期或时间单元格
     *
     * @param cell
     * @return
     */
    public static boolean isDateCell(Cell cell) {
        return ExcelUtils.isCellDateFormatted(cell);
    }

    /**
     * 获取单元格数据内容为字符串类型的数据
     *
     * @param cell Excel单元格
     * @return String 单元格数据内容
     */
    private static String getStringCellValue(Cell cell, Parame... parame) {
        String val = "";
        if (cell == null) {
            return val;
        }
        if (parame.length > 0) {
            Parame par = parame[0];
            val = ExcelUtils.getStringCellValue(cell, new ExcleGetParamesDTO(par.getConverDateToStr(), par.getDateFormat()));
        } else {
            val = ExcelUtils.getStringCellValue(cell, ExcleGetParamesDTO.defaualt());
        }
        return val;
    }

    /**
     * 取得cell如果不存在则创建cell
     *
     * @param row
     * @param colIndex
     * @return
     */
    public static Cell getCellOrCreateCell(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }
        return cell;
    }

    /**
     * 取得Row如果不存在则创建Row
     *
     * @param sheet
     * @param rowIndex
     * @return
     */
    public static Row getRowOrCreateRow(Sheet sheet, int rowIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        return row;
    }

    /**
     * @param wb            HSSFWorkbook对象
     * @param realSheet     需要操作的sheet对象
     * @param datas         下拉的列表数据
     * @param startCol      开始列
     * @param endCol        结束列
     * @param errorCanInput 验证错误也可以输入
     * @return
     * @throws Exception
     */
    public static HSSFWorkbook dropDownList(Workbook wb, Sheet realSheet, String[] datas, int startCol, int endCol, boolean errorCanInput) {
        return ExcelUtils.dropDownList(wb, realSheet, datas, startCol, endCol, errorCanInput);
    }

    /**
     * @param wb            XSSFWorkbook
     * @param realSheet     需要操作的sheet对象
     * @param datas         下拉的列表数据
     * @param startCol      开始列
     * @param endCol        结束列
     * @param errorCanInput 验证错误也可以输入
     * @return
     * @throws Exception
     */
    public static XSSFWorkbook dropDownList2007(Workbook wb, Sheet realSheet, String[] datas, int startCol, int endCol, boolean errorCanInput) {
        return ExcelUtils.dropDownList2007(wb, realSheet, datas, startCol, endCol, errorCanInput);
    }

    private static void setExcelColumnName(StringBuilder str, int col) {
        final int max = 26;
        int tmp = col / max;
        if (tmp > max) {
            setExcelColumnName(str, tmp - 1);
        } else if (tmp > 0) {
            str.append((char) (tmp + 64));
        }
        str.append((char) (col % max + 65));
    }

    /**
     * 通过索引取得列名
     *
     * @param col
     * @return
     */
    public static String getExcelColumnName(int col) {
        StringBuilder str = new StringBuilder(2);
        setExcelColumnName(str, col);
        return str.toString();
    }

    public Workbook getWb() {
        return wb;
    }

    /**
     * 只有传了OutputStream才会有
     *
     * @return
     */
    public OutputStream getOut() {
        return this.out;
    }

    /**
     * 创建sheet
     *
     * @param sheetName
     * @return
     */
    public Sheet createSheet(String sheetName) {
        Sheet sheet;
        if (sheetName == null) {
            sheet = this.wb.createSheet();
        } else {
            sheet = this.wb.createSheet(sheetName);
        }
        return sheet;
    }

    /**
     * 获取工作簿
     *
     * @param name
     * @return
     */
    public Sheet getSheetByName(String name) {
        Sheet sheet = wb.getSheet(name);
        if (sheet == null) {
            throw new RuntimeException(name + "工作薄不存在");
        }
        return sheet;
    }

    /**
     * 获取工作簿
     *
     * @param index
     * @return
     */
    public Sheet getSheetByIndex(int index) {
        Sheet sheet = wb.getSheetAt(index);
        if (sheet == null) {
            throw new RuntimeException("第" + index + "工作薄不存在");
        }
        return sheet;
    }

    /**
     * 读取Excle
     *
     * @param file
     * @return
     */
    private ByteArrayOutputStream readExcelFile(File file) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            outStream = readExcelFile(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return outStream;
    }

    /**
     * 读取Excle
     *
     * @param in
     * @return
     */
    private ByteArrayOutputStream readExcelFile(InputStream in) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            byte[] data = new byte[1024];
            int count = -1;
            while ((count = in.read(data, 0, 1024)) != -1) {
                outStream.write(data, 0, count);
            }
            data = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in == null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return outStream;
    }

    /**
     * 获取所有的工作簿名称
     *
     * @return
     */
    public List<String> getExcleSheetNames() {
        List<String> sheetNames = new ArrayList<>();
        int total = wb.getNumberOfSheets();
        for (int i = 0; i < total; i++) {
            Sheet sheet = wb.getSheetAt(i);
            sheetNames.add(sheet.getSheetName());
        }
        return sheetNames;
    }

    /**
     * 插入行拷贝样式
     *
     * @param sheet
     * @param startRow 开始行
     * @param rows     插入行数
     */
    public void insertRow(Sheet sheet, int startRow, int rows) {
        ExcelUtils.insertRow(sheet, startRow, rows);
        ExcelUtils.formatRows(sheet, startRow + rows, startRow, rows, true);
    }

    /**
     * 插入行拷贝样式和合并行
     *
     * @param sheet
     * @param startRow 开始行
     * @param rows     插入行数
     * @return int    返回截至行
     */
    public int insertRowAndMerged(Sheet sheet, int startRow, int rows) {
        int startRowClon = startRow;
        Row lineOnerow = getRowOrCreateRow(sheet, startRow);
        int lineLastCellNum = lineOnerow.getLastCellNum();
        Cell[] cells = new Cell[lineLastCellNum];
        CellRangeAddress[] rangeAddresses = new CellRangeAddress[lineLastCellNum];
        int maxLineCellRange = 1;//当前行实际是几行
        for (int i = 0; i < lineLastCellNum; i++) {
            Cell cell = getCellOrCreateCell(lineOnerow, i);
            if (cell.getCellType() == CellType.STRING) {
                String val = cell.getStringCellValue();
                if (RegexUtil.isEspressione(Strings.sNull(val))) {
                    cell.setCellValue("");
                }
            }
            cells[i] = cell;
            CellRangeAddress cellRangeAddress = getCellRangeAddress(cell);
            rangeAddresses[i] = cellRangeAddress;
            if (cellRangeAddress != null) {
                int x = cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow();
                if (x > 0) {
                    x = x + 1;
                }
                maxLineCellRange = x > maxLineCellRange ? x : maxLineCellRange;
            }
        }
        List<Cell[]> cellsList = new ArrayList<>();
        if (maxLineCellRange > 1) {
            for (int x = 0; x < maxLineCellRange; x++) {
                Cell[] cs = new Cell[lineLastCellNum];
                Row row = getRowOrCreateRow(sheet, startRow + x);
                for (int i = 0; i < lineLastCellNum; i++) {
                    Cell cell = getCellOrCreateCell(row, i);
                    cs[i] = cell;
                }
                cellsList.add(cs);
            }
        }
        int maxLine = rows * maxLineCellRange;
        sheet.shiftRows(startRow, sheet.getLastRowNum(), maxLine, true, false);
        int d = 0;
        for (int i = 0; (version == 2003 ? i <= maxLine : i < maxLine); i++) {
            if (d >= maxLineCellRange) {
                d = 0;
            }
            Row newRow = getRowOrCreateRow(sheet, startRow);//原始位置创建新的行
            newRow.setHeight(lineOnerow.getHeight());
            if (lineOnerow.isFormatted()) {
                //支持行格式化
                newRow.setRowStyle(lineOnerow.getRowStyle());
            }
            int start = lineOnerow.getFirstCellNum() < 0 ? 0 : lineOnerow.getFirstCellNum();
            for (; start < lineLastCellNum; start++) {
                Cell newCell = newRow.createCell(start);
                Cell oldCell = cells[start];
                if (maxLineCellRange > 1) {
                    copyCell(cellsList.get(d)[start], newCell, true);
                } else {
                    copyCell(oldCell, newCell, true);
                }
                CellRangeAddress oldcellrange = rangeAddresses[start];
                //是合并单元格
                if (oldcellrange != null) {
                    int firstRow = oldcellrange.getFirstRow();
                    int lastRow = oldcellrange.getLastRow();
                    int firstCol = oldcellrange.getFirstColumn();
                    int lastCol = oldcellrange.getLastColumn();
                    try {//偷个懒，懒的去计算了，直接添加合并信息，加不起表示已经处于合并区域了就不管了
                        if (version == 2003 && (firstRow <= (maxLine + startRow))) {
                            firstRow = firstRow - (i * maxLineCellRange);
                            lastRow = lastRow - (i * maxLineCellRange);
                            CellRangeAddress rangeAddress = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
                            sheet.addMergedRegion(rangeAddress);
                        } else if (version == 2010 && (firstRow <= (maxLine + startRow))) {
                            firstRow = firstRow + (i * maxLineCellRange);
                            lastRow = lastRow + (i * maxLineCellRange);
                            CellRangeAddress rangeAddress = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
                            sheet.addMergedRegion(rangeAddress);
                        } else {
                            throw new RuntimeException("未知版本");
                        }
                    } catch (Exception e) {
                    }
                }
            }
            startRow++;
            d++;
        }
        return maxLine + maxLineCellRange + startRowClon;
    }

    /**
     * 报表写值
     *
     * @return
     */
    public void wirteWorkBook(List<PoiInsertCellDataDTO> cellsValList) throws IOException {
        for (PoiInsertCellDataDTO c : cellsValList) {
            Sheet sheet = wb.getSheet(c.getSheetName());
            Row row = getRowOrCreateRow(sheet, c.getRow());
            Cell cell = getCellOrCreateCell(row, c.getCol());
            cell.setCellValue(c.getVal());
        }
    }

    /**
     * 报表写值
     * <p>
     * 需要重新计算值坐标（针对合并列和合并行）
     * </p>
     *
     * @return
     */
    public void wirteMergeWorkBook(List<PoiInsertCellDataDTO> cellsValList) throws IOException {
        for (PoiInsertCellDataDTO c : cellsValList) {
            Sheet sheet = wb.getSheet(c.getSheetName());
            int relRowIndex = getRelRowIndex(sheet, c.getRow());
            Row relrow = getRowOrCreateRow(sheet, relRowIndex);
            int relColIndex = getRelColIndex(relrow, c.getCol());
            Cell cell = getCellOrCreateCell(relrow, relColIndex);
            cell.setCellValue(c.getVal());
        }
    }

    /**
     * 报表写值
     * <p>
     * 需要重新计算值坐标（针对合并列和合并行） 指定区域
     * </p>
     *
     * @param cellsValList
     * @param startRow
     * @param endRow
     * @param startCol
     * @param endCol
     * @throws IOException
     */
    @Deprecated
    public void wirteMergeWorkBookByArea(List<PoiInsertCellDataDTO> cellsValList, int startRow, int endRow, int startCol, int endCol) throws IOException {
        for (PoiInsertCellDataDTO c : cellsValList) {
            Sheet sheet = wb.getSheet(c.getSheetName());
            Row relrow = getRowOrCreateRow(sheet, c.getRow());
            if (c.getRow() >= startRow && c.getRow() <= endRow) {
                int relRowIndex = getRelRowIndex(sheet, c.getRow());
                relrow = getRowOrCreateRow(sheet, relRowIndex);
            }
            Cell cell = getCellOrCreateCell(relrow, c.getCol());
            if (c.getCol() >= startCol && c.getCol() <= endCol) {
                int relColIndex = getRelColIndex(relrow, c.getCol());
                cell = getCellOrCreateCell(relrow, relColIndex);
            }
            cell.setCellValue(c.getVal());
        }
    }

    /**
     * 取得真实的列
     *
     * @param row
     * @param colIndex 逻辑行
     */
    public int getRelColIndex(Row row, int colIndex) {
        int i = row.getFirstCellNum() < 0 ? 0 : row.getFirstCellNum();
        int end = row.getPhysicalNumberOfCells() < 0 ? 0 : row.getPhysicalNumberOfCells();
        int mergeRowIndex = 0;
        int cellNow = 0;
        sw:
        for (; i < end; i++) {
            if (cellNow == colIndex) {
                break sw;
            }
            Cell cell = getCellOrCreateCell(row, i);
            CellRangeAddress cellRangeAddress = getCellRangeAddress(cell);
            if (cellRangeAddress != null) {//有合并情况
                int mergeRowTemp = cellRangeAddress.getLastColumn() - cellRangeAddress.getFirstColumn();
                if (mergeRowTemp > 0) {//有左右合并情况
                    i = i + mergeRowTemp;//下一次跳过被合并的单元格
                    if (mergeRowIndex == 0) {
                        mergeRowIndex = mergeRowTemp;
                    } else {
                        mergeRowIndex = mergeRowIndex + mergeRowTemp;
                    }
                }
            }
            cellNow++;
        }
        return colIndex + mergeRowIndex;
    }

    /**
     * 取得真实的行
     *
     * @param sheet
     * @param rowIndex 逻辑行
     */
    public int getRelRowIndex(Sheet sheet, int rowIndex) {
        int i = sheet.getFirstRowNum() < 0 ? 0 : sheet.getFirstRowNum();
        int total = 0;
        for (; i < rowIndex; i++) {
            Row row = getRowOrCreateRow(sheet, i);
            int start = row.getFirstCellNum();
            int end = row.getPhysicalNumberOfCells();
            if (start < 0) {
                start = 0;
            }
            if (end < 0) {
                start = 0;
            }
            int mergeRowIndex = 0;
            for (; start < end; start++) {
                Cell cell = getCellOrCreateCell(row, start);
                CellRangeAddress cellRangeAddress = getCellRangeAddress(cell);
                if (cellRangeAddress != null) {
                    //有合并情况
                    int mergeRowTemp = cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow();
                    if (mergeRowTemp > 0) {
                        if (mergeRowIndex == 0) {
                            mergeRowIndex = mergeRowTemp;
                        } else if (mergeRowTemp > mergeRowIndex) {
                            mergeRowIndex = mergeRowTemp;
                        }
                    }
                }
            }
            total = total + mergeRowIndex;
        }
        return total + rowIndex;
    }

    /**
     * 报表使用el表达式引擎重新写值
     * 读取整个Excle报表执行宏替换
     *
     * @return
     */
    public void renderAllSheetMacro(Context ctx) throws IOException {
        List<String> sheetNames = getExcleSheetNames();
        for (String sheetName : sheetNames) {
            renderAllSheetMacro(ctx, sheetName);
        }
    }

    /**
     * 报表使用el表达式引擎重新写值
     * 读取整个Excle报表执行宏替换
     *
     * @return
     */
    public void renderAllSheetMacro(Context ctx, String sheetName) throws IOException {
        renderAllSheetMacro(ctx, wb.getSheet(sheetName));
    }

    /**
     * 报表使用el表达式引擎重新写值
     * 读取整个Excle报表执行宏替换
     *
     * @return
     */
    public void renderAllSheetMacro(Context ctx, Sheet sheet) throws IOException {
        int firstRowNum = sheet.getFirstRowNum();
        //sheet.getLastRowNum();必须实时取
        for (int i = firstRowNum; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                renderSheetOrRow(sheet, sheet.getRow(i), ctx);
            }
        }
    }

    /**
     * 报表使用el表达式引擎重新写值
     *
     * @return
     */
    private void renderSheetOrRow(Sheet sheet, Row row, Context ctx) throws IOException {
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
                    if (RegexUtil.isEspressione(val)) {
                        b = false;
                        if (val.indexOf("|") > -1) {
                            int rowIndex = cell.getRowIndex();
                            int columnIndex = cell.getColumnIndex();
                            String[] ss = val.split("\\|");
                            String key = ss[0].substring(2, ss[0].length());
                            String json = ss[1].substring(0, ss[1].length() - 1);
                            PoiDto poiDto = Json.fromJson(PoiDto.class, json);
                            renderEspressione(sheet, ctx, cell, key, poiDto);
                            int rowEnd = rowIndex;
                            int colEnd = columnIndex + poiDto.getY().length;
                            if (poiDto.getX() <= 0) {
                                if (ctx.get(key) instanceof ReportDataDTO) {
                                    ReportDataDTO dataDTO = (ReportDataDTO) ctx.get(key);
                                    rowEnd += dataDTO.getDataCount();
                                } else if (ctx.get(key) instanceof List) {
                                    rowEnd += ((List) ctx.get(key)).size();
                                }
                            } else {
                                rowEnd += poiDto.getX();
                            }
                            List<MergedModel> list = new ArrayList<>();
                            if (poiDto.isVerticalLikeMerged()) {
                                list.addAll(verticalLikeMerged(sheet, rowIndex, rowEnd, columnIndex, colEnd));
                            }
                            if (poiDto.isHorizontalLikeMerged()) {
                                list.addAll(horizontalLikeMerged(sheet, rowIndex, rowEnd, columnIndex, colEnd));
                            }
                            addCellRangeAddress(sheet, list);
                        }
                    }
                }
                if (b) {
                    renderCell(row.getCell(i), ctx);
                }
            }
        }
    }

    /**
     * 合并横向相同的单元格
     *
     * @param sheet
     * @param rowIndex
     * @param rowEnd
     * @param columnIndex
     * @param colEnd
     */
    private List<MergedModel> horizontalLikeMerged(Sheet sheet, int rowIndex, int rowEnd, int columnIndex, int colEnd) {
        List<MergedModel> list = new ArrayList<>();
        for (int rowNum = rowIndex; rowNum < rowEnd; rowNum++) {
            Row row = getRowOrCreateRow(sheet, rowNum);
            int num = columnIndex;
            do {
                Cell cell = getCellOrCreateCell(row, num);
                int tempNum = num;
                MergedModel model = new MergedModel();
                model.setContent(cell.getStringCellValue());
                model.setFirstCol(num);
                num = horizontallLike(sheet, model, num, rowNum, colEnd);
                model.setLastCol(num);
                model.setFirstRow(rowNum);
                model.setLastRow(rowNum);
                if (num == tempNum) {
                    num++;
                } else {
                    list.add(model);
                }
            } while (num < colEnd);
        }
        return list;
    }

    /**
     * 合并纵向相同的单元格
     *
     * @param sheet
     * @param rowIndex
     * @param rowEnd
     * @param columnIndex
     * @param colEnd
     */
    public List<MergedModel> verticalLikeMerged(Sheet sheet, int rowIndex, int rowEnd, int columnIndex, int colEnd) {
        List<MergedModel> list = new ArrayList<>();
        for (int colNum = columnIndex; colNum < colEnd; colNum++) {
            int num = rowIndex;
            do {
                MergedModel model = new MergedModel();
                Row row = getRowOrCreateRow(sheet, num);
                Cell cell = getCellOrCreateCell(row, colNum);
                model.setContent(cell.getStringCellValue());
                model.setFirstRow(num);
                int tempNum = num;
                num = verticalLike(sheet, model, colNum, num, rowEnd);
                model.setLastRow(num);
                model.setFirstCol(colNum);
                model.setLastCol(colNum);
                if (num == tempNum) {
                    num++;
                } else {
                    list.add(model);
                }
            } while (num < rowEnd);
        }
        return list;
    }

    private int verticalLike(Sheet sheet, MergedModel model, int columnIndex, int rowIndex, int rowEnd) {
        int num = rowIndex;
        br:
        for (int i = rowIndex; i < rowEnd; i++) {
            Row tempRow = sheet.getRow(i);
            if (tempRow != null) {
                Cell cell = tempRow.getCell(columnIndex);
                if (cell != null && Objects.equals(model.getContent(), cell.getStringCellValue())) {
                    num = i;
                } else {
                    break br;
                }
            } else {
                break br;
            }
        }
        return num;
    }

    private int horizontallLike(Sheet sheet, MergedModel model, int columnIndex, int rowIndex, int colEnd) {
        int num = columnIndex;
        br:
        for (int i = columnIndex; i < colEnd; i++) {
            Row tempRow = sheet.getRow(rowIndex);
            if (tempRow != null) {
                Cell cell = tempRow.getCell(i);
                if (cell != null && Objects.equals(model.getContent(), cell.getStringCellValue())) {
                    num = i;
                } else {
                    break br;
                }
            } else {
                break br;
            }
        }
        return num;
    }

    public void addCellRangeAddress(Sheet sheet, List<MergedModel> list) {
        for (MergedModel mergedModel : list) {
            try {
                sheet.addMergedRegion(new CellRangeAddress(mergedModel.getFirstRow(), mergedModel.getLastRow(), mergedModel.getFirstCol(), mergedModel.getLastCol()));
            } catch (Exception e) {
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
        if (LIST.equals(dto.getType())) {
            int rowMax = dto.getX();
            String[] y = dto.getY();
            int col = cell.getColumnIndex();
            int rol = cell.getRowIndex();
            //是报表查询返回得数据
            if (ctx.get(key) instanceof ReportDataDTO) {
                ReportDataDTO dataDTO = (ReportDataDTO) ctx.get(key);
                switch (dataDTO.getDataType()) {
                    case 0:
                        //单行
                        renderRowData(Arrays.asList(dataDTO.getAloneData()), 1, y, dto.getDateFormat(), rol, col, sheet);
                        break;
                    case 1:
                        //大数据
                        insertRow(dto, dataDTO.getDataCount(), sheet, rol);
                        int tempRow = rol;
                        for (Path path : dataDTO.getBigListData()) {
                            List<NutMap> nutMaps = ReportUtil.readObjectFromFile(path);
                            renderRowData(nutMaps, nutMaps.size(), y, dto.getDateFormat(), tempRow, col, sheet);
                            tempRow = tempRow + nutMaps.size();
                        }
                        break;
                    default:
                        break;
                }

            } else if (ctx.get(key) instanceof List) {
                List<NutMap> datalist = ctx.getList(NutMap.class, key);
                if (datalist != null && datalist.size() != 0) {
                    if (rowMax > 0) {
                        rowMax = datalist.size() > rowMax ? rowMax : datalist.size();
                    } else {
                        rowMax = datalist.size();
                    }
                    insertRow(dto, rowMax, sheet, rol);
                    renderRowData(datalist, rowMax, y, dto.getDateFormat(), rol, col, sheet);
                } else {
                    cell.setCellType(CellType.STRING);
                    cell.setCellValue("");
                }
            }
        } else if (DATA.equals(dto.getType())) {
            cell.setCellType(CellType.STRING);
            String strVal = "";
            List<NutMap> datalist = ctx.getList(NutMap.class, key);
            if (datalist != null && datalist.size() > 0) {
                try {
                    NutMap data = datalist.get(0);
                    Object object = data.get(dto.getY()[0]);
                    strVal = Strings.sNull(DateUtil.renderStrDate(object, dto.getDateFormat()));
                } catch (Exception e) {
                    strVal = "表达式不正确！解析异常！";
                }
            }
            cell.setCellValue(strVal);
        }
        renderDimension(); //重置此工作表中的所有单元格的行和列边界
    }

    private void insertRow(PoiDto dto, int rowMax, Sheet sheet, int rol) {
        if (dto.isAutoInsertStyleRow()) {
            //自动插入行
            if (rowMax > 1) {
                if (dto.isMerged()) {
                    insertRow(sheet, rol, rowMax - 1);
                } else {
                    insertRowAndMerged(sheet, rol, rowMax - 1);
                }
            }
        }
    }

    private void renderRowData(List<NutMap> datalist, int rowMax, String[] cols, String dateFormat, int rol, int col, Sheet sheet) {
        for (int i = 0; i < rowMax; i++) {
            NutMap d = datalist.get(i);
            if (d != null) {
                Row row = getRowOrCreateRow(sheet, rol + i);
                int rangeCol = 0;
                for (int j = 0; j < cols.length; j++) {
                    Cell rcell = getCellOrCreateCell(row, col + rangeCol + j);
                    CellRangeAddress cellRangeAddress = getCellRangeAddress(rcell);
                    //是合并单元格
                    if (cellRangeAddress != null) {
                        rangeCol = rangeCol + (cellRangeAddress.getLastColumn() - cellRangeAddress.getFirstColumn());
                    }
                    rcell.setCellType(CellType.STRING);
                    Object objVal = d.get(cols[j]);
                    String strVal = Strings.sNull(DateUtil.renderStrDate(objVal, dateFormat));
                    rcell.setCellValue(strVal);
                }
            }
        }
    }

    /****
     *  判断是否是合并单元格
     * @param cell
     * @return
     */
    public boolean isMerged(Cell cell) {
        return ExcelUtils.isMerged(cell);
    }

    /****
     * 取得合并单元格信息
     * @param cell
     * @return
     */
    public CellRangeAddress getCellRangeAddress(Cell cell) {
        return ExcelUtils.getCellRangeAddress(cell);
    }

    /**
     * 报表使用el表达式引擎重新写值
     *
     * @return
     */
    private void renderCell(Cell cell, Context ctx) {
        ExcelUtils.renderCell(cell, ctx);
    }

    public void insertImgages(Sheet sheet, PoiImagesDTO dto) {
        ExcelUtils.insertImgage(sheet, dto);
    }

    /***
     * 按行写入值 套用第 startRow 行数据的样式
     * @param sheetName 工作表名称
     * @param startRow   起始行
     * @param datalist 数据列表
     * @return
     */
    public void insetStyleRowDataList(String sheetName, int startRow, List<String[]> datalist) {
        Sheet sheet = wb.getSheet(sheetName);
        CellStyle[] cellStyles = null;
        Row lineOnerow = getRowOrCreateRow(sheet, startRow);
        int datalen = 0;
        if (datalist.size() > 0) {
            datalen = datalist.get(0).length;
        }
        cellStyles = new CellStyle[datalen];
        for (int i = 0; i < datalen; i++) {
            Cell cell = getCellOrCreateCell(lineOnerow, i);
            cellStyles[i] = cell.getCellStyle();
        }
        for (int i = 0; i < datalist.size(); i++) {
            Row row = getRowOrCreateRow(sheet, startRow);
            String[] ss = datalist.get(i);
            inserCellDatalist(row, ss, cellStyles);
            startRow++;
        }
    }

    /***
     * 按行写入值
     * @param sheetName 工作表名称
     * @param startRow   起始行
     * @param datalist 数据列表
     * @return
     */
    public void insetStringRowDataList(String sheetName, int startRow, List<String[]> datalist) {
        Sheet sheet = wb.getSheet(sheetName);
        for (int i = 0; i < datalist.size(); i++) {
            Row row = getRowOrCreateRow(sheet, startRow);
            String[] ss = datalist.get(i);
            inserStringCellDatalist(row, ss);
            startRow++;
        }
    }

    /***
     * 按行写入值
     * @param sheetName 工作表名称
     * @param startRow   起始行
     * @param datalist 数据列表
     * @return
     */
    public void insetRowDataList(String sheetName, int startRow, List<String[]> datalist) {
        Sheet sheet = wb.getSheet(sheetName);
        if (sheet == null) {
            sheet = wb.createSheet(sheetName);
        }
        for (int i = 0; i < datalist.size(); i++) {
            Row row = getRowOrCreateRow(sheet, startRow);
            String[] ss = datalist.get(i);
            inserCellDatalist(row, ss, null);
            startRow++;
        }
    }

    private void inserCellDatalist(Row row, String[] texts, CellStyle[] cellStyle) {
        int colNum = 0;
        for (String s : texts) {
            Cell cell = getCellOrCreateCell(row, colNum);
            if (cellStyle != null) {
                cell.setCellStyle(cellStyle[colNum]);
            }
            cell.setCellValue(s);
            colNum++;
        }
    }

    private void inserStringCellDatalist(Row row, String[] texts) {
        int colNum = 0;
        for (String s : texts) {
            Cell cell = getCellOrCreateCell(row, colNum);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(s);
            colNum++;
        }
    }

    /***
     * 取得指定的导入信息
     * @param sheeIndex
     * @param startRow
     * @param endColSize
     * @param parame //是否强行转换日期为字符串 此参数为了兼容以前的代码采用不固定参数模式，可传可不传
     * @return
     */
    public List<String[]> getImportList(int sheeIndex, int startRow, int endColSize, Parame... parame) {
        return getImportList(wb.getSheetAt(sheeIndex).getSheetName(), startRow, endColSize, parame);
    }

    /**
     * 给单元格设置  黑色-红色样式
     * <p>
     * 不支持合并单元格样式修改
     *
     * @param errorCellList
     * @param styleType     1--黑色 2--红色
     */
    public void setCellStyle(List<PoiInsertCellDataDTO> errorCellList, Workbook wb, int styleType) {
        CellStyle cellStyle = getCellErrorStyle(wb, styleType);
        for (PoiInsertCellDataDTO cellDto : errorCellList) {
            Sheet sheet = getSheetByName(cellDto.getSheetName());
            Cell cell = getCellOrCreateCell(getRowOrCreateRow(sheet, cellDto.getRow()), cellDto.getCol());
            if (null != cell) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    /**
     * 给单元格设置  黑色-红色样式
     * <p>
     * 不支持合并单元格样式修改
     *
     * @param sheet
     * @param row
     * @param col
     * @param cellStyle
     */
    public void setCellStyle(Sheet sheet, int row, int col, CellStyle cellStyle) {
        Cell cell = getCellOrCreateCell(getRowOrCreateRow(sheet, row), col);
        if (null != cell) {
            cell.setCellStyle(cellStyle);
        }
    }

    /**
     * 给单元格设置  黑色-红色样式
     * <p>
     * 不支持合并单元格样式修改
     *
     * @param sheet
     * @param row
     * @param col
     */
    public CellStyle getCellStyle(Sheet sheet, int row, int col) {
        Cell cell = getCellOrCreateCell(getRowOrCreateRow(sheet, row), col);
        return cell.getCellStyle() == null ? sheet.getWorkbook().createCellStyle() : cell.getCellStyle();
    }

    /**
     * 给单元格设置  黑色-红色样式
     * <p>
     * 不支持合并单元格样式修改
     *
     * @param styleType 1--黑色 2--红色
     */
    public CellStyle getCellErrorStyle(Workbook workbook, int styleType) {
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = wb.createFont();
        cellStyle.setBorderBottom(BorderStyle.THIN); //下边框
        cellStyle.setBorderLeft(BorderStyle.THIN);//左边框
        cellStyle.setBorderTop(BorderStyle.THIN);//上边框
        cellStyle.setBorderRight(BorderStyle.THIN);//右边框
        switch (styleType) {
            case 1:
                font.setColor(Font.COLOR_NORMAL);
                cellStyle.setBottomBorderColor(HSSFColor.HSSFColorPredefined.GREY_40_PERCENT.getIndex());
                cellStyle.setTopBorderColor(HSSFColor.HSSFColorPredefined.GREY_40_PERCENT.getIndex());
                cellStyle.setLeftBorderColor(HSSFColor.HSSFColorPredefined.GREY_40_PERCENT.getIndex());
                cellStyle.setRightBorderColor(HSSFColor.HSSFColorPredefined.GREY_40_PERCENT.getIndex());
                break;
            case 2:
                cellStyle.setBottomBorderColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
                cellStyle.setTopBorderColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
                cellStyle.setLeftBorderColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
                cellStyle.setRightBorderColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
                font.setColor(Font.COLOR_RED);
                cellStyle.setFont(font);
                break;
            default:
                break;
        }
        return cellStyle;
    }

    /**
     * 保存文件  前提是已经传入文件了
     *
     * @throws IOException
     */
    public void saveFile() {
        try (FileOutputStream outputStream = new FileOutputStream(new File(this.filePath))) {
            this.wb.setForceFormulaRecalculation(true);
            this.wb.write(outputStream);
            outputStream.flush();
            log.debug("修改保存Excle文件:" + this.filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setClleVal(Sheet sheet, int row, int col, String val) {
        Cell cell = getCellOrCreateCell(getRowOrCreateRow(sheet, row), col);
        if (null != cell) {
            cell.setCellType(CellType.STRING);
            cell.setCellValue(val);
        }
    }

    public void setClleValAndHiden(String sheetName, int row, int col, String val, String pass) {
        Sheet sheet = getSheetByName(sheetName);
        setClleVal(sheet, row, col, val);
        this.wb.setSheetHidden(this.wb.getSheetIndex(sheet), true);
        sheet.protectSheet(pass);
    }

    public void appendClleErrorInfo(Sheet sheet, int row, int col, String val) {
        Cell cell = getCellOrCreateCell(getRowOrCreateRow(sheet, row), col);
        if (null != cell) {
            cell.setCellType(CellType.STRING);
            String oldVal = getStringCellValue(cell);
            if ("".equals(oldVal)) {
                cell.setCellValue(val);
            } else {
                cell.setCellValue(oldVal + "," + val);
            }
        }
    }

    /**
     * 保护工作表
     *
     * @param index
     */
    public void lockSheet(int index, String pass) {
        Sheet sheet = getSheetByIndex(index);
        sheet.protectSheet(new String(pass));
    }

    public void setColumnWidth(String sheetName, int i, int width) {
        Sheet sheet = getSheetByName(sheetName);
        sheet.setColumnWidth(i, width);
    }

    /***
     * 取得指定的导入信息
     * @param sheetName
     * @param endColSize
     * @param parame//是否强行转换日期为字符串
     * @return
     */
    public List<String[]> getImportList(String sheetName, int startRow, int endColSize, Parame... parame) {
        Sheet sheet = wb.getSheet(sheetName);
        int rowSize = sheet.getLastRowNum();
        List<String[]> strings = new ArrayList<>();
        all:
        for (int x = startRow; x <= rowSize; x++) {
            Row row = sheet.getRow(x);
            if (row != null) {
                String[] temp = new String[endColSize];
                int cellSize = endColSize;
                int firstCellNum = 0;
                for (int i = firstCellNum; i < cellSize; i++) {
                    Cell cell = row.getCell(i);
                    temp[i] = getStringCellValue(cell, parame);
                }
                // 检查数组是否全部是空的
                boolean flag = false;
                sw:
                for (String str : temp) {
                    if (Strings.isNotBlank(str)) {
                        flag = true;
                        break sw;
                    }
                }
                if (flag) {
                    //不是全部空的
                    strings.add(temp);
                } else {
                    //如果全部是空的，就丢弃后面所有得数据
                    break all;
                }
            }
        }
        return strings;
    }

    /**
     * 根据工作薄名称写值
     *
     * @return
     */
    public void wirteSheetByName(String sheetName, List<PoiInsertCellDataDTO> cellsValList) throws IOException {
        Sheet sheet = wb.getSheet(sheetName);
        for (PoiInsertCellDataDTO c : cellsValList) {
            Row row = getRowOrCreateRow(sheet, c.getRow());
            Cell cell = getCellOrCreateCell(row, c.getCol());
            cell.setCellValue(c.getVal());
        }
    }

    /**
     * 写完成
     *
     * @throws IOException
     */
    public void saveWirte() throws IOException {
        renderDimension();
        this.wb.setForceFormulaRecalculation(true);
        this.wbWrite();
    }

    /**
     * 直接写到文件
     *
     * @throws IOException
     */
    public void toCreateNewFile(File file) throws IOException {
        if (file == null) {
            throw new RuntimeException("参数错误 ! file is null");
        }
        file.createNewFile();
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            this.wb.write(outputStream);
            outputStream.flush();
            outputStream.close();
            this.wb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重置此工作表中的所有单元格的行和列边界
     */
    public void renderDimension() {
        ExcelUtils.renderDimension(this.wb);
    }

    /**
     * 给out写流
     *
     * @throws IOException
     */
    private void wbWrite() throws IOException {
        if (this.out != null) {
            this.wb.write(this.out);
            this.wb.close();
        }
    }

    /**
     * 指定列为日期格式
     *
     * @param sheetName
     * @param colIndex
     * @param formatStr
     * @return
     */
    public void setColumnDateFormat(String sheetName, int colIndex, String formatStr) {
        Sheet sheet = this.wb.getSheet(sheetName);
        CellStyle cellStyle = this.wb.createCellStyle();
        DataFormat format = this.wb.createDataFormat();
        cellStyle.setDataFormat(format.getFormat(formatStr));
        sheet.setDefaultColumnStyle(colIndex, cellStyle);
        //2193表示1906-01-01
        String formula = "2193";
        DataValidationConstraint constraint = new XSSFDataValidationConstraint(
                DataValidationConstraint.ValidationType.DATE,
                DataValidationConstraint.OperatorType.GREATER_OR_EQUAL, formula
        );
        CellRangeAddressList regions = new CellRangeAddressList(1, 65335, colIndex, colIndex);
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) sheet);
        XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(constraint, regions);
        validation.createErrorBox("错误提示", MessageFormat.format("请按照{0}格式输入日期,并且日期需要大于等于1906-01-01", formatStr));
        validation.setSuppressDropDownArrow(true);
        validation.setShowErrorBox(true);
        sheet.addValidationData(validation);
    }

    /**
     * 冻结行列
     *
     * @param sheetName
     * @param colSplit
     * @param rowSplit
     * @param leftmostColumn
     * @param topRow
     */
    public void createFreezePane(String sheetName, int colSplit, int rowSplit, int leftmostColumn, int topRow) {
        Sheet sheet = this.wb.getSheet(sheetName);
        sheet.createFreezePane(colSplit, rowSplit, leftmostColumn, topRow);
    }

    /**
     * 指定列为文本格式
     *
     * @param sheetName
     * @param colIndex
     * @param textLength
     * @return
     */
    public void setColumnTextFormat(String sheetName, int colIndex, int textLength) {
        Sheet sheet = this.wb.getSheet(sheetName);
        CellStyle cellStyle = this.wb.createCellStyle();
        DataFormat format = this.wb.createDataFormat();
        cellStyle.setDataFormat(format.getFormat("@"));
        sheet.setDefaultColumnStyle(colIndex, cellStyle);
        DataValidationConstraint constraint = new XSSFDataValidationConstraint(
                DataValidationConstraint.ValidationType.TEXT_LENGTH,
                DataValidationConstraint.OperatorType.LESS_OR_EQUAL, String.valueOf(textLength)
        );
        CellRangeAddressList regions = new CellRangeAddressList(1, 65335, colIndex, colIndex);
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) sheet);
        XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(constraint, regions);
        validation.createErrorBox("错误提示", MessageFormat.format("字符串长度不能大于{0}", textLength));
        validation.setSuppressDropDownArrow(true);
        validation.setShowErrorBox(true);
        sheet.addValidationData(validation);
    }

    /**
     * 指定列为保留小数格式
     *
     * @param sheetName
     * @param colIndex
     * @param size
     * @return
     */
    public void setColumnDecimalFormat(String sheetName, int colIndex, int size, int point) {
        Sheet sheet = this.wb.getSheet(sheetName);
        CellStyle cellStyle = this.wb.createCellStyle();
        DataFormat format = this.wb.createDataFormat();
        StringBuilder pointBuilder = new StringBuilder("");
        if (point == 0) {
            cellStyle.setDataFormat(format.getFormat("#,#0"));
        } else {
            StringBuilder builder = new StringBuilder("#,#0.");
            for (int i = 0; i < point; i++) {
                builder.append("0");
                pointBuilder.append("9");
            }
            cellStyle.setDataFormat(format.getFormat(builder.toString()));
        }
        sheet.setDefaultColumnStyle(colIndex, cellStyle);

        StringBuilder builder = new StringBuilder("");
        for (int i = 0; i < size - point; i++) {
            builder.append("9");
        }
        if (point > 0) {
            builder.append(".");
            builder.append(pointBuilder);
        }
        DataValidationConstraint constraint = new XSSFDataValidationConstraint(
                DataValidationConstraint.ValidationType.DECIMAL,
                DataValidationConstraint.OperatorType.BETWEEN, "-".concat(builder.toString()), builder.toString()
        );
        CellRangeAddressList regions = new CellRangeAddressList(1, 65335, colIndex, colIndex);
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) sheet);
        XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(constraint, regions);
        validation.createErrorBox("错误提示", MessageFormat.format("只能输入{0}至{1}之间的数值", "-".concat(builder.toString()), builder.toString()));
        validation.setSuppressDropDownArrow(true);
        validation.setShowErrorBox(true);
        sheet.addValidationData(validation);

    }

    /**
     * 设置某些列的值只能输入预制的数据,显示下拉框.
     *
     * @param sheetName     要设置的sheet.
     * @param textlist      下拉框显示的内容
     * @param errorCanInput 验证错误也可以输入
     */
    public void setSheetValidation(String sheetName, List<Integer> cellRangeAddressLists, List<String[]> textlist, boolean errorCanInput) {
        Sheet sheet = getSheetByName(sheetName);
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        //2003
        if (sheet instanceof HSSFSheet) {
            for (int i = 0; i < cellRangeAddressLists.size(); i++) {
                dropDownList(wb, sheet, textlist.get(i), cellRangeAddressLists.get(i), cellRangeAddressLists.get(i), errorCanInput);
            }
        } else
            //2007
            if (sheet instanceof XSSFSheet) {
                for (int i = 0; i < cellRangeAddressLists.size(); i++) {
                    dropDownList2007(wb, sheet, textlist.get(i), cellRangeAddressLists.get(i), cellRangeAddressLists.get(i), errorCanInput);
                }
            }
    }

    /**
     * 设置错误信息
     *
     * @param sheet
     * @param rowIndex
     * @param errorMsg
     * @param style
     */
    public void setErrorMsg(Sheet sheet, int rowIndex, int colIndex, String errorMsg, CellStyle style) {
        setCellStyle(sheet, rowIndex, colIndex, style);
        Row row = sheet.getRow(rowIndex);
        int a = row.getLastCellNum();
        if (a < 7) {
            a = 7;
        }
        Cell cell = row.createCell(a);
        cell.setCellType(CellType.STRING);
        cell.setCellStyle(style);
        cell.setCellValue(errorMsg);
    }

    /**
     * 取得excle数据的时候进行参数的转换控制可以自定义扩展
     */
    public static class Parame extends ExcleGetParamesDTO {

    }

    public class MergedModel extends MergedDTO {

    }
}
