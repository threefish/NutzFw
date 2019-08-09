package com.nutzfw.modules.tabledata.vo;

import lombok.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/1/2
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SingeDataMaintainQueryVO {
    Integer id;
    String name;
    String fieldName;
    String sysCode;
    Boolean isDate;
    String dateFormat;
    String startVal;
    String endVal;
    String val;
    String joiner;
}
