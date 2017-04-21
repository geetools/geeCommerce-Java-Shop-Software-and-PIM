package com.geecommerce.core.system.attribute.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.boon.Str;

import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.enums.InputType;
import com.geecommerce.core.service.AbstractModel;
import com.geecommerce.core.service.annotation.Column;
import com.geecommerce.core.service.annotation.Model;
import com.geecommerce.core.system.attribute.repository.AttributeOptions;
import com.geecommerce.core.system.attribute.repository.Attributes;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.ContextObjects;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.type.TypeConverter;
import com.google.inject.Inject;
import com.owlike.genson.annotation.JsonIgnore;

@Model
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "attributeValue")
public class DefaultAttributeValue extends AbstractModel implements AttributeValue {
    private static final long serialVersionUID = -8455439478707707968L;

    @Column(Col.ATTRIBUTE_ID)
    protected Id attributeId = null;

    @Column(Col.VALUE)
    protected ContextObject<?> value = null;

    @Column(Col.OPTION_ID)
    protected List<Id> optionIds = null;

    @Column(Col.XOPTION_ID)
    protected ContextObject<List<Id>> xOptionIds = null;

    @Column(Col.SORT_ORDER)
    protected int sortOrder = 0;

    @Column(Col.PROPERTIES)
    protected Map<String, Object> properties = null;

    @Column(Col.OPT_OUT)
    protected ContextObject<Boolean> optOut = null;

    // Lazy-loaded objects
    @JsonIgnore
    protected transient Attribute attribute = null;

    @JsonIgnore(serialize = true)
    protected transient Map<Id, AttributeOption> attributeOptionsMap = null;

    // Attributes repository
    @JsonIgnore
    protected final transient Attributes attributes;

    // AttributeOptions repository
    @JsonIgnore
    protected final transient AttributeOptions attributeOptions;

    public DefaultAttributeValue() {
        this(i(Attributes.class), i(AttributeOptions.class));
    }

    @Inject
    public DefaultAttributeValue(Attributes attributes, AttributeOptions attributeOptions) {
        super();
        this.attributes = attributes;
        this.attributeOptions = attributeOptions;
    }

    public DefaultAttributeValue(Id attributeId, int sortOrder) {
        this(i(Attributes.class), i(AttributeOptions.class));

        this.attributeId = attributeId;
        this.sortOrder = sortOrder;
    }

    public DefaultAttributeValue(Id attributeId, ContextObject<?> value, int sortOrder) {
        this(i(Attributes.class), i(AttributeOptions.class));

        this.attributeId = attributeId;
        this.value = value;
        this.sortOrder = sortOrder;
    }

    public DefaultAttributeValue(Id attributeId, Id optionId, int sortOrder) {
        this(i(Attributes.class), i(AttributeOptions.class));

        this.attributeId = attributeId;
        this.optionIds = new ArrayList<>();
        this.optionIds.add(optionId);
        this.sortOrder = sortOrder;
    }

    @Override
    public Id getId() {
        return getAttributeId();
    }

    @Override
    public Id getAttributeId() {
        return attributeId;
    }

    @Override
    public AttributeValue setAttributeId(Id attributeId) {
        this.attributeId = attributeId;
        return this;
    }

    @Override
    public AttributeValue forAttribute(Attribute attribute) {
        if (attribute == null || attribute.getId() == null)
            throw new NullPointerException(
                "Attribute or id of attribute cannot be null when setting it in AttributeVaue object.");

        this.attribute = attribute;
        this.attributeId = attribute.getId();

        return this;
    }

    @Override
    public AttributeValue forAttribute(AttributeTargetObject targetObject, String attributeCode) {
        if (Str.isEmpty(attributeCode == null))
            throw new NullPointerException("Attribute code cannot be null or empty.");

        if (targetObject == null)
            throw new NullPointerException("Attribute target object cannot be null.");

        this.attribute = attributes.havingCode(targetObject, attributeCode);

        if (this.attribute == null)
            throw new IllegalArgumentException("Unable to locate attribute using code '" + attributeCode + "'.");

        this.attributeId = attribute.getId();

        return this;
    }

    @Override
    public AttributeValue forAttributeWithCode2(AttributeTargetObject targetObject, String attributeCode2) {
        if (Str.isEmpty(attributeCode2 == null))
            throw new NullPointerException("Attribute code2 cannot be null or empty.");

        if (targetObject == null)
            throw new NullPointerException("Attribute target object cannot be null.");

        this.attribute = attributes.havingCode2(targetObject, attributeCode2);

        if (this.attribute == null)
            throw new IllegalArgumentException("Unable to locate attribute using code2 '" + attributeCode2 + "'.");

        this.attributeId = attribute.getId();

        return this;
    }

    @JsonIgnore
    @Override
    public Attribute getAttribute() {
        if (this.attributeId == null)
            return null;

        if (this.attribute == null) {
            this.attribute = attributes.findById(Attribute.class, this.attributeId);
        }

        return this.attribute;
    }

    @JsonIgnore
    @Override
    public AttributeTargetObject getAttributeTargetObject() {
        if (this.attributeId == null)
            return null;

        return getAttribute().getTargetObject();
    }

    @JsonIgnore
    @Override
    public boolean attributeExists() {
        return getAttribute() != null;
    }

    @SuppressWarnings("unchecked")
    @JsonIgnore(serialize = true)
    @Override
    public Map<Id, AttributeOption> getAttributeOptions() {
        if (this.attributeOptionsMap == null && (this.optionIds != null && !this.optionIds.isEmpty())
            || (this.xOptionIds != null && !this.xOptionIds.isEmpty())) {
            List<Id> optionIds = new ArrayList<>();

            if (this.optionIds != null && !this.optionIds.isEmpty()) {
                optionIds = this.optionIds;
            } else {
                for (Map<String, Object> ctxMap : this.xOptionIds) {
                    Set<String> keys = ctxMap.keySet();

                    for (String key : keys) {
                        if (ContextObject.VALUE.equals(key)) {
                            Object val = ctxMap.get(ContextObject.VALUE);

                            if (val instanceof Collection) {
                                for (Id optionId : (Collection<Id>) val) {
                                    if (!optionIds.contains(optionId))
                                        optionIds.add(optionId);
                                }
                            } else if (val instanceof Id) {
                                Id optionId = (Id) val;

                                if (!optionIds.contains(optionId))
                                    optionIds.add(optionId);
                            }
                        }
                    }
                }
            }

            List<AttributeOption> attributeOptionsList = attributeOptions.findByIds(AttributeOption.class,
                optionIds.toArray(new Id[optionIds.size()]));

            if (attributeOptionsList != null && !attributeOptionsList.isEmpty()) {
                this.attributeOptionsMap = new LinkedHashMap<>();

                for (AttributeOption attributeOption : attributeOptionsList) {
                    if (attributeOption != null)
                        this.attributeOptionsMap.put(attributeOption.getId(), attributeOption);
                }
            }
        }

        return this.attributeOptionsMap;
    }

    @JsonIgnore()
    @Override
    public AttributeOption getFirstAttributeOption() {
        getAttributeOptions();

        if (this.attributeOptionsMap != null && this.attributeOptionsMap.size() > 0) {
            return this.attributeOptionsMap.values().iterator().next();
        }

        return null;
    }

    @JsonIgnore()
    @Override
    public AttributeOption getFirstXAttributeOption() {
        getAttributeOptions();

        ContextObject<List<Id>> ctxObj = getXOptionIds();
        List<Id> optionIds = ContextObjects.findCurrentStoreOrGlobal(ctxObj);

        if (optionIds != null && !optionIds.isEmpty()) {
            return this.attributeOptionsMap.get(optionIds.get(0));
        }

        return null;
    }

    @Override
    public String getLabel() {
        Attribute attr = getAttribute();

        if (attr != null) {
            if (attr.getFrontendLabel() != null) {
                return (String) attr.getFrontendLabel().getString();
            } else {
                return attr.getCode();
            }
        } else {
            return null;
        }
    }

    @Override
    public ContextObject<String> getBackendLabel() {
        Attribute attr = getAttribute();

        if (attr != null) {
            if (attr.getFrontendLabel() != null) {
                return attr.getBackendLabel();
            }
        }

        return null;
    }

    @Override
    public String getCode() {
        Attribute attr = getAttribute();

        if (attr != null) {
            return attr.getCode();
        } else {
            return null;
        }
    }

    @Override
    public Object getVal() {
        return getValue() == null ? null : getValue().getClosestValue();
    }

    @Override
    public Object getVal(Store store) {
        return store == null || getValue() == null ? null : getValue().getValueForStore(store.getId());
    }

    @JsonIgnore
    @Override
    public String firstLabel() {
        return getFirstLabel();
    }

    @JsonIgnore
    @Override
    public String getFirstLabel() {
        String s = null;

        if (getValue() != null) {
            s = getValue().str();
        } else {
            AttributeOption firstOption = getFirstAttributeOption();

            if (firstOption != null && firstOption.getLabel() != null) {
                s = firstOption.getLabel().str();
            } else {
                firstOption = getFirstXAttributeOption();

                if (firstOption != null && firstOption.getLabel() != null)
                    s = firstOption.getLabel().str();
            }
        }

        return s;
    }

    @JsonIgnore
    @Override
    public String firstLabel(String language) {
        String s = null;

        if (getValue() != null) {
            s = getValue().str(language);
        } else {
            AttributeOption firstOption = getFirstAttributeOption();

            if (firstOption != null) {
                s = firstOption.getLabel().str(language);
            } else {
                firstOption = getFirstXAttributeOption();

                s = firstOption.getLabel().str(language);
            }
        }

        return s;
    }

    @JsonIgnore
    @Override
    public String firstLabel(Store store) {
        Object o = null;

        if (getValue() != null) {
            o = getValue().getValueForStore(store.getId());
        } else {
            AttributeOption firstOption = getFirstAttributeOption();

            if (firstOption != null) {
                o = firstOption.getLabel().getValueForStore(store.getId());
            } else {
                firstOption = getFirstXAttributeOption();

                o = firstOption.getLabel().getValueForStore(store.getId());
            }
        }

        return o == null ? null : o.toString();
    }

    @JsonIgnore
    @Override
    public String firstLabel(Store store, String language) {
        Object o = null;

        if (getValue() != null) {
            o = getValue().getValueForStore(store.getId(), language);
        } else {
            AttributeOption firstOption = getFirstAttributeOption();

            if (firstOption != null) {
                o = firstOption.getLabel().getValueForStore(store.getId(), language);
            } else {
                firstOption = getFirstXAttributeOption();

                o = firstOption.getLabel().getValueForStore(store.getId(), language);
            }
        }

        return o == null ? null : o.toString();
    }

    @Override
    public List<String> allLabels() {
        return getAllLabels();
    }

    @Override
    public List<String> getAllLabels() {
        List<String> sList = new ArrayList<>();
        String s = null;

        if (getValue() != null) {
            s = getValue().str();
            if (s != null)
                sList.add(s);
        } else {
            Map<Id, AttributeOption> options = getAttributeOptions();
            if (options != null) {
                for (AttributeOption option : options.values()) {
                    if (option != null && option.getLabel() != null) {
                        s = option.getLabel().str();
                        if (s != null)
                            sList.add(s);
                    }
                }
            }
        }

        return sList;
    }

    @JsonIgnore
    @Override
    public String getString() {
        return getValue() == null ? null : getValue().getString();
    }

    @JsonIgnore
    @Override
    public String getString(String language) {
        return getValue() == null ? null : getValue().str(language);
    }

    @JsonIgnore
    @Override
    public String getString(Store store) {
        return store == null || getValue() == null ? null : (String) getValue().getValueForStore(store.getId());
    }

    @JsonIgnore
    @Override
    public String str(String language) {
        return getString(language);
    }

    @JsonIgnore
    @Override
    public String getStr() {
        return getString();
    }

    @JsonIgnore
    @Override
    public Double getDouble() {
        return getValue() == null ? null : getValue().getDouble();
    }

    @JsonIgnore
    @Override
    public Number getNumber() {
        return getLong();
    }

    @JsonIgnore
    @Override
    public Long getLong() {
        return getValue() == null ? null : getValue().getLong();
    }

    @JsonIgnore
    @Override
    public Long getLong(Store store) {
        return store == null || getValue() == null ? null : getValue().getLong(store.getId());
    }

    @JsonIgnore
    @Override
    public Integer getInteger() {
        return getValue() == null ? null : getValue().getInteger();
    }

    @JsonIgnore
    @Override
    public Integer getInteger(Store store) {
        return store == null || getValue() == null ? null : getValue().getInteger(store.getId());
    }

    @JsonIgnore
    @Override
    public Float getFloat() {
        return getValue() == null ? null : getValue().getFloat();
    }

    @JsonIgnore
    @Override
    public Boolean getBoolean() {
        return getValue() == null ? null : getValue().getBoolean();
    }

    @JsonIgnore
    @Override
    public Date getDate() {
        return getValue() == null ? null : getValue().getDate();
    }

    @Override
    public boolean hasValueFor(Store store) {
        if (value == null || value.isEmpty() || store == null)
            return false;

        return value.hasEntryForStore(store.getId());
    }

    @Override
    public boolean hasValueForCurrentStore() {
        if (value == null || value.isEmpty())
            return false;

        ApplicationContext appCtx = app.context();
        Store store = appCtx.getStore();

        if (store == null)
            return false;

        return value.hasEntryForStore(store.getId());
    }

    @SuppressWarnings("unchecked")
    @JsonIgnore
    @Override
    public ContextObject<? extends Object> getValue() {
        return value;
    }

    @Override
    public AttributeValue setValue(ContextObject<?> value) {
        if ((this.optionIds != null && this.optionIds.size() > 0)
            || (this.xOptionIds != null && this.xOptionIds.size() > 0)) {
            throw new IllegalStateException(
                "You may only specify optionIds OR xOptionIds OR a value per instance of AttributeValue and not in combination");
        }

        if (value != null) {
            this.value = value;
        }

        return this;
    }

    @Override
    public AttributeValue setValue(String language, String value) {
        if ((this.optionIds != null && this.optionIds.size() > 0)
            || (this.xOptionIds != null && this.xOptionIds.size() > 0)) {
            throw new IllegalStateException(
                "You may only specify optionIds OR xOptionIds OR a value per instance of AttributeValue and not in combination");
        }

        if (value != null) {
            this.value = new ContextObject<>(language, value);
        }

        return this;
    }

    @Override
    public AttributeValue setSimpleValue(Object value) {
        if ((this.optionIds != null && this.optionIds.size() > 0)
            || (this.xOptionIds != null && this.xOptionIds.size() > 0)) {
            throw new IllegalStateException(
                "You may only specify optionIds OR xOptionIds OR a value per instance of AttributeValue and not in combination");
        }

        if (value != null) {
            Object v = toAttributeType(value);

            if (v != null) {
                this.value = new ContextObject<>(v);
            }
        }

        return this;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public AttributeValue setStoreValue(Object value, Store store) {
        if ((this.optionIds != null && this.optionIds.size() > 0)
            || (this.xOptionIds != null && this.xOptionIds.size() > 0)) {
            throw new IllegalStateException(
                "You may only specify optionIds OR xOptionIds OR a value per instance of AttributeValue and not in combination");
        }

        if (value != null) {
            Object v = toAttributeType(value);

            if (v != null) {
                if (this.value != null) {
                    ContextObject ctxObj = this.value;
                    ctxObj.addOrUpdateForStore(store.getId(), v);
                } else {
                    this.value = new ContextObject<>().addForStore(store.getId(), v);
                }
            }
        }

        return this;
    }

    @Override
    public boolean hasOptionId(Id optionId) {
        return optionIds != null && optionIds.contains(optionId);
    }

    @Override
    public Id getOptionId() {
        return optionIds != null && optionIds.size() > 0 ? optionIds.iterator().next() : null;
    }

    @Override
    public List<Id> getOptionIds() {
        return optionIds;
    }

    @Override
    public AttributeValue setOptionId(Id optionId) {
        if ((this.value != null && this.value.size() > 0) || (this.xOptionIds != null && this.xOptionIds.size() > 0)) {
            throw new IllegalStateException(
                "You may only specify optionIds OR xOptionIds OR a value per instance of AttributeValue and not in combination");
        }

        if (optionId != null) {
            this.optionIds = new ArrayList<>();
            this.optionIds.add(optionId);

        }

        return this;
    }

    @Override
    public AttributeValue setOptionIds(List<Id> optionIds) {
        if ((this.value != null && this.value.size() > 0) || (this.xOptionIds != null && this.xOptionIds.size() > 0)) {
//            throw new IllegalStateException(
//                "You may only specify optionIds OR xOptionIds OR a value per instance of AttributeValue and not in combination");
            // TODO!!!

            // Temporary fix for combobox.
            this.value = null;
            this.xOptionIds = null;
        }

        this.optionIds = optionIds;

        return this;
    }

    @Override
    public AttributeValue addOptionId(Id optionId) {
        if ((this.value != null && this.value.size() > 0) || (this.xOptionIds != null && this.xOptionIds.size() > 0)) {
            throw new IllegalStateException(
                "You may only specify optionIds OR xOptionIds OR a value per instance of AttributeValue and not in combination");
        }

        if (optionId != null) {
            if (this.optionIds == null) {
                this.optionIds = new ArrayList<>();
            }

            if (!this.optionIds.contains(optionId))
                this.optionIds.add(optionId);
        }

        return this;
    }

    @Override
    public AttributeValue addOptionIds(List<Id> newOptionIds) {
        if ((this.value != null && this.value.size() > 0) || (this.xOptionIds != null && this.xOptionIds.size() > 0)) {
            throw new IllegalStateException(
                "You may only specify optionIds OR xOptionIds OR a value per instance of AttributeValue and not in combination");
        }

        if (newOptionIds != null && !newOptionIds.isEmpty()) {
            if (this.optionIds == null)
                this.optionIds = new ArrayList<>();

            for (Id optionId : newOptionIds) {
                if (!this.optionIds.contains(optionId))
                    this.optionIds.add(optionId);
            }
        }

        return this;
    }

    @Override
    public boolean hasXOptionIdFor(Store store) {
        if (xOptionIds == null || xOptionIds.isEmpty() || store == null)
            return false;

        return xOptionIds.hasEntryForStore(store.getId());
    }

    @Override
    public boolean hasXOptionIdForCurrentStore() {
        if (xOptionIds == null || xOptionIds.isEmpty())
            return false;

        ApplicationContext appCtx = app.context();
        Store store = appCtx.getStore();

        if (store == null)
            return false;

        return xOptionIds.hasEntryForStore(store.getId());
    }

    @Override
    public ContextObject<List<Id>> getXOptionIds() {
        return xOptionIds;
    }

    @Override
    public AttributeValue setXOptionId(Id optionId, Store store) {
        if ((this.value != null && this.value.size() > 0) || (this.optionIds != null && this.optionIds.size() > 0)) {
            throw new IllegalStateException(
                "You may only specify optionIds OR xOptionIds OR a value per instance of AttributeValue and not in combination");
        }

        if (store == null || store.getId() == null)
            throw new IllegalStateException("This method can only be used with a valid store object");

        if (optionId != null && store != null) {
            if (this.xOptionIds == null) {
                List<Id> optionIds = new ArrayList<>();
                optionIds.add(optionId);

                this.xOptionIds = new ContextObject<>();
                this.xOptionIds.addForStore(store.getId(), optionIds);
            } else {
                List<Id> optionIds = this.xOptionIds.getValueForStore(store.getId());

                if (optionIds == null) {
                    optionIds = new ArrayList<>();
                    optionIds.add(optionId);

                    this.xOptionIds.addOrUpdateForStore(store.getId(), optionIds);
                } else {
                    optionIds.clear();
                    optionIds.add(optionId);
                }
            }
        }

        return this;
    }

    @Override
    public AttributeValue setXOptionIds(List<Id> optionIds, Store store) {
        if ((this.value != null && this.value.size() > 0) || (this.optionIds != null && this.optionIds.size() > 0)) {
            throw new IllegalStateException(
                "You may only specify optionIds OR xOptionIds OR a value per instance of AttributeValue and not in combination");
        }

        if (store == null || store.getId() == null)
            throw new IllegalStateException("This method can only be used with a valid store object");

        if (optionIds != null && !optionIds.isEmpty() && store != null) {
            if (this.xOptionIds == null) {
                this.xOptionIds = new ContextObject<>();
                this.xOptionIds.addForStore(store.getId(), optionIds);
            } else {
                this.xOptionIds.addOrUpdateForStore(store.getId(), optionIds);
            }
        }

        return this;
    }

    @Override
    public AttributeValue setXOptionIds(ContextObject<List<Id>> xOptionIds) {
        if ((this.value != null && this.value.size() > 0) || (this.optionIds != null && this.optionIds.size() > 0)) {
            throw new IllegalStateException(
                "You may only specify optionIds OR xOptionIds OR a value per instance of AttributeValue and not in combination");
        }

        this.xOptionIds = xOptionIds;

        return this;
    }

    @Override
    public AttributeValue addXOptionId(Id optionId, Store store) {
        if ((this.value != null && this.value.size() > 0) || (this.optionIds != null && this.optionIds.size() > 0)) {
            throw new IllegalStateException(
                "You may only specify optionIds OR xOptionIds OR a value per instance of AttributeValue and not in combination");
        }

        if (store == null || store.getId() == null)
            throw new IllegalStateException("This method can only be used with a valid store object");

        if (optionId != null && store != null) {
            if (this.xOptionIds == null) {
                List<Id> optionIds = new ArrayList<>();
                optionIds.add(optionId);

                this.xOptionIds = new ContextObject<>();
                this.xOptionIds.addForStore(store.getId(), optionIds);
            } else {
                List<Id> optionIds = this.xOptionIds.getValueForStore(store.getId());

                if (optionIds == null) {
                    optionIds = new ArrayList<>();
                    optionIds.add(optionId);

                    this.xOptionIds.addOrUpdateForStore(store.getId(), optionIds);
                } else if (!optionIds.contains(optionId)) {
                    optionIds.add(optionId);
                }
            }
        }

        return this;
    }

    @Override
    public AttributeValue addXOptionIds(List<Id> newOptionIds, Store store) {
        if ((this.value != null && this.value.size() > 0) || (this.optionIds != null && this.optionIds.size() > 0)) {
            throw new IllegalStateException(
                "You may only specify optionIds OR xOptionIds OR a value per instance of AttributeValue and not in combination");
        }

        if (store == null || store.getId() == null)
            throw new IllegalStateException("This method can only be used with a valid store object");

        if (newOptionIds != null && !newOptionIds.isEmpty()) {
            if (this.xOptionIds == null) {
                List<Id> optionIds = new ArrayList<>();
                optionIds.addAll(newOptionIds);

                this.xOptionIds = new ContextObject<>();
                this.xOptionIds.addForStore(store.getId(), optionIds);
            } else {
                List<Id> optionIds = this.xOptionIds.getValueForStore(store.getId());

                if (optionIds == null) {
                    optionIds = new ArrayList<>();
                    optionIds.addAll(newOptionIds);

                    this.xOptionIds.addOrUpdateForStore(store.getId(), optionIds);
                } else {
                    for (Id optionId : newOptionIds) {
                        if (!optionIds.contains(optionId))
                            optionIds.add(optionId);
                    }
                }
            }
        }

        return this;
    }

    @Override
    public int getSortOrder() {
        return sortOrder;
    }

    @Override
    public AttributeValue setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    @Override
    public AttributeValue addProperty(String key, Object value) {
        if (this.properties == null) {
            this.properties = new HashMap<>();
        }

        if (key != null && value != null) {
            this.properties.put(key, value);
        }

        return this;
    }

    @Override
    public Object getProperty(String key) {
        if (this.properties == null || this.properties.size() == 0) {
            return null;
        }

        if (key != null) {
            return this.properties.get(key);
        }

        return null;
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public AttributeValue setProperties(Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public boolean hasProperty(String key) {
        return getProperty(key) != null;
    }

    @Override
    public AttributeValue removeProperty(String key) {
        if (hasProperty(key))
            this.properties.remove(key);

        return this;
    }

    @Override
    public ContextObject<Boolean> getOptOut() {
        return optOut;
    }

    @Override
    public AttributeValue setOptOut(ContextObject<Boolean> optOut) {
        this.optOut = optOut;
        return this;
    }

    @Override
    public AttributeValue setGlobalOptOut(boolean optOut) {
        this.optOut = ContextObjects.global(optOut);
        return this;
    }

    @Override
    public AttributeValue setOptOut(boolean optOut, Store store) {
        if (this.optOut == null)
            this.optOut = new ContextObject<>();

        this.optOut.addOrUpdateForStore(store.getId(), optOut);

        return this;
    }

    @Override
    public AttributeValue setOptOut(boolean optOut, String languageCode) {
        if (this.optOut == null)
            this.optOut = new ContextObject<>();

        this.optOut.addOrUpdate(languageCode, optOut);

        return this;
    }

    @Override
    public boolean isOptOut() {
        if (this.optOut == null)
            return false;

        Attribute attr = getAttribute();

        if (InputType.OPTOUT != attr.getInputType())
            return false;

        Boolean optOut = this.optOut.getBoolean();

        return optOut == null ? false : optOut.booleanValue();
    }

    @Override
    public boolean isOptOut(Store store) {
        if (this.optOut == null || store == null)
            return false;

        Attribute attr = getAttribute();

        if (InputType.OPTOUT != attr.getInputType())
            return false;

        Boolean optOut = this.optOut.getBoolean(store.getId());

        return optOut == null ? false : optOut.booleanValue();
    }

    @Override
    public boolean isOptOut(String languageCode) {
        if (this.optOut == null || languageCode == null)
            return false;

        Attribute attr = getAttribute();

        if (InputType.OPTOUT != attr.getInputType())
            return false;

        Boolean optOut = (Boolean) this.optOut.getValueFor(languageCode);

        return optOut == null ? false : optOut.booleanValue();
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        this.attributeId = id_(map.get(Col.ATTRIBUTE_ID));
        this.value = ctxObj_(map.get(Col.VALUE));
        this.optionIds = idList_(map.get(Col.OPTION_ID));
        this.xOptionIds = ctxObj_(map.get(Col.XOPTION_ID), true);
        this.sortOrder = int_(map.get(Col.SORT_ORDER), 0);
        this.properties = map_(map.get(Col.PROPERTIES));
        this.optOut = ctxObj_(map.get(Col.OPT_OUT));
    }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put(Col.ATTRIBUTE_ID, getAttributeId());

        if (getValue() != null)
            m.put(Col.VALUE, getValue());

        if (getOptionIds() != null)
            m.put(Col.OPTION_ID, getOptionIds());

        if (getXOptionIds() != null)
            m.put(Col.XOPTION_ID, getXOptionIds());

        m.put(Col.SORT_ORDER, getSortOrder());

        if (getProperties() != null)
            m.put(Col.PROPERTIES, getProperties());

        if (getOptOut() != null)
            m.put(Col.OPT_OUT, getOptOut());

        return m;
    }

    @SuppressWarnings("unchecked")
    private Object toAttributeType(Object object) {
        if (object == null)
            return null;

        Attribute attr = getAttribute();

        if (attr == null)
            throw new IllegalStateException("Attribute cannot be null");

        if (attr.getBackendType() == null && (object instanceof Number || object instanceof String
            || object instanceof Boolean || object instanceof Date || object instanceof Id)) {
            return object;
        } else if (attr.getBackendType() == null) {
            throw new IllegalStateException("The value '" + object + "' for attribute '" + attr.getCode()
                + "' cannot be converted to specific type as no backend-type has been configured. This only works for standard objects such as Number, String, Boolean, Date, Id");
        }

        if (attr.isAllowMultipleValues() && object instanceof List) {
            List<Object> l = (List<Object>) object;

            List<Object> convertedList = new ArrayList<>();

            for (Object obj : l) {
                switch (attr.getBackendType()) {
                case BOOLEAN:
                    convertedList.add(TypeConverter.asBoolean(obj));
                    break;
                case DATE:
                    convertedList.add(TypeConverter.asDate(obj));
                    break;
                case DOUBLE:
                    convertedList.add(TypeConverter.asDouble(obj));
                    break;
                case FLOAT:
                    convertedList.add(TypeConverter.asFloat(obj));
                    break;
                case INTEGER:
                    convertedList.add(TypeConverter.asInteger(obj));
                    break;
                case LONG:
                    convertedList.add(TypeConverter.asLong(obj));
                    break;
                case SHORT:
                    convertedList.add(TypeConverter.asShort(obj));
                    break;
                case STRING:
                    convertedList.add(TypeConverter.asString(obj));
                    break;
                default:
                    convertedList.add(TypeConverter.asRaw(obj));
                    break;
                }
            }

            return convertedList;
        } else {
            switch (attr.getBackendType()) {
            case BOOLEAN:
                return TypeConverter.asBoolean(object);
            case DATE:
                return TypeConverter.asDate(object);
            case DOUBLE:
                return TypeConverter.asDouble(object);
            case FLOAT:
                return TypeConverter.asFloat(object);
            case INTEGER:
                return TypeConverter.asInteger(object);
            case LONG:
                return TypeConverter.asLong(object);
            case SHORT:
                return TypeConverter.asShort(object);
            case STRING:
                return TypeConverter.asString(object);
            default:
                return TypeConverter.asRaw(object);
            }
        }
    }

    @Override
    public String toString() {
        return "DefaultAttributeValue [attributeId=" + attributeId + ", value=" + value + ", optionIds=" + optionIds
            + ", xOptionIds=" + xOptionIds + ", sortOrder=" + sortOrder + ", properties=" + properties + ", optOut="
            + optOut + "]";
    }

    @Override
    public AttributeValue copy() {
        DefaultAttributeValue av = new DefaultAttributeValue();
        av.attributeId = attributeId;
        av.sortOrder = sortOrder;
        av.optOut = optOut;

        if (value != null)
            av.value = ContextObject.valueOf(value);

        if (optionIds != null)
            av.optionIds = new ArrayList<>(optionIds);

        if (xOptionIds != null)
            av.xOptionIds = ContextObject.valueOf(xOptionIds);

        if (properties != null)
            av.properties = new LinkedHashMap<>(properties);

        return av;
    }
}
