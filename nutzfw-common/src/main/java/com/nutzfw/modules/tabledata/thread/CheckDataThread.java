/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.tabledata.thread;

import com.nutzfw.core.common.util.DateUtil;
import com.nutzfw.core.common.util.StringUtil;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.sys.biz.DictBiz;
import com.nutzfw.modules.sys.entity.DataTable;
import com.nutzfw.modules.sys.entity.Dict;
import com.nutzfw.modules.sys.entity.TableFields;
import com.nutzfw.modules.sys.service.DataTableService;
import com.nutzfw.modules.sys.service.FileAttachService;
import com.nutzfw.modules.sys.service.TableFieldsService;
import com.nutzfw.modules.tabledata.biz.DataMaintainBiz;
import com.nutzfw.modules.tabledata.entity.DataImportHistory;
import com.nutzfw.modules.tabledata.enums.FieldType;
import com.nutzfw.modules.tabledata.enums.FormValidationRulesType;
import com.nutzfw.modules.tabledata.enums.ImportType;
import com.nutzfw.modules.tabledata.enums.TableType;
import com.nutzfw.modules.tabledata.service.DataImportHistoryService;
import com.nutzfw.modules.tabledata.util.DataImportPoiUtil;
import com.nutzfw.modules.tabledata.util.DataUtil;
import org.nutz.dao.Cnd;
import org.nutz.dao.entity.Record;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.trans.Trans;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/19
 * 描述此类：数据导入效验
 */
public class CheckDataThread implements Runnable {

    /***
     * 字典分隔符
     */
    static final String DELIMITER = DataMaintainBiz.DELIMITER;
    DataTableService         tableService;
    TableFieldsService       fieldsService;
    DataMaintainBiz          dataMaintainBiz;
    DataImportHistoryService importHistoryService;
    FileAttachService        fileAttachService;
    DictBiz                  dictBiz;
    UserAccountService       accountService;
    UserAccount              userAccount;
    long                     startTime;
    DataTable                dataTable;
    List<NutMap>             listData = new ArrayList<>();
    private Log log = Logs.get();

    private DataImportHistory history;

    private String uniqueField = DataMaintainBiz.UNIQUE_FIELD;

    public CheckDataThread(Ioc ioc, DataImportHistory history) {
        this.history = history;
        this.tableService = ioc.get(DataTableService.class, "dataTableService");
        this.fieldsService = ioc.get(TableFieldsService.class, "tableFieldsService");
        this.dataMaintainBiz = ioc.get(DataMaintainBiz.class, "dataMaintainBiz");
        this.importHistoryService = ioc.get(DataImportHistoryService.class, "dataImportHistoryService");
        this.fileAttachService = ioc.get(FileAttachService.class, "fileAttachService");
        this.dictBiz = ioc.get(DictBiz.class, "dictBiz");
        this.accountService = ioc.get(UserAccountService.class, "userAccountService");
        this.dataTable = tableService.fetchAllFields(history.getTableId());
        this.userAccount = accountService.fetch(history.getUserid());
    }

    /**
     * 效验数据
     */
    private void checkData(DataImportPoiUtil util) throws IOException {
        //执行检查
        boolean success = true;
        String sheetName = dataTable.getName() + "导入版本" + dataTable.getVersion();
        history.setStaus(1);
        importHistoryService.update(history);
        //检查中
        String[] ids = util.getImportDataTableFieldsList();
        List<TableFields> tableFieldsList = fieldsService.query(Cnd.where("id", "in", ids));
        //LABLE-ID
        HashMap<String, Dict> lableAndIdDictsInfo = new HashMap<>(1);
        //LABLE
        HashMap<String, Dict> sysCodeAndLableDictsInfo = new HashMap<>(1);

        HashMap<Integer, TableFields> headerInfo = new HashMap<>(1);
        // 1->用户名
        int fixSize = 0;
        int startColIndex = 0;
        if (dataTable.getTableType() != TableType.SingleTable) {
            fixSize = 1;
            startColIndex = 1;
        }
        for (TableFields fields : tableFieldsList) {
            if (Strings.isNotBlank(fields.getDictSysCode())) {
                //是字典
                List<Dict> dictDetails = dictBiz.listCache(fields.getDictSysCode());
                dictDetails.forEach(dictDetail -> lableAndIdDictsInfo.put(dictDetail.getLable().concat(DELIMITER).concat(String.valueOf(dictDetail.getId())), dictDetail));
                dictDetails.forEach(dictDetail -> sysCodeAndLableDictsInfo.put(fields.getDictSysCode().concat(dictDetail.getLable()), dictDetail));
            }
            if (fields.getFieldType() == FieldType.MultiAttach.getValue() || fields.getFieldType() == FieldType.SingleAttach.getValue()) {
                continue;
            }
            headerInfo.put(fixSize, fields);
            fixSize++;
        }
        listData = util.getImportDataList(sheetName, 1, headerInfo, lableAndIdDictsInfo, sysCodeAndLableDictsInfo, accountService, dataTable);
        Set<String> userNames = new HashSet<>();
        for (int i = 0; i < listData.size(); i++) {
            NutMap rowData = listData.get(i);
            //忽略附件进行效验
            List<String> errors = dataMaintainBiz.checkImportTableData(tableFieldsList, rowData, uniqueField, history.getImportType(), dataTable.getTableType());
            if (rowData.containsKey("userid")) {
                if (dataTable.getTableType() == TableType.PrimaryTable) {
                    //主表每个人只能有一条记录，检查excle中是否存在重复数据
                    String userid = rowData.getString("userid");
                    if (userNames.contains(userid)) {
                        errors.add("用户名重复，主表每个人只能有一条记录，检查excle中是否存在重复数据");
                    }
                    userNames.add(userid);
                }
            } else if (dataTable.getTableType() != TableType.SingleTable) {
                errors.add("用户名不存在");
            }
            if (errors.size() > 0) {
                String errorMsg = Strings.join(",", errors);
                util.setErrorMsg(sheetName, i + 1, headerInfo.size() + startColIndex, errorMsg);
                success = false;
                history.setErrorMsg(errorMsg);
            } else {
                util.setResetCell(sheetName, i + 1, headerInfo.size() + startColIndex);
            }
        }
        if (success) {
            //状态修改为导入中
            history.setStaus(3);
        } else {
            //状态修改为检查失败
            history.setStaus(2);
        }
        importHistoryService.update(history);
    }


    @Override
    public void run() {
        if (canStartRunCheckOrImport()) {
            DataImportHistory dataImportHistory = getDataImportHistory();
            if (dataImportHistory != null) {
                excute();
            }
        }
    }


    /**
     * 取得一个待检查数据
     */
    private DataImportHistory getDataImportHistory() {
        return importHistoryService.fetch(Cnd.where("staus", "=", 0));
    }


    /**
     * 检查是否能够可以开始检查或导入
     */
    private synchronized boolean canStartRunCheckOrImport() {
        //检查是否有检查中或导入中的数据
        return importHistoryService.count(
                Cnd.where("staus", "=", 1)
                        .or("staus", "=", 3)
        ) == 0;
    }


    public void excute() {
        startTime = System.currentTimeMillis();
        Path attachPath = fileAttachService.getPath(history.getAttachId());
        DataImportPoiUtil util = null;
        try {
            util = new DataImportPoiUtil(attachPath);
            DataTable table = tableService.fetchAllFields(history.getTableId());
            if (table.getTableType() == TableType.SingleTable) {
                if (history.getImportType() > 1) {
                    //单表 导入模式不是全部导入，需要验证唯一值
                    boolean hasField = table.getFields().stream().anyMatch(fields -> fields.getId() == history.getUniqueField());
                    if (!hasField) {
                        //没有字段
                        throw new RuntimeException("没有唯一效验字段，无法继续验证！");
                    } else {
                        uniqueField = table.getFields().stream().filter(fields -> fields.getId() == history.getUniqueField()).findAny().get().getFieldName();
                    }
                } else {
                    //有唯一值必须进行勾选
                    boolean hasUnique = table.getFields().stream().anyMatch(fields -> fields.getValidationRulesType() == FormValidationRulesType.UNIQUE.getValue());
                    if (hasUnique) {
                        throw new RuntimeException("请选择唯一效验字段！");
                    }
                }
            }
            checkData(util);
            if (history.getStaus() == 3) {
                coverDependsData();
                importData();
            }
        } catch (Throwable e) {
            log.error(e);
            //0 待检查 1检查中 2检查失败 3导入中 4导入成功 5导入失败
            history.setStaus(2);
            if (e instanceof NullPointerException) {
                history.setErrorMsg("空指针异常");
            } else {
                history.setErrorMsg(e.getLocalizedMessage());
                if (Strings.sNull(history.getErrorMsg()).indexOf("Nutz SQL Error") > -1) {
                    history.setErrorMsg("数据无法保存至数据库！请联系开发人员处理！");
                }
            }
            history.setErrorMsgInfo(StringUtil.throwableToString(e));
        } finally {
            try {
                util.save();
            } catch (Exception e) {
            }
            long endTime = System.currentTimeMillis();
            history.setConsuming(DateUtil.getDistanceTime(startTime, endTime, "{H}小时{M}分{S}秒{MS}毫秒"));
            importHistoryService.update(history);
        }
    }

    private void importUserData() {
        if (dataTable.getTableType() == TableType.PrimaryTable) {
            //主表一个人只能有一条记录---所以只能选择
            if (history.getImportType() == ImportType.existsUpdate_ImportNotExists.getValue()) {
                //记录存在则更新，不存在则导入
                existsUpdateImportNotExists();
            } else if (history.getImportType() == ImportType.existsUpdate_IgnoreNotExists.getValue()) {
                //记录存在则更新，不存在则忽略
                existsUpdateIgnoreNotExists();
            } else if (history.getImportType() == ImportType.iGnoreExists_ImportNotExists.getValue()) {
                //记录存在则忽略，不存在则导入
                iGnoreExistsImportNotExists();
            } else {
                throw new RuntimeException("导入模式不支持");
            }
        } else {
            //附表导入全部记录
            importDataALL();
        }
    }


    private void importData() {
        try {
            if (dataTable.getTableType() != TableType.SingleTable) {
                importUserData();
            } else {
                importSingleTableData();
            }
            history.setStaus(4);
        } catch (Throwable e) {
            log.error(e);
            //0 待检查 1检查中 2检查失败 3导入中 4导入成功 5导入失败
            history.setStaus(5);
            history.setErrorMsg(e.getLocalizedMessage());
            history.setErrorMsgInfo(StringUtil.throwableToString(e));
        } finally {
            long endTime = System.currentTimeMillis();
            history.setConsuming(DateUtil.getDistanceTime(startTime, endTime, "{D}天{H}小时{M}分{S}秒{MS}毫秒"));
            importHistoryService.update(history);
        }
    }

    private void importSingleTableData() {
        if (dataTable.getTableType() == TableType.SingleTable) {
            //和单表都可以执行全部导入
            boolean hasUnique = dataTable.getFields().stream().anyMatch(fields -> fields.getValidationRulesType() == FormValidationRulesType.UNIQUE.getValue());
            //主表一个人只能有一条记录---所以只能选择
            if (history.getImportType() == ImportType.existsUpdate_ImportNotExists.getValue()) {
                //记录存在则更新，不存在则导入
                existsUpdateImportNotExists();
            } else if (history.getImportType() == ImportType.existsUpdate_IgnoreNotExists.getValue()) {
                //记录存在则更新，不存在则忽略
                existsUpdateIgnoreNotExists();
            } else if (history.getImportType() == ImportType.iGnoreExists_ImportNotExists.getValue()) {
                //记录存在则忽略，不存在则导入
                iGnoreExistsImportNotExists();
            } else if (!hasUnique && history.getImportType() == ImportType.ALL.getValue()) {
                //没有唯一验证，并且是全部导入
                importDataALL();
            } else {
                throw new RuntimeException("导入模式不支持");
            }
        } else {
            //附表导入全部记录
            importDataALL();
        }


    }

    /**
     * 记录存在则忽略，不存在则导入
     */
    private void iGnoreExistsImportNotExists() {
        List<NutMap> inserData = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        listData.forEach(nutMap -> ids.add(nutMap.getString(uniqueField)));
        //旧数据
        List<Record> oldDatalist = tableService.dao().query(dataTable.getTableName(), Cnd.where(uniqueField, "in", ids));
        HashMap<String, Record> oldDataMap = new HashMap<>(oldDatalist.size());
        oldDatalist.forEach(record -> oldDataMap.put(record.getString(uniqueField), record));
        listData.forEach(nutMap -> {
            Record old = oldDataMap.get(nutMap.getString(uniqueField));
            if (old == null) {
                //记录不存在
                inserData.add(coverInsertData(nutMap));
            }
        });
        Trans.exec(() -> tableService.dao().fastInsert(inserData));
    }


    /**
     * 记录存在则更新，不存在则忽略
     */
    private void existsUpdateIgnoreNotExists() {
        List<NutMap> updateData = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        listData.forEach(nutMap -> ids.add(nutMap.getString(uniqueField)));
        //旧数据
        List<Record> oldDatalist = tableService.dao().query(dataTable.getTableName(), Cnd.where(uniqueField, "in", ids));
        HashMap<String, Record> oldDataMap = new HashMap<>(oldDatalist.size());
        oldDatalist.forEach(record -> oldDataMap.put(record.getString(uniqueField), record));
        listData.forEach(nutMap -> {
            Record old = oldDataMap.get(nutMap.getString(uniqueField));
            if (old != null) {
                //记录存在
                updateData.add(coverUpdateData(nutMap, old));
            }
        });
        Trans.exec(() -> {
            //todo 抽空优化下Nutz源码实现Nutmap的批量多条件更新，目前先完成功能为主
            updateData.forEach(nutMap -> {
                tableService.dao().update(nutMap,
                        Cnd.where(uniqueField, "=", nutMap.getString(uniqueField))
                                .and("update_version", "=", nutMap.getInt("update_version") - 1)
                );
            });
        });
    }

    /**
     * 记录存在则更新，不存在则导入
     */
    private void existsUpdateImportNotExists() {
        List<NutMap> inserData = new ArrayList<>();
        List<NutMap> updateData = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        listData.forEach(nutMap -> ids.add(nutMap.getString(uniqueField)));
        //旧数据
        List<Record> oldDatalist = tableService.dao().query(dataTable.getTableName(), Cnd.where(uniqueField, "in", ids));
        HashMap<String, Record> oldDataMap = new HashMap<>(oldDatalist.size());
        oldDatalist.forEach(record -> oldDataMap.put(record.getString(uniqueField), record));
        listData.forEach(nutMap -> {
            Record old = oldDataMap.get(nutMap.getString(uniqueField));
            if (old == null) {
                //记录不存在
                inserData.add(coverInsertData(nutMap));
            } else {
                //记录存在
                updateData.add(coverUpdateData(nutMap, old));
            }
        });
        Trans.exec(() -> {
            tableService.dao().fastInsert(inserData);
            //todo 抽空优化下Nutz源码实现Nutmap的批量多条件更新，目前先完成功能为主
            updateData.forEach(nutMap ->
                    tableService.dao().update(nutMap,
                            Cnd.where(uniqueField, "=", nutMap.getString(uniqueField))
                                    .and("update_version", "=", nutMap.getInt("update_version") - 1)
                    ));
        });
    }


    /**
     * 导入全部记录
     */
    private void importDataALL() {
        List<NutMap> inserData = new ArrayList<>();
        listData.forEach(nutMap -> inserData.add(coverInsertData(nutMap)));
        Trans.exec(() -> tableService.dao().fastInsert(inserData));
    }

    private NutMap coverInsertData(NutMap nutMap) {
        nutMap.setv(dataTable.getPrimaryKey(), R.UU16());
        return DataUtil.coverInsertData(nutMap, userAccount);
    }

    private NutMap coverUpdateData(NutMap nutMap, Record old) {
        nutMap.put(dataTable.getPrimaryKey(), old.getString(dataTable.getPrimaryKey()));
        nutMap.put(uniqueField, old.getString(uniqueField));
        return DataUtil.coverUpdateData(nutMap, userAccount, old.getInt("update_version", 1));
    }

    /**
     * 处理数据依赖
     */
    private void coverDependsData() {
        List<NutMap> coverDataList = new ArrayList<>();
        listData.forEach(nutMap -> coverDataList.add(dataMaintainBiz.coverSaveTableData(dataTable.getFields(), nutMap)));
        listData.clear();
        listData.addAll(coverDataList);
    }

}
