/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.portal.biz;

import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.ZtreeBeanVO;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.List;

/**
 * @author 叶世游
 * @date 2018/6/19 10:05
 * @description 首页配置
 */
@IocBean
public interface PortalBiz {

    /**
     * 获取首页可以选择的功能tree
     *
     * @return
     */
    List<ZtreeBeanVO> tree();

    /**
     * 获取已选中的功能
     *
     * @param id
     * @return
     */
    AjaxResult getSelectedIds(String id);

    /**
     * 保存组和功能之间的关系
     *
     * @param groupId
     * @param ztreeBeans
     * @return
     */
    AjaxResult saveGroupFunction(String groupId, String ztreeBeans);

    /**
     * 获取所有用户树
     *
     * @return
     */
    List<ZtreeBeanVO> userTree();

    /**
     * 保存组和人之间的关系
     *
     * @param groupId
     * @param userIds
     * @return
     */
    AjaxResult savePortalUser(String groupId, String[] userIds);

    /**
     * 获取人和组的关系
     *
     * @param groupId
     * @return
     */
    AjaxResult getSelectedUserIds(String groupId);
}
