/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.tabledata.vo;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/7/3
 * 描述此类：审核数据变化
 */
public class ReviewChangeVO {
    /**
     * 字段
     */
    private String  lable;
    /**
     * 旧数据
     */
    private String  oldValue;
    /**
     * 新数据
     */
    private String  newValue;
    /**
     * 是否发生变化
     */
    private boolean change;

    /**
     * 字段类型
     */
    private int fieldType;

    /**
     * 变更前附件个数
     */
    private int oldAttachNum;

    /**
     * 变更后附件个数
     */
    private int newAttachNum;

    public int getFieldType() {
        return fieldType;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    public int getOldAttachNum() {
        return oldAttachNum;
    }

    public void setOldAttachNum(int oldAttachNum) {
        this.oldAttachNum = oldAttachNum;
    }

    public int getNewAttachNum() {
        return newAttachNum;
    }

    public void setNewAttachNum(int newAttachNum) {
        this.newAttachNum = newAttachNum;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public boolean isChange() {
        return change;
    }

    public void setChange(boolean change) {
        this.change = change;
    }

    @Override
    public String toString() {
        return "ReviewChangeVO{" +
                "lable='" + lable + '\'' +
                ", oldValue='" + oldValue + '\'' +
                ", newValue='" + newValue + '\'' +
                ", change=" + change +
                ", fieldType=" + fieldType +
                ", oldAttachNum=" + oldAttachNum +
                ", newAttachNum=" + newAttachNum +
                '}';
    }
}
