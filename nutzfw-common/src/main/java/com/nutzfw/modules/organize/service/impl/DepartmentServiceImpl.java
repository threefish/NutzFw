/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.organize.service.impl;

import com.github.threefish.nutz.dto.PageDataDTO;
import com.github.threefish.nutz.sqltpl.ISqlDaoExecuteService;
import com.github.threefish.nutz.sqltpl.SqlsTplHolder;
import com.github.threefish.nutz.sqltpl.SqlsXml;
import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.service.impl.BaseServiceImpl;
import com.nutzfw.core.common.util.FileUtil;
import com.nutzfw.core.common.util.StringUtil;
import com.nutzfw.core.common.util.excel.PoiExcelUtil;
import com.nutzfw.core.common.vo.AjaxResult;
import com.nutzfw.modules.organize.entity.Department;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.service.DepartmentService;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.organize.vo.DeptJobTreeVO;
import com.nutzfw.modules.organize.vo.DeptSelectVO;
import com.nutzfw.modules.sys.service.FileAttachService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.trans.Trans;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/23
 * 描述此类：
 */
@IocBean(args = {"refer:dao"})
@SqlsXml
public class DepartmentServiceImpl extends BaseServiceImpl<Department> implements DepartmentService, ISqlDaoExecuteService {
    @Inject
    FileAttachService  fileAttachService;
    @Inject
    UserAccountService userAccountService;

    SqlsTplHolder sqlsTplHolder;

    public DepartmentServiceImpl(Dao dao) {
        super(dao);
    }

    /**
     * 获取包含job的tree
     * isStatistics =1
     *
     * @return
     */
    @Override
    public List<DeptJobTreeVO> treeAboutJob() {
        List<DeptJobTreeVO> trees = queryEntityBySql("treeAboutJob", NutMap.NEW(), DeptJobTreeVO.class);
        trees.add(new DeptJobTreeVO("0", "0", Cons.optionsCach.getUnitName(), "dept"));
        trees.forEach((r) -> r.setName("job".equals(r.getType()) ? r.getName() + "(岗位)" : r.getName()));
        return trees;
    }

    /**
     * 获取包含job的tree
     * 忽略 isStatistics
     *
     * @return
     */
    @Override
    public List<DeptJobTreeVO> treeAboutJob2() {
        List<DeptJobTreeVO> trees = queryEntityBySql("treeAboutJob2", NutMap.NEW(), DeptJobTreeVO.class);
        trees.add(new DeptJobTreeVO("0", "0", Cons.optionsCach.getUnitName(), "dept"));
        trees.forEach((r) -> r.setName("job".equals(r.getType()) ? r.getName() + "(岗位)" : r.getName()));
        return trees;
    }

    /**
     * 获取部门岗位树(包含人员数量)
     * 叶世游
     *
     * @return
     */
    @Override
    public List<DeptJobTreeVO> treeAboutJobAndCount() {
        List<DeptJobTreeVO> trees = treeAboutJob();
        convertCount(trees);
        trees.forEach((r) -> r.setName(r.getName() + "(" + r.getCount() + ")"));
        return trees;
    }

    /**
     * 获取所有部门树
     */
    @Override
    public List<Department> tree() {
        List<Department> depts = query(Cnd.orderBy().asc("short_no"));
        Department dept = new Department();
        dept.setId("0");
        dept.setPid("0");
        dept.setName(Cons.optionsCach.getUnitName());
        depts.add(dept);
        return depts;
    }

    /**
     * 获取所有部门,以字符的格式
     *
     * @return
     */
    @Override
    public String[] allDeptStrs() {
        List<Department> departments = query(Cnd.where("delFlag", "=", 0).asc("shortNo"));
        Map<String, List<Department>> dMap = new LinkedHashMap<>();
        List<String> dnames = new ArrayList<>();
        while (departments.size() > 0) {
            for (int i = 0; i < departments.size(); i++) {
                Department d = departments.get(i);
                if (dMap.containsKey(d.getPid())) {
                    dMap.get(d.getPid()).add(d);
                } else {
                    List<Department> ds = new ArrayList<>();
                    ds.add(d);
                    dMap.put(d.getPid(), ds);
                }
                departments.remove(d);
                i--;
            }
            addNames(dnames, dMap.get("0"), dMap, "");

        }
        return dnames.toArray(new String[dnames.size()]);
    }

    void addNames(List<String> names, List<Department> ds, Map<String, List<Department>> dmap, String name) {
        for (Department d : ds) {
            if ("0".equals(d.getPid())) {
                names.add(d.getName() + "→" + d.getId());
            } else {
                names.add(name.split("→")[0] + "-" + d.getName() + "→" + d.getId());
            }
            if (dmap.containsKey(d.getId())) {
                addNames(names, dmap.get(d.getId()), dmap, names.get(names.size() - 1));
            }
        }
    }

    /**
     * 拖动排序
     *
     * @param map
     */
    @Override
    public void sort(NutMap map) {
        List<Department> depts = query(Cnd.where("id", IN, map.keySet()));
        depts.forEach(d -> d.setShortNo(map.getInt(d.getId())));
        update(depts);
    }

    private void convertCount(List<DeptJobTreeVO> trees) {
        NutMap nutMap = new NutMap();
        for (DeptJobTreeVO tree : trees) {
            if ("job".equals(tree.getType())) {
                nutMap.setv(tree.getPid() + "," + tree.getId(), tree);
            } else {
                nutMap.setv(tree.getId(), tree);
            }
        }
    }

    private void convertCountNoJob(List<DeptJobTreeVO> trees) {
        NutMap nutMap = new NutMap();
        for (int i = trees.size() - 1; i >= 0; i--) {
            DeptJobTreeVO tree = trees.get(i);
            if ("dept".equals(tree.getType())) {
                nutMap.setv(tree.getId(), tree);
            } else {
                trees.remove(tree);
            }
        }
        List<UserAccount> userInfos = userAccountService.query(Cnd.where("delFlag", "=", 0));
        for (UserAccount info : userInfos) {
            DeptJobTreeVO tree = (DeptJobTreeVO) nutMap.get(info.getDeptId());
            if (tree == null) {
                continue;
            }
            tree.setCount(tree.getCount() + 1);
            setParentTreeCount(tree, nutMap);
        }
    }

    private void setParentTreeCount(DeptJobTreeVO tree, NutMap nutMap) {
        DeptJobTreeVO tree2 = (DeptJobTreeVO) nutMap.get(tree.getPid());
        tree2.setCount(tree2.getCount() + 1);
        nutMap.setv(tree2.getId(), tree2);
        if (!"0".equals(tree2.getId())) {
            setParentTreeCount(tree2, nutMap);
        }
    }

    /**
     * 模板导出
     *
     * @return
     */
    @Override
    public Path createDownTemplate() throws IOException {
        Path template = FileUtil.createTempFile();
        PoiExcelUtil util = PoiExcelUtil.createNewExcel();
        String sheetName = "部门信息导入";
        List<String[]> strings = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        headers.add("部门ID(必填)");
        headers.add("上级部门ID(必填,最上层部门填0)");
        headers.add("部门名称(必填)");
        strings.add(headers.toArray(new String[0]));
        util.createSheet(sheetName);
        //插入表头
        util.insetRowDataList(sheetName, 0, strings);
        //设置列宽
        for (int i = 0, l = headers.size(); i < l; i++) {
            int width = String.valueOf(headers.get(i)).getBytes().length * 256;
            util.setColumnWidth(sheetName, i, width);
        }
        //设置用户名列为文本
        util.setColumnTextFormat(sheetName, 0, 50);
        //冻结表头
        util.createFreezePane(sheetName, 0, 1, 0, 1);
        util.toCreateNewFile(template.toFile());
        return template;
    }

    /**
     * 导入部门
     *
     * @param attachId
     * @param deptId
     * @return
     */
    @Override
    public AjaxResult importDepartment(String attachId, String deptId) {
        Path attachPath = fileAttachService.getPath(attachId);
        PoiExcelUtil poiExcelUtil = new PoiExcelUtil(attachPath.toFile());
        CellStyle errstyle = poiExcelUtil.getCellErrorStyle(poiExcelUtil.getWb(), 2);
        Sheet sheet = poiExcelUtil.getSheetByIndex(0);
//
        boolean canImport = true;
        //id集合,用于判断父id是否存在
        List<String> ids = new ArrayList<>();
        //默认有0
        ids.add("0");
        //名字加父Id组合,用于判断是否重复名字
        List<String> names = new ArrayList<>();
        //需要到数据库查询的名字集合
        List<String> names2 = new ArrayList<>();
        List<String[]> values = poiExcelUtil.getImportList(0, 0, 3);

        //循环取值,忽略第一行表头,判断是否有空值
        for (int i = 1; i < values.size(); i++) {
            String[] strings = values.get(i);
            ids.add(strings[0]);
            names.add(strings[1] + "@" + strings[2]);
            if ("0".equals(strings[1])) {
                names2.add(strings[2]);
            }
            //验证所有值不能为空
            for (int i1 = 0; i1 < 3; i1++) {
                if (StringUtil.isBlank(strings[i1])) {
                    poiExcelUtil.setErrorMsg(sheet, i, i1, "第" + (i1 + 1) + "列的值不能为空", errstyle);
                    canImport = false;
                }
            }
        }
        //判断父id是否存在,并且判断同部门是否重复名字
        for (int i = 1; i < values.size(); i++) {
            String[] strings = values.get(i);
            if (!ids.contains(strings[1])) {
                poiExcelUtil.setErrorMsg(sheet, i, 1, "上级部门ID值没找到", errstyle);
                canImport = false;
            }
            int index = names.indexOf(strings[1] + "@" + strings[2]);
            if (index >= 0 && index != (i - 1)) {
                poiExcelUtil.setErrorMsg(sheet, i, 2, "部门名称和第" + (index + 2) + "行重复", errstyle);
                poiExcelUtil.setErrorMsg(sheet, (index + 1), 2, "部门名称和第" + (i + 1) + "行重复", errstyle);
                canImport = false;
            }
        }
        //判断是否和数据库名字重复
        List<Department> departments = query(Cnd.where("name", IN, names2).and("pid", EQ, deptId));
        for (int i = 0; i < departments.size(); i++) {
            int index = names.indexOf(0 + "@" + departments.get(i).getName());
            if (index >= 0) {
                poiExcelUtil.setErrorMsg(sheet, (index + 1), 2, "部门名称和数据库重复", errstyle);
                canImport = false;
            }
        }
        if (canImport) {
            saveDepartments(values, deptId);
        }
        poiExcelUtil.saveFile();
        if (!canImport) {
            return AjaxResult.error("导入失败,请下载文件查看失败原因!");
        }
        return AjaxResult.sucess("导入成功!");
    }

    private void saveDepartments(List<String[]> values, String pid) {
        Map<String, List<String[]>> allDepts = new HashMap<>(1);
        while (values.size() > 1) {
            for (int i = values.size() - 1; i >= 1; i--) {
                String[] v = values.get(i);
                if (allDepts.containsKey(v[1])) {
                    allDepts.get(v[1]).add(v);
                } else {
                    List<String[]> strings = new ArrayList<>();
                    strings.add(v);
                    allDepts.put(v[1], strings);
                }
                values.remove(i);
            }
        }
        Department department = new Department();
        if ("0".equals(pid)) {
            department = new Department();
            department.setId("0");
            department.setName(Cons.optionsCach.getUnitName());
        } else {
            department = fetch(Cnd.where("id", EQ, pid));
        }
        Department d = department;
        Trans.exec(() -> {
            saveDepartment(allDepts, d, "0");
        });
    }

    private void saveDepartment(Map<String, List<String[]>> allDepts, Department department, String pid) {
        List<String[]> strings = allDepts.get(pid);
        if (strings == null) {
            return;
        }
        for (int i = 0; i < strings.size(); i++) {
            Department dept = new Department();
            dept.setPid(department.getId());
            dept.setName(strings.get(i)[2]);
            dept.setParentName(department.getName());
            dept.setPlaitNum(0);
            Department newDept = insert(dept);
            saveDepartment(allDepts, newDept, strings.get(i)[0]);
        }
        allDepts.remove(pid);
    }

    /**
     * 部门统计数据定制
     *
     * @param customizedParams
     * @return
     */
    @Override
    public List<NutMap> statisticsDeptUser(String customizedParams) {
        try {
            String[] params = customizedParams.split(",");
            List<Department> ids = query(Cnd.where("delFlag", EQ, 0).and("code", IN, params));
            if (ids.size() > 0) {
                //获取所有的部门
                List<String> params2 = new ArrayList<>();
                params = new String[ids.size()];
                for (int i = 0; i < ids.size(); i++) {
                    params[i] = ids.get(i).getId();
                    params2.add(ids.get(i).getId());
                }
                List<Department> departments = query(Cnd.where("delFlag", EQ, 0));
                //map部门tree
                Map<String, List<Department>> dMap = new HashMap<>(1);
                Map<String, Department> chooseDeptMap = new HashMap<>(1);
                departments.forEach(d -> {
                    if (dMap.containsKey(d.getPid())) {
                        dMap.get(d.getPid()).add(d);
                    } else {
                        List<Department> ds = new ArrayList<>();
                        ds.add(d);
                        dMap.put(d.getPid(), ds);
                    }
                    if (params2.contains(d.getId())) {
                        chooseDeptMap.put(d.getId(), d);
                    }
                });
                //循环获取统计值
                List<NutMap> maps = new ArrayList<>();
                for (String param : params) {
                    List<String> ids2 = getAllChildId(dMap, param);
                    int count = userAccountService.count(Cnd.where("deptId", IN, ids2));
                    NutMap map = new NutMap();
                    map.setv("name", chooseDeptMap.get(param).getName());
                    map.setv("value", count);
                    map.setv("id", param);
                    maps.add(map);
                }
                return maps;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<String> getAllChildId(Map<String, List<Department>> dMap, String param) {
        List<String> ids = new ArrayList<>();
        ids.add(param);
        List<Department> depts = dMap.get(param);
        if (depts != null && depts.size() > 0) {
            depts.forEach(d -> {
                ids.addAll(getAllChildId(dMap, d.getId()));
            });
        }
        return ids;
    }

    /**
     * 获取部门下拉
     *
     * @return
     */
    @Override
    public AjaxResult getAllDeptSelect() {
        try {
            List<Department> depts = tree();
            Set<String> pids = new HashSet<>();
            Map<String, DeptSelectVO> dmap = new HashMap<>(1);
            //循环取出所有的父级部门;
            depts.forEach(d -> {
                pids.add(d.getPid());
                dmap.put(d.getId(), new DeptSelectVO(d.getId(), d.getName(), new ArrayList<>()));
            });
            pids.add("0");
            List<DeptSelectVO> deptSelects = new ArrayList<>();
            deptSelects.add(new DeptSelectVO("0", "不限", new ArrayList<>()));
            dmap.put("0", new DeptSelectVO("0", "顶级部门临时名字", deptSelects));
            while (depts.size() > 1) {
                Set<String> waitRemove = new HashSet<>();
                for (int i = depts.size() - 1; i >= 0; i--) {
                    Department d = depts.get(i);
                    if (!pids.contains(d.getId())) {
                        waitRemove.add(d.getPid());
                        dmap.get(d.getPid()).getChilds().add(dmap.get(d.getId()));
                        depts.remove(i);
                    }
                }
                pids.removeAll(waitRemove);
            }

            return AjaxResult.sucess(dmap.get("0").getChilds(), "获取成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("获取失败!");
        }
    }

    /**
     * h获取部门树,包含数量
     *
     * @return
     */
    @Override
    public List<DeptJobTreeVO> treeAboutCount() {
        List<DeptJobTreeVO> trees = treeAboutJob2();
        convertCountNoJob(trees);
        trees.forEach((r) -> r.setName(r.getName() + "(" + r.getCount() + ")"));
        return trees;
    }

    @Override
    public List<NutMap> listUserInfo(String deptId) {
        return queryMapBySql("listUserInfo", NutMap.NEW().setv("deptId", deptId));
    }

    @Override
    public List<NutMap> listUserInfo(List<String> queryUserNames) {
        if (CollectionUtils.isNotEmpty(queryUserNames)) {
            return queryMapBySql("listUserInfo", NutMap.NEW().setv("deptId", ""), Cnd.where("userName", "in", queryUserNames));
        }
        return Arrays.asList();
    }

    @Override
    public List<Department> queryAndChild(String deptId) {
        return queryMapBySql("queryAndChild", NutMap.NEW().setv("deptId", deptId));
    }

    @Override
    public PageDataDTO queryListUserInfo(String query, int pageNum, int pageSize) {
        return queryMapBySql("queryListUserInfo", NutMap.NEW().setv("query", query), new Pager(pageNum, pageSize));
    }


    @Override
    public SqlsTplHolder getSqlsTplHolder() {
        return sqlsTplHolder;
    }

    @Override
    public Dao getDao() {
        return dao;
    }
}
