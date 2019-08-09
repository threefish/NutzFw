package com.nutzfw.modules.tabledata.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import com.nutzfw.modules.tabledata.enums.TableType;
import lombok.*;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/19
 * 描述此类：数据导入效验表
 */

@Table("sys_data_import_history")
@Comment("数据导入历史")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class DataImportHistory extends BaseEntity {

    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("id")
    private String id;

    @Column
    private String userid;

    @Column
    private String userDesc;

    @Column
    @Comment("临时文件")
    private String attachId;

    @Column
    private int tableId;

    @Column
    @Comment("表名")
    private String tableName;

    @Column
    @Comment("表类型")
    private TableType tableType;

    @Column
    @Comment("导入模式")
    private int importType;

    @Column
    @Comment("唯一效验字段")
    private int uniqueField;

    @Column
    @Comment("0 待检查 1检查中 2检查失败 3导入中 4导入成功 5导入失败")
    private int staus;

    @Column
    @ColDefine(width = 2000)
    private String errorMsg;

    @Column
    @ColDefine(type = ColType.TEXT)
    private String errorMsgInfo;

    @Column
    @Comment("耗时")
    private String consuming;


}
