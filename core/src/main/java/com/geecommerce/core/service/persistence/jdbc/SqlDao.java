package com.geecommerce.core.service.persistence.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.geecommerce.core.service.api.Dao;

public interface SqlDao extends Dao {
    public void duplicateTable(String from, String to) throws SQLException;

    public ResultSet executeQuery(String query) throws SQLException;

    public ResultSet executePreparedQuery(String query, Object... args) throws SQLException;

    public int executeUpdate(String query) throws SQLException;
}
