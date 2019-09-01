package com.nutzfw.modules.flow.action;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.plugin.flowable.constant.FlowConstant;
import com.nutzfw.core.plugin.flowable.converter.CustomBpmnJsonConverter;
import com.nutzfw.core.plugin.flowable.util.FlowDiagramUtils;
import com.nutzfw.core.plugin.flowable.util.FlowUtils;
import com.nutzfw.core.plugin.flowable.validator.CustomProcessValidatorFactory;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.sys.entity.Role;
import com.nutzfw.modules.sys.service.RoleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ValuedDataObject;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.editor.constants.ModelDataJsonConstants;
import org.flowable.editor.constants.StencilConstants;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Model;
import org.flowable.idm.api.User;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntity;
import org.flowable.ui.common.model.ResultListDataRepresentation;
import org.flowable.ui.common.model.UserRepresentation;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ValidationError;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.*;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/9
 */
@IocBean
@At("/flowDesign")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class FlowDesignAction extends BaseAction {

    @Inject
    RepositoryService repositoryService;
    @Inject
    UserAccountService userAccountService;
    @Inject
    RoleService roleService;

    @Inject
    FlowDiagramUtils flowDiagramUtils;

    CustomBpmnJsonConverter customBpmnJsonConverter = new CustomBpmnJsonConverter();

    @At("/flowable")
    @RequiresPermissions("sys.flow")
    @AutoCreateMenuAuth(name = "流程管理", icon = "fa-tachometer")
    public String flowable() {
        return "";
    }

    @At({"/stencil-sets/editor", "/stencilSets/editor"})
    @Ok("raw")
    public String NutMap() {
        return Files.read("flowable/stencilset_bpmn.json");
    }

    @At("/models/?/editor/json")
    @Ok("json")
    @GET
    public NutMap getEditorJson(String modelId) {
        NutMap modelNode = new NutMap();
        Model model = repositoryService.getModel(modelId);
        if (model != null) {
            try {
                if (Strings.isNotBlank(model.getMetaInfo())) {
                    modelNode = Json.fromJson(NutMap.class, model.getMetaInfo());
                } else {
                    modelNode = new NutMap();
                    modelNode.put(ModelDataJsonConstants.MODEL_NAME, model.getName());
                }
                modelNode.put(ModelDataJsonConstants.MODEL_ID, model.getId());
                byte[] bytes = repositoryService.getModelEditorSource(model.getId());
                modelNode.put("model", NutMap.WRAP(new String(bytes)));
            } catch (Exception e) {
                log.error("创建JSON模型时出错", e);
                throw new FlowableException("创建JSON模型时出错", e);
            }
        }
        return modelNode;
    }

    @At("/models/?/save")
    @POST
    @Ok("json")
    public AjaxResult saveModel(String modelId, @Param("name") String name, @Param("key") String key, @Param("description") String description, @Param("json_xml") String json) {
        try {
            Model model = repositoryService.getModel(modelId);
            Model oldModel = repositoryService.createModelQuery().modelKey(key).singleResult();
            if (oldModel != null && !oldModel.getId().equals(model.getId())) {
                return AjaxResult.error("Key 已经存在！请修改！");
            }
            NutMap modelJson = Json.fromJson(NutMap.class, model.getMetaInfo());
            NutMap bpmJson = Json.fromJson(NutMap.class, json);
            NutMap propertiesNode = bpmJson.getAs("properties", NutMap.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            BpmnModel bpmnModel = customBpmnJsonConverter.convertToBpmnModel(jsonNode);
            if (bpmnModel.getMainProcess() != null) {
                ValuedDataObject valuedDataObject = bpmnModel.getMainProcess().getDataObjects().stream().filter(va -> FlowConstant.PROCESS_TITLE.equals(va.getName())).findAny().orElse(null);
                if (valuedDataObject == null) {
                    return AjaxResult.errorf("保存失败!流程应该设置标题模版，id,name为{0}", FlowConstant.PROCESS_TITLE);
                }
            }
            ProcessValidator validator = new CustomProcessValidatorFactory().createDefaultProcessValidator();
            List<ValidationError> errors = validator.validate(bpmnModel);
            if (errors.size() > 0) {
                ValidationError error = errors.get(0);
                return AjaxResult.errorf("{1} ：[{0}] ", error.getActivityName(), error.getDefaultDescription());
            }
            propertiesNode.put(StencilConstants.PROPERTY_PROCESS_ID, key);
            modelJson.put(ModelDataJsonConstants.MODEL_NAME, Strings.sNull(name));
            modelJson.put("key", Strings.sNull(key));
            modelJson.put(ModelDataJsonConstants.MODEL_DESCRIPTION, Strings.sNull(description));
            model.setMetaInfo(Json.toJson(modelJson, JsonFormat.compact()));
            model.setName(Strings.sNull(name));
            model.setKey(Strings.sNull(key));
            model.setVersion(model.getVersion() + 1);
            //保存前将设计器传回的json转化一下,设计器上的json有些问题
            jsonNode = customBpmnJsonConverter.convertToJson(bpmnModel);
            repositoryService.saveModel(model);
            repositoryService.addModelEditorSource(model.getId(), jsonNode.toString().getBytes(StandardCharsets.UTF_8));
            return AjaxResult.sucessMsg("保存成功！");
        } catch (Exception e) {
            log.error("模型保存失败", e);
            return AjaxResult.errorf("模型保存失败:{0}", e.getLocalizedMessage());
        }
    }

    @At("/models/validate")
    @POST
    @Ok("json")
    @AdaptBy(type = JsonAdaptor.class)
    public List<ValidationError> validate(@Param("..") HashMap body) {
        List<ValidationError> errors = new ArrayList<>();
        try {
            ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(new StringReader(Json.toJson(body, JsonFormat.compact())));
            BpmnModel bpmnModel = customBpmnJsonConverter.convertToBpmnModel(modelNode);
            ProcessValidator validator = new CustomProcessValidatorFactory().createDefaultProcessValidator();
            errors = validator.validate(bpmnModel);
        } catch (IOException e) {
            log.error(e);
        }
        return errors;
    }

    /**
     * 此方法已经被自定义流程审核范围覆盖
     *
     * @param filter
     * @return
     */
    @At("/editor-users")
    @Ok("json")
    public ResultListDataRepresentation getUsers(@Param("filter") String filter) {
        if (Strings.isNotBlank(filter)) {
            List<UserAccount> userAccountList = userAccountService.findUsersByNameFilter(filter);
            List<? extends User> matchingUsers = FlowUtils.toFlowableUserList(userAccountList);
            List<UserRepresentation> userRepresentations = new ArrayList<>(matchingUsers.size());
            for (User user : matchingUsers) {
                userRepresentations.add(new UserRepresentation(user));
            }
            return new ResultListDataRepresentation(userRepresentations);
        } else {
            return new ResultListDataRepresentation();
        }
    }

    /**
     * 此方法已经被自定义流程审核范围覆盖
     *
     * @param filter
     * @return
     */
    @At("/editor-groups")
    @Ok("json")
    public ResultListDataRepresentation getGroups(@Param("filter") String filter) {
        if (Strings.isNotBlank(filter)) {
            List<Role> roles = roleService.findGroupsByNameFilter(filter);
            List<GroupEntity> result = new ArrayList<>();
            List<? extends GroupEntity> groups = FlowUtils.toFlowableGroupList(roles);
            for (GroupEntity group : groups) {
                result.add(group);
            }
            return new ResultListDataRepresentation(result);
        } else {
            return new ResultListDataRepresentation();
        }
    }

    @At("/reviewerUsers")
    @Ok("json:{actived:'userName|userId|realName'}")
    @POST
    public List<UserAccount> reviewerUsers(@Param("filter") String filter) {
        if (Strings.isNotBlank(filter)) {
            return userAccountService.findUsersByNameFilter(filter);
        } else {
            return Arrays.asList();
        }
    }

    @At("/reviewerUserRoles")
    @Ok("json")
    @POST
    public List<Role> reviewerUserRoles(@Param("filter") String filter) {
        if (Strings.isNotBlank(filter)) {
            return roleService.findGroupsByNameFilter(filter);
        } else {
            return Arrays.asList();
        }
    }

    @At("/admin/process-instances/?/model-json")
    @Ok("json")
    @GET
    public NutMap getProcessInstanceModelJSON(String processInstanceId, @Param("processDefinitionId") String processDefinitionId) {
        return flowDiagramUtils.getHistoryProcessInstanceModelJSON(processInstanceId, processDefinitionId);
    }

    @At(value = "/admin/process-instances/?/history-model-json")
    @Ok("json")
    @GET
    public NutMap getHistoryProcessInstanceModelJSON(String processInstanceId, @Param("processDefinitionId") String processDefinitionId) {
        return flowDiagramUtils.getHistoryProcessInstanceModelJSON(processInstanceId, processDefinitionId);
    }


}
