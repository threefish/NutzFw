package com.nutzfw.modules.monitor.entity;

import com.nutzfw.core.common.entity.BaseEntity;
import lombok.*;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.interceptor.annotation.PrevInsert;

import java.io.Serializable;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/12
 * 描述此类：
 */
@Table("sys_operate_log_${ym}")
@Comment("系统操作日志")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class SysOperateLog extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column
    int id;

    @Comment("类型")
    @Column("type")
    private String type;

    @Comment("标签")
    @Column("tag")
    private String tag;

    @Comment("java方法")
    @Column("source")
    private String source;

    @Comment("访问路径")
    @Column("path")
    private String path;

    @Comment("IP")
    @Column("ip")
    @ColDefine(width = 500)
    private String ip;

    @Comment("浏览器")
    @Column("browser")
    private String browser;

    @Comment("操作系统")
    @Column("os")
    private String os;

    @Comment("信息")
    @Column("msg")
    @ColDefine(width = 500)
    private String msg;

    @Comment("访问方式")
    @Column("method")
    private String method;

    @Comment("执行耗时")
    @Column("consuming")
    private String consuming;

    @Comment("参数")
    @Column("param")
    @ColDefine(type = ColType.TEXT, customType = "mediumtext")
    private String param;

    @Comment("返回结果")
    @Column("result")
    @ColDefine(type = ColType.TEXT, customType = "mediumtext")
    private String result;

    @Comment("访问人")
    @Column("userName")
    @PrevInsert(els = @EL("$me.userName()"))
    private String userName;

    @Comment("访问人部门")
    @Column("deptId")
    private String deptId;

    @Comment("访问人部门名称")
    @Column("deptDesc")
    private String deptDesc;

}
