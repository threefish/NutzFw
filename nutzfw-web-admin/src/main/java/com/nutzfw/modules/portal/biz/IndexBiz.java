/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.portal.biz;
/**
 * Created by lgk on 2018/6/20.
 * 描述:
 */

import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.portal.entity.MsgNotice;
import com.nutzfw.modules.sys.entity.Dict;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * @author 叶世游
 * @date 2018/6/20 14:49
 * @description 首页 展示
 */
@IocBean
public interface IndexBiz {
    /**
     * 获取用户信息
     *
     * @return
     */
    UserAccount getUserInfo(String userid);

    /**
     * 获取当前用户可视的消息提醒
     *
     * @param account
     */
    AjaxResult msgNotices(UserAccount account);

    /**
     * 获取当前用户可视的统计信息
     *
     * @param account
     * @return
     */
    AjaxResult statisticsConfigures(UserAccount account);

    /**
     * 获取当前用户可视的快捷操作
     *
     * @param account
     * @return
     */
    AjaxResult quickLinks(UserAccount account);

    /**
     * 获取消息数据列表
     *
     * @param mid
     * @param pageNum
     * @param pageSize
     * @return
     */
    LayuiTableDataListVO msgDetail(String mid, int pageNum, int pageSize);

    /**
     * 获取消息信息
     *
     * @param mid
     * @return
     */
    MsgNotice getMsgNotice(String mid);

    /**
     * 修改用户信息
     *
     * @param account
     */
    void updateUser(UserAccount account);

    /**
     * 获取字典信息
     *
     * @param gender
     * @param sys_user_sex
     * @return
     */
    Dict getDict(int gender, String sys_user_sex);
}
