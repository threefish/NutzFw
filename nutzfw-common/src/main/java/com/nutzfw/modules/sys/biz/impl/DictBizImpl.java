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
import org.nutz.lang.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
    public Dict getDict(String sysCode) {
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
        List<Dict> list = list(sysCode);
        HashMap data = new HashMap(list.size());
        list.forEach(dictDetail -> data.put(dictDetail.getId(), dictDetail.getLable()));
        return data;
    }

    @Override
    public HashMap<String, String> getDictEnumsByValue(String sysCode) {
        List<Dict> list = list(sysCode);
        HashMap data = new HashMap(list.size());
        list.forEach(dictDetail -> data.put(dictDetail.getVal(), dictDetail.getLable()));
        return data;
    }

    /**
     * 取得字典
     *
     * @param dictId
     * @param sysCode
     * @return
     */
    @Override
    public Dict getDict(int dictId, String sysCode) {
        List<Dict> list = list(sysCode);
        return list.stream().filter(d -> d.getId() == dictId).findFirst().orElse(null);
    }

    @Override
    public Dict getDict(String sysCode, String value) {
        List<Dict> list = list(sysCode);
        return list.stream().filter(d -> d.getVal().equals(value)).findFirst().orElse(null);
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
        return getDict(dictId, sysCode).getLable();
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

    /**
     * 取得字典列表
     *
     * @param sysCode
     * @return
     */
    @Override
    public List<Dict> list(String sysCode) {
        return dictService.getCache(sysCode);
    }

    @Override
    public List<DictDependentChangeVO> dictDependentChangeList(int selectDictId, String sysCode, List<TableFields> dictDependFieldIdList) {
        List<DictDependentChangeVO> list = new ArrayList<>();
        if (selectDictId == 0) {
            return list;
        }
        Dict dict = getDict(selectDictId, sysCode);
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
        dict = dictService.insert(dict);
        dictService.updateCache(dict.getSysCode());
        return dict;
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
