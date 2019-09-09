package com.nutzfw.modules.sys.action;

import com.nutzfw.core.common.annotation.AutoCreateMenuAuth;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.modules.common.action.BaseAction;
import com.nutzfw.modules.sys.biz.DictBiz;
import com.nutzfw.modules.sys.entity.Dict;
import com.nutzfw.modules.sys.service.DictService;
import com.nutzfw.modules.sys.service.TableFieldsService;
import com.nutzfw.modules.sys.vo.DictVO;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;
import org.nutz.plugins.validation.Errors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/4
 * 描述此类：
 */
@IocBean
@At("/sysDict")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class DictAction extends BaseAction {

    @Inject
    DictService dictService;


    @Inject
    TableFieldsService tableFieldsService;
    @Inject
    DictBiz            dictBiz;

    @At("/manager")
    @Ok("btl:WEB-INF/view/sys/dict/manager.html")
    @RequiresPermissions("sysDict.manager")
    @AutoCreateMenuAuth(name = "数据字典", icon = "fa-cogs", parentPermission = "sys.index")
    public NutMap details() {
        return NutMap.NEW();
    }

    @At("/saveOrUpdate")
    @POST
    @Ok("json")
    @RequiresPermissions("sysDict.saveOrUpdate")
    @AutoCreateMenuAuth(name = "新增编辑", icon = "fa-cogs", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysDict.manager")
    public AjaxResult saveOrUpdate(@Param("::data.") Dict detail, Errors errors) {
        if (errors.hasError()) {
            return AjaxResult.error(errors.getErrorsList().iterator().next());
        }
        try {
            detail.setSysCode(detail.getSysCode().toLowerCase());
            Cnd cnd0 = Cnd.where("sysCode", "=", detail.getSysCode()).and("grouping", "=", true);
            Dict dictDetail = dictService.fetch(cnd0);
            if (dictDetail != null && !dictDetail.isEdit()) {
                return AjaxResult.error("系统程序使用，不允许修改！");
            }
            if (dictDetail != null && dictDetail.getId() != detail.getId() && detail.isGrouping()) {
                return AjaxResult.error("系统唯一编码不允许重复");
            }
            if (detail.getPid() <= 0 && !detail.isGrouping()) {
                return AjaxResult.error("请先设置字典分组");
            }
            if (detail.getPid() != 0 && !detail.isGrouping()) {
                //是字典项-需要继承父节点的syscode
                Dict parentDict = dictService.fetch(detail.getPid());
                if (parentDict != null) {
                    detail.setSysCode(parentDict.getSysCode());
                    if (!parentDict.isEdit()) {
                        detail.setEdit(false);
                    }
                }
            }
            Cnd cnd1 = Cnd.where("lable", "=", detail.getLable()).and("sysCode", "=", detail.getSysCode()).and("grouping", "=", false);
            if (detail.getId() != 0) {
                cnd1.and("id", "!=", detail.getId());
            }
            if (dictService.count(cnd1) > 0) {
                return AjaxResult.error("字典名称已存在");
            }
            detail.setEdit(true);
            dictService.insertOrUpdate(detail);
            return AjaxResult.sucess(detail.getId(), "操作成功");
        } catch (Exception e) {
            log.error(e);
        }
        return AjaxResult.error("操作失败");
    }

    @At("/del")
    @POST
    @Ok("json")
    @RequiresPermissions("sysDict.del")
    @AutoCreateMenuAuth(name = "删除", icon = "fa-cogs", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysDict.manager")
    public AjaxResult del(@Param("id") int id) {
        try {
            Dict dictDetail = dictService.fetch(id);
            if (dictDetail.isInternal() || !dictDetail.isEdit()) {
                return AjaxResult.error("系统内置字典不允许删除！");
            }
            int count = tableFieldsService.count(Cnd.where("dictSysCode", "=", dictDetail.getSysCode()));
            if (count > 0) {
                return AjaxResult.error("在表单中已经使用了该字典！不能删除！如需删除，请先清除表单中的字典引用！");
            }
            int childsCount = dictService.count(Cnd.where("pid", "=", dictDetail.getId()));
            if (childsCount > 0) {
                return AjaxResult.error("字典还存在下级节点，无法进行删除！");
            }
            dictService.delete(id);
            return AjaxResult.sucess(dictDetail.getPid(), "操作成功！");
        } catch (Exception e) {
            log.error(e);
            return AjaxResult.error("操作失败!" + e.getLocalizedMessage());
        }

    }

    /**
     * 取得字典名称
     *
     * @param sysCode
     * @param values  在多选情况下数据是数组节点ids[]，单选情况是ids
     * @return
     */
    @Ok("json:{ignoreNull:false,locked:'createTime|updateTime'}")
    @POST
    @At("/getDictName")
    public String getDictName(@Param("sysCode") String sysCode, @Param("ids") String[] values, @Param("ids[]") String[] values1, @Param("defaualtValueField") String defaualtValueField) {
        if (Strings.isBlank(defaualtValueField)) {
            defaualtValueField = "id";
        }
        HashMap<String, String> hashMap = dictBiz.getDictEnumsByDefaualtValueField(sysCode, defaualtValueField);
        List<String> names = new ArrayList<>();
        List<String> vals = new ArrayList<>();
        if (values != null) {
            vals.addAll(Arrays.asList(values));
        } else if (values1 != null) {
            vals.addAll(Arrays.asList(values1));
        }
        for (String val : vals) {
            if (Strings.isNotBlank(val)) {
                names.add(hashMap.get(val));
            }
        }
        return Strings.join(",", names);
    }

    /**
     * 同步加载syscode全部字典
     *
     * @param sysCode
     * @return
     */
    @Ok("json:{ignoreNull:false,locked:'createTime|updateTime'}")
    @POST
    @At("/tree")
    public List<DictVO> tree(@Param("sysCode") String sysCode) {
        List<Dict> dictDetails = dictService.getCache(Strings.sNull(sysCode));
        return dictDetails.stream().map(dict -> DictVO.create(dict, dictBiz.hasChilds(dict))).collect(Collectors.toList());
    }

    /**
     * 异步加载
     *
     * @param pid
     * @return
     */
    @Ok("json:{ignoreNull:false,locked:'createTime|updateTime'}")
    @POST
    @At("/asynTree")
    public List<DictVO> tree(@Param("id") int pid) {
        Cnd cnd = Cnd.NEW();
        cnd.and("pid", "=", pid);
        cnd.asc("shortNo");
        List<Dict> dictDetails = dictService.query(cnd);
        return dictDetails.stream().map(dict -> DictVO.create(dict, dictBiz.hasChilds(dict))).collect(Collectors.toList());
    }

    /**
     * 全部加载
     *
     * @return
     */
    @Ok("json:{ignoreNull:false,locked:'createTime|updateTime'}")
    @POST
    @At("/all")
    public List<DictVO> all() {
        List<Dict> dictDetails = dictService.query(Cnd.orderBy().asc("shortNo"));
        return dictDetails.stream().map(dict -> DictVO.create(dict, dictBiz.hasChilds(dict))).collect(Collectors.toList());
    }

    @Ok("json")
    @POST
    @At("/sort")
    @RequiresPermissions("sysDict.sort")
    @AutoCreateMenuAuth(name = "拖动排序", icon = "fa-cogs", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysDict.manager")
    public AjaxResult sort(@Param("::") NutMap map) {
        dictService.sort(map);
        return AjaxResult.sucess("操作成功");
    }
}
