/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.tabledata.vo;

import com.nutzfw.modules.tabledata.enums.TableType;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/8
 * 描述此类：
 */
@Data
@AllArgsConstructor
public class DbTreeVO {
    Integer id;
    /**
     * 0表  1字段
     */
    Integer type;

    String text;

    TableType tableType;

    Integer tableId;

    Integer fieldId;

    Integer fieldType;

    Boolean isParent;

    Boolean chkDisabled;

    public DbTreeVO(Integer id, Integer type, String text, TableType tableType, Integer tableId, Integer fieldId, Integer fieldType, Boolean isParent) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.tableType = tableType;
        this.tableId = tableId;
        this.fieldId = fieldId;
        this.fieldType = fieldType;
        this.isParent = isParent;
        this.chkDisabled = false;
    }


}
