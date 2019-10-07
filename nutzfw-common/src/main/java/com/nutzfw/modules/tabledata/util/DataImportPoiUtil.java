/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.tabledata.util;

import com.nutzfw.core.common.util.DateUtil;
import com.nutzfw.core.common.util.excel.ExcelUtils;
import com.nutzfw.core.common.util.excel.PoiExcelUtil;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.sys.entity.DataTable;
import com.nutzfw.modules.sys.entity.Dict;
import com.nutzfw.modules.sys.entity.TableFields;
import com.nutzfw.modules.tabledata.enums.FieldType;
import com.nutzfw.modules.tabledata.enums.TableType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.nutz.lang.Encoding;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.repo.Base64;

import java.io.*;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.nutzfw.modules.tabledata.biz.DataMaintainBiz.DELIMITER;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/19
 * 描述此类：
 */
public class DataImportPoiUtil {

    public static final String IMPORT_DICT_NOT_FOUND = "IMPORT_DICT_NOT_FOUND";

    private Log log = Logs.get();

    private Workbook wb;

    private OutputStream outputStream;

    private CellStyle errorStyle;

    private HashMap<String, String> userAccountMap = new HashMap<>();

    public DataImportPoiUtil(Path path) throws IOException {
        this.wb = new XSSFWorkbook(new FileInputStream(path.toFile()));
        this.outputStream = new FileOutputStream(path.toFile());
    }

    public DataImportPoiUtil(File file) throws IOException {
        this.wb = new XSSFWorkbook(new FileInputStream(file));
        this.outputStream = new FileOutputStream(file);

    }


    /***
     * 取得指定的导入信息
     * @param sheetName
     * @param headerInfo
     * @param dataTable
     * @return
     */
    public List<NutMap> getImportDataList(String sheetName, int startRow, HashMap<Integer, TableFields> headerInfo, HashMap<String, Dict> lableAndIdDictsInfo, HashMap<String, Dict> sysCodeAndLableDictsInfo,
                                          UserAccountService accountService, DataTable dataTable) {
        List<NutMap> strings = new ArrayList<>();
        //缓存当前操作的cell，一旦该单元格发生错误，好记录当前行列号
        AtomicReference<Cell> errorCell = new AtomicReference<>();
        try {
            Sheet sheet = wb.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("错误：导入模版版本不是最新的！");
            }
            //暂时支持5000条数据
            int rowSize = sheet.getLastRowNum() > 5000 ? 5000 : sheet.getLastRowNum();
            for (int x = startRow; x <= rowSize; x++) {
                Row row = sheet.getRow(x);
                if (row != null) {
                    NutMap record = new NutMap();
                    record.put(dataTable.getPrimaryKey(), "");
                    record.put(".table", dataTable.getTableName());
                    if (dataTable.getTableType() != TableType.SingleTable) {
                        Cell userNameCell = row.getCell(0);
                        String userName = userNameCell.getStringCellValue().trim();
                        if (userAccountMap.containsKey(userName)) {
                            record.put("userid", userAccountMap.get(userName));
                            record.put("userName", userName);
                        } else {
                            UserAccount userAccount = accountService.fetchByUserName(userName);
                            if (userAccount != null) {
                                userAccountMap.put(userName, userAccount.getUserid());
                                record.put("userid", userAccountMap.get(userName));
                                record.put("userName", userName);
                            }
                        }
                    }
                    headerInfo.forEach((integer, fields) -> {
                        Cell cell = row.getCell(integer);
                        errorCell.set(cell);
                        record.put(fields.getFieldName(), null);
                        if (cell != null) {
                            //忽略附件字段
                            if (!(fields.getFieldType() == FieldType.MultiAttach.getValue() || fields.getFieldType() == FieldType.SingleAttach.getValue())) {
                                if (Strings.isNotBlank(fields.getDictSysCode())) {
                                    //是字典
                                    String val = cell.getStringCellValue().trim();
                                    if (fields.isMultipleDict()) {
                                        //多选字典
                                        val = val.replaceAll("，", ",");
                                        String[] dicts = Strings.splitIgnoreBlank(val);
                                        HashSet<Integer> dictVals = new HashSet<>();
                                        Set<String> notFounds = new HashSet<>();
                                        for (String dictVal : dicts) {
                                            Dict dictDetail;
                                            if (dictVal.indexOf(DELIMITER) > 0) {
                                                dictDetail = lableAndIdDictsInfo.get(dictVal);
                                            } else {
                                                dictDetail = sysCodeAndLableDictsInfo.get(fields.getDictSysCode().concat(dictVal));
                                            }
                                            if (dictDetail != null) {
                                                dictVals.add(dictDetail.getId());
                                            } else {
                                                notFounds.add(dictVal);
                                            }
                                        }
                                        if (notFounds.size() > 0) {
                                            record.put(fields.getFieldName(), Strings.join(",", dictVals) + IMPORT_DICT_NOT_FOUND + Strings.join(",", notFounds));
                                        } else {
                                            record.put(fields.getFieldName(), Strings.join(",", dictVals));
                                        }
                                    } else {
                                        Dict dictDetail;
                                        if (val.indexOf(DELIMITER) > 0) {
                                            dictDetail = lableAndIdDictsInfo.get(val);
                                        } else {
                                            dictDetail = sysCodeAndLableDictsInfo.get(fields.getDictSysCode().concat(val));
                                        }
                                        if (dictDetail != null) {
                                            record.put(fields.getFieldName(), dictDetail.getId());
                                        } else {
                                            record.put(fields.getFieldName(), val + IMPORT_DICT_NOT_FOUND + val);
                                        }
                                    }
                                } else {
                                    if (fields.getFieldType() == FieldType.Date.getValue()) {
                                        //日期型
                                        Date date = cell.getDateCellValue();
                                        if (date != null) {
                                            record.put(fields.getFieldName(), DateUtil.date2date(date, DateUtil.YYYY_MM_DD_HH_MM_SS));
                                        }
                                    } else if (fields.getFieldType() == FieldType.String.getValue() || fields.getFieldType() == FieldType.Text.getValue()) {
                                        //文本型
                                        if (CellType.STRING == cell.getCellType()) {
                                            record.put(fields.getFieldName(), cell.getStringCellValue().trim());
                                        } else if (CellType.NUMERIC == cell.getCellType()) {
                                            DecimalFormat decimalFormat = new DecimalFormat("0");
                                            record.put(fields.getFieldName(), decimalFormat.format(cell.getNumericCellValue()));
                                        }
                                    } else if (fields.getFieldType() == FieldType.Decimal.getValue()) {
                                        //数值型
                                        StringBuilder builder = new StringBuilder("");
                                        for (int i = 0; i < fields.getLength() - fields.getDecimalPoint(); i++) {
                                            builder.append("0");
                                        }
                                        if (fields.getDecimalPoint() > 0) {
                                            builder.append(".");
                                            for (int i = 0; i < fields.getDecimalPoint(); i++) {
                                                builder.append("0");
                                            }
                                        }
                                        DecimalFormat decimalFormat = new DecimalFormat(builder.toString());
                                        if (CellType.STRING == cell.getCellType()) {
                                            double val = Double.parseDouble(cell.getStringCellValue().trim());
                                            record.put(fields.getFieldName(), decimalFormat.format(val));
                                        } else if (CellType.NUMERIC == cell.getCellType()) {
                                            double val = cell.getNumericCellValue();
                                            record.put(fields.getFieldName(), decimalFormat.format(val));
                                        }
                                    }
                                }
                            }
                        } else {
                            record.put(fields.getFieldName(), null);
                        }
                    });
                    boolean isAllNotNull = true;
                    sw:
                    for (Map.Entry<String, Object> entry : record.entrySet()) {
                        boolean isSystem = "id".equals(entry.getKey()) || ".table".equals(entry.getKey());
                        if (!isSystem && null != entry.getValue()) {
                            isAllNotNull = false;
                            break sw;
                        }
                    }
                    if (!isAllNotNull) {
                        strings.add(record);
                    }
                }
            }
        } catch (Exception e) {
            Cell cell = errorCell.get();
            if (cell != null) {
                log.error(e);
                throw new RuntimeException(MessageFormat.format("在第{0}行 第{1}列 发生错误：{2}", cell.getRowIndex() + 1, PoiExcelUtil.getExcelColumnName(cell.getColumnIndex()), e.getLocalizedMessage()));
            } else {
                throw e;
            }
        }
        return strings;
    }

    /**
     * 设置错误信息
     *
     * @param sheetName
     * @param rowIndex
     * @param colIndex
     * @param errorMsg
     */
    public void setErrorMsg(String sheetName, int rowIndex, int colIndex, String errorMsg) {
        Sheet sheet = this.wb.getSheet(sheetName);
        if (errorStyle == null) {
            errorStyle = this.wb.createCellStyle();
            Font font = this.wb.createFont();
            font.setColor(Font.COLOR_RED);
            errorStyle.setFont(font);
        }
        Row row = sheet.getRow(rowIndex);
        if (row != null) {
            Cell cell = row.getCell(colIndex);
            if (cell == null) {
                cell = row.createCell(colIndex);
            }
            cell.setCellType(CellType.STRING);
            cell.setCellStyle(errorStyle);
            cell.setCellValue(errorMsg);
        }
    }

    public void save() {
        renderDimension();
        wbWrite();
    }


    /**
     * 重置此工作表中的所有单元格的行和列边界
     */
    public void renderDimension() {
        ExcelUtils.renderDimension(this.wb);
    }

    /**
     * 给out写流
     */
    private void wbWrite() {
        try {
            this.wb.write(outputStream);
            this.wb.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            log.error(e);
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception ex) {
            }
        }
    }

    /**
     * 清空单元格
     *
     * @param sheetName
     * @param rowIndex
     * @param colIndex
     */
    public void setResetCell(String sheetName, int rowIndex, int colIndex) {
        Sheet sheet = this.wb.getSheet(sheetName);
        Row row = sheet.getRow(rowIndex);
        if (row != null) {
            Cell cell = row.getCell(colIndex);
            if (cell != null) {
                cell = row.createCell(colIndex);
                row.removeCell(cell);
            }
        }
    }

    /**
     * 取得记录字段信息
     *
     * @return
     */
    public String[] getImportDataTableFieldsList() throws UnsupportedEncodingException {
        Sheet sheet = this.wb.getSheetAt(1);
        String val = sheet.getRow(0).getCell(0).getStringCellValue();
        byte[] str = Base64.decodeFast(val);
        return new String(str, Encoding.UTF8).split(",");
    }
}
