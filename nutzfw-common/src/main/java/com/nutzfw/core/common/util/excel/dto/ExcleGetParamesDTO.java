/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.util.excel.dto;

import com.nutzfw.core.common.util.DateUtil;
import lombok.Data;

/**
 * @author 黄川 huchuc@vip.qq.com
 * 取得excle数据的时候进行参数的转换控制可以自定义扩展
 */
@Data
public class ExcleGetParamesDTO {
    /**
     * 是否强转日期为字符串
     */
    private Boolean converDateToStr;
    /**
     * 默认日期时间
     */
    private String  dateFormat;

    public ExcleGetParamesDTO() {
        this.converDateToStr = true;
        this.dateFormat = DateUtil.YYYY_MM_DD;
    }

    public ExcleGetParamesDTO(boolean converDateToStr, String dateFormat) {
        this.converDateToStr = converDateToStr;
        this.dateFormat = dateFormat;
    }

    public static ExcleGetParamesDTO defaualt() {
        return new ExcleGetParamesDTO(false, DateUtil.YYYY_MM_DD);
    }

    public static ExcleGetParamesDTO create(String dateFormat) {
        return new ExcleGetParamesDTO(true, dateFormat);
    }

}