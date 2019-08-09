package com.nutzfw.modules.sys.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/18
 * 描述此类：
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuVO {

    String id;

    String text;

    String icon;

    String url;

    String targetType = "iframe-tab";

    List<MenuVO> children = new ArrayList<>();

}
