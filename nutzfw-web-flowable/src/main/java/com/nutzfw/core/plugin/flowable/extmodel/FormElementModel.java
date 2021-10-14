package com.nutzfw.core.plugin.flowable.extmodel;

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
    String formType;

}
