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
