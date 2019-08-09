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

    Dict getDict(String sysCode);

    HashMap<Integer, String> getDictEnums(String sysCode);

    HashMap<String, String> getDictEnumsByValue(String sysCode);

    Dict getDict(int dictId, String sysCode);

    Dict getDict(String sysCode, String value);

    String getDictLable(String dictIds, String sysCode);

    String getDictLable(int dictId, String sysCode);

    String getDictLable(int[] dictIds, String sysCode);

    String getDictLable(String[] dictIds, String sysCode);

    String getDictEnumsJson(String sysCode);

    String getDictByValueEnumsJson(String sysCode);

    List<Dict> list(String sysCode);

    List<DictDependentChangeVO> dictDependentChangeList(int selectDictId, String sysCode, List<TableFields> dictDependFieldIdList);

    Dict addDict(Dict dict);

    boolean hasChilds(Dict dict);
}
