package com.geecommerce.core.elasticsearch.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequestBuilder;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequestBuilder;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.hppc.ObjectLookupContainer;
import org.elasticsearch.common.hppc.cursors.ObjectCursor;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.jsoup.Jsoup;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Char;
import com.geecommerce.core.DefaultApplicationContext;
import com.geecommerce.core.Str;
import com.geecommerce.core.app.standalone.helper.MongoHelper;
import com.geecommerce.core.config.EnvProps;
import com.geecommerce.core.elasticsearch.api.SearchIndexSupport;
import com.geecommerce.core.elasticsearch.enums.ElasticSearch;
import com.geecommerce.core.service.Annotations;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.DefaultRequestContext;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.system.service.SystemService;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.DateTimes;
import com.geecommerce.core.util.Strings;
import com.google.inject.Inject;
import com.mongodb.BasicDBObject;

@Helper
public class DefaultElasticsearchIndexHelper implements ElasticsearchIndexHelper {
    @Inject
    protected App app;

    protected static final String COLLECTION_SEARCH_INDEXER = "search_indexer";
    protected static final String SEARCH_INDEXER_COLUMN_LAST_INDEXED = "last_indexed";

    protected static final String PREFIX_ATTRIBUTE = "att_";
    protected static final String PREFIX_HAS = "has_";
    protected static final String SUFFIX_HASH = "_hash";
    protected static final String SUFFIX_RAW = "_raw";
    protected static final String SUFFIX_NORMALIZED = "_norm";
    protected static final String SUFFIX_SLUG = "_slug";
    protected static final String SUFFIX_IS_OPTION = "_is_option";

    protected static final Logger log = LogManager.getLogger(DefaultElasticsearchIndexHelper.class);

    protected final SystemService systemService;

    @Inject
    public DefaultElasticsearchIndexHelper(SystemService systemService) {
        this.systemService = systemService;
    }

    @Override
    public <T extends Model> String indexName(Id merchantId, Id storeId, Class<T> modelClass) {
        return indexName(merchantId.longValue(), storeId.longValue(), Annotations.getCollectionName(modelClass));
    }

    @Override
    public String indexName(Long merchantId, Long storeId, String name) {
        String mode = EnvProps.GET.val(EnvProps.MODE);
        return new StringBuilder(mode == null ? Str.EMPTY : mode).append(Char.UNDERSCORE).append(merchantId).append(Char.UNDERSCORE).append(storeId).append(Char.UNDERSCORE).append(name).toString();
    }

    @Override
    public Map<String, Set<String>> getIndexAliases() {
        ImmutableOpenMap<String, ImmutableOpenMap<String, AliasMetaData>> indexAliasesMap = clusterAdminClient().prepareState().execute().actionGet().getState().getMetaData().getAliases();

        Map<String, Set<String>> indexAliases = new HashMap<>();

        if (indexAliasesMap != null && !indexAliasesMap.isEmpty()) {
            ObjectLookupContainer<String> aliasNames = indexAliasesMap.keys();

            for (ObjectCursor<String> aliasName : aliasNames) {
                ImmutableOpenMap<String, AliasMetaData> indexMap = indexAliasesMap.get(aliasName.value);

                if (indexMap != null && !indexMap.isEmpty()) {
                    ObjectLookupContainer<String> indexContainer = indexMap.keys();

                    if (!indexContainer.isEmpty()) {
                        Set<String> indexNames = new HashSet<>();

                        for (ObjectCursor<String> objectCursor : indexContainer) {
                            indexNames.add(objectCursor.value);
                        }

                        indexAliases.put(aliasName.value, indexNames);
                    }
                }
            }
        }

        return indexAliases;
    }

    @Override
    public void updateIndexedItem(SearchIndexSupport indexItem) {
        updateIndexedItem(indexItem, false);
    }

    @Override
    public void updateIndexedItem(SearchIndexSupport indexedItem, boolean allIndexes) {
        if (indexedItem == null)
            return;

        ApplicationContext appCtx = app.getApplicationContext();

        List<Map<String, Object>> indexerContexts = getIndexerContexts(appCtx.getMerchant());

        for (Map<String, Object> indexerContext : indexerContexts) {
            Merchant merchant = appCtx.getMerchant();
            List<Store> stores = merchant.getStores();

            for (Store store : stores) {
                Id storeId = Id.valueOf(indexerContext.get(GlobalColumn.STORE_ID));
                Id reqCtxId = Id.valueOf(indexerContext.get(GlobalColumn.REQUEST_CONTEXT_ID));

                if (storeId.equals(store.getId())) {
                    String threadCtx = app.registryGet("thread.context");

                    if ("observer".equals(threadCtx)) {
                        RequestContext reqCtx = getRequestContext(reqCtxId, merchant, store);
                        ApplicationContext obsAppCtx = new DefaultApplicationContext(reqCtx, merchant);
                        app.setApplicationContext(obsAppCtx);
                    }

                    String idxName = indexName(merchant.getId().longValue(), storeId.longValue(), Annotations.getCollectionName(((Model) indexedItem).getClass()));

                    System.out.println("[" + Thread.currentThread().getName() + "] Updating item " + indexedItem.getId() + " in index: " + idxName);

                    updateIndex(indexerContext, indexedItem);

                    flushIndex(idxName);

                    if ("observer".equals(threadCtx)) {
                        app.setApplicationContext(appCtx);
                    }
                }
            }
        }
    }

    @Override
    public void removeIndexedItem(SearchIndexSupport indexedItem) {
        ApplicationContext appCtx = app.getApplicationContext();

        List<Map<String, Object>> indexerContexts = getIndexerContexts(appCtx.getMerchant());

        for (Map<String, Object> indexerContext : indexerContexts) {
            Merchant merchant = appCtx.getMerchant();
            List<Store> stores = merchant.getStores();

            for (Store store : stores) {
                Id storeId = Id.valueOf(indexerContext.get(GlobalColumn.STORE_ID));

                if (storeId.equals(store.getId())) {
                    removeFromIndex(indexerContext, indexedItem);

                    flushIndex(indexName(merchant.getId().longValue(), storeId.longValue(), Annotations.getCollectionName(((Model) indexedItem).getClass())));
                }
            }
        }
    }

    @Override
    public void updateIndex(Map<String, Object> indexerContext, SearchIndexSupport item) {
        if (item == null || item.getId() == null)
            return;

        Long merchantId = (Long) indexerContext.get(GlobalColumn.MERCHANT_ID);
        Long storeId = (Long) indexerContext.get(GlobalColumn.STORE_ID);

        String modelName;
        if (item instanceof Model) {
            modelName = Annotations.getCollectionName(((Model) item).getClass());
        } else {
            throw new UnsupportedOperationException("Item should implement both Model and SearchIndexSupport interfaces");
        }

        String indexName = indexName(merchantId, storeId, modelName);

        updateIndex(indexName, item);
    }

    @Override
    public void updateIndex(String indexName, SearchIndexSupport item) {
        if (item == null || item.getId() == null)
            return;

        Client client = ElasticSearch.CLIENT.get();

        Map<String, Object> jsonProduct = item.getIndexMap();

        if (log.isTraceEnabled())
            log.trace("Indexing json product: " + jsonProduct);

        if (jsonProduct == null)
            return;

        String key = Annotations.getIndexedCollectionName(((Model) item).getClass());

        IndexResponse response = client.prepareIndex(indexName, key, String.valueOf(item.getId())).setSource(jsonProduct).setOperationThreaded(false).execute().actionGet();

        String _index = response.getIndex();
        String _type = response.getType();
        String _id = response.getId();
        long _version = response.getVersion();

        if (log.isTraceEnabled())
            log.trace("Index: " + _index + ", Type: " + _type + ", Version: " + _version + ", _Id: " + _id + ", Id: " + item.getId());
    }

    @Override
    public void removeFromIndex(Map<String, Object> indexerContext, SearchIndexSupport indexedItem) {
        if (indexedItem == null || indexedItem.getId() == null)
            return;

        Long merchantId = (Long) indexerContext.get(GlobalColumn.MERCHANT_ID);
        Long storeId = (Long) indexerContext.get(GlobalColumn.STORE_ID);

        String indexName = indexName(merchantId, storeId, Annotations.getCollectionName(((Model) indexedItem).getClass()));

        removeFromIndex(indexName, indexedItem);
    }

    @Override
    public void removeFromIndex(String indexName, SearchIndexSupport indexedItem) {
        if (indexedItem == null || indexedItem.getId() == null)
            return;

        Client client = ElasticSearch.CLIENT.get();

        client.prepareDelete(indexName, Annotations.getIndexedCollectionName(((Model) indexedItem).getClass()), String.valueOf(indexedItem.getId())).execute().actionGet();
    }

    @Override
    public void createNewIndex(String indexName) {
        if (!indexExists(indexName)) {
            CreateIndexRequestBuilder indexRequestBuilder = indicesAdminClient().prepareCreate(indexName);

            // TODO: SYNONYMS need to be moved to core
            /*
             * if (SynonymsHelper.isSynonymsEnabled()) {
             * indexRequestBuilder.setSettings(SynonymsHelper.
             * createIndexSettings());
             * }
             */
            CreateIndexResponse response = indexRequestBuilder.execute().actionGet();

            log.info("Index: " + indexName + ", isAcknowledged: " + response.isAcknowledged());
        }
    }

    @Override
    public boolean indexExists(String indexName) {
        return indicesAdminClient().prepareExists(indexName).execute().actionGet().isExists();
    }

    @Override
    public String[] getAllIndexes() {
        return clusterAdminClient().prepareState().execute().actionGet().getState().getMetaData().concreteAllIndices();
    }

    /*
     * Cleans up old, inactive indexes, leaving at least 3 old ones for each
     * context.
     */
    @Override
    public void cleanupOldIndexes(String newIndexName, Map<String, Set<String>> indexAliases) {
        String[] allIndexes = getAllIndexes();

        List<String> allIndexList = Arrays.asList(allIndexes);

        if (allIndexList != null && !allIndexList.isEmpty()) {
            // Sort newest to oldest so that we can skip the newer indexes when
            // deleting.
            Collections.sort(allIndexList, (s1, s2) -> s1.compareTo(s2));

            // Make sure the newest come first.
            Collections.reverse(allIndexList);

            int keepLastNumIndexes = 3;

            // Map for context-aware counter.
            Map<String, Integer> indexCounts = new HashMap<>();

            List<String> indexesToDelete = new ArrayList<>();

            for (String indexName : allIndexList) {
                int lastIndexUnderscore = indexName.lastIndexOf(Char.UNDERSCORE);
                if (lastIndexUnderscore < 0) {
                    continue;
                }

                String aliasName = indexName.substring(0, lastIndexUnderscore); // TODO
                                                                                // ...
                Integer count = indexCounts.get(aliasName);

                if (count == null) {
                    count = 0;
                    indexCounts.put(aliasName, count);
                }

                if (count > keepLastNumIndexes && !isActive(indexName, indexAliases) && !indexName.equals(newIndexName)) {
                    log.info("Deleting inactive index " + indexName);

                    indexesToDelete.add(indexName);
                }

                indexCounts.put(aliasName, ++count);
            }

            if (!indexesToDelete.isEmpty()) {
                log.info("Deleting indexes: " + indexesToDelete);

                DeleteIndexRequestBuilder deleteIndexRequest = new DeleteIndexRequestBuilder(indicesAdminClient(), indexesToDelete.toArray(new String[indexesToDelete.size()]));
                DeleteIndexResponse deleteResponse = deleteIndexRequest.execute().actionGet();

                log.info("DeleteIndexResponse isAcknowledged: " + deleteResponse.isAcknowledged());
            }
        }
    }

    @Override
    public void createIndexAlias(String indexName, String aliasName) {
        IndicesAliasesResponse response = indicesAdminClient().prepareAliases().addAlias(indexName, aliasName).execute().actionGet();

        log.info("Index: " + indexName + ", isAcknowledged: " + response.isAcknowledged());
    }

    @Override
    public void removeIndexAlias(String indexName, String aliasName) {
        IndicesAliasesResponse response = indicesAdminClient().prepareAliases().removeAlias(indexName, aliasName).execute().actionGet();

        log.info("Index: " + indexName + ", isAcknowledged: " + response.isAcknowledged());
    }

    /**
     * Activates an index by moving the alias from the old index to the new one.
     */
    @Override
    public void activateIndex(String indexName, String aliasName) {
        Map<String, Set<String>> indexAliasMap = getIndexAliases();

        IndicesAliasesRequestBuilder builder = indicesAdminClient().prepareAliases();

        if (indexAliasMap.size() > 0) {
            Set<String> aliasNames = indexAliasMap.keySet();

            for (String an : aliasNames) {
                Set<String> indexNames = indexAliasMap.get(an);

                for (String in : indexNames) {
                    if (an.equals(aliasName)) {
                        log.info("Removing alias '" + an + "' from index '" + in + "'.");

                        builder.removeAlias(in, an);
                    }
                }
            }
        }

        IndicesAliasesResponse response = builder.addAlias(indexName, aliasName).execute().actionGet();

        log.info("activateIndex: [indexName=" + indexName + ", aliasName=" + aliasName + ", isAcknowledged=" + response.isAcknowledged() + "].");
    }

    /**
     * An index is considered to be active if it is connected to an alias.
     *
     * @return
     */
    protected boolean isActive(String indexName, Map<String, Set<String>> indexAliases) {
        if (indexName == null || indexAliases == null)
            return false;

        Set<String> aliasNames = indexAliases.keySet();

        for (String aliasName : aliasNames) {
            Set<String> indexNames = indexAliases.get(aliasName);

            // Index has an alias pointing to it.
            if (indexNames.contains(indexName))
                return true;
        }

        return false;
    }

    @Override
    public void waitForGreenStatus(String indexName) {
        waitForStatus(indexName, "GREEN");
    }

    @Override
    public void flushIndex(String indexName) {
        indicesAdminClient().flush(new FlushRequest(indexName)).actionGet();
    }

    @Override
    public void waitForStatus(String indexName, String waitingStatusName) {
        ClusterHealthStatus waitingStatus = ClusterHealthStatus.valueOf(waitingStatusName);

        Client client = ElasticSearch.CLIENT.get();

        if (log.isDebugEnabled()) {
            log.debug("Start waiting for " + waitingStatusName + "  status for index " + indexName);
        }

        ClusterHealthStatus status = ClusterHealthStatus.RED;

        int counter = 0;
        int maxLoops = 20;
        int waitBetweenLoops = 2000;

        while (status != waitingStatus) {
            ClusterHealthRequestBuilder healthRequest = client.admin().cluster().prepareHealth();
            healthRequest.setIndices(indexName);
            healthRequest.setWaitForGreenStatus();
            ClusterHealthResponse healthResponse = healthRequest.execute().actionGet();

            status = healthResponse.getStatus();

            if (log.isDebugEnabled()) {
                log.debug("Waiting for " + waitingStatus + " status (#" + counter + ") " + status);
            }

            try {
                Thread.sleep(waitBetweenLoops);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (counter > maxLoops) {
                break;
            }
            counter++;
        }

        if (log.isDebugEnabled()) {
            log.debug("Done waiting for " + waitingStatus + " status for index " + indexName);
        }
    }

    @Override
    public void updateLastIndexedDate(Map<String, Object> updateData) {
        Long indexerId = (Long) updateData.get(GlobalColumn.ID);
        updateData.put(SEARCH_INDEXER_COLUMN_LAST_INDEXED, DateTimes.newDate());

        MongoHelper.update(MongoHelper.mongoSystemDB(), COLLECTION_SEARCH_INDEXER, new BasicDBObject(GlobalColumn.ID, Id.valueOf(indexerId)), new BasicDBObject(updateData));
    }

    @Override
    public void disableDateDetection(String indexName, String name) {
        XContentBuilder mapping;

        try {
            mapping = XContentFactory.jsonBuilder().startObject().startObject(name).field("date_detection", false).endObject().endObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        indicesAdminClient().preparePutMapping(indexName).setType(name).setSource(mapping).execute().actionGet();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void addAttribute(Map<String, Object> json, Attribute attribute, AttributeValue attributeValue) {
        if (attribute == null || attributeValue == null || attribute.getCode() == null)
            return;

        StringBuilder attributeKey = new StringBuilder(PREFIX_ATTRIBUTE).append(Strings.slugify(attribute.getCode()).replaceAll("\\-", "_"));
        StringBuilder isOptionKey = new StringBuilder(attributeKey).append(SUFFIX_IS_OPTION);

        // -------------------------------------------------------------
        // Add options
        // -------------------------------------------------------------
        if (attributeValue.getOptionId() != null && attributeValue.getOptionIds().size() > 0) {
            addAttributeOptions(json, attribute, attributeValue);
        }
        // -------------------------------------------------------------
        // Add attribute value (none-option)
        // -------------------------------------------------------------
        else if (attributeValue.getValue() != null && attributeValue.getValue().size() > 0) {
            ContextObject<?> co = attributeValue.getValue();

            if (co == null)
                return;

            for (Map<String, Object> map : co) {
                Object value = map.get(ContextObject.VALUE);

                if (value == null)
                    continue;

                String languageCode = (String) map.get(ContextObject.LANGUAGE);
                String countryCode = (String) map.get(ContextObject.COUNTRY);

                StringBuilder hashKey = new StringBuilder(attributeKey).append(SUFFIX_HASH);
                StringBuilder slugKey = new StringBuilder(attributeKey).append(SUFFIX_SLUG);
                StringBuilder rawKey = new StringBuilder(attributeKey).append(SUFFIX_RAW);
                StringBuilder normalizedKey = new StringBuilder(attributeKey).append(SUFFIX_NORMALIZED);

                if (languageCode != null && !Str.EMPTY.equals(languageCode)) {
                    rawKey.append(Char.UNDERSCORE).append(languageCode);
                    slugKey.append(Char.UNDERSCORE).append(languageCode);
                    normalizedKey.append(Char.UNDERSCORE).append(languageCode);
                }

                if (countryCode != null && !Str.EMPTY.equals(countryCode)) {
                    rawKey.append(Char.UNDERSCORE).append(countryCode);
                    slugKey.append(Char.UNDERSCORE).append(countryCode);
                    normalizedKey.append(Char.UNDERSCORE).append(countryCode);
                }

                if (value instanceof String) {
                    json.put(rawKey.toString(), Jsoup.parse((String) value).text());
                    json.put(normalizedKey.toString(), Strings.transliterate(Jsoup.parse((String) value).text()));
                    json.put(slugKey.toString(), new StringBuilder(Str.UNDERSCORE_2X).append(Strings.slugify(Jsoup.parse((String) value).text()).replaceAll(Str.MINUS_ESCAPED, Str.UNDERSCORE))
                        .append(Str.UNDERSCORE_2X).toString());
                } else if (value instanceof List) {
                    List<Object> valueList = (List) value;

                    // Create list of slugs
                    List<String> slugs = new ArrayList<String>();

                    for (Object obj : valueList) {
                        if (obj instanceof String) {
                            slugs.add(new StringBuilder(Str.UNDERSCORE_2X).append(Strings.slugify(Jsoup.parse((String) obj).text()).replaceAll(Str.MINUS_ESCAPED, Str.UNDERSCORE))
                                .append(Str.UNDERSCORE_2X).toString());
                        } else {
                            slugs.add(
                                new StringBuilder(Str.UNDERSCORE_2X).append(Strings.slugify(String.valueOf(obj)).replaceAll(Str.MINUS_ESCAPED, Str.UNDERSCORE)).append(Str.UNDERSCORE_2X).toString());
                        }
                    }

                    json.put(slugKey.toString(), slugs);
                } else {
                    json.put(rawKey.toString(), value);
                    json.put(slugKey.toString(),
                        new StringBuilder(Str.UNDERSCORE_2X).append(Strings.slugify(String.valueOf(value)).replaceAll(Str.MINUS_ESCAPED, Str.UNDERSCORE)).append(Str.UNDERSCORE_2X).toString());
                }

                // Also store hash of value in index
                json.put(hashKey.toString(), new StringBuilder(Str.UNDERSCORE_2X).append(Strings.hash(String.valueOf(value))).append(Str.UNDERSCORE_2X).toString().replace(Str.MINUS, Str.UNDERSCORE));
                json.put(isOptionKey.toString(), false);
            }
        }
    }

    public void addAttributeOptions(Map<String, Object> json, Attribute attribute, AttributeValue attributeValue) {
        StringBuilder attributeKey = new StringBuilder(PREFIX_ATTRIBUTE).append(Strings.slugify(attribute.getCode()).replaceAll(Str.MINUS_ESCAPED, Str.UNDERSCORE));

        StringBuilder hashKey = new StringBuilder(attributeKey).append(SUFFIX_HASH);
        StringBuilder isOptionKey = new StringBuilder(attributeKey).append(SUFFIX_IS_OPTION);

        List<String> newOptionIds = new ArrayList<>(); // for options, we just
                                                       // use the option id for
                                                       // the hash
        Map<String, List<String>> newOptionSlugs = new LinkedHashMap<>();
        Map<String, List<Object>> newOptionRawValues = new LinkedHashMap<>();
        Map<String, List<Object>> newOptionNormalizedValues = new LinkedHashMap<>();

        Map<Id, AttributeOption> attributeOptions = attributeValue.getAttributeOptions();

        for (Id optionId : attributeValue.getOptionIds()) {
            if (attributeOptions == null || attributeOptions.size() == 0)
                continue;

            AttributeOption option = attributeOptions.get(optionId);

            if (option == null)
                continue;

            newOptionIds.add(new StringBuilder(Str.UNDERSCORE_2X).append(optionId.longValue()).append(Str.UNDERSCORE_2X).toString());

            ContextObject<String> co = option.getLabel();

            if (co == null)
                continue;

            for (Map<String, Object> map : co) {
                Object value = (String) map.get(ContextObject.VALUE);

                if (value == null)
                    continue;

                String languageCode = (String) map.get(ContextObject.LANGUAGE);
                String countryCode = (String) map.get(ContextObject.COUNTRY);

                StringBuilder slugKey = new StringBuilder(attributeKey).append(SUFFIX_SLUG);
                StringBuilder rawKey = new StringBuilder(attributeKey).append(SUFFIX_RAW);
                StringBuilder normalizedKey = new StringBuilder(attributeKey).append(SUFFIX_NORMALIZED);

                if (languageCode != null && !Str.EMPTY.equals(languageCode)) {
                    slugKey.append(Char.UNDERSCORE).append(languageCode);
                    rawKey.append(Char.UNDERSCORE).append(languageCode);
                    normalizedKey.append(Char.UNDERSCORE).append(languageCode);
                }

                if (countryCode != null && !Str.EMPTY.equals(countryCode)) {
                    slugKey.append(Char.UNDERSCORE).append(countryCode);
                    rawKey.append(Char.UNDERSCORE).append(countryCode);
                    normalizedKey.append(Char.UNDERSCORE).append(countryCode);
                }

                List<String> slugValues = newOptionSlugs.get(slugKey.toString());
                if (slugValues == null) {
                    slugValues = new ArrayList<>();
                    newOptionSlugs.put(slugKey.toString(), slugValues);
                }

                List<Object> rawValues = newOptionRawValues.get(rawKey.toString());
                if (rawValues == null) {
                    rawValues = new ArrayList<>();
                    newOptionRawValues.put(rawKey.toString(), rawValues);
                }

                List<Object> normalizedValues = newOptionNormalizedValues.get(normalizedKey.toString());
                if (normalizedValues == null) {
                    normalizedValues = new ArrayList<>();
                    newOptionNormalizedValues.put(normalizedKey.toString(), normalizedValues);
                }

                if (value instanceof String) {
                    slugValues.add(new StringBuilder(Str.UNDERSCORE_2X).append(Strings.slugify(Jsoup.parse((String) value).text()).replaceAll(Str.MINUS_ESCAPED, Str.UNDERSCORE))
                        .append(Str.UNDERSCORE_2X).toString());
                    rawValues.add(Jsoup.parse((String) value).text());
                    normalizedValues.add(Strings.transliterate(Jsoup.parse((String) value).text()));
                } else {
                    slugValues
                        .add(new StringBuilder(Str.UNDERSCORE_2X).append(Strings.slugify(String.valueOf(value)).replaceAll(Str.MINUS_ESCAPED, Str.UNDERSCORE)).append(Str.UNDERSCORE_2X).toString());
                    rawValues.add(value);
                }
            }

            Set<String> slugKeys = newOptionSlugs.keySet();
            for (String slugKey : slugKeys) {
                json.put(slugKey.toString(), newOptionSlugs.get(slugKey.toString()));
            }

            Set<String> rawKeys = newOptionRawValues.keySet();
            for (String rawKey : rawKeys) {
                json.put(rawKey.toString(), newOptionRawValues.get(rawKey.toString()));
            }

            Set<String> normalizedKeys = newOptionNormalizedValues.keySet();
            for (String normalizedKey : normalizedKeys) {
                json.put(normalizedKey.toString(), newOptionNormalizedValues.get(normalizedKey.toString()));
            }

            json.put(hashKey.toString(), newOptionIds);
            json.put(isOptionKey.toString(), true);
        }
    }

    protected List<Map<String, Object>> getIndexerContexts(Merchant m) {
        Map<String, Object> query = new HashMap<>();
        query.put(GlobalColumn.ENABLED, Boolean.TRUE);
        query.put(GlobalColumn.MERCHANT_ID, m.getId());

        return MongoHelper.find(MongoHelper.mongoSystemDB(), COLLECTION_SEARCH_INDEXER, query, null);
    }

    protected RequestContext getRequestContext(Id requestContextId, Merchant m, Store s) {
        RequestContext reqCtx = null;

        if (requestContextId != null) {
            reqCtx = systemService.getRequestContext(requestContextId);
        } else {
            try {
                reqCtx = systemService.findRequestContext(m, s, (s == null ? null : s.getDefaultLanguage()), null, null);

                if (reqCtx == null)
                    reqCtx = systemService.findRequestContext(m, s, null, null, null);
            } catch (IllegalStateException e) {
                reqCtx = new DefaultRequestContext(null, m.getId(), s.getId(), null, null, null);
            }
        }

        return reqCtx;
    }

    protected ClusterAdminClient clusterAdminClient() {
        Client client = ElasticSearch.CLIENT.get();
        return client.admin().cluster();
    }

    protected IndicesAdminClient indicesAdminClient() {
        Client client = ElasticSearch.CLIENT.get();
        return client.admin().indices();
    }
}
