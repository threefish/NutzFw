package com.nutzfw.modules.sys.biz;

import com.nutzfw.modules.sys.entity.Dict;
import com.nutzfw.modules.sys.entity.TableFields;
import com.nutzfw.modules.tabledata.vo.DictDependentChangeVO;

import java.util.HashMap;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/4
 * 描述此类：数据字典取用
 */
public interface DictBiz {
    /**
     * 通过字典code取得字典
     * <p>
     * 字典分组会被过滤
     *
     * @param sysCode
     * @return
     */
    Dict getCacheDict(String sysCode);

    /**
     * 取得字典ID->字典名称
     *
     * @param sysCode
     * @return
     */
    HashMap<Integer, String> getDictEnums(String sysCode);

    /**
     * 取得字典val->字典名称
     *
     * @param sysCode
     * @return
     */
    HashMap<String, String> getDictEnumsByValue(String sysCode);

    /**
     * 取得字典 defaualtValueField  -> 字典名称
     *
     * @param sysCode
     * @param defaualtValueField - 可以是ID,VAL,extra1....extra10
     * @return
     */
    HashMap<String, String> getDictEnumsByDefaualtValueField(String sysCode, String defaualtValueField);

    /**
     * 通过ID取得字典
     *
     * @param dictId
     * @param sysCode
     * @return
     */
    Dict getCacheDict(int dictId, String sysCode);

    /**
     * 通过val取得字典
     *
     * @param sysCode
     * @param value
     * @return
     */
    Dict getCacheDict(String sysCode, String value);

    /**
     * 通过字典ids取得显示文字
     *
     * @param dictIds
     * @param sysCode
     * @return
     */
    String getDictLable(String dictIds, String sysCode);

    /**
     * 通过字典id取得显示文字
     *
     * @param dictId
     * @param sysCode
     * @return
     */
    String getDictLable(int dictId, String sysCode);

    /**
     * 通过字典ids取得显示文字
     *
     * @param dictIds
     * @param sysCode
     * @return
     */
    String getDictLable(int[] dictIds, String sysCode);

    /**
     * 通过字典ids取得显示文字
     *
     * @param dictIds
     * @param sysCode
     * @return
     */
    String getDictLable(String[] dictIds, String sysCode);

    /**
     * 取得字典 id -> json （取得字典ID->字典名称）
     *
     * @param sysCode
     * @return
     */
    String getDictEnumsJson(String sysCode);

    /**
     * 取得字典 val -> json （取得字典val->字典名称）
     *
     * @param sysCode
     * @return
     */
    String getDictByValueEnumsJson(String sysCode);
    /**
     * 取得字典 defaualtValueField  -> json
     *
     * @param sysCode
     * @param defaualtValueField - 可以是ID,VAL,extra1....extra10
     * @return
     */
    String getDictEnumsByDefaualtValueFieldJson(String sysCode, String defaualtValueField);

    /**
     * 取得字典缓存
     * @param sysCode
     * @return
     */
    List<Dict> listCache(String sysCode);

    /**
     * 字典关联查询
     * <p>
     * 如字典附加值关联等
     *
     * @param selectDictId
     * @param sysCode
     * @param dictDependFieldIdList
     * @return
     */
    List<DictDependentChangeVO> dictDependentChangeList(int selectDictId, String sysCode, List<TableFields> dictDependFieldIdList);

    /**
     * 添加字典
     * @param dict
     * @return
     */
    Dict addDict(Dict dict);

    /**
     * 字典分类是否有值
     * @param dict
     * @return
     */
    boolean hasChilds(Dict dict);


}
