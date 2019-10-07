/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.userchage.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;
import java.util.Date;

/**
 * 人员异动表
 *
 * @author 叶世游
 * @date 2018/7/9 18:00
 * @description
 */
@Table("sys_user_change_history")
@Comment("首页统计配置")
public class UserChangeHistory extends BaseEntity implements Serializable {
    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("uuid")
    private String id;

    @Column
    @Comment("异动的用户id")
    @ColDefine(width = 32)
    private String userId;


    @Column
    @Comment("附件列表")
    @ColDefine(customType = "longtext")
    private String attachIds;

    @Comment("变更记录信息")
    @Column("oldDataViewJson")
    @ColDefine(customType = "longtext")
    private String dataChangeJson;
    /**
     * 数据可以直接入库
     */
    @Comment("新数据原始缓存")
    @Column("newDataJson")
    @ColDefine(customType = "longtext")
    private String newDataJson;


    @Comment("备注")
    @Column("remark")
    @ColDefine(customType = "longtext")
    private String remark;

    @Column
    @Comment("异动原因")
    private int changeType;

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
    @Comment("异动时间")
    private Date changeDate;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAttachIds() {
        return attachIds;
    }

    public void setAttachIds(String attachIds) {
        this.attachIds = attachIds;
    }

    public String getDataChangeJson() {
        return dataChangeJson;
    }

    public void setDataChangeJson(String dataChangeJson) {
        this.dataChangeJson = dataChangeJson;
    }

    public String getNewDataJson() {
        return newDataJson;
    }

    public void setNewDataJson(String newDataJson) {
        this.newDataJson = newDataJson;
    }

    public int getChangeType() {
        return changeType;
    }

    public void setChangeType(int changeType) {
        this.changeType = changeType;
    }

    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }

    public String getReviewOpinion() {
        return reviewOpinion;
    }

    public void setReviewOpinion(String reviewOpinion) {
        this.reviewOpinion = reviewOpinion;
    }

    public String getAddUserId() {
        return addUserId;
    }

    public void setAddUserId(String addUserId) {
        this.addUserId = addUserId;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public String getReviewUserId() {
        return reviewUserId;
    }

    public void setReviewUserId(String reviewUserId) {
        this.reviewUserId = reviewUserId;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
