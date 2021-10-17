/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.flow.action;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.util.FileUtil;
import com.nutzfw.core.common.util.ViewUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.core.plugin.flowable.constant.FlowConstant;
import com.nutzfw.core.plugin.flowable.converter.CustomBpmnJsonConverter;
import com.nutzfw.core.plugin.flowable.service.FlowCacheService;
import com.nutzfw.core.plugin.flowable.util.FlowUtils;
import com.nutzfw.modules.common.action.BaseAction;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.editor.constants.StencilConstants;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ModelQuery;
import org.flowable.ui.common.service.exception.BadRequestException;
import org.flowable.ui.common.util.XmlUtil;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Encoding;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.flowable.editor.constants.EditorJsonConstants.EDITOR_PROPERTIES_GENERAL_ITEMS;
import static org.flowable.editor.constants.StencilConstants.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/9
 * 流程定义-新增-编辑-导入-部署-删除
 */
@IocBean
@At("/flowable")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class FlowModelAction extends BaseAction {

    @Inject
    RepositoryService repositoryService;

    @Inject
    FlowCacheService flowCacheService;

    CustomBpmnJsonConverter bpmnJsonConverter = new CustomBpmnJsonConverter();
    BpmnXMLConverter bpmnXmlConverter = new BpmnXMLConverter();

    @GET
    @At("/design")
    @Ok("btl:WEB-INF/view/modules/flow/model/design.html")
    public NutMap design() {
        return NutMap.NEW();
    }

    @GET
    @At("/index")
    @Ok("btl:WEB-INF/view/modules/flow/model/index.html")
    @RequiresPermissions("sys.flow.model")
    @AutoCreateMenuAuth(name = "流程模型定义", icon = "fa-tasks", parentPermission = "sys.flow")
    public NutMap index() {
        return NutMap.NEW();
    }

    @POST
    @At("/addModel")
    @Ok("json")
    @RequiresPermissions("sys.flow.model.add")
    @AutoCreateMenuAuth(name = "添加", icon = "fa-tasks", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sys.flow.model")
    public AjaxResult addModel(@Param("categoryId") String categoryId, @Param("name") String name, @Param("key") String key, @Param("description") String description) throws UnsupportedEncodingException {
        Model model = FlowUtils.buildModel(repositoryService.newModel(), categoryId, name, key, description);
        boolean hasOldKey = repositoryService.createModelQuery().modelKey(key).count() > 0;
        if (hasOldKey) {
            return AjaxResult.error("Key 已经存在！请修改！");
        }
        repositoryService.saveModel(model);
        NutMap editorNode = NutMap.NEW();
        editorNode.put("resourceId", model.getId());
        NutMap properties = NutMap.NEW();
        properties.put("process_id", key);
        properties.put("process_namespace", "https://github.com/threefish");
        properties.put("name", name);
        properties.put("documentation", description);
        properties.put(PROPERTY_DATA_PROPERTIES, new NutMap()
                .setv(EDITOR_PROPERTIES_GENERAL_ITEMS, Arrays.asList(new NutMap()
                        .setv(PROPERTY_DATA_ID, FlowConstant.PROCESS_TITLE)
                        .setv(PROPERTY_DATA_NAME, "流程标题")
                        .setv(PROPERTY_DATA_TYPE, "string")
                        .setv("dataproperty_expression", "${user.realName} 发起的" + name + "流程")
                )));
        editorNode.put("properties", properties);
        NutMap stencilSetNode = NutMap.NEW();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);

        repositoryService.addModelEditorSource(model.getId(), Json.toJson(editorNode, JsonFormat.compact()).getBytes(StandardCharsets.UTF_8));
        return AjaxResult.sucess(model.getId());
    }

    @POST
    @At("/listPage")
    @Ok("json:{locked:'metaInfo|originalPersistentState'}")
    @RequiresPermissions("sys.flow.model")
    public LayuiTableDataListVO listPage(HttpServletRequest request, @Param("categoryId") String categoryId) {
        LayuiTableDataListVO vo = LayuiTableDataListVO.get(request);
        ModelQuery modelQuery = repositoryService.createModelQuery().modelCategory(categoryId);
        List<Model> list = modelQuery.orderByCreateTime().desc().listPage(vo.getFirstResult(), vo.getPageSize());
        long total = modelQuery.count();
        return LayuiTableDataListVO.pageByData(list, (int) total);
    }

    @POST
    @At("/delete")
    @Ok("json")
    @RequiresPermissions("sys.flow.model.delete")
    @AutoCreateMenuAuth(name = "删除", icon = "fa-tasks", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sys.flow.model")
    public AjaxResult delete(@Param("modelId") String modelId) {
        try {
            Model modelData = repositoryService.getModel(modelId);
            if (Strings.isNotBlank(modelData.getDeploymentId())) {
                repositoryService.deleteDeployment(modelData.getDeploymentId(), true);
            }
            repositoryService.deleteModel(modelId);
            return AjaxResult.sucess("删除成功，模型ID=" + modelId);
        } catch (Exception e) {
            return AjaxResult.errorf("删除模型失败：modelId={0} \r\n {1}", modelId, e.getMessage());
        }
    }

    @POST
    @At("/category/edit")
    @Ok("json")
    @RequiresPermissions("sys.flow.model.category.edit")
    @AutoCreateMenuAuth(name = "调整流程分类", icon = "fa-tasks", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sys.flow.model")
    public AjaxResult editCategory(@Param("modelId") String modelId, @Param("categoryId") String categoryId) {
        try {
            Model model = repositoryService.getModel(modelId);
            NutMap modelJson = Json.fromJson(NutMap.class, model.getMetaInfo());
            modelJson.put("category", categoryId);
            model.setMetaInfo(Json.toJson(modelJson, JsonFormat.compact()));
            model.setCategory(categoryId);
            repositoryService.saveModel(model);
            return AjaxResult.sucess("修改成功！");
        } catch (Exception e) {
            return AjaxResult.errorf("修改失败！{0}", e.getMessage());
        }
    }

    /**
     * 如果将项目部署至中文目录下，部署流程时会报错的
     *
     * @param modelId
     * @return
     */
    @POST
    @At("/deploy")
    @Ok("json")
    @RequiresPermissions("sys.flow.model.deploy")
    @AutoCreateMenuAuth(name = "部署", icon = "fa-tasks", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sys.flow.model")
    public AjaxResult deploy(@Param("modelId") String modelId) {
        try {
            Model modelData = repositoryService.getModel(modelId);
            ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
            BpmnModel model = new CustomBpmnJsonConverter().convertToBpmnModel(modelNode);
            byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model, Encoding.UTF8);
            String processName = modelData.getName() + ".bpmn20.xml";
            Deployment deployment = repositoryService.createDeployment().name(modelData.getName()).category(modelData.getCategory()).addString(processName, new String(bpmnBytes)).deploy();
            modelData.setDeploymentId(deployment.getId());
            repositoryService.saveModel(modelData);
            flowCacheService.delCache();
            return AjaxResult.sucess("部署成功，部署ID=" + modelId);
        } catch (Exception e) {
            return AjaxResult.errorf("根据模型部署流程失败：modelId={0} \r\n {1}", modelId, e.getMessage());
        }
    }

    @POST
    @Ok("json")
    @At("/import")
    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:upload"})
    @RequiresPermissions("sys.flow.model.import")
    @AutoCreateMenuAuth(name = "导入", icon = "fa-tasks", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sys.flow.model")
    public AjaxResult fileUploadact(@Param("file") TempFile tf, @Param("categoryId") String categoryId) {
        String fileName = tf.getSubmittedFileName();
        boolean isBpmn20xml = fileName != null && (fileName.endsWith(".bpmn") || fileName.endsWith(".bpmn20.xml"));
        if (isBpmn20xml) {
            try {
                XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
                InputStreamReader xmlIn = new InputStreamReader(tf.getInputStream(), StandardCharsets.UTF_8);
                XMLStreamReader xtr = xif.createXMLStreamReader(xmlIn);
                BpmnModel bpmnModel = bpmnXmlConverter.convertToBpmnModel(xtr);
                if (CollectionUtils.isEmpty(bpmnModel.getProcesses())) {
                    return AjaxResult.error("找不到定义的流程 " + fileName);
                }
                if (bpmnModel.getLocationMap().size() == 0) {
                    BpmnAutoLayout bpmnLayout = new BpmnAutoLayout(bpmnModel);
                    bpmnLayout.execute();
                }
                ObjectNode modelNode = bpmnJsonConverter.convertToJson(bpmnModel);

                org.flowable.bpmn.model.Process process = bpmnModel.getMainProcess();
                String key = process.getId();
                String name = process.getName();
                String description = process.getDocumentation();
                if (Strings.isBlank(categoryId)) {
                    categoryId = FlowConstant.DEFAULT_CATEGORY;
                }
                if (repositoryService.createModelQuery().modelKey(key).count() > 0) {
                    key = R.UU16();
                    ObjectNode propertiesNode = (ObjectNode) modelNode.get("properties");
                    propertiesNode.put(StencilConstants.PROPERTY_PROCESS_ID, key);
                }
                Model model = FlowUtils.buildModel(repositoryService.newModel(), categoryId, name, key, description);
                repositoryService.saveModel(model);
                repositoryService.addModelEditorSource(model.getId(), modelNode.toString().getBytes(StandardCharsets.UTF_8));
                return AjaxResult.sucess();
            } catch (BadRequestException e) {
                throw e;
            } catch (Exception e) {
                log.errorf("导入失败 {0}", fileName, e);
                return AjaxResult.error("导入失败 " + fileName + ", 错误信息 " + e.getMessage());
            }
        } else {
            return AjaxResult.error("文件名无效，仅支持.bpmn和.bpmn20.xml文件 " + fileName);
        }

    }

    @At("/export/?")
    @GET
    @Ok("raw")
    @RequiresPermissions("sys.flow.model.export")
    @AutoCreateMenuAuth(name = "导出", icon = "fa-tasks", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sys.flow.model")
    public Object export(String modelId, HttpServletResponse response) {
        try {
            Model modelData = repositoryService.getModel(modelId);
            CustomBpmnJsonConverter jsonConverter = new CustomBpmnJsonConverter();
            JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
            BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
            byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);
            Path file = FileUtil.createTempFile();
            Files.write(file, bpmnBytes);
            String filename = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            return file.toFile();
        } catch (Exception e) {
            log.error("导出model的xml文件失败：modelId=" + modelId, e);
            return ViewUtil.toErrorPage("导出model的xml文件失败：modelId={0} {1}", modelId, e.getLocalizedMessage());
        }
    }
}
