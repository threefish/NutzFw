package com.nutzfw.modules.flow.service.impl;

import com.github.threefish.nutz.sqltpl.ISqlDaoExecuteService;
import com.github.threefish.nutz.sqltpl.SqlsTplHolder;
import com.github.threefish.nutz.sqltpl.SqlsXml;
import com.google.common.collect.Lists;
import com.nutzfw.core.plugin.flowable.constant.FlowConstant;
import com.nutzfw.core.plugin.flowable.dto.FlowSubmitInfoDTO;
import com.nutzfw.modules.flow.service.FlowCustomQueryService;
import org.apache.commons.collections.CollectionUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.Entity;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;

import java.util.List;
import java.util.Objects;

/**
 * @author huchuc@vip.qq.com
 * @date: 2019/7/11
 */
@SqlsXml
@IocBean
public class FlowCustomQueryServiceImpl implements FlowCustomQueryService, ISqlDaoExecuteService {
    SqlsTplHolder sqlsTplHolder;

    @Inject
    Dao dao;

    @Override
    public FlowSubmitInfoDTO getFlowSubmitInfo(String taskId) {
        FlowSubmitInfoDTO dto = new FlowSubmitInfoDTO();
        List<NutMap> list = queryMapBySql("getFlowSubmitInfo", NutMap.NEW().setv("taskId", taskId));
        list.forEach(nutMap -> {
            String name = nutMap.getString("name");
            String value = nutMap.getString("val");
            if (Objects.equals(FlowConstant.SUBMITTER, name)) {
                dto.setUserName(value);
            }
            if (Objects.equals(FlowConstant.SUBMITTER_DEPT_ID, name)) {
                dto.setDeptId(value);
            }
        });
        return dto;
    }

    @Override
    public List<NutMap> listUserTaskNodeAllReviewerUser(List<String> candidateUserNames) {
        if (CollectionUtils.isEmpty(candidateUserNames)) {
            return Lists.newArrayList();
        }
        return queryMapBySql("listUserTaskNodeAllReviewerUser", NutMap.NEW(), Cnd.where("userName", "in", candidateUserNames));
    }

    @Override
    public SqlsTplHolder getSqlsTplHolder() {
        return sqlsTplHolder;
    }

    @Override
    public Dao getDao() {
        return dao;
    }

    @Override
    public Entity getEntity() {
        return null;
    }

    @Override
    public Class getEntityClass() {
        return null;
    }
}
