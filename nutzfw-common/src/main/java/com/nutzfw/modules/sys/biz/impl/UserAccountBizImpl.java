/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.sys.biz.impl;

import com.github.threefish.nutz.sqltpl.ISqlDaoExecuteService;
import com.github.threefish.nutz.sqltpl.SqlsTplHolder;
import com.github.threefish.nutz.sqltpl.SqlsXml;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.util.FileUtil;
import com.nutzfw.core.common.util.excel.PoiExcelUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.entity.UserAccountRole;
import com.nutzfw.modules.organize.entity.UserImportHistory;
import com.nutzfw.modules.organize.service.*;
import com.nutzfw.modules.organize.thread.CheckUserDataThread;
import com.nutzfw.modules.sys.action.QuartzJobAction;
import com.nutzfw.modules.sys.biz.UserAccountBiz;
import com.nutzfw.modules.sys.service.QuartzJobService;
import net.sf.ehcache.util.NamedThreadFactory;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.trans.Trans;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author panchuang
 * @data 2018/6/14 0014
 */
@IocBean(name = "userAccountBiz")
@SqlsXml("UserAccountBizImpl.xml")
public class UserAccountBizImpl implements UserAccountBiz, ISqlDaoExecuteService {
    private static ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>(100),
            new NamedThreadFactory("用户导入线程", false));
    @Inject
    protected UserAccountService userAccountService;
    @Inject
    Dao dao;
    @Inject
    JobService jobService;
    @Inject
    QuartzJobAction quartzJobAction;
    @Inject
    UserAccountRoleService userAccountRoleService;
    @Inject
    DepartmentJobService departmentJobService;
    @Inject("refer:$ioc")
    Ioc ioc;
    @Inject
    private DepartmentService departmentService;
    @Inject
    private UserImportHistoryService userImportHistoryService;
    @Inject
    private QuartzJobService quartzJobService;
    private SqlsTplHolder sqlsTplHolder;

    /**
     * 用户管理-接收岗位
     * by panc
     *
     * @param query 模糊查询 岗位名称或岗位编码
     * @return
     */
    @Override
    public LayuiTableDataListVO jobSelectList(String query, String deptId, int pageNumber, int pageSize) {
        if (Strings.isBlank(deptId)) {
            return new LayuiTableDataListVO();
        }
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT j.id,j.name,j.code,j.nature,j.category FROM sys_job j WHERE j.delFlag = false");
        sb.append(" AND j.id IN (SELECT dj.job_id FROM sys_department_job dj where dj.dept_id = @deptId ) ");
        if (Strings.isNotBlank(query)) {
            sb.append(" AND (j.name LIKE  @query OR j.code LIKE @query)");
        }
        Sql sql = Sqls.create(sb.toString());
        sql.setCallback(Sqls.callback.records());
        sql.setParam("deptId", deptId);
        sql.setParam("query", "%" + query + "%");
        return jobService.listPage(pageNumber, pageSize, sql);
    }

    @Override
    public LayuiTableDataListVO listPage(int pageNum, int pageSize, String name, int status, int review, String deptId, String jobId, boolean hasRole) {
        List<String> userids = new ArrayList<>();
        if (hasRole) {
            //未授权的人员
            List<UserAccountRole> roles = userAccountRoleService.query(Cnd.where("delFlag", "=", 0));
            if (roles.size() > 0) {
                roles.forEach(r -> userids.add(r.getUserId()));
            }
        }
        NutMap nutMap = new NutMap();
        nutMap.setv("deptId", Strings.sNull(deptId));
        nutMap.setv("name", Strings.sNull(name));
        nutMap.setv("status", status);
        nutMap.setv("review", review);
        nutMap.setv("userids", userids.toArray());
        Sql sql = getSqlsTplHolder().getSql("listPage", nutMap);
        sql.setParam("name", "%" + Strings.sNull(name) + "%");
        sql.setCallback(Sqls.callback.maps());
        long count = Daos.queryCount(dao, sql);
        sql.setPager(new Pager(pageNum, pageSize));
        dao.execute(sql);
        return LayuiTableDataListVO.pageByData(sql.getList(Map.class), (int) count);
    }

    /**
     * 创建导出模板
     *
     * @return
     */
    @Override
    public Path createDownTemplate() throws IOException {
        Path template = FileUtil.createTempFile();
        PoiExcelUtil util = PoiExcelUtil.createNewExcel();
        String sheetName = "用户信息导入";
        List<String[]> strings = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        headers.add("用户名(必填)");
        headers.add("真实姓名(必填)");
        headers.add("部门(必填)");
        headers.add("岗位(必填)");
        headers.add("手机号");
        headers.add("邮箱");
        headers.add("密码");
        //按顺序依次写入
        strings.add(headers.toArray(new String[0]));
        util.createSheet(sheetName);
        //插入表头
        util.insetRowDataList(sheetName, 0, strings);
        //设置列宽
        for (int i = 0, l = headers.size(); i < l; i++) {
            int width = String.valueOf(headers.get(i)).getBytes().length * 256;
            util.setColumnWidth(sheetName, i, width);
        }
        util.setColumnWidth(sheetName, 2, 256 * 50);
        util.setColumnWidth(sheetName, 3, 256 * 50);
        //设置用户名列为文本
        util.setColumnTextFormat(sheetName, 0, 50);
        //冻结表头
        util.createFreezePane(sheetName, 0, 1, 0, 1);
        //以隐藏sheet页方式写下拉字典
        List<Integer> cellRangeAddressLists = new ArrayList<>();
        List<String[]> textlist = new ArrayList<>();
        String[] deptNames = departmentService.allDeptStrs();
        cellRangeAddressLists.add(2);
        textlist.add(deptNames);
        String[] jobNames = jobService.allJobStrs();
        cellRangeAddressLists.add(3);
        textlist.add(jobNames);

        //POI起始位置0 用户名占一个位置fixSize=1 所以开始为fixSize + 1
        util.setSheetValidation(sheetName, cellRangeAddressLists, textlist, false);
        util.toCreateNewFile(template.toFile());
        return template;
    }

    /**
     * 解析上传的文件
     *
     * @param attachId
     * @return
     */
    @Override
    public AjaxResult importUser(String attachId) {
        UserImportHistory history = new UserImportHistory();
        history.setAttachId(attachId);
        history.setConsuming(null);
        history.setStaus(0);
        UserAccount userAccount = (UserAccount) Mvcs.getReq().getSession().getAttribute(Cons.SESSION_USER_KEY);
        history.setUserDesc(userAccount.getRealName());
        history.setUserid(userAccount.getUserid());
        userImportHistoryService.insert(history);
        executorService.submit(new CheckUserDataThread(ioc, history));
        return AjaxResult.sucess("数据开始效验中，效验完成后自动进行导入，详情进入【用户导入历史】中查看，请稍候....");
    }

    /**
     * 保存人员和角色的关系
     *
     * @param userIds
     * @param roleIds
     * @param type
     * @return
     */
    @Override
    public AjaxResult saveUserRole(String userIds, String roleIds, Integer type) {
        String[] userIdArr = userIds.split(",");
        String[] roleIdArr = roleIds.split(",");
        List<UserAccountRole> roles = userAccountRoleService.query(Cnd.where("userId", "in", userIdArr).and("roleId", "in", roleIdArr));
        if (type == 2) {
            userAccountRoleService.delete(roles);
            return AjaxResult.sucess("取消授权成功!");
        }
        List<UserAccountRole> newRoles = new ArrayList<>();
        for (String s : userIdArr) {
            for (String s1 : roleIdArr) {
                UserAccountRole role = new UserAccountRole();
                role.setRoleId(s1);
                role.setUserId(s);
                newRoles.add(role);
            }
        }
        Trans.exec(() -> {
            userAccountRoleService.delete(roles);
            userAccountService.insert(newRoles);
        });
        return AjaxResult.sucess("授权成功!");
    }

    /**
     * 根据岗位获取用户
     *
     * @param deptId
     * @param jobId
     * @param sort
     * @return
     */
    @Override
    public AjaxResult queryByJob(String deptId, String jobId, Integer sort) {
        try {
            //选择部门但是岗位不限的情况
            String sqlstr = "SELECT ua.id,ua.realName,dd.lable AS gender,ua.f_192 AS age,d.`name` AS deptName,j.`name` AS jobName\n" +
                    "FROM sys_user_account ua\n" +
                    "LEFT JOIN sys_dict dd\n" +
                    "ON dd.id = ua.f_188\n" +
                    "LEFT JOIN sys_user_account_job uaj\n" +
                    "ON uaj.user_id = ua.id\n" +
                    "LEFT JOIN sys_job j\n" +
                    "ON j.id = uaj.job_id\n" +
                    "LEFT JOIN sys_department d\n" +
                    "ON ua.deptId = d.id\n" +
                    "WHERE ua.delFlag = 0\n";
            if ("0".equals(jobId) && !"0".equals(deptId)) {
                sqlstr += "AND ua.deptId = @deptId ";
            } else if (!"0".equals(jobId) && !"0".equals(deptId)) {
                sqlstr += "AND ua.deptId = @deptId AND j.id = @jobId ";
            } else if (!"0".equals(jobId) && "0".equals(deptId)) {
                sqlstr += " AND j.id = @jobId ";
            }
            switch (sort) {
                case 1:
                    //男女排序升序
                    sqlstr += "ORDER BY dd.id ASC";
                    break;
                case 2:
                    //男女排序降序序
                    sqlstr += "ORDER BY dd.id DESC";
                    break;
                case 3:
                    //年龄排序升序
                    sqlstr += "ORDER BY age ASC";
                    break;
                case 4:
                    //年龄排序降序
                    sqlstr += "ORDER BY age DESC";
                    break;
                case 5:
                    //岗位排序升序
                    sqlstr += "ORDER BY jobName ASC";
                    break;
                case 6:
                    //岗位排序降序
                    sqlstr += "ORDER BY jobName DESC";
                    break;
                default:
                    break;
            }
            Sql sql = Sqls.create(sqlstr);
            sql.setParam("deptId", deptId);
            sql.setParam("jobId", jobId);
            sql.setCallback(Sqls.callback.maps());
            userAccountService.execute(sql);
            List<NutMap> maps = sql.getList(NutMap.class);
            return AjaxResult.sucess(maps, "获取用户成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("获取用户失败");
        }
    }

    /**
     * 后管通讯录查询用户
     *
     * @param key
     * @param pageNum
     * @param pageSize @return
     */
    @Override
    public List<NutMap> userSearch(String key, int pageNum, int pageSize) {
        String str = "SELECT ua.realName AS `name`,d.`name` AS deptName,ua.phone,ua.mail FROM sys_user_account AS ua\n" +
                "LEFT JOIN sys_department AS d\n" +
                "ON d.id = ua.deptId\n" +
                "WHERE ua.realName LIKE @key\n" +
                "AND ua.delFlag =0";
        Sql sql = Sqls.create(str);
        sql.setParam("key", "%" + key + "%");
        sql.setCallback(Sqls.callback.maps());
        if (pageNum * pageSize != 0) {
            sql.setPager(new Pager(pageNum, pageSize));
        }
        userAccountService.execute(sql);
        return sql.getList(NutMap.class);
    }

    /**
     * 通过部门查询人员
     *
     * @param deptId
     */
    @Override
    public List<NutMap> usersByDeptId(String deptId) {
        String str = "SELECT realName AS `name`,phone,mail FROM sys_user_account WHERE deptId = @deptId AND delFlag =0";
        Sql sql = Sqls.create(str);
        sql.setParam("deptId", deptId);
        sql.setCallback(Sqls.callback.maps());
        userAccountService.execute(sql);
        return sql.getList(NutMap.class);
    }

    @Override
    public List<String> listUserNameByRoleCodes(List<String> roleCodes) {
        return queryStrsBySql("listUserNameByRoleCodes", NutMap.NEW(), Cnd.where("r.locked", "=", false).and("r.role_code", "in", roleCodes));
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
        return dao.getEntity(UserAccount.class);
    }

    @Override
    public Class getEntityClass() {
        return UserAccount.class;
    }
}
