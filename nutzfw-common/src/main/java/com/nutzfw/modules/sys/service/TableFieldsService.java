package com.nutzfw.modules.sys.service;


import com.nutzfw.core.common.service.BaseService;
import com.nutzfw.modules.sys.entity.TableFields;
import org.nutz.lang.util.NutMap;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年06月05日 20时33分41秒
 */
public interface TableFieldsService extends BaseService<TableFields> {
    List<TableFields> fetchAuthFields(String fetchAuthFields, NutMap setv);
}
