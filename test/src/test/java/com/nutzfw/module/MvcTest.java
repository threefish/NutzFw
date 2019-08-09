package com.nutzfw.module;

import com.nutzfw.modules.organize.entity.UserAccount;
import com.nutzfw.modules.organize.service.UserAccountService;
import com.nutzfw.modules.sys.biz.EmailBiz;
import com.nutzfw.modules.sys.entity.MailBody;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.Arrays;
import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2018/5/14
 * 描述此类：
 */
@RunWith(TestRunner.class)
@IocBean
public class MvcTest {

    @Inject
    Dao dao;

    @Inject
    EmailBiz emailBiz;
    @Inject
    UserAccountService userAccountService;
    String html = "<div id=\"content\" style=\"padding: 15px\"><h1 id=\"-\"><strong>后台管理系统</strong></h1>\n" +
            "<p>&gt; 前端推荐采用vue.js单页面方式进行开发</p>\n" +
            "<h2 id=\"-\">运行环境</h2>\n" +
            "<ul>\n" +
            "<li>JDK 8 162 + </li>\n" +
            "<li>Maven 3.3.9 +</li>\n" +
            "<li>Redis 3.2.100 +</li>\n" +
            "<li>MariaDB 10.2 + or MySQL 5.7 +</li>\n" +
            "</ul>\n" +
            "<h3 id=\"-\">目前功能</h3>\n" +
            "<ul>\n" +
            "<li><input checked=\"\" disabled=\"\" type=\"checkbox\"> 1 sigar服务器状态监控</li>\n" +
            "<li><input checked=\"\" disabled=\"\" type=\"checkbox\"> 2 druid监控</li>\n" +
            "<li><input checked=\"\" disabled=\"\" type=\"checkbox\"> 3 redis</li>\n" +
            "<li><input disabled=\"\" type=\"checkbox\"> 4 RabbitMQ（未完成）</li>\n" +
            "<li><input disabled=\"\" type=\"checkbox\"> 5 系统信息配置-登录验证码-系统名称等等</li>\n" +
            "<li><input disabled=\"\" type=\"checkbox\"> 6 错误日志记录（未完成）</li>\n" +
            "<li><input disabled=\"\" type=\"checkbox\"> 7 组织架构管理</li>\n" +
            "<li><input checked=\"\" disabled=\"\" type=\"checkbox\"> Nutz国际化</li>\n" +
            "</ul>\n" +
            "<h2 id=\"-\">后端技术选型</h2>\n" +
            "<ul>\n" +
            "<li>核心框架：<a href=\"https://nutz.cn/\">Nutz、Nutz MVC、Nutz Dao</a></li>\n" +
            "<li>安全框架：Shiro</li>\n" +
            "<li>任务调度：Quartz</li>\n" +
            "<li>数据库连接池：Druid </li>\n" +
            "<li>支持数据库：MySql <a href=\"https://downloads.mariadb.org/\">MariaDB</a> （理论上支持Oracle、SqlServer未测试）</li>\n" +
            "<li>前后端API：swagger</li>\n" +
            "<li>后端效验插件：nutz-plugins-validation</li>\n" +
            "</ul>\n" +
            "<h2 id=\"-\">前端技术选型</h2>\n" +
            "<ul>\n" +
            "<li>前端框架：<a href=\"https://almsaeedstudio.com/themes/AdminLTE/index2.html\">AdminLTE</a></li>\n" +
            "<li>双向绑定 MVVM：<a href=\"https://cn.vuejs.org/v2/guide/\">vue.js</a></li>\n" +
            "<li>前端UI组件：<a href=\"http://www.bootcss.com/\">Bootstrap</a> &amp; <a href=\"http://www.layui.com/\">layui</a></li>\n" +
            "<li>前端树：<a href=\"http://www.treejs.cn/\">ztree</a></li>\n" +
            "<li>树表格：<a href=\"https://github.com/ludo/jquery-treetable\">JQuery.treetable.js</a></li>\n" +
            "<li>表格：layui.table</li>\n" +
            "<li>弹窗：<a href=\"http://layer.layui.com/\">layer</a></li>\n" +
            "<li>日期插件：layui.date</li>\n" +
            "<li>前端效验插件：<a href=\"https://github.com/WLDragon/SMValidator\">SMValidator</a></li>\n" +
            "<li>文件上传插件封装：HUCuploadFile.js || webuploader（单独用）</li>\n" +
            "<li>前端图表：<a href=\"http://echarts.baidu.com/\">echarts</a></li>\n" +
            "<li>前端图标：Font Awesome 4.7.0</li>\n" +
            "<li>单选框复选框美化：<a href=\"http://www.bootcss.com/p/icheck/\">iCheck</a> || magic-check</li>\n" +
            "</ul>\n" +
            "<h2 id=\"-\">自动生成菜单</h2>\n" +
            "<ul>\n" +
            "<li>开关sys.properties中 </li>\n" +
            "<li>此功能开启会自动删除表重新建表，建议开发时使用自己本机数据库使用</li>\n" +
            "</ul>\n" +
            "<pre><code class=\"language-properties\">#初始化系统-\n" +
            "initSystem=true</code></pre>\n" +
            "<ul>\n" +
            "<li>自动生成菜单 </li>\n" +
            "</ul>\n" +
            "<pre><code class=\"language-java\">//默认菜单\n" +
            "  @RequiresPermissions(\"sysMenu.index\")\n" +
            "  @AutoCreateMenuAuth(name = \"菜单管理\",icon = \"fa-eye\")</code></pre>\n" +
            "<ul>\n" +
            "<li>自动生成按钮或者资源</li>\n" +
            "</ul>\n" +
            "<pre><code class=\"language-java\">//type=1表示是按钮或者资源\n" +
            "  @RequiresPermissions(\"sysMonitor.update\")\n" +
            "  @AutoCreateMenuAuth(name = \"修改配置\", icon = \"fa-eye\", type = AutoCreateMenuAuth.RESOURCE, parentPermission = \"sysMonitor.index\")</code></pre>\n" +
            "<h2 id=\"-\">参考功能</h2>\n" +
            "<ul>\n" +
            "<li><p>前端使用vue.js结合插件进行开发</p>\n" +
            "<ul>\n" +
            "<li><a href=\"http://localhost:8080/sysOrganize/department/index\">部门机构设置</a></li>\n" +
            "<li><a href=\"http://localhost:8080/sysOptions/manager\">系统设置</a></li>\n" +
            "<li><a href=\"http://localhost:8080/manage/user/HomePage\">个人资料修改</a></li>\n" +
            "</ul>\n" +
            "</li>\n" +
            "<li><p>表格功能参考layerui table进行开发</p>\n" +
            "<ul>\n" +
            "<li><a href=\"http://localhost:localhost:8080/jobs/\">定时任务管理</a></li>\n" +
            "</ul>\n" +
            "</li>\n" +
            "<li><p>树表格功能参考jquery.treetable进行开发</p>\n" +
            "<ul>\n" +
            "<li><a href=\"http://localhost:8080/sysMenu/index\">系统菜单管理</a></li>\n" +
            "</ul>\n" +
            "</li>\n" +
            "</ul>\n" +
            "</div>";



    /**
     * 取得用户信息
     */
    @Test
    public void queryByJoinUser() {
        UserAccount userAccount = userAccountService.fetchByJoin(null, Cnd.where("userName", "=", "admin"));
    }

    /**
     * 使用定时任务自动发送邮件
     */
    @Test
    public void autoSendMail() throws Throwable {
        MailBody body = new MailBody();
        body.setMaxSendNum(2);
        body.setHtmlMsg(html);
        body.setSubject("你好，你的信息");
        body.setTo("123@vip.qq.com");
        body.setBcc("xxx@vip.qq.com,xxx123@qq.com");
        dao.insert(body);
    }

    /**
     * 发送邮件
     */
    @Test
    public void sendMail() throws Throwable {
        List<String> to = Arrays.asList("xx@vip.qq.com");
        List<String> cc = Arrays.asList("xxx@vip.qq.com");
        List<String> bcc = Arrays.asList("xxx@vip.qq.com");
        emailBiz.send(to, cc, bcc, "抄送密送主送", html);
    }


}
