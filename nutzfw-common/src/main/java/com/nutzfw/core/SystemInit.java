package com.nutzfw.core;

import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.plugin.quartz.BaseJob;
import com.nutzfw.core.plugin.redis.RedisHelpper;
import com.nutzfw.modules.common.action.FileAction;
import com.nutzfw.modules.monitor.entity.AlarmOption;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.sys.biz.DictBiz;
import com.nutzfw.modules.sys.entity.*;
import com.nutzfw.modules.tabledata.entity.DataTableVersionHistory;
import com.nutzfw.modules.tabledata.enums.FieldType;
import com.nutzfw.modules.tabledata.enums.TableType;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.random.R;
import org.nutz.resource.Scans;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/6/4
 * 描述此类：
 */
public class SystemInit {
    /**
     * 字典分隔符
     */
    final static String DELIMITER = "→→";
    /**
     * 附件字典类型-初始化值
     */
    protected static HashMap<String, String> attachType = new HashMap<>(6);

    /**
     * 初始化定时任务
     */
    protected static List<QuartzJob> quartzJobs = new ArrayList<>();
    /**
     * 这些表不需要自动创建
     */
    protected static Set<Class<?>> tablesFilters = new HashSet<>();

    /**
     * 清除Redis缓存
     */
    public static void cleanRedis(RedisService redisService) {
        //清除字典缓存
        Set<String> lists = redisService.keys(RedisHelpper.buildRediskey("*"));
        if (lists.size() > 0) {
            redisService.del(lists.toArray(new String[]{}));
        }
    }

    protected static QuartzJob buildeQuartJob(Class<? extends BaseJob> jobClass, String corn, String name, String desc, boolean autoStartRun, String args) {
        return QuartzJob.builder()
                .jobName(name)
                .jobShort(0)
                .jobCorn(corn)
                .jobKlass(jobClass.getName())
                .jobLastStatus(true)
                //0随服务启动|1手动启动
                .jobType(autoStartRun ? 0 : 1)
                .args(args == null ? "{}" : args)
                .jobLastStatus(true)
                .jobDesc(desc)
                .build();
    }

    /**
     * 添加定时任务-已经存在的任务会忽略导入
     *
     * @param jobList
     * @param dao
     * @return
     */
    protected void addQuartJob(List<QuartzJob> jobList, Dao dao) {
        List<QuartzJob> jobs = new ArrayList<>();
        Sql sql = Sqls.create("select job_klass from ".concat(QuartzJob.class.getAnnotation(Table.class).value()));
        sql.setCallback(Sqls.callback.strList());
        dao.execute(sql);
        List<String> list = sql.getList(String.class);
        jobList.stream().filter(quartzJob -> !list.contains(quartzJob.getJobKlass())).forEach(quartzJob -> jobs.add(quartzJob));
        dao.insert(jobs);
    }

    /**
     * 自动创建表
     *
     * @param dao
     */
    public void initTable(Dao dao, boolean force) {
        //自动创建表
        Daos.createTablesInPackage(dao, "com.nutzfw", force, (klass, table) -> !tablesFilters.contains(klass));
        //统一据库表结构-不删除已有字段避免已经创建的动态字段被删
        for (Class<?> klass : Scans.me().scanPackage("com.nutzfw")) {
            if (klass.getAnnotation(Table.class) != null && !tablesFilters.contains(klass)) {
                Daos.migration(dao, klass, true, false, false, null);
            }
        }
    }

    /**
     * 创建系统基础数据
     *
     * @param dao
     */
    public void initSystemInfo(Dao dao, PropertiesProxy enumsConf) {
        //创建管理员
        String salt = R.UU16();
        Sha256Hash sha = new Sha256Hash(Cons.DEFAULT_PASSWORD, salt);
        UserAccount userAccount = UserAccount.builder()
                .locked(false).salt(salt).userName(Cons.ADMIN).userPass(sha.toHex())
                .createByDate(new Date(System.currentTimeMillis()))
                .createByName("自动创建").realName("超级管理员").review(1).reviewOpinion("无").build();
        dao.insert(userAccount);
        //创建超级管理员角色
        Role role = Role.builder().locked(false).roleName("超级管理员").roleCode("superadmin").shortNo(0).build();
        dao.insert(role);
        /**
         * 初始化系统信息
         */
        Options options = Options.builder()
                .errorPassInputTimes(5).fristLoginNeedChangePass(false)
                .unitName("某某集团")
                .version("3.2.1").productLongName("NutzFw快速开发框架")
                .productSortName("NutzFw").productLogo("")
                .productEnLongName("NutzFw SOFT PLATFORM")
                .passExpired(3).passStrength(0).theme(2)
                .needVerificationCode(1)
                .registrationTime(new Timestamp(System.currentTimeMillis()))
                .build();
        dao.insert(options);
        /**
         * 初始化监控信息
         */
        List<AlarmOption> alarmOptionList = new ArrayList<>();
        List<String> listenerTypes = Arrays.asList("CPU", "DISK", "JVM", "RAM", "SWAP");
        for (String listenerType : listenerTypes) {
            AlarmOption alarmOption = new AlarmOption();
            alarmOption.setAlarmType(listenerType);
            alarmOption.setSms(true);
            alarmOption.setEmail(true);
            alarmOption.setPercent(90.00);
            alarmOption.setListeners(userAccount.getId());
            alarmOption.setListenersDesc(userAccount.getUserName());
            alarmOptionList.add(alarmOption);
        }
        dao.insert(alarmOptionList);
        /**
         * 初始化系统字典
         */
        createInternalDictDetails(dao, enumsConf);
        /**
         * 初始化系统表信息
         */
        createSystemDataTable(dao);
    }

    /**
     * 创建系统表
     *
     * @param dao
     */
    private void createSystemDataTable(Dao dao) {
        DataTable dataTable = DataTable.builder()
                .name("个人基本信息表").tableType(TableType.PrimaryTable).comment("个人基本信息表").status(1).system(true)
                .formTemplate(1).tableName(Cons.USER_ACCOUNT_TABLE_NAME).build();
        if (dao.count(DataTable.class.getAnnotation(Table.class).value()) == 0) {
            dao.insert(dataTable);
            List<TableFields> fields = new ArrayList<>();
            fields.add(TableFields.builder()
                    .tableId(dataTable.getId()).tableName(dataTable.getTableName()).system(true).controlType(0)
                    .fieldName("username").comment("用户名").fromLable("用户名").name("用户名").length(50)
                    .nullValue(false).defaultValue("NULL").fieldType(FieldType.String.getValue()).fromDisplay(true).validationRulesType(-1)
                    .build());
            fields.add(TableFields.builder()
                    .tableId(dataTable.getId()).tableName(dataTable.getTableName()).system(true).controlType(0)
                    .fieldName("realname").comment("真实姓名").fromLable("真实姓名").name("真实姓名").length(50)
                    .nullValue(false).defaultValue("NULL").fieldType(FieldType.String.getValue()).fromDisplay(true).validationRulesType(-1)
                    .build());
            fields.add(TableFields.builder()
                    .tableId(dataTable.getId()).tableName(dataTable.getTableName()).system(true).controlType(0)
                    .fieldName("phone").comment("手机号").fromLable("手机号").name("手机号").length(20)
                    .nullValue(false).defaultValue("NULL").fieldType(FieldType.String.getValue()).fromDisplay(true).validationRulesType(4)
                    .build());
            fields.add(TableFields.builder()
                    .tableId(dataTable.getId()).tableName(dataTable.getTableName()).system(true).controlType(0)
                    .fieldName("mail").comment("电子邮箱").fromLable("电子邮箱").name("电子邮箱").length(20)
                    .nullValue(false).defaultValue("NULL").fieldType(FieldType.String.getValue()).fromDisplay(true).validationRulesType(5)
                    .build());
            fields.add(TableFields.builder()
                    .tableId(dataTable.getId()).tableName(dataTable.getTableName()).system(true).controlType(0)
                    .fieldName("gender").comment("性别").fromLable("性别").name("性别").length(10)
                    .nullValue(false).defaultValue("NULL").fieldType(FieldType.String.getValue()).dictSysCode("sys_user_sex")
                    .dictSysCodeDesc("性别").fromDisplay(true).validationRulesType(-1)
                    .build());
            dao.insert(fields);
            //建立版本历史信息
            dataTable.setFields(fields);
            dao.insert(new DataTableVersionHistory(dataTable.getId(), dataTable.getVersion(), Json.toJson(dataTable, JsonFormat.compact())));
        }
    }

    private void createInternalDictDetails(Dao dao, PropertiesProxy enumsConf) {
        List<Dict> dictDetails = new ArrayList<>();
        enumsConf.forEach((key, val) -> {
            key = key.trim();
            int start = val.indexOf(DELIMITER);
            String name = val.substring(0, start).trim();
            val = val.substring(start + 2).trim();
            Dict parentDict = Dict.builder().sysCode(key).pid(0).lable(name).val("").showType(0).grouping(true)
                    .internal(true).shortNo(0).mark("").defaultVal(false)
                    .build();
            dao.insert(parentDict);
            dictDetails.addAll(createInternalDictDetail(parentDict, val));
        });
        dao.insert(dictDetails);
    }

    private List<Dict> createInternalDictDetail(Dict parentDict, String val) {
        List<Dict> list = new ArrayList<>();
        String[] ss = val.split(",");
        for (int i = 0; i < ss.length; i++) {
            String s = ss[i];
            String[] temp = s.split("=");
            Dict detail = Dict.builder()
                    .pid(parentDict.getId()).grouping(false).internal(true).defaultVal(false)
                    .sysCode(parentDict.getSysCode()).lable(temp[0].trim()).val(temp[1].trim()).shortNo(i).mark("")
                    .build();
            list.add(detail);
        }
        return list;
    }

    /**
     * 如果不存在则返回新建的字典分组
     *
     * @param dictBiz
     * @param sysCode
     * @param lable
     * @return
     */
    public Dict ifNotExistCreateDictGroup(DictBiz dictBiz, String sysCode, String lable) {
        Dict dict = dictBiz.getDict(FileAction.SYS_ATTACH_TYPE);
        if (dict == null) {
            dict = dictBiz.addDict(Dict.builder().lable(lable).grouping(true).internal(true).sysCode(sysCode).val("").edit(false).build());
        }
        return dict;
    }


    public void checkAttachTypeDict(DictBiz dictBiz, HashMap<String, String> stringStringHashMap) {
        Dict parentDict = ifNotExistCreateDictGroup(dictBiz, FileAction.SYS_ATTACH_TYPE, "附件类型");
        stringStringHashMap.forEach((name, value) -> {
            if (dictBiz.getDict(parentDict.getSysCode(), value) == null) {
                dictBiz.addDict(Dict.builder().sysCode(parentDict.getSysCode()).pid(parentDict.getId())
                        .lable(name).val(value).showType(0).grouping(false)
                        .internal(true).shortNo(0).mark("").defaultVal(false)
                        .edit(false)
                        .build());
            }
        });
    }
}
