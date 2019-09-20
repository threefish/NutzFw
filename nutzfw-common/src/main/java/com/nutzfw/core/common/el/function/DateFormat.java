package com.nutzfw.core.common.el.function;

import com.nutzfw.core.common.util.DateUtil;
import org.nutz.el.opt.RunMethod;
import org.nutz.lang.Strings;
import org.nutz.plugin.Plugin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * DateFormat 日期格式化
 *
 * @author huchuc@vip.qq.com
 * <p>
 * ${df(contract.effectiveDate,'yyyy年MM月dd日')}
 */
public class DateFormat implements RunMethod, Plugin {

    SimpleDateFormat sformat = new SimpleDateFormat(DateUtil.YYYY_MM_DD_HH_MM_SS_ZH);

    @Override
    public Object run(List<Object> fetchParam) {
        if (fetchParam.size() == 0) {
            return "解析异常";
        }
        if (fetchParam.size() < 2) {
            return "解析异常:参数不够";
        }
        try {
            Date date = getDate(fetchParam.get(0));
            return new SimpleDateFormat(Strings.sNull(fetchParam.get(1))).format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "解析异常:格式化表达式异常！";
        }
    }

    private Date getDate(Object object) throws ParseException {
        if (object instanceof Date) {
            return (Date) object;
        }
        if (object instanceof java.sql.Date) {
            return new Date(((java.sql.Date) object).getTime());
        }
        if (object instanceof java.sql.Timestamp) {
            return new Date(((java.sql.Timestamp) object).getTime());
        }
        if (object instanceof String) {
            return sformat.parse((String) object);
        }
        if (object instanceof Long) {
            return new Date((Long) object);
        }
        if (object instanceof Integer) {
            return new Date((Integer) object);
        }
        throw new RuntimeException("参数不是预期的日期格式！");
    }

    @Override
    public boolean canWork() {
        return true;
    }

    @Override
    public String fetchSelf() {
        return "df";
    }
}