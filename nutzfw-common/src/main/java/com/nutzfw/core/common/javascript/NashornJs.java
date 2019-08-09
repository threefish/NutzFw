package com.nutzfw.core.common.javascript;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author 306955302@qq.com
 * 创建人：黄川
 * 创建时间: 2018/1/8  15:44
 * 描述此类：
 */
public class NashornJs {

    protected static final Log log = Logs.get();

    private static ClassLoader ccl = Thread.currentThread().getContextClassLoader();

    private static NashornScriptEngineFactory factory = new NashornScriptEngineFactory();

    private static NashornScriptEngine engine;

    public NashornJs() {
        engine = (NashornScriptEngine) factory.getScriptEngine(new String[]{"-scripting",
                "-nse",
        }, ccl, (s -> false));
    }

    /**
     * 解析获取sql
     *
     * @param jsSql
     * @param args
     * @return
     */
    public String evalJsSql(String jsSql, Map<String, Object> args) {
        StringBuffer jsCode = new StringBuffer();
        jsCode.append("function getSqlText(ctx){\n");
        jsCode.append(jsSql + "\n");
        jsCode.append("}");
        String sql = "";
        try {
            engine.compile(jsCode.toString());
            engine.eval(jsCode.toString());
            Object result = engine.invokeFunction("getSqlText", args);
            sql = String.valueOf(result);
            log.debug(sql);
        } catch (Exception e) {
            log.error(e);
        }
        return sql;
    }

}