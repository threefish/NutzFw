/*
 * Copyright (c) 2019- 2019 threefish(https://gitee.com/threefish https://github.com/threefish) All Rights Reserved.
 * 本项目完全开源，商用完全免费。但请勿侵犯作者合法权益，如申请软著等。
 * 最后修改时间：2019/10/07 18:26:07
 * 源 码 地 址：https://gitee.com/threefish/NutzFw
 */

package com.nutzfw.core.plugin.flowable.transaction;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionException;
import org.nutz.trans.Trans;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/19
 */
@Slf4j
public class NutzTransaction implements Transaction {


    private DataSource dataSource;
    private TransactionIsolationLevel level;
    private Connection connection;
    private boolean autoCommmit;

    public NutzTransaction(Connection connection) {
        this.connection = connection;
    }

    public NutzTransaction(DataSource ds, TransactionIsolationLevel level, boolean autoCommmit) {
        this.dataSource = ds;
        this.level = level;
        this.autoCommmit = autoCommmit;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (Trans.isTransactionNone()) {
            if (this.connection == null) {
                openConnection();
            }
        } else {
            this.connection = Trans.get().getConnection(dataSource);
        }
        return this.connection;
    }

    @Override
    public void commit() throws SQLException {
        // Does nothing
    }

    @Override
    public void rollback() throws SQLException {
        // Does nothing
    }

    @Override
    public void close() throws SQLException {
        if (this.connection != null) {
            if (Trans.isTransactionNone()) {
                resetAutoCommit();
                this.connection.close();
            }
        }
    }

    protected void openConnection() throws SQLException {
        this.connection = dataSource.getConnection();
        if (level != null) {
            this.connection.setTransactionIsolation(level.getLevel());
        }
        setDesiredAutoCommit(autoCommmit);
    }

    protected void setDesiredAutoCommit(boolean desiredAutoCommit) {
        try {
            if (connection.getAutoCommit() != desiredAutoCommit) {
                connection.setAutoCommit(desiredAutoCommit);
            }
        } catch (SQLException e) {
            // Only a very poorly implemented driver would fail here,
            // and there's not much we can do about that.
            throw new TransactionException("Error configuring AutoCommit.  "
                    + "Your driver may not support getAutoCommit() or setAutoCommit(). "
                    + "Requested setting: " + desiredAutoCommit + ".  Cause: " + e, e);
        }
    }

    protected void resetAutoCommit() {
        try {
            if (!connection.getAutoCommit()) {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            if (log.isDebugEnabled()) {
                log.debug("Error resetting autocommit to true "
                        + "before closing the connection.  Cause: " + e);
            }
        }
    }

    @Override
    public Integer getTimeout() throws SQLException {
        return null;
    }
}
