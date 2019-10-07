/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.util.excel;

import com.nutzfw.core.common.util.DateUtil;
import com.nutzfw.core.common.util.excel.dto.ExcleGetParamesDTO;
import com.nutzfw.core.common.util.excel.dto.MergedDTO;
import com.nutzfw.core.common.util.excel.dto.PoiImagesDTO;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.*;
import org.nutz.el.El;
import org.nutz.lang.random.R;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetDimension;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/1/10
 */
public class ExcelUtils {


    static final Log     log                 = Logs.get();
    /**
     * 自定义表达式
     */
    static final Pattern ESPRESSIONE_PATTERN = Pattern.compile("^\\#\\{.*?}$");

    /**
     * 按模版对全部sheet页计算渲染
     * <p>
     * context 中的list支持NutMap和数据实体类
     *
     * @param context
     * @param template
     * @param outFile
     * @return
     * @throws IOException
     */
    public static File renderAllSheetMacro(Context context, File template, File outFile) throws IOException {
        return new ExcelTemplate(template, outFile).renderAllSheetMacro(context).save();
    }

    /**
     * 按模版对指定sheetName页计算渲染
     * <p>
     * context 中的list支持NutMap和数据实体类
     *
     * @param context
     * @param template
     * @param outFile
     * @return
     * @throws IOException
     */
    public static File renderSheetMacro(Context context, String sheetName, File template, File outFile) throws IOException {
        return new ExcelTemplate(template, outFile).renderSheetMacro(sheetName, context).save();
    }

    /**
     * 格式化行
     *
     * @param sheet
     * @param srcRowIndex 源格式行
     * @param stratRow    开始行
     * @param n           连续格式化N行
     */
    public static void formatRows(Sheet sheet, int srcRowIndex, int stratRow, int n) {
        formatRows(sheet, srcRowIndex, stratRow, n, false);
    }

    /**
     * 格式化行
     *
     * @param sheet
     * @param srcRowIndex    源格式行
     * @param stratRow       开始行
     * @param n              连续格式化N行
     * @param copyRowCellVal 是否复制cell的值
     */
    public static void formatRows(Sheet sheet, int srcRowIndex, int stratRow, int n, boolean copyRowCellVal) {
        Row srcRow = sheet.getRow(srcRowIndex);
        if (null == srcRow) {
            throw new RuntimeException("源格式行不存在！");
        }
        int endCellIndex = srcRow.getLastCellNum() < 0 ? 0 : srcRow.getLastCellNum();
        int endRowIndex = stratRow + n;
        for (int index = stratRow; index <= endRowIndex; index++) {
            Row row = CellUtil.getRow(index, sheet);
            //设置行样式
            row.setHeight(srcRow.getHeight());
            row.setRowStyle(srcRow.getRowStyle());
            for (int i = 0; i < endCellIndex; i++) {
                Cell srcCell = CellUtil.getCell(srcRow, i);
                Cell newCell = CellUtil.getCell(row, i);
                copyCell(srcCell, newCell, copyRowCellVal);
                CellRangeAddress cellAddresses = getCellRangeAddress(srcCell);
                if (cellAddresses != null) {
                    //在合并单元格范围内
                    if (cellAddresses.getFirstColumn() == srcCell.getColumnIndex()) {
                        //是由 srcCell列 开始合并的，那么添加合并
                        addMergedRegion(sheet, new CellRangeAddress(
                                index,
                                cellAddresses.getLastRow() - cellAddresses.getFirstRow() + index,
                                cellAddresses.getFirstColumn(),
                                cellAddresses.getLastColumn()
                        ));
                    }
                }
            }
        }
    }

    /**
     * 添加合并单元格信息，并过滤异常
     *
     * @param sheet
     * @param addresses
     */
    public static void addMergedRegion(Sheet sheet, CellRangeAddress addresses) {
        try {
            sheet.addMergedRegion(addresses);
        } catch (Exception e) {
        }
    }

    /**
     * 插入行
     *
     * @param sheet
     * @param startRow   开始行
     * @param insertRows 插入行数
     */
    public static void insertRow(Sheet sheet, int startRow, int insertRows) {
        //最后一行
        int lastRow = sheet.getLastRowNum();
        //插入后最后行
        int afterLastRow = lastRow + insertRows;
        //需要移动的行数
        int moveRow = lastRow - startRow;
        //倒数移动行
        for (int i = 0; i <= moveRow; i++) {
            Row oldRow = CellUtil.getRow(lastRow - i, sheet);
            int start = oldRow.getFirstCellNum() < 0 ? 0 : oldRow.getFirstCellNum();
            int end = oldRow.getLastCellNum() < 0 ? 0 : oldRow.getLastCellNum();
            Row newRow = CellUtil.getRow(afterLastRow - i, sheet);
            newRow.setHeight(oldRow.getHeight());
            if (null == oldRow.getRowStyle()) {
                newRow.setRowStyle(oldRow.getRowStyle());
            }
            for (; start <= end; start++) {
                copyCell(CellUtil.getCell(oldRow, start), newRow.createCell(start), true);
            }
            sheet.removeRow(oldRow);
        }
        resetComputeMerge(sheet, startRow, insertRows);
    }

    /**
     * 重置合并单元格信息
     *
     * @param sheet
     * @param startRow   开始行
     * @param insertRows 插入行
     */
    private static void resetComputeMerge(Sheet sheet, int startRow, int insertRows) {
        int mergedRegionsIndexCount = sheet.getMergedRegions().size();
        List<CellRangeAddress> newAddressList = new ArrayList<>(mergedRegionsIndexCount);
        for (int index = mergedRegionsIndexCount - 1; index >= 0; index--) {
            CellRangeAddress cellAddresses = resetComputeMerge(sheet.getMergedRegion(index), startRow, insertRows);
            if (cellAddresses != null) {
                newAddressList.add(resetComputeMerge(sheet.getMergedRegion(index), startRow, insertRows));
                sheet.removeMergedRegion(index);
            }
        }
        newAddressList.forEach(cellAddresses -> sheet.addMergedRegion(cellAddresses));
    }

    /**
     * 重置合并单元格信息
     *
     * @param address    旧的合并信息
     * @param startRow   开始行
     * @param insertRows 插入行
     * @return CellAddresses    新的合并信息
     */
    private static CellRangeAddress resetComputeMerge(CellRangeAddress address, int startRow, int insertRows) {
        if (address.getFirstRow() >= startRow) {
            int cellFirstRow = address.getFirstRow() + insertRows;
            int cellLastRow = address.getLastRow() + insertRows;
            int cellFirstCol = address.getFirstColumn();
            int cellLastCol = address.getLastColumn();
            return new CellRangeAddress(cellFirstRow, cellLastRow, cellFirstCol, cellLastCol);
        }
        return null;
    }

    /**
     * 复制单元格
     *
     * @param srcCell       源单元格
     * @param distCell      目标单元格
     * @param copyValueFlag true则连同cell的内容一起复制
     */
    public static void copyCell(Cell srcCell, Cell distCell, boolean copyValueFlag) {
        //样式
        distCell.setCellStyle(srcCell.getCellStyle());
        //评论
        if (srcCell.getCellComment() != null) {
            distCell.setCellComment(srcCell.getCellComment());
        }
        // 不同数据类型处理
        CellType srcCellType = srcCell.getCellType();
        distCell.setCellType(srcCellType);
        if (copyValueFlag) {
            if (srcCellType == CellType.NUMERIC) {
                if (HSSFDateUtil.isCellDateFormatted(srcCell)) {
                    distCell.setCellValue(srcCell.getDateCellValue());
                } else {
                    distCell.setCellValue(srcCell.getNumericCellValue());
                }
            } else if (srcCellType == CellType.STRING) {
                distCell.setCellValue(srcCell.getRichStringCellValue());
            } else if (srcCellType == CellType.BLANK) {
                // nothing21
            } else if (srcCellType == CellType.BOOLEAN) {
                distCell.setCellValue(srcCell.getBooleanCellValue());
            } else if (srcCellType == CellType.ERROR) {
                distCell.setCellErrorValue(srcCell.getErrorCellValue());
            } else if (srcCellType == CellType.FORMULA) {
                distCell.setCellFormula(srcCell.getCellFormula());
            } else { // nothing29
            }
        }
    }

    /**
     * 判断是否是合并单元格
     *
     * @param cell
     * @return
     */
    public static boolean isMerged(Cell cell) {
        return getCellRangeAddress(cell) != null;
    }

    /**
     * 取得合并单元格信息
     *
     * @param cell
     * @return
     */
    public static CellRangeAddress getCellRangeAddress(Cell cell) {
        Row row = cell.getRow();
        Sheet sheet = row.getSheet();
        List<CellRangeAddress> cellRangeAddresses = sheet.getMergedRegions();
        for (CellRangeAddress cellRangeAddress : cellRangeAddresses) {
            if (cellRangeAddress.isInRange(row.getRowNum(), cell.getColumnIndex())) {
                return cellRangeAddress;
            }
        }
        return null;
    }

    /**
     * 获取工作簿
     *
     * @param workbook
     * @param name
     * @return
     */
    public static Sheet getSheetByName(Workbook workbook, String name) {
        Sheet sheet = workbook.getSheet(name);
        if (sheet == null) {
            throw new RuntimeException(name + "工作薄不存在");
        }
        return sheet;
    }

    /**
     * 获取工作簿
     *
     * @param workbook
     * @param index
     * @return
     */
    public static Sheet getSheetByIndex(Workbook workbook, int index) {
        Sheet sheet = workbook.getSheetAt(index);
        if (sheet == null) {
            throw new RuntimeException("第" + index + "工作薄不存在");
        }
        return sheet;
    }

    /**
     * 插入图片
     *
     * @param sheet
     * @param dto
     */
    public static void insertImgage(Sheet sheet, PoiImagesDTO dto) {
        try {
            if (sheet instanceof XSSFSheet) {
                XSSFDrawing drawing = (XSSFDrawing) sheet.getDrawingPatriarch();
                if (drawing == null) {
                    drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
                }
                XSSFClientAnchor anchor = new XSSFClientAnchor(
                        dto.getDx1(),
                        dto.getDy1(),
                        dto.getDx2(),
                        dto.getDy2(),
                        dto.getCol1(),
                        dto.getRow1(),
                        dto.getCol2(),
                        dto.getRow2()
                );
                drawing.createPicture(anchor, sheet.getWorkbook().addPicture(org.nutz.lang.Files.readBytes(dto.getImgFile()), XSSFWorkbook.PICTURE_TYPE_PNG));
            }
        } catch (Exception e) {
            log.error("不支持2003插入图片");
        }
    }

    /**
     * 报表使用el表达式引擎重新写值
     *
     * @param cell
     * @param ctx
     */
    public static void renderCell(Cell cell, Context ctx) {
        if (cell != null) {
            //如果是字符串
            if (cell.getCellType() == CellType.STRING) {
                String val = cell.getStringCellValue();
                try {
                    CharSegment msg = new CharSegment(val);
                    Map<String, El> els = new HashMap<>(1);
                    if (msg.hasKey()) {
                        els = new HashMap(msg.keys().size());
                        for (String key : msg.keys()) {
                            els.put(key, new El(key));
                        }
                    }
                    PoiImagesDTO imagesDTO = null;
                    for (String key : msg.keys()) {
                        Object obj = ctx.get(key);
                        if (obj instanceof PoiImagesDTO) {
                            val = "";
                            imagesDTO = (PoiImagesDTO) obj;
                        } else {
                            ctx.set(key, els.get(key).eval(ctx));
                        }
                    }
                    if (imagesDTO != null) {
                        ExcelUtils.insertImgage(cell.getSheet(), imagesDTO);
                    } else {
                        val = msg.render(ctx).toString();
                    }
                } catch (Exception e) {
                    log.error(e);
                    val = "函数执行异常：" + e.getMessage();
                }
                cell.setCellValue(val);
            }
        }
    }

    /**
     * 判断是否是自定义表达式
     *
     * @param str
     * @return
     */
    public static boolean isEspressione(String str) {
        return ESPRESSIONE_PATTERN.matcher(str).matches();
    }

    /**
     * 重置此工作表中的所有单元格的行和列边界
     *
     * @param wb
     */
    public static void renderDimension(Workbook wb) {
        try {
            int total = wb.getNumberOfSheets();
            for (int i = 0; i < total; i++) {
                Sheet sheet = wb.getSheetAt(i);
                CellRangeAddress cellRangeAddress = new CellRangeAddress(
                        sheet.getFirstRowNum(),
                        sheet.getLastRowNum(),
                        0,
                        sheet.getPhysicalNumberOfRows()
                );
                if (sheet instanceof XSSFSheet) {
                    CTWorksheet ctWorksheet = ((XSSFSheet) sheet).getCTWorksheet();
                    CTSheetDimension ctSheetDimension = ctWorksheet.getDimension();
                    ctSheetDimension.setRef(cellRangeAddress.formatAsString());
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * 获取所有的工作簿名称
     *
     * @return
     */
    public static List<String> getExcleSheetNames(Workbook workbook) {
        List<String> sheetNames = new ArrayList<>();
        int total = workbook.getNumberOfSheets();
        for (int i = 0; i < total; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            sheetNames.add(sheet.getSheetName());
        }
        return sheetNames;
    }

    /**
     * 判断是否是日期或时间单元格
     *
     * @param cell
     * @return
     */
    public static boolean isCellDateFormatted(Cell cell) {
        CellStyle cellStyle = cell.getCellStyle();
        String dataFormat = cellStyle.getDataFormatString();
        //处理所有日期-时间样式的单元格
        if ("m/d/yy".equals(dataFormat) ||
                "yyyy/m/d;@".equals(dataFormat) ||
                "[DBNum1][$-804]yyyy\"年\"m\"月\"d\"日\";@".equals(dataFormat) ||
                "yyyy\"年\"m\"月\";@".equals(dataFormat) ||
                "m\"月\"d\"日\";@".equals(dataFormat) ||
                "yyyy\"年\"m\"月\"d\"日\";@".equals(dataFormat) ||
                "yyyy\\-mm\\-dd\\ hh:mm:ss".equals(dataFormat) ||
                "[DBNum1][$-804]m\"月\"d\"日\";@".equals(dataFormat) ||
                "[$-F800]dddd\\,\\ mmmm\\ dd\\,\\ yyyy".equals(dataFormat) ||
                "[$-409]yyyy/m/d\\ h:mm\\ AM/PM;@".equals(dataFormat) ||
                "yyyy/m/d\\ h:mm;@".equals(dataFormat) ||
                "yy/m/d;@".equals(dataFormat) ||
                "m/d/yy;@".equals(dataFormat) ||
                "mm/dd/yy;@".equals(dataFormat) ||
                "h:mm;@".equals(dataFormat) ||
                "[$-409]h:mm\\ AM/PM;@".equals(dataFormat) ||
                "h:mm:ss;@".equals(dataFormat) ||
                "[$-409]h:mm:ss\\ AM/PM;@".equals(dataFormat) ||
                "h\"时\"mm\"分\";@".equals(dataFormat) ||
                "h\"时\"mm\"分\"ss\"秒\";@".equals(dataFormat) ||
                "上午/下午h\"时\"mm\"分\";@".equals(dataFormat) ||
                "上午/下午h\"时\"mm\"分\"ss\"秒\";@".equals(dataFormat) ||
                "[DBNum1][$-804]h\"时\"mm\"分\";@".equals(dataFormat) ||
                "[DBNum1][$-804]上午/下午h\"时\"mm\"分\";@".equals(dataFormat) ||
                "[$-F400]h:mm:ss\\ AM/PM".equals(dataFormat) ||
                "yyyy/m/d h:mm;@".equals(dataFormat) ||
                "mm/dd/yy;@".equals(dataFormat) ||
                "m/d/yy;@".equals(dataFormat)
        ) {
            return true;
        }
        return false;
    }

    /**
     * 获取单元格数据内容为字符串类型的数据
     *
     * @param cell Excel单元格
     * @return String 单元格数据内容
     */
    public static String getStringCellValue(Cell cell, ExcleGetParamesDTO paramesDTO) {
        String val = "";
        if (cell == null) {
            return val;
        }
        try {
            if (paramesDTO.getConverDateToStr()) {
                if (isCellDateFormatted(cell)) {
                    Date date = null;
                    String cellStrval = "";
                    try {
                        date = cell.getDateCellValue();
                    } catch (Exception e) {
                        if (cell.getCellType() == CellType.STRING) {
                            cellStrval = cell.getStringCellValue();
                        }
                    }
                    cell.setCellType(CellType.STRING);
                    if (date == null) {
                        cell.setCellValue(cellStrval);
                    } else {
                        cell.setCellValue(DateUtil.date2string(date, paramesDTO.getDateFormat()));
                    }
                }
            }
            //默认全部使用字符串处理
            if (CellType.NUMERIC == cell.getCellType()) {
                double oldVal = cell.getNumericCellValue();
                cell.setCellType(CellType.STRING);
                String newVal = cell.getStringCellValue();
                DecimalFormat df = null;
                if (newVal.indexOf("E+") > -1) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue(oldVal);
                    df = new DecimalFormat("#");
                    newVal = df.format(cell.getNumericCellValue());
                }
                cell.setCellValue(newVal);
            }
            cell.setCellType(CellType.STRING);
        } catch (Exception e) {
            log.error(e);
        }
        CellType cellType = cell.getCellType();
        if (cellType == CellType.STRING) {
            val = cell.getStringCellValue();
        } else if (cellType == CellType.NUMERIC) {
            //忽略double的小数点数据
            val = String.valueOf(cell.getNumericCellValue());
        } else if (cellType == CellType.BOOLEAN) {
            val = String.valueOf(cell.getBooleanCellValue());
        } else {
            val = "";
        }
        return val;
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
        HSSFWorkbook workbook = (HSSFWorkbook) wb;
        if (datas.length == 0) {
            return workbook;
        }
        // 创建一个数据源sheet
        HSSFSheet hidden = workbook.createSheet(R.UU16());
        // 数据源sheet页不显示
        workbook.setSheetHidden(workbook.getSheetIndex(hidden), true);
        // 将下拉列表的数据放在数据源sheet上
        for (int i = 0, length = datas.length; i < length; i++) {
            HSSFRow row = hidden.createRow(i);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(datas[i]);
        }
        // 指定下拉数据时，给定目标数据范围 hiddenSheetName!$A$1:$A   隐藏sheet的A1格的数据
        DVConstraint constraint = DVConstraint.createFormulaListConstraint(hidden.getSheetName() + "!$A$1:$A" + datas.length);
        CellRangeAddressList addressList = new CellRangeAddressList(1, 65335, startCol, endCol);
        // 指定单元格下拉数据
        HSSFDataValidation validation = new HSSFDataValidation(addressList, constraint);
        validation.setSuppressDropDownArrow(true);
        validation.setShowErrorBox(!errorCanInput);
        realSheet.addValidationData(validation);
        hidden.protectSheet(new String(R.UU16()));
        return workbook;
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
        XSSFWorkbook workbook = (XSSFWorkbook) wb;
        // 创建一个数据源sheet
        XSSFSheet hidden = workbook.createSheet(R.UU16());
        // 数据源sheet页不显示
        workbook.setSheetHidden(workbook.getSheetIndex(hidden), true);
        // 将下拉列表的数据放在数据源sheet上
        for (int i = 0, length = datas.length; i < length; i++) {
            XSSFRow row = hidden.createRow(i);
            XSSFCell cell = row.createCell(0);
            cell.setCellValue(datas[i]);
        }
        // 指定下拉数据时，给定目标数据范围 hiddenSheetName!$A$1:$A   隐藏sheet的A1格的数据
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) realSheet);
        XSSFDataValidationConstraint constraint = (XSSFDataValidationConstraint) dvHelper.createFormulaListConstraint(hidden.getSheetName() + "!$A$1:$A" + datas.length);
        CellRangeAddressList addressList = new CellRangeAddressList(1, 65335, startCol, endCol);
        DataValidation validation = dvHelper.createValidation(constraint, addressList);
        validation.setShowErrorBox(!errorCanInput);
        validation.setSuppressDropDownArrow(true);
        realSheet.addValidationData(validation);
        hidden.protectSheet(new String(R.UU16()));
        return workbook;
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
    private static List<MergedDTO> horizontalLikeMerged(Sheet sheet, int rowIndex, int rowEnd, int columnIndex, int colEnd) {
        List<MergedDTO> list = new ArrayList<>();
        for (int rowNum = rowIndex; rowNum < rowEnd; rowNum++) {
            Row row = CellUtil.getRow(rowNum, sheet);
            int num = columnIndex;
            do {
                Cell cell = CellUtil.getCell(row, num);
                int tempNum = num;
                MergedDTO model = new MergedDTO();
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
    public static List<MergedDTO> verticalLikeMerged(Sheet sheet, int rowIndex, int rowEnd, int columnIndex, int colEnd) {
        List<MergedDTO> list = new ArrayList<>();
        for (int colNum = columnIndex; colNum < colEnd; colNum++) {
            int num = rowIndex;
            do {
                MergedDTO model = new MergedDTO();
                Row row = CellUtil.getRow(num, sheet);
                Cell cell = CellUtil.getCell(row, colNum);
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

    private static int verticalLike(Sheet sheet, MergedDTO model, int columnIndex, int rowIndex, int rowEnd) {
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

    private static int horizontallLike(Sheet sheet, MergedDTO model, int columnIndex, int rowIndex, int colEnd) {
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
}
