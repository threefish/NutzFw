/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:32:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.modules.monitor.quartz.job;

import com.nutzfw.core.common.cons.Cons;
import com.nutzfw.core.common.util.DateUtil;
import com.nutzfw.core.common.util.NumbersUtil;
import com.nutzfw.core.plugin.quartz.BaseJob;
import com.nutzfw.modules.monitor.entity.APMAlarm;
import com.nutzfw.modules.monitor.entity.AlarmOption;
import com.nutzfw.modules.monitor.service.APMAlarmService;
import com.nutzfw.modules.monitor.service.AlarmOptionService;
import com.nutzfw.modules.monitor.websocket.ApmDashboardWs;
import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.sys.biz.DictBiz;
import com.nutzfw.modules.sys.entity.MailBody;
import com.nutzfw.modules.sys.service.MailBodyService;
import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.quartz.JobDataMap;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSFileStore;
import oshi.util.FormatUtil;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/23
 * 描述此类：
 */
@IocBean(args = {"refer:$ioc"})
public class ApmJob extends BaseJob {

    @Inject
    APMAlarmService apmAlarmService;

    @Inject
    AlarmOptionService alarmOptionService;

    @Inject
    UserAccountService userAccountService;

    @Inject
    MailBodyService bodyService;
    SystemInfo               systemInfo       = new SystemInfo();
    HardwareAbstractionLayer hardwar          = systemInfo.getHardware();
    SimpleDateFormat         simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    MemoryMXBean             memoryMXBean     = ManagementFactory.getMemoryMXBean();
    @Inject
    ApmDashboardWs ws;
    @Inject
    DictBiz        dictBiz;
    private DecimalFormat     decimalFormat  = new DecimalFormat("#.##");
    /**
     * 时间点
     */
    private List<Date>        timePoints     = new ArrayList();
    /**
     * cpu使用情况
     */
    private List<Double>      cpuUsages      = new ArrayList();
    /**
     * ram使用情况
     */
    private List<Double>      ramUsages      = new ArrayList();
    /**
     * jvm使用情况
     */
    private List<Double>      jvmUsages      = new ArrayList();
    /**
     * swap使用情况
     */
    private List<Double>      swapUsages     = new ArrayList();
    /**
     * 监听项目
     */
    private List<AlarmOption> alarmOptions   = new ArrayList();
    /**
     * 是否开启监听
     */
    private boolean           listenerStatus = true;
    /**
     * 缓存监控多少个最近使用情况
     */
    private int               monitorCount   = 500;
    /**
     * 缓存最近的一个状态
     */
    private HashMap           usaGes         = new HashMap();

    public ApmJob(Ioc ioc) {
        super(ioc);
    }

    @Override
    public void run(JobDataMap data) {
        try {
            log.info("执行系统性能曲线监测与收集任务");
            alarmOptions = alarmOptionService.query();
            if (alarmOptions.size() == 0) {
                //如果还是为0,就不监控了
                if (alarmOptions.size() == 0) {
                    listenerStatus = false;
                }
            }
            if (listenerStatus) {
                GlobalMemory memory = hardwar.getMemory();
                double jvmUsage = 0, ramUsage = 0, swapUsage = 0, cpuUsage = 0, diskPercent = 0.00;
                for (AlarmOption option : alarmOptions) {
                    switch (option.getAlarmType()) {
                        case "JVM":
                            //椎内存使用情况
                            MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
                            jvmUsage = 100d * memoryUsage.getUsed() / memoryUsage.getMax();
                            alarm("JVM", "JVM内存告警", "JVM", jvmUsage, option.getPercent());
                            break;
                        case "RAM":
                            ramUsage = 100d * memory.getAvailable() / memory.getTotal();
                            alarm("RAM", "RAM内存告警", "RAM", ramUsage, option.getPercent());
                            break;
                        case "SWAP":
                            swapUsage = 100d * memory.getSwapUsed() / memory.getSwapTotal();
                            alarm("SWAP", "SWAP内存告警", "SWAP", swapUsage, option.getPercent());
                            break;
                        case "CPU":
                            cpuUsage = hardwar.getProcessor().getSystemCpuLoadBetweenTicks() * 100;
                            alarm("CPU", "CPU告警", "CPU", cpuUsage, option.getPercent());
                            break;
                        case "DISK":
                            OSFileStore[] fsArray = systemInfo.getOperatingSystem().getFileSystem().getFileStores();
                            long total = 0L;
                            long usable = 0L;
                            for (OSFileStore fs : fsArray) {
                                usable += fs.getUsableSpace();
                                total += fs.getTotalSpace();
                            }
                            diskPercent = 100 - (100d * usable / total);
                            alarm("DISK", "磁盘告警", "DISK", diskPercent, option.getPercent());
                            break;
                        default:
                            break;
                    }
                }
                this.add(timePoints, simpleDateFormat.format(new Date()));
                this.add(jvmUsages, jvmUsage);
                this.add(ramUsages, ramUsage);
                this.add(swapUsages, swapUsage);
                this.add(cpuUsages, cpuUsage);
                usaGes.put("CPU", decimalFormat.format(cpuUsage));
                usaGes.put("RAM", decimalFormat.format(ramUsage));
                usaGes.put("JVM", decimalFormat.format(jvmUsage));
                usaGes.put("SWAP", decimalFormat.format(swapUsage));
                usaGes.put("DISK", decimalFormat.format(diskPercent));
            }
        } catch (Exception e) {
            log.error(e);
        } finally {
            ws.sendJson(new NutMap().setv("alarmData", usaGes).setv("lineData", getMoreTempAll()));
        }
    }

    /**
     * 取得临时缓存的最近多个系统状态信息
     *
     * @return
     */
    public HashMap getMoreTempAll() {
        HashMap map = new HashMap(5);
        map.put("timePoints", timePoints);
        map.put("cpuUsages", cpuUsages);
        map.put("ramUsages", ramUsages);
        map.put("jvmUsages", jvmUsages);
        map.put("swapUsages", swapUsages);
        return map;
    }

    /**
     * 取得临时缓存的最近1个系统状态信息
     *
     * @return
     */
    public HashMap getOneTempAll() {
        return usaGes;
    }

    /**
     * 更新主机监控项目
     *
     * @return
     */
    public void setAlarmOptions(List<AlarmOption> alarmOptions) {
        this.alarmOptions = alarmOptions;
    }

    /**
     * 添加数据
     *
     * @param list 列表
     * @param obj  待添加数据
     */
    private void add(List list, Object obj) {
        if (obj instanceof Number) {
            list.add(NumbersUtil.keepPrecision((Number) obj, 2));
        } else {
            list.add(obj);
        }
        if (list.size() > monitorCount) {
            list.remove(0);
        }
    }

    /**
     * 插入告警表-通知后半小时内不再告警
     *
     * @param type
     * @param title
     * @param device
     * @param usage
     * @param alarmPoint
     */
    private void alarm(String type, String title, String device, double usage, double alarmPoint) {
        if (usage > alarmPoint) {
            APMAlarm alarm = new APMAlarm();
            alarm.setType(type);
            alarm.setIp("127.0.0.1");
            alarm.setMsg(MessageFormat.format("{0}:当前 {1} 使用率 {2},高于预警值 {3} {4}", title, device, decimalFormat.format(usage), decimalFormat.format(alarmPoint), DateUtil.date2date(new Date(), DateUtil.YYYY_MM_DD_HH_MM_SS_ZH)));
            alarm.setTitle(title);
            alarm.setDevice(device);
            alarm.setUsage(usage);
            alarm.setAlarm(alarmPoint);
            try {
                Calendar startDate = Calendar.getInstance();
                startDate.add(Calendar.MINUTE, -30);
                int count = apmAlarmService.count(Cnd.where("alarmTime", ">=", startDate.getTime()).and("type", "=", type));
                if (count == 0) {
                    //记录通知后半小时内不再告警
                    apmAlarmService.insert(alarm);
                    sendMsg(type, title, alarm.getMsg());
                }
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    private void sendMsg(String type, String title, String msg) {
        AlarmOption option = alarmOptionService.fetch(type);
        String[] listeners = Strings.splitIgnoreBlank(Strings.sNull(option.getListeners()));
        Cnd cnd = Cnd.NEW();
        SqlExpressionGroup group = new SqlExpressionGroup();
        for (String listener : listeners) {
            group.or(Cons.USER_ACCOUNT_TABLE_NAME.concat(".id"), "=", listener);
        }
        cnd.and(group);
        List<UserAccount> accountList = userAccountService.queryByJoin(cnd);
        Set<String> mailSets = new HashSet<>();
        Set<String> phoneSets = new HashSet<>();
        for (UserAccount account : accountList) {
            if (Strings.isNotBlank(account.getMail())) {
                mailSets.add(account.getMail());
            }
            if (Strings.isNotBlank(account.getPhone())) {
                phoneSets.add(account.getPhone());
            }
        }
        if (option.isEmail()) {
            sendMail(title, msg, Strings.join(",", mailSets));
        }
        if (option.isSms()) {
            sendSms(title, msg, Strings.join(",", phoneSets));
        }
    }

    /**
     * 发送报警短信
     *
     * @param msg
     * @param phones
     */
    private void sendSms(String title, String msg, String phones) {

    }

    /**
     * 发送报警邮件
     *
     * @param title
     * @param msg
     * @param mails
     */
    private void sendMail(String title, String msg, String mails) {
        //写入邮件
        MailBody mailBody = new MailBody();
        mailBody.setBizType(dictBiz.getCacheDict("sys_mail_type", "notice").getId());
        mailBody.setTo(mails);
        mailBody.setHtmlMsg(MessageFormat.format("{0} 来自：【{1}】 ", msg, Cons.optionsCach.getProductLongName()));
        mailBody.setSubject(MessageFormat.format("服务器{0}【{1}】 ", title, Cons.optionsCach.getProductLongName()));
        bodyService.insert(mailBody);
    }

    public List<NutMap> getFileSystemInfo() {
        List<NutMap> list = new ArrayList<>();
        OSFileStore[] fileStores = systemInfo.getOperatingSystem().getFileSystem().getFileStores();
        for (OSFileStore fs : fileStores) {
            long usable = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            NutMap d = new NutMap();
            d.put("name", fs.getName());
            d.put("fsType", fs.getType());
            d.put("description", fs.getDescription());
            d.put("mount", fs.getMount());
            d.put("usable", FormatUtil.formatBytes(usable));
            d.put("total", FormatUtil.formatBytes(total));
            if (usable != 0 || total != 0) {
                d.put("percent", NumbersUtil.keepPrecision(100 - (100d * usable / total), 2));
            } else {
                d.put("percent", 0);
            }
            list.add(d);
        }
        return list;
    }

    public String getCpuInfo() {
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        return MessageFormat.format("{0}  ({1}物理核心 {2}逻辑核心)", processor.toString(), processor.getPhysicalProcessorCount(), processor.getLogicalProcessorCount());
    }
}
