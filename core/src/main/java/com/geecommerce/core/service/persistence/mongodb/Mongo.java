package com.geecommerce.core.service.persistence.mongodb;

import java.util.HashSet;
import java.util.Set;

import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.service.api.Model;
import com.mongodb.BulkWriteOperation;

public class Mongo {
    private static final String MONGO_BULK_MODE_ENABLED_KEY = "mongo.bulk.enabled";
    private static final String MONGO_BULK_FLUSH_INTERVAL_KEY = "mongo.bulk.flush.interval";

    private static final String MONGO_BULK_PROCESSING_COUNT_KEY = "mongo.bulk.processing.count";
    private static final String MONGO_BULK_AUTO_PROCESSING_COUNT_KEY = "mongo.bulk.auto.processing.count";

    private static final String MONGO_BULK_WRITE_OPERATION_CACHE_KEY = "mongo.bulk.write.operation.cache";

    private static final int MONGO_BULK_DEFAULT_FLUSH_INTERVAL = 100;

    public static final void enableBulkMode() {
        App.get().registryPut(MONGO_BULK_MODE_ENABLED_KEY, true);
    }

    public static final void disableBulkMode() {
        App.get().registryRemove(MONGO_BULK_MODE_ENABLED_KEY);
    }

    public static final boolean isBulkModeEnabled() {
        Boolean bulkModeEnabled = App.get().registryGet(MONGO_BULK_MODE_ENABLED_KEY);

        return bulkModeEnabled != null && bulkModeEnabled == true;
    }

    public static final void setBulkFlushInterval(int numDocuments) {
        App.get().registryPut(MONGO_BULK_FLUSH_INTERVAL_KEY, numDocuments);
    }

    public static final int getBulkFlushInterval() {
        Integer flushInterval = App.get().registryGet(MONGO_BULK_FLUSH_INTERVAL_KEY);

        return flushInterval == null ? MONGO_BULK_DEFAULT_FLUSH_INTERVAL : flushInterval;
    }

    public static final void enableBulkAutoProcessingCount() {
        App.get().registryPut(MONGO_BULK_AUTO_PROCESSING_COUNT_KEY, true);
    }

    public static final void disableBulkAutoProcessingCount() {
        App.get().registryRemove(MONGO_BULK_AUTO_PROCESSING_COUNT_KEY);
    }

    public static final boolean isBulkAutoProcessingCountEnabled() {
        Boolean batchAutoProcessingCountEnabled = App.get().registryGet(MONGO_BULK_AUTO_PROCESSING_COUNT_KEY);

        return batchAutoProcessingCountEnabled == null ? true : batchAutoProcessingCountEnabled;
    }

    public static final int getBulkProcessingCount() {
        Integer processingCount = App.get().registryGet(MONGO_BULK_PROCESSING_COUNT_KEY);

        return processingCount == null ? 0 : processingCount;
    }

    public static final void incrementBulkProcessingCount() {
        Integer processingCount = getBulkProcessingCount();

        if (processingCount == null)
            App.get().registryPut(MONGO_BULK_PROCESSING_COUNT_KEY, 1);

        else
            App.get().registryPut(MONGO_BULK_PROCESSING_COUNT_KEY, ++processingCount);
    }

    public static final void resetBulkProcessingCount() {
        App.get().registryPut(MONGO_BULK_PROCESSING_COUNT_KEY, 0);
    }

    public static final <T extends Model> BulkWriteOperation fetchBulkWriteOperation(Class<T> modelClass) {
        return App.get().registryGet(bulkWriteOperationKey(modelClass));
    }

    public static final <T extends Model> void stashBulkWriteOperation(Class<T> modelClass, BulkWriteOperation bulkWriteOp) {
        App.get().registryPut(bulkWriteOperationKey(modelClass), bulkWriteOp);
    }

    public static final <T extends Model> void resetBulkWriteOperation(Class<T> modelClass) {
        App.get().registryRemove(bulkWriteOperationKey(modelClass));
    }

    public static final void finalizeBulk() {
        try {
            Set<String> keySet = App.get().registryKeys();

            Set<String> keys = new HashSet<String>(keySet);

            for (String key : keys) {
                if (key.startsWith(MONGO_BULK_WRITE_OPERATION_CACHE_KEY)) {
                    BulkWriteOperation bulkWriteOp = null;

                    try {
                        bulkWriteOp = App.get().registryGet(key);

                        if (bulkWriteOp != null)
                            bulkWriteOp.execute();

                        App.get().registryRemove(key);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }

            resetBulkProcessingCount();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static final <T extends Model> String bulkWriteOperationKey(Class<T> modelClass) {
        return new StringBuilder(MONGO_BULK_WRITE_OPERATION_CACHE_KEY).append(Char.AT).append(modelClass.getName()).toString();
    }
}
