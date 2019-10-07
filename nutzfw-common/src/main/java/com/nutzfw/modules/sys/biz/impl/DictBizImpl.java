/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.biz.impl;

import com.nutzfw.modules.sys.biz.DictBiz;
import com.nutzfw.modules.sys.entity.Dict;
import com.nutzfw.modules.sys.entity.TableFields;
import com.nutzfw.modules.sys.service.DictService;
import com.nutzfw.modules.tabledata.enums.DictDepend;
import com.nutzfw.modules.tabledata.vo.DictDependentChangeVO;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/4
 * 描述此类：
 */
@IocBean(name = "dictBiz")
public class DictBizImpl implements DictBiz {

    @Inject
    DictService dictService;


    /**
     * 取得字典类型
     *
     * @param sysCode
     * @return
     */
    @Override
    public Dict getCacheDict(String sysCode) {
        return dictService.fetch(Cnd.where("sysCode", "=", sysCode).and("grouping", "=", true));
    }

    /**
     * 取得字典ID->字典名称
     *
     * @param sysCode
     * @return
     */
    @Override
    public HashMap<Integer, String> getDictEnums(String sysCode) {
        List<Dict> list = listCache(sysCode);
        HashMap data = new HashMap(list.size());
        list.forEach(dictDetail -> data.put(dictDetail.getId(), dictDetail.getLable()));
        return data;
    }

    @Override
    public HashMap<String, String> getDictEnumsByValue(String sysCode) {
        List<Dict> list = listCache(sysCode);
        HashMap data = new HashMap(list.size());
        list.forEach(dictDetail -> data.put(dictDetail.getVal(), dictDetail.getLable()));
        return data;
    }

    @Override
    public HashMap<String, String> getDictEnumsByDefaualtValueField(String sysCode, String defaualtValueField) {
        List<Dict> list = listCache(sysCode);
        List<NutMap> collect = list.stream().map(dict -> Lang.obj2map(dict, NutMap.class)).collect(Collectors.toList());
        HashMap data = new HashMap(list.size());
        collect.forEach(dictDetail -> data.put(dictDetail.getString(defaualtValueField), dictDetail.getString("lable")));
        return data;
    }

    /**
     * 通过ID取得字典
     *
     * @param dictId
     * @param sysCode
     * @return
     */
    @Override
    public Dict getCacheDict(int dictId, String sysCode) {
        return listCache(sysCode).stream().filter(d -> d.getId() == dictId).findFirst().orElse(null);
    }

    /**
     * 通过val取得字典
     *
     * @param sysCode
     * @param value
     * @return
     */
    @Override
    public Dict getCacheDict(String sysCode, String value) {
        return listCache(sysCode).stream().filter(d -> d.getVal().equals(value)).findFirst().orElse(null);
    }

    @Override
    public Dict getCacheDictByLable(String sysCode, String lable) {
        return listCache(sysCode).stream().filter(d -> d.getLable().equals(lable)).findFirst().orElse(null);
    }

    /**
     * 取得字典名称
     *
     * @param dictIds 1,2,3
     * @param sysCode
     * @return
     */
    @Override
    public String getDictLable(String dictIds, String sysCode) {
        return getDictLable(Strings.splitIgnoreBlank(dictIds), sysCode);
    }

    @Override
    public String getDictLable(int dictId, String sysCode) {
        return getCacheDict(dictId, sysCode).getLable();
    }


    /**
     * 取得字典名称
     *
     * @param dictIds
     * @param sysCode
     * @return
     */
    @Override
    public String getDictLable(int[] dictIds, String sysCode) {
        HashSet<String> names = new HashSet<>();
        HashMap<Integer, String> keyLables = getDictEnums(sysCode);
        for (int dictId : dictIds) {
            if (dictId != 0) {
                names.add(keyLables.get(dictId));
            }
        }
        return Strings.join(",", names);
    }

    /**
     * 取得字典名称
     *
     * @param dictIds
     * @param sysCode
     * @return
     */
    @Override
    public String getDictLable(String[] dictIds, String sysCode) {
        HashSet<String> names = new HashSet<>();
        HashMap<Integer, String> keyLables = getDictEnums(sysCode);
        for (String dictId : dictIds) {
            if (Strings.isNotBlank(dictId)) {
                names.add(keyLables.get(Integer.parseInt(dictId)));
            }
        }
        return Strings.join(",", names);
    }

    @Override
    public String getDictEnumsJson(String sysCode) {
        return Json.toJson(getDictEnums(sysCode), JsonFormat.compact());
    }

    @Override
    public String getDictByValueEnumsJson(String sysCode) {
        return Json.toJson(getDictEnumsByValue(sysCode), JsonFormat.compact());
    }

    @Override
    public String getDictEnumsByDefaualtValueFieldJson(String sysCode, String defaualtValueField) {
        return Json.toJson(getDictEnumsByDefaualtValueField(sysCode, defaualtValueField), JsonFormat.compact());
    }

    /**
     * 取得字典列表
     *
     * @param sysCode
     * @return
     */
    @Override
    public List<Dict> listCache(String sysCode) {
        return dictService.getCache(sysCode);
    }

    @Override
    public List<DictDependentChangeVO> dictDependentChangeList(int selectDictId, String sysCode, List<TableFields> dictDependFieldIdList) {
        List<DictDependentChangeVO> list = new ArrayList<>();
        if (selectDictId == 0) {
            return list;
        }
        Dict dict = getCacheDict(selectDictId, sysCode);
        for (TableFields fields : dictDependFieldIdList) {
            String val = "";
            if (fields.getDictDepend() == DictDepend.Name.getValue()) {
                val = dict.getLable();
            } else if (fields.getDictDepend() == DictDepend.keyValue.getValue()) {
                val = dict.getVal();
            } else if (fields.getDictDepend() == DictDepend.extra1.getValue()) {
                val = dict.getExtra1();
            } else if (fields.getDictDepend() == DictDepend.extra2.getValue()) {
                val = dict.getExtra2();
            } else if (fields.getDictDepend() == DictDepend.extra3.getValue()) {
                val = dict.getExtra3();
            } else if (fields.getDictDepend() == DictDepend.extra4.getValue()) {
                val = dict.getExtra4();
            } else if (fields.getDictDepend() == DictDepend.extra5.getValue()) {
                val = dict.getExtra5();
            } else if (fields.getDictDepend() == DictDepend.extra6.getValue()) {
                val = dict.getExtra6();
            } else if (fields.getDictDepend() == DictDepend.extra7.getValue()) {
                val = dict.getExtra7();
            } else if (fields.getDictDepend() == DictDepend.extra8.getValue()) {
                val = dict.getExtra8();
            } else if (fields.getDictDepend() == DictDepend.extra9.getValue()) {
                val = dict.getExtra9();
            } else if (fields.getDictDepend() == DictDepend.extra10.getValue()) {
                val = dict.getExtra10();
            }
            list.add(new DictDependentChangeVO("fromData." + fields.getFieldName(), val));
        }
        return list;
    }

    @Override
    public Dict addDict(Dict dict) {
        return dictService.insertOrUpdate(dict);
    }

    @Override
    public boolean hasChilds(Dict dict) {
        if (dict.isGrouping()) {
            return dictService.count(Cnd.where("pid", "=", dict.getId())) > 0;
        } else {
            return false;
        }

    }
}
