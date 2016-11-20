package com.geecommerce.core.service.persistence.jdbc;

import java.lang.invoke.MethodHandle;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.core.Str;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.db.Connections;
import com.geecommerce.core.event.Event;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.AbstractDao;
import com.geecommerce.core.service.Annotations;
import com.geecommerce.core.service.ColumnInfo;
import com.geecommerce.core.service.ColumnKey;
import com.geecommerce.core.service.DaoException;
import com.geecommerce.core.service.Models;
import com.geecommerce.core.service.QueryMetadata;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.api.ModelEnum;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.DateTimes;
import com.geecommerce.core.util.IO;
import com.geecommerce.core.util.Strings;
import com.google.common.collect.Sets;
import com.mongodb.DBCollection;

public abstract class AbstractSqlDao extends AbstractDao implements SqlDao {
    private static final String FIELD_KEY_ALL = "$__all";
    private static final String FIELD_ID = "id";

    private static final Map<String, Object> EMPTY_FILTER = new HashMap<String, Object>();
    private static final Map<String, String> COMPARISON_QUERY_OPERATORS = new HashMap<String, String>();
    static {
        COMPARISON_QUERY_OPERATORS.put("$gt", ">");
        COMPARISON_QUERY_OPERATORS.put("$gte", ">=");
        COMPARISON_QUERY_OPERATORS.put("$in", "in");
        COMPARISON_QUERY_OPERATORS.put("$lt", "<");
        COMPARISON_QUERY_OPERATORS.put("$lte", "<=");
        COMPARISON_QUERY_OPERATORS.put("$ne", "<>");
        COMPARISON_QUERY_OPERATORS.put("$nin", "not in");
    }

    private static final Logger log = LogManager.getLogger(AbstractSqlDao.class);

    public AbstractSqlDao(Connections connections, CacheManager cacheManager) {
        super(connections, cacheManager);
    }

    protected Connection connection() {
        return this.connections.getSqlConnection();
    }

    protected <T extends Model> String tableName(Class<T> modelClass) {
        String tblName = getTableName(modelClass);

        if (Str.isEmpty(tblName)) {
            Model m = app.model(modelClass);

            MethodHandle mh = Reflect.getMethodHandle(modelClass, "__name", true);

            try {
                Object o = (Object) mh.invokeExact((Object) m);

                if (o != null)
                    tblName = (String) o;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        if (Str.isEmpty(tblName))
            throw new IllegalStateException("The model class '" + modelClass.getName()
                + "' must specify the table name in the @Model annotation or in the __name() method.");

        return tblName;
    }

    protected abstract <T extends Model> String getTableName(Class<T> modelClass);

    @Override
    public <T extends Model> T findById(Class<T> modelClass, Id id) {
        return findById(modelClass, id, null);
    }

    @Override
    public <T extends Model> T findById(Class<T> modelClass, Id id, QueryOptions queryOptions) {
        if (modelClass == null || id == null)
            throw new DaoException("Not all parameters set [modelClass=" + modelClass + ", id=" + id + "]");

        T modelObject = null;

        boolean isCacheable = isCacheable(modelClass, queryOptions);
        boolean isRefresh = isRefresh(queryOptions);

        // If object exists and is cacheable, just return that.
        if (isCacheable && !isRefresh && !isPreview()) {
            // We want to also return null values if the key exists to avoid
            // unnecessary queries being made.
            if (modelCacheHasKeyFor(modelClass, id, queryOptions)) {
                return cacheGet(modelClass, id, queryOptions);
            }
        }

        PreparedStatement pstmt = null;
        ResultSet res = null;

        try {
            // Build SQL
            StringBuilder sql = new StringBuilder("SELECT * FROM `").append(tableName(modelClass))
                .append("` WHERE `_id`=?");

            // System.out.println("FIND SQL: " + sql.toString());

            pstmt = connection().prepareStatement(sql.toString());
            pstmt.setObject(1, convertObject(id));

            // C3P0ProxyStatement c3p0Stmt = (C3P0ProxyStatement) pstmt;
            // Method toStringMethod = Object.class.getMethod("toString", new
            // Class[] {});
            // Object toStr = c3p0Stmt.rawStatementOperation(toStringMethod,
            // C3P0ProxyStatement.RAW_STATEMENT, new
            // Object[] {});
            // if (toStr instanceof String)
            // {
            // String psql = (String) toStr;
            // psql = psql.substring(psql.indexOf('-') + 1).trim() + ";";
            // System.out.println(psql);
            // }

            Map<String, Object> map = new LinkedHashMap<>();

            res = pstmt.executeQuery();

            ResultSetMetaData metaData = res.getMetaData();
            int numColumns = metaData.getColumnCount();

            if (res.next()) {
                for (int j = 1; j <= numColumns; j++) {
                    map.put(metaData.getColumnLabel(j), new JDBCValue(res.getObject(j)));
                }

                modelObject = app.model(modelClass);

                notify(modelObject, Event.BEFORE_POPULATE);

                Models.populate(modelClass, modelObject, map);
                modelObject.fromMap(map);

                notify(modelObject, Event.AFTER_POPULATE);

                if (isCacheable && !isPreview() && modelObject != null && modelObject.getId() != null) {
                    cachePut(modelClass, modelObject, queryOptions);
                }
            }
            // If doc is null and the model class has been marked as cacheable,
            // we cache anyway to avoid
            // unnecessary queries if this object is requested again at a later
            // stage.
            else if (isCacheable && !isPreview()) {
                cachePutNull(modelClass, id, null);
            }
        } catch (Throwable t) {
            throw new DaoException(t);
        } finally {
            IO.closeQuietly(res);
            IO.closeQuietly(pstmt);
        }

        return modelObject;
    }

    @Override
    public <T extends Model> List<T> findAll(Class<T> modelClass) {
        return findAll(modelClass, null);
    }

    @Override
    public <T extends Model> List<T> findAll(Class<T> modelClass, QueryOptions queryOptions) {
        // Map<String, Object> filter = new HashMap<>();
        // filter.put(FIELD_KEY_ALL, true);

        return find(modelClass, null, queryOptions);
    }

    @Override
    public <T extends Model> List<T> find(Class<T> modelClass, Map<String, Object> filter) {
        return find(modelClass, filter, null);
    }

    // TODO: doesn't work Query options for now
    @Override
    public <T extends Model> List<T> find(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions) {
        // if (queryOptions != null)
        // throw new UnsupportedOperationException("Not supported
        // QueryOptions");

        if (modelClass == null)
            throw new DaoException("Not all parameters set [modelClass=" + modelClass + ", filter=" + filter + "]");

        long start = System.currentTimeMillis();

        List<T> modelObjects = new ArrayList<>();

        // This query seems to be of type findById(), so lets just call that to
        // avoid the extra processing.
        if (filter.size() == 1 && filter.containsKey(GlobalColumn.ID) && filter.values().toArray()[0] instanceof Id) {
            T modelObject = findById(modelClass, (Id) filter.values().toArray()[0]);
            modelObjects.add(modelObject);
            return modelObjects;
        }

        PreparedStatement pstmt = null;
        ResultSet res = null;

        try {
            Long count = null;
            if ((queryOptions != null && queryOptions.isProvideCount())) {
                count = count(modelClass, filter);
            }

            boolean isCacheable = Annotations.isCacheable(modelClass);
            boolean isRefresh = isRefresh(queryOptions);

            // If object exists and is cacheable, just return that.
            if (isCacheable && !isRefresh && !isPreview()) {
                // We want to also return null values if the key exists to avoid
                // unnecessary queries being made.
                if (queryCacheHasKeyFor(modelClass, filter, queryOptions)) {
                    List<Id> cachedIds = cacheGet(modelClass, filter, queryOptions);

                    if (cachedIds != null && cachedIds.size() > 0) {
                        for (Id id : cachedIds) {
                            modelObjects.add(findById(modelClass, id, queryOptions));
                        }
                    }

                    return modelObjects;
                }
            }

            // Build SQL
            StringBuilder sql = new StringBuilder("SELECT * FROM `").append(tableName(modelClass)).append("`");

            List<Object> whereValues = new ArrayList<>();

            buildQueryFilter(modelClass, filter, sql, whereValues);

            if (queryOptions != null) {
                appendSortBy(modelClass, queryOptions, sql);
                appendLimit(modelClass, queryOptions, sql);
            }

            // System.out.println("FIND SQL: " + sql.toString());

            // Add where-values and execute SQL

            pstmt = connection().prepareStatement(sql.toString());

            if (whereValues.size() > 0) {
                int i = 0;
                for (Object value : whereValues) {
                    if (value.getClass().isArray()) {
                        Object[] arr = (Object[]) value;

                        // System.out.println(Arrays.asList(arr));

                        for (Object o : arr) {
                            pstmt.setObject(++i, o);
                        }
                    } else {
                        pstmt.setObject(++i, value);
                    }
                }
            }

            // System.out.println(" sql#1: " + (System.currentTimeMillis() -
            // start));
            //
            // C3P0ProxyStatement c3p0Stmt = (C3P0ProxyStatement) pstmt;
            // Method toStringMethod = Object.class.getMethod("toString", new
            // Class[] {});
            // Object toStr = c3p0Stmt.rawStatementOperation(toStringMethod,
            // C3P0ProxyStatement.RAW_STATEMENT, new
            // Object[] {});
            // if (toStr instanceof String)
            // {
            // String psql = (String) toStr;
            // psql = psql.substring(psql.indexOf('-') + 1).trim() + ";";
            // System.out.println(psql);
            // }

            Map<String, Object> map = new LinkedHashMap<>();

            res = pstmt.executeQuery();

            // System.out.println(" sql#2: " + (System.currentTimeMillis() -
            // start));

            ResultSetMetaData metaData = res.getMetaData();
            int numColumns = metaData.getColumnCount();

            List<Id> cachedIds = new ArrayList<>();

            while (res.next()) {
                for (int j = 1; j <= numColumns; j++) {
                    map.put(metaData.getColumnLabel(j), new JDBCValue(res.getObject(j)));
                }

                T modelObject = app.model(modelClass);

                notify(modelObject, Event.BEFORE_POPULATE);

                Models.populate(modelClass, modelObject, map);
                modelObject.fromMap(map);

                notify(modelObject, Event.AFTER_POPULATE);

                modelObjects.add(modelObject);

                if (isCacheable && !isPreview() && modelObject != null && modelObject.getId() != null) {
                    cachedIds.add(modelObject.getId());
                    cachePut(modelClass, modelObject, queryOptions);
                }
            }

            // System.out.println(" sql#3: " + (System.currentTimeMillis() -
            // start));

            // Also cache if the list is empty to avoid unnecessary queries
            // being made over and over again.
            if (isCacheable && !isPreview()) {
                cachePut(modelClass, filter, queryOptions, cachedIds);
            }
        } catch (Throwable t) {
            throw new DaoException(t);
        } finally {
            IO.closeQuietly(res);
            IO.closeQuietly(pstmt);
        }

        return modelObjects;
    }

    @Override
    public <T extends Model> List<T> findByIds(Class<T> modelClass, Id[] ids) {
        return findByIds(modelClass, ids, null);
    }

    @Override
    public <T extends Model> List<T> findByIds(Class<T> modelClass, Id[] ids, QueryOptions queryOptions) {
        return null;
    }

    public <T extends Model> T findOne(Class<T> modelClass, Map<String, Object> filter) {
        List<T> modelObjects = find(modelClass, filter, null);
        if (modelObjects != null && modelObjects.size() > 0)
            return modelObjects.get(0);
        return null;
    }

    @Override
    public <T extends Model> List<Id> findIds(Class<T> modelClass, Map<String, Object> filter) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public <T extends Model> List<Id> findIds(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions) {
        throw new RuntimeException("Operation not supported");
    }

    protected <T extends Model> void buildQueryFilter(Class<T> modelClass, Map<String, Object> filter,
        StringBuilder sql, List<Object> whereValues) {
        if (filter != null && filter.size() > 0) {
            sql.append(" WHERE ");

            List<ColumnInfo> columnInfos = Annotations.getColumns(modelClass);

            int x = 0;
            for (String column : filter.keySet()) {
                if (FIELD_KEY_ALL.equals(column))
                    continue;

                ColumnInfo columnInfo = Models.columnInfo(columnInfos, column);

                if (x > 0) {
                    sql.append(" AND ");
                }

                Object val = filter.get(column);

                if (val == null) {
                    sql.append("`").append(column).append("`").append(" is null");
                } else {
                    if (val instanceof Map && ((Map) val).size() > 0) {
                        String operator = ((Map<String, Object>) val).keySet().iterator().next();

                        if (COMPARISON_QUERY_OPERATORS.containsKey(operator.trim())) {
                            sql.append("`").append(column).append("` ").append(COMPARISON_QUERY_OPERATORS.get(operator))
                                .append(" ?");
                        }
                    } else if (val instanceof Collection<?> || val.getClass().isArray()) {
                        int len = val.getClass().isArray() ? ((Object[]) val).length : ((Collection<?>) val).size();

                        StringBuilder sb = new StringBuilder();

                        for (int i = 0; i < len; i++) {
                            if (i > 0)
                                sb.append(", ");

                            sb.append("?");
                        }
                        sql.append("`").append(column).append("`").append(" in ( ").append(sb.toString()).append(" )");
                    } else {
                        sql.append("`").append(column).append("`").append("= ?");
                    }

                    whereValues.add(convertObject(val, columnInfo));
                }

                x++;
            }
        }
    }

    @Override
    public <T extends Model> Long count(Class<T> modelClass, Map<String, Object> filter) {
        return count(modelClass, filter, null);
    }

    @Override
    public <T extends Model> Long count(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions) {
        if (modelClass == null)
            throw new DaoException("Model class cannot be null");

        if (filter == null)
            filter = EMPTY_FILTER;

        Long count = null;

        if (queryOptions == null || (!queryOptions.isNoCache() && !queryOptions.isRefresh()))
            count = countCacheGet(modelClass, filter);

        if (count == null) {
            PreparedStatement pstmt = null;
            ResultSet res = null;

            try {
                // Build SQL
                StringBuilder sql = new StringBuilder("SELECT COUNT(*) AS total FROM `").append(tableName(modelClass))
                    .append("`");

                List<Object> whereValues = new ArrayList<>();

                buildQueryFilter(modelClass, filter, sql, whereValues);

                pstmt = connection().prepareStatement(sql.toString());

                if (whereValues.size() > 0) {
                    int i = 0;
                    for (Object value : whereValues) {
                        if (value.getClass().isArray()) {
                            Object[] arr = (Object[]) value;

                            // System.out.println(Arrays.asList(arr));

                            for (Object o : arr) {
                                pstmt.setObject(++i, o);
                            }
                        } else {
                            pstmt.setObject(++i, value);
                        }
                    }
                }

                res = pstmt.executeQuery();

                while (res.next()) {
                    count = res.getLong("total");
                }

            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                IO.closeQuietly(res);
                IO.closeQuietly(pstmt);
            }

        }

        if (count != null) {
            app.setQueryMetadata(QueryMetadata.builder().count(count).build());
        }

        return count;
    }

    @SuppressWarnings({ "unchecked", "rawtypes", "resource" })
    public <T extends Model> T create(T entity) {
        if (entity == null)
            throw new DaoException("Entity cannot be null");

        Map<String, Object> map = null;
        PreparedStatement pstmt = null;
        Connection conn = null;
        String query = null;

        Class modelClass = entity.getClass();

        boolean isBatchModeEnabled = JDBC.isBatchModeEnabled();
        boolean isBatchCommitOnFlushEnabled = JDBC.isBatchCommitOnFlushEnabled();
        int batchFlushInterval = JDBC.getBatchFlushInterval();
        int batchProcessCount = JDBC.getBatchProcessingCount();
        boolean batchError = false;

        String errorField = null;
        Object errorValue = null;

        try {
            conn = connection();

            // If we are in batch mode and want to commit on flush interval, we
            // turn off auto-commit.
            if (isBatchModeEnabled && isBatchCommitOnFlushEnabled) {
                conn.setAutoCommit(false);
            }

            if (!isBatchModeEnabled)
                notify(entity, Event.BEFORE_NEW);

            map = Models.toMap(modelClass, entity);
            map.putAll(((Model) entity).toMap());

            if (map.get(GlobalColumn.ID) == null) {
                map.put(GlobalColumn.ID, app.nextId());
            }

            if (map.get(GlobalColumn.CREATED_ON) == null)
                map.put(GlobalColumn.CREATED_ON, DateTimes.newDate());

            if (Annotations.isOptimisticLockingEnabled(modelClass)) {
                map.put(GlobalColumn.VERSION, 1);
            } else {
                map.remove(GlobalColumn.VERSION);
            }

            map.remove(GlobalColumn.HISTORY_ID);
            map.remove(GlobalColumn.HISTORY_DATE);

            // Build SQL
            // StringBuilder sql = new StringBuilder("INSERT INTO
            // \"").append(tableName(modelClass)).append("\" (");
            StringBuilder sql = new StringBuilder("INSERT INTO `").append(tableName(modelClass)).append("` ("); // mysql
            StringBuilder valuePart = new StringBuilder("VALUES (");

            List<ColumnInfo> columnInfos = Annotations.getColumns(modelClass);

            Map<ColumnKey, Object> values = new LinkedHashMap<>();

            int x = 0;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                errorField = entry.getKey();
                errorValue = value;

                if (value == null)
                    continue;

                if (key == null || "".equals(key.trim())) {
                    throw new DaoException("Key in map cannot be null or empty");
                }

                if (x > 0) {
                    sql.append(", ");
                    valuePart.append(", ");
                }

                // sql.append("\"").append(key).append("\""); // column name
                sql.append("`").append(key).append("`"); // mysql
                valuePart.append("?");

                ColumnInfo columnInfo = Models.columnInfo(columnInfos, key);

                if ((GlobalColumn.CREATED_ON.equals(key) || GlobalColumn.MODIFIED_ON.equals(key))
                    && map.get(key) == null) {
                    values.put(new ColumnKey(key, columnInfo), new Timestamp(DateTimes.newDate().getTime()));
                } else {
                    values.put(new ColumnKey(key, columnInfo), convertObject(map.get(key), columnInfo));
                }

                x++;
            }

            sql.append(") ").append(valuePart).append(");");

            query = sql.toString();

            if (log.isTraceEnabled()) {
                log.trace("INSERT SQL: " + query);
            }

            System.out.println("INSERT SQL: " + query);

            // Fetch the already prepared statement in batch mode.
            if (JDBC.isBatchModeEnabled())
                pstmt = JDBC.fetchPreparedStatement(modelClass, query);

            // If statement does not exist or batch mode is not enabled, create
            // new prepared statement.
            if (pstmt == null || !JDBC.isBatchModeEnabled()) {
                pstmt = conn.prepareStatement(query);

                // Remember the prepared statement for the next batch insert.
                if (JDBC.isBatchModeEnabled())
                    JDBC.stashPreparedStatement(modelClass, query, pstmt);
            }

            // Add where-values and execute SQL

            int i = 0;
            for (Map.Entry<ColumnKey, Object> entry : values.entrySet()) {
                Object value = entry.getValue();
                ColumnKey columnKey = entry.getKey();
                ColumnInfo columnInfo = columnKey.columnInfo();
                String columnName = columnKey.name();

                if (value == null)
                    continue;

                Class<?> type = columnInfo == null ? value.getClass() : columnInfo.type();

                if (type == null)
                    type = value.getClass();

                errorField = columnName;
                errorValue = value;

                if (String.class.isAssignableFrom(type)) {
                    pstmt.setString(++i, (String) value);
                } else if (BigInteger.class.isAssignableFrom(type)) {
                    pstmt.setString(++i, String.valueOf(value));
                } else if (java.util.Date.class.isAssignableFrom(type)) {
                    pstmt.setTimestamp(++i, new Timestamp(((java.util.Date) value).getTime()));
                } else if (Timestamp.class.isAssignableFrom(type)) {
                    pstmt.setTimestamp(++i, (Timestamp) value);
                } else if (Date.class.isAssignableFrom(type)) {
                    pstmt.setDate(++i, (Date) value);
                } else if (ModelEnum.class.isAssignableFrom(type)) {
                    pstmt.setInt(++i, (Integer) value);
                } else if (byte[].class.isAssignableFrom(type)) {
                    pstmt.setBytes(++i, (byte[]) value);
                } else if (byte.class.isAssignableFrom(type)) {
                    pstmt.setByte(++i, (byte) value);
                    // } else if (ContextObject.class.isAssignableFrom(type) ||
                    // Map.class.isAssignableFrom(type) ||
                    // Model.class.isAssignableFrom(type)) {
                    // PGobject obj = new PGobject();
                    // obj.setType("jsonb");
                    // obj.setValue(JSON.serialize(value));
                    // pstmt.setObject(++i, obj);
                } else if (value instanceof Collection) {
                    List<Class<?>> genericType = null;

                    if (columnInfo != null)
                        genericType = Reflect.getGenericType(columnInfo.genericType());

                    if (genericType == null && !((Collection) value).isEmpty()) {
                        genericType = new ArrayList<>();
                        genericType.add(((Collection) value).iterator().next().getClass());
                    }

                    boolean isJson = false;

                    if (!(genericType != null && !genericType.isEmpty()
                        && (String.class.isAssignableFrom(genericType.get(0))
                            || char.class.isAssignableFrom(genericType.get(0))
                            || char[].class.isAssignableFrom(genericType.get(0))
                            || BigInteger.class.isAssignableFrom(genericType.get(0))
                            || Long.class.isAssignableFrom(genericType.get(0))
                            || Integer.class.isAssignableFrom(genericType.get(0))
                            || BigDecimal.class.isAssignableFrom(genericType.get(0))
                            || Double.class.isAssignableFrom(genericType.get(0))
                            || Float.class.isAssignableFrom(genericType.get(0))
                            || Boolean.class.isAssignableFrom(genericType.get(0))))) {
                        isJson = true;
                    }

                    // if (isJson) {
                    // System.out.println("!! IS LIST WITH CONTEXT OBJECTS OR
                    // ATTRIBUTES: " + genericType);
                    // PGobject obj = new PGobject();
                    // obj.setType("jsonb");
                    // obj.setValue(JSON.serialize(value));
                    // pstmt.setObject(++i, obj);
                    // } else {
                    final Array sqlArr = toSqlArray((Collection) value, columnInfo, conn);
                    if (((Collection) value).isEmpty()) {
                        pstmt.setArray(++i, null);
                    } else {
                        pstmt.setArray(++i, sqlArr);
                    }
                    // }
                } else if (value != null && value.getClass().isArray()) {
                    final Object[] arr = (Object[]) value;

                    if (arr.length == 0) {
                        pstmt.setArray(++i, null);
                    } else {
                        final Array sqlArr = toSqlArray(arr, conn);
                        pstmt.setArray(++i, sqlArr);
                    }

                } else {
                    pstmt.setObject(++i, value);
                }
            }

            if (isBatchModeEnabled) {
                // Add the current insert to the batch.
                pstmt.addBatch();

                // If auto process count is enabled, we increment to counter.
                if (JDBC.isBatchAutoProcessingCountEnabled()) {
                    JDBC.incrementBatchProcessingCount();
                }
            } else {
                // If we are not in batch mode, then we just execute the
                // statement.
                pstmt.executeUpdate();

                // Remove all entries from the query-cache for now, to make sure
                // that this object
                // can be found in subsequent find-calls. TODO: Make more
                // intelligent in the future.

                if (isCacheable(modelClass)) {
                    invalidateModelCache(modelClass);

                    invalidateQueryCache(modelClass);
                }

                // Only works in none-batch mode for now (which is most of the
                // time anyway).
                notify(entity, Event.AFTER_NEW);
            }

            // If we have reached the flush interval, we execute the batch and
            // commit if necessary.
            if (isBatchModeEnabled && batchProcessCount > 0 && (batchProcessCount % batchFlushInterval) == 0) {
                pstmt.executeBatch();

                if (isBatchCommitOnFlushEnabled) {
                    conn.commit();
                    conn.setAutoCommit(true);
                }
            }
        } catch (Throwable t) {
            System.out.println("Create ERROR: " + errorField + " --> " + errorValue);

            if (isBatchModeEnabled && isBatchCommitOnFlushEnabled) {
                batchError = true;

                try {
                    conn.rollback();
                    conn.setAutoCommit(true);
                } catch (Throwable t2) {

                }
            }

            throw new RuntimeException(t);
        } finally {
            if (batchError
                || (isBatchModeEnabled && batchProcessCount > 0 && (batchProcessCount % batchFlushInterval) == 0)) {
                JDBC.resetBatchProcessingCount();

                if (JDBC.isBatchRenewPreparedStatementOnFlushEnabled()) {
                    IO.closeQuietly(pstmt);
                    JDBC.resetPreparedStatement(modelClass, query);
                } else {
                    try {
                        pstmt.clearBatch();
                    } catch (Throwable t) {

                    }
                }
            } else if (!isBatchModeEnabled) {
                IO.closeQuietly(pstmt);
            }
        }

        if (Reflect.fieldExists(FIELD_ID, modelClass))
            Reflect.setField(entity, FIELD_ID, map.get(GlobalColumn.ID));

        return entity;
    }

    protected Class<?> getType(String name, Class modelClass) {
        List<ColumnInfo> columnInfos = Annotations.getColumns(modelClass);

        ColumnInfo colInfo = Models.columnInfo(columnInfos, name);

        return colInfo == null ? null : colInfo.type();
    }

    @SuppressWarnings("unchecked")
    protected Array toSqlArray(Collection<?> values, ColumnInfo columnInfo, Connection conn) throws SQLException {
        List<?> genericType = Reflect.getGenericType(columnInfo.genericType());

        if (genericType == null || genericType.isEmpty()) {
            System.out.println("toSqlArray#1: " + genericType);
            return toSqlArray(values.toArray(), conn);
        } else {
            System.out.println("toSqlArray#2a: " + genericType);

            Object[] arr = (Object[]) java.lang.reflect.Array.newInstance((Class) genericType.get(0), values.size());

            System.out.println("toSqlArray#2b: " + arr);

            return toSqlArray(arr, conn);
        }
    }

    @SuppressWarnings("rawtypes")
    protected Array toSqlArray(Object[] values, Connection conn) throws SQLException {
        Class<?> arrayType = values.getClass().getComponentType();

        System.out.println("Type 1: " + arrayType.getName());

        if (arrayType == Object.class && values.length > 0) {
            arrayType = values[0].getClass();
        }

        System.out.println("Type 2: " + arrayType.getName() + " - " + values.length);

        Array sqlArray = null;

        if (arrayType == Object.class && values.length == 0) {
            sqlArray = conn.createArrayOf("text", values);
        } else if (String.class.isAssignableFrom(arrayType) || char.class.isAssignableFrom(arrayType)
            || char[].class.isAssignableFrom(arrayType)) {
            System.out.println("text!");
            sqlArray = conn.createArrayOf("text", values);
        } else if (BigInteger.class.isAssignableFrom(arrayType) || Long.class.isAssignableFrom(arrayType)) {
            System.out.println("bigint! --> " + Arrays.asList((Long[]) values));
            sqlArray = conn.createArrayOf("bigint", (Long[]) values);
        } else if (Integer.class.isAssignableFrom(arrayType)) {
            System.out.println("integer!");
            sqlArray = conn.createArrayOf("integer", values);
        } else if (BigDecimal.class.isAssignableFrom(arrayType) || Double.class.isAssignableFrom(arrayType)) {
            System.out.println("decimal!");
            sqlArray = conn.createArrayOf("decimal", values);
        } else if (Float.class.isAssignableFrom(arrayType)) {
            System.out.println("float!");
            sqlArray = conn.createArrayOf("float", values);
        } else if (Number.class.isAssignableFrom(arrayType)) {
            System.out.println("bigint! --> " + Arrays.asList((Number[]) values));
            sqlArray = conn.createArrayOf("bigint", (Number[]) values);
        } else if (Boolean.class.isAssignableFrom(arrayType)) {
            System.out.println("boolean!");
            sqlArray = conn.createArrayOf("boolean", values);
        } else {
            System.out.println("text2! -> " + arrayType);
            sqlArray = conn.createArrayOf("text", values);
        }

        return sqlArray;
    }

    public static void main(String[] args) {

        Collection c = new ArrayList<Long>();
        c.add(1111111111111L);

        Object[] values = c.toArray(new Long[1]);

        Class<?> arrayType = values.getClass().getComponentType();

        System.out.println("Type 1: " + arrayType.getName());

        if (arrayType == Object.class && values.length > 0) {
            arrayType = values[0].getClass();
        }

        System.out.println("Type 2: " + arrayType.getName());

        Array sqlArray = null;

        if (arrayType == String.class || arrayType == char.class) {
            System.out.println("TEXT!");
        } else if (arrayType == BigInteger.class || arrayType == Long.class) {
            System.out.println("int8!");
        } else if (arrayType == Integer.class) {
            System.out.println("int4!");
        } else if (arrayType == BigDecimal.class || arrayType == Double.class) {
            System.out.println("numeric!");
        } else if (arrayType == Float.class) {
            System.out.println("float4!");
        } else if (arrayType == Boolean.class) {
            System.out.println("bool!");
        } else {
            System.out.println("TEXT2!");
        }
    }

    @Override
    public <T extends Model> void update(T entity, Map<String, Object> filter) {
        throw new RuntimeException("Operation not supported. Use update(entity) instead.");
    }

    @Override
    public <T extends Model> void update(T entity, Map<String, Object> filter, boolean upsert) {
        throw new RuntimeException("Operation not supported. Use update(entity) instead.");
    }

    @Override
    public <T extends Model> void update(T entity, Map<String, Object> filter, boolean upsert, boolean multi) {
        throw new RuntimeException("Operation not supported. Use update(entity) instead.");
    }

    @Override
    public <T extends Model> void update(T entity, Map<String, Object> filter, boolean upsert, boolean multi,
        String... updateFields) {
        throw new RuntimeException("Operation not supported. Use update(entity) instead.");
    }

    @SuppressWarnings({ "unchecked", "rawtypes", "resource" })
    @Override
    public <T extends Model> void update(T entity) {
        if (entity == null)
            throw new DaoException("Entity cannot be null");

        PreparedStatement pstmt = null;
        Connection conn = null;
        String query = null;

        Class modelClass = entity.getClass();

        boolean isBatchModeEnabled = JDBC.isBatchModeEnabled();
        boolean isBatchCommitOnFlushEnabled = JDBC.isBatchCommitOnFlushEnabled();
        int batchFlushInterval = JDBC.getBatchFlushInterval();
        int batchProcessCount = JDBC.getBatchProcessingCount();
        boolean batchError = false;

        try {
            conn = connection();

            // If we are in batch mode and want to commit on flush interval, we
            // turn off auto-commit.
            if (isBatchModeEnabled && isBatchCommitOnFlushEnabled) {
                conn.setAutoCommit(false);
            }

            if (!isBatchModeEnabled)
                notify(entity, Event.BEFORE_UPDATE);

            Map<String, Object> map = Models.toMap(modelClass, entity);
            map.putAll(((Model) entity).toMap());

            // Map<String, Object> map = ((Model) entity).toMap();

            boolean isOptimisticLockingEnabled = Annotations.isOptimisticLockingEnabled(modelClass);

            Integer version = null;

            if (isOptimisticLockingEnabled) {
                version = (Integer) map.get(GlobalColumn.VERSION);

                // We start with 1 if version field does not exist yet.
                if (version == null) {
                    map.put(GlobalColumn.VERSION, 1);
                } else {
                    map.put(GlobalColumn.VERSION, version + 1);
                }
            } else {
                map.remove(GlobalColumn.VERSION);
            }

            map.remove(GlobalColumn.HISTORY_ID);
            map.remove(GlobalColumn.HISTORY_DATE);

            Set<String> columnNames = map.keySet();

            if (columnNames == null || columnNames.size() == 0) {
                throw new DaoException("No columns found to update in entity '" + modelClass
                    + "'. The entity must either return a valid map or have fields marked with the @Column annotation.");
            }

            if (map.get(GlobalColumn.ID) == null) {
                throw new IllegalStateException("Cannot update document with null id");
            }

            // Create lists of fields to update
            Set<String> updatableFields = Sets.newLinkedHashSet();

            for (String columnName : columnNames) {
                if (!GlobalColumn.ID.equals(columnName.trim())) {
                    // We don't want to fail during an update just because the
                    // timestamp is not set.
                    if (GlobalColumn.CREATED_ON.equals(columnName) && map.get(columnName) == null)
                        continue;

                    updatableFields.add(columnName);
                }
            }

            // Build SQL
            StringBuilder sql = new StringBuilder("UPDATE `").append(tableName(modelClass)).append("` SET ");

            int x = 0;
            for (String columnName : updatableFields) {
                if (columnName == null || "".equals(columnName.trim())) {
                    throw new DaoException("Key in map cannot be null or empty");
                }

                if (x > 0) {
                    sql.append(", ");
                }

                sql.append("`").append(columnName).append("`").append("=?");

                x++;
            }

            sql.append(" WHERE `").append(GlobalColumn.ID).append("`=?");

            if (isOptimisticLockingEnabled && version != null) {
                sql.append(" AND `").append(GlobalColumn.VERSION).append("`=?");
            }

            query = sql.toString();

            if (log.isTraceEnabled()) {
                log.trace("UPDATE SQL: " + query);
            }

            // pstmt = connection().prepareStatement(sql.toString());

            // Fetch the already prepared statement in batch mode.
            if (JDBC.isBatchModeEnabled())
                pstmt = JDBC.fetchPreparedStatement(modelClass, query);

            // If statement does not exist or batch mode is not enabled, create
            // new prepared statement.
            if (pstmt == null || !JDBC.isBatchModeEnabled()) {
                pstmt = conn.prepareStatement(query);

                // Remember the prepared statement for the next batch update.
                if (JDBC.isBatchModeEnabled())
                    JDBC.stashPreparedStatement(modelClass, query, pstmt);
            }

            List<ColumnInfo> columnInfos = Annotations.getColumns(modelClass);

            int i = 0;
            for (String columnName : updatableFields) {
                if (GlobalColumn.MODIFIED_ON.equals(columnName)) {
                    pstmt.setTimestamp(++i, new Timestamp(DateTimes.newDate().getTime()));
                } else {
                    Object value = map.get(columnName);

                    if (value instanceof String) {
                        pstmt.setString(++i, (String) value);
                    } else if (value instanceof BigInteger) {
                        pstmt.setString(++i, String.valueOf(value));
                    } else if (value instanceof Timestamp) {
                        pstmt.setTimestamp(++i, (Timestamp) value);
                    } else if (value instanceof Date) {
                        pstmt.setDate(++i, (Date) value);
                    } else {
                        ColumnInfo columnInfo = Models.columnInfo(columnInfos, columnName);

                        pstmt.setObject(++i, convertObject(value, columnInfo));
                    }
                }
            }

            pstmt.setObject(++i, convertObject(map.get(GlobalColumn.ID)));

            if (isOptimisticLockingEnabled && version != null) {
                pstmt.setInt(++i, version);
            }

            // pstmt.executeUpdate();

            if (isBatchModeEnabled) {
                // Add the current update to the batch.
                pstmt.addBatch();

                // If auto process count is enabled, we increment to counter.
                if (JDBC.isBatchAutoProcessingCountEnabled()) {
                    JDBC.incrementBatchProcessingCount();
                }
            } else {
                // If we are not in batch mode, then we just execute the
                // statement.
                pstmt.executeUpdate();

                if (isCacheable(modelClass)) {
                    invalidateModelCache(modelClass);

                    // We also invalidate the query cache for now, in case a
                    // sort number has changed.
                    // TODO: Make more intelligent in the future.
                    invalidateQueryCaches(entity.getId());
                }

                notify(entity, Event.AFTER_UPDATE);
            }

            // If we have reached the flush interval, we execute the batch and
            // commit if necessary.
            if (isBatchModeEnabled && batchProcessCount > 0 && (batchProcessCount % batchFlushInterval) == 0) {
                pstmt.executeBatch();

                if (isBatchCommitOnFlushEnabled) {
                    conn.commit();
                    conn.setAutoCommit(true);
                }
            }
        } catch (Throwable t) {
            if (isBatchModeEnabled && isBatchCommitOnFlushEnabled) {
                batchError = true;

                try {
                    conn.rollback();
                    conn.setAutoCommit(true);
                } catch (Throwable t2) {

                }
            }

            throw new RuntimeException(t);
        } finally {
            if (batchError
                || (isBatchModeEnabled && batchProcessCount > 0 && (batchProcessCount % batchFlushInterval) == 0)) {
                JDBC.resetBatchProcessingCount();

                if (JDBC.isBatchRenewPreparedStatementOnFlushEnabled()) {
                    IO.closeQuietly(pstmt);
                    JDBC.resetPreparedStatement(modelClass, query);
                } else {
                    try {
                        pstmt.clearBatch();
                    } catch (Throwable t) {

                    }
                }
            } else if (!isBatchModeEnabled) {
                IO.closeQuietly(pstmt);
            }
        }
    }

    @SuppressWarnings({ "unchecked", "resource", "rawtypes" })
    @Override
    public <T extends Model> void delete(T entity) {
        if (entity == null)
            throw new DaoException("Entity cannot be null");

        PreparedStatement pstmt = null;
        Connection conn = null;
        String query = null;

        Class modelClass = entity.getClass();

        boolean isBatchModeEnabled = JDBC.isBatchModeEnabled();
        boolean isBatchCommitOnFlushEnabled = JDBC.isBatchCommitOnFlushEnabled();
        int batchFlushInterval = JDBC.getBatchFlushInterval();
        int batchProcessCount = JDBC.getBatchProcessingCount();
        boolean batchError = false;

        try {
            conn = connection();

            // If we are in batch mode and want to commit on flush interval, we
            // turn off auto-commit.
            if (isBatchModeEnabled && isBatchCommitOnFlushEnabled) {
                conn.setAutoCommit(false);
            }

            if (!isBatchModeEnabled)
                notify(entity, Event.BEFORE_REMOVE);

            Map<String, Object> map = Models.toMap((Class<T>) entity.getClass(), entity);
            map.putAll(((Model) entity).toMap());

            Set<String> columnNames = map.keySet();

            if (columnNames == null || columnNames.size() == 0) {
                throw new DaoException("No columns found to update in entity '" + entity.getClass()
                    + "'. The entity must either return a valid map or have fields marked with the @Column annotation.");
            }

            if (map.get(GlobalColumn.ID) == null) {
                throw new IllegalStateException("Cannot delete document with null id");
            }

            // Build SQL
            StringBuilder sql = new StringBuilder("DELETE FROM `").append(tableName(entity.getClass()))
                .append("` WHERE `").append(GlobalColumn.ID).append("`=?");

            query = sql.toString();

            if (log.isTraceEnabled()) {
                log.trace("DELETE SQL: " + query);
            }

            // pstmt = connection().prepareStatement(sql.toString());

            // Fetch the already prepared statement in batch mode.
            if (JDBC.isBatchModeEnabled())
                pstmt = JDBC.fetchPreparedStatement(modelClass, query);

            // If statement does not exist or batch mode is not enabled, create
            // new prepared statement.
            if (pstmt == null || !JDBC.isBatchModeEnabled()) {
                pstmt = conn.prepareStatement(query);

                // Remember the prepared statement for the next batch delete.
                if (JDBC.isBatchModeEnabled())
                    JDBC.stashPreparedStatement(modelClass, query, pstmt);
            }

            pstmt.setObject(1, convertObject(map.get(GlobalColumn.ID)));

            // pstmt.executeUpdate();

            if (isBatchModeEnabled) {
                // Add the current delete to the batch.
                pstmt.addBatch();

                // If auto process count is enabled, we increment to counter.
                if (JDBC.isBatchAutoProcessingCountEnabled()) {
                    JDBC.incrementBatchProcessingCount();
                }
            } else {
                // If we are not in batch mode, then we just execute the
                // statement.
                pstmt.executeUpdate();

                // If the object is cacheable, remove it after the update.
                if (isCacheable(modelClass)) {
                    invalidateModelCache(entity.getClass());

                    // Remove all entries from the query-cache containing this
                    // id, to make sure that this
                    // object does not turn up in future find calls. TODO: Make
                    // more intelligent in the future.

                    invalidateQueryCaches(entity.getId());
                }

                notify(entity, Event.AFTER_REMOVE);
            }

            // If we have reached the flush interval, we execute the batch and
            // commit if necessary.
            if (isBatchModeEnabled && batchProcessCount > 0 && (batchProcessCount % batchFlushInterval) == 0) {
                pstmt.executeBatch();

                if (isBatchCommitOnFlushEnabled) {
                    conn.commit();
                    conn.setAutoCommit(true);
                }
            }
        } catch (Throwable t) {
            if (isBatchModeEnabled && isBatchCommitOnFlushEnabled) {
                batchError = true;

                try {
                    conn.rollback();
                    conn.setAutoCommit(true);
                } catch (Throwable t2) {

                }
            }

            throw new RuntimeException(t);
        } finally {
            if (batchError
                || (isBatchModeEnabled && batchProcessCount > 0 && (batchProcessCount % batchFlushInterval) == 0)) {
                JDBC.resetBatchProcessingCount();

                if (JDBC.isBatchRenewPreparedStatementOnFlushEnabled()) {
                    IO.closeQuietly(pstmt);
                    JDBC.resetPreparedStatement(modelClass, query);
                } else {
                    try {
                        pstmt.clearBatch();
                    } catch (Throwable t) {

                    }
                }
            } else if (!isBatchModeEnabled) {
                IO.closeQuietly(pstmt);
            }
        }
    }

    /**
     * TODO when needed.
     */
    public <T extends Model> void delete(Class<T> modelClass, Map<String, Object> filter) {
        throw new RuntimeException("Operation not supported. Use delete(entity) instead.");
    }

    public Object convertObject(Object obj) {
        return convertObject(obj, null); // TODO!!
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object convertObject(Object obj, ColumnInfo columnInfo) {
        if (obj == null)
            return null;

        if (obj instanceof Id) {
            return ((Id) obj).num();
        } else if (obj instanceof ModelEnum) {
            return ((ModelEnum) obj).toId();
        } else if (obj.getClass().isArray()) {
            Class<?> componentType = obj.getClass().getComponentType();

            if (Id.class.isAssignableFrom(componentType)) {
                List<Long> numIds = new ArrayList<>();
                for (Id id : (Id[]) obj) {
                    numIds.add(id.num().longValue());
                }

                return numIds.toArray(new Long[numIds.size()]);
            } else if (ModelEnum.class.isAssignableFrom(componentType)) {
                List<Integer> numIds = new ArrayList<>();
                for (ModelEnum e : (ModelEnum[]) obj) {
                    numIds.add(e.toId());
                }

                return numIds.toArray(new Integer[numIds.size()]);
            } else {
                return obj;
            }

        } else if (!(obj instanceof ContextObject) && (obj instanceof Collection)) {
            Collection coll = (Collection) obj;
            Class<?> componentType = null;
            int targetLen = ((Collection) obj).size();

            if (columnInfo != null) {
                List<Class<?>> genericType = Reflect.getGenericType(columnInfo.genericType());
                componentType = genericType == null || genericType.isEmpty() ? Object.class : genericType.get(0);
            } else {
                if (targetLen > 0) {
                    componentType = coll.iterator().next().getClass();
                } else {
                    componentType = Object.class;
                }
            }

            if (Id.class.isAssignableFrom(componentType)) {
                List<Number> numIds = new ArrayList<>();
                for (Id id : (Collection<Id>) obj) {
                    numIds.add(id.num());
                }

                return numIds.toArray(new Number[numIds.size()]);
            } else if (ModelEnum.class.isAssignableFrom(componentType)) {
                List<Number> numIds = new ArrayList<>();
                for (ModelEnum e : (Collection<ModelEnum>) obj) {
                    numIds.add(e.toId());
                }

                return numIds.toArray(new Number[numIds.size()]);
            } else if (String.class.isAssignableFrom(componentType)) {
                return coll.toArray(new String[coll.size()]);
            } else if (Number.class.isAssignableFrom(componentType)) {
                return coll.toArray(new Number[coll.size()]);
            } else if (Boolean.class.isAssignableFrom(componentType)) {
                return coll.toArray(new Boolean[coll.size()]);
            } else if (java.util.Date.class.isAssignableFrom(componentType)) {
                return coll.toArray(new java.util.Date[coll.size()]);
            } else {
                return coll;
            }
        } else {
            return obj;
        }
    }

    @Override
    public <T extends Model> List<Object> distinct(Class<T> modelClass, String... distinctField) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public <T extends Model> List<Object> distinct(Class<T> modelClass, Map<String, Object> filter,
        String... distinctField) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public <T extends Model> List<Object> distinct(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions, String... distinctField) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public <T extends Model> List<Map<String, Object>> findData(Class<T> modelClass) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public <T extends Model> List<Map<String, Object>> findData(Class<T> modelClass, Map<String, Object> filter) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public <T extends Model> List<Map<String, Object>> findData(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public <T extends Model> List<Map<String, Object>> findData(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions, DBCollection collection) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public <T extends Model> List<Map<String, Object>> findDataByIds(Class<T> modelClass, Id[] ids) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public void duplicateTable(String from, String to) throws SQLException {
        Connection conn = null;
        Statement stmt1 = null;
        Statement stmt2 = null;
        ResultSet rs = null;

        try {
            conn = connection();
            stmt1 = conn.createStatement();
            stmt2 = conn.createStatement();

            // Select from source table so that we can get some column
            // information.
            rs = stmt1.executeQuery("SELECT * FROM `" + from + "` LIMIT 0,1");
            ResultSetMetaData rsmd = rs.getMetaData();

            DatabaseMetaData dmd = conn.getMetaData();

            int columnCount = rsmd.getColumnCount();
            StringBuilder sql = new StringBuilder();

            // If columns exist, we can start building the table copy.
            if (columnCount > 0) {
                // What primary keys does the source table have?
                ResultSet pkRS = null;
                List<String> columnPKs = new ArrayList<String>();

                try {
                    pkRS = dmd.getPrimaryKeys(conn.getCatalog(), null, from);

                    while (pkRS.next()) {
                        columnPKs.add(pkRS.getString("COLUMN_NAME"));
                    }
                } catch (Throwable t) {

                } finally {
                    IO.closeQuietly(pkRS);
                }

                // Start building the table.
                sql.append("CREATE TABLE `").append(to).append("` ( ");

                // Columns
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1)
                        sql.append(",\n");

                    String columnName = rsmd.getColumnLabel(i);
                    String columnType = rsmd.getColumnTypeName(i);

                    // Find out if the column has a default value.
                    ResultSet dmdRS = null;
                    String columnDefault = null;

                    try {
                        dmdRS = dmd.getColumns(conn.getCatalog(), dmd.getUserName(), from, columnName);

                        if (dmdRS.next())
                            columnDefault = dmdRS.getString("COLUMN_DEF");
                    } catch (Throwable t) {

                    } finally {
                        IO.closeQuietly(dmdRS);
                    }

                    StringBuilder column = new StringBuilder("`").append(columnName).append("` ").append(columnType);

                    // See if a precision exists for the column.
                    int precision = rsmd.getPrecision(i);
                    if (precision != 0 && !"DATE".equals(columnType) && !"DATETIME".equals(columnType)
                        && !"TIMESTAMP".equals(columnType)) {
                        StringBuilder precisionPart = new StringBuilder("(").append(precision);

                        int scale = rsmd.getScale(i);
                        if (scale > 0)
                            precisionPart.append(',').append(scale);

                        precisionPart.append(")");

                        int signedPos = column.indexOf("SIGNED");
                        int unsignedPos = column.indexOf("UNSIGNED");

                        if (unsignedPos != -1)
                            column.insert(unsignedPos - 1, precisionPart);
                        else if (signedPos != -1)
                            column.insert(signedPos - 1, precisionPart);
                        else
                            column.append(precisionPart);
                    }

                    // Is the field a NOT NULL field?
                    if (rsmd.isNullable(i) == ResultSetMetaData.columnNoNulls)
                        column.append(" NOT NULL");

                    // Append column default value if it exists.
                    if (columnDefault != null) {
                        if ("VARCHAR".equals(columnType) || "CHAR".equals(columnType) || "TEXT".equals(columnType))
                            column.append(" DEFAULT '").append(columnDefault).append("'");
                        else
                            column.append(" DEFAULT ").append(columnDefault);
                    }

                    // Append column to create sql.
                    sql.append(column);
                }

                // If the table has a primary key, we append it at the end.
                if (columnPKs.size() > 0)
                    sql.append(",\nPRIMARY KEY (").append(Strings.toCsvString(columnPKs)).append(")");

                sql.append(")\n");

                // System.out.println(sql.toString());

                stmt2.executeUpdate(sql.toString());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            IO.closeQuietly(stmt1);
            IO.closeQuietly(stmt2);
            IO.closeQuietly(rs);
        }
    }

    @Override
    public ResultSet executeQuery(String query) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = connection();
            stmt = conn.createStatement();

            rs = stmt.executeQuery(query);

            // System.out.println(query);
        } finally {
            // IO.closeQuietly(stmt);
        }

        return rs;
    }

    @Override
    public ResultSet executePreparedQuery(String query, Object... args) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = connection();
            pstmt = conn.prepareStatement(query);

            int i = 0;

            for (Object obj : args) {
                if (obj instanceof String) {
                    pstmt.setString(++i, (String) obj);
                } else if (obj instanceof BigInteger) {
                    pstmt.setString(++i, String.valueOf(obj));
                } else {
                    pstmt.setObject(++i, obj);
                }
            }

            rs = pstmt.executeQuery();

            // System.out.println(query);
        } finally {
            // IO.closeQuietly(stmt);
        }

        return rs;
    }

    @Override
    public int executeUpdate(String query) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        int numRows = 0;

        try {
            conn = connection();
            stmt = conn.createStatement();

            numRows = stmt.executeUpdate(query);

            // System.out.println(query);
        } finally {
            IO.closeQuietly(stmt);
        }

        return numRows;
    }

    @Override
    public <T extends Model> List<Map<String, Object>> findDataByIds(Class<T> modelClass, Id[] ids,
        QueryOptions queryOptions) {
        throw new RuntimeException("Operation not supported");
    }

    protected <T extends Model> void appendSortBy(Class<T> modelClass, QueryOptions queryOptions, StringBuilder sql) {
        if (modelClass == null || queryOptions == null)
            return;

        List<ColumnInfo> columns = null;

        if ((queryOptions.sortAsc() != null && queryOptions.sortAsc().size() > 0)
            || (queryOptions.sortDesc() != null && queryOptions.sortDesc().size() > 0)) {
            columns = Annotations.getColumns(modelClass);
        }

        boolean firstField = true;

        if ((queryOptions.sortAsc() != null && queryOptions.sortAsc().size() > 0)
            || queryOptions.sortDesc() != null && queryOptions.sortDesc().size() > 0)
            sql.append(" order by ");

        // ----------------------------------------
        // Ascending sort fields
        // ----------------------------------------
        if (queryOptions.sortAsc() != null && queryOptions.sortAsc().size() > 0) {
            List<String> ascFields = queryOptions.sortAsc();

            for (String ascField : ascFields) {
                if (!firstField)
                    sql.append(", ");

                sql.append(Models.columnName(columns, ascField)).append(" asc");

                firstField = false;
            }
        }

        // ----------------------------------------
        // Descending sort fields
        // ----------------------------------------
        if (queryOptions.sortDesc() != null && queryOptions.sortDesc().size() > 0) {
            List<String> descFields = queryOptions.sortDesc();

            for (String descField : descFields) {
                if (!firstField)
                    sql.append(", ");

                sql.append(Models.columnName(columns, descField)).append(" desc");

                firstField = false;
            }
        }
    }

    protected <T extends Model> void appendLimit(Class<T> modelClass, QueryOptions queryOptions, StringBuilder sql) {
        if (modelClass == null || queryOptions == null)
            return;

        if (queryOptions.limit() != null && queryOptions.offset() != null) {
            sql.append(" limit " + queryOptions.offset() + "," + queryOptions.limit());
        }
    }

    @Override
    public void createDatabase(String name) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public <T extends Model> T findSnapshot(Class<T> modelClass, Id id, Integer version) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public <T extends Model> T findSnapshot(Class<T> modelClass, Id id, Integer version, QueryOptions queryOptions) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id, QueryOptions queryOptions) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id, Integer[] versions) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id, Integer[] versions,
        QueryOptions queryOptions) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Map<String, Object> filter) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public <T extends Model> Long snapshotCount(Class<T> modelClass, Map<String, Object> filter) {
        throw new RuntimeException("Operation not supported");
    }
}
