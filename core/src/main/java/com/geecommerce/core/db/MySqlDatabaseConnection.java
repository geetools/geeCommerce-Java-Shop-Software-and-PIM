package com.geecommerce.core.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.geecommerce.core.Str;
import com.geecommerce.core.db.annotation.Persistence;
import com.geecommerce.core.db.api.ConnectionProvider;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

@Persistence("mysql")
public class MySqlDatabaseConnection implements ConnectionProvider {
    protected ComboPooledDataSource cpds;
    protected String configurationName;
    protected Map<String, String> properties;
    private static ThreadLocal<java.sql.Connection> MYSQL_CONNECTION_THREAD_LOCAL = new ThreadLocal<java.sql.Connection>();

    @Override
    public String group() {
        return "sql";
    }

    @Override
    public String name() {
        return configurationName;
    }

    @Override
    public void init(String configurationName, Map<String, String> properties) {
        this.configurationName = configurationName;
        this.properties = new HashMap<>(properties);

        String connectionKey = getConnectionKey();

        try {
            String driver = property("driver");

            String userName = property("user");
            String password = property("pass");

            cpds = new ComboPooledDataSource();
            cpds.setDriverClass(driver);
            cpds.setJdbcUrl(getJdbcUrl());
            cpds.setUser(userName);
            cpds.setPassword(password);

            int minPoolSize = Integer.parseInt(property("pool_min_size"));
            int aquireIncrement = Integer.parseInt(property("pool_increment_by"));
            int maxPoolSize = Integer.parseInt(property("pool_max_size"));

            cpds.setMinPoolSize(minPoolSize);
            cpds.setAcquireIncrement(aquireIncrement);
            cpds.setMaxPoolSize(maxPoolSize);
            cpds.setIdleConnectionTestPeriod(300);
            cpds.setPreferredTestQuery("SELECT 1");
            cpds.setTestConnectionOnCheckin(false);
            cpds.setTestConnectionOnCheckout(false);
        } catch (Throwable t) {
            throw new RuntimeException("Unable to establich SQL connection for JDBC-key: " + connectionKey, t);
        }
    }

    @Override
    public Object provide() {
        java.sql.Connection conn = null;

        try {
            conn = MYSQL_CONNECTION_THREAD_LOCAL.get();

            if (conn == null) {
                conn = cpds.getConnection();
                MYSQL_CONNECTION_THREAD_LOCAL.set(conn);
            }
        } catch (Throwable t) {
            throw new RuntimeException("Unable to establich SQL connection: " + getConnectionKey(), t);
        }

        return conn;
    }

    @Override
    public void close() {
        try {
            java.sql.Connection conn = MYSQL_CONNECTION_THREAD_LOCAL.get();

            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
        } finally {
            MYSQL_CONNECTION_THREAD_LOCAL.remove();
        }
    }

    @Override
    public void destroy() {
        try {
            DataSources.destroy(cpds);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected String getConnectionKey() {
        String userName = property("user");
        StringBuilder connKey = new StringBuilder(getJdbcUrl()).append("&user=").append(userName);
        return connKey.toString();
    }

    protected String getJdbcUrl() {
        String url = property("url");

        if (!Str.isEmpty(url)) {
            return url;
        } else {
            String protocol = property("protocol");
            String host = property("host");
            int port = Integer.parseInt(property("port"));
            String dbName = property("name");

            StringBuilder connStr = new StringBuilder(protocol);
            connStr.append(host).append(":").append(port);
            connStr.append("/").append(dbName).append("?autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8");

            return connStr.toString();
        }
    }

    protected String property(String key) {
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (entry.getKey().endsWith("." + key)) {
                return entry.getValue();
            }
        }

        return null;
    }
}
