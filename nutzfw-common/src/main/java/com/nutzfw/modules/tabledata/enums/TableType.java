package com.nutzfw.modules.tabledata.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/5
 * 描述此类：
 */
@Getter
@AllArgsConstructor
public enum TableType {

    PrimaryTable("人员主表"),
    Schedule("人员附表"),
    SingleTable("数据单表");

    private String lable;

}
