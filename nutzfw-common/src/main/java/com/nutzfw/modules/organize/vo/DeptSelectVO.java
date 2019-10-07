/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.organize.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 部门下拉实体类
 *
 * @author 叶世游
 * @date 2018/7/4 14:06
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeptSelectVO {
    private String             id;
    private String             value;
    private List<DeptSelectVO> childs;
}


