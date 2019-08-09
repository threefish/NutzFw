package com.nutzfw.core.common.util;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtil {
    public static final String ENG_DATE_FROMAT = "EEE, d MMM yyyy HH:mm:ss z";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM_SS2 = "yyyy-MM-dd HH-mm-ss";
    public static final String YYYY_MM_DD_HH_MM_SS_ZH = "yyyy年MM月dd日 HH时mm分ss秒";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYY_MM_DD_ZH = "yyyy年MM月dd日";
    public static final String YYYY_MM = "yyyy-MM";
    public static final String YYYY_MM_ZH = "yyyy年MM月";
    public static final String MM_DD = "MM-dd";
    public static final String MM_DD_ZH = "MM月dd日";
    public static final String YYYY = "yyyy";
    public static final String HH_MM_SS = "HH:mm:ss";
    public static final String HH_MM_SS_ZH = "HH时mm分ss秒";
    public static final String MM = "MM";
    public static final String DD = "dd";
    public static final String YYYY_MM_DD_HH_MM_ZH = "yyyy年MM月dd日  HH:mm";
    public static final String YYYY_MM_DD_HH_MM_ZH2 = "yyyyMMddHHmmss";
    public static final long DAYTIME = 1000 * 60 * 60 * 24;

    /**
     * 拆分前台传递的日期分段
     *
     * @param dateRange
     * @param type      2年月日 年月4  年5
     * @return
     */
    public static String[] getDateRange(String dateRange, int type) throws ParseException {
        dateRange = dateRange.replaceAll("年", "-")
                .replaceAll("月", "-")
                .replaceAll("日", "")
                .replaceAll("时", ":")
                .replaceAll("分", ":")
                .replaceAll("秒", "");
        String[] vals = dateRange.split("至");
        String[] date = new String[2];
        if (type == 2) {
            date[0] = vals[0].trim() + " 00:00:00";
            date[1] = vals[1].trim() + " 23:59:59";
        } else if (type == 4) {
            date[0] = vals[0].trim() + "01 00:00:00";
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date lastDate = df.parse(vals[1].trim() + "01");
            Calendar ca = Calendar.getInstance();
            ca.setTime(lastDate);
            ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
            String last = df.format(ca.getTime());
            date[1] = last + " 23:59:59";
        } else if (type == 5) {
            date[0] = vals[0].trim() + "01-01 00:00:00";
            date[1] = vals[1].trim() + "12-31 23:59:59";
        }
        return date;
    }

    /**
     * 拆分前台传递的日期分段
     *
     * @param dateRange
     * @return
     */
    public static String[] getDateRange(String dateRange) {
        dateRange = dateRange.replaceAll("年", "-")
                .replaceAll("月", "-")
                .replaceAll("日", "")
                .replaceAll("时", ":")
                .replaceAll("分", ":")
                .replaceAll("秒", "");
        String[] vals = dateRange.split("至");
        String[] date = new String[2];
        date[0] = vals[0].trim() + " 00:00:00";
        date[1] = vals[1].trim() + " 23:59:59";
        return date;
    }

    /**
     * 日期转换
     *
     * @param pattern
     * @return
     */
    public static String getDate(String pattern) {
        return DateFormatUtils.format(new Date(), pattern);
    }

    /**
     * 将timestamp转换成date
     *
     * @param tt
     * @return
     */
    public static Date timestampToDate(Timestamp tt) {
        return new Date(tt.getTime());
    }

    /**
     * 比较两个时间的大小
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean befor(Date a, Date b) {
        return a.before(b);
    }

    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1, Date date2) {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
        return days;
    }

    /**
     * @param date
     * @描述 —— 格式化日期对象
     */
    public static Date date2date(Date date, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        String str = sdf.format(date);
        try {
            date = sdf.parse(str);
        } catch (Exception e) {
            return null;
        }
        return date;
    }

    /**
     * @param tt
     * @描述 —— Timestamp格式化日期对象
     */
    public static String timestamp2String(Timestamp tt, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        String str = sdf.format(tt);
        return str;
    }

    /**
     * @param date
     * @描述 —— 时间对象转换成字符串
     */
    public static String date2string(Date date, String formatStr) {
        String strDate = "";
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        if (date != null) {
            strDate = sdf.format(date);
        }
        return strDate;
    }

    /**
     * @param timestamp
     * @描述 —— sql时间对象转换成字符串
     */
    public static String timestamp2string(Timestamp timestamp, String formatStr) {
        String strDate = "";
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        strDate = sdf.format(timestamp);
        return strDate;
    }

    /**
     * @param dateString
     * @param formatStr
     * @描述 —— 字符串转换成时间对象
     */
    public static Date string2date(String dateString, String formatStr) {
        Date formateDate = null;
        DateFormat format = new SimpleDateFormat(formatStr);
        try {
            formateDate = format.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
        return formateDate;
    }

    /**
     * @param dateString
     * @param formatStr
     * @描述 —— 字符串转换成时间对象
     */
    public static Date string2date2(String dateString, String formatStr) throws ParseException {
        DateFormat format = new SimpleDateFormat(formatStr);
        Date formateDate = format.parse(dateString);
        return formateDate;
    }

    /**
     * @param dateString
     * @param formatStr
     * @描述 —— 字符串转换成时间对象
     */
    public static Date stringExcleDate2Date(String dateString, String formatStr) throws ParseException {
        dateString = dateString.replaceAll("/", "-");
        dateString = dateString.replaceAll("年", "-");
        dateString = dateString.replaceAll("月", "-");
        dateString = dateString.replaceAll("", "-");


        DateFormat format = new SimpleDateFormat(formatStr);
        Date formateDate = format.parse(dateString);
        return formateDate;
    }


    /**
     * 字符串日期格式化
     *
     * @param dateString
     * @param formatStr
     * @return
     */
    public static String string2string(String dateString, String formatStr) {
        SimpleDateFormat sdfs = new SimpleDateFormat(YYYY_MM_DD);
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        String strDate = "";
        try {
            strDate = sdf.format(sdfs.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strDate;
    }

    /**
     * 字符串日期格式化
     *
     * @param dateString
     * @param formatStr
     * @return
     */
    public static String string2string2(String dateString, String formatStr) {
        SimpleDateFormat sdfs = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        String strDate = "";
        try {
            strDate = sdf.format(sdfs.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strDate;
    }


    /**
     * @param date
     * @描述 —— Date类型转换为Timestamp类型
     */
    public static Timestamp date2timestamp(Date date) {
        if (date == null) {
            return null;
        }
        return new Timestamp(date.getTime());
    }

    /**
     * 获取本月开始日期和今天结束日期
     *
     * @return
     */
    public static Date[] getNowMonth() {
        return getMonthStartAndEnd(new Date());
    }

    /**
     * 获取指定月开始日期和今天结束日期
     *
     * @return
     */
    public static Date[] getMonthStartAndEnd(Date now) {
        String date1 = getFullYear(now) + "-" + getMonth(now) + "-01 00:00:00";
        String date2 = getFullYear(now) + "-" + getMonth(now) + "-01 23:59:59";
        Date[] dates = new Date[2];
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(string2date(date2, YYYY_MM_DD_HH_MM_SS));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));
        dates[0] = string2date(date1, YYYY_MM_DD_HH_MM_SS);
        dates[1] = calendar.getTime();
        return dates;
    }

    /**
     * 获取近期num个月日期
     * num = -1;  是近三月
     *
     * @return
     */
    public static Date[] getNumMonth(int num) {
        Calendar clnow = Calendar.getInstance();
        Date now = new Date();
        String date1 = getFullYear(now) + "-" + (clnow.get(Calendar.MONTH) + num) + "-01 00:00:00";
        String date2 = getFullYear(now) + "-" + getMonth(now) + "-" + getDay(now) + " 23:59:59";
        Date[] dates = new Date[2];
        dates[0] = string2date(date1, YYYY_MM_DD_HH_MM_SS);
        dates[1] = string2date(date2, YYYY_MM_DD_HH_MM_SS);
        return dates;
    }


    /**
     * 获取上num个年度
     * num = -1;
     *
     * @return
     */
    public static Date[] getNumYear(int num) {
        String date1 = getFullYear(num) + "-01-01 00:00:00";
        String date2 = getFullYear(num) + "-12-31 23:59:59";
        Date[] dates = new Date[2];
        dates[0] = string2date(date1, YYYY_MM_DD_HH_MM_SS);
        dates[1] = string2date(date2, YYYY_MM_DD_HH_MM_SS);
        return dates;
    }

    /**
     * 获取上num个年度
     * num = -1;
     *
     * @return
     */
    public static Date getNumHour(int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - num);
        return calendar.getTime();
    }

    /**
     * 获取最近一周
     *
     * @return
     */
    public static Date[] getWeek(int num) {
        Date[] dates = new Date[2];
        dates[1] = new Date();
        Calendar cl = Calendar.getInstance();
        cl.setTime(dates[1]);
        cl.add(Calendar.WEEK_OF_YEAR, -num);
        dates[0] = cl.getTime();
        dates[0] = date2date(dates[0], YYYY_MM_DD_HH_MM_SS);
        dates[1] = date2date(dates[1], YYYY_MM_DD_HH_MM_SS);
        return dates;
    }

    /**
     * 获取最近几个月
     *
     * @param num
     * @return
     */
    public static Date[] getLastNumMonth(int num) {
        Date[] dates = new Date[2];
        dates[1] = new Date();
        Calendar cl = Calendar.getInstance();
        cl.setTime(dates[1]);
        //月
        cl.add(Calendar.MONTH, -num);
        dates[0] = cl.getTime();
        dates[0] = date2date(dates[0], YYYY_MM_DD_HH_MM_SS);
        dates[1] = date2date(dates[1], YYYY_MM_DD_HH_MM_SS);
        return dates;
    }


    /**
     * 年
     *
     * @param date
     * @return
     */
    public static String getFullYear(Date date) {
        return new SimpleDateFormat("yyyy").format(date);
    }

    /**
     * 年
     *
     * @param date
     * @return
     */
    public static int getIntFullYear(Date date) {
        return Integer.parseInt(getFullYear(date));
    }

    /**
     * 取得当前年往前、往后的年
     *
     * @param num
     * @return
     */
    public static String getFullYear(int num) {
        Calendar now = Calendar.getInstance();
        return String.valueOf(now.get(Calendar.YEAR) + num);
    }


    /**
     * 月
     *
     * @param date
     * @return
     */
    public static String getMonth(Date date) {
        return new SimpleDateFormat("MM").format(date);
    }

    /**
     * 获取当前月的上一月
     *
     * @param date
     * @return
     */
    public static int getBeforeMonth(Date date) {
        return Integer.valueOf(getMonth(date), 10) - 1;
    }

    /**
     * 日
     *
     * @param date
     * @return
     */
    public static String getDay(Date date) {
        return new SimpleDateFormat("dd").format(date);
    }

    /**
     * 天
     *
     * @param date
     * @return
     */
    public static String getHour(Date date) {
        return new SimpleDateFormat("HH").format(date);
    }

    /**
     * 分
     *
     * @param date
     * @return
     */
    public static String getMinute(Date date) {
        return new SimpleDateFormat("mm").format(date);
    }

    /**
     * 秒
     *
     * @param date
     * @return
     */
    public static String getSecond(Date date) {
        return new SimpleDateFormat("ss").format(date);
    }

    /**
     * @param time
     * @描述 —— 指定时间距离当前时间的中文信息
     */
    public static String getLnow(long time) {
        Calendar cal = Calendar.getInstance();
        long timel = cal.getTimeInMillis() - time;
        if (timel / 1000 < 60) {
            return "1分钟以内";
        } else if (timel / 1000 / 60 < 60) {
            return timel / 1000 / 60 + "分钟前";
        } else if (timel / 1000 / 60 / 60 < 24) {
            return timel / 1000 / 60 / 60 + "小时前";
        } else {
            return timel / 1000 / 60 / 60 / 24 + "天前";
        }
    }

    /**
     * 根据出生年月日
     *
     * @param dateOfBirth
     * @return
     */
    public static int getAge(Date dateOfBirth) {
        int age = 0;
        Calendar born = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        if (dateOfBirth != null) {
            now.setTime(new Date());
            born.setTime(dateOfBirth);
            if (born.after(now)) {
                throw new IllegalArgumentException("年龄不能超过当前日期");
            }
            age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
            int nowDayOfYear = now.get(Calendar.DAY_OF_YEAR);
            int bornDayOfYear = born.get(Calendar.DAY_OF_YEAR);
            System.out.println("nowDayOfYear:" + nowDayOfYear + " bornDayOfYear:" + bornDayOfYear);
            if (nowDayOfYear < bornDayOfYear) {
                age -= 1;
            }
        }
        return age;
    }


    /**
     * 根据身份证号码获取年龄
     *
     * @param idCardnum 身份证
     * @return 年龄
     */
    public static int getAge(String idCardnum) {
        int year, month, day, idLength = idCardnum.length();
        Calendar cal1 = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        if (idLength == 18) {
            year = Integer.parseInt(idCardnum.substring(6, 10));
            month = Integer.parseInt(idCardnum.substring(10, 12));
            day = Integer.parseInt(idCardnum.substring(12, 14));
        } else if (idLength == 15) {
            year = Integer.parseInt(idCardnum.substring(6, 8)) + 1900;
            month = Integer.parseInt(idCardnum.substring(8, 10));
            day = Integer.parseInt(idCardnum.substring(10, 12));
        } else {
            System.out.println("This ID card number is invalid!");
            return -1;
        }
        cal1.set(year, month, day);
        return getYearDiff(today, cal1);
    }

    private static int getYearDiff(Calendar cal, Calendar cal1) {
        int m = (cal.get(Calendar.MONTH)) - (cal1.get(Calendar.MONTH));
        int y = (cal.get(Calendar.YEAR)) - (cal1.get(Calendar.YEAR));
        return (y * 12 + m) / 12;
    }

    /**
     * 获得指定日期的前一天
     *
     * @param specifiedDay
     * @return
     * @throws Exception
     */
    public static String getSpecifiedDayBefore(String specifiedDay) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - 1);

        String dayBefore = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).format(c.getTime());
        return dayBefore;
    }


    /**
     * @param start
     * @param end
     * @param format DateUtil.getDistanceTime(start,end,"{D}天{H}小时{M}分{S}秒{MS}毫秒")
     * @return
     */
    public static String getDistanceTime(long start, long end, String format) {
        long diff;
        if (start < end) {
            diff = end - start;
        } else {
            diff = start - end;
        }
        long day = diff / (24 * 60 * 60 * 1000);
        long hour = (diff / (60 * 60 * 1000) - day * 24);
        long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long ms = (diff - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - sec * 1000);
        format = format.replaceAll("\\{D\\}", String.valueOf(day));
        format = format.replaceAll("\\{H\\}", String.valueOf(hour));
        format = format.replaceAll("\\{M\\}", String.valueOf(min));
        format = format.replaceAll("\\{S\\}", String.valueOf(sec));
        format = format.replaceAll("\\{MS\\}", String.valueOf(ms));
        return format;
    }

    /**
     * @param time
     * @return
     */
    public static String getDistanceTime(long time) {
        long day = time / (24 * 60 * 60 * 1000);
        long hour = (time / (60 * 60 * 1000) - day * 24);
        long min = ((time / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long sec = (time / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        StringBuilder sb = new StringBuilder();
        if (day > 0){
            sb.append(day).append("天");
        }
        if (hour > 0){
            sb.append(hour).append("时");
        }
        if (min > 0){
            sb.append(min).append("分");
        }
        if (sec > 0){
            sb.append(sec).append("秒");
        }
        return sb.toString();
    }


    /**
     * 时间差得到 年-月-日
     *
     * @param start
     * @param end
     * @param format {Y}年{M}月{D}日
     * @return
     */
    public static String getDistanceDate(long start, long end, String format) {
        Calendar endC = Calendar.getInstance();
        endC.setTimeInMillis(end);
        Calendar startC = Calendar.getInstance();
        startC.setTimeInMillis(start);
        int day = endC.get(Calendar.DAY_OF_MONTH) - startC.get(Calendar.DAY_OF_MONTH);
        int month = endC.get(Calendar.MONTH) - startC.get(Calendar.MONTH);
        int year = endC.get(Calendar.YEAR) - startC.get(Calendar.YEAR);
        if (day < 0) {
            month -= 1;
            endC.add(Calendar.MONTH, -1);
            day = day + endC.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        if (month < 0) {
            month = (month + 12) % 12;
            year--;
        }
        format = format.replaceAll("\\{Y\\}", String.valueOf(year));
        format = format.replaceAll("\\{M\\}", String.valueOf(month));
        format = format.replaceAll("\\{D\\}", String.valueOf(day));
        return format;
    }


    /**
     * 获取指定时间的上一个月第一天和最后一天的日期
     *
     * @param date 指定日期
     * @return Map(key : first ： 第一天 ； last ： 最后一天 ）
     */
    public static Map<String, String> getFirstdayLastdayMonth(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(YYYY_MM_DD);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        Date theDate = calendar.getTime();

        //上个月第一天
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        String dayFirst = df.format(gcLast.getTime());
        StringBuffer str = new StringBuffer().append(dayFirst);
        dayFirst = str.toString();
        dayFirst = dayFirst + " 00:00:00";
        //上个月最后一天
        //加一个月
        calendar.add(Calendar.MONTH, 1);
        //设置为该月第一天
        calendar.set(Calendar.DATE, 1);
        //再减一天即为上个月最后一天
        calendar.add(Calendar.DATE, -1);
        String dayLast = df.format(calendar.getTime());
        StringBuffer endStr = new StringBuffer().append(dayLast);
        dayLast = endStr.toString();
        dayLast = dayLast + " 23:59:59";
        Map<String, String> map = new HashMap(2);
        map.put("first", dayFirst);
        map.put("last", dayLast);
        return map;
    }

    /**
     * 获取指定时间月份第一天和最后一天的日期
     *
     * @param date 指定日期
     * @return Map(key : first ： 第一天 ； last ： 最后一天 ）
     */
    public static Map<String, String> getFirstdayAndLastday(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date lastDayOfMonth = calendar.getTime();
        Map<String, String> map = new HashMap(2);
        map.put("first", df.format(firstDayOfMonth));
        map.put("last", df.format(lastDayOfMonth));
        return map;
    }

    /**
     * 根据指定日期获取星期
     *
     * @param date
     * @return 1到7
     * @throws Exception
     */
    public static int dayForWeekforInt(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayForWeek;
    }

    /**
     * 根据指定日期获取星期
     *
     * @param date
     * @return 周几
     * @throws Exception
     */
    public static String dayForWeekforStr(Date date) {
        int dayForWeek = dayForWeekforInt(date);
        String weekStr;
        switch (dayForWeek) {
            case 1:
                weekStr = "周一";
                break;
            case 2:
                weekStr = "周二";
                break;
            case 3:
                weekStr = "周三";
                break;
            case 4:
                weekStr = "周四";
                break;
            case 5:
                weekStr = "周五";
                break;
            case 6:
                weekStr = "周六";
                break;
            default:
                weekStr = "周日";
                break;
        }
        return weekStr;
    }

    /**
     * 判断两个日期是否为同一天
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                .get(Calendar.YEAR);
        boolean isSameMonth = isSameYear
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                .get(Calendar.DAY_OF_MONTH);

        return isSameDate;
    }


    //计算两个日期相差年数
    public static int yearDateDiff(Date startDate, Date endDate) {
        //获取日历实例
        Calendar calBegin = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        //字符串按照指定格式转化为日期
        calBegin.setTime(startDate);
        calEnd.setTime(endDate);
        return calEnd.get(Calendar.YEAR) - calBegin.get(Calendar.YEAR);
    }


    /**
     * 处理时间
     *
     * @param date
     * @return
     */
    public static Object renderStrDate(Object date, String format) {
        if (date == null) {
            return "";
        }
        if (date instanceof Timestamp) {
            return DateUtil.timestamp2string((Timestamp) date, format);
        }
        if (date instanceof Date) {
            return DateUtil.date2string((Date) date, format);
        }
        return date;
    }

    /**
     * 得到两个日期之间间隔的秒数
     *
     * @param d1
     * @param d2
     * @return
     */
    public static long getSecondsBetweenDates(Date d1, Date d2) {
        return Math.abs((d1.getTime() - d2.getTime()) / 1000);
    }

    /**
     * 去年今天至今天
     *
     * @return
     */
    public static Calendar[] getLastYearTodayToToday() {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.add(Calendar.YEAR, -1);
        return new Calendar[]{startDate, endDate};
    }

    /**
     * 今年1月1日至今
     * As of January 1 of the previous month so far
     *
     * @return
     */
    public static Calendar[] getFromJanuary1ThisYearTillNow() {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        //上月1月1日至今
        startDate.add(Calendar.MONTH, -startDate.get(Calendar.MONTH));
        //设置日历中月份的最小天数
        startDate.set(Calendar.DAY_OF_MONTH, startDate.getActualMinimum(Calendar.DAY_OF_MONTH));
        return new Calendar[]{startDate, endDate};
    }

    /**
     * 去年1月1日至12月31日
     *
     * @return
     */
    public static Calendar[] getLastYear() {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.add(Calendar.YEAR, -1);
        startDate.add(Calendar.MONTH, -startDate.get(Calendar.MONTH));
        //设置日历中月份的最小天数
        startDate.set(Calendar.DAY_OF_MONTH, startDate.getActualMinimum(Calendar.DAY_OF_MONTH));
        endDate.add(Calendar.YEAR, -1);
        endDate.add(Calendar.MONTH, -endDate.get(Calendar.MONTH) + 11);
        //设置日历中月份的最大天数
        endDate.set(Calendar.DAY_OF_MONTH, startDate.getMaximum(Calendar.DAY_OF_MONTH));
        return new Calendar[]{startDate, endDate};
    }

    /**
     * 近3年（1月1日至12月31日）
     *
     * @return
     */
    public static Calendar[] getNearly3Years() {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        //近3年（1月1日至12月31日）
        startDate.add(Calendar.YEAR, -3);
        startDate.add(Calendar.MONTH, -startDate.get(Calendar.MONTH));
        //设置日历中月份的最小天数
        startDate.set(Calendar.DAY_OF_MONTH, startDate.getActualMinimum(Calendar.DAY_OF_MONTH));
        endDate.add(Calendar.YEAR, -1);
        endDate.add(Calendar.MONTH, -endDate.get(Calendar.MONTH) + 11);
        //设置日历中月份的最大天数
        endDate.set(Calendar.DAY_OF_MONTH, startDate.getMaximum(Calendar.DAY_OF_MONTH));
        return new Calendar[]{startDate, endDate};
    }

    /**
     * 上月今天至今
     *
     * @return
     */
    public static Calendar[] getLastMonthToday() {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);
        return new Calendar[]{startDate, endDate};
    }

    /**
     * 上月1月1日至今
     * As of January 1 of the previous month so far
     *
     * @return
     */
    public static Calendar[] getAsOfJanuary1OfThePreviousMonthSoFar() {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        //上月1月1日至今
        startDate.add(Calendar.MONTH, -1);
        //设置日历中月份的最小天数
        startDate.set(Calendar.DAY_OF_MONTH, startDate.getActualMinimum(Calendar.DAY_OF_MONTH));
        return new Calendar[]{startDate, endDate};
    }

    /**
     * 上月1月1日至31日
     *
     * @return
     */
    public static Calendar[] getLastMonth1To31() {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        //设置月份
        startDate.add(Calendar.MONTH, -1);
        endDate.add(Calendar.MONTH, -1);
        //设置日历中月份的最小天数
        startDate.set(Calendar.DAY_OF_MONTH, startDate.getActualMinimum(Calendar.DAY_OF_MONTH));
        //设置日历中月份的最大天数
        endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        return new Calendar[]{startDate, endDate};
    }

    /**
     * 近3月（1日至31日）
     *
     * @return
     */
    public static Calendar[] getLast3Month1To31() {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        //设置月份
        startDate.add(Calendar.MONTH, -2);
        //设置日历中月份的最小天数
        startDate.set(Calendar.DAY_OF_MONTH, startDate.getActualMinimum(Calendar.DAY_OF_MONTH));
        //设置日历中月份的最大天数
        endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
        return new Calendar[]{startDate, endDate};
    }

    /**
     * 上周周一至周日
     *
     * @return
     */
    public static Calendar[] getLastWeek() {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_WEEK, -1);
        endDate.add(Calendar.DAY_OF_WEEK, -1);
        int d = 0;
        //上周周一至周日
        if (startDate.get(Calendar.DAY_OF_WEEK) == 1) {
            d = -6;
        } else {
            d = 2 - startDate.get(Calendar.DAY_OF_WEEK);
        }
        //设置日历中周的起始位置
        startDate.add(Calendar.DAY_OF_WEEK, d);
        //设置日历中周的结束位置
        endDate.add(Calendar.DAY_OF_WEEK, d);
        endDate.add(Calendar.DAY_OF_WEEK, 6);
        return new Calendar[]{startDate, endDate};
    }


    /**
     * 上周周一至周日
     *
     * @return
     */
    public static Calendar[] getLastWeekToToday() {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        int d = 0;
        if (startDate.get(Calendar.DAY_OF_WEEK) == 1) {
            d = -6;
        } else {
            d = 2 - startDate.get(Calendar.DAY_OF_WEEK);
        }
        //设置日历中周的起始位置
        startDate.add(Calendar.DAY_OF_WEEK, d);
        return new Calendar[]{startDate, endDate};
    }

    /**
     * 添加月
     *
     * @param m
     * @return
     */
    public static Date addMonths(Date date, int m) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.add(Calendar.MONTH, m);
        return now.getTime();
    }

    /**
     * 从今天起添加或者减少n年
     *
     * @param y
     * @return
     */
    public static Date[] addYears(int y) {
        Date dates[] = new Date[2];
        Calendar now = Calendar.getInstance();
        if (y >= 0) {
            dates[0] = now.getTime();
            now.add(Calendar.YEAR, y);
            dates[1] = now.getTime();
        } else {
            dates[1] = now.getTime();
            now.add(Calendar.YEAR, y);
            dates[0] = now.getTime();
        }
        return dates;
    }

    /**
     * 从今天起添加或者减少n月
     *
     * @param m
     * @return
     */
    public static Date[] addMonths(int m) {
        Date dates[] = new Date[2];
        Calendar now = Calendar.getInstance();
        if (m >= 0) {
            dates[0] = now.getTime();
            now.add(Calendar.MONTH, m);
            dates[1] = now.getTime();
        } else {
            dates[1] = now.getTime();
            now.add(Calendar.MONTH, m);
            dates[0] = now.getTime();
        }
        return dates;
    }

    /**
     * 从今天起添加或者减少天
     *
     * @param d
     * @return
     */
    public static Date[] addDays(int d) {
        Date dates[] = new Date[2];
        Calendar now = Calendar.getInstance();
        if (d >= 0) {
            dates[0] = now.getTime();
            now.add(Calendar.DAY_OF_MONTH, d);
            dates[1] = now.getTime();
        } else {
            dates[1] = now.getTime();
            now.add(Calendar.DAY_OF_MONTH, d);
            dates[0] = now.getTime();
        }
        return dates;
    }

    /**
     * 取得开始年月至结束年月全部年月
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public static List<Date> getStartToEnd(Date beginDate, Date endDate) {
        beginDate = DateUtil.date2date(beginDate, DateUtil.YYYY_MM);
        endDate = DateUtil.date2date(endDate, DateUtil.YYYY_MM);
        List<Date> dates = new ArrayList<>();
        dates.add(beginDate);
        while (beginDate.getTime() < endDate.getTime()) {
            beginDate = DateUtil.addMonths(beginDate, 1);
            dates.add(beginDate);
        }
        return dates;
    }
}


