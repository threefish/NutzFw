package com.nutzfw.modules.tabledata.biz.impl;

import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.util.DateUtil;
import com.nutzfw.core.common.util.FileUtil;
import com.nutzfw.core.common.util.RegexUtil;
import com.nutzfw.core.common.util.excel.PoiExcelUtil;
import com.nutzfw.core.common.vo.LayuiTableDataListVO;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.sys.biz.DictBiz;
import com.nutzfw.modules.sys.entity.DataTable;
import com.nutzfw.modules.sys.entity.Dict;
import com.nutzfw.modules.sys.entity.FileAttach;
import com.nutzfw.modules.sys.entity.TableFields;
import com.nutzfw.modules.sys.service.DataTableService;
import com.nutzfw.modules.sys.service.FileAttachService;
import com.nutzfw.modules.sys.service.TableFieldsService;
import com.nutzfw.modules.tabledata.biz.DataMaintainBiz;
import com.nutzfw.modules.tabledata.entity.UserDataChangeHistory;
import com.nutzfw.modules.tabledata.enums.*;
import com.nutzfw.modules.tabledata.service.DataImportHistoryService;
import com.nutzfw.modules.tabledata.service.UserDataChangeHistoryService;
import com.nutzfw.modules.tabledata.util.DataImportPoiUtil;
import com.nutzfw.modules.tabledata.util.DataUtil;
import com.nutzfw.modules.tabledata.vo.DictDependentChangeVO;
import com.nutzfw.modules.tabledata.vo.ReviewChangeVO;
import com.nutzfw.modules.tabledata.vo.SingeDataMaintainQueryVO;
import com.nutzfw.modules.tabledata.vo.TableColsVO;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.repo.Base64;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/9
 * 描述此类：
 */
@IocBean(name = "dataMaintainBiz")
public class DataMaintainBizImpl implements DataMaintainBiz {


    @Inject
    UserAccountService accountService;
    @Inject
    DataTableService tableService;
    @Inject
    TableFieldsService fieldsService;
    @Inject
    DataImportHistoryService importHistoryService;
    @Inject
    DictBiz dictBiz;
    @Inject
    FileAttachService fileAttachService;
    @Inject
    UserDataChangeHistoryService userDataChangeHistoryService;

    /**
     * 取得表头
     *
     * @param tableid
     * @param sessionRoleIds
     * @return
     */
    @Override
    public List<TableColsVO> getCols(int tableid, Set<String> sessionRoleIds) {
        DataTable dataTable = tableService.fetchAuthReadFields(tableid, sessionRoleIds);
        List<TableColsVO> list = new ArrayList<>();
        list.add(new TableColsVO("checkbox", "true"));
        list.add(new TableColsVO("numbers", "true"));
        if (dataTable.getTableType() != TableType.SingleTable) {
            list.add(new TableColsVO("realname", "真实姓名", 100, 1, 0, 0));
            list.add(new TableColsVO("username", "用户名", 100, 0, 0, 0));
            list.add(new TableColsVO("deptname", "部门", 100, 0, 0, 0));
            dataTable.getFields().stream().filter(fields ->
                    !("真实姓名".equals(fields.getName()) || "用户名".equals(fields.getName()))
            ).forEach(fields ->
                    list.add(new TableColsVO(fields.getFieldName(), fields.getName(), 100, 0, fields.getFieldType(), fields.getControlType()))
            );
        } else {
            dataTable.getFields().forEach(fields -> list.add(new TableColsVO(fields.getFieldName(), fields.getName(), 100, 0, fields.getFieldType(), fields.getControlType())));
        }
        return list;
    }

    @Override
    public List<TableColsVO> getColsNotFix(int tableid, Set<String> sessionRoleIds) {
        List<TableColsVO> colsVOS = getCols(tableid, sessionRoleIds);
        colsVOS.stream().forEach(tableColsVO -> tableColsVO.setFixed(null));
        return colsVOS;
    }

    /**
     * 根据用户取得表数据
     *
     * @param pageNum
     * @param pageSize
     * @param tableid
     * @param userid
     * @return
     */
    @Override
    public LayuiTableDataListVO listUserDataPage(int pageNum, int pageSize, int tableid, String userid, Set<String> sessionManagerUserNames) {
        return listDataByCnd(pageNum, pageSize, tableid, userid, "", "", new String[0], null, sessionManagerUserNames);
    }


    /**
     * 取得表数据
     *
     * @param pageNum
     * @param pageSize
     * @param tableid
     * @param userNameOrRealName
     * @param deptIds
     * @param list
     * @param sessionManagerUserNames
     * @return
     */
    @Override
    public LayuiTableDataListVO listPage(int pageNum, int pageSize, int tableid, String userNameOrRealName, String[] deptIds, List<SingeDataMaintainQueryVO> list, Set<String> sessionManagerUserNames) {
        return listDataByCnd(pageNum, pageSize, tableid, "", "", userNameOrRealName, deptIds, list, sessionManagerUserNames);
    }

    @Override
    public Record fetchData(String tableName, String sourceId) {
        return fetchData(sourceId, tableService.fetchByTableName(tableName));
    }

    @Override
    public Record fetchData(String sourceId, DataTable dataTable) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT $showFields FROM $tableName where id=@id order by $tableName.create_by_date");
        Sql querySql = Sqls.create(sb.toString());
        querySql.vars().set("tableName", Sqls.escapeSqlFieldValue(dataTable.getTableName()));
        querySql.vars().set("showFields", Strings.join(",", getQueryFields(dataTable)));
        querySql.setParam("id", sourceId);
        querySql.setCallback(Sqls.callback.record());
        tableService.dao().execute(querySql);
        return querySql.getObject(Record.class);
    }

    @Override
    public NutMap fetchDataToView(String tableName, String sourceId) {
        DataTable dataTable = tableService.fetchByTableName(tableName);
        return NutMap.WRAP(coverDataToView(fetchData(sourceId, dataTable), dataTable.getFields()));
    }

    @Override
    public List<Record> queryUserDataList(int tableid, String userName, Set<String> sessionManagerUserNames) {
        LayuiTableDataListVO vo = listDataByCnd(0, 99999, tableid, "", userName, "", new String[0], null, sessionManagerUserNames);
        return vo.getData();
    }

    /**
     * 取得单表数据
     *
     * @param pageNum
     * @param pageSize
     * @param tableid
     * @param list
     * @return
     */
    @Override
    public LayuiTableDataListVO listSingeTableDataPage(int pageNum, int pageSize, int tableid, List<SingeDataMaintainQueryVO> list) {
        DataTable dataTable = tableService.fetchAllFields(tableid);
        Pager pager = tableService.dao().createPager(pageNum, pageSize);
        List<Record> recordList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT $showFields FROM $tableName ");
        String whereSql = getWhereSql(list);
        if (Strings.isNotBlank(whereSql)) {
            sb.append("where " + whereSql);
        }
        sb.append(" order by $tableName.create_by_date");
        Sql querySql = Sqls.create(sb.toString());
        querySql.vars().set("tableName", Sqls.escapeSqlFieldValue(dataTable.getTableName()));
        querySql.vars().set("showFields", Strings.join(",", getQueryFields(dataTable)));
        setWhereParam(list, querySql);
        Sql countSql = Sqls.create(querySql.toString());
        querySql.setCallback(Sqls.callback.records());
        querySql.setPager(pager);
        tableService.dao().execute(querySql);
        List<Record> queryRecords = querySql.getList(Record.class);
        long count = Daos.queryCount(tableService.dao(), countSql);
        if (queryRecords != null && queryRecords.size() > 0) {
            recordList.addAll(queryRecords);
        }
        recordList = coverData(recordList, dataTable.getFields());
        return new LayuiTableDataListVO(0, (int) count, recordList);
    }

    private String getWhereSql(List<SingeDataMaintainQueryVO> list) {
        Set<String> sqls = new HashSet<>();
        if (null == list) {
            return "";
        }
        list.stream().forEach(queryVO -> {
            String fieldName = Sqls.escapeSqlFieldValue(queryVO.getFieldName()).toString();
            CharSequence val = Sqls.escapteConditionValue(queryVO.getVal());
            CharSequence startVal = Sqls.escapteConditionValue(queryVO.getStartVal());
            CharSequence endVal = Sqls.escapteConditionValue(queryVO.getEndVal());
            boolean notBlank = Strings.isNotBlank(val);
            String sql = getConditionSql(queryVO, fieldName, startVal, endVal, notBlank);
            if (Strings.isNotBlank(sql)) {
                sqls.add(sql);
            }
        });
        if (sqls.size() > 0) {
            return Strings.join(" and ", sqls);
        }
        return "";
    }

    private String getConditionSql(SingeDataMaintainQueryVO queryVO, String fieldName, CharSequence startVal, CharSequence endVal, boolean notBlank) {
        String sql = "";
        switch (queryVO.getJoiner()) {
            case "=":
                if (notBlank) {
                    sql = MessageFormat.format("{0} = @{0}", fieldName);
                }
                break;
            case "!=":
                if (notBlank) {
                    sql = MessageFormat.format("{0} <> @{0}", fieldName);
                }
                break;
            case "<":
                if (notBlank) {
                    sql = MessageFormat.format("{0} < @{0}", fieldName);
                }
                break;
            case "<=":
                if (notBlank) {
                    sql = MessageFormat.format("{0} <= @{0}", fieldName);
                }
                break;
            case ">":
                if (notBlank) {
                    sql = MessageFormat.format("{0} > @{0}", fieldName);
                }
                break;
            case ">=":
                if (notBlank) {
                    sql = MessageFormat.format("{0} >= @{0}", fieldName);
                }
                break;
            case "like":
                if (notBlank) {
                    sql = MessageFormat.format("{0} like @{0}", fieldName);
                }
                break;
            case "not like":
                if (notBlank) {
                    sql = MessageFormat.format("{0} not like @{0}", fieldName);
                }
                break;
            case "start like":
                if (notBlank) {
                    sql = MessageFormat.format("{0} like @{0}", fieldName);
                }
                break;
            case "end like":
                if (notBlank) {
                    sql = MessageFormat.format("{0} like @{0}", fieldName);
                }
                break;
            case "between":
                if (Strings.isNotBlank(startVal) && Strings.isNotBlank(endVal)) {
                    sql = MessageFormat.format("{0} between @{0}_strat and @{0}_end", fieldName);
                }
                break;
            case "not between":
                if (Strings.isNotBlank(startVal) && Strings.isNotBlank(endVal)) {
                    sql = MessageFormat.format("{0} not between @{0}_strat and @{0}_end", fieldName);
                }
                break;
            case "is null":
                sql = MessageFormat.format("{0} is null", fieldName);
                break;
            case "is not null":
                sql = MessageFormat.format("{0} is not null", fieldName);
                break;
            case "empty":
                sql = MessageFormat.format("{0} == ''", fieldName);
                break;
            case "not empty":
                sql = MessageFormat.format("{0} <> ''", fieldName);
                break;
            default:
                System.out.println("xxx");
                break;
        }
        return sql;
    }

    private void setWhereParam(List<SingeDataMaintainQueryVO> list, Sql querySql) {
        if (null != list) {
            list.stream().forEach(queryVO -> {
                String fieldName = Sqls.escapeSqlFieldValue(queryVO.getFieldName()).toString();
                CharSequence val = Sqls.escapteConditionValue(queryVO.getVal());
                CharSequence startVal = Sqls.escapteConditionValue(queryVO.getStartVal());
                CharSequence endVal = Sqls.escapteConditionValue(queryVO.getEndVal());
                boolean notBlank = Strings.isNotBlank(val);
                switch (queryVO.getJoiner()) {
                    case "=":
                    case "!=":
                    case "<":
                    case "<=":
                    case ">":
                    case ">=":
                        if (notBlank) {
                            querySql.setParam(fieldName, val);
                        }
                        break;
                    case "like":
                    case "not like":
                        if (notBlank) {
                            querySql.setParam(fieldName, "%" + val + "%");
                        }
                        break;
                    case "start like":
                        if (notBlank) {
                            querySql.setParam(fieldName, val + "%");
                        }
                        break;
                    case "end like":
                        if (notBlank) {
                            querySql.setParam(fieldName, "%" + val);
                        }
                        break;
                    case "between":
                    case "not between":
                        if (Strings.isNotBlank(startVal) && Strings.isNotBlank(endVal)) {
                            if (queryVO.getIsDate()) {
                                String dateFormat = Strings.isNotBlank(queryVO.getDateFormat()) ? queryVO.getDateFormat() : DateUtil.YYYY_MM_DD_HH_MM_SS;
                                querySql.setParam(fieldName.concat("_strat"), DateUtil.string2date(startVal.toString(), dateFormat));
                                querySql.setParam(fieldName.concat("_end"), DateUtil.string2date(endVal.toString(), dateFormat));
                            } else {
                                querySql.setParam(fieldName.concat("_strat"), startVal);
                                querySql.setParam(fieldName.concat("_end"), endVal);
                            }
                        }
                        break;
                    default:
                        break;
                }
            });
        }
    }

    @Override
    public List<NutMap> getUniqueFields(int tableid) {
        List<TableFields> tableFields = fieldsService.query(Cnd.where("tableid", "=", tableid).and("validationRulesType", "=", FormValidationRulesType.UNIQUE.getValue()));
        List<NutMap> list = new ArrayList<>();
        tableFields.forEach(fields -> list.add(NutMap.NEW().setv("id", fields.getId()).setv("name", fields.getName())));
        return list;
    }

    @Override
    public NutMap formJsonData(String dataStr, UserAccount userAccount) {
        NutMap data = Json.fromJson(NutMap.class, dataStr);
        String userid = data.getString("userid", "");
        if (!Strings.isEmpty(userid)) {
            data.put("username", accountService.fetch(userid).getUserName());
        }
        return data;
    }


    /**
     * 只针对用户主表，从表慎用
     *
     * @param tableName
     * @param userName
     * @return
     */
    @Override
    public String getSourceId(String tableName, String userName) {
        StringBuilder sb = new StringBuilder("SELECT id FROM $tableName where userName=@userName");
        Sql querySql = Sqls.create(sb.toString());
        querySql.vars().set("tableName", tableName);
        querySql.setParam("userName", userName);
        querySql.setCallback(Sqls.callback.str());
        tableService.dao().execute(querySql);
        return querySql.getString();
    }

    /**
     * 检查是否是自己的数据
     *
     * @param tableName
     * @param userName
     * @return
     */
    @Override
    public boolean isMyData(String tableName, String userName, String sourceId) {
        Sql querySql = Sqls.create("SELECT count(*) FROM $tableName where userName=@userName and id=@sourceId");
        querySql.vars().set("tableName", tableName);
        querySql.setParam("userName", userName);
        querySql.setParam("sourceId", sourceId);
        long count = Daos.queryCount(tableService.dao(), querySql);
        return count > 0;
    }

    /**
     * 根据条件查询用户数据
     *
     * @param pageNum
     * @param pageSize
     * @param tableid
     * @param userid                  用户ID（如不为空表示是按人进行查询的）
     * @param likeUserNameOrRealName  帐号或姓名
     * @param deptIds
     * @param sessionManagerUserNames
     * @return
     */
    @Override
    public LayuiTableDataListVO listDataByCnd(int pageNum, int pageSize, int tableid, String userid, String userName, String likeUserNameOrRealName, String[] deptIds, List<SingeDataMaintainQueryVO> list, Set<String> sessionManagerUserNames) {
        DataTable dataTable = tableService.fetchAllFields(tableid);
        Pager pager = tableService.dao().createPager(pageNum, pageSize);
        List<Record> recordList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        //当前查询的就是系统用户表所以不需要连用户表查询了
        if (Cons.USER_ACCOUNT_TABLE_NAME.equals(dataTable.getTableName())) {
            sb.append("SELECT $showFields,sys_user_account.userName,sys_department.NAME as deptname FROM sys_user_account,sys_department WHERE ");
        } else {
            sb.append("SELECT $showFields,sys_user_account.username,sys_user_account.realname,sys_department.NAME as deptname FROM $tableName, sys_user_account,sys_department");
            sb.append(" WHERE sys_user_account.userid = $tableName.userid AND  ");
        }
        sb.append(" sys_user_account.deptId = sys_department.id AND sys_user_account.userName <>'" + Cons.ADMIN + "' ");
        sb.append(" and FIND_IN_SET(sys_user_account.userName,@sessionManagerUserNames) ");
        String whereSql = getWhereSql(list);
        if (Strings.isNotBlank(whereSql)) {
            sb.append(" and " + whereSql);
        }
        if (Strings.isNotBlank(likeUserNameOrRealName)) {
            sb.append(" and (sys_user_account.username like @likeUserNameOrRealName or sys_user_account.realname like @likeUserNameOrRealName) ");
        }
        if (Strings.isNotBlank(userid)) {
            sb.append(" and sys_user_account.userid=@userid ");
        }
        if (Strings.isNotBlank(userName)) {
            sb.append(" and sys_user_account.userName=@userName ");
        }
        if (deptIds != null && deptIds.length > 0) {
            List<String> deptIdWheres = new ArrayList<>();
            for (String deptId : deptIds) {
                if (!"".equals(deptId.trim())) {
                    deptIdWheres.add("sys_user_account.deptId = '" + Sqls.escapteConditionValue(deptId) + "'");
                }
            }
            if (deptIdWheres.size() > 0) {
                sb.append(" and ( ");
                sb.append(Strings.join(" or ", deptIdWheres));
                sb.append(")");
            }
        }
        sb.append(" order by $tableName.create_by_date");
        List<String> showFields = getQueryFields(dataTable);
        Sql querySql = Sqls.create(sb.toString());
        querySql.vars().set("tableName", Sqls.escapeSqlFieldValue(dataTable.getTableName()));
        querySql.vars().set("showFields", Strings.join(",", showFields));
        Sql countSql = Sqls.create(sb.toString());
        countSql.vars().set("tableName", Sqls.escapeSqlFieldValue(dataTable.getTableName()));
        countSql.vars().set("showFields", Strings.join(",", showFields));
        NutMap param = NutMap.NEW().setv("likeUserNameOrRealName", "%" + likeUserNameOrRealName + "%")
                .setv("sessionManagerUserNames", Strings.join(",", sessionManagerUserNames))
                .setv("userid", userid)
                .setv("userName", userName);
        param.forEach((s, o) -> {
            querySql.setParam(s, o);
            countSql.setParam(s, o);
        });
        setWhereParam(list, querySql);
        querySql.setCallback(Sqls.callback.records());
        querySql.setPager(pager);
        tableService.dao().execute(querySql);
        List<Record> queryRecords = querySql.getList(Record.class);
        long count = Daos.queryCount(tableService.dao(), countSql);
        if (queryRecords != null && queryRecords.size() > 0) {
            recordList.addAll(queryRecords);
        }
        recordList = coverData(recordList, dataTable.getFields());
        return new LayuiTableDataListVO(0, (int) count, recordList);
    }

    /**
     * 取得要查询的字段
     *
     * @return
     */
    @Override
    public List<String> getQueryFields(DataTable dataTable) {
        List<String> showFields = new ArrayList<>();
        dataTable.getFields().forEach(field -> {
            if (field.isLogic()) {
                showFields.add(field.getLogicSqlExpression() + " as " + field.getFieldName());
            } else {
                showFields.add(field.getTableName() + "." + field.getFieldName());
            }
        });
        List<String> systemFields = new ArrayList<>();
        if (dataTable.getTableType() != TableType.SingleTable) {
            systemFields.add("userid");
        }
        systemFields.add(dataTable.getPrimaryKey());
        systemFields.add("update_version");
        for (String fieldName : systemFields) {
            showFields.add(dataTable.getTableName() + "." + fieldName);
        }
        return showFields;
    }


    /**
     * 转化数据-翻译字典ID为中文数据显示-并处理日期
     *
     * @param recordList
     * @param tableFieldsList
     * @return
     */
    @Override
    public List<Record> coverData(List<Record> recordList, List<TableFields> tableFieldsList) {
        List<Record> records = new ArrayList<>();
        //字典转换
        HashMap<String, List<Dict>> dicts = new HashMap<>(0);
        Set<String> names = new HashSet<>(0);
        tableFieldsList.stream().filter(fields -> Strings.isNotBlank(fields.getDictSysCode())).forEach(fields -> {
            List<Dict> dictDetails = dictBiz.list(fields.getDictSysCode());
            dicts.put(fields.getFieldName(), dictDetails);
            names.add(fields.getFieldName());
        });
        recordList.forEach(record -> records.add(coverData(record, dicts, names, tableFieldsList)));
        dicts.clear();
        names.clear();
        return records;
    }


    /**
     * 转化数据-翻译字典ID为中文数据显示-并处理日期
     *
     * @param record
     * @param tableFieldsList
     * @return
     */
    @Override
    public Record coverData(Record record, List<TableFields> tableFieldsList) {
        //字典转换
        HashMap<String, List<Dict>> dicts = new HashMap<>(0);
        Set<String> names = new HashSet<>(0);
        tableFieldsList.stream().filter(fields -> Strings.isNotBlank(fields.getDictSysCode())).forEach(fields -> {
            List<Dict> dictDetails = dictBiz.list(fields.getDictSysCode());
            dicts.put(fields.getFieldName(), dictDetails);
            names.add(fields.getFieldName());
        });
        record = coverData(record, dicts, names, tableFieldsList);
        dicts.clear();
        names.clear();
        return record;
    }

    /**
     * 转化数据-翻译字典ID为中文数据显示-并处理日期
     *
     * @param data
     * @param tableFieldsList
     * @return
     */
    @Override
    public NutMap coverData(NutMap data, List<TableFields> tableFieldsList) {
        Record record = new Record();
        record.putAll(data);
        return NutMap.WRAP(coverData(record, tableFieldsList));
    }

    /**
     * 转化数据-翻译字典ID为中文数据显示-并处理日期
     * 只是为了显示
     *
     * @param record
     * @param tableFieldsList
     * @return
     */
    @Override
    public Record coverDataToView(Record record, List<TableFields> tableFieldsList) {
        record = coverData(record, tableFieldsList);
        Record finalRecord = record;
        tableFieldsList.stream().filter(TableFields::isFromDisplay).forEach(tableFields -> {
            Object val = finalRecord.get(tableFields.getFieldName());
            if (val != null) {
                finalRecord.set(tableFields.getFromLable(), val);
            } else {
                finalRecord.set(tableFields.getFromLable(), "");
            }
            finalRecord.remove(tableFields.getFieldName());
        });
        return finalRecord;
    }

    /**
     * 转化数据-翻译字典ID为中文数据显示-并处理日期
     * 只是为了显示
     *
     * @param data
     * @param tableFieldsList
     * @return
     */
    @Override
    public Record coverDataToView(NutMap data, List<TableFields> tableFieldsList) {
        Record newRecordData = new Record();
        newRecordData.putAll(data);
        return coverDataToView(newRecordData, tableFieldsList);
    }


    /**
     * 转化数据-翻译字典ID为中文数据显示-并处理日期
     *
     * @param record
     * @param dicts
     * @param names
     * @param fieldsList
     * @return
     */
    private Record coverData(Record record, HashMap<String, List<Dict>> dicts, Set<String> names, List<TableFields> fieldsList) {
        names.forEach(fieldName -> {
            Object val = record.get(fieldName);
            List<Dict> discts = dicts.get(fieldName);
            if (val instanceof String) {
                //复选字典
                String[] ss = ((String) val).split(",");
                Set<String> lables = new HashSet<>();
                for (String s : ss) {
                    if (s.trim() == "") {
                        continue;
                    }
                    int dictId = Integer.parseInt(s);
                    discts.stream().filter(dictDetail -> dictDetail.getId() == dictId).forEach(dictDetail -> lables.add(dictDetail.getLable()));
                }
                record.set(fieldName, Strings.join(",", lables));
            } else if (val instanceof BigDecimal || val instanceof Integer) {
                //单选字典
                int dictId = record.getInt(fieldName);
                discts.stream().filter(dictDetail -> dictDetail.getId() == dictId).forEach(dictDetail -> record.set(fieldName, dictDetail.getLable()));
            }
        });
        return coverDate(record, fieldsList);
    }

    /**
     * 转义日期
     *
     * @param record
     * @param fieldList
     * @return
     */
    private Record coverDate(Record record, List<TableFields> fieldList) {
        //日期转换
        fieldList.stream().filter(fields -> fields.getFieldType() == FieldType.Date.getValue() && (fields.getControlType() == ControlType.Date.getValue() || fields.getControlType() == ControlType.DateTime.getValue())).forEach(fields -> {
            Object obj = record.get(fields.getFieldName());
            if (obj != null) {
                Object value = null;
                if (obj instanceof Timestamp) {
                    Timestamp data = (Timestamp) obj;
                    if (fields.getControlType() == ControlType.Date.getValue()) {
                        value = DateUtil.timestamp2string(data, DateUtil.YYYY_MM_DD);
                    } else if (fields.getControlType() == ControlType.DateTime.getValue()) {
                        value = DateUtil.timestamp2string(data, DateUtil.YYYY_MM_DD_HH_MM_SS);
                    }
                } else if (obj instanceof Date) {
                    Date data = (Date) obj;
                    if (fields.getControlType() == ControlType.Date.getValue()) {
                        value = DateUtil.date2string(data, DateUtil.YYYY_MM_DD);
                    } else if (fields.getControlType() == ControlType.DateTime.getValue()) {
                        value = DateUtil.date2string(data, DateUtil.YYYY_MM_DD_HH_MM_SS);
                    }
                } else if (obj instanceof String) {
                    String data = (String) obj;
                    if (fields.getControlType() == ControlType.Date.getValue()) {
                        value = DateUtil.string2date(data, DateUtil.YYYY_MM_DD);
                    } else if (fields.getControlType() == ControlType.DateTime.getValue()) {
                        value = DateUtil.string2date(data, DateUtil.YYYY_MM_DD_HH_MM_SS);
                    }
                }
                record.set(fields.getFieldName(), value);
            }
        });
        return record;
    }


    /**
     * 转换为vuejs支持的表单数据，主要针对复选框和下拉复选
     *
     * @param record
     * @param fieldList
     * @return
     */
    @Override
    public Record coverVueJsFromData(Record record, List<TableFields> fieldList) {
        fieldList.stream().filter(fields -> Strings.isNotBlank(fields.getDictSysCode()) && fields.isMultipleDict()).forEach(fields -> {
            Object obj = record.get(fields.getFieldName());
            if (null == obj) {
                record.set(fields.getFieldName(), new String[]{});
            } else {
                record.set(fields.getFieldName(), String.valueOf(obj).split(","));
            }
        });
        return coverDate(record, fieldList);
    }

    /**
     * 效验数据是否符合设定规则
     *
     * @param tableId
     * @param data
     * @return
     */
    @Override
    public List<String> checkTableData(int tableId, NutMap data, String uniqueField) {
        DataTable dataTable = tableService.fetchAllFields(tableId);
        String uuid = data.getString(uniqueField, "");
        List<String> errorMsgs = new ArrayList<>();
        if (dataTable.getTableType() == TableType.PrimaryTable || dataTable.getTableType() == TableType.Schedule) {
            if (Strings.isBlank(data.getString("userid", "")) || Strings.isBlank(data.getString("username", ""))) {
                errorMsgs.add("人员信息缺失！");
            }
        }
        dataTable.getFields().stream().filter(fields -> fields.isFromDisplay() && !fields.isLogic()).forEach(fields -> {
            String msg = check(fields, data.get(fields.getFieldName()), uuid, uniqueField, 0, dataTable.getTableType());
            if (msg != null) {
                errorMsgs.add(msg);
            }
        });
        return errorMsgs;
    }


    /**
     * 效验数据是否符合设定规则-忽略附件-忽略依赖值
     *
     * @param fieldList
     * @param data
     * @param uniqueField
     * @param importType
     * @param tableType
     * @return
     */
    @Override
    public List<String> checkImportTableData(List<TableFields> fieldList, NutMap data, String uniqueField, int importType, TableType tableType) {
        String uuid = data.getString("id", "");
        List<String> errorMsgs = new ArrayList<>();
        fieldList.stream().filter(fields ->
                (fields.getFieldType() != FieldType.SingleAttach.getValue() && fields.getFieldType() != FieldType.MultiAttach.getValue()) && fields.getDictDepend() == DictDepend.NONE.getValue()
        ).forEach(fields -> {
            String msg = check(fields, data.get(fields.getFieldName()), uuid, uniqueField, importType, tableType);
            if (msg != null) {
                errorMsgs.add(msg);
            }
        });
        return errorMsgs;
    }

    /**
     * 效验数据是否符合设定规则
     *
     * @param fields
     * @param value
     * @param importType
     * @param tableType
     * @return
     */
    private String check(TableFields fields, Object value, String uuid, String uniqueField, int importType, TableType tableType) {
        String msg = null;
        if (fields.getDictDepend() > 0 || fields.isLogic()) {
            //是依赖字段/逻辑字段，不验证
            return null;
        }
        String val = String.valueOf(value);
        String lableName = fields.getFromLable();
        if (Strings.isNotBlank(fields.getDictSysCode()) && val.indexOf(DataImportPoiUtil.IMPORT_DICT_NOT_FOUND) > -1) {
            //是字典
            String[] ss = val.split(DataImportPoiUtil.IMPORT_DICT_NOT_FOUND);
            return MessageFormat.format("{0} 枚举不存在!", ss[1]);
        }
        if (!fields.isNullValue() && Strings.isBlank(Strings.sNull(value))) {
            return MessageFormat.format("{0}是必填项，请勿遗漏!", lableName);
        } else //不是必填项 并且值是 空的
            if (fields.isNullValue() && Strings.isBlank(Strings.sNull(value).trim())) {
                return null;
            }
        switch (fields.getFieldType()) {
            case 0:
                //字符串
                if (val.length() > fields.getLength()) {
                    msg = MessageFormat.format("{0} 字段长度不能大于{1}!", lableName, fields.getLength());
                }
                break;
            case 1:
                if (!RegexUtil.isNum(val)) {
                    msg = MessageFormat.format("{0}请输入数字!", lableName, fields.getLength());
                }
                if (val.indexOf(".") > 0) {
                    String[] ss = Strings.splitIgnoreBlank(val, "\\.");
                    int integer = ss[0].replace("-", "").length();
                    int decimal = ss[1].length();
                    if (integer > fields.getLength() - fields.getDecimalPoint()) {
                        msg = MessageFormat.format("{0} 字段整数部分长度不能大于{1}!", lableName, fields.getLength() - fields.getDecimalPoint());
                    }
                    if (decimal > fields.getDecimalPoint()) {
                        msg = MessageFormat.format("{0} 字段小数部分长度不能大于{1}!", lableName, fields.getDecimalPoint());
                    }
                    if (integer + decimal > fields.getLength()) {
                        msg = MessageFormat.format("{0} 字段长度不能大于{1}!", lableName, fields.getLength());
                    }
                } else if (val.length() > fields.getLength() - fields.getDecimalPoint()) {
                    msg = MessageFormat.format("{0} 字段整数部分长度不能大于{1}，小数部分长度不能大于{2}!", lableName, fields.getLength() - fields.getDecimalPoint(), fields.getDecimalPoint());
                }
                break;
            case 2:
                String format = DateUtil.YYYY_MM_DD_HH_MM_SS;
                if (fields.getControlType() == 5) {
                    format = DateUtil.YYYY_MM_DD;
                }
                if (!(value instanceof java.util.Date || value instanceof java.sql.Date || value instanceof Timestamp)) {
                    if (DateUtil.string2date(val, format) == null) {
                        msg = MessageFormat.format("{0} 字段格式不正确，请输入{1}格式的日期!", lableName, format);
                    }
                }
                break;
            case 3:
                if (val.length() > 65533) {
                    msg = MessageFormat.format("{0} 字段长度不能大于{1}!", lableName, fields.getLength());
                }
                break;
            case 4:
                if (val.length() > 26) {
                    msg = MessageFormat.format("{0}只能上传一个文件!", lableName);
                }
                break;
            case 5:
                if (val.length() > 550) {
                    msg = MessageFormat.format("{0}只能上传20个文件!", lableName);
                }
                break;
            default:
                break;
        }
        if (msg == null) {
            msg = checkValidationRules(lableName, fields, value, uuid, uniqueField, importType, tableType);
        }
        return msg;
    }


    /**
     * 效验数据是否符合设定规则
     *
     * @param lableName
     * @param fields
     * @param value
     * @param uuid
     * @param uniqueField
     * @param importType
     * @param tableType
     * @return
     */
    private String checkValidationRules(String lableName, TableFields fields, Object value, String uuid, String uniqueField, int importType, TableType tableType) {
        String msg = null;
        String val = String.valueOf(value);
        FormValidationRulesType formValidationRulesType = FormValidationRulesType.valueOf(fields.getValidationRulesType());
        switch (formValidationRulesType) {
            case UNIQUE:
                if (!(tableType == TableType.SingleTable && uniqueField.equals(fields.getFieldName()) && importType > 1)) {
                    /**
                     * 1、是单表
                     * 2、是选择的唯一字段
                     * 3、导入模式不是导入全部记录
                     * 那么该字段数据则忽略，否则进行唯一验证
                     **/
                    Cnd cnd = Cnd.where(fields.getFieldName(), "=", value);
                    if (!Strings.isBlank(uuid)) {
                        //修改排除自己
                        cnd.and("id", "!=", uuid);
                    }
                    //唯一效验
                    if (tableService.count(fields.getTableName(), cnd) > 0) {
                        msg = MessageFormat.format("{0}字段【 {1} 】值已存在，不能重复添加!", lableName, value);
                    }
                }
                break;
            case NON_EMPTY:
                //非空
                if (Strings.isBlank(val)) {
                    msg = MessageFormat.format("{0}字段不能为空!", lableName);
                }
                break;
            case NUMBER:
                //数字
                if (!RegexUtil.isNum(val)) {
                    msg = MessageFormat.format("{0}字段请输入数字!当前值：({1})", lableName, val);
                }
                break;
            case LETTER:
                //字母
                if (!RegexUtil.isA_z(val)) {
                    msg = MessageFormat.format("{0}字段请输入字母!当前值：({1})", lableName, val);
                }
                break;
            case PHONE:
                if (!RegexUtil.isPhone(val)) {
                    msg = MessageFormat.format("{0}字段请输入正确的手机号码!当前值：({1})", lableName, val);
                }
                break;
            case EMAIL:
                if (!RegexUtil.isEmail(val)) {
                    msg = MessageFormat.format("{0}字段请输入正确的电子邮件!当前值：({1})", lableName, val);
                }
                break;
            case URL:
                if (!RegexUtil.isHTTP(val)) {
                    msg = MessageFormat.format("{0}字段请输入正确的网址!当前值：({1})", lableName, val);
                }
                break;
            case CHINESE:
                if (!RegexUtil.isChinese(val)) {
                    msg = MessageFormat.format("{0}字段请输入中文!当前值：({1})", lableName, val);
                }
                break;
            case POSTAL:
                if (!RegexUtil.isPOST(val)) {
                    msg = MessageFormat.format("{0}字段请输入正确的邮政编码!当前值：({1})", lableName, val);
                }
                break;
            case STRING6_18:
                if (val.length() < 6 || val.length() > 18) {
                    msg = MessageFormat.format("{0}字段请输入6到18位字符串!", lableName);
                }
                break;
            default:
                break;
        }
        return msg;
    }

    /**
     * 保存数据
     *
     * @param tableId
     * @param data
     * @param userAccount
     */
    @Override
    public void saveTableData(int tableId, NutMap data, UserAccount userAccount) {
        DataTable dataTable = tableService.fetchAllFields(tableId);
        data.put(".table", dataTable.getTableName());
        String primaryKey = dataTable.getPrimaryKey();
        String uuid = data.getString(primaryKey, "");
        data = coverSaveTableData(dataTable.getFields(), data);
        if (Strings.isBlank(uuid)) {
            data.setv(primaryKey, R.UU16());
            data = DataUtil.coverInsertData(data, userAccount);
            tableService.dao().insert(data);
        } else {
            data.put("+*".concat(primaryKey), uuid);
            data = DataUtil.coverUpdateData(data, userAccount, data.getInt("update_version", 1));
            tableService.dao().update(data);
        }
    }


    /**
     * 保存数据-等待审核
     *
     * @param tableId
     * @param newData
     */
    @Override
    public void saveReviewData(int tableId, NutMap newData, String addUserId) {
        DataTable dataTable = tableService.fetchAllFields(tableId);
        newData.put(".table", dataTable.getTableName());
        String primaryKey = dataTable.getPrimaryKey();
        String sourceId = newData.getString(primaryKey, "");
        newData = coverSaveTableData(dataTable.getFields(), newData);
        String userId = newData.getString("userid");
        UserDataChangeHistory changeHistory = null;
        if (dataTable.getTableType() == TableType.PrimaryTable) {
            //主表只能有一条记录--应当查询是否有重复的审核记录
            changeHistory = userDataChangeHistoryService.fetch(
                    Cnd.where("userid", "=", userId)
                            .and("tableId", "=", tableId)
                            .and("review", "=", 0));
        } else if (dataTable.getTableType() == TableType.Schedule && Strings.isNotBlank(sourceId)) {
            //是从表 并且是编辑
            changeHistory = userDataChangeHistoryService.fetch(
                    Cnd.where("userid", "=", userId)
                            .and("tableId", "=", tableId)
                            .and("review", "=", 0)
                            .and("sourceId", "=", sourceId));
        }
        if (null == changeHistory) {
            changeHistory = new UserDataChangeHistory();
        }
        Record oldData = new Record();
        if (Strings.isBlank(sourceId)) {
            newData.put(primaryKey, R.UU16());
            //新增记录
            changeHistory.setStatus(0);
        } else {
            newData.put("+*".concat(primaryKey), sourceId);
            oldData = tableService.dao().fetch(dataTable.getTableName(), Cnd.where(primaryKey, "=", sourceId), getFieldsRegx(dataTable.getFields()));
            if (oldData == null) {
                oldData = new Record();
            }
            //修改记录
            changeHistory.setStatus(1);
        }
        List<ReviewChangeVO> reviewChangeVOList = getChangeVOList(NutMap.WRAP(oldData), newData, dataTable);
        changeHistory.setUserId(userId);
        changeHistory.setNewDataJson(Json.toJson(newData));
        changeHistory.setDataChangeJson(Json.toJson(reviewChangeVOList, JsonFormat.tidy()));
        changeHistory.setTableId(tableId);
        changeHistory.setReview(0);
        changeHistory.setAddDate(new Date());
        changeHistory.setAddUserId(addUserId);
        changeHistory.setSourceId(sourceId);
        userDataChangeHistoryService.insertOrUpdate(changeHistory);
    }

    @Override
    public void delToReview(int tableId, String[] sourceIds, String delUserId) {
        DataTable dataTable = tableService.fetchAllFields(tableId);
        List<Record> changes = new ArrayList<>();
        for (String sourceId : sourceIds) {
            changes.add(coverDataToView(fetchData(dataTable.getTableName(), sourceId), dataTable.getFields()));
        }
        UserDataChangeHistory changeHistory = new UserDataChangeHistory();
        changeHistory.setUserId(delUserId);
        changeHistory.setDataChangeJson(Json.toJson(changes, JsonFormat.tidy()));
        changeHistory.setDelIdsJson(Json.toJson(sourceIds, JsonFormat.tidy()));
        changeHistory.setTableId(tableId);
        changeHistory.setReview(0);
        changeHistory.setStatus(2);
        changeHistory.setAddDate(new Date());
        changeHistory.setAddUserId(delUserId);
        userDataChangeHistoryService.insertOrUpdate(changeHistory);
    }

    /**
     * 取得变更记录信息
     *
     * @param finalOldData
     * @param finalNewData
     * @param dataTable
     * @return
     */
    private List<ReviewChangeVO> getChangeVOList(NutMap finalOldData, NutMap finalNewData, DataTable dataTable) {
        List<ReviewChangeVO> reviewChangeVOList = new ArrayList<>();
        NutMap finalOldDataView = coverData(finalOldData, dataTable.getFields());
        NutMap finalNewDataView = coverData(finalNewData, dataTable.getFields());
        dataTable.getFields().stream().filter(TableFields::isFromDisplay).forEach(tableFields -> {
            ReviewChangeVO vo = new ReviewChangeVO();
            vo.setLable(tableFields.getFromLable());
            vo.setNewValue(finalNewDataView.getString(tableFields.getFieldName(), ""));
            vo.setOldValue(finalOldDataView.getString(tableFields.getFieldName(), ""));
            vo.setChange(!vo.getNewValue().equals(vo.getOldValue()));
            vo.setFieldType(tableFields.getFieldType());
            if (tableFields.getFieldType() == FieldType.SingleAttach.getValue() || tableFields.getFieldType() == FieldType.MultiAttach.getValue()) {
                vo.setNewAttachNum(Strings.splitIgnoreBlank(finalNewData.getString(tableFields.getFieldName(), "")).length);
                vo.setOldAttachNum(Strings.splitIgnoreBlank(finalOldData.getString(tableFields.getFieldName(), "")).length);
            }
            reviewChangeVOList.add(vo);
        });
        return reviewChangeVOList;
    }


    /**
     * 保存数据前转换下不符合数据库规则的数据
     *
     * @param tableFields
     * @param data
     */
    @Override
    public NutMap coverSaveTableData(List<TableFields> tableFields, NutMap data) {
        tableFields.stream().filter(TableFields::isFromDisplay).forEach(f -> {
            Object val = data.get(f.getFieldName());
            if (val != null) {
                String valStr = Strings.sNull(val).trim();
                switch (f.getFieldType()) {
                    case 1:
                        //数值型
                        if (val instanceof String && Strings.isNotBlank(valStr)) {
                            data.setv(f.getFieldName(), BigDecimal.valueOf(Double.parseDouble(valStr)));
                        } else if (!(val instanceof BigDecimal || val instanceof Number || val instanceof Double || val instanceof Float || val instanceof Integer)) {
                            data.setv(f.getFieldName(), null);
                        }
                        break;
                    case 2:
                        //日期
                        if (val instanceof String && Strings.isNotBlank(valStr)) {
                            String format = DateUtil.YYYY_MM_DD_HH_MM_SS;
                            if (f.getControlType() == ControlType.Date.getValue()) {
                                format = DateUtil.YYYY_MM_DD;
                            }
                            data.setv(f.getFieldName(), DateUtil.string2date(valStr, format));
                        } else if (!(val instanceof java.util.Date || val instanceof java.sql.Date || val instanceof java.sql.Timestamp)) {
                            data.setv(f.getFieldName(), null);
                        }
                        break;
                    default:
                        break;
                }
            }
            //不修改逻辑字段值
            if (f.isLogic()) {
                data.remove(f.getFieldName());
            }
        });
        //所有依赖字段
        List<TableFields> dictDependFieldIdList = new ArrayList<>();
        tableFields.stream().filter(fields -> fields.getDictDepend() > DictDepend.NONE.getValue()).forEach(fields -> dictDependFieldIdList.add(fields));
        //全部字典字段
        List<TableFields> dictFields = new ArrayList<>();
        tableFields.stream().filter(fields -> Strings.isNotBlank(fields.getDictSysCode())).forEach(fields -> dictFields.add(fields));
        dictFields.forEach(fields -> {
            //当前依赖本字段的所有依赖字段
            List<TableFields> tempDictDependFieldIdList = new ArrayList<>();
            dictDependFieldIdList.stream().filter(fields1 -> fields1.getDictDependFieldId() == fields.getId()).forEach(fields1 -> tempDictDependFieldIdList.add(fields1));
            if (tempDictDependFieldIdList.size() > 0) {
                //字典值
                int dictVal = data.getInt(fields.getFieldName());
                List<DictDependentChangeVO> dictDependentChangeVOS = dictBiz.dictDependentChangeList(dictVal, fields.getDictSysCode(), tempDictDependFieldIdList);
                dictDependentChangeVOS.forEach(dictDependentChangeVO -> data.put(dictDependentChangeVO.getKey(), dictDependentChangeVO.getValue()));
            }
        });
        return data;
    }


    /**
     * 查询表记录的字段组成表达式给dao查询
     *
     * @param fieldsList
     * @return
     */
    @Override
    public String getFieldsRegx(List<TableFields> fieldsList) {
        Set<String> sets = new HashSet<>(fieldsList.size() + 2);
        sets.add("id");
        sets.add("userid");
        fieldsList.stream().filter(TableFields::isFromDisplay).forEach(fields -> sets.add(fields.getFieldName()));
        return Strings.join(",", sets);
    }

    /**
     * 创建下载模版
     *
     * @param tableId
     * @param fieldlist
     * @return
     * @throws IOException
     */
    @Override
    public Path createDownTemplate(int tableId, int[] fieldlist) throws IOException {
        DataTable dataTable = tableService.fetch(tableId);
        dataTable.setFields(fieldsService.query(Cnd.where("id", "in", fieldlist)));
        Path template = FileUtil.createTempFile();
        PoiExcelUtil util = PoiExcelUtil.createNewExcel();
        String sheetName = dataTable.getName() + "导入版本" + dataTable.getVersion();
        List<String[]> strings = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        int startColIndex = 0;
        if (dataTable.getTableType() != TableType.SingleTable) {
            startColIndex = 1;
            headers.add("用户名");
        }
        dataTable.getFields().stream().filter(fields -> !(fields.getFieldType() == FieldType.MultiAttach.getValue() || fields.getFieldType() == FieldType.SingleAttach.getValue()))
                .forEach(fields -> headers.add(fields.getName() + (fields.isNullValue() ? "" : "(必填)") + (fields.isMultipleDict() ? "(允许多选)" : "")));
        //按顺序依次写入
        strings.add(headers.toArray(new String[0]));
        util.createSheet(sheetName);
        //创建sheet页记录字段信息
        String infoSheetName = R.UU16().substring(0, 15);
        util.createSheet(infoSheetName);
        Set<Integer> integers = new HashSet<>();
        for (int i : fieldlist) {
            integers.add(i);
        }
        //记录字段信息
        util.setClleValAndHiden(infoSheetName, 0, 0, Base64.encodeToString(Strings.join(",", integers).getBytes(), true), R.UU16());
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
        //以隐藏sheet页方式写下拉字典
        List<Integer> errorCanInputAddressList = new ArrayList<>();
        List<String[]> errorCanInputTextlist = new ArrayList<>();
        List<Integer> errorCanNotInputAddressList = new ArrayList<>();
        List<String[]> errorCanNotInputTextlist = new ArrayList<>();

        //POI起始位置0 用户名占一个位置startColIndex=1 所以开始为startColIndex + 1
        int colIndex = startColIndex;
        for (int i = 0, l = dataTable.getFields().size(); i < l; i++) {
            TableFields fields = dataTable.getFields().get(i);
            if (fields.getFieldType() == FieldType.MultiAttach.getValue() || fields.getFieldType() == FieldType.SingleAttach.getValue()) {
                //忽略附件字段
                continue;
            }
            //是字典
            if (Strings.isNotBlank(fields.getDictSysCode())) {
                if (fields.isMultipleDict()) {
                    errorCanNotInputTextlist.add(corverExcleCell(fields.getDictSysCode()));
                    errorCanNotInputAddressList.add(colIndex);
                } else {
                    errorCanInputTextlist.add(corverExcleCell(fields.getDictSysCode()));
                    errorCanInputAddressList.add(colIndex);
                }
            } else {
                if (fields.getFieldType() == FieldType.Date.getValue()) {
                    //默认日期格式
                    String format = DateUtil.YYYY_MM_DD_HH_MM_SS;
                    //日期格式
                    if (fields.getControlType() == ControlType.Date.getValue()) {
                        format = DateUtil.YYYY_MM_DD;
                    }
                    util.setColumnDateFormat(sheetName, colIndex, format);
                }
                if (fields.getFieldType() == FieldType.String.getValue() || fields.getFieldType() == FieldType.Text.getValue()) {
                    //文本型
                    util.setColumnTextFormat(sheetName, colIndex, fields.getLength());
                }
                if (fields.getFieldType() == FieldType.Decimal.getValue()) {
                    //数值型
                    util.setColumnDecimalFormat(sheetName, colIndex, fields.getLength(), fields.getDecimalPoint());
                }
            }
            colIndex++;
        }
        util.setSheetValidation(sheetName, errorCanInputAddressList, errorCanInputTextlist, false);
        util.setSheetValidation(sheetName, errorCanNotInputAddressList, errorCanNotInputTextlist, true);
        util.toCreateNewFile(template.toFile());
        return template;
    }

    /**
     * 转换成excle中选择项目
     *
     * @param sysCode
     * @return
     */
    private String[] corverExcleCell(String sysCode) {
        List<Dict> dictDetails = dictBiz.list(sysCode);
        List<String> ss = new ArrayList<>();
        dictDetails.forEach(dict -> {
            //判断是否在当前枚举中存在重复
            if (duplicate(dict, dictDetails)) {
                ss.add(dict.getLable().concat(DELIMITER).concat(String.valueOf(dict.getId())));
            } else {
                ss.add(dict.getLable());
            }
        });
        return ss.toArray(new String[0]);
    }

    /**
     * 判断是否在当前字段类型中存在重复
     *
     * @param dict
     * @param dictList
     * @return
     */
    private boolean duplicate(Dict dict, List<Dict> dictList) {
        return dictList.stream().filter(dict1 -> dict.getId() != dict1.getId() && dict.getLable().equals(dict1.getLable())).findAny().isPresent();
    }

    @Override
    public String importFileAttach(int tableId, int filedId, String[] attachIds, int uniqueField, UserAccount userAccount) {
        DataTable dataTable = tableService.fetch(tableId);
        List<FileAttach> fileAttachList = fileAttachService.query(Cnd.where("id", "in", attachIds));
        TableFields tableFields = fieldsService.fetch(filedId);
        TableFields uniqueFields = fieldsService.fetch(uniqueField);
        String tableName = tableFields.getTableName();
        String fieldName = tableFields.getFieldName();
        List<String> errorsUserNames = new ArrayList<>();
        List<FileAttach> attaches = new ArrayList<>();
        for (FileAttach attach : fileAttachList) {
            try {
                String uniqueValue = getUniqueValueByAttachName(attach);
                Record old;
                if (dataTable.getTableType() == TableType.SingleTable) {
                    old = getOldSingleTableData(dataTable.getPrimaryKey(), tableName, uniqueFields.getFieldName(), uniqueValue);
                } else {
                    old = getOldTableData(dataTable.getPrimaryKey(), tableName, fieldName, uniqueValue);
                }

                if (old != null) {
                    String attachIdStr = old.getString(fieldName);
                    attachIdStr = getAttachIds(tableFields, attach, attachIdStr);
                    if (attachIdStr != null) {
                        NutMap nutMap = new NutMap();
                        nutMap.put(".table", tableName);
                        nutMap.put(dataTable.getPrimaryKey(), old.getString(dataTable.getPrimaryKey()));
                        nutMap.put(fieldName, attachIdStr);
                        nutMap.put("update_by_date", new Date());
                        nutMap.put("update_by_name", userAccount.getRealName());
                        nutMap.put("update_by_userid", userAccount.getUserid());
                        nutMap.put("update_version", old.getInt("update_version") + 1);
                        attach.setAttachtype("datatable");
                        attaches.add(attach);
                        tableService.update(nutMap);
                    }
                } else {
                    fileAttachService.deleteFile(attach);
                    errorsUserNames.add(uniqueValue);
                }
            } catch (Exception e) {
                //单个文件异常不影响其他文件处理
            }
        }
        fieldsService.update(attaches);
        if (errorsUserNames.size() > 0) {
            return MessageFormat.format("本次导入附件已忽略以下数据:[ {0} ]，系统中不存在相关数据！", Strings.join(",", errorsUserNames));
        }
        return null;
    }


    private String getAttachIds(TableFields tableFields, FileAttach attach, String attachIdStr) {
        if (tableFields.getFieldType() == FieldType.MultiAttach.getValue()) {
            String[] ids = Strings.splitIgnoreBlank(Strings.sNull(attachIdStr));
            List<String> old = new ArrayList<>();
            for (String id : ids) {
                old.add(id);
            }
            if (old.size() < 20) {
                old.add(attach.getId());
                return Strings.join(",", old);
            } else {
                //附件超了,需要删除这个新上传的附件
                fileAttachService.deleteFile(attach);
                return null;
            }
        } else {
            return attach.getId();
        }
    }

    private Record getOldSingleTableData(String primaryKey, String tableName, String fieldName, String uniqueValue) {
        Sql querySql = Sqls.create("SELECT $primaryKey,update_version,$fieldName FROM $tableName where $fieldName=@uniqueValue");
        querySql.vars().set("primaryKey", primaryKey);
        querySql.vars().set("tableName", tableName);
        querySql.vars().set("fieldName", fieldName);
        querySql.setParam("uniqueValue", Sqls.escapeSqlFieldValue(uniqueValue));
        querySql.setCallback(Sqls.callback.record());
        tableService.execute(querySql);
        return querySql.getObject(Record.class);
    }


    private Record getOldTableData(String primaryKey, String tableName, String fieldName, String userName) {
        Sql querySql = Sqls.create("SELECT $primaryKey,update_version,$fieldName FROM $tableName where userid=(SELECT userid FROM sys_user_account WHERE userName=@userName)");
        querySql.vars().set("primaryKey", primaryKey);
        querySql.vars().set("tableName", tableName);
        querySql.vars().set("fieldName", fieldName);
        querySql.setParam("userName", Sqls.escapeSqlFieldValue(userName));
        querySql.setCallback(Sqls.callback.record());
        tableService.execute(querySql);
        return querySql.getObject(Record.class);
    }

    private String getUniqueValueByAttachName(FileAttach attach) {
        int start = attach.getFileName().trim().indexOf(".");
        String fileName = attach.getFileName().trim().substring(0, start);
        return fileName;
    }
}
