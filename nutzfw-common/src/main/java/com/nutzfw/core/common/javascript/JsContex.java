package com.nutzfw.core.common.javascript;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author : 黄川
 */
@Slf4j
public class JsContex {

    private static ClassLoader ccl = Thread.currentThread().getContextClassLoader();
    private static NashornScriptEngineFactory factory = new NashornScriptEngineFactory();

    private static transient ThreadLocal<NashornScriptEngine> jsEngineThreadLocal = new ThreadLocal<>();

    /**
     * 创建引擎
     *
     * @param jsEngine
     */
    public static synchronized void set(NashornScriptEngine jsEngine) {
        jsEngineThreadLocal.set(jsEngine);
    }

    /**
     * 取得引擎
     *
     * @return
     */
    public static synchronized NashornScriptEngine get() {
        NashornScriptEngine jsEngine = jsEngineThreadLocal.get();
        //双重检查
        if (jsEngine == null) {
            synchronized (JsContex.class) {
                if (jsEngine == null) {
                    NashornScriptEngine engine = (NashornScriptEngine) factory.getScriptEngine(new String[]{"-scripting",
                            "-nse",
                    }, ccl, (s) -> false);
                    jsEngine = engine;
                    set(engine);
                }
            }
        }
        return jsEngine;
    }

    /**
     * 释放
     */
    public static void destory() {
        jsEngineThreadLocal.remove();
    }


    /**
     * @param javaScript 例子 value = param.name +parm.num ;
     * @param context
     * @return
     */
    public static String evalJavaScript(String javaScript, Map<String,Object> context) {
        StringBuffer jsCode = new StringBuffer();
        jsCode.append("function getText(ctx){ var value=''; ");
        jsCode.append(javaScript);
        jsCode.append(" return value; }");
        String value = "";
        try {
            JsContex.get().compile(jsCode.toString());
            JsContex.get().eval(jsCode.toString());
            Object result = JsContex.get().invokeFunction("getText", context);
            value = String.valueOf(result);
        } catch (Exception e) {
            log.error("解析动态JS错误", e);
        }
        return value;
    }


}