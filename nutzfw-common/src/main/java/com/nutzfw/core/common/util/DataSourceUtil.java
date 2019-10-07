/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created with IntelliJ IDEA.
 *
 * @author huchuc@vip.qq.com
 * 创建人：黄川
 * 创建时间: 2018/1/9  11:39
 * 描述此类：
 */
public class DataSourceUtil {

    static final Log log = Logs.get();

    /**
     * 测试连接
     *
     * @param user
     * @param pass
     * @param jdbcUrl
     * @return
     */
    public static synchronized String testConn(String user, String pass, String jdbcUrl) {
        try {
            String driver = JdbcUtils.getDriverClassName(jdbcUrl);
            Class.forName(driver).newInstance();
            Connection connection = DriverManager.getConnection(jdbcUrl, user, pass);
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            log.warn(e);
            return e.getMessage();
        }
        return null;
    }


    /**
     * 测试连接
     *
     * @param user
     * @param pass
     * @param jdbcUrl
     * @return
     */
    public static synchronized DruidDataSource getDs(String user, String pass, String jdbcUrl) {
        try {
            DruidDataSource ds = new DruidDataSource();
            ds.setUrl(jdbcUrl);
            ds.setPassword(pass);
            ds.setUsername(user);
            ds.setInitialSize(1);
            ds.init();
            return ds;
        } catch (Exception e) {
            log.warn(e);
        }
        return null;
    }
}
