package com.nutzfw.core.plugin.quartz;

import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.ioc.meta.IocObject;
import org.nutz.json.Json;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.StringReader;

public class QuartzIocLoader extends JsonLoader {
    private static final Log log = Logs.get();
    protected JsonLoader proxy;

    public QuartzIocLoader(String... args) {
        super(new String[0]);
        String confName = args.length > 0 ? args[0] : "conf";
        StringBuilder sb = new StringBuilder("{");
        sb.append("scheduler:{type : \'org.quartz.Scheduler\',\n");
        sb.append("factory:\'org.quartz.impl.StdSchedulerFactory#getDefaultScheduler\',events:{\n");
        sb.append("create:\'start\',depose:\'shutdown\',},\n");
        sb.append("fields:{jobFactory:{refer:\'jobFactory\'}}},");
        sb.append("jobFactory:{type:\'com.nutzfw.core.plugin.quartz.NutQuartzJobFactory\', args:[{refer:\'$ioc\'}]},");
        sb.append("nutQuartzCronJobFactory:{type:\'com.nutzfw.core.plugin.quartz.NutQuartzCronJobFactory\',");
        sb.append("events:{create:\'init\'}, fields:{\'scheduler\':{refer:\'scheduler\'},ioc:{refer:\'$ioc\'},conf:{refer:\'" + confName + "\'}}}");
        sb.append("}");
        String json = Json.toJson(Json.fromJson(sb.toString()));
        log.debug("Quartz Ioc Define as:\n" + json);
        this.proxy = new JsonLoader(new StringReader(json));
    }

    @Override
    public String[] getName() {
        return this.proxy.getName();
    }

    @Override
    public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
        return this.proxy.load(loading, name);
    }

    @Override
    public boolean has(String name) {
        return this.proxy.has(name);
    }
}
