/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:29:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

import com.nutzfw.core.common.javascript.V8Js;

import java.util.HashMap;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/12/18
 */
public class J2v8Test {

    public static void main(String[] args) {
        HashMap<String, Object> vals = new HashMap<>();
        vals.put("name", 999);
        String sql = "var a=1,b=2;  a=ctx.name+1000; return a;";
        System.out.println(new V8Js().evalJsSql(sql, vals));
    }

    public void print(String s) {
        System.out.println(s);
    }
}
