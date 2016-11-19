package com.geecommerce.core.app.standalone.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MysqlHelper {
    public static Connection getJdbcConnection(String host, int port, String name, String user, String password) {
	Connection conn = null;
	Properties connectionProps = new Properties();
	connectionProps.put("user", user);
	connectionProps.put("password", password);

	try {
	    conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + name + "?useUnicode=yes&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull", connectionProps);
	} catch (SQLException e) {
	    throw new RuntimeException(e);
	}

	return conn;
    }
}
