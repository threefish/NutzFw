package com.nutzfw.modules.sys.service;


import com.nutzfw.core.common.service.BaseService;
import com.nutzfw.modules.sys.entity.Menu;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA Code Generator
 *
 * @author 黄川 huchuc@vip.qq.com
 * @date 2018年01月02日 15时39分07秒
 * 功能描述： 菜单管理
 */
public interface MenuService extends BaseService<Menu> {

    List<Menu> querMenusByUserRoles(Set<String> userRoles);
}
