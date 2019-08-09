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
    private String id;
    private String value;
    private List<DeptSelectVO> childs;
}


