package com.nutzfw.modules.sys.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;

import java.sql.Timestamp;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/28
 * 描述此类：
 */
@Table("sys_error_log_history")
@Comment("系统错误日志")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ErrorLogHistory extends BaseEntity {
    @Id
    private int id;
    @Column
    private String userid;
    @Column
    private String userDesc;
    @Column
    private Timestamp ct;
    @Column
    @ColDefine(width = 300)
    private String path;
    @Column
    @ColDefine(width = 2000)
    private String errorMsg;
    @Column
    @ColDefine(type = ColType.TEXT)
    private String errorMsgInfo;

}
