/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.beetl;

import com.nutzfw.core.common.util.DateUtil;
import com.nutzfw.modules.sys.entity.TableFields;
import com.nutzfw.modules.tabledata.enums.FieldType;
import org.beetl.core.Context;
import org.beetl.core.Function;
import org.nutz.lang.Strings;

import java.text.DecimalFormat;
import java.text.MessageFormat;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/8
 * 描述此类：
 */
public class GenMySqlFieldTypeFn implements Function {


    private static String getDecimalFormatValue(String value, int point) {
        StringBuffer sb = new StringBuffer("#");
        if (point > 0) {
            sb.append(".");
        }
        for (int i = 0; i < point; i++) {
            sb.append("#");
        }
        DecimalFormat ss = new DecimalFormat(sb.toString());
        return ss.format(Double.parseDouble(value));
    }

    @Override
    public Object call(Object[] obj, Context context) {
        if (obj[0] instanceof TableFields) {
            TableFields fields = (TableFields) obj[0];
            String value;
            /**
             * @see FieldType
             */
            switch (fields.getFieldType()) {
                case 0:
                    return MessageFormat.format("varchar({0}) DEFAULT {1}", String.valueOf(fields.getLength()), fields.getDefaultValue());
                case 1:
                    if (fields.getDecimalPoint() == 0) {
                        value = String.valueOf(fields.getLength());
                    } else {
                        value = fields.getLength() + "," + fields.getDecimalPoint();
                    }
                    if ("NULL".equals(fields.getDefaultValue()) || Strings.isBlank(fields.getDefaultValue())) {
                        return MessageFormat.format("decimal({0})", value);
                    }
                    return MessageFormat.format("decimal({0}) DEFAULT {1}", value, getDecimalFormatValue(fields.getDefaultValue(), fields.getDecimalPoint()));
                case 2:
                    if (Strings.isNotBlank(fields.getDefaultValue()) && DateUtil.string2date(fields.getDefaultValue(), DateUtil.YYYY_MM_DD_HH_MM_SS) != null) {
                        value = fields.getDefaultValue();
                    } else {
                        value = "NULL";
                    }
                    return MessageFormat.format("DATETIME {0} DEFAULT NULL", value);
                case 3:
                    return MessageFormat.format("text DEFAULT {0}", fields.getDefaultValue());
                case 4:
                    return "varchar(26) DEFAULT NULL";
                case 5:
                    return "varchar(550) DEFAULT NULL";
                default:
                    break;
            }
        } else {
            throw new RuntimeException("错误类型");
        }
        return "";
    }
}
