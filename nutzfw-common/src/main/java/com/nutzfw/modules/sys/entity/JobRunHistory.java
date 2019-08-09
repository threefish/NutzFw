package com.nutzfw.modules.sys.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/10
 * 描述此类：
 */
@Table("sys_quartz_job_run_history")
@Comment("任务执行历史")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class JobRunHistory extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("uuid")
    private String uuid;

    @Comment("定时任务主键")
    @Column("job_id")
    private String jobId;

    @Comment("花费时长")
    @Column("consuming")
    private String consuming;

    @Column("status")
    @Comment("执行情况")
    private boolean status;

    @Column("errorLog")
    @ColDefine(type = ColType.TEXT)
    @Comment("错误日志")
    private String errorLog;

}
