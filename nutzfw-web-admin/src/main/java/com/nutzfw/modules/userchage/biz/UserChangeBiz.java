/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.userchage.biz;

import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.organize.entity.UserAccount;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

import java.util.List;

/**
 * @author 叶世游
 * @date 2018/7/10 14:49
 * @description 人员异动
 */
@IocBean
public interface UserChangeBiz {
    /**
     * 查询所有用户
     *
     * @param pageNum
     * @param pageSize
     * @param key
     * @return
     */
    LayuiTableDataListVO listUsersPage(int pageNum, int pageSize, String key);

    /**
     * 查询用户部门和岗位
     *
     * @param userid
     * @return
     */
    AjaxResult userDeptJobInfo(String userid);

    /**
     * 保存人员异动
     *
     * @param data
     * @param sessionUserAccount
     * @return
     */
    AjaxResult saveUserChange(String data, UserAccount sessionUserAccount);

    /**
     * 查询人员异动记录
     *
     * @param pageNum
     * @param pageSize
     * @param key
     * @param review
     * @param changeType
     * @return
     */
    LayuiTableDataListVO listPage(int pageNum, int pageSize, String key, int review, int changeType);

    /**
     * 审核人员异动
     *
     * @param changeId
     * @param review
     * @param reviewOpinion
     * @param sessionUserAccount
     * @return
     */
    AjaxResult review(String changeId, int review, String reviewOpinion, UserAccount sessionUserAccount);

    /**
     * 获取人员历史
     *
     * @param id
     * @return
     */
    List<NutMap> history(String id);

    /**
     * 查询人员是否有未审核的异动
     *
     * @param userid
     * @return
     */
    AjaxResult haveChange(String userid);
}
