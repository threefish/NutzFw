package com.nutzfw.modules.organize.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author 823085344@qq.com
 * 创建人：潘闯
 * 创建时间: 2018/6/14
 * 描述此类：系统用户岗位关联
 */
@Table("sys_user_account_job")
@PK({"jobId", "userId"})
@TableIndexes(@Index(fields = {"jobId", "userId"}, name = "pks"))
@Comment("用户岗位关联信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class UserAccountJob extends BaseEntity {

    @Column("job_id")
    @ColDefine(width = 32, notNull = true)
    private String jobId;

    @Column("user_id")
    @Name
    @ColDefine(width = 32, notNull = true)
    private String userId;
}
