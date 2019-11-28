/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.flow.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.entity.BaseTreeEntity;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.util.TreeUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.plugin.flowable.constant.FlowConstant;
import com.nutzfw.modules.flow.entity.FlowType;
import com.nutzfw.modules.flow.service.FlowTypeService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ModelQuery;
import org.nutz.aop.interceptor.ioc.TransAop;
import org.nutz.dao.Cnd;
import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import org.nutz.plugins.validation.Errors;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2019年04月15日
 * 流程分类管理
 */
@IocBean
@At("/FlowType")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class FlowTypeAction {

    final static Log log = Logs.get();

    @Inject
    FlowTypeService   flowTypeService;
    @Inject
    RepositoryService repositoryService;

    @At("/tree")
    @Ok("json")
    public List<BaseTreeEntity> flowTypes() {
        List<FlowType> types = flowTypeService.query(Cnd.orderBy().asc("shortNo"));
        FlowType root = FlowType.builder().name("根节点").virtualNode(true).build();
        root.setId("0");
        root.setPid("");
        FlowType defaultType = FlowType.builder().name("未分类").virtualNode(true).build();
        defaultType.setId(FlowConstant.DEFAULT_CATEGORY);
        defaultType.setPid("0");
        defaultType.setPName("根节点");

        types.add(root);
        types.add(defaultType);
        return TreeUtil.createTree(types, "");
    }

    /**
     * 批量删除
     *
     * @param categoryId
     * @return
     */
    @At("/del")
    @Ok("json")
    @RequiresPermissions("FlowType.index.del")
    @AutoCreateMenuAuth(name = "删除分类", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-cogs", parentPermission = "sys.flow.model")
    public AjaxResult del(@Param("id") String categoryId) {
        long count = flowTypeService.count(Cnd.where("pid", "=", categoryId));
        if (count > 0) {
            return AjaxResult.error("当前分类下还有其他分类，无法删除！");
        }
        ModelQuery modelQuery = repositoryService.createModelQuery().modelCategory(categoryId);
        if (modelQuery.count() > 0) {
            return AjaxResult.error("当前分类下还有流程模型，无法删除！");
        }
        flowTypeService.delete(categoryId);
        return AjaxResult.sucess("删除成功");
    }

    /**
     * 保存
     *
     * @param data
     * @return
     */
    @At("/save")
    @Ok("json")
    @POST
    @RequiresPermissions("FlowType.index.edit")
    @Aop(TransAop.READ_UNCOMMITTED)
    @AutoCreateMenuAuth(name = "新增、修改分类", type = AutoCreateMenuAuth.RESOURCE, icon = "fa-cogs", parentPermission = "sys.flow.model")
    public AjaxResult save(@Param("::") FlowType data, Errors errors) {
        if (errors.hasError()) {
            return AjaxResult.error(errors.getErrorsList().iterator().next());
        }
        Cnd checkCnd = Cnd.NEW();
        boolean update = Strings.isNotBlank(data.getId());
        if (update) {
            checkCnd.and("name", "=", data.getName()).and("id", "!=", data.getId());
        } else {
            checkCnd.and("name", "=", data.getName());
        }
        int count = flowTypeService.count(checkCnd);
        if (count > 0) {
            return AjaxResult.errorf("【{0}】名称已经存在!", data.getName());
        } else {
            if (update) {
                flowTypeService.updateIgnoreNull(data);
            } else {
                flowTypeService.insert(data);
            }
            return AjaxResult.sucessMsg("保存成功");
        }
    }
}
