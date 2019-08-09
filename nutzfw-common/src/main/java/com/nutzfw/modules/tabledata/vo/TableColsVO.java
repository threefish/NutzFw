package com.nutzfw.modules.tabledata.vo;

import com.nutzfw.modules.tabledata.enums.ControlType;
import com.nutzfw.modules.tabledata.enums.FieldType;

public class TableColsVO {
    String field;
    String title;
    String type = "";
    String fixed = "false";
    String toolbar;
    String templet;

    /**
     * 最小宽度
     */
    Integer minWidth;
    /**
     * 宽度
     */
    Integer width;

    public TableColsVO() {
    }

    public TableColsVO(String type, String fixed) {
        this.fixed = fixed;
        this.type = type;
        this.title = "";
        this.minWidth = 50;
        this.width = 50;
        if ("numbers".equals(type)) {
            this.title = "序号";
        }
    }


    /**
     * @param field
     * @param title
     * @param minWidth
     * @param fixed
     * @param attachType  单附件4  多附件 5  结果 0 图片 1单附件 2多附件
     * @param controlType 7图片  8附件
     */
    public TableColsVO(String field, String title, int minWidth, int fixed, int attachType, int controlType) {
        this.field = field.toLowerCase();
        this.title = title;
        this.minWidth = minWidth;
        switch (fixed) {
            case 0:
                this.fixed = null;
                break;
            case 1:
                this.fixed = "left";
                break;
            case 2:
                this.fixed = "right";
                break;
            default:
                break;
        }
        //附件
        if (attachType == FieldType.SingleAttach.getValue() || attachType == FieldType.MultiAttach.getValue()) {
            this.minWidth = 120;
            if (controlType == ControlType.Img.getValue()) {
                //图片处理方式
                this.templet = "#AttachTypeImgTpl";
            } else {
                //默认附件处理方式
                this.templet = "#AttachTypeTpl";
            }
        }
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFixed() {
        return fixed;
    }

    public void setFixed(String fixed) {
        this.fixed = fixed;
    }

    public String getToolbar() {
        return toolbar;
    }

    public void setToolbar(String toolbar) {
        this.toolbar = toolbar;
    }

    public String getTemplet() {
        return templet;
    }

    public void setTemplet(String templet) {
        this.templet = templet;
    }

    public Integer getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(Integer minWidth) {
        this.minWidth = minWidth;
    }

    @Override
    public String toString() {
        return "TableColsVO{" +
                "field='" + field + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", fixed='" + fixed + '\'' +
                ", toolbar='" + toolbar + '\'' +
                ", templet='" + templet + '\'' +
                ", minWidth=" + minWidth +
                '}';
    }
}