#### IDEA 开发环境
* [Lombok plugin](https://github.com/mplushnikov/lombok-intellij-plugin) 开发利器
* [NutzCodeInsight](https://github.com/threefish/NutzCodeInsight)   Nutz 开发工具
* [NutzFwCodeGenerat](https://github.com/threefish/NutzFwCodeGenerat)  NutzFw 代码生成器快速生成service,serviceImpl,后台action,管理页面,编辑查看页面


#### 开发注意事项

0. 系统必须运行在UTF-8环境
1. 前端推荐采用vue.js单页面方式进行开发
2. 将配置文件目录指向自己的开发配置文件
    - 在 idea 中的 VM OPTIONS 中添加  
    ```properties
    -Dfile.encoding=UTF-8
    ```
3. 创建数据库初始化系统数据库数据
    ```properties
    #创建数据库后，将initSystem改为true，初始化系统数据库数据- 初始化后务必改成false
    initSystem=true
    ```
4. 自动生成菜单或按钮、资源 （严格使用该功能，系统安全性极高，访问后台均受Shiro权限控制）
    - 菜单
    ```java
    //默认菜单 具体参考 AutoCreateMenuAuth 类说明
      @RequiresPermissions("sysMenu.index")
      @AutoCreateMenuAuth(name = "菜单管理",icon = "fa-eye")
    ```
   - 按钮、资源 
    ```java
    //type=AutoCreateMenuAuth.RESOURCE表示是按钮或者资源
      @RequiresPermissions("sysMonitor.update")
      @AutoCreateMenuAuth(name = "修改配置", icon = "fa-eye", type = AutoCreateMenuAuth.RESOURCE, parentPermission = "sysMonitor.index")
    ```

5. 数据字典使用
    ```html
     * @param obj 0 sysCode
     *            1 multiple
     *            2 htmlClass
     *            3 vueModelFieldName （为树节点时必须指定）
     *            N ....
     *            如果N为 "END" 那么表示参数结束，之后的全部为HTML attr
     * <p>
     * \${dict("sys_news_level",false,"form-control","leave.leaveType")}
     * \${dict("holiday_types")}
     * <p>
     * 如果还需要设置自定义HTML属性需要设置END字符参数隔开，例子参考
     * <p>
     * \${dict("sys_news_level",false,"form-control","leave.leaveType","END","v-model='test.xxx'")}
     * \${dict("sys_news_level","END","v-model='test.xxx'")}
 
    ```
   - 解析结果
    ```html
    <select v-model="fromData.level" name="news.level" class="input-md form-control form-control ">
      <option value="" key-val="">--请选择--</option>
      <option value="18" key-val="1">弱</option>
      <option value="19" key-val="2">普通</option>
      <option value="20" key-val="3">急</option>
      <option value="21" key-val="4">特急</option>
    </select>
    ```

6. 邮件
    - 发送邮件时只需要向MailBody实体写入信息即可，支持定时发送功能
    
    ```java
        com.nutzfw.modules.sys.entity.MailBody
    ```

7. 短信（暂不开放，此功能需要针对具体的短信服务提供商开发接口）
    - 发送短信时只需要向SmsBody实体写入信息即可，支持定时发送功能
    
    ```java
        com.nutzfw.modules.sys.entity.SmsBody
    ```
8.  全局文件查看器
    ```javascript
        core.showAttachList('文件id')
    ```
9. 防止重复提交
    #如果在提交的操作中需要效验数据，直接可以抛出com.nutzfw.error.PreventDuplicateSubmitError异常，任何异常都将使token立即回滚
   1. 防止重复表单提交- 第一种情况地址相同，GET请求/loging 页面,POST发送数据至/login接口
        ```java
            @GET
            @At("login")
            @Ok("btl:WEB-INF/view/login.html")
            @Token
            public void loginPage() {
            }
            
            @Ok("json:{nullAsEmtry:true}")
            @POST
            @At("login")
            @Token(type = Type.REMOVE)
            public AjaxResult login(@Param("username") String username, @Param("password") String password) {
            }
        ``` 
  2. 防止重复表单提交- 第二种情况地址不相同,将path值设置为一样即可
   
        ```java
            @GET
            @At("login")
            @Ok("btl:WEB-INF/view/login.html")
            @Token(path = "management.login")
            public void loginPage() {
            }
            
            @Ok("json:{nullAsEmtry:true}")
            @POST
            @At("login")
            @Token(type = Type.REMOVE,path = "management.login")
            public AjaxResult login(@Param("username") String username, @Param("password") String password) {
            }
        ```

10. 后台开发规范
    1. 操作数据库必须使用 service 接口进行操作
    2. action中逻辑超过10行请使用biz接口进行操作 超过20行必须使用biz接口
    3. 有些时效性不长的信息可以考虑存入 redis 如登录密码错误多次会冻结账号
    4. 涉及到高并发操作 如秒杀、投票等等 采用RabbitMQ队列控制（暂未涉及，未添加）
    5. request.setAttribute中key不能为obj--obj是内置key

#### 前端参考功能

1. 前端使用vue.js结合插件进行开发
   * [部门机构设置](http://localhost:8080/sysOrganize/department/index)
   * [系统设置](http://localhost:8080/sysOptions/manager)
   * [登录](http://localhost:8080/management/login)

2. 表格功能参考layerui table进行开发
    * [定时任务管理](http://localhost:8080/jobs/)
    
3. 树表格功能参考jquery.treetable进行开发
    * [系统菜单管理](http://localhost:8080/sysMenu/index)

