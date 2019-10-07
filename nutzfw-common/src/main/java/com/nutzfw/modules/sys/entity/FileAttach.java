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

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018/2/6  18:10
 * 描述此类：系统附件表---附件只能逻辑删除，禁止物理删除，因为使用了MD5标识，一个附件可能有多条记录存在。
 */
@Table("sys_file_attach")
@Comment("系统附件表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class FileAttach extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Name
    @PrevInsert(uu32 = true)
    @ColDefine(width = 32, notNull = true)
    @Comment("主键")
    @Column("id")
    private String id;

    @Column("attachType")
    @ColDefine(notNull = true)
    private String attachtype;

    @Column("fileName")
    @ColDefine(type = ColType.VARCHAR, notNull = true)
    private String fileName;

    @Column("savedPath")
    @ColDefine(type = ColType.VARCHAR, notNull = true)
    private String savedPath;

    @Column("filesize")
    private long filesize;

    @Column("adduser")
    private String adduser;

    @Column("md5")
    @ColDefine(type = ColType.VARCHAR, width = 32)
    private String md5;

    @Column
    @Comment("引用文件ID")
    private String referenceId;

    @Column
    @Comment("被引用次数-只有引用次数为0的附件才能被删")
    private int referenceCount;

    @Column("addtime")
    @Comment("操作时间")
    @PrevInsert(now = true)
    @ColDefine(type = ColType.TIMESTAMP)
    private Timestamp addtime;

}
