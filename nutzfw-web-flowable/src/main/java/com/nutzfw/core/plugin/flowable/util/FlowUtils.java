package com.nutzfw.core.plugin.flowable.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.nutzfw.core.plugin.flowable.converter.CustomUserTaskJsonConverter;
import com.nutzfw.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.sys.entity.Role;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.editor.constants.ModelDataJsonConstants;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.flowable.engine.repository.Model;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntity;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.UserEntity;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/9
 */
public class FlowUtils {

    public static ExtensionElement buildExtensionElement(String name, String textValue) {
        ExtensionElement extensionElement = new ExtensionElement();
        extensionElement.setName(name);
        extensionElement.setNamespacePrefix("nutzfw");
        extensionElement.setNamespace(BpmnJsonConverter.MODELER_NAMESPACE);
        extensionElement.setElementText(textValue);
        return extensionElement;
    }

    public static List<UserEntity> toFlowableUserList(List<UserAccount> users) {
        List<UserEntity> entities = new ArrayList<>(users.size());
        users.forEach(userAccount -> entities.add(toFlowableUser(userAccount)));
        return entities;
    }

    public static UserEntity toFlowableUser(UserAccount user) {
        if (user == null) {
            return null;
        }
        UserEntity userEntity = new UserEntityImpl();
        userEntity.setId(user.getUserName());
        userEntity.setFirstName(user.getRealName());
        userEntity.setEmail(user.getMail());
        userEntity.setRevision(1);
        return userEntity;
    }

    public static List<GroupEntity> toFlowableGroupList(List<Role> roles) {
        List<GroupEntity> entities = new ArrayList<>(roles.size());
        roles.forEach(role -> entities.add(toFlowableGroup(role)));
        return entities;
    }

    public static GroupEntity toFlowableGroup(Role role) {
        if (role == null) {
            return null;
        }
        GroupEntity groupEntity = new GroupEntityImpl();
        groupEntity.setId(role.getRoleCode());
        groupEntity.setName(role.getRoleName());
        groupEntity.setType(role.getRoleCode());
        groupEntity.setRevision(1);
        return groupEntity;
    }

    public static Model buildModel(Model model, String categoryId, String name, String key, String description) {
        description = Strings.sNull(description);
        name = Strings.sNull(name);
        key = Strings.sNull(key).replaceAll(" ", "");
        NutMap nutMap = NutMap.NEW();
        nutMap.put("category", categoryId);
        nutMap.put("key", key);
        nutMap.put(ModelDataJsonConstants.MODEL_NAME, name);
        nutMap.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        nutMap.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        model.setMetaInfo(Json.toJson(nutMap, JsonFormat.compact()));
        model.setKey(key);
        model.setName(name);
        model.setCategory(categoryId);
        model.setKey(key);
        return model;
    }


    public static NutMap toNutMap(JsonNode displayNode) {
        return NutMap.WRAP(displayNode.toString());
    }


    public static UserTaskExtensionDTO getUserTaskExtension(UserTask userTask) {
        UserTaskExtensionDTO dto = null;
        if (userTask != null) {
            List<ExtensionElement> properites = userTask.getExtensionElements().get(CustomUserTaskJsonConverter.USER_TASK_EXTENSION_ELEMENT_NAME);
            if (properites.size() > 0) {
                String extensionElementText = properites.get(0).getElementText();
                if (Strings.isNotBlank(extensionElementText)) {
                    dto = Json.fromJson(UserTaskExtensionDTO.class, extensionElementText);
                }
            }
            if (userTask.getBehavior() instanceof MultiInstanceActivityBehavior && dto != null) {
                //是多实例节点
                dto.setMultiInstanceNode(true);
            }
        }
        return dto;
    }

}
