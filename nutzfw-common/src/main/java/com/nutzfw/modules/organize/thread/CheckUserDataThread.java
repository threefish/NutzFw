/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.organize.thread;

import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.util.DateUtil;
import com.nutzfw.core.common.util.RegexUtil;
import com.nutzfw.core.common.util.StringUtil;
import com.nutzfw.core.common.util.excel.PoiExcelUtil;
import com.nutzfw.modules.organize.entity.DepartmentJob;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.entity.UserAccountJob;
import com.nutzfw.modules.organize.entity.UserImportHistory;
import com.nutzfw.modules.organize.service.DepartmentJobService;
import com.nutzfw.modules.organize.service.UserAccountJobService;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.organize.service.UserImportHistoryService;
import com.nutzfw.modules.sys.service.FileAttachService;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.nutz.dao.Cnd;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.log.Logs;
import org.nutz.trans.Trans;

import java.nio.file.Path;
import java.util.*;

/**
 * @author 叶世游
 * @date 2018/6/22 21:02
 * @description 用户导入校验
 */
public class CheckUserDataThread implements Runnable {
    private Ioc ioc;
    private UserImportHistory userImportHistory;
    private UserAccountService userAccountService;
    private FileAttachService fileAttachService;
    private UserImportHistoryService userImportHistoryService;
    private DepartmentJobService departmentJobService;
    private UserAccountJobService userAccountJobService;
    private long startTime;
    private Sheet sheet;
    private CellStyle errstyle;

    public CheckUserDataThread(Ioc ioc, UserImportHistory userImportHistory) {
        this.ioc = ioc;
        this.userImportHistory = userImportHistory;
        this.userAccountService = ioc.get(UserAccountService.class);
        this.fileAttachService = ioc.get(FileAttachService.class);
        this.userImportHistoryService = ioc.get(UserImportHistoryService.class);
        this.departmentJobService = ioc.get(DepartmentJobService.class);
        this.userAccountJobService = ioc.get(UserAccountJobService.class);
    }

    @Override
    public void run() {
        checkUserData();
    }

    /**
     * 解析用户数据
     */
    private void checkUserData() {
        startTime = System.currentTimeMillis();
        try {
            userImportHistory.setStaus(1);
            userImportHistoryService.update(userImportHistory);
            Path attachPath = fileAttachService.getPath(userImportHistory.getAttachId());
            PoiExcelUtil poiExcelUtil = new PoiExcelUtil(attachPath.toFile());
            errstyle = poiExcelUtil.getCellErrorStyle(poiExcelUtil.getWb(), 2);
            sheet = poiExcelUtil.getSheetByIndex(0);
            List<UserAccount> userAccounts = new ArrayList<>();
            List<UserAccountJob> userAccountJobs = new ArrayList<>();
            List<String> userNames = new ArrayList<>();
            List<String[]> values = poiExcelUtil.getImportList(0, 0, 10);
            if (values.size() == 0) {
                throw new RuntimeException("未取得任何导入数据！请检查！");
            }
            String userName = "";
            boolean canImport = true;
            List<DepartmentJob> djs = departmentJobService.query(Cnd.where("delFlag", "=", 0));
            Map<String, List<String>> dmap = new HashMap<>(1);
            djs.forEach(d -> {
                if (dmap.containsKey(d.getDeptId())) {
                    dmap.get(d.getDeptId()).add(d.getJobId());
                } else {
                    List<String> dids = new ArrayList<>();
                    dids.add(d.getJobId());
                    dmap.put(d.getDeptId(), dids);
                }
            });
            for (int i = 1; i < values.size(); i++) {
                String[] strings = values.get(i);
                //验证所有值不能为空
                for (int i1 = 0; i1 < 4; i1++) {
                    if (StringUtil.isBlank(strings[i1])) {
                        poiExcelUtil.setErrorMsg(sheet, i, i1, "第" + (i1 + 1) + "列的值不能为空", errstyle);
                        canImport = false;
                    }
                }
                //验证用户名重复
                userName = strings[0];
                if (!RegexUtil.isAccount(userName)) {
                    poiExcelUtil.setErrorMsg(sheet, i, 0, "用户名不符合规范", errstyle);
                    canImport = false;
                }
                int index = userNames.indexOf(userName);
                if (index >= 0) {
                    poiExcelUtil.setErrorMsg(sheet, i, 0, "用户名和第" + (index + 2) + "行重复", errstyle);
                    poiExcelUtil.setErrorMsg(sheet, index + 1, 0, "用户名和第" + (i + 1) + "行重复", errstyle);
                    canImport = false;
                } else {
                    userNames.add(userName);
                }
                //验证部门下面是否有这个岗位
                if (dmap.get(strings[2].split("→")[1]) == null || !dmap.get(strings[2].split("→")[1]).contains(strings[3].split("→")[1])) {
                    canImport = false;
                    poiExcelUtil.setErrorMsg(sheet, i, 3, "部门下没有该岗位", errstyle);
                }
                //验证手机号
                if (!StringUtil.isBlank(strings[4]) && !RegexUtil.isPhone(strings[4])) {
                    poiExcelUtil.setErrorMsg(sheet, i, 4, "手机号格式错误", errstyle);
                    canImport = false;
                }
                //验证邮箱
                if (!RegexUtil.isEmail(strings[5]) && !StringUtil.isBlank(strings[5])) {
                    poiExcelUtil.setErrorMsg(sheet, i, 5, "邮箱格式错误", errstyle);
                    canImport = false;
                }
                //验证密码
                if (StringUtil.isBlank(strings[6])) {
                    strings[6] = Cons.DEFAULT_PASSWORD;
                }
                if (canImport) {
                    UserAccount userAccount = new UserAccount();
                    userAccount.setLocked(false);
                    userAccount.setUserName(userName);
                    userAccount.setRealName(strings[1]);
                    userAccount.setDeptId(strings[2].split("→")[1]);
                    userAccount.setPhone(strings[4]);
                    userAccount.setMail(strings[5]);

                    String salt = R.UU16();
                    System.out.println(strings[6]);
                    Sha256Hash sha = new Sha256Hash(strings[6], salt);
                    userAccount.setUserPass(sha.toHex());
                    userAccount.setSalt(salt);

                    userAccount.setCreateByDate(new Date(System.currentTimeMillis()));
                    userAccount.setCreateByName(userImportHistory.getUserDesc());
                    userAccount.setCreateByUserid(userImportHistory.getOpBy());
                    userAccounts.add(userAccount);

                    UserAccountJob job = new UserAccountJob();
                    job.setJobId(strings[3].split("→")[1]);
                    userAccountJobs.add(job);
                }
            }
            //验证数据库是否已经存在用户名
            List<UserAccount> oldUser = userAccountService.query(Cnd.where("userName", "in", userNames));
            for (int i = 0; i < oldUser.size(); i++) {
                String uName = oldUser.get(i).getUserName();
                poiExcelUtil.setErrorMsg(sheet, userNames.indexOf(uName) + 1, 0, "用户名" + uName + "已经存在", errstyle);
                canImport = false;
            }
            if (canImport) {
                userImportHistory.setStaus(3);
                userImportHistoryService.update(userImportHistory);
                Trans.exec(() -> {
                    for (int i = 0; i < userAccounts.size(); i++) {
                        UserAccount us = userAccountService.insert(userAccounts.get(i));
                        userAccountJobs.get(i).setUserId(us.getId());
                    }
                    userAccountJobService.insert(userAccountJobs);
                });
                userImportHistory.setStaus(4);
            } else {
                userImportHistory.setStaus(2);
                poiExcelUtil.saveFile();
            }
        } catch (Throwable t) {
            userImportHistory.setStaus(5);
            userImportHistory.setErrMsg("系统错误:" + Strings.cutStr(120, t.getMessage(), "..."));
            userImportHistory.setErrMsgInfo(Strings.cutStr(10000, StringUtil.throwableToString(t), "..."));
            Logs.get().error(t);
        } finally {
            long endTime = System.currentTimeMillis();
            userImportHistory.setConsuming(DateUtil.getDistanceTime(startTime, endTime, "{H}小时{M}分{S}秒{MS}毫秒"));
            userImportHistoryService.update(userImportHistory);
        }
    }
}
