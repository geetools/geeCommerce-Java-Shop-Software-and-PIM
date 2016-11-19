package com.geecommerce.core.type;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Char;
import com.geecommerce.core.cache.Cache;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.db.Connections;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.type.decorators.IncrementIdDecorator;
import com.geecommerce.core.util.DateTimes;
import com.geecommerce.core.util.IO;

/**
 * Generates a new increment number. The standard format is
 * $prefix$sequenceNumber. An id can be build up of:
 * 
 * 1) Prefix. 2) Date-Format (e.g. "yy" for just the year). 3) Sequence-Number.
 * 
 * Without further configuration, this would result in something like: 1141. If
 * we were to configure a hyphen as the id separator character, it would
 * look like this: 1-14-1.
 * 
 * Configuration-Options:
 * 
 * 1) system/increment_number/%s/prefix: Determines what the id should start
 * with. Could be a storeId for example. 2)
 * system/increment_number/%s/separator. If the long number should be separated
 * into parts, we can specify a separator here. 3)
 * system/increment_number/%s/decorator. We can optionally define a decorator
 * for doing even more fancy stuff. The class must implement the
 * IncrementIdDecorator interface. 4) system/increment_number/%s/date_format. If
 * we want to split the id into different years and/or months, we can
 * specify a date-pattern. 5) system/increment_number/%s/type. The final number
 * can be converted into a particular java-type. Can be either String,
 * Long, Integer or Id. Default is String. 6)
 * system/increment_number/%s/start_at. What should the sequence-number start
 * at? If we did not want it to
 * start at 1, we could specify 10000001 for example.
 */
public enum IncrementIdGenerator {
    GET;

    private static final String PREFIX_CONFIG_KEY = "system/increment_number/%s/prefix";
    private static final String SEPARATOR_CONFIG_KEY = "system/increment_number/%s/separator";
    private static final String DECORATOR_CONFIG_KEY = "system/increment_number/%s/decorator";
    private static final String DATE_FORMAT_CONFIG_KEY = "system/increment_number/%s/date_format";
    private static final String TYPE_CONFIG_KEY = "system/increment_number/%s/type";
    private static final String START_AT_CONFIG_KEY = "system/increment_number/%s/start_at";

    private static final String DEFAULT_TYPE = "String";
    private static final String TYPE_STRING = "String";
    private static final String TYPE_LONG = "Long";
    private static final String TYPE_INTEGER = "Integer";
    private static final String TYPE_ID = "Id";

    private static final String CACHE_NAME = "gc/incrementidgenerator";
    private static final String DATE_FORMAT_CACHE_KEY = "date_format/%s";
    private static final String DECORATOR_CACHE_KEY = "decorator/%s";

    private final ConcurrentHashMap<String, Object> lockMap = new ConcurrentHashMap<>();;

    @SuppressWarnings("unchecked")
    public final <T> T nextId(String name) {
        App app = App.get();

        ApplicationContext appCtx = app.getApplicationContext();
        Store store = appCtx.getStore();

        if (name == null || store == null || store.getId() == null)
            throw new IllegalStateException("Name and store cannot be null when generating a new increment number.");

        Connection conn = app.inject(Connections.class).getSqlConnection();

        if (conn == null)
            throw new IllegalStateException("SQL connection cannot be null when generating a new increment number.");

        T returnValue = null;

        // We do not want to lock the whole object or method for performance
        // reasons.
        synchronized (getLock(name, store)) {
            try {
                // Begin transaction.
                begin(conn);

                // What should the increment_number start with?
                String prefix = app.cpStr_(String.format(PREFIX_CONFIG_KEY, name));
                // Do we want to add a separator char between the id-parts?
                String separator = app.cpStr_(String.format(SEPARATOR_CONFIG_KEY, name));
                // Do we want to do any special decoration? Class must implement
                // IncrementIdDecorator.
                String decorator = app.cpStr_(String.format(DECORATOR_CONFIG_KEY, name));
                // Do we want to add the year or month and year to the id.
                // Specify the pattern here.
                String dateFormat = app.cpStr_(String.format(DATE_FORMAT_CONFIG_KEY, name));
                // What should the increment id finally be converted to? String,
                // Long or Integer?
                String javaType = app.cpStr_(String.format(TYPE_CONFIG_KEY, name), DEFAULT_TYPE);

                StringBuilder newIncrementId = new StringBuilder();

                // -------------------------------------------------------------------------
                // First we start with a prefix. Typically this value is used if
                // we want
                // different starting number per store.
                // -------------------------------------------------------------------------
                if (prefix != null)
                    newIncrementId.append(prefix);

                // -------------------------------------------------------------------------
                // Optionally add a separator char if one is configured.
                // In most cased this would be one of '/' or '-'.
                // -------------------------------------------------------------------------
                if (separator != null)
                    newIncrementId.append(separator);

                // -------------------------------------------------------------------------
                // The next part of the increment number is part of a date. This
                // allows us
                // to separate the id in months and years if needed.
                // -------------------------------------------------------------------------
                if (dateFormat != null) {
                    String dataFormatKey = String.format(DATE_FORMAT_CACHE_KEY, dateFormat);
                    SimpleDateFormat sdf = (SimpleDateFormat) cache().get(dataFormatKey);

                    if (sdf == null) {
                        sdf = new SimpleDateFormat(dateFormat);
                        cache().put(dataFormatKey, sdf);
                    }

                    newIncrementId.append(sdf.format(DateTimes.newDate()));
                }

                // Separator.
                if (separator != null)
                    newIncrementId.append(separator);

                // -------------------------------------------------------------------------
                // Here we increment the number part in the form of a
                // sequence-number
                // with a select-for-update to make sure that it is always
                // unique.
                // -------------------------------------------------------------------------
                Number seqNumber = nextSequenceNumber(name, store.getId());
                newIncrementId.append(seqNumber);

                String strValue = newIncrementId.toString();

                // -------------------------------------------------------------------------
                // Finally we can use a decorator if all the above is not enough
                // and we
                // want to make it even more fancy. One will most likely want to
                // leave
                // the separator blank when using the decorator.
                // -------------------------------------------------------------------------
                if (decorator != null) {
                    String decoratorKey = String.format(DECORATOR_CACHE_KEY, dateFormat);
                    IncrementIdDecorator decoratorInstance = (IncrementIdDecorator) cache().get(decoratorKey);

                    if (decoratorInstance == null) {
                        try {
                            decoratorInstance = (IncrementIdDecorator) Class.forName(decorator).newInstance();
                            cache().put(decoratorKey, decoratorInstance);
                        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    if (decoratorInstance != null)
                        strValue = decoratorInstance.decorate(newIncrementId.toString());
                }

                // -------------------------------------------------------------------------
                // Finally we attempt to convert the value to a particular
                // java-type if
                // configured. By default this is a java.lang.String.
                // -------------------------------------------------------------------------
                if (TYPE_STRING.equals(javaType)) {
                    returnValue = (T) String.valueOf(strValue);
                } else if (TYPE_LONG.equals(javaType)) {
                    returnValue = (T) Long.valueOf(strValue);
                } else if (TYPE_INTEGER.equals(javaType)) {
                    returnValue = (T) Integer.valueOf(strValue);
                } else if (TYPE_ID.equals(javaType)) {
                    returnValue = (T) Id.valueOf(strValue);
                } else {
                    returnValue = (T) String.valueOf(strValue);
                }

                // Commit transaction.
                commit(conn);
            } catch (Throwable t) {
                returnValue = null;

                t.printStackTrace();

                // Rollback transaction.
                rollback(conn);

                throw new RuntimeException("IncrementId could not be generated for [name=" + name + ", storeId=" + store.getId() + "]", t);
            }
        }

        return returnValue;
    }

    private final void begin(Connection conn) throws SQLException {
        conn.setAutoCommit(false);
    }

    private final void commit(Connection conn) throws SQLException {
        conn.commit();
        conn.setAutoCommit(true);
    }

    private final void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException e) {
        }

        try {
            conn.setAutoCommit(true);
        } catch (SQLException e) {
        }
    }

    /**
     * Method for creating, updating and getting the next sequence number.
     * 
     * @param name
     * @param storeId
     * @return
     */
    private final Number nextSequenceNumber(String name, Id storeId) {
        Connection conn = App.get().inject(Connections.class).getSqlConnection();

        if (conn == null)
            throw new RuntimeException("Unable to start transaction because no jdbc connection has been initialized.");

        Number nextId = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement("SELECT id FROM increment_id WHERE name=? and store_id=? FOR UPDATE");
            pstmt.setString(1, name);
            pstmt.setLong(2, storeId.longValue());

            rs = pstmt.executeQuery();

            if (rs.next()) {
                nextId = rs.getLong("id") + 1;

                PreparedStatement upstmt = null;

                try {
                    upstmt = conn.prepareStatement("UPDATE increment_id SET id=? WHERE name=? and store_id=?");
                    upstmt.setLong(1, nextId.longValue());
                    upstmt.setString(2, name);
                    upstmt.setLong(3, storeId.longValue());
                    upstmt.execute();
                } finally {
                    IO.closeQuietly(upstmt);
                }
            } else {
                nextId = App.get().cpInt_(String.format(START_AT_CONFIG_KEY, name), 1);

                PreparedStatement ipstmt = null;

                try {
                    ipstmt = conn.prepareStatement("INSERT INTO increment_id (name, store_id, id) VALUES (?, ?, ?)");
                    ipstmt.setString(1, name);
                    ipstmt.setLong(2, storeId.longValue());
                    ipstmt.setLong(3, nextId.longValue());
                    ipstmt.execute();
                } finally {
                    IO.closeQuietly(ipstmt);
                }
            }
        } catch (Throwable t) {
            // Reset if an error occurs as the value in the database is about to
            // be rolled back.
            nextId = null;

            throw new RuntimeException(t);
        } finally {
            IO.closeQuietly(rs);
            IO.closeQuietly(pstmt);
        }

        return nextId;
    }

    private final Object getLock(String name, Store store) {
        StringBuilder lockName = new StringBuilder(name).append(Char.UNDERSCORE).append(store.getId().str());

        Object newLock = new Object();
        Object lock = lockMap.putIfAbsent(lockName.toString(), newLock);

        if (lock == null)
            lock = newLock;

        return lock;
    }

    private final Cache<String, Object> cache() {
        return App.get().inject(CacheManager.class).getCache(CACHE_NAME);
    }
}
