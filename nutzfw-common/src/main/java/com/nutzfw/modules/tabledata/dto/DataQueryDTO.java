package com.nutzfw.modules.tabledata.dto;

import lombok.Data;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/7/30
 * 描述此类：
 */
@Data
public class DataQueryDTO {
    /**
     * 字段ID
     */
    Integer id;
    /**
     * 表名称
     */
    String tableName;
    /**
     * 字段名称
     */
    String text;
    /**
     * 查询条件
     */
    String cndText;
    /**
     * 排序方式  0无  1升序 2降序
     */
    Integer fieldDesc;
    /**
     * 固定方式 0无  1左固定 2右固定
     */
    Integer fieldFixed;
    /**
     * 分组 0 无  1分组 2总和 3平均值 4最小值 5最大值 6计数
     */
    Integer fieldGroup;
    /**
     * 字段组合 0与 1或
     */
    Integer fieldLinkType;
    /**
     * 是否显示  0输出  1不输出
     */
    Integer fieldShow;
    /**
     * 字段类型
     */
    Integer fieldType;


}
