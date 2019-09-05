package com.nutzfw.modules.sys.service;


import com.nutzfw.core.common.service.BaseService;
import com.nutzfw.modules.sys.entity.Dict;
import org.nutz.lang.util.NutMap;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年06月04日 15时09分41秒
 */
public interface DictService extends BaseService<Dict> {

    Dict insertOrUpdate(Dict detail);

    int delete(Dict detail);

    List<Dict> getCache(String sysCode);

    void sort(NutMap map);

    List<Dict> listAllDictBylikeCode(String sysCode);
}
