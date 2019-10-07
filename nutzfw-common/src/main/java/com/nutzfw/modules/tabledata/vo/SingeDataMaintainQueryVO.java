/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
    String  name;
    String  fieldName;
    String  sysCode;
    Boolean isDate;
    String  dateFormat;
    String  startVal;
    String  endVal;
    String  val;
    String  joiner;
}
