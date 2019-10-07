/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2015/9/1416:54
 */
@Table("sys_quartz_job")
@Comment("定时任务信息表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class QuartzJob extends BaseEntity {

    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("uuid")
    private String uuid;

    /**
     * 任务名称
     */
    @Column("job_name")
    private String jobName;
    /**
     * 任务类路径
     */
    @Column("job_klass")
    private String jobKlass;

    /**
     * 任务类表达式
     */
    @Column("job_corn")
    private String jobCorn;

    /**
     * 任务描述
     */
    @Column("job_desc")
    private String jobDesc;

    /**
     * 任务排序号
     */
    @Column("job_short")
    private int jobShort;

    /**
     * 任务组
     */
    @Column("job_group")
    private String jobGroup;

    /**
     * 任务状态
     */
    @Column("job_status")
    private String jobStatus;


    /**
     * 运行参数
     */
    @Column("job_args")
    @ColDefine(width = 1000)
    private String args;

    /**
     * 任务最后一次执行情况
     */
    @Column("job_last_status")
    private boolean jobLastStatus;

    /**
     * 任务最后一次执行时长
     */
    @Comment("花费时长")
    @Column("last_consuming")
    private String lastConsuming;

    /**
     * 任务类型 （0随服务启动|1手动启动）
     */
    @Column("job_type")
    private int jobType;

    /**
     * 启动后的任务名
     */
    @Column("job_run_name")
    private String jobRunName;

}
