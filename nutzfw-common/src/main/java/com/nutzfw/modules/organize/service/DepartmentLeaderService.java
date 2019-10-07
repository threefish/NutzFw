/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.organize.service;

import com.nutzfw.core.common.service.BaseService;
import com.nutzfw.modules.organize.entity.DepartmentLeader;
import com.nutzfw.modules.organize.enums.LeaderTypeEnum;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2019年07月03日
 * 部门领导关联信息
 */
public interface DepartmentLeaderService extends BaseService<DepartmentLeader> {

    /**
     * 取得部门主管列表
     * @param deptId
     * @param typeEnum
     * @return
     */
    List<String> queryUserNames(String deptId, LeaderTypeEnum typeEnum);

    /**
     * 迭代取得上级主管列表
     * 如果上级主管列表没人，再向上一级获取，直到获取到数据或迭代结束
     *
     * @param deptId
     * @param typeEnum
     * @return
     */
    List<String> queryIterationUserNames(String deptId, LeaderTypeEnum typeEnum);
}
