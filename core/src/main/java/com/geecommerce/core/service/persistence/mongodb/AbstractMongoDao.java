package com.geecommerce.core.service.persistence.mongodb;

import java.lang.invoke.MethodHandle;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.OptimisticLockException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.BSON;
import org.bson.Transformer;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.json.simple.JSONObject;

import com.geecommerce.core.Str;
import com.geecommerce.core.cache.Cache;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.db.Connections;
import com.geecommerce.core.event.Event;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.AbstractDao;
import com.geecommerce.core.service.Annotations;
import com.geecommerce.core.service.AttributeSupport.AttributeSupportColumn;
import com.geecommerce.core.service.ColumnInfo;
import com.geecommerce.core.service.DaoException;
import com.geecommerce.core.service.EmptyFilter;
import com.geecommerce.core.service.Models;
import com.geecommerce.core.service.QueryMetadata;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.api.ModelEnum;
import com.geecommerce.core.service.api.MultiContextModel;
import com.geecommerce.core.service.exception.IllegalMultiContextException;
import com.geecommerce.core.service.mongodb.cmd.Command;
import com.geecommerce.core.service.mongodb.cmd.CommandFactory;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.system.model.ContextNode;
import com.geecommerce.core.system.model.ContextTree;
import com.geecommerce.core.system.user.pojo.ClientSession;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.type.TypeConverter;
import com.geecommerce.core.type.Versionable;
import com.geecommerce.core.util.DateTimes;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteRequestBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryOperators;
import com.mongodb.WriteResult;

public abstract class AbstractMongoDao extends AbstractDao implements MongoDao {
    private static final Logger log = LogManager.getLogger(DefaultMongoDao.class);

    private static final Map<String, Object> EMPTY_FILTER = new HashMap<String, Object>();

    private static final String FIELD_ID = "id";
    private static final String FIELD_CREATED_ON = "createdOn";

    private static final String IS_PARTIAL_OBJECT_MARKER_FIELD = "__cbIsPartialObject";

    private static final String CONF_KEY_HISTORY_ENABLED = "db/history/enabled";
    private static final String CONF_KEY_HISTORY_ENABLED_MODEL_PREFIX = "db/model/";
    private static final String CONF_KEY_HISTORY_ENABLED_MODEL_SUFFIX = "/history/enabled";

    static {
        BSON.addEncodingHook(Id.class, new Transformer() {
            public Object transform(Object o) {
                if (o instanceof Id) {
                    return ((Id) o).longValue();
                }

                return o;
            }
        });

        BSON.addEncodingHook(ModelEnum.class, new Transformer() {
            public Object transform(Object o) {
                if (o instanceof ModelEnum) {
                    return ((ModelEnum) o).toId();
                }

                return o;
            }
        });

        BSON.addEncodingHook(BigDecimal.class, new Transformer() {
            public Object transform(Object o) {
                if (o instanceof BigDecimal) {
                    return ((BigDecimal) o).doubleValue();
                }

                return o;
            }
        });
    }

    public AbstractMongoDao(Connections connections, CacheManager cacheManager) {
        super(connections, cacheManager);
    }

    protected DB db(Class<? extends Model> modelClass) {
        DB db = (DB) connections.getConnection(modelClass);

        return (DB) connections.getConnection(modelClass);
    }

    protected <T extends Model> DBCollection collection(Class<T> modelClass, boolean history) {
        String colName = getCollectionName(modelClass);

        if (history)
            colName = colName + "_history";

        if (colName == null) {
            Model m = app.model(modelClass);

            MethodHandle mh = Reflect.getMethodHandle(modelClass, "__name", true);

            try {
                colName = (String) mh.invokeExact(m);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        if (Str.isEmpty(colName))
            throw new IllegalStateException("The model class '" + modelClass.getName()
                + "' must specify the collection name in the @Model annotation or in the __name() method.");

        return db(modelClass).getCollection(colName);
    }

    protected <T extends Model> DBCollection collection(Class<T> modelClass) {
        return collection(modelClass, false);
    }

    protected abstract <T extends Model> String getCollectionName(Class<T> modelClass);

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
        boolean isFromCacheOnly = isFromCacheOnly(queryOptions);
        boolean isPreloadableModel = isPreloadableModel(modelClass);

        // If object exists and is cacheable, just return that.
        if ((isCacheable && !isRefresh && !isPreview()) || isFromCacheOnly || isPreloadableModel) {
            // We want to also return null values if the key exists to avoid
            // unnecessary queries being made.
            if (modelCacheHasKeyFor(modelClass, id, queryOptions)) {
                return cacheGet(modelClass, id, queryOptions);
            }
        }
        
        if (isCacheable && isRefresh) {
            invalidateModelCache(modelClass);
            invalidateQueryCaches(id);
        }

        if (isCacheable && isFromCacheOnly)
            return modelObject;

        DBObject dbObject = new BasicDBObject();
        dbObject.put(GlobalColumn.ID, id);

        boolean isReadCounterEnabled = Annotations.isReadCounterEnabled(modelClass);

        DBObject fields = buildFieldList(modelClass, queryOptions);

        DBObject doc = null;

        if (isReadCounterEnabled) {
            DBObject readCountUpdate = new BasicDBObject("$inc", new BasicDBObject(GlobalColumn.READ_COUNT, 1));
            readCountUpdate.put("$set", new BasicDBObject(GlobalColumn.READ_COUNT_DATE, DateTimes.newDate()));
            doc = collection(modelClass).findAndModify(dbObject, fields, null, false, readCountUpdate, false, false);
        } else {
            doc = collection(modelClass).findOne(dbObject, fields);
        }

        if (doc != null) {
            try {
                Map<String, Object> map = new LinkedHashMap<>();
                convertToMongoDBValues(doc, map);

                modelObject = app.model(modelClass);

                notify(modelObject, Event.BEFORE_POPULATE);

                Models.populate(modelClass, modelObject, map);
                modelObject.fromMap(map);

                notify(modelObject, Event.AFTER_POPULATE);

                if (isCacheable && !isPreview() && modelObject != null && modelObject.getId() != null) {
                    cachePut(modelClass, modelObject, queryOptions);
                }
            } catch (Throwable t) {
                throw new DaoException(t);
            }
        }
        // If doc is null and the model class has been marked as cacheable, we
        // cache anyway to avoid
        // unnecessary queries if this object is requested again at a later
        // stage.
        else if (isCacheable && !isPreview()) {
            cachePutNull(modelClass, id, null);
        }

        return modelObject;
    }

    @Override
    public <T extends Model> List<T> findAll(Class<T> modelClass) {
        return findAll(modelClass, null);
    }

    @Override
    public <T extends Model> List<T> findAll(Class<T> modelClass, QueryOptions queryOptions) {
        return find(modelClass, EMPTY_FILTER, queryOptions);
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
            DBObject dbObject = buildQueryFilter(modelClass, filter, queryOptions);

            System.out.println("COUNT ::::: " + dbObject);

            DBCollection col = collection(modelClass);

            count = col.count(dbObject);

            System.out.println("COUNT ::::: " + count);
        }

        if (count != null) {
            app.setQueryMetadata(QueryMetadata.builder().count(count).build());
        }

        return count;
    }

    @Override
    public <T extends Model> List<T> find(Class<T> modelClass, Map<String, Object> filter) {
        return find(modelClass, filter, null);
    }

    @Override
    public <T extends Model> List<T> find(Class<T> modelClass, Map<String, Object> filter, QueryOptions queryOptions) {
        if (modelClass == null)
            throw new DaoException("Model class cannot be empty");

        if (filter == null)
            filter = EMPTY_FILTER;

        List<T> docs = new ArrayList<>();

        // This query seems to be of type findById(), so lets just call that to
        // avoid the extra processing.
        if (filter.size() == 1 && filter.containsKey(GlobalColumn.ID) && filter.values().toArray()[0] instanceof Id) {
            T modelObject = findById(modelClass, (Id) filter.values().toArray()[0]);

            if (modelObject != null)
                docs.add(modelObject);

            return docs;
        }

        Long count = null;

        if ((queryOptions != null && queryOptions.isProvideCount())) {
            count = count(modelClass, filter);
        }

        boolean isCacheable = isCacheable(modelClass, queryOptions);
        boolean isRefresh = isRefresh(queryOptions);
        boolean isFromCacheOnly = isFromCacheOnly(queryOptions);

        // If object exists and is cacheable, just return that.
        if ((isCacheable && !isRefresh && !isPreview()) || isFromCacheOnly) {
            // We want to also return null values if the key exists to avoid
            // unnecessary queries being made.
            if (queryCacheHasKeyFor(modelClass, filter, queryOptions)) {
                List<Id> cachedIds = cacheGet(modelClass, filter, queryOptions);

                if (cachedIds != null && cachedIds.size() > 0) {
                    for (Id id : cachedIds) {
                        T modelObject = findById(modelClass, id, queryOptions);

                        if (modelObject != null)
                            docs.add(modelObject);
                    }
                }

                return docs;
            }
        }

        if (isCacheable && isFromCacheOnly)
            return docs;

        DBObject dbObject = buildQueryFilter(modelClass, filter, queryOptions);

        if (modelClass.getName().contains("AttributeInputCondition"))
            System.out.println(dbObject);

        DBObject fields = buildFieldList(modelClass, queryOptions);
        // System.out.println("fields: " + fields);

        DBObject sortBy = buildSortBy(modelClass, queryOptions);
        // System.out.println("sort: " + sortBy);

        DBCollection col = collection(modelClass);

        // System.out.println("filter: " + dbObject + " -> " + sortBy);

        // ------------------------------------------------------------------
        // Query with filter
        // ------------------------------------------------------------------

        DBCursor cursor = null;

        // If we have a count, we might as well use it to determine whether it
        // is worth doing a query or not.
        if (count == null || count > 0) {
            try {
                cursor = col.find(dbObject, fields);

                cursor.sort(sortBy);

                if (queryOptions != null && queryOptions.limit() != null) {
                    if (queryOptions.limit() != -1)
                        cursor.limit(queryOptions.limit());
                } else {
                    if (queryOptions != null && queryOptions.isFetchIdsOnly()) {
                        // We allow more results when only fetching ids.
                        cursor.limit(100000);
                    } else {
                        // There is no reason to ever return more than 1500
                        // results. // TODO: make configurable
                        cursor.limit(1500);
                    }
                }

                if (queryOptions != null && queryOptions.offset() != null) {
                    cursor.skip(queryOptions.offset().intValue());
                }

                // ------------------------------------------------------------------
                // Iterate through results and add to results list docs
                // ------------------------------------------------------------------
                boolean isReadCounterEnabled = Annotations.isReadCounterEnabled(modelClass);

                List<Id> cachedIds = new ArrayList<>();
                Map<Id, List<Id>> cachedIdsMap = null;

                while (cursor.hasNext()) {
                    DBObject doc = cursor.next();

                    T modelObject = null;

                    if (doc != null) {
                        /* Increment read counter if is is enabled */
                        if (isReadCounterEnabled) {
                            updateReadCounter(col, doc.get(GlobalColumn.ID));
                        }

                        try {
                            Map<String, Object> map = new LinkedHashMap<>();

                            // AS we cannot filter attributes out at query
                            // stage, we at least try to
                            // remove unwanted ones as soon as possible, so that
                            // there is no need
                            // to process them in the upcoming steps.
                            filterAttributes(doc, queryOptions);

                            convertToMongoDBValues(doc, map);

                            modelObject = app.inject(modelClass);

                            notify(modelObject, Event.BEFORE_POPULATE);

                            // long start = System.currentTimeMillis();

                            Models.populate(modelClass, modelObject, map);

                            // System.out.println("POPULATE-111
                            // ----------------------> " +
                            // (System.currentTimeMillis()-start));

                            modelObject.fromMap(map);

                            // System.out.println("POPULATE-222
                            // ----------------------> " +
                            // (System.currentTimeMillis()-start));

                            notify(modelObject, Event.AFTER_POPULATE);

                            docs.add(modelObject);

                            if (isCacheable && !isPreview() && modelObject != null && modelObject.getId() != null) {
                                cachedIds.add(modelObject.getId());

                                if (!modelCacheHasKeyFor(modelClass, modelObject.getId(), queryOptions))
                                    cachePut(modelClass, modelObject, queryOptions);

                                // If we want to cache for a particular single
                                // query, like for a product,
                                // we collect all the ids for now and map them
                                // to each product.
                                if (queryOptions != null && !Str.isEmpty(queryOptions.singleQueryField())) {
                                    // Id that will be used in the single query
                                    // version of this query.
                                    Id singleQueryId = Id.valueOf(doc.get(queryOptions.singleQueryField()));

                                    if (singleQueryId != null) {
                                        if (cachedIdsMap == null)
                                            cachedIdsMap = new HashMap<>();

                                        // Ids collected for this single query
                                        // id.
                                        List<Id> singleQueryIds = cachedIdsMap.get(singleQueryId);

                                        if (singleQueryIds == null) {
                                            singleQueryIds = new ArrayList<>();
                                            cachedIdsMap.put(singleQueryId, singleQueryIds);
                                        }

                                        singleQueryIds.add(modelObject.getId());
                                    }
                                }
                            }

                            addPartialObjectMarker(modelClass, modelObject, queryOptions);
                        } catch (Throwable t) {
                            t.printStackTrace();
                            throw new DaoException(t);
                        }
                    }
                }

                // Also cache if the list is empty to avoid unnecessary queries
                // being made over and over again.
                if (isCacheable && !isPreview()) {
                    cachePut(modelClass, filter, queryOptions, cachedIds);

                    if (cachedIdsMap != null && cachedIdsMap.size() > 0) {
                        Set<Id> keys = cachedIdsMap.keySet();

                        for (Id singleQueryId : keys) {
                            List<Id> singleQueryCacheIds = cachedIdsMap.get(singleQueryId);

                            Map<String, Object> singleQueryFiler = new LinkedHashMap<>(filter);
                            toSingleQueryFilter(queryOptions.singleQueryField(), singleQueryId, singleQueryFiler);

                            // The key will not be the same if we do not use a
                            // DBObject here too. Improve later.
                            // DBObject dbo = buildQueryFilter(modelClass,
                            // singleQueryFiler);

                            cachePut(modelClass, singleQueryFiler,
                                queryOptions.numSetFields() == 1 ? null : queryOptions, singleQueryCacheIds);
                        }
                    }
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }

        return docs;
    }

    private final <T extends Model> void addPartialObjectMarker(Class<T> modelClass, Model modelObject,
        QueryOptions queryOptions) {
        if (queryOptions == null)
            return;

        List<String> attributesToInclude = queryOptions.attributesToInclude();
        List<String> attributesToExclude = queryOptions.attributesToExclude();

        if ((attributesToInclude != null && !attributesToInclude.isEmpty())
            || (attributesToExclude != null && !attributesToExclude.isEmpty())
            || limitFieldsInQuery(modelClass, queryOptions)) {
            Reflect.setField(modelObject, IS_PARTIAL_OBJECT_MARKER_FIELD, true);
        }
    }

    private final <T extends Model> boolean isPartialObject(Class<T> modelClass, Model modelObject) {
        Object obj = Reflect.getFieldValue(modelObject, IS_PARTIAL_OBJECT_MARKER_FIELD);

        return obj != null && TypeConverter.asBoolean(obj) == true;
    }

    protected void filterAttributes(DBObject doc, QueryOptions queryOptions) {
        if (doc == null || queryOptions == null)
            return;

        List<String> attributesToInclude = queryOptions.attributesToInclude();
        List<String> attributesToExclude = queryOptions.attributesToExclude();

        if ((attributesToInclude == null || attributesToInclude.isEmpty())
            && (attributesToExclude == null || attributesToExclude.isEmpty()))
            return;

        DBObject _attributesValues = (DBObject) doc.get(AttributeSupportColumn.ATTRIBUTES);

        if (_attributesValues == null || !(_attributesValues instanceof BasicDBList))
            return;

        BasicDBList attributesValues = (BasicDBList) _attributesValues;

        if (attributesValues.isEmpty())
            return;

        Map<Id, String> attributeIdCodeMap = attributeIdCodeMap();

        if (attributeIdCodeMap == null || attributeIdCodeMap.isEmpty())
            return;

        List<Object> objectsToRemove = new ArrayList<>();

        for (Object obj : attributesValues) {
            DBObject attrValue = (DBObject) obj;

            Id attrId = Id.valueOf(attrValue.get(AttributeValue.Col.ATTRIBUTE_ID));

            if (attrId != null) {
                String attrCode = attributeIdCodeMap.get(attrId);

                if (attributesToInclude != null && !attributesToInclude.isEmpty()
                    && !attributesToInclude.contains(attrCode)) {
                    objectsToRemove.add(obj);
                }

                if ((attributesToInclude == null || attributesToInclude.isEmpty()) && (attributesToExclude != null
                    && !attributesToExclude.isEmpty() && attributesToInclude.contains(attrCode))) {
                    objectsToRemove.add(obj);
                }
            }
        }

        if (objectsToRemove.size() > 0)
            attributesValues.removeAll(objectsToRemove);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void toSingleQueryFilter(String singleQueryField, Id singleQueryId, Object singleQueryFilter) {
        if (singleQueryFilter instanceof Map) {
            Map<String, Object> m = (Map) singleQueryFilter;

            if (m == null || m.size() == 0)
                return;

            Set<String> keys = m.keySet();

            for (String key : keys) {
                Object val = m.get(key);

                if (key.equals(singleQueryField) && val instanceof Map) {
                    Map<String, Object> inMap = new LinkedHashMap<>((Map) val);
                    m.put(key, inMap);

                    if (inMap != null && inMap.size() == 1 && inMap.containsKey(QueryOperators.IN)) {
                        Object[] valArr = inMap.values().toArray();
                        Object inList = valArr[0];

                        if (inList instanceof Collection || inList.getClass().isArray()) {
                            Object[] idsArr = null;

                            if (inList.getClass().isArray())
                                idsArr = (Object[]) inList;

                            else
                                idsArr = ((Collection) inList).toArray();

                            if (idsArr != null && idsArr.length > 0 && idsArr[0] instanceof Id)
                                m.put(singleQueryField, singleQueryId);
                        }
                    }
                } else if (val != null && val instanceof Map) {
                    Map<String, Object> map = new LinkedHashMap<>((Map) val);
                    m.put(key, map);

                    toSingleQueryFilter(singleQueryField, singleQueryId, val);
                } else if (val != null && val instanceof Collection) {
                    List list = new ArrayList<>();
                    m.put(key, list);

                    for (Object o : (Collection) val) {
                        if (o instanceof Map) {
                            list.add(new LinkedHashMap<>((Map) o));
                        } else {
                            list.add(o);
                        }
                    }

                    for (Object o : list) {
                        if (o instanceof Map) {
                            toSingleQueryFilter(singleQueryField, singleQueryId, o);
                        }
                    }
                }
            }
        }
    }

    @Override
    public <T extends Model> List<Object> distinct(Class<T> modelClass, String... distinctField) {
        return distinct(modelClass, null, null, distinctField);
    }

    @Override
    public <T extends Model> List<Object> distinct(Class<T> modelClass, Map<String, Object> filter,
        String... distinctField) {
        return distinct(modelClass, filter, null, distinctField);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <T extends Model> List<Object> distinct(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions, String... distinctField) {
        if (modelClass == null)
            throw new DaoException("Model class cannot be empty");

        if (filter == null)
            filter = EMPTY_FILTER;

        List distinctValues = null;

        DBObject dbObject = buildQueryFilter(modelClass, filter, queryOptions);

        DBCollection col = collection(modelClass);

        distinctValues = col.distinct(distinctField[0], dbObject);

        distinctValues.removeAll(Collections.singleton(null));

        Collections.sort(distinctValues);

        return distinctValues;
    }

    @Override
    public <T extends Model> List<T> findByIds(Class<T> modelClass, Id[] ids) {
        return findByIds(modelClass, ids, null);
    }

    @Override
    public <T extends Model> List<T> findByIds(Class<T> modelClass, Id[] ids, QueryOptions queryOptions) {
        if (modelClass == null || ids == null || ids.length == 0)
            throw new DaoException("Not all parameters set [modelClass=" + modelClass + ", ids=" + ids + "]");

        List<Id> uncachedIds = new ArrayList<>();
        Map<Id, T> documentsFromCache = new LinkedHashMap<>();

        boolean isCacheable = isCacheable(modelClass, queryOptions);
        boolean isFromCacheOnly = isFromCacheOnly(queryOptions);

        // if there is only one id, we'll just do a simple findById.
        if (ids.length == 1) {
            T obj = findById(modelClass, ids[0]);
            List<T> res = new ArrayList<>();

            if (obj != null)
                res.add(obj);

            return res;
        } else {
            // If object exists and is cacheable, add it to return list.
            if ((isCacheable && !isRefresh(queryOptions) && !isPreview()) || isFromCacheOnly) {
                for (Id id : ids) {
                    if (modelCacheHasKeyFor(modelClass, id, queryOptions)) {
                        documentsFromCache.put(id, cacheGet(modelClass, id, queryOptions));
                    } else {
                        uncachedIds.add(id);
                    }
                }
            } else {
                uncachedIds.addAll(Arrays.asList(ids));
            }
        }

        if (uncachedIds.size() > 0 && (!isCacheable || !isFromCacheOnly)) {
            Map<String, Object> inClause = new HashMap<>();
            inClause.put(QueryOperators.IN, uncachedIds);

            Map<String, Object> query2 = new HashMap<>();
            query2.put(GlobalColumn.ID, inClause);

            List<T> documentsFromDB = find(modelClass, query2, queryOptions);

            for (T dbDoc : documentsFromDB) {
                documentsFromCache.put(dbDoc.getId(), dbDoc);
            }
        }

        // Make sure that the sorting stays the same as the id list passed into
        // this method.
        // It seems that Mongodb does not do this by default, but instead sorts
        // by _id.
        List<T> returnDocs = new ArrayList<>();

        if (documentsFromCache.size() > 0) {
            for (Id id : ids) {
                T t = documentsFromCache.get(id);

                if (t != null)
                    returnDocs.add(t);
            }
        }

        return returnDocs;
    }

    @Override
    public <T extends Model> T findOne(Class<T> modelClass, Map<String, Object> filter) {
        List<T> result = find(modelClass, filter);

        return result == null || result.size() == 0 ? null : result.get(0);
    }

    @Override
    public <T extends Model> List<Id> findIds(Class<T> modelClass, Map<String, Object> filter) {
        return findIds(modelClass, filter, null);
    }

    @Override
    public <T extends Model> List<Id> findIds(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions) {
        List<T> result = find(modelClass, filter, QueryOptions.builder(queryOptions).fetchIdsOnly(true).build());

        List<Id> ids = new ArrayList<>();

        for (T model : result) {
            if (model != null && model.getId() != null)
                ids.add(model.getId());
        }

        return ids;
    }

    @Override
    public <T extends Model> List<Map<String, Object>> findData(Class<T> modelClass) {
        return findData(modelClass, null, null);
    }

    @Override
    public <T extends Model> List<Map<String, Object>> findData(Class<T> modelClass, Map<String, Object> filter) {
        return findData(modelClass, filter, null);
    }

    @Override
    public <T extends Model> List<Map<String, Object>> findData(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions) {
        return findData(modelClass, filter, queryOptions, null);
    }

    @Override
    public <T extends Model> List<Map<String, Object>> findData(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions, DBCollection collection) {
        if (modelClass == null)
            throw new DaoException("Model class cannot be empty");

        if (filter == null)
            filter = EMPTY_FILTER;

        List<Map<String, Object>> docs = new ArrayList<>();

        DBObject dbObject = buildQueryFilter(modelClass, filter, queryOptions);

        // if (!modelClass.getName().startsWith("com.geecommerce.core"))
        // System.out.println("filter: " + dbObject);

        DBObject fields = buildFieldList(modelClass, queryOptions);
        // System.out.println("fields: " + fields);

        DBObject sortBy = buildSortBy(modelClass, queryOptions);
        // System.out.println("sort: " + sortBy);

        DBCollection col = collection == null ? collection(modelClass) : collection;

        // ------------------------------------------------------------------
        // Query with filter
        // ------------------------------------------------------------------

        DBCursor cursor = null;

        try {
            cursor = col.find(dbObject, fields);
            cursor.sort(sortBy);

            if (queryOptions != null && queryOptions.limit() != null) {
                cursor.limit(queryOptions.limit());
            } else {
                // There is no reason to ever return more than 1500 results. //
                // TODO: make configurable
                cursor.limit(1500);
            }

            if (queryOptions != null && queryOptions.offset() != null) {
                cursor.skip(queryOptions.offset().intValue());
            }

            // ------------------------------------------------------------------
            // Iterate through results and add to results list docs
            // ------------------------------------------------------------------
            while (cursor.hasNext()) {
                DBObject doc = cursor.next();

                if (doc != null) {
                    try {
                        Map<String, Object> map = new LinkedHashMap<>();
                        convertToMongoDBValues(doc, map);

                        docs.add(map);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        throw new DaoException(t);
                    }
                }
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return docs;
    }

    @Override
    public <T extends Model> List<Map<String, Object>> findDataByIds(Class<T> modelClass, Id[] ids) {
        return findDataByIds(modelClass, ids, null);
    }

    @Override
    public <T extends Model> List<Map<String, Object>> findDataByIds(Class<T> modelClass, Id[] ids,
        QueryOptions queryOptions) {
        if (modelClass == null || ids == null || ids.length == 0)
            throw new DaoException("Not all parameters set [modelClass=" + modelClass + ", ids=" + ids + "]");

        List<Id> docIds = new ArrayList<>();
        docIds.addAll(Arrays.asList(ids));

        Map<String, Object> inClause = new HashMap<>();
        inClause.put("$in", docIds);

        Map<String, Object> query = new HashMap<>();
        query.put(GlobalColumn.ID, inClause);

        return findData(modelClass, query, queryOptions);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T extends Model> T create(T entity) {
        if (entity == null)
            throw new IllegalStateException("Cannot create null document");

        boolean isBulkModeEnabled = Mongo.isBulkModeEnabled();
        int bulkFlushInterval = Mongo.getBulkFlushInterval();
        int bulkProcessCount = Mongo.getBulkProcessingCount();
        boolean bulkError = false;

        Class clazz = entity.getClass();

        try {
            if (!isBulkModeEnabled)
                notify(entity, Event.BEFORE_NEW);

            validateDocument(entity);

            Map<String, Object> map = Models.toMap(clazz, entity);
            map.putAll(((Model) entity).toMap());

            DBObject doc = new BasicDBObject();

            if (map.get(GlobalColumn.ID) == null)
                map.put(GlobalColumn.ID, app.nextId());

            if (map.get(GlobalColumn.CREATED_ON) == null)
                map.put(GlobalColumn.CREATED_ON, DateTimes.newDate());

            if (app.isAPIRequest() && app.isUserLoggedIn()) {
                ClientSession user = app.getLoggedInUser();
                doc.put(GlobalColumn.CREATED_BY, user.getUsername());
            }

            if (Annotations.isOptimisticLockingEnabled(clazz)) {
                map.put(GlobalColumn.VERSION, 1);
            }

            doc.putAll(map);

            // Fetch the already created bulk operation builder in bulk mode.
            if (isBulkModeEnabled) {
                BulkWriteOperation bulkWriteOp = Mongo.fetchBulkWriteOperation(clazz);

                if (bulkWriteOp == null) {
                    bulkWriteOp = collection(clazz).initializeUnorderedBulkOperation();
                    Mongo.stashBulkWriteOperation(clazz, bulkWriteOp);
                }

                // Add the current insert to the bulk operation.
                bulkWriteOp.insert(doc);

                // If auto process count is enabled, we increment to counter.
                if (Mongo.isBulkAutoProcessingCountEnabled())
                    Mongo.incrementBulkProcessingCount();

                // If we have reached the flush interval, we execute the bulk.
                if (bulkProcessCount > 0 && (bulkProcessCount % bulkFlushInterval) == 0) {
                    bulkWriteOp.execute();
                }
            } else {
                // If we are not in bulk mode, then we just insert the document.
                collection(clazz).insert(doc);

                // Remove all entries from the query-cache for now, to make sure
                // that this object
                // can be found in subsequent find-calls. TODO: Make more
                // intelligent in the future.

                if (isCacheable(clazz)) {
                    invalidateModelCache(clazz);

                    invalidateQueryCache(clazz);
                }

                if (Reflect.fieldExists(FIELD_ID, clazz))
                    Reflect.setField(entity, FIELD_ID, doc.get(GlobalColumn.ID));

                if (Reflect.fieldExists(FIELD_CREATED_ON, clazz))
                    Reflect.setField(entity, FIELD_CREATED_ON, doc.get(GlobalColumn.CREATED_ON));

                // Only works in none-batch mode for now (which is most of the
                // time anyway).
                notify(entity, Event.AFTER_NEW);
            }
        } catch (Throwable t) {
            if (isBulkModeEnabled) {
                bulkError = true;
            }

            throw new RuntimeException(t);
        } finally {
            if (bulkError
                || (isBulkModeEnabled && bulkProcessCount > 0 && (bulkProcessCount % bulkFlushInterval) == 0)) {
                Mongo.resetBulkProcessingCount();
                Mongo.resetBulkWriteOperation(clazz);
            }
        }

        return entity;
    }

    @Override
    public <T extends Model> void update(T entity) {
        update(entity, null);
    }

    @Override
    public <T extends Model> void update(T entity, Map<String, Object> filter) {
        update(entity, filter, false);
    }

    @Override
    public <T extends Model> void update(T entity, Map<String, Object> filter, boolean upsert) {
        update(entity, filter, upsert, false);
    }

    public <T extends Model> void update(T entity, Map<String, Object> filter, boolean upsert, boolean multi) {
        update(entity, filter, upsert, multi, (String[]) null);
    }

    @SuppressWarnings("unchecked")
    private <T extends Model> void createHistory(T entity) {
        try {
            if (entity == null)
                return;

            // First we check if history is generally available.
            boolean isHistoryEnabled = app.cpBool_(CONF_KEY_HISTORY_ENABLED, false);

            // History archiving has not been enabled.
            if (!isHistoryEnabled)
                return;

            Class<T> modelClass = (Class<T>) entity.getClass();

            // See if history archiving has been enabled per annotation.
            boolean isHistory = Annotations.isHistoryEnabled(modelClass);

            // See if history archiving has been enabled per configuration.
            String interfaceName = Models.interfaceName(modelClass);
            Boolean isHistoryConf = app.cpBool_(new StringBuilder(CONF_KEY_HISTORY_ENABLED_MODEL_PREFIX)
                .append(interfaceName).append(CONF_KEY_HISTORY_ENABLED_MODEL_SUFFIX).toString());

            // No configuration setting and annotation attribute is not set to
            // true.
            if (isHistoryConf == null && !isHistory)
                return;

            // History archiving has been disabled using db configuration.
            if (isHistoryConf != null && !isHistoryConf)
                return;

            DBObject dbObject = new BasicDBObject();
            dbObject.put(GlobalColumn.ID, entity.getId());
            DBObject doc = collection(modelClass).findOne(dbObject);

            doc.put(GlobalColumn.HISTORY_ID, doc.get(GlobalColumn.ID));
            doc.put(GlobalColumn.HISTORY_DATE, DateTimes.newDate());
            doc.put(GlobalColumn.ID, app.nextId());

            collection(modelClass, true).insert(doc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T extends Model> void update(T entity, Map<String, Object> filter, boolean upsert, boolean multi,
        String... updateFields) {
        if (entity == null)
            throw new IllegalStateException("Cannot update null document");

        boolean isBulkModeEnabled = Mongo.isBulkModeEnabled();
        int bulkFlushInterval = Mongo.getBulkFlushInterval();
        int bulkProcessCount = Mongo.getBulkProcessingCount();
        boolean bulkError = false;

        Class clazz = entity.getClass();

        if (isPartialObject(clazz, entity))
            throw new IllegalStateException(
                "Unable to update a partial object. Please retrieve a complete object first. A partial object is one that does not contain all fields and/or attributes - hence a filtering has taken place at retrieval time. [type="
                    + clazz.getName() + ", id=" + entity.getId() + "]");

        if (!isBulkModeEnabled)
            notify(entity, Event.BEFORE_UPDATE);

        validateDocument(entity);

        Map<String, Object> map = Models.toMap((Class<T>) clazz, entity);
        map.putAll(((Model) entity).toMap());

        if ((filter == null || filter.isEmpty()) && map.get(GlobalColumn.ID) == null
            && !(filter instanceof EmptyFilter))
            throw new IllegalStateException("Cannot update document with null id and query");

        boolean isOptimisticLockingEnabled = Annotations.isOptimisticLockingEnabled(clazz);

        try {
            createHistory(entity);

            DBObject query = new BasicDBObject();

            // If a query has been specified we use that, otherwise we just use
            // use the id from the object.
            if (filter != null && !filter.isEmpty()) {
                query.putAll(filter);
            } else {
                query.put(GlobalColumn.ID, map.get(GlobalColumn.ID));
            }

            // Optimistic locking not available in bulk-mode.
            if (isOptimisticLockingEnabled && !isBulkModeEnabled) {
                if (map.get(GlobalColumn.VERSION) == null) {
                    if (log.isWarnEnabled()) {
                        log.warn("Updating object with optimistic concurrency enabled that has no version field: "
                            + clazz.getSimpleName() + " - " + map);
                    }
                } else {
                    query.put(GlobalColumn.VERSION, map.get(GlobalColumn.VERSION));
                }
            }

            DBObject updateDoc = new BasicDBObject();
            DBObject doc = new BasicDBObject();

            // If a fields array is passed, we only add these specific fields to
            // the update map.
            if (updateFields != null && updateFields.length > 0) {
                for (String field : updateFields) {
                    doc.put(field, map.get(field));
                }
            }
            // Otherwise we add all values that we extracted from the entity.
            else {
                doc.putAll(map);

                doc.removeField(GlobalColumn.ID);
                doc.removeField(GlobalColumn.CREATED_BY);
                doc.removeField(GlobalColumn.CREATED_ON);

                // if (entity instanceof AttributeSupport &&
                // doc.containsField(AttributeSupport.AttributeSupportColumn.ATTRIBUTES))
                // {
                // doc.removeField(AttributeSupport.AttributeSupportColumn.ATTRIBUTES);
                // updateAttributes(entity);
                // }
            }

            doc.put(GlobalColumn.MODIFIED_ON, DateTimes.newDate());

            if (app.isAPIRequest() && app.isUserLoggedIn()) {
                ClientSession user = app.getLoggedInUser();
                doc.put(GlobalColumn.MODIFIED_BY, user.getUsername());
            }

            // Optimistic locking not available in bulk-mode.
            if (isOptimisticLockingEnabled && !isBulkModeEnabled) {
                // We start with 1 if version field does not exist yet.
                if (map.get(GlobalColumn.VERSION) == null) {
                    doc.put(GlobalColumn.VERSION, 1);
                } else {
                    doc.removeField(GlobalColumn.VERSION);

                    DBObject versionInc = new BasicDBObject(GlobalColumn.VERSION, 1);
                    updateDoc.put("$inc", versionInc);
                }
            }

            updateDoc.put("$set", doc);

            // Only insert these values in case of an insert when using upsert.
            if (upsert) {
                DBObject insertDoc = new BasicDBObject();

                if (map.get(GlobalColumn.ID) == null) {
                    insertDoc.put(GlobalColumn.ID, app.nextId());
                } else {
                    insertDoc.put(GlobalColumn.ID, map.get(GlobalColumn.ID));
                }

                if (map.get(GlobalColumn.CREATED_ON) == null)
                    insertDoc.put(GlobalColumn.CREATED_ON, DateTimes.newDate());

                updateDoc.put("$setOnInsert", insertDoc);
            }

            if (isBulkModeEnabled) {
                // Fetch the already created bulk operation builder in bulk
                // mode.
                BulkWriteOperation bulkWriteOp = Mongo.fetchBulkWriteOperation(clazz);

                // Create a new one if it does not exist yet.
                if (bulkWriteOp == null) {
                    bulkWriteOp = collection(clazz).initializeOrderedBulkOperation();
                    Mongo.stashBulkWriteOperation(clazz, bulkWriteOp);
                }

                BulkWriteRequestBuilder bulkWriteRequestBuilder = bulkWriteOp.find(query);

                if (upsert) {
                    bulkWriteRequestBuilder.upsert().update(updateDoc);
                } else {
                    bulkWriteRequestBuilder.update(updateDoc);
                }

                // If auto process count is enabled, we increment to counter.
                if (Mongo.isBulkAutoProcessingCountEnabled())
                    Mongo.incrementBulkProcessingCount();

                // If we have reached the flush interval, we execute the bulk.
                if (bulkProcessCount > 0 && (bulkProcessCount % bulkFlushInterval) == 0) {
                    bulkWriteOp.execute();
                }
            } else {
                WriteResult result = collection(clazz).update(query, updateDoc, upsert, multi);
                int numUpdatedResults = result.getN();

                // If the object is cacheable, remove it after the update.
                if (isCacheable(clazz)) {
                    invalidateModelCache(clazz);

                    // We also invalidate the query cache for now, in case a
                    // sort number has changed.
                    // TODO: Make more intelligent in the future.
                    invalidateQueryCaches(entity.getId());
                }

                // Make sure that we have not run into concurrency issues and
                // that the document has been updated.
                if (isOptimisticLockingEnabled) {
                    Versionable vModel = findOne(clazz, new BasicDBObject(GlobalColumn.ID, map.get(GlobalColumn.ID)));

                    // Optimistic concurrency error.
                    if (numUpdatedResults == 0 && vModel.getVersion() != ((Versionable) entity).getVersion()) {
                        throw new OptimisticLockException("Unable to update document of type '" + clazz.getSimpleName()
                            + "' because the version number did not match - expected current version to be '"
                            + ((Versionable) entity).getVersion() + "' and not '" + vModel.getVersion()
                            + "'. The version number has most likely been updated by another process. Please check.\nOld: "
                            + map + ".\nNew: " + ((Model) vModel).toMap() + ".");
                    }

                    if (numUpdatedResults > 0 && vModel.getVersion() == ((Versionable) entity).getVersion()) {
                        if (log.isWarnEnabled()) {
                            log.warn("The document of type '" + clazz.getSimpleName()
                                + "' has been updated successfully, but the version number has not been updated, even although optimistic concurrency is enabled for this object.");
                        }
                    }

                    // Make sure that the updated object has the new version
                    // number in case
                    // further updates are done on the same object.
                    ((Versionable) entity).setVersion(vModel.getVersion());
                    map.put(GlobalColumn.VERSION, vModel.getVersion());
                }

                notify(entity, Event.AFTER_UPDATE);
            }
        } catch (Throwable t) {
            if (isBulkModeEnabled) {
                bulkError = true;
            }

            throw new RuntimeException(t);
        } finally {
            if (bulkError
                || (isBulkModeEnabled && bulkProcessCount > 0 && (bulkProcessCount % bulkFlushInterval) == 0)) {
                Mongo.resetBulkProcessingCount();
                Mongo.resetBulkWriteOperation(clazz);
            }
        }
    }

    protected <T extends Model> void updateAttributes(T entity) {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Model> void delete(T entity) {
        if (entity == null)
            return;

        notify(entity, Event.BEFORE_REMOVE);

        Id id = ((Model) entity).getId();

        if (id == null) {
            Map<String, Object> map = Models.toMap((Class<T>) entity.getClass(), entity);
            map.putAll(((Model) entity).toMap());

            // Map<String, Object> map = ((Model) entity).toMap();

            if (map.get(GlobalColumn.ID) != null) {
                id = (Id) map.get(GlobalColumn.ID);
            } else {
                throw new IllegalStateException("Cannot delete document with null id");
            }
        }

        DBObject query = new BasicDBObject();
        query.put(GlobalColumn.ID, id);

        collection(entity.getClass()).remove(query);

        notify(entity, Event.AFTER_REMOVE);

        // If the object is cacheable, remove it after the update.
        if (isCacheable(entity.getClass())) {
            invalidateModelCache(entity.getClass());

            // Remove all entries from the query-cache containing this id, to
            // make sure that this
            // object does not turn up in future find calls. TODO: Make more
            // intelligent in the future.

            invalidateQueryCaches(entity.getId());
        }
    }

    @Override
    public <T extends Model> void delete(Class<T> modelClass, Map<String, Object> filter) {
        if (modelClass == null || filter == null || filter.size() == 0)
            return;

        DBObject query = new BasicDBObject();
        query.putAll(filter);

        collection(modelClass).remove(query);

        // If the object is cacheable, remove it after the update.
        if (isCacheable(modelClass)) {
            invalidateModelCache(modelClass);
        }
    }

    @Override
    public void createDatabase(String name) {
        MongoClient mongoClient = com.geecommerce.core.db.Mongo.CLIENT.get();
        DB db = mongoClient.getDB(name);
        db.getCollection("test").insert(new BasicDBObject("key1", "value1"));

    }

    @Override
    public <T extends Model> T findSnapshot(Class<T> modelClass, Id id, Integer version) {
        return findSnapshot(modelClass, id, version, null);
    }

    @Override
    public <T extends Model> T findSnapshot(Class<T> modelClass, Id id, Integer version, QueryOptions queryOptions) {
        if (modelClass == null || version == null)
            throw new DaoException("Not all parameters set [modelClass=" + modelClass + ", version=" + version + "]");

        T modelObject = null;

        DBObject dbObject = new BasicDBObject();
        dbObject.put(GlobalColumn.HISTORY_ID, id);
        dbObject.put(GlobalColumn.VERSION, version);

        DBObject fields = buildFieldList(modelClass, queryOptions);

        DBObject doc = collection(modelClass, true).findOne(dbObject, fields);

        if (doc != null) {
            try {
                Map<String, Object> map = new LinkedHashMap<>();
                convertToMongoDBValues(doc, map);

                modelObject = app.model(modelClass);

                Models.populate(modelClass, modelObject, map);
                modelObject.fromMap(map);
            } catch (Throwable t) {
                throw new DaoException(t);
            }
        }

        return modelObject;
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id) {
        return findSnapshots(modelClass, id, (QueryOptions) null);
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id, QueryOptions queryOptions) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(GlobalColumn.HISTORY_ID, id);

        return findSnapshots(modelClass, filter, queryOptions);
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id, Integer[] versions) {
        return findSnapshots(modelClass, id, versions, null);
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Id id, Integer[] versions,
        QueryOptions queryOptions) {
        Map<String, Object> inClause = new HashMap<>();
        inClause.put(QueryOperators.IN, versions);

        Map<String, Object> filter = new HashMap<>();
        filter.put(GlobalColumn.HISTORY_ID, id);
        filter.put(GlobalColumn.VERSION, inClause);

        return findSnapshots(modelClass, filter, queryOptions);
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Map<String, Object> filter) {
        return findSnapshots(modelClass, filter, null);
    }

    @Override
    public <T extends Model> List<T> findSnapshots(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions) {
        if (modelClass == null)
            throw new DaoException("Model class cannot be empty");

        List<T> docs = new ArrayList<>();

        Long count = null;

        if ((queryOptions != null && queryOptions.isProvideCount())) {
            count = snapshotCount(modelClass, filter);
        }

        DBObject dbObject = buildQueryFilter(modelClass, filter, queryOptions);

        DBObject fields = buildFieldList(modelClass, queryOptions);
        // System.out.println("fields: " + fields);

        DBObject sortBy = buildSortBy(modelClass, queryOptions);
        // System.out.println("sort: " + sortBy);

        DBCollection col = collection(modelClass, true);

        // System.out.println("filter: " + dbObject + " -> " + sortBy);

        // ------------------------------------------------------------------
        // Query with filter
        // ------------------------------------------------------------------

        DBCursor cursor = null;

        // If we have a count, we might as well use it to determine whether it
        // is worth doing a query or not.
        if (count == null || count > 0) {
            try {
                cursor = col.find(dbObject, fields);

                cursor.sort(sortBy);

                if (queryOptions != null && queryOptions.limit() != null) {
                    if (queryOptions.limit() != -1)
                        cursor.limit(queryOptions.limit());
                } else {
                    if (queryOptions != null && queryOptions.isFetchIdsOnly()) {
                        // We allow more results when only fetching ids.
                        cursor.limit(100000);
                    } else {
                        // There is no reason to ever return more than 1500
                        // results. // TODO: make configurable
                        cursor.limit(1500);
                    }
                }

                if (queryOptions != null && queryOptions.offset() != null) {
                    cursor.skip(queryOptions.offset().intValue());
                }

                // ------------------------------------------------------------------
                // Iterate through results and add to results list docs
                // ------------------------------------------------------------------
                while (cursor.hasNext()) {
                    DBObject doc = cursor.next();

                    T modelObject = null;

                    if (doc != null) {
                        try {
                            Map<String, Object> map = new LinkedHashMap<>();

                            // AS we cannot filter attributes out at query
                            // stage, we at least try to
                            // remove unwanted ones as soon as possible, so that
                            // there is no need
                            // to process them in the upcoming steps.
                            filterAttributes(doc, queryOptions);

                            convertToMongoDBValues(doc, map);

                            modelObject = app.inject(modelClass);

                            Models.populate(modelClass, modelObject, map);

                            modelObject.fromMap(map);

                            docs.add(modelObject);
                        } catch (Throwable t) {
                            t.printStackTrace();
                            throw new DaoException(t);
                        }
                    }
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }

        return docs;
    }

    @Override
    public <T extends Model> Long snapshotCount(Class<T> modelClass, Map<String, Object> filter) {
        if (modelClass == null)
            throw new DaoException("Model class cannot be null");

        if (filter == null)
            filter = EMPTY_FILTER;

        DBObject dbObject = buildQueryFilter(modelClass, filter, null);

        DBCollection col = collection(modelClass, true);

        Long count = col.count(dbObject);

        if (count != null) {
            app.setQueryMetadata(QueryMetadata.builder().count(count).build());
        }

        return count;
    }

    private void updateReadCounter(DBCollection col, Object _id) {
        try {
            DBObject readCountUpdate = new BasicDBObject("$inc", new BasicDBObject(GlobalColumn.READ_COUNT, 1));
            readCountUpdate.put("$set", new BasicDBObject(GlobalColumn.READ_COUNT_DATE, DateTimes.newDate()));

            col.update(new BasicDBObject(GlobalColumn.ID, _id), readCountUpdate);
        } catch (Throwable t) {
            throw new DaoException(t);
        }
    }

    private final <T extends Model> void validateDocument(T entity) {
        if (entity instanceof MultiContextModel) {
            MultiContextModel mcm = (MultiContextModel) entity;

            // True if this is for global scope.
            if ((mcm.getMerchantIds() == null || mcm.getMerchantIds().size() == 0)
                && (mcm.getStoreIds() == null || mcm.getStoreIds().size() == 0)
                && (mcm.getRequestContextIds() == null || mcm.getRequestContextIds().size() == 0)) {
                return;
            }

            isValidContextHierarchy((MultiContextModel) entity);
        }
    }

    private final boolean isValidContextHierarchy(MultiContextModel multiCtxModel) {
        List<Id> reqCtxIds = multiCtxModel.getRequestContextIds();
        List<Id> storeIds = multiCtxModel.getStoreIds();
        List<Id> merchantIds = multiCtxModel.getMerchantIds();

        // Is the object for global scope?
        if ((reqCtxIds == null || reqCtxIds.size() == 0) && (storeIds == null || storeIds.size() == 0)
            && (merchantIds == null || merchantIds.size() == 0)) {
            return true;
        }

        // Get tree representation of all request-contexts (global -> merchant
        // -> store -> request-context).
        ContextTree ctxTree = app.getContextTree();

        // Lets start with the request-context-ids.
        if (reqCtxIds != null && reqCtxIds.size() > 0) {
            for (Id id : reqCtxIds) {
                // We get the requestContext node here, so that we can travel up
                // afterwards, looking for nodes that
                // belong to this context hierarchy (which should not be the
                // case).
                ContextNode reqCtxNode = ctxTree.findContextNode(id);

                // Now we make sure that the context-hierarchy only has one
                // element in the multi-context-model
                // and not more, i.e. merchant and store instead of just store
                // or merchant.

                if (storeIds != null && storeIds.size() > 0) {
                    for (Id storeId : storeIds) {
                        ContextNode storeNode = reqCtxNode.findParent(storeId);

                        if (storeNode != null)
                            throw new IllegalMultiContextException(
                                "When storing a MultiContextModel object, make sure that each scope hierarchy only has one of merchant, store or request-context and not a combination of them. Found [reqCtxId="
                                    + id + ", storeId=" + storeId + "].");
                    }
                }

                if (merchantIds != null && merchantIds.size() > 0) {
                    // Do the same with the merchant ids.
                    for (Id merchantId : merchantIds) {
                        ContextNode merchantNode = reqCtxNode.findParent(merchantId);

                        if (merchantNode != null)
                            throw new IllegalMultiContextException(
                                "When storing a MultiContextModel object, make sure that each scope hierarchy only has one of merchant, store or request-context and not a combination of them. Found [reqCtxId="
                                    + id + ", merchantId=" + merchantId + "].");
                    }
                }
            }
        }

        // If we have got this far, the request-contexts seem to be OK. Lets try
        // and find an illegal store-merchant
        // combo.
        if (storeIds != null && storeIds.size() > 0) {
            for (Id storeId : storeIds) {
                // We get the requestContext node here, so that we can travel up
                // afterwards, looking for nodes that
                // belong to this context hierarchy (which should not be the
                // case).
                ContextNode storeNode = ctxTree.findContextNode(storeId);

                if (merchantIds != null && merchantIds.size() > 0) {
                    // Do the same with the merchant ids.
                    for (Id merchantId : merchantIds) {
                        ContextNode merchantNode = storeNode.findParent(merchantId);

                        if (merchantNode != null)
                            throw new IllegalMultiContextException(
                                "When storing a MultiContextModel object, make sure that each scope hierarchy only has one of merchant, store or request-context and not a combination of them. Found [storeId="
                                    + storeId + ", merchantId=" + merchantId + "].");
                    }
                }
            }
        }

        // Context information in MultiContextModel seems to be OK - return
        // true.

        return true;
    }

    @SuppressWarnings("unchecked")
    private final void convertToMongoDBValues(final Object fromDoc, final Object toObject) {
        Collection<Object> col = null;

        if (fromDoc instanceof List) {
            col = (Collection<Object>) fromDoc;
        } else if (fromDoc instanceof Map) {
            col = new ArrayList<>();

            for (Object key : ((Map<Object, Object>) fromDoc).keySet()) {
                col.add(key);
            }
        }

        for (Object object : col) {
            if (fromDoc instanceof List) {
                if (object instanceof List) {
                    List<Object> innerList = new ArrayList<>();
                    convertToMongoDBValues(object, innerList);
                    ((List<Object>) toObject).add(innerList);
                } else if (object instanceof Map) {
                    Map<Object, Object> innerMap = new LinkedHashMap<>();
                    convertToMongoDBValues(object, innerMap);
                    ((List<Object>) toObject).add(innerMap);
                } else {
                    ((List<Object>) toObject).add(new MongoValue(object));
                }
            } else if (fromDoc instanceof Map) {
                Object mapValue = ((Map<Object, Object>) fromDoc).get(object);

                if (mapValue instanceof List) {
                    List<Object> innerList = new ArrayList<>();
                    convertToMongoDBValues(mapValue, innerList);

                    ((Map<Object, Object>) toObject).put(object, innerList);

                } else if (mapValue instanceof Map) {
                    Map<Object, Object> innerMap = new LinkedHashMap<>();
                    convertToMongoDBValues(mapValue, innerMap);
                    ((Map<Object, Object>) toObject).put(object, innerMap);
                } else {
                    ((Map<Object, Object>) toObject).put(object, new MongoValue(mapValue));
                }
            }
        }
    }

    protected <T extends Model> DBObject buildFieldList(Class<T> modelClass, QueryOptions queryOptions) {
        DBObject fields = null;

        boolean limitFieldsInQuery = limitFieldsInQuery(modelClass, queryOptions);

        if (queryOptions != null && queryOptions.isFetchIdsOnly()) {
            fields = new BasicDBObject();
            fields.put(GlobalColumn.ID, 1);
        } else if (queryOptions != null && queryOptions.fieldsToInclude() != null && limitFieldsInQuery) {
            List<ColumnInfo> columns = Annotations.getColumns(modelClass);

            fields = new BasicDBObject();

            List<String> fieldList = queryOptions.fieldsToInclude();

            for (String field : fieldList) {
                fields.put(Models.columnName(columns, field), 1);
            }
        }

        return fields;
    }

    protected <T extends Model> DBObject buildSortBy(Class<T> modelClass, QueryOptions queryOptions) {
        if (modelClass == null || queryOptions == null)
            return null;

        DBObject sortFields = null;

        List<ColumnInfo> columns = null;

        if ((queryOptions.sortAsc() != null && queryOptions.sortAsc().size() > 0)
            || (queryOptions.sortDesc() != null && queryOptions.sortDesc().size() > 0)) {
            columns = Annotations.getColumns(modelClass);
        }

        // ----------------------------------------
        // Ascending sort fields
        // ----------------------------------------
        if (queryOptions.sortAsc() != null && queryOptions.sortAsc().size() > 0) {
            sortFields = new BasicDBObject();

            List<String> ascFields = queryOptions.sortAsc();

            for (String ascField : ascFields) {
                sortFields.put(Models.columnName(columns, ascField), 1);
            }
        }

        // ----------------------------------------
        // Descending sort fields
        // ----------------------------------------
        if (queryOptions.sortDesc() != null && queryOptions.sortDesc().size() > 0) {
            if (sortFields == null)
                sortFields = new BasicDBObject();

            List<String> descFields = queryOptions.sortDesc();

            for (String descField : descFields) {
                sortFields.put(Models.columnName(columns, descField), -1);
            }
        }

        return sortFields;
    }

    /**
     * Build query filter from map.
     */
    protected <T extends Model> DBObject buildQueryFilter(Class<T> modelClass, Map<String, Object> filter,
        QueryOptions queryOptions) {
        DBObject dbObject = new BasicDBObject();

        if (filter != null && filter.size() > 0) {
            List<ColumnInfo> columns = Annotations.getColumns(modelClass);

            Set<String> filterKeys = filter.keySet();

            for (String key : filterKeys) {
                Object value = filter.get(key);

                if (value == null) {
                    dbObject.put(Models.columnName(columns, key), value);
                } else if (value instanceof String && ObjectId.isValid((String) value)) {
                    dbObject.put(Models.columnName(columns, key), new ObjectId((String) filter.get(key)));
                } else if (isDateRange(value)) {
                    DBObject between = createBetweenFilter(value);

                    dbObject.put(Models.columnName(columns, key), between);
                }
                // TODO
                else if ((value instanceof Collection || value.getClass().isArray()) && !key.equals(QueryOperators.IN)
                    && !key.equals(QueryOperators.AND) && !key.equals(QueryOperators.OR)) {
                    dbObject.put(Models.columnName(columns, key), new BasicDBObject(QueryOperators.IN, value));
                } else if (isProbableBoolean(key, filter.get(key))) {
                    dbObject.put(Models.columnName(columns, key), TypeConverter.asBoolean(filter.get(key)));
                } else {
                    processFilterPart(modelClass, key, Models.columnName(columns, key), filter.get(key), dbObject,
                        queryOptions);

                    // dbObject.put(columnName(columns, key),
                    // processCommandIfExists(filter.get(key)));
                }
            }
        }

        return dbObject;
    }

    private DBObject createBetweenFilter(Object value) {
        JSONObject dateRange = parseDateRange(value);

        org.joda.time.format.DateTimeFormatter parser = ISODateTimeFormat.dateTime();

        BasicDBObjectBuilder start = BasicDBObjectBuilder.start();

        if (dateRange.get("startDate") != null) {
            DateTime startDate = parser.parseDateTime((String) dateRange.get("startDate"));
            start.add(QueryOperators.GTE, startDate.toDate());
        }

        if (dateRange.get("endDate") != null) {
            DateTime endDate = parser.parseDateTime((String) dateRange.get("endDate"));
            start.add(QueryOperators.LTE, endDate.toDate());
        }

        return start.get();
    }

    private static final String ATTRIBUTE_MAPPING_CACHE_NAME = "gc/attributes/id_code_mapping";
    private static final String ATTRIBUTE_MAPPING_KEY_NAME = "all";

    protected Map<Id, String> attributeIdCodeMap() {
        CacheManager cm = app.inject(CacheManager.class);
        Cache<String, Map<Id, String>> c = cm.getCache(ATTRIBUTE_MAPPING_CACHE_NAME);

        Map<Id, String> attributeIdCodeMap = c.get(ATTRIBUTE_MAPPING_KEY_NAME);

        if (attributeIdCodeMap == null) {
            List<Map<String, Object>> attributeIdCodeList = findData(Attribute.class, null,
                QueryOptions.builder().fetchFields(GlobalColumn.ID, Attribute.Col.CODE).build(),
                collection(Attribute.class));

            if (attributeIdCodeList != null && !attributeIdCodeList.isEmpty()) {
                attributeIdCodeMap = new LinkedHashMap<>();

                for (Map<String, Object> map : attributeIdCodeList) {
                    attributeIdCodeMap.put(TypeConverter.asId(map.get(GlobalColumn.ID)),
                        TypeConverter.asString(map.get(Attribute.Col.CODE)));
                }

                c.put(ATTRIBUTE_MAPPING_KEY_NAME, attributeIdCodeMap);
            }
        }

        return attributeIdCodeMap;
    }

    private static List<String> booleanWhiteList = new ArrayList<>();

    static {
        booleanWhiteList.add("enabled");
    }

    /**
     * There are certain fields that are most likely booleans, but the value is
     * passed as an int. Mongodb cannot handle this, so we silently convert.
     *
     * @param key
     * @param object
     * @return boolean
     */
    private boolean isProbableBoolean(String key, Object object) {
        if (object instanceof Boolean) {
            return true;
        } else if (booleanWhiteList.contains(key)) {
            if (object instanceof Number) {
                int n = ((Number) object).intValue();

                if (n == 0 || n == 1) {
                    return true;
                }
            }
        }

        return false;
    }

    public void processFilterPart(Class<? extends Model> modelClass, String originalKey, String columnName,
        Object value, DBObject query, QueryOptions queryOptions) {
        Command cmd = null;

        if (value instanceof String || originalKey.startsWith("$"))
            cmd = CommandFactory.INSTANCE.get(originalKey, value);

        if (cmd == null) {
            query.put(columnName, value);
        } else {
            cmd.process(modelClass, originalKey, columnName, value, query, queryOptions);
        }
    }
}
