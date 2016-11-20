package com.geecommerce.core.elasticsearch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.core.Str;
import com.geecommerce.core.elasticsearch.api.SearchIndexSupport;
import com.geecommerce.core.elasticsearch.helper.ElasticsearchIndexHelper;
import com.geecommerce.core.service.Annotations;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;

public abstract class SearchIndexer {

    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMddHHmm");

    private static final Logger log = LogManager.getLogger(SearchIndexer.class);
    private final ElasticsearchIndexHelper elasticsearchHelper;

    protected SearchIndexer(ElasticsearchIndexHelper elasticsearchHelper) {
        this.elasticsearchHelper = elasticsearchHelper;
    }

    public abstract List<Id> getIndexedIds();

    public abstract SearchIndexSupport getIndexedItem(Id id);

    protected <T extends Model> void updateOrCreateIndex(Map<String, Object> indexerContext, Class<T> modelClass) {
        Long indexerId = (Long) indexerContext.get(GlobalColumn.ID);
        Long merchantId = (Long) indexerContext.get(GlobalColumn.MERCHANT_ID);
        Long storeId = (Long) indexerContext.get(GlobalColumn.STORE_ID);

        log.info("Using index for [indexerId=" + indexerId + ", merchantId=" + merchantId + ", storeId=" + storeId
            + "].");

        String indexName = Annotations.getCollectionName(modelClass);
        String name = Annotations.getIndexedCollectionName(modelClass);

        long start = System.currentTimeMillis();
        long start100 = System.currentTimeMillis();

        // Alias name which points to the actual index.
        String aliasName = elasticsearchHelper.indexName(merchantId.longValue(), storeId.longValue(), indexName);
        // The index itself. Every index has a unique name with a timestamp
        // suffix.
        String newIndexName = elasticsearchHelper.indexName(merchantId.longValue(), storeId.longValue(), indexName)
            + Str.UNDERSCORE + dateTimeFormat.format(new Date());

        log.info("IndexAliases (1): " + elasticsearchHelper.getIndexAliases());

        // Get products to index.
        List<Id> ids = getIndexedIds();

        log.info("Found " + (ids == null ? 0 : ids.size()) + " items to process.");

        if (ids != null && !ids.isEmpty()) {
            log.info("Creating new index: " + newIndexName);

            // First we create a new index for the current indexing process.
            elasticsearchHelper.createNewIndex(newIndexName);

            // Disable date detection for now as this causes serious problems
            // when elasticsearch gets it wrong.
            elasticsearchHelper.disableDateDetection(newIndexName, name);

            int totalCount = 0;

            for (Id id : ids) {
                try {
                    // Get product from database
                    SearchIndexSupport indexedItem = getIndexedItem(id);

                    // Add product to new index.
                    elasticsearchHelper.updateIndex(newIndexName, indexedItem);

                    // Here we flush the index periodically to free up memory.
                    if ((totalCount % 100) == 0) {
                        log.info("Total processed: " + totalCount + ", 100 products took: "
                            + (System.currentTimeMillis() - start100) + "ms.");
                        start100 = System.currentTimeMillis();

                        elasticsearchHelper.flushIndex(newIndexName);
                    }

                    totalCount++;
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    e.printStackTrace();
                }
            }

            // Final flush to make sure that all products make it to the new
            // index.
            elasticsearchHelper.flushIndex(newIndexName);

            // Before processing to the next stage, we wait for the green
            // status.
            elasticsearchHelper.waitForGreenStatus(newIndexName);

            log.info("IndexAliases (2): " + elasticsearchHelper.getIndexAliases());

            // Here we activate the new index by moving the alias-name from the
            // previous index to this one.
            elasticsearchHelper.activateIndex(newIndexName, aliasName);

            // Wait again to make sure that our final log ouput has the latest
            // data.
            elasticsearchHelper.waitForGreenStatus(newIndexName);

            Map<String, Set<String>> indexAliases = elasticsearchHelper.getIndexAliases();

            log.info("IndexAliases (3): " + indexAliases);

            // Set the date of the last update.
            elasticsearchHelper.updateLastIndexedDate(indexerContext);

            log.info("Total count: " + totalCount + " items indexed.");
            log.info("Total time: " + (((System.currentTimeMillis() - start)) / 1000 / 60) + " minutes.");

            // Clean up old indexes after completion.
            elasticsearchHelper.cleanupOldIndexes(newIndexName, indexAliases);
        }
    }
}
