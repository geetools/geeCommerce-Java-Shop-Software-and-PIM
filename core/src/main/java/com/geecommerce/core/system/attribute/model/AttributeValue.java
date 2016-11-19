package com.geecommerce.core.system.attribute.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.owlike.genson.annotation.JsonIgnore;

public interface AttributeValue extends Model {
    public Id getAttributeId();

    @JsonIgnore
    public Attribute getAttribute();

    @JsonIgnore
    public AttributeTargetObject getAttributeTargetObject();

    @JsonIgnore
    public boolean attributeExists();

    public String getCode();

    @JsonIgnore
    public String getLabel();

    public ContextObject<String> getBackendLabel();

    public boolean isShowInProductDetails();

    @JsonIgnore(serialize = true)
    public Map<Id, AttributeOption> getAttributeOptions();

    @JsonIgnore
    public AttributeOption getFirstAttributeOption();

    @JsonIgnore
    public AttributeOption getFirstXAttributeOption();

    public AttributeValue forAttribute(Attribute attribute);

    public AttributeValue forAttribute(AttributeTargetObject targetObject, String attributeCode);

    public AttributeValue forAttributeWithCode2(AttributeTargetObject targetObject, String attributeCode2);

    public AttributeValue setAttributeId(Id attributeId);

    public boolean hasValueFor(Store store);

    public boolean hasValueForCurrentStore();

    public <T> ContextObject<T> getValue();

    @JsonIgnore
    public Object getVal();

    @JsonIgnore
    public Object getVal(Store store);

    @JsonIgnore
    public String firstLabel();

    @JsonIgnore
    public String getFirstLabel();

    @JsonIgnore
    public String firstLabel(String language);

    @JsonIgnore
    public String firstLabel(Store store);

    @JsonIgnore
    public String firstLabel(Store store, String language);

    @JsonIgnore
    public List<String> allLabels();

    @JsonIgnore
    public List<String> getAllLabels();

    @JsonIgnore
    public String getString();

    @JsonIgnore
    public String getString(String language);

    @JsonIgnore
    public String getString(Store store);

    @JsonIgnore
    public String getStr();

    @JsonIgnore
    public String str(String language);

    @JsonIgnore
    public Double getDouble();

    @JsonIgnore
    public Long getLong();

    @JsonIgnore
    public Long getLong(Store store);

    @JsonIgnore
    public Number getNumber();

    @JsonIgnore
    public Integer getInteger();

    @JsonIgnore
    public Integer getInteger(Store store);

    @JsonIgnore
    public Float getFloat();

    @JsonIgnore
    public Boolean getBoolean();

    @JsonIgnore
    public Date getDate();

    public AttributeValue setValue(String language, String value);

    public AttributeValue setSimpleValue(Object value);

    public AttributeValue setStoreValue(Object value, Store store);

    public AttributeValue setValue(ContextObject<?> value);

    public boolean hasOptionId(Id optionId);

    public Id getOptionId();

    public List<Id> getOptionIds();

    public AttributeValue setOptionId(Id optionId);

    public AttributeValue setOptionIds(List<Id> optionIds);

    public AttributeValue addOptionId(Id optionId);

    public AttributeValue addOptionIds(List<Id> newOptionIds);

    public boolean hasXOptionIdFor(Store store);

    public boolean hasXOptionIdForCurrentStore();

    public ContextObject<List<Id>> getXOptionIds();

    public AttributeValue setXOptionId(Id optionId, Store store);

    public AttributeValue setXOptionIds(ContextObject<List<Id>> xOptionIds);

    public AttributeValue addXOptionId(Id optionId, Store store);

    public AttributeValue setXOptionIds(List<Id> optionIds, Store store);

    public AttributeValue addXOptionIds(List<Id> newOptionIds, Store store);

    public int getSortOrder();

    public AttributeValue setSortOrder(int sortOrder);

    public AttributeValue addProperty(String key, Object value);

    public Object getProperty(String key);

    public Map<String, Object> getProperties();

    public AttributeValue setProperties(Map<String, Object> properties);

    public boolean hasProperty(String key);

    public AttributeValue removeProperty(String key);

    public ContextObject<Boolean> getOptOut();

    public AttributeValue setOptOut(ContextObject<Boolean> optOut);

    public AttributeValue setGlobalOptOut(boolean optOut);

    public AttributeValue setOptOut(boolean optOut, Store store);

    public AttributeValue setOptOut(boolean optOut, String languageCode);

    public boolean isOptOut();

    public boolean isOptOut(Store store);

    public boolean isOptOut(String languageCode);

    public AttributeValue copy();

    static final class Col {
        public static final String ATTRIBUTE_ID = "attr_id";
        public static final String VALUE = "val";
        public static final String OPTION_ID = "opt_id";
        public static final String XOPTION_ID = "xopt_id";
        public static final String SORT_ORDER = "order";
        public static final String PROPERTIES = "props";
        public static final String OPT_OUT = "opt_out";
    }
}
