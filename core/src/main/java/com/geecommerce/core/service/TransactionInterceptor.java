package com.geecommerce.core.service;

import java.sql.Connection;
import java.sql.SQLException;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.geecommerce.core.App;
import com.geecommerce.core.db.Connections;

public class TransactionInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Connection conn = App.get().inject(Connections.class).getSqlConnection();

        if (conn == null)
            throw new RuntimeException("Unable to start transaction because no jdbc connection has been initialized.");

        try {
            // System.out.println("STARTING TRANSACTION");
            conn.setAutoCommit(false);

            Object o = invocation.proceed();

            // System.out.println("COMMITTING TRANSACTION");
            conn.commit();
            conn.setAutoCommit(true);

            return o;
        } catch (Throwable t) {
            // System.out.println("ROLLING BACK TRANSACTION");

            try {
                conn.rollback();
            } catch (SQLException e) {
            }

            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
            }

            throw t;
        }
    }
}
