package com.nutzfw.core.common.util;

import org.nutz.lang.Strings;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2018/1/9  15:56
 * 描述此类：
 */
public class StringUtil {
    /**
     * 异常转成字符串
     */
    public static String throwableToString(Throwable e) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(bao);
        e.printStackTrace(pw);
        pw.flush();
        return new String(bao.toByteArray());
    }

    /**
     * 是否为空
     */
    public static boolean isBlank(String str) {
        return Strings.isBlank(str);
    }

    /**
     * 解析数据
     *
     * @param line config.decrypt=true;config.decrypt.key=Mxxx==asd==asd;a=1
     * @return
     */
    private static HashMap<String, String> loadProperties(String line) {
        final String separator = ";";
        HashMap<String, String> data = new HashMap<>(0);
        do {
            if (line.startsWith(separator)) {
                line = line.substring(1);
            }
            int start = line.indexOf(separator);
            String tempLine = line;
            if (start > -1) {
                tempLine = line.substring(0, start);
                line = line.substring(start);
            } else {
                line = "";
            }
            int start2 = tempLine.indexOf("=");
            String key = tempLine.substring(0, start2);
            String value = tempLine.substring(start2 + 1);
            data.put(key, value);
        } while (!"".equals(line.trim()) && !separator.equals(line.trim()));
        return data;
    }

}
