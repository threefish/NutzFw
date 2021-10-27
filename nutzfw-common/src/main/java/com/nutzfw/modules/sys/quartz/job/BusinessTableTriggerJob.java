package com.nutzfw.modules.sys.quartz.job;

import com.nutzfw.core.common.threadpool.BusinessCommonTaskExecutorContextHolder;
import com.nutzfw.core.common.util.ElUtil;
import com.nutzfw.core.plugin.quartz.BaseJob;
import com.nutzfw.modules.sys.dto.BusinessTableTrigger;
import com.nutzfw.modules.sys.entity.DataTable;
import com.nutzfw.modules.sys.entity.TableFields;
import com.nutzfw.modules.sys.service.DataTableService;
import com.nutzfw.modules.tabledata.biz.DataMaintainBiz;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 黄川 huchuc@vip.qq.com
 * date: 2021/10/27
 */
@IocBean(args = {"refer:$ioc"})
@DisallowConcurrentExecution
public class BusinessTableTriggerJob extends BaseJob {

    @Inject
    DataTableService dataTableService;
    @Inject
    DataMaintainBiz dataMaintainBiz;

    public BusinessTableTriggerJob(Ioc ioc) {
        super(ioc);
    }

    @Override
    public void run(JobDataMap data) throws Exception {
        List<DataTable> dataTableList = dataTableService.query(Cnd.where("triggersJsonText", "!=", ""));
        dataTableList.forEach(dataTable -> {
            dataTableService.fetchLinks(dataTable, null);
            List<BusinessTableTrigger> triggers = dataTable.getTriggers();
            triggers.forEach(trigger -> BusinessCommonTaskExecutorContextHolder.execute(() -> execute(dataTable, trigger)));
        });
    }

    public void execute(DataTable dataTable, BusinessTableTrigger trigger) {

        String effectiveConditions = trigger.getEffectiveConditions();
        String triggerTiming = trigger.getTriggerTiming();
        String messageTemplate = trigger.getMessageTemplate();
        Map<String, Object> data = dataTable.getFields().stream().collect(Collectors.toMap(TableFields::getName, TableFields::getFieldName));
        String sql1 = ElUtil.render(effectiveConditions, data);
        String sql2 = ElUtil.render(triggerTiming, data);

        List<String> showFields = dataMaintainBiz.getQueryFields(dataTable);
        Sql sql = Sqls.create("SELECT $showFields from $tableName where 1=1 $sql1 $sql2");
        sql.setVar("tableName", dataTable.getTableName());
        sql.setVar("showFields", Strings.join(",", showFields));
        sql.setVar("sql1", "");
        sql.setVar("sql2", "");
        if (Strings.isNotBlank(sql1)) {
            sql.setVar("sql1", " and " + sql1);
        }
        if (Strings.isNotBlank(sql2)) {
            sql.setVar("sql2", " and " + sql2);
        }
        sql.setCallback(Sqls.callback.records());
        dataTableService.dao().execute(sql);
        List<Record> list = sql.getList(Record.class);
        // 批量转换数据
        list.stream().parallel().peek(record -> dataMaintainBiz.coverVueJsFromData(record, dataTable.getFields()));
        list.forEach(record -> {
            Map<String, Object> context = new HashMap<>(dataTable.getFields().size());
            dataTable.getFields().stream().forEach(f -> context.put(f.getName(),record.get(f.getFieldName())));
            String content = ElUtil.render(messageTemplate, context);
            System.out.println(content);
            //TODO 发送消息
        });
        System.out.println("");
    }
}
