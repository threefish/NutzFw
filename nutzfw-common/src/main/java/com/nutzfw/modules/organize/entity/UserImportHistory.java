package com.nutzfw.modules.organize.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

/**
 * 用户导入历史记录
 *
 * @author 叶世游
 * @date 2018/6/22 20:20
 * @description
 */
@Table("sys_user_account_import_history")
@Comment("用户导入历史记录")
//@TableIndexes({@Index(name = "userName_unique", fields = {"userName"})})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class UserImportHistory extends BaseEntity {
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
    @Comment("0 待检查 1检查中 2检查失败 3导入中 4导入成功 5导入失败")
    private int staus;

    @Column
    @Comment("耗时")
    private String consuming;

    @Column
    @Comment("错误消息")
    private String errMsg;
    @Column
    @Comment("错误消息详情")
    @ColDefine(type = ColType.TEXT)
    private String errMsgInfo;

    public UserImportHistory(String id, String userid, String userDesc, String attachId, int staus, String consuming) {
        this.id = id;
        this.userid = userid;
        this.userDesc = userDesc;
        this.attachId = attachId;
        this.staus = staus;
        this.consuming = consuming;
    }
}
