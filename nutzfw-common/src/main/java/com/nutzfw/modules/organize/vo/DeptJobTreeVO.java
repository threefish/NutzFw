/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.organize.vo;

import com.nutzfw.core.common.vo.ZtreeBeanVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author 创建人：叶世游
 * 创建时间: 2018/6/5
 * 描述此类：部门岗位实体类
 */
public class DeptJobTreeVO extends ZtreeBeanVO {

    private String              type;
    private Integer             count     = 0;
    private List<DeptJobTreeVO> childrens = new ArrayList<>();


    public DeptJobTreeVO() {
    }

    public DeptJobTreeVO(String id, String pid, String name, String type) {
        super(id, pid, name);
        this.type = type;
    }


    public DeptJobTreeVO(String id, String pid, String name, String type, List<DeptJobTreeVO> childrens) {
        super(id, pid, name);
        this.type = type;
        this.childrens = childrens;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<DeptJobTreeVO> getChildrens() {
        return childrens;
    }

    public void setChildrens(List<DeptJobTreeVO> childrens) {
        this.childrens = childrens;
    }
}
