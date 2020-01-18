/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.vo;

import lombok.Data;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntityImpl;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/10
 */
@Data
public class ProcessDefinitionEntitVO implements Serializable {

    private static final long serialVersionUID = 1L;

    String id;
    String name;
    String key;
    String category;
    String categoryName;
    Integer version;
    String deploymentId;
    String resourceName;
    String diagramResourceName;
    Integer suspensionState;
    Date deploymentTime;


    public ProcessDefinitionEntitVO(ProcessDefinitionEntityImpl procDef) {
        this.id = procDef.getId();
        this.name = procDef.getName();
        this.key = procDef.getKey();
        this.version = procDef.getVersion();
        this.deploymentId = procDef.getDeploymentId();
        this.resourceName = procDef.getResourceName();
        this.diagramResourceName = procDef.getDiagramResourceName();
        this.suspensionState = procDef.getSuspensionState();
    }

}


