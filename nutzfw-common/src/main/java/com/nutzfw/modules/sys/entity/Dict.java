package com.nutzfw.modules.sys.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.plugins.validation.annotation.Validations;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/4
 * 描述此类：字典类型
 */
@Table("sys_dict")
@Comment("数据字典")
@TableIndexes(@Index(fields = {"sysCode", "grouping", "lable"}, name = "pks"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Dict extends BaseEntity {

    @Id
    private int id;

    @Column
    @Comment("pid")
    private int pid;

    @Column
    @Comment("字典编码")
    @ColDefine(notNull = true)
    @Validations(custom = "checkSysCode", errorMsg = "字典编码不能为空")
    private String sysCode;

    @Column
    @Comment("通过likecode可以一次性查询出全部子节点")
    @ColDefine(notNull = true, width = 500)
    private String likeCode;

    @Column
    @Comment("字典名称")
    @ColDefine(notNull = true)
    @Validations(required = true, errorMsg = "字典名称不能为空")
    private String lable;

    @Comment("展示模式  0下拉框  1单选框  2复选框  3树形字典")
    @Column("showType")
    private int showType;

    @Comment("系统内置")
    @Column("internal")
    private boolean internal;

    @Comment("允许编辑")
    @Column("edit")
    @Default("1")
    private boolean edit;

    @Comment("枚举分组")
    @Column("grouping")
    private boolean grouping;

    @Comment("描述")
    @Column("mark")
    private String mark;

    @Column
    @Comment("字典键值")
    @ColDefine(notNull = true)
    @Validations(custom = "checkVal", errorMsg = "字典键值不能为空")
    private String val;

    @Column
    @Comment("字典排序")
    private int shortNo;

    @Column
    @Comment("是否是默认值")
    private boolean defaultVal;

    @Comment("附加值1")
    @Column
    @Default("")
    private String extra1;

    @Comment("附加值")
    @Column
    @Default("")
    private String extra2;
    @Comment("附加值")
    @Column
    @Default("")
    private String extra3;
    @Comment("附加值")
    @Column
    @Default("")
    private String extra4;
    @Comment("附加值")
    @Column
    @Default("")
    private String extra5;
    @Comment("附加值")
    @Column
    @Default("")
    private String extra6;
    @Comment("附加值")
    @Column
    @Default("")
    private String extra7;
    @Comment("附加值")
    @Column
    @Default("")
    private String extra8;
    @Comment("附加值")
    @Column
    @Default("")
    private String extra9;
    @Comment("附加值")
    @Column
    @Default("")
    private String extra10;
    /**
     * 动态表单需要，禁止删除或修改
     */
    private String domId;

    /**
     * 动态表单需要，禁止删除或修改
     */
    public String getDomId() {
        return domId == null ? R.UU16() : domId;
    }

    public void setSysCode(String sysCode) {
        this.sysCode = sysCode.trim();
    }

    /**
     * 效验
     *
     * @return
     */
    public boolean checkSysCode() {
        return !(isGrouping() && Strings.isEmpty(this.sysCode));
    }

    /**
     * 效验
     *
     * @return
     */
    public boolean checkVal() {
        if (!isGrouping()) {
            return (Strings.isNotBlank(this.val) && Strings.sNull(this.val).length() <= 100);
        }
        return true;
    }
}
