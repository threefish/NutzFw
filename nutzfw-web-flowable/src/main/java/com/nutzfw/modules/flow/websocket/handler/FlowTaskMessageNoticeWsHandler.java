/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.flow.websocket.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.plugin.flowable.constant.FlowConstant;
import com.nutzfw.modules.flow.vo.TaskNoticeVO;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.sys.entity.Role;
import org.flowable.engine.TaskService;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.plugins.mvc.websocket.handler.AbstractWsHandler;

import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/30
 */
public class FlowTaskMessageNoticeWsHandler extends AbstractWsHandler implements MessageHandler.Whole<String> {

    TaskService taskService;
    /**
     * 用户名
     *
     * @return
     */
    private String       userName;
    /**
     * 用户拥有的角色
     *
     * @return
     */
    private List<String> roleCode;

    public FlowTaskMessageNoticeWsHandler(TaskService taskService) {
        super("wsroom:");
        this.taskService = taskService;
    }

    public String getUserName() {
        return userName;
    }

    public Session getSession() {
        return this.session;
    }

    @Override
    public void init() {
        super.init();
        if (roleCode == null) {
            List<Role> roles = (List<Role>) httpSession.getAttribute(Cons.SESSION_ROLES_KEY);
            Set<String> codes = Sets.newHashSet();
            roles.forEach(role -> codes.add(role.getRoleCode()));
            this.roleCode = Lists.newArrayList(codes);
        }
        if (userName == null) {
            UserAccount userAccount = (UserAccount) httpSession.getAttribute(Cons.SESSION_USER_KEY);
            this.userName = userAccount.getUserName();
        }
    }

    /**
     * 主动告知客户端有未读消息
     */
    public void sendHasNotice() {
        endpoint.sendJson(session.getId(), NutMap.NEW().setv("type", "hasTaskMessageNotice"));
    }

    public void queryTaskNotice(NutMap req) {
        TaskQuery query = taskService.createTaskQuery().includeProcessVariables().active()
                .or()
                .taskAssignee(userName)
                .taskCandidateUser(userName)
                .taskCandidateGroupIn(roleCode)
                .endOr()
                .orderByTaskCreateTime().desc();
        List<Task> list = query.list();
        List<TaskNoticeVO> taskNotices = new ArrayList<>();
        list.forEach(task -> {
            taskNotices.add(TaskNoticeVO.builder()
                    .title(task.getName())
                    .content(Strings.sNull(task.getProcessVariables().get(FlowConstant.PROCESS_TITLE)))
                    .assignee(task.getAssignee())
                    .todo(task.getAssignee() != null)
                    .claim(task.getAssignee() == null)
                    .delegate(task.getDelegationState() == DelegationState.PENDING)
                    .createTime(task.getCreateTime())
                    .build());
        });
        try {
            endpoint.sendJson(session.getId(), taskNotices);
        } catch (Exception e) {
        }
    }

}
