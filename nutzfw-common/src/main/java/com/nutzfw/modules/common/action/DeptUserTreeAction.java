/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.common.action;

import com.github.threefish.nutz.dto.PageDataDTO;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.filter.CheckRoleAndSession;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.enums.LeaderTypeEnum;
import com.nutzfw.modules.organize.service.DepartmentLeaderService;
import com.nutzfw.modules.organize.service.DepartmentService;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.organize.vo.DeptJobTreeVO;
import com.nutzfw.modules.sys.biz.DictBiz;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/15
 * 用户选择器-部门用户树
 */
@IocBean
@At("/selectUser")
@Filters(@By(type = CheckRoleAndSession.class, args = {Cons.SESSION_USER_KEY, Cons.SESSION_USER_ROLE}))
public class DeptUserTreeAction {

    @Inject
    DepartmentService departmentService;

    @Inject
    UserAccountService userAccountService;

    @Inject
    DictBiz dictBiz;

    @Inject
    DepartmentLeaderService departmentLeaderService;

    @GET
    @At("/index")
    @Ok("btl:WEB-INF/view/tool/deptUser/deptUserTree.html")
    public void indexPage() {
    }

    @Ok("json:{locked:'opAt|opBy'}")
    @POST
    @At("/dept/treeAboutCount")
    public List<DeptJobTreeVO> treeAboutCount() {
        return departmentService.treeAboutCount();
    }

    @Ok("json:{locked:'opAt|opBy'}")
    @POST
    @At("/dept/users")
    public List<NutMap> deptUsers(String deptId) {
        HashMap<Integer, String> userSex = dictBiz.getDictEnums("sys_user_sex");
        List<NutMap> list = departmentService.listUserInfo(deptId);
        list.stream().forEach(nutMap ->
                nutMap.setv("sex", userSex.get(nutMap.getInt("gender")))
        );
        return list;
    }

    @Ok("json:{locked:'opAt|opBy'}")
    @POST
    @At("/dept/deptLeaderUsers")
    public List<NutMap> deptLeaderUsers(String leaderType, @Attr(Cons.SESSION_USER_KEY) UserAccount userAccount) {
        HashMap<Integer, String> userSex = dictBiz.getDictEnums("sys_user_sex");
        List<NutMap> list = departmentService.listUserInfo(departmentLeaderService.queryUserNames(userAccount.getDeptId(), LeaderTypeEnum.valueOf(leaderType)));
        list.stream().forEach(nutMap ->
                nutMap.setv("sex", userSex.get(nutMap.getInt("gender")))
        );
        return list;
    }


    @Ok("json:{locked:'opAt|opBy'}")
    @POST
    @At("/users")
    public LayuiTableDataListVO users(String query, int pageNum, int pageSize) {
        HashMap<Integer, String> userSex = dictBiz.getDictEnums("sys_user_sex");
        PageDataDTO pageDataDTO = departmentService.queryListUserInfo(query, pageNum, pageSize);
        List<NutMap> list = pageDataDTO.getData();
        list.stream().forEach(nutMap -> nutMap.setv("sex", userSex.get(nutMap.getInt("gender"))));
        return LayuiTableDataListVO.pageByData(list, pageDataDTO.getCount());
    }
}
