package com.nutzfw.core.common.util.excel.dto;

import lombok.Data;
import org.nutz.json.JsonField;

/**
 * @author 黄川 huchuc@vip.qq.com
 */
@Data
public class MergedDTO {

    @JsonField(ignore = true)
    String content;
    Integer firstRow;
    Integer lastRow;
    Integer firstCol;
    Integer lastCol;

}