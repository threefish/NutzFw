/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:27:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.common.util;

import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: 黄川
 * 创建时间: 2017/11/2  18:35
 * 描述此类：
 */
public class MysqlBackUpUtil {

    public static final String MYSQL_DUMP_EXE = "c:" + File.separator + "mysqldump" + File.separator + "mysqldump.exe";

    private static Log log = Logs.get();

    /**
     * 测试是否正常连接数据库
     *
     * @param dbName
     * @param dbip
     * @param port
     * @param username
     * @param password
     * @return
     */
    public static String testConn(String dbName, String dbip, String port, String username, String password) {
        return testConn(dbName, dbip.concat(":").concat(port), username, password);
    }

    /**
     * 测试是否正常连接数据库
     *
     * @param dbName
     * @param dbipport
     * @param username
     * @param password
     * @return
     */
    public static String testConn(String dbName, String dbipport, String username, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String jdbc = "jdbc:mysql://" + dbipport + "/" + dbName + "?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull";
            Connection connection = DriverManager.getConnection(jdbc, username, password);
            if (connection != null) {
                connection.close();
                return null;
            } else {
                return "空连接";
            }
        } catch (Exception e) {
            log.error(e);
            return e.getMessage();
        }
    }


    /**
     * 备份MySql数据库
     *
     * @param dbs    要备份的数据库列表
     * @param dbip
     * @param dbport
     * @param user
     * @param pass
     * @param path   备份至path目录
     * @return
     */
    public static boolean backMysql(String[] dbs, String dbip, String dbport, String user, String pass, String path) {
        boolean isWin = Lang.isWin();
        Runtime runtime = Runtime.getRuntime();
        try {
            File file = new File(path);
            file.mkdirs();
            String errmsg = testConn(dbs[0], dbip + ":" + dbport, user, pass);
            if (errmsg == null) {
                for (String db : dbs) {
                    StringBuilder sb = new StringBuilder();
                    if (isWin) {
                        sb.append("cmd /c " + MYSQL_DUMP_EXE);
                    } else {
                        sb.append(Paths.get(Mvcs.getServletContext().getRealPath(""), "WEB-INF", "lib", "mysqldump").toString());
                    }
                    sb.append(" -h" + dbip);
                    sb.append(" -P" + dbport);
                    sb.append(" -u" + user);
                    sb.append(" -p" + pass);
                    sb.append(" " + db);
                    sb.append(" >" + path + File.separator + db + ".sql");

                    if (isWin) {
                        runtime.exec(sb.toString());
                    } else {
                        String[] mysqldump = {"/bin/sh", "-c", sb.toString()};
                        runtime.exec(mysqldump);
                    }
                }
            } else {
                log.error("数据库连接失败!" + errmsg);
                return false;
            }
        } catch (IOException e) {
            log.error("数据库备份失败", e);
            return false;
        }
        return true;
    }

    /**
     * 检查备份数据库必须的文件
     */
    public static void checkMysqlDumpExe() {
        try {
            if (Lang.isWin()) {
                Path path = Paths.get(MYSQL_DUMP_EXE);
                File file = path.toFile();
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    Files.copy(Paths.get(Mvcs.getServletContext().getRealPath(""), "WEB-INF", "lib", "mysqldump.exe"), path);
                }
            } else {
                String[] chmod = {"/bin/sh", "-c", "chmod +x " + Paths.get(Mvcs.getServletContext().getRealPath(""), "WEB-INF", "lib", "mysqldump").toString()};
                Runtime.getRuntime().exec(chmod);
            }
        } catch (Exception e) {
            log.error(e);
        }
    }
}
