/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.util.excel;

import com.nutzfw.core.common.util.excel.dto.PoiDto;
import org.nutz.dao.entity.Record;
import org.nutz.el.El;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者:黄川
 * 邮件:huchuc@vip.qq.com
 * 时间：2016-11-22 23:13
 */
public class ReportUtil {

    /**
     * 查询结果列别名前缀
     */
    public static final String ALIAS = "alias_";
    private static      Log    log   = Logs.get();

    public static PoiDto format(String viewtemplatetext) {
        String[] ss = viewtemplatetext.split("\\|");
        String key = ss[0].substring(2, ss[0].length());
        String json = ss[1].substring(0, ss[1].length() - 1);
        PoiDto dto = Json.fromJson(PoiDto.class, json);
        dto.setCtxKey(key);
        return dto;
    }

    /**
     * Excel column index begin 1
     *
     * @param colStr
     * @param length
     * @return
     */
    public static int excelColStrToNum(String colStr, int length) {
        int num = 0;
        int result = 0;
        for (int i = 0; i < length; i++) {
            char ch = colStr.charAt(length - i - 1);
            num = (int) (ch - 'A' + 1);
            num *= Math.pow(26, i);
            result += num;
        }
        return result;
    }

    /**
     * Excel column index begin 1
     *
     * @param columnIndex
     * @return
     */
    public static String excelColIndexToStr(int columnIndex) {
        if (columnIndex <= 0) {
            return null;
        }
        String columnStr = "";
        columnIndex--;
        do {
            if (columnStr.length() > 0) {
                columnIndex--;
            }
            columnStr = ((char) (columnIndex % 26 + (int) 'A')) + columnStr;
            columnIndex = (int) ((columnIndex - columnIndex % 26) / 26);
        } while (columnIndex > 0);
        return columnStr;
    }


    /**
     * 重新赋予列名
     *
     * @param num
     * @return
     */
    public static String randomColumn(int num) {
        if (num == 0) {
            throw new RuntimeException("num必须大于0");
        }
        return excelColIndexToStr(num);
    }


    /**
     * 创建可以导出的json数据
     *
     * @return
     */
    public static String create2010ExcelJsonDate(String excleJson) {
        NutMap data = new NutMap();
        NutMap spread = Json.fromJson(NutMap.class, renderExcleJson(excleJson));
        data.setv("spread", spread);
        data.setv("exportFileType", "xlsx");
        data.setv("exportFileName", "exportXlsx");
        data.setv("excel", new NutMap().setv("saveFlags", 0).setv("password", ""));
        return Json.toJson(data, JsonFormat.compact());
    }


    /**
     * 去除不符合spreadJs的Format格式
     *
     * @param excleJson
     * @return
     */
    public static String renderExcleJson(String excleJson) {
        NutMap json = Json.fromJson(NutMap.class, excleJson);
        return Json.toJson(renderExcleJson(json), JsonFormat.compact());
    }

    /**
     * 去除不符合spreadJs的Format格式
     *
     * @param spread
     * @return
     */
    public static NutMap renderExcleJson(NutMap spread) {
        List<NutMap> namedStyles = spread.getList("namedStyles", NutMap.class);
        if (namedStyles != null) {
            spread.put("namedStyles", removeFormatNamedStyles(namedStyles));
        }
        return spread;
    }

    /**
     * 去除不符合spreadJs的Format格式防止报错
     *
     * @param namedStyles
     * @return
     */
    public static List<NutMap> removeFormatNamedStyles(List<NutMap> namedStyles) {
        for (int i = 0; i < namedStyles.size(); i++) {
            NutMap namedStyle = namedStyles.get(i);
            String formatter = namedStyle.getString("formatter", "");
            if (Strings.isNotBlank(formatter)) {
                namedStyle.put("formatter", "");
                namedStyles.set(i, namedStyle);
            }
        }
        return namedStyles;
    }

    /**
     * 格式化结果
     *
     * @param viewData
     * @param templatetext
     * @return
     */
    public static String getCellValue(Map<String, String> viewData, String templatetext) {
        StringBuffer sb = new StringBuffer("");
        if (Strings.isEmpty(templatetext)) {
            for (Map.Entry<String, String> entry : viewData.entrySet()) {
                sb.append(entry.getValue());
            }
        } else if (viewData.size() != 0) {
            int i = 1;
            for (Map.Entry<String, String> entry : viewData.entrySet()) {
                templatetext = templatetext.replaceAll("\\{" + i + "\\}", entry.getValue());
                i++;
            }
            sb.append(templatetext);
        }
        return sb.toString();
    }


    /**
     * 格式化结果
     *
     * @param record
     * @param templatetext
     * @return
     */
    public static String getCellValue(Record record, String templatetext) {
        Map<String, String> viewData = new HashMap<>(1);
        for (Map.Entry entry : record.entrySet()) {
            String key = String.valueOf(entry.getKey());
            String val = String.valueOf(entry.getValue());
            viewData.put(key, val);
        }
        return getCellValue(viewData, templatetext);
    }

    /**
     * 格式化结果
     *
     * @param record
     * @param templatetext
     * @return
     */
    public static String getCellValue(NutMap record, String templatetext) {
        Map<String, String> viewData = new HashMap<>(record.size());
        for (Map.Entry entry : record.entrySet()) {
            String key = String.valueOf(entry.getKey());
            String val = String.valueOf(entry.getValue());
            viewData.put(key, val);
        }
        return getCellValue(viewData, templatetext);
    }

    /**
     * 格式化结果
     *
     * @param templatetext
     * @return
     */
    public static String formatFuncVal(String templatetext) {
        try {
            CharSegment msg = new CharSegment(templatetext);
            Map<String, El> els = new HashMap<>(1);
            if (msg.hasKey()) {
                els = new HashMap(1);
                for (String key : msg.keys()) {
                    els.put(key, new El(key));
                }
            }
            Context ctx = Lang.context();
            ctx.clear();
            for (String key : msg.keys()) {
                ctx.set(key, els.get(key).eval(ctx));
            }
            return msg.render(ctx).toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            return "函数执行异常：" + e.getMessage();
        }
    }

    /**
     * 序列化为文件
     *
     * @param object
     * @param path
     */
    public static void writeObjectToFile(Object object, Path path) {
        try (FileOutputStream out = new FileOutputStream(path.toFile()); ObjectOutputStream objOut = new ObjectOutputStream(out);) {
            objOut.writeObject(object);
            objOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 反序列化为对象
     *
     * @param path
     */
    public static <T> List<T> readObjectFromFile(Path path) {
        List<T> object = null;
        try (FileInputStream in = new FileInputStream(path.toFile()); ObjectInputStream objIn = new ObjectInputStream(in)) {
            object = (List<T>) objIn.readObject();
            objIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return object;
    }

}
