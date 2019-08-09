package com.nutzfw.core.common.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 叶世游
 * @date 2018/6/19 10:14
 * @description ztree专用bean
 */
@NoArgsConstructor
@Data
public class ZtreeBeanVO {
    private String id;
    private String pid;
    private String name;
    private String iconSkin;

    public ZtreeBeanVO(String id, String pid, String name) {
        this.id = id;
        this.pid = pid;
        this.name = name;
    }

    public ZtreeBeanVO(String id, String deptId, String realName, String iconSkin) {
        this(id, deptId, realName);
        this.iconSkin = iconSkin;
    }

}
