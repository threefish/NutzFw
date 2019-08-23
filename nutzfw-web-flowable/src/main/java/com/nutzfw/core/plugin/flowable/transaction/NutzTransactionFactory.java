package com.nutzfw.core.plugin.flowable.transaction;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/19
 */
public class NutzTransactionFactory implements TransactionFactory {


    @Override
    public void setProperties(Properties props) {

    }

    @Override
    public Transaction newTransaction(Connection conn) {
        return new NutzTransaction(conn);
    }

    @Override
    public Transaction newTransaction(DataSource ds, TransactionIsolationLevel level, boolean autoCommit) {
        return new NutzTransaction(ds, level, autoCommit);
    }
}
