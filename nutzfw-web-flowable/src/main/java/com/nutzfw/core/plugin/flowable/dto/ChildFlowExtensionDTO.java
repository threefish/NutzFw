package com.nutzfw.core.plugin.flowable.dto;

import com.nutzfw.core.plugin.flowable.enums.FormType;
import com.nutzfw.modules.sys.entity.RoleField;
import lombok.Data;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2021/10/26
 */
@Data
public class ChildFlowExtensionDTO {

    private String childTableId;
    private String mainTableId;
    private String jsonData;
    private FormType formType;
    private List<RoleField> mainFields;
    private List<RoleField> childFields;
    private List<BindField> bindFields;

    @Data
    public static class BindField{
        private RoleField mainField;
        private RoleField childField;
    }
}
