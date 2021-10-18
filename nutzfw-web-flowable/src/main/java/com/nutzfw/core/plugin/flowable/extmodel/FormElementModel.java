package com.nutzfw.core.plugin.flowable.extmodel;

import com.nutzfw.core.plugin.flowable.enums.FormType;
import lombok.Data;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2021/10/14
 */
@Data
public class FormElementModel {

    List<OnlineFieldAuth> fieldAuths;
    String formKey;
    String tableId;
    // 流程状态回写字段
    String writeBackProccessStatusField;
    FormType formType;

}
