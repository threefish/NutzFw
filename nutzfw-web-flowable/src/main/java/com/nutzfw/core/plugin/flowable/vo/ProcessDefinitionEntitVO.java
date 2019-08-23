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


