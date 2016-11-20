package com.geecommerce.core.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.service.api.Value;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.type.IdSupport;
import com.geecommerce.core.type.TypeConverter;
import com.google.inject.Inject;
import com.owlike.genson.annotation.JsonIgnore;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractModel implements Model {
    private static final long serialVersionUID = -1961148696446650676L;

    @Inject
    protected App app;

    // Model-thread-cache key.
    protected static final String THREAD_KEY_PREFIX = "mtc/";

    @Column(GlobalColumn.CREATED_ON)
    protected Date createdOn = null;

    @Column(GlobalColumn.CREATED_BY)
    protected String createdBy = null;

    @Column(GlobalColumn.MODIFIED_ON)
    protected Date modifiedOn = null;

    @Column(GlobalColumn.MODIFIED_BY)
    protected String modifiedBy = null;

    @Column(GlobalColumn.VERSION)
    protected Long version = null;

    @Column(GlobalColumn.HISTORY_ID)
    protected Id historyId = null;

    @Column(GlobalColumn.HISTORY_DATE)
    protected Date historyDate = null;

    // Hidden field only used by DAO.
    @JsonIgnore
    @XmlTransient
    private boolean __cbIsPartialObject = false;

    @JsonIgnore
    @XmlTransient
    private Map<String, Class<?>> __cbTransientServiceFields = new HashMap<>();

    protected AbstractModel() {
        Map<String, Field> transientServiceFields = Reflect.getTransientServiceFields(this.getClass());

        Set<String> keys = transientServiceFields.keySet();

        for (String fieldName : keys) {
            __cbTransientServiceFields.put(fieldName, transientServiceFields.get(fieldName).getType());
        }
    }

    protected String __name() {
        return null;
    }

    protected final static <T> T i(final Class<T> type) {
        return inject(type);
    }

    protected final static <T> T inject(final Class<T> type) {
        return App.get().inject(type);
    }

    protected final static void injectMembers(final Object instance) {
        App.get().injectMembers(instance);
    }

    public void set(Map<String, Object> updateMap) {
        set(updateMap, true);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void set(Map<String, Object> updateMap, boolean override) {
        if (updateMap == null)
            return;

        List<ColumnInfo> columns = Annotations.getColumns(this.getClass());

        Set<String> keys = updateMap.keySet();

        Map<String, Object> oldMap = toMap();

        for (String key : keys) {
            Object val = updateMap.get(key);

            // if (!oldMap.containsKey(key) && val == null)
            // continue;

            if (!oldMap.containsKey(key) || override) {
                ColumnInfo col = column(columns, key);

                if (col != null && Set.class.isAssignableFrom(col.type()) && val instanceof List)
                    val = new HashSet((List) val);

                oldMap.put(col == null ? key : col.name(), val);
            }
        }

        Models.populate(this.getClass(), this, oldMap);

        fromMap(oldMap);
    }

    @Override
    public void set(String field, Object value) {
        // System.out.println("set(String field, Object value) in ABSTRACT
        // MODEL!");

        // This method is not used directly. Instead, it is copied to the
        // model-implementation
        // using bytecode-instrumentation and used for populating objects
        // instead of reflection.
    }

    protected void threadPut(IdSupport idObject, String key, Object value) {
        if (idObject == null || key == null)
            return;

        app.registryPut(threadCacheKey(idObject, key), value);
    }

    protected <T> T threadGet(IdSupport idObject, String key) {
        if (idObject == null || key == null)
            return null;

        return app.registryGet(threadCacheKey(idObject, key));
    }

    protected String threadCacheKey(IdSupport idObject, String key) {
        ApplicationContext appCtx = app.context();
        RequestContext reqCtx = appCtx.getRequestContext();

        StringBuilder cacheKey = new StringBuilder(THREAD_KEY_PREFIX);

        if (reqCtx != null && reqCtx.getId() != null) {
            cacheKey.append("r").append(reqCtx.getId()).append(Char.SLASH);
        } else {
            Store store = appCtx.getStore();

            if (store != null && store.getId() != null) {
                cacheKey.append("s").append(store.getId().str()).append(Char.SLASH);
            } else {
                Merchant merchant = appCtx.getMerchant();

                if (merchant != null && merchant.getId() != null) {
                    cacheKey.append("m").append(merchant.getId().str()).append(Char.SLASH);
                }
            }
        }

        cacheKey.append(idObject.getClass().getSimpleName()).append(Char.AT).append(idObject.getId()).append(Char.COLON)
            .append(key);

        return cacheKey.toString();
    }

    public Map<String, Object> normalize(Map<String, Object> map) {
        if (map == null)
            return map;

        List<ColumnInfo> columns = Annotations.getColumns(this.getClass());
        Set<String> keys = map.keySet();
        Map<String, Object> resultMap = new HashMap<>();
        for (String key : keys) {
            Object val = map.get(key);
            resultMap.put(columnName(columns, key), val);
        }
        return resultMap;
    }

    protected ColumnInfo column(List<ColumnInfo> columnInfos, String propertyName) {
        if (columnInfos != null && columnInfos.size() > 0) {
            for (ColumnInfo columnInfo : columnInfos) {
                if (columnInfo.property().equals(propertyName)) {
                    return columnInfo;
                }
            }
        }

        return null;
    }

    protected String columnName(List<ColumnInfo> columnInfos, String propertyName) {
        if (columnInfos != null && columnInfos.size() > 0) {
            for (ColumnInfo columnInfo : columnInfos) {
                if (columnInfo.property().equals(propertyName)) {
                    return columnInfo.name();
                }
            }
        }

        return propertyName;
    }

    @Override
    public Date getCreatedOn() {
        return createdOn;
    }

    @Override
    public Model setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public Model setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    @Override
    public Date getModifiedOn() {
        return modifiedOn;
    }

    @Override
    public Model setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
        return this;
    }

    @Override
    public String getModifiedBy() {
        return modifiedBy;
    }

    @Override
    public Model setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
        return this;
    }

    @Override
    public Long getVersion() {
        return version;
    }

    @Override
    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public Id getHistoryId() {
        return historyId;
    }

    @Override
    public void setHistoryId(Id historyId) {
        this.historyId = historyId;
    }

    @Override
    public Date getHistoryDate() {
        return historyDate;
    }

    @Override
    public void setHistoryDate(Date historyDate) {
        this.historyDate = historyDate;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null || map.size() == 0)
            return;

        this.createdOn = date_(map.get(GlobalColumn.CREATED_ON));
        this.createdBy = str_(map.get(GlobalColumn.CREATED_BY));
        this.modifiedOn = date_(map.get(GlobalColumn.MODIFIED_ON));
        this.modifiedBy = str_(map.get(GlobalColumn.MODIFIED_BY));

        if (map.get(GlobalColumn.VERSION) != null)
            this.version = long_(map.get(GlobalColumn.VERSION));

        if (map.get(GlobalColumn.HISTORY_ID) != null)
            this.historyId = id_(map.get(GlobalColumn.HISTORY_ID));

        if (map.get(GlobalColumn.HISTORY_DATE) != null)
            this.historyDate = date_(map.get(GlobalColumn.HISTORY_DATE));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put(GlobalColumn.CREATED_ON, getCreatedOn());

        if (!Str.isEmpty(getCreatedBy()))
            map.put(GlobalColumn.CREATED_BY, getCreatedBy());

        map.put(GlobalColumn.MODIFIED_ON, getModifiedOn());

        if (!Str.isEmpty(getModifiedBy()))
            map.put(GlobalColumn.MODIFIED_BY, getModifiedBy());

        if (getVersion() != null && getVersion() > 0)
            map.put(GlobalColumn.VERSION, getVersion());

        if (getHistoryId() != null)
            map.put(GlobalColumn.HISTORY_ID, getHistoryId());

        if (getHistoryDate() != null)
            map.put(GlobalColumn.HISTORY_DATE, getHistoryDate());

        return map;
    }

    public byte[] bytes_(Object object) {
        return TypeConverter.asBytes(object);
    }

    public Id id_(Object object) {
        return TypeConverter.asId(object);
    }

    public String str_(Value val) {
        return TypeConverter.asString(val);
    }

    public String str_(Object object) {
        return TypeConverter.asString(object);
    }

    public String str_(Object object, String defaultValue) {
        return isNull(object) ? defaultValue : str_(object);
    }

    public Integer int_(Object object) {
        return TypeConverter.asInteger(object);
    }

    public Integer int_(Object object, int defaultValue) {
        return isNull(object) ? defaultValue : int_(object);
    }

    public Long long_(Object object) {
        return TypeConverter.asLong(object);
    }

    public Long long_(Object object, long defaultValue) {
        return isNull(object) ? defaultValue : long_(object);
    }

    public Double double_(Object object) {
        return TypeConverter.asDouble(object);
    }

    public Double double_(Object object, double defaultValue) {
        return isNull(object) ? defaultValue : double_(object);
    }

    public Float float_(Object object) {
        return TypeConverter.asFloat(object);
    }

    public Float float_(Object object, float defaultValue) {
        return isNull(object) ? defaultValue : float_(object);
    }

    public Short short_(Object object) {
        return TypeConverter.asShort(object);
    }

    public Short short_(Object object, short defaultValue) {
        return isNull(object) ? defaultValue : short_(object);
    }

    public Boolean bool_(Object object) {
        return TypeConverter.asBoolean(object);
    }

    public Boolean bool_(Object object, boolean defaultValue) {
        return isNull(object) ? defaultValue : bool_(object);
    }

    public BigDecimal bigDecimal_(Object object) {
        return TypeConverter.asBigDecimal(object);
    }

    public BigDecimal bigDecimal_(Object object, BigDecimal defaultValue) {
        return isNull(object) ? defaultValue : bigDecimal_(object);
    }

    public BigInteger bigInteger_(Object object) {
        return TypeConverter.asBigInteger(object);
    }

    public BigInteger bigInteger_(Object object, BigInteger defaultValue) {
        return isNull(object) ? defaultValue : bigInteger_(object);
    }

    public Character char_(Object object) {
        return TypeConverter.asCharacter(object);
    }

    public Character char_(Object object, char defaultValue) {
        return isNull(object) ? defaultValue : char_(object);
    }

    public UUID uuid_(Object object) {
        return TypeConverter.asUUID(object);
    }

    public <E extends Enum<E>> E enum_(Class<E> enumType, Object object) {
        return TypeConverter.asEnum(enumType, object);
    }

    public <E extends Enum<E>> E enum_(Class<E> enumType, Object object, E defaultValue) {
        return isNull(object) ? defaultValue : enum_(enumType, object);
    }

    public <E extends Enum<E>> List<E> enumList_(Class<E> enumType, Object object) {
        return TypeConverter.asListOfEnums(enumType, object);
    }

    public <E extends Enum<E>> Set<E> enumSet_(Class<E> enumType, Object object) {
        return TypeConverter.asSetOfEnums(enumType, object);
    }

    public static <T> ContextObject<T> ctxObj_(Object object) {
        return ctxObj_(object, false);
    }

    public static <T> ContextObject<T> ctxObj_(Object object, boolean containsIdList) {
        return TypeConverter.asContextObject(object, containsIdList);
    }

    public static void main(String[] args) {
        List<Map<String, Object>> mapList = new ArrayList<>();

        List<Long> optionIds = new ArrayList<>();
        optionIds.add(987654321L);

        Map<String, Object> m = new HashMap<>();
        m.put("s", 123456789L);
        m.put("val", optionIds);

        mapList.add(m);

        ContextObject<List<Id>> ctxObj = ctxObj_(mapList, true);

        System.out.println(ctxObj);
    }

    public <T> List<ContextObject<?>> ctxObjList_(Object object) {
        return TypeConverter.asListOfContextObjects(object);
    }

    public String ascii_(Object object) {
        return TypeConverter.asASCII(object);
    }

    public Date date_(Object object) {
        return TypeConverter.asDate(object);
    }

    public Map<Object, Object> objMap_(Object object) {
        return TypeConverter.asObjectMap(object);
    }

    public <K, V> Map<K, V> map_(Object object) {
        return TypeConverter.asMap(object);
    }

    public <K, V> Map<K, V> mapIdList_(Object object) {
        return TypeConverter.asIdListsInMap(object);
    }

    public <K, V> Map<K, V> mapIdSet_(Object object) {
        return TypeConverter.asIdSetsInMap(object);
    }

    public <T> List<T> list_(Object object) {
        return TypeConverter.asList(object);
    }

    public <T> Set<T> set_(Object object) {
        return TypeConverter.asSet(object);
    }

    public List<Id> idList_(Object object) {
        return TypeConverter.asIdList(object);
    }

    public Set<Id> idSet_(Object object) {
        return TypeConverter.asIdSet(object);
    }

    public Object raw_(Object object) {
        return TypeConverter.asRaw(object);
    }

    protected boolean isNull(Object object) {
        if (object == null)
            return true;

        if (object instanceof Value && ((Value) object).getRaw() == null)
            return true;

        return false;
    }

    /**
     * This method is called automatically by the JVM during deserialization. As
     * the injected objects, which have been marked as transient, will not be
     * recovered after deserialization, we do it manually here. In order for
     * this to work it is very important for the model-constructor to invoke
     * super(), as this ensures that the transient fields are remembered, ready
     * for re-injecting after deserialization.
     * <p>
     * http://docs.oracle.com/javase/8/docs/platform/serialization/spec/input.html#a2971
     *
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        injectMembers(this);

        if (__cbTransientServiceFields != null && !__cbTransientServiceFields.isEmpty()) {
            Set<String> fieldNames = __cbTransientServiceFields.keySet();

            for (String fieldName : fieldNames) {
                Reflect.setField(this, fieldName, app.inject(__cbTransientServiceFields.get(fieldName)));
            }
        }

        afterReadingObject();
    }

    /**
     * A model-class should implement this method if any custom logic is to be
     * invoked after deserialization.
     */
    protected void afterReadingObject() {

    }
}
