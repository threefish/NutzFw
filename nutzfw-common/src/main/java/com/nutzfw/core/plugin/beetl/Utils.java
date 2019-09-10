package com.nutzfw.core.plugin.beetl;

import org.nutz.lang.Strings;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2018/1/17  17:30
 * gt.registerFunctionPackage("fn",new Utils());
 */
public class Utils {

    /**
     * 用法 ${fn.match(2,'a,b,c')}返回 c
     *
     * @param key
     * @param vals
     * @return
     */
    public static String match(int key, String vals) {
        List<String> ss = Arrays.asList(vals.split(","));
        return ss.get(key) == null ? "" : ss.get(key);
    }


    /**
     * 用法 ${fn.match('a','a,b,c')} 返回 true
     *
     * @param key
     * @param vals
     * @return
     */
    public static boolean match(String key, String vals) {
        return Arrays.asList(vals.split(",")).contains(key);
    }


    /**
     * 女=0||男=1
     *
     * @param defaualtvalue
     * @param vals
     * @return
     */
    public static String str2SelectOption(String defaualtvalue, String vals) {
        List<String> strings = Arrays.asList(vals.split("\\|\\|"));
        StringBuilder sb = new StringBuilder();
        for (String line : strings) {
            String[] temps = line.split("=");
            sb.append(MessageFormat.format("<option value=\"{0}\" {1}>{2}</option>", temps[1], defaualtvalue.equals(temps[1]) ? "selected" : "", temps[0]));
        }
        return sb.toString();
    }


    /**
     * 用法 ${fn.fileFormat('1024')}返回
     *
     * @param size
     * @return
     */
    public static String fileFormat(String size) {
        return Strings.formatSizeForReadBy1024(Long.valueOf(Strings.sNull(size)));
    }
}
