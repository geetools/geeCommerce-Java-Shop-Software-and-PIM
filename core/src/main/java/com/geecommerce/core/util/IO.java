package com.geecommerce.core.util;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class IO {
    public static final void closeQuietly(Writer writer) {
        try {
            if (writer != null)
                writer.close();
        } catch (Throwable t) {

        }
    }

    public static final void closeQuietly(Reader reader) {
        try {
            if (reader != null)
                reader.close();
        } catch (Throwable t) {

        }
    }

    public static final void closeQuietly(InputStream is) {
        try {
            if (is != null)
                is.close();
        } catch (Throwable t) {

        }
    }

    public static final void closeQuietly(OutputStream os) {
        try {
            if (os != null)
                os.close();
        } catch (Throwable t) {

        }
    }

    public static final void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (Throwable t) {

        }
    }

    public static final void closeQuietly(Connection conn) {
        try {
            if (conn != null)
                conn.close();
        } catch (Throwable t) {

        }
    }

    public static final void closeQuietly(ResultSet res) {
        try {
            if (res != null)
                res.close();
        } catch (Throwable t) {

        }
    }

    public static final void closeQuietly(Statement stmt) {
        try {
            if (stmt != null)
                stmt.close();
        } catch (Throwable t) {

        }
    }
}
