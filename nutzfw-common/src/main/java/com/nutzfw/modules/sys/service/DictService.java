/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.service;


import com.nutzfw.modules.sys.entity.Dict;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.lang.util.NutMap;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年06月04日 15时09分41秒
 */
public interface DictService {

    Dict insertOrUpdate(Dict detail);

    int delete(Dict detail);

    List<Dict> getCache(String sysCode);

    void sort(NutMap map);

    List<Dict> listAllDictBylikeCode(String sysCode);

    Dict fetch(Cnd and);

    int count(Cnd pid);

    List<Dict> query();

    List<Dict> query(Condition shortNo);

    void delete(int id);

    Dict fetch(int id);
}
