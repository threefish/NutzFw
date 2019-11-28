/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.util;

import org.nutz.lang.segment.CharSegment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author threefish huchuc@vip.qq.com
 */
public class RegexUtil {


    public static final String PHONE_REG_STR = "^((13[0-9])|(14[0-9])|(15[0-9])|(16[0-9])|(17[0-9])|(18[0-9]))\\d{8}$";
    static final Pattern ACCOUN_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+", Pattern.MULTILINE + Pattern.DOTALL);
    static final Pattern PHONE_REG = Pattern.compile(PHONE_REG_STR);
    static final Pattern IS_A_Z_PATTERN = Pattern.compile("^[A-Za-z]+$");
    static final Pattern IS_A_Z_PATTERN2 = Pattern.compile("^[a-z]+$");
    static final Pattern IS_A_Z_PATTERN3 = Pattern.compile("^[A-Z]+$");
    static final Pattern IS_EMPTY_PATTERN = Pattern.compile("^\\S+$");

    /**
     * 判断是否是邮政编码
     *
     * @param str
     * @return
     */
    public static boolean isAccount(String str) {
        Matcher matcher = ACCOUN_PATTERN.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是邮政编码
     *
     * @param str
     * @return
     */
    public static boolean isPOST(String str) {
        String regEx = "[1-9]\\d{5}(?!\\d)";
        Pattern pattern = Pattern.compile(regEx, Pattern.MULTILINE + Pattern.DOTALL);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是自定义表达式
     *
     * @param str
     * @return
     */
    public static boolean isEspressione(String str) {
        String regEx = "^\\#\\{.*?}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是自定义表达式
     *
     * @param str
     * @return
     */
    public static boolean isEl(String str) {
        return new CharSegment(str).hasKey();
    }

    /**
     * 判断是否是整数
     *
     * @param str
     * @return
     */
    public static boolean isNegativeInteger(String str) {
        String regEx = "^-[1-9]\\d*$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是正整数
     *
     * @param str
     * @return
     */
    public static boolean isPositiveInteger(String str) {
        String regEx = "^[1-9]\\d*$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是负数
     *
     * @param str
     * @return
     */
    public static boolean isNegativeNumber(String str) {
        String regEx = "^-[1-9]\\d*|0$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是正数
     *
     * @param str
     * @return
     */
    public static boolean isPositiveNumber(String str) {
        String regEx = "^[1-9]\\d*|0$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNum(String str) {
        String regEx = "^([+-]?)\\d*\\.?\\d+$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 正则表达式校验最多两位小数的实数
     *
     * @param str
     * @return
     */
    public static boolean isNumAndTwodecimals(String str) {
        String regEx = "^(([0-9]*)|(([0]\\.\\d{0,2}|[0-9]*\\.\\d{0,2})))$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();

    }

    /**
     * 判断是否是整数(负数,零,正数)
     *
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        String regEx = "^-?([0-9]\\d*)$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是不是整数和小数
     *
     * @param str
     * @return
     */
    public static boolean isIntOrFloat(String str) {
        String regEx = "[+]?\\d+(\\.\\d+)?$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是正浮点数类型
     *
     * @param str
     * @return
     */
    public static boolean isPositiveFloat(String str) {
        String regEx = "^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是负浮点数类型
     *
     * @param str
     * @return
     */
    public static boolean isNegativeFloat(String str) {
        String regEx = "^-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是浮点数类型
     *
     * @param str
     * @return
     */
    public static boolean isFloat(String str) {
        String regEx = "^-?([1-9]\\d*|0(?!\\.0+$))\\.\\d+?$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是非负浮点数类型（正浮点数大于等于10的数字且包含0）
     *
     * @param str
     * @return
     */
    public static boolean isNonNegativeFloat(String str) {
        String regEx = "^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是非正浮点数类型（正浮点数大于等于-10的数字且包含0）
     *
     * @param str
     * @return
     */
    public static boolean isNonPositiveFloat(String str) {
        String regEx = "^(-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*))|0?.0+|0$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是非正浮点数类型（正浮点数大于等于-10的数字且包含0）
     *
     * @param str
     * @return
     */
    public static boolean isACSII(String str) {
        String regEx = "^[\\x00-\\xFF]+$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是字母
     *
     * @param str
     * @return
     */
    public static boolean isAz(String str) {
        return IS_A_Z_PATTERN.matcher(str).matches();
    }

    /**
     * 判断是否是小写字母
     *
     * @param str
     * @return
     */
    public static boolean isaz(String str) {
        return IS_A_Z_PATTERN2.matcher(str).matches();
    }

    /**
     * 判断是否是大写字母
     *
     * @param str
     * @return
     */
    public static boolean isAZ(String str) {
        return IS_A_Z_PATTERN3.matcher(str).matches();
    }

    /**
     * 判断是否是非空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return IS_EMPTY_PATTERN.matcher(str).matches();
    }

    /**
     * 判断是否是是中文
     *
     * @param str
     * @return
     */
    public static boolean isChinese(String str) {
        String regEx = "^[\\u4e00-\\u9fa5]+$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是是色值
     *
     * @param str
     * @return
     */
    public static boolean isColor(String str) {
        String regEx = "^#[a-fA-F0-9]{6}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是日期
     *
     * @param str
     * @return
     */
    public static boolean isDate(String str) {
        String regEx = "^\\d{4}(\\-|\\/|.)\\d{1,2}\\1\\d{1,2}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是手机号码
     *
     * @param str
     * @return
     */
    public static boolean isPhone(String str) {
        Matcher matcher = PHONE_REG.matcher(str);
        return matcher.matches();
    }


    /**
     * 判断是不是座机
     *
     * @param str
     * @return
     */
    public static boolean isTel(String str) {
        String regEx = "^((0\\d{2,3})-)(\\d{7,8})(-(\\d{3,}))?$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是邮箱
     *
     * @param str
     * @return
     */
    public static boolean isEmail(String str) {
        String regEx = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是QQ
     *
     * @param str
     * @return
     */
    public static boolean isQQ(String str) {
        String regEx = "^[1-9]*[1-9][0-9]*$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是身份证号码
     *
     * @param str
     * @return
     */
    public static boolean isIdCard(String str) {
        String regEx = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是外籍分配身份证号码
     *
     * @param str
     * @return
     */
    public static boolean isFIdCard(String str) {
        String regEx = "^F\\S{16}F$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是IP地址
     *
     * @param str
     * @return
     */
    public static boolean isIP(String str) {
        String regEx = "((?:(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d)\\.){3}(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d))";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }


    /**
     * 判断是否是图片
     *
     * @param str
     * @return
     */
    public static boolean isPIC(String str) {
        String regEx = "(.*)\\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是压缩文件
     *
     * @param str
     * @return
     */
    public static boolean isRAR(String str) {
        String regEx = "(.*)\\.(rar|zip|7zip|7z|tgz|gz)$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是网址
     *
     * @param str
     * @return
     */
    public static boolean isHTTP(String str) {
        String regEx = "[a-zA-z]+:\\/\\/[^\\s]+";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }


    /**
     * 判断是否是网址
     *
     * @param str
     * @return
     */
    public static boolean isFTP(String str) {
        String regEx = "ftp\\:\\/\\/[^:]*:@([^\\/]*)";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是字母和数字混合(只有字母或数字也可以)
     *
     * @param str
     * @return
     */
    public static boolean isNumAndLetter(String str) {
        String regEx = "^[\\d|A-Za-z]+$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否是汉字、字母、数字的混合
     *
     * @param str
     * @return
     */
    public static boolean isChineseAzNum(String str) {
        String regEx = "^[\\u4e00-\\u9fa5a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}
