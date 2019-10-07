/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.tabledata.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/7/2
 * 描述此类：用户数据变更记录，需要审核
 */
@Table("user_data_change_history")
@Comment("用户数据变更记录")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDataChangeHistory extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("uuid")
    private String uuid;

    @Comment("表ID")
    @Column("tableId")
    private int tableId;

    @Column
    @Comment("标识这条数据是谁的")
    @ColDefine(width = 32)
    private String userId;

    @Column
    @Comment("旧数据的源ID")
    @ColDefine(width = 32)
    private String sourceId;


    @Comment("变更记录信息")
    @Column("oldDataViewJson")
    @ColDefine(customType = "longtext")
    private String dataChangeJson;

    @Comment("删除记录")
    @Column("delIdsJson")
    @ColDefine(customType = "longtext")
    private String delIdsJson;

    /**
     * 数据可以直接入库
     */
    @Comment("新数据原始缓存")
    @Column("newDataJson")
    @ColDefine(customType = "longtext")
    private String newDataJson;


    @Column
    @Comment("状态 0新增 1修改 2删除")
    @Default("0")
    private int status;

    @Column
    @Comment("审核状态 0待审核 1通过审核 2未通过审核")
    @Default("0")
    private int review;

    @Column
    @Comment("审核意见")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    private String reviewOpinion;

    @Column
    @Comment("添加人员-管理员或用户")
    @ColDefine(width = 32)
    private String addUserId;

    @Column
    @Comment("添加时间")
    private Date addDate;

    @Column
    @Comment("审核人")
    @ColDefine(width = 32)
    private String reviewUserId;

    @Column
    @Comment("审核时间")
    private Date reviewDate;


}
