package com.nutzfw.modules.sys.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import com.nutzfw.modules.tabledata.enums.TableType;
import lombok.*;
import org.nutz.dao.entity.annotation.*;
import org.nutz.plugins.validation.annotation.Validations;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/5
 * 描述此类：
 */
@Table("sys_data_table")
@Comment("表定义信息")
@TableIndexes({@Index(name = "realName_unique", fields = {"tableName"}), @Index(name = "name_unique", fields = {"name"})})
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class DataTable extends BaseEntity {

    @Id
    private int id;

    @Column
    @Comment("表名")
    @ColDefine(notNull = true, width = 50)
    @Validations(required = true, strLen = {2, 50}, errorMsg = "表名称必须填写,长度2-50")
    private String name;

    @Column
    @Comment("物理表名")
    @ColDefine(width = 50)
    private String tableName;

    @Column
    @Comment("备注")
    @ColDefine(notNull = true, width = 50)
    @Validations(required = true, strLen = {2, 50}, errorMsg = "表备注必须填写,长度2-50")
    private String comment;


    @Column
    @Comment("表类型")
    @Validations(required = true, el = "value>=0", errorMsg = "表类型必须填写")
    @ColDefine(width = 50)
    private TableType tableType;

    @Column
    @Comment("是否系统表")
    @Default("0")
    private boolean system;

    @Column
    @Comment("是UUID")
    @Default("0")
    private boolean uuid;

    @Column(version = true)
    @Comment("版本")
    private int version;

    @Column
    @Comment("状态 0未同步 1已同步 2同步失败")
    private int status;

    @Column
    @Comment("表单模版")
    @Default("1")
    private int formTemplate;

    /**
     * 一对多关系
     */
    @Many(field = "tableId")
    private List<TableFields> fields;

    public String getPrimaryKey() {
        return isUuid() ? "uuid" : "id";
    }

}
