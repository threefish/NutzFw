/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

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
