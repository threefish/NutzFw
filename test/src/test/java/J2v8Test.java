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
