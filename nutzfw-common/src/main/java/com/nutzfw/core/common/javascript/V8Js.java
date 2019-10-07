/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.javascript;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2018/1/8  15:44
 * 描述此类：
 */
public class V8Js {

    static final Log log = Logs.get();

    public V8Js() {

    }

    public void print(String s) {
        System.out.println(s);
    }

    /**
     * 解析获取sql
     *
     * @param jsSql
     * @param args  只接收基本类型
     * @return
     */
    public synchronized String evalJsSql(String jsSql, HashMap<String, Object> args) {
        synchronized (this) {
            StringBuffer jsCode = new StringBuffer();
            jsCode.append("function getSqlText(ctx){\n");
            jsCode.append(jsSql + "\n");
            jsCode.append("}");
            String sql = "";
            V8 runtime = V8.createV8Runtime();
            V8Object ctx = renderObject(runtime, args);
            V8Array parameters = new V8Array(runtime).push(ctx);
            try {
                runtime.registerJavaMethod(this, "print", "print", new Class<?>[]{String.class});
                runtime.executeVoidScript(jsCode.toString());
                Object val = runtime.executeFunction("getSqlText", parameters);
                sql = String.valueOf(val);
                log.debug(sql);
            } catch (Exception e) {
                log.error(e);
            } finally {
                if (ctx != null) {
                    ctx.release();
                }
                if (parameters != null) {
                    parameters.release();
                }
                runtime.release(false);
            }
            return sql;
        }
    }

    /**
     * 设置参数
     *
     * @param runtime
     * @param args
     * @return
     */
    private V8Object renderObject(V8 runtime, HashMap<String, Object> args) {
        V8Object parameter = new V8Object(runtime);
        args.forEach((key, val) -> {
            if (val instanceof String) {
                parameter.add(key, (String) val);
            } else if (val instanceof Double) {
                parameter.add(key, (Double) val);
            } else if (val instanceof Integer) {
                parameter.add(key, (Integer) val);
            } else if (val instanceof Boolean) {
                parameter.add(key, (Boolean) val);
            } else if (val instanceof HashMap) {
                parameter.add(key, renderObject(runtime, (HashMap<String, Object>) val));
            } else {
                parameter.add(key, renderObject(runtime, Lang.obj2map(val, HashMap.class)));
            }
        });
        return parameter;
    }

}
