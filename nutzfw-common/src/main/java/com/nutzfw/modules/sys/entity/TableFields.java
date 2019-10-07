/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import com.nutzfw.core.common.util.RegexUtil;
import com.nutzfw.modules.sys.enums.FieldAuth;
import com.nutzfw.modules.tabledata.enums.ControlType;
import com.nutzfw.modules.tabledata.enums.DictDepend;
import com.nutzfw.modules.tabledata.enums.FieldType;
import com.nutzfw.modules.tabledata.enums.FormValidationRulesType;
import lombok.*;
import org.nutz.dao.entity.annotation.*;
import org.nutz.lang.Strings;
import org.nutz.plugins.validation.annotation.Validations;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/5
 * 描述此类：
 */
@Table("sys_data_fields")
@Comment("字段定义信息")
@TableIndexes({@Index(name = "realName_unique", fields = {"fieldName"})})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TableFields extends BaseEntity {

    @Id
    private int id;

    @Column
    @Comment("字段名")
    @ColDefine(notNull = true, width = 50)
    @Validations(required = true, strLen = {2, 50}, custom = "checkName", errorMsg = "字段名称必须填写,只允许汉字、字母、数字混合,长度2-50")
    private String name;

    @Column
    @Comment("表ID")
    private int tableId;

    @Column
    @Comment("是否系统字段")
    @Default("0")
    private boolean system;


    @Column
    @Comment("物理表名")
    @ColDefine(width = 50)
    private String tableName;

    @Column
    @Comment("物理字段名-必须为全小写-自动生成")
    @ColDefine(width = 50)
    private String fieldName;

    @Column
    @Comment("备注")
    @ColDefine(notNull = true, width = 50)
    private String comment;

    @Column
    @Comment("字段长度")
    @ColDefine(notNull = true)
    @Validations(required = true, el = "value>0", errorMsg = "字段长度必须大于0")
    private int length;

    @Column
    @Comment("小数点")
    private int decimalPoint;

    @Column
    @Comment("默认值")
    @ColDefine(width = 50)
    @Validations(custom = "decimalCheckDefault", errorMsg = "数值型字段默认值请输入数字或NULL")
    private String defaultValue;

    /**
     * @see FieldType
     */
    @Column
    @Comment("字段类型")
    private int fieldType;

    @Column
    @Comment("允许空值")
    private boolean nullValue;

    @Column
    @Comment("主键")
    private boolean primaryKey;

    @Column
    @Comment("表单显示")
    private boolean fromDisplay;
    /**
     * @see ControlType
     */
    @Column
    @Comment("表单控件类型")
    private int     controlType;

    /**
     * @see FormValidationRulesType
     */
    @Column
    @Comment("效验规则")
    private int validationRulesType;

    @Column
    @Comment("表单显示文字")
    @Validations(strLen = {0, 20}, errorMsg = "表单显示文字长度0-20")
    private String fromLable;

    @Column
    @Comment("字段填写提示")
    @Validations(strLen = {0, 100}, errorMsg = "字段填写提示0-100")
    @Default("")
    private String fromLableTips;

    @Column
    @Comment("字段显示顺序")
    @Default("0")
    private int shortNo;

    @Column
    @Comment("字典CODE")
    @Default("")
    private String dictSysCode;

    @Column
    @Comment("字典名称")
    @Default("")
    private String dictSysCodeDesc;

    @Comment("多选字典")
    @Column("multiple")
    @Default("0")
    private boolean multipleDict;

    /**
     * @see DictDepend
     */
    @Comment("字典依赖类型")
    @Column("dictDepend")
    @Default("0")
    private int dictDepend;

    @Comment("依赖字段名称")
    @Column("dictDependFieldDesc")
    @Default("")
    @Validations(custom = "checkDictDependFieldDesc", errorMsg = "当前为依赖字段不能选择字典！")
    private String dictDependFieldDesc;

    @Comment("依赖字段ID")
    @Column("dictDependFieldId")
    @Default("0")
    private int dictDependFieldId;

    @Column
    @Comment("附件后缀限制")
    @Default("")
    private String attachSuffix;


    @Column
    @Comment("等待删除-同步时会将值为true的字段删掉")
    @Default("0")
    private boolean delectStatus;


    @Column
    @Comment("是否逻辑字段")
    @Default("0")
    private boolean logic;

    @Column
    @Comment("逻辑字段解析后的SQL表达式")
    @Default("")
    @ColDefine(width = 300)
    private String logicSqlExpression;

    @Column
    @Comment("逻辑字段解析前的EL表达式")
    @Default("")
    @ColDefine(width = 300)
    @Validations(strLen = {0, 300}, errorMsg = "逻辑表达式长度为0-300")
    private String logicElExpression;

    /**
     * 当前字段权限
     */
    @Readonly
    @Column
    private FieldAuth auth;

    public void setFieldName(String fieldName) {
        if (Strings.isNotBlank(fieldName)) {
            this.fieldName = Strings.sNull(fieldName).toLowerCase();
        } else {
            this.fieldName = fieldName;
        }
    }

    public String getFromLable() {
        return Strings.isBlank(fromLable) ? getName() : fromLable;
    }


    public boolean checkDictDependFieldDesc() {
        return !(Strings.isNotBlank(this.dictDependFieldDesc) && Strings.isNotBlank(this.dictSysCode));
    }

    public boolean decimalCheckDefault() {
        try {
            final String NULL = "NULL";
            if (NULL.equals(defaultValue)) {
                return true;
            }
            Double.parseDouble(defaultValue);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查字段名
     *
     * @return
     */
    public boolean checkName() {
        return RegexUtil.isChineseAzNum(name);
    }
}
