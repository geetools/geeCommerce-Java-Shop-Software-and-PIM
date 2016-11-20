package com.geecommerce.core.service.persistence.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.Set;

import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.db.Connections;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.util.IO;

public class JDBC {
    private static final String JDBC_BATCH_MODE_ENABLED_KEY = "jdbc.batch.enabled";
    private static final String JDBC_BATCH_FLUSH_INTERVAL_KEY = "jdbc.batch.flush.interval";
    private static final String JDBC_BATCH_MODE_COMMIT_ON_FLUSH_KEY = "jdbc.batch.commit.on.flush";

    private static final String JDBC_BATCH_PROCESSING_COUNT_KEY = "jdbc.batch.processing.count";
    private static final String JDBC_BATCH_AUTO_PROCESSING_COUNT_KEY = "jdbc.batch.auto.processing.count";

    private static final String JDBC_BATCH_PREPARED_STATEMENT_CACHE_KEY = "jdbc.batch.prepared.statement.cache";
    private static final String JDBC_BATCH_PREPARED_STATEMENT_RENEW_ON_FLUSH = "jdbc.batch.prepared.statement.renew.on.flush";

    private static final int JDBC_BATCH_DEFAULT_FLUSH_INTERVAL = 100;

    public static final void enableBatchMode() {
        App.get().registryPut(JDBC_BATCH_MODE_ENABLED_KEY, true);
    }

    public static final void disableBatchMode() {
        App.get().registryRemove(JDBC_BATCH_MODE_ENABLED_KEY);
    }

    public static final boolean isBatchModeEnabled() {
        Boolean batchModeEnabled = App.get().registryGet(JDBC_BATCH_MODE_ENABLED_KEY);

        return batchModeEnabled != null && batchModeEnabled == true;
    }

    public static final void enableBatchCommitOnFlush() {
        App.get().registryPut(JDBC_BATCH_MODE_COMMIT_ON_FLUSH_KEY, true);
    }

    public static final void disableBatchCommitOnFlush() {
        App.get().registryRemove(JDBC_BATCH_MODE_COMMIT_ON_FLUSH_KEY);
    }

    public static final boolean isBatchCommitOnFlushEnabled() {
        Boolean batchCommitOnFlushEnabled = App.get().registryGet(JDBC_BATCH_MODE_COMMIT_ON_FLUSH_KEY);

        return batchCommitOnFlushEnabled == null ? true : batchCommitOnFlushEnabled;
    }

    public static final void setBatchFlushInterval(int numRows) {
        App.get().registryPut(JDBC_BATCH_FLUSH_INTERVAL_KEY, numRows);
    }

    public static final int getBatchFlushInterval() {
        Integer flushInterval = App.get().registryGet(JDBC_BATCH_FLUSH_INTERVAL_KEY);

        return flushInterval == null ? JDBC_BATCH_DEFAULT_FLUSH_INTERVAL : flushInterval;
    }

    public static final void enableBatchAutoProcessingCount() {
        App.get().registryPut(JDBC_BATCH_AUTO_PROCESSING_COUNT_KEY, true);
    }

    public static final void disableBatchAutoProcessingCount() {
        App.get().registryRemove(JDBC_BATCH_AUTO_PROCESSING_COUNT_KEY);
    }

    public static final boolean isBatchAutoProcessingCountEnabled() {
        Boolean batchAutoProcessingCountEnabled = App.get().registryGet(JDBC_BATCH_AUTO_PROCESSING_COUNT_KEY);

        return batchAutoProcessingCountEnabled == null ? true : batchAutoProcessingCountEnabled;
    }

    public static final int getBatchProcessingCount() {
        Integer processingCount = App.get().registryGet(JDBC_BATCH_PROCESSING_COUNT_KEY);

        return processingCount == null ? 0 : processingCount;
    }

    public static final void incrementBatchProcessingCount() {
        Integer processingCount = getBatchProcessingCount();

        if (processingCount == null)
            App.get().registryPut(JDBC_BATCH_PROCESSING_COUNT_KEY, 1);

        else
            App.get().registryPut(JDBC_BATCH_PROCESSING_COUNT_KEY, ++processingCount);
    }

    public static final void resetBatchProcessingCount() {
        App.get().registryPut(JDBC_BATCH_PROCESSING_COUNT_KEY, 0);
    }

    public static final <T extends Model> PreparedStatement fetchPreparedStatement(Class<T> modelClass, String query) {
        return App.get().registryGet(preparedStatementKey(modelClass, query));
    }

    public static final <T extends Model> void stashPreparedStatement(Class<T> modelClass, String query,
        PreparedStatement pstmt) {
        App.get().registryPut(preparedStatementKey(modelClass, query), pstmt);
    }

    public static final <T extends Model> void resetPreparedStatement(Class<T> modelClass, String query) {
        App.get().registryRemove(preparedStatementKey(modelClass, query));
    }

    public static final void enableBatchRenewPreparedStatementOnFlush() {
        App.get().registryPut(JDBC_BATCH_PREPARED_STATEMENT_RENEW_ON_FLUSH, true);
    }

    public static final void disableBatchRenewPreparedStatementOnFlush() {
        App.get().registryRemove(JDBC_BATCH_PREPARED_STATEMENT_RENEW_ON_FLUSH);
    }

    public static final boolean isBatchRenewPreparedStatementOnFlushEnabled() {
        Boolean batchRenewPreparedStatementOnFlushEnabled = App.get()
            .registryGet(JDBC_BATCH_PREPARED_STATEMENT_RENEW_ON_FLUSH);

        return batchRenewPreparedStatementOnFlushEnabled == null ? false : batchRenewPreparedStatementOnFlushEnabled;
    }

    public static final void finalizeBatch() {
        Connection conn = null;

        try {
            Connections connections = App.get().inject(Connections.class);
            conn = connections.getSqlConnection();

            Set<String> keySet = App.get().registryKeys();

            Set<String> keys = new HashSet<String>(keySet);

            for (String key : keys) {
                if (key.startsWith(JDBC_BATCH_PREPARED_STATEMENT_CACHE_KEY)) {
                    PreparedStatement pstmt = null;

                    try {
                        pstmt = App.get().registryGet(key);

                        if (pstmt != null)
                            pstmt.executeBatch();

                        App.get().registryRemove(key);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    } finally {
                        IO.closeQuietly(pstmt);
                    }
                }
            }

            resetBatchProcessingCount();

            if (JDBC.isBatchCommitOnFlushEnabled()) {
                if (!conn.getAutoCommit()) {
                    conn.commit();
                    conn.setAutoCommit(true);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();

            try {
                conn.rollback();
            } catch (Throwable t2) {

            }

            try {
                conn.setAutoCommit(true);
            } catch (Throwable t2) {

            }
        }
    }

    private static final <T extends Model> String preparedStatementKey(Class<T> modelClass, String query) {
        return new StringBuilder(JDBC_BATCH_PREPARED_STATEMENT_CACHE_KEY).append(Char.AT).append(modelClass.getName())
            .append(Char.DOT).append(query.hashCode()).toString();
    }
}
