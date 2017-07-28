package com.geecommerce.core.type;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Str;
import com.geecommerce.core.rest.jersey.adapter.ContextObjectAdapter;
import com.geecommerce.core.system.ConfigurationKey;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.merchant.model.View;
import com.geecommerce.core.system.model.RequestContext;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@XmlJavaTypeAdapter(ContextObjectAdapter.class)
public class ContextObject<T> extends ArrayList<Map<String, Object>> {
    private static final long serialVersionUID = -7977064503542096074L;

    // private boolean ignoreNull = false;

    public static final String MERCHANT = "m";
    public static final String STORE = "s";
    public static final String REQUEST_CONTEXT = "rc";
    public static final String LANGUAGE = "l";
    public static final String COUNTRY = "c";
    public static final String VIEW = "v";
    public static final String VALUE = "val";

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public ContextObject() {
        super();
    }

    public ContextObject(Map<String, Object> entry) {
        if (!isEntryValid(entry)) {
            // if (!ignoreNull)
            throw new IllegalArgumentException("ContextObject entry " + entry + " is invalid");
        } else {
            add(entry);
        }
    }

    public ContextObject(String value) {
        if (isXML(value)) {
            ContextObject<Object> ctxObj = ContextObject.fromXML(value);
            super.addAll(ctxObj);
        } else if (isJSON(value)) {
            ContextObject<Object> ctxObj = ContextObject.fromJSON(value);
            super.addAll(ctxObj);
        } else {
            add(toEntry(null, null, null, null, null, null, value));
        }
    }

    @SuppressWarnings("unchecked")
    public ContextObject(Object value) {
        add(toEntry(null, null, null, null, null, null, (T) value));
    }

    public ContextObject(String languageCode, T value) {
        add(toEntry(null, null, languageCode, null, null, null, value));
    }

    public ContextObject(String languageCode, String countryCode, T value) {
        add(toEntry(null, null, languageCode, countryCode, null, null, value));
    }

    @Override
    public void add(int index, Map<String, Object> element) {
        if (element != null)
            super.add(index, element);
    }

    @Override
    public boolean add(Map<String, Object> element) {
        if (element != null)
            return super.add(element);

        return false;
    }

    public ContextObject<T> addOrUpdateGlobal(T value) {
        return setGlobal(value, true);
    }

    public ContextObject<T> addGlobal(T value) {
        return setGlobal(value, false);
    }

    protected ContextObject<T> setGlobal(T value, boolean updateIfExists) {
        int hashCode = getGlobalEntryHash();

        if (hashCode == 0) {
            add(toEntry(null, null, null, null, null, null, value));
        } else {
            if (!updateIfExists)
                throw new IllegalStateException(
                    "Unable to update value to '" + value + "'. Use addOrUpdateGlobal instead.");

            updateValue(hashCode, value);
        }

        return this;
    }

    public ContextObject<T> addOrUpdate(String languageCode, T value) {
        return set(languageCode, value, true);
    }

    public ContextObject<T> add(String languageCode, T value) {
        return set(languageCode, value, false);
    }

    protected ContextObject<T> set(String languageCode, T value, boolean updateIfExists) {
        int hashCode = getEntryHashFor(languageCode);

        if (hashCode == 0) {
            add(toEntry(null, null, languageCode, null, null, null, value));
        } else {
            if (!updateIfExists)
                throw new IllegalStateException("Unable to update value to '" + value + "' for language '"
                    + languageCode + "'. Use addOrUpdate instead.");

            updateValue(hashCode, value);
        }

        return this;
    }

    public ContextObject<T> addOrUpdate(String languageCode, String countryCode, T value) {
        return set(languageCode, countryCode, value, true);
    }

    public ContextObject<T> add(String languageCode, String countryCode, T value) {
        return set(languageCode, countryCode, value, false);
    }

    protected ContextObject<T> set(String languageCode, String countryCode, T value, boolean updateIfExists) {
        int hashCode = getEntryHashFor(languageCode, countryCode);

        if (hashCode == 0) {
            add(toEntry(null, null, languageCode, countryCode, null, null, value));
        } else {
            if (!updateIfExists)
                throw new IllegalStateException("Unable to update value to '" + value + "' for language '"
                    + languageCode + "', country '" + countryCode + "'. Use addOrUpdate instead.");

            updateValue(hashCode, value);
        }

        return this;
    }

    public ContextObject<T> addOrUpdateForCountry(String country, T value) {
        return setForCountry(country, value, true);
    }

    public ContextObject<T> addForCountry(String country, T value) {
        return setForCountry(country, value, false);
    }

    protected ContextObject<T> setForCountry(String country, T value, boolean updateIfExists) {
        int hashCode = getEntryHashForCountry(country);

        if (hashCode == 0) {
            add(toEntry(null, null, null, country, null, null, value));
        } else {
            if (!updateIfExists)
                throw new IllegalStateException("Unable to update value to '" + value + "' for country '" + country
                    + "'. Use addOrUpdate instead.");

            updateValue(hashCode, value);
        }

        return this;
    }

    public ContextObject<T> addOrUpdateForView(Id viewId, T value) {
        return setForView(viewId, value, true);
    }

    public ContextObject<T> addForView(Id viewId, T value) {
        return setForView(viewId, value, false);
    }

    protected ContextObject<T> setForView(Id viewId, T value, boolean updateIfExists) {
        int hashCode = getEntryHashForView(viewId);

        if (hashCode == 0) {
            add(toEntry(null, null, null, null, viewId, null, value));
        } else {
            if (!updateIfExists)
                throw new IllegalStateException("Unable to update value to '" + value + "' for view '" + viewId
                    + "'. Use addOrUpdate instead.");

            updateValue(hashCode, value);
        }

        return this;
    }

    public ContextObject<T> addOrUpdateForView(Id viewId, String languageCode, T value) {
        return setForView(viewId, languageCode, value, true);
    }

    public ContextObject<T> addForView(Id viewId, String languageCode, T value) {
        return setForView(viewId, languageCode, value, false);
    }

    protected ContextObject<T> setForView(Id viewId, String languageCode, T value, boolean updateIfExists) {
        int hashCode = getEntryHashForView(viewId, languageCode);

        if (hashCode == 0) {
            add(toEntry(null, null, languageCode, null, viewId, null, value));
        } else {
            if (!updateIfExists)
                throw new IllegalStateException("Unable to update value to '" + value + "' for view '" + viewId
                    + "', language '" + languageCode + "'. Use addOrUpdate instead.");

            updateValue(hashCode, value);
        }

        return this;
    }

    public ContextObject<T> addOrUpdateForStore(Id storeId, T value) {
        return setForStore(storeId, value, true);
    }

    public ContextObject<T> addForStore(Id storeId, T value) {
        return setForStore(storeId, value, false);
    }

    protected ContextObject<T> setForStore(Id storeId, T value, boolean updateIfExists) {
        int hashCode = getEntryHashForStore(storeId);

        if (hashCode == 0) {
            add(toEntry(null, storeId, null, null, null, null, value));
        } else {
            if (!updateIfExists)
                throw new IllegalStateException("Unable to update value to '" + value + "' for store '" + storeId
                    + "'. Use addOrUpdate instead.");

            updateValue(hashCode, value);
        }

        return this;
    }

    public ContextObject<T> addOrUpdateForStore(Id storeId, String languageCode, T value) {
        return setForStore(storeId, languageCode, value, true);
    }

    public ContextObject<T> addForStore(Id storeId, String languageCode, T value) {
        return setForStore(storeId, languageCode, value, false);
    }

    protected ContextObject<T> setForStore(Id storeId, String languageCode, T value, boolean updateIfExists) {
        int hashCode = getEntryHashForStore(storeId, languageCode);

        if (hashCode == 0) {
            add(toEntry(null, storeId, languageCode, null, null, null, value));
        } else {
            if (!updateIfExists)
                throw new IllegalStateException("Unable to update value to '" + value + "' for store '" + storeId
                    + "', language '" + languageCode + "'. Use addOrUpdate instead.");

            updateValue(hashCode, value);
        }

        return this;
    }

    public ContextObject<T> addOrUpdateForMerchant(Id merchantId, T value) {
        return setForMerchant(merchantId, value, true);
    }

    public ContextObject<T> addForMerchant(Id merchantId, T value) {
        return setForMerchant(merchantId, value, false);
    }

    protected ContextObject<T> setForMerchant(Id merchantId, T value, boolean updateIfExists) {
        int hashCode = getEntryHashForMerchant(merchantId);

        if (hashCode == 0) {
            add(toEntry(merchantId, null, null, null, null, null, value));
        } else {
            if (!updateIfExists)
                throw new IllegalStateException("Unable to update value to '" + value + "' for merchant '" + merchantId
                    + "'. Use addOrUpdate instead.");

            updateValue(hashCode, value);
        }

        return this;
    }

    public ContextObject<T> addOrUpdateForMerchant(Id merchantId, String languageCode, T value) {
        return setForMerchant(merchantId, languageCode, value, true);
    }

    public ContextObject<T> addForMerchant(Id merchantId, String languageCode, T value) {
        return setForMerchant(merchantId, languageCode, value, false);
    }

    protected ContextObject<T> setForMerchant(Id merchantId, String languageCode, T value, boolean updateIfExists) {
        int hashCode = getEntryHashForMerchant(merchantId, languageCode);

        if (hashCode == 0) {
            add(toEntry(merchantId, null, languageCode, null, null, null, value));
        } else {
            if (!updateIfExists)
                throw new IllegalStateException("Unable to update value to '" + value + "' for merchant '" + merchantId
                    + "', language '" + languageCode + "'. Use addOrUpdate instead.");

            updateValue(hashCode, value);
        }

        return this;
    }

    public ContextObject<T> addOrUpdateForRequestContext(Id requestContextId, T value) {
        return setForRequestContext(requestContextId, value, true);
    }

    public ContextObject<T> addForRequestContext(Id requestContextId, T value) {
        return setForRequestContext(requestContextId, value, false);
    }

    protected ContextObject<T> setForRequestContext(Id requestContextId, T value, boolean updateIfExists) {
        int hashCode = getEntryHashForRequestContext(requestContextId);

        if (hashCode == 0) {
            add(toEntry(null, null, null, null, null, requestContextId, value));
        } else {
            if (!updateIfExists)
                throw new IllegalStateException("Unable to update value to '" + value + "' for request-context '"
                    + requestContextId + "'. Use addOrUpdate instead.");

            updateValue(hashCode, value);
        }

        return this;
    }

    public ContextObject<T> removeEntry(int hashCode) {
        int idxToRemove = -1;

        for (int i = 0; i < this.size(); i++) {
            Map<String, Object> entry = this.get(i);
            if (entry != null && entryHashCode(entry) == hashCode) {
                idxToRemove = i;
                break;
            }
        }

        if (idxToRemove > -1)
            this.remove(idxToRemove);

        return this;
    }

    public ContextObject<T> updateValue(int hashCode, Object value) {
        Map<String, Object> entry = findEntryForHashCode(hashCode);

        if (entry != null) {
            entry.put(VALUE, value);
        } else {
            throw new IllegalStateException(
                "Unable to find entry '" + hashCode + "' for updating with new value '" + value + "'");
        }

        return this;
    }

    public ContextObject<T> merge(ContextObject<T> ctxObj) {
        return merge(ctxObj, true);
    }

    public ContextObject<T> merge(ContextObject<T> otherCtxObj, boolean overrideExistingValues) {
        Set<Integer> matchingHashCodes = Sets.newHashSet();

        for (Map<String, Object> thisEntry : this) {
            for (Map<String, Object> otherEntry : otherCtxObj) {
                if (entryHashCode(thisEntry) == entryHashCode(otherEntry)) {
                    matchingHashCodes.add(entryHashCode(otherEntry));
                    break;
                }
            }
        }

        // If all new entries have a new scope, just insert them.
        if (matchingHashCodes.size() == 0) {
            this.addAll(otherCtxObj.subList(0, otherCtxObj.size()));
        }
        // Values are allowed to be overridden if they exist.
        else if (matchingHashCodes.size() > 0 && overrideExistingValues) {
            // Remove already existing scopes, as we will add them again with
            // the new values in the next step.
            for (Integer hashCode : matchingHashCodes) {
                this.removeEntry(hashCode);
            }

            this.addAll(otherCtxObj.subList(0, otherCtxObj.size()));
        }
        // We do not override values here.
        else if (matchingHashCodes.size() > 0 && !overrideExistingValues) {
            List<Map<String, Object>> otherList = otherCtxObj.subList(0, otherCtxObj.size());

            for (Map<String, Object> otherEntry : otherList) {
                // As we do not override here, only insert if the hash does not
                // exist yet.
                if (!hasEntryForHash(entryHashCode(otherEntry))) {
                    add(otherEntry);
                }
            }
        }

        return this;
    }

    public String getString() {
        T v = getClosestValue();

        if (v == null) {
            boolean fallbackToEmptyString = App.get().cpBool_(ConfigurationKey.I18N_FALLBACK_TO_EMPTY_STRING, false);

            if (fallbackToEmptyString) {
                return "";
            } else {
                return null;
            }
        } else {
            return String.valueOf(v);
        }
    }

    public String str() {
        return getString();
    }

    public String getStr() {
        return getString();
    }

    public String str(final String language) {
        return (String) getValueFor(language);
    }

    public String getStr(final String language) {
        return (String) getValueFor(language);
    }

    public String str(final String language, final String country) {
        return (String) getValueFor(language, country);
    }

    public String getStr(final String language, final String country) {
        return (String) getValueFor(language, country);
    }

    public Double getDouble() {
        return TypeConverter.asDouble(getClosestValue());
    }

    public Long getLong() {
        return TypeConverter.asLong(getClosestValue());
    }

    public Integer getInteger() {
        return TypeConverter.asInteger(getClosestValue());
    }

    public Float getFloat() {
        return TypeConverter.asFloat(getClosestValue());
    }

    public Boolean getBoolean() {
        return TypeConverter.asBoolean(getClosestValue());
    }

    public Date getDate() {
        return TypeConverter.asDate(getClosestValue());
    }

    public Double getDouble(Id storeId) {
        return TypeConverter.asDouble(getValueForStore(storeId));
    }

    public Long getLong(Id storeId) {
        return TypeConverter.asLong(getValueForStore(storeId));
    }

    public Integer getInteger(Id storeId) {
        return TypeConverter.asInteger(getValueForStore(storeId));
    }

    public Float getFloat(Id storeId) {
        return TypeConverter.asFloat(getValueForStore(storeId));
    }

    public Boolean getBoolean(Id storeId) {
        return TypeConverter.asBoolean(getValueForStore(storeId));
    }

    public Date getDate(Id storeId) {
        return TypeConverter.asDate(getValueForStore(storeId));
    }

    public T getVal() {
        return getClosestValue();
    }

    public T getClosestValue() {
        return getClosestValue(null);
    }

    public T getClosestValue(final String language) {
        return getClosestValue(language, null);
    }

    @SuppressWarnings("unchecked")
    protected T getClosestValue(final String language, final String country) {
        App app = App.get();

        ApplicationContext appCtx = app.context();

        RequestContext reqCtx = appCtx.getRequestContext();
        Merchant merchant = appCtx.getMerchant();
        Store store = appCtx.getStore();
        View view = appCtx.getView();

        // Use language from context or parameter

        String languageCode = null;

        if (language == null) {
            languageCode = appCtx.getLanguage();
        } else {
            languageCode = language;
        }

        // Use country from context or parameter

        String countryCode = null;

        if (language == null) {
            countryCode = appCtx.getCountry();
        } else {
            countryCode = country;
        }

        // -----------------------------------------------------
        // Search from most specific to least specific context
        // -----------------------------------------------------

        // View, language
        Map<String, Object> m = null;

        if (reqCtx != null)
            m = findEntryForRequestContext(reqCtx.getId());

        if (m == null && view != null)
            m = findEntryForView(view.getId(), languageCode);

        // Store, language
        if (m == null && store != null)
            m = findEntryForStore(store.getId(), languageCode);

        // Merchant, language
        if (m == null && merchant != null)
            m = findEntryForMerchant(merchant.getId(), languageCode);

        // View
        if (m == null && view != null)
            m = findEntryForView(view.getId());

        // Store
        if (m == null && store != null)
            m = findEntryForStore(store.getId());

        // Merchant
        if (m == null && merchant != null)
            m = findEntryForMerchant(merchant.getId());

        // Language, country
        if (m == null)
            m = findEntryFor(languageCode, countryCode);

        // Language
        if (m == null)
            m = findEntryFor(languageCode);

        // Global
        if (m == null) {
            m = findGlobalEntry();
        }

        if (m != null) {
            return (T) m.get(VALUE);
        }
        // If value has not been found, attempt to find a fallback language
        else {
            boolean fallbackToDefaultLanguage = app.cpBool_(ConfigurationKey.I18N_FALLBACK_TO_DEFAULT_LANGUAGE, false);
            // boolean fallbackToAllLanguages =
            // app.cpBool_(ConfigurationProperty.Key.FALLBACK_TO_ALL_LANGUAGES,
            // false);
            String defaultLanguage = app.cpStr_(ConfigurationKey.I18N_DEFAULT_LANGUAGE);

            if (language == null && fallbackToDefaultLanguage && defaultLanguage != null) {
                // try again with the default language
                return getClosestValue(defaultLanguage);
            }
            // else if (fallbackToAllLanguages)
            // {
            // return Json.toJson(this.toArray());
            // }
            else {
                return null;
            }
        }
    }

    public Map<String, Object> getClosestEntry() {
        ApplicationContext appCtx = App.get().context();

        Merchant merchant = appCtx.getMerchant();
        Store store = appCtx.getStore();
        View view = appCtx.getView();
        // View
        Map<String, Object> m = findEntryForView(view.getId());

        // Store
        if (m == null)
            m = findEntryForStore(store.getId());

        // Merchant
        if (m == null)
            m = findEntryForMerchant(merchant.getId());

        // Global
        if (m == null) {
            m = findGlobalEntry();
        }

        return m;
    }

    public Map<String, Object> findEntryForHashCode(int hashCode) {
        for (Map<String, Object> entry : this) {
            if (entry != null && entryHashCode(entry) == hashCode)
                return entry;
        }

        return null;
    }

    public boolean hasGlobalEntry() {
        return findGlobalEntry() != null;
    }

    public boolean hasEntryForHash(int hashCode) {
        return findEntryForHashCode(hashCode) != null;
    }

    public boolean hasEntryFor(String language) {
        return findEntryFor(language) != null;
    }

    public boolean hasEntryFor(String language, String country) {
        return findEntryFor(language, country) != null;
    }

    public boolean hasEntryForView(Id viewId) {
        return findEntryForView(viewId) != null;
    }

    public boolean hasEntryForView(Id viewId, String language) {
        return findEntryForView(viewId, language) != null;
    }

    public boolean hasEntryForStore(Id storeId) {
        return findEntryForStore(storeId) != null;
    }

    public boolean hasEntryForStore(Id storeId, String language) {
        return findEntryForStore(storeId, language) != null;
    }

    public boolean hasEntryForMerchant(Id merchantId) {
        return findEntryForMerchant(merchantId) != null;
    }

    public boolean hasEntryForMerchant(Id merchantId, String language) {
        return findEntryForMerchant(merchantId, language) != null;
    }

    public Map<String, Object> findGlobalEntry() {
        for (Map<String, Object> m : this) {
            if (m.get(MERCHANT) == null && m.get(STORE) == null && m.get(VIEW) == null && m.get(COUNTRY) == null
                && m.get(LANGUAGE) == null && m.get(REQUEST_CONTEXT) == null) {
                return m;
            }
        }

        return null;
    }

    public Map<String, Object> findEntryFor(String language) {
        for (Map<String, Object> m : this) {
            if (m.get(MERCHANT) == null && m.get(STORE) == null && m.get(VIEW) == null && m.get(COUNTRY) == null
                && m.get(LANGUAGE) != null && m.get(LANGUAGE).equals(language) && m.get(REQUEST_CONTEXT) == null) {
                return m;
            }
        }

        return null;
    }

    public Map<String, Object> findEntryFor(String language, String country) {
        for (Map<String, Object> m : this) {
            if (m.get(MERCHANT) == null && m.get(STORE) == null && m.get(VIEW) == null && m.get(COUNTRY) != null
                && m.get(COUNTRY).equals(country) && m.get(LANGUAGE) != null && m.get(LANGUAGE).equals(language)
                && m.get(REQUEST_CONTEXT) == null) {
                return m;
            }
        }

        return null;
    }

    public Map<String, Object> findEntryForCountry(String country) {
        for (Map<String, Object> m : this) {
            if (m.get(MERCHANT) == null && m.get(STORE) == null && m.get(VIEW) == null && m.get(COUNTRY) != null
                && m.get(COUNTRY).equals(country) && m.get(LANGUAGE) == null && m.get(REQUEST_CONTEXT) == null) {
                return m;
            }
        }

        return null;
    }

    public Map<String, Object> findEntryForView(Id viewId) {
        for (Map<String, Object> m : this) {
            if (m.get(MERCHANT) == null && m.get(STORE) == null && m.get(VIEW) != null && m.get(VIEW).equals(viewId)
                && m.get(COUNTRY) == null && m.get(LANGUAGE) == null && m.get(REQUEST_CONTEXT) == null) {
                return m;
            }
        }

        return null;
    }

    public Map<String, Object> findEntryForView(Id viewId, String language) {
        for (Map<String, Object> m : this) {
            if (m.get(MERCHANT) == null && m.get(STORE) == null && m.get(VIEW) != null && m.get(VIEW).equals(viewId)
                && m.get(COUNTRY) == null && m.get(LANGUAGE) != null && m.get(LANGUAGE).equals(language)
                && m.get(REQUEST_CONTEXT) == null) {
                return m;
            }
        }

        return null;
    }

    public Map<String, Object> findEntryForStore(Id storeId) {
        for (Map<String, Object> m : this) {
            if (m.get(MERCHANT) == null && m.get(STORE) != null && m.get(STORE).equals(storeId) && m.get(VIEW) == null
                && m.get(COUNTRY) == null && m.get(LANGUAGE) == null && m.get(REQUEST_CONTEXT) == null) {
                return m;
            }
        }

        return null;
    }

    public Map<String, Object> findEntryForStore(Id storeId, String language) {
        for (Map<String, Object> m : this) {
            if (m.get(MERCHANT) == null && m.get(STORE) != null && m.get(STORE).equals(storeId) && m.get(VIEW) == null
                && m.get(COUNTRY) == null && m.get(LANGUAGE) != null && m.get(LANGUAGE).equals(language)
                && m.get(REQUEST_CONTEXT) == null) {
                return m;
            }
        }

        return null;
    }

    public Map<String, Object> findEntryForMerchant(Id merchantId) {
        for (Map<String, Object> m : this) {
            if (m.get(MERCHANT) != null && m.get(MERCHANT).equals(merchantId) && m.get(STORE) == null
                && m.get(VIEW) == null && m.get(COUNTRY) == null && m.get(LANGUAGE) == null
                && m.get(REQUEST_CONTEXT) == null) {
                return m;
            }
        }

        return null;
    }

    public Map<String, Object> findEntryForMerchant(Id merchantId, String language) {
        for (Map<String, Object> m : this) {
            if (m.get(MERCHANT) != null && m.get(MERCHANT).equals(merchantId) && m.get(STORE) == null
                && m.get(VIEW) == null && m.get(COUNTRY) == null && m.get(LANGUAGE) != null
                && m.get(LANGUAGE).equals(language) && m.get(REQUEST_CONTEXT) == null) {
                return m;
            }
        }

        return null;
    }

    public Map<String, Object> findEntryForRequestContext(Id requestContextId) {
        for (Map<String, Object> m : this) {
            if (m.get(MERCHANT) == null && m.get(STORE) == null && m.get(VIEW) == null && m.get(COUNTRY) == null
                && m.get(LANGUAGE) == null && m.get(REQUEST_CONTEXT) != null
                && m.get(REQUEST_CONTEXT).equals(requestContextId)) {
                return m;
            }
        }

        return null;
    }

    public int getGlobalEntryHash() {
        Map<String, Object> entry = findGlobalEntry();

        return entry == null ? 0 : entryHashCode(entry);
    }

    public int getEntryHashFor(String language) {
        Map<String, Object> entry = findEntryFor(language);

        return entry == null ? 0 : entryHashCode(entry);
    }

    public int getEntryHashFor(String language, String country) {
        Map<String, Object> entry = findEntryFor(language, country);

        return entry == null ? 0 : entryHashCode(entry);
    }

    public int getEntryHashForCountry(String country) {
        Map<String, Object> entry = findEntryForCountry(country);

        return entry == null ? 0 : entryHashCode(entry);
    }

    public int getEntryHashForView(Id viewId) {
        Map<String, Object> entry = findEntryForView(viewId);

        return entry == null ? 0 : entryHashCode(entry);
    }

    public int getEntryHashForView(Id viewId, String language) {
        Map<String, Object> entry = findEntryForView(viewId, language);

        return entry == null ? 0 : entryHashCode(entry);
    }

    public int getEntryHashForStore(Id storeId) {
        Map<String, Object> entry = findEntryForStore(storeId);

        return entry == null ? 0 : entryHashCode(entry);
    }

    public int getEntryHashForStore(Id storeId, String language) {
        Map<String, Object> entry = findEntryForStore(storeId, language);

        return entry == null ? 0 : entryHashCode(entry);
    }

    public int getEntryHashForMerchant(Id merchantId) {
        Map<String, Object> entry = findEntryForMerchant(merchantId);

        return entry == null ? 0 : entryHashCode(entry);
    }

    public int getEntryHashForMerchant(Id merchantId, String language) {
        Map<String, Object> entry = findEntryForMerchant(merchantId, language);

        return entry == null ? 0 : entryHashCode(entry);
    }

    public int getEntryHashForRequestContext(Id requestContextId) {
        Map<String, Object> entry = findEntryForRequestContext(requestContextId);

        return entry == null ? 0 : entryHashCode(entry);
    }

    public int entryHashCode(Map<String, Object> entry) {
        if (entry == null)
            return 0;

        // We return 1 for global value because it will always return 0 once we
        // have removed the value-element.
        if (entry.size() == 1 && entry.containsKey(VALUE))
            return 1;

        Map<String, Object> copy = Maps.newHashMap(entry);
        copy.remove(VALUE);

        return copy.hashCode();
    }

    public boolean globalValueExists(Object value) {
        Object val = getGlobalValue();

        if (val == null && value == null)
            return true;

        if ((val == null && value != null) || (val != null && value == null))
            return false;

        return value.equals(val);
    }

    public boolean valueExistsFor(String language, Object value) {
        Object val = getValueFor(language);

        if (val == null && value == null)
            return true;

        if ((val == null && value != null) || (val != null && value == null))
            return false;
        
        return value.equals(val);
    }

    public boolean valueExistsFor(String language, String country, Object value) {
        Object val = getValueFor(language, country);

        if (val == null && value == null)
            return true;

        if ((val == null && value != null) || (val != null && value == null))
            return false;

        return value.equals(val);
    }

    public boolean valueExistsForView(Id viewId, Object value) {
        Object val = getValueForView(viewId);

        if (val == null && value == null)
            return true;

        if ((val == null && value != null) || (val != null && value == null))
            return false;

        return value.equals(val);
    }

    public boolean valueExistsForView(Id viewId, String language, Object value) {
        Object val = getValueForView(viewId, language);

        if (val == null && value == null)
            return true;

        if ((val == null && value != null) || (val != null && value == null))
            return false;

        return value.equals(val);
    }

    public Object valueExistsForStore(Id storeId, Object value) {
        Object val = getValueForStore(storeId);

        if (val == null && value == null)
            return true;

        if ((val == null && value != null) || (val != null && value == null))
            return false;

        return value.equals(val);
    }

    public boolean valueExistsForStore(Id storeId, String language, Object value) {
        Object val = getValueForStore(storeId, language);

        if (val == null && value == null)
            return true;

        if ((val == null && value != null) || (val != null && value == null))
            return false;

        return value.equals(val);
    }

    public boolean valueExistsForMerchant(Id merchantId, Object value) {
        Object val = getValueForMerchant(merchantId);

        if (val == null && value == null)
            return true;

        if ((val == null && value != null) || (val != null && value == null))
            return false;

        return value.equals(val);
    }

    public Object valueExistsForMerchant(Id merchantId, String language, Object value) {
        Object val = getValueForMerchant(merchantId, language);

        if (val == null && value == null)
            return true;

        if ((val == null && value != null) || (val != null && value == null))
            return false;

        return value.equals(val);
    }

    @SuppressWarnings("unchecked")
    public T getGlobalValue() {
        Map<String, Object> entry = findGlobalEntry();

        return (T) (entry == null ? null : entry.get(VALUE));
    }

    public Object getValueFor(String language) {
        Map<String, Object> entry = findEntryFor(language);

        return entry == null ? null : entry.get(VALUE);
    }

    public Object getValueFor(String language, String country) {
        Map<String, Object> entry = findEntryFor(language, country);

        return entry == null ? null : entry.get(VALUE);
    }

    public Object getValueForView(Id viewId) {
        Map<String, Object> entry = findEntryForView(viewId);

        return entry == null ? null : entry.get(VALUE);
    }

    public Object getValueForView(Id viewId, String language) {
        Map<String, Object> entry = findEntryForView(viewId, language);

        return entry == null ? null : entry.get(VALUE);
    }

    @SuppressWarnings("unchecked")
    public T getValueForStore(Id storeId) {
        Map<String, Object> entry = findEntryForStore(storeId);

        return (T) (entry == null ? null : entry.get(VALUE));
    }

    public Object getValueForStore(Id storeId, String language) {
        Map<String, Object> entry = findEntryForStore(storeId, language);

        return entry == null ? null : entry.get(VALUE);
    }

    public Object getValueForMerchant(Id merchantId) {
        Map<String, Object> entry = findEntryForMerchant(merchantId);

        return entry == null ? null : entry.get(VALUE);
    }

    public Object getValueForMerchant(Id merchantId, String language) {
        Map<String, Object> entry = findEntryForMerchant(merchantId, language);

        return entry == null ? null : entry.get(VALUE);
    }

    public Object getValueForRequestContext(Id reqCtxId) {
        Map<String, Object> entry = findEntryForRequestContext(reqCtxId);

        return entry == null ? null : entry.get(VALUE);
    }

    public Set<String> specifiedLanguages() {
        Set<String> languages = new HashSet<>();

        if (size() > 0) {
            for (Map<String, Object> entry : this) {
                String lang = (String) entry.get(LANGUAGE);

                if (!Str.isEmpty(lang) && !languages.contains(lang))
                    languages.add(lang);
            }
        }

        return languages;
    }

    protected LinkedHashMap<String, Object> toEntry(Id merchantId, Id storeId, String languageCode, String countryCode,
        Id viewId, Id requestContextId, Object value) {
        if (value == null) {
            // if (!ignoreNull)
            // {
            // throw new NullPointerException("Value cannot be null");
            // }
            // else
            {
                return null;
            }
        }

        LinkedHashMap<String, Object> m = new LinkedHashMap<>();

        if (merchantId != null)
            m.put(MERCHANT, merchantId);

        if (storeId != null)
            m.put(STORE, storeId);

        if (languageCode != null)
            m.put(LANGUAGE, languageCode);

        if (countryCode != null)
            m.put(COUNTRY, countryCode);

        if (viewId != null)
            m.put(VIEW, viewId);

        if (requestContextId != null)
            m.put(REQUEST_CONTEXT, requestContextId);

        if (value instanceof String) {
            m.put(VALUE, ((String) value).trim());
        } else {
            m.put(VALUE, value);
        }

        return m;
    }

    public static <T> ContextObject<T> valueOf(List<Map<String, Object>> list) {
        if (list == null)
            return null;

        ContextObject<T> ctxObj = new ContextObject<>();

        for (Map<String, Object> entry : list) {
            if (!isEntryValid(entry)) {
                throw new IllegalArgumentException("ContextObject entry " + entry + " is invalid");
            }

            ctxObj.add(new LinkedHashMap<>(entry));
        }

        return ctxObj;
    }

    public static <T> ContextObject<T> valueOf(String ctxObject) {
        if (ctxObject == null)
            return null;

        if (isXML(ctxObject)) {
            return fromXML(ctxObject);
        } else if (isJSON(ctxObject)) {
            return fromJSON(ctxObject);
        } else {
            throw new IllegalArgumentException("Unabe to parse string. Expecting JSON or XML.");
        }
    }

    /**
     * Converts the following JSON structure to a ContextObject: <code>
     * [
     *  {"val":"My context value #1","v":"789","s":"456","c":"DE","l":"de","m":"123"},
     *  {"val":"My context value #2","v":"987","s":"654","c":"RU","l":"ru","m":"321"},
     *  {"val":"My context value #3","l":"en"},
     *  {"val":"My context value #4"}
     * ]");
     * </code>
     */
    public static <T> ContextObject<T> fromJSON(String json) {
        if (json == null)
            return null;

        if (!isJSON(json))
            throw new IllegalArgumentException(
                "The given string does not appear to be a JSON string. Expected it to start with '[{' and contain the value part 'val:'.");

        List<Map<String, Object>> ctxValues = new ArrayList<>();

        try {
            // Much faster than genson for this type of thing.
            JSONArray jsonArray = new JSONArray(json);

            int numValues = jsonArray.length();

            for (int i = 0; i < numValues; i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);

                if (jsonObj != null) {
                    Map<String, Object> ctxValue = new LinkedHashMap<>();

                    // Merchant
                    if (jsonObj.has(MERCHANT))
                        ctxValue.put(MERCHANT, Id.valueOf(jsonObj.getString(MERCHANT)));

                    // Store
                    if (jsonObj.has(STORE))
                        ctxValue.put(STORE, Id.valueOf(jsonObj.getString(STORE)));

                    // Language
                    if (jsonObj.has(LANGUAGE))
                        ctxValue.put(LANGUAGE, jsonObj.getString(LANGUAGE));

                    // Country
                    if (jsonObj.has(COUNTRY))
                        ctxValue.put(COUNTRY, jsonObj.getString(COUNTRY));

                    // View
                    if (jsonObj.has(VIEW))
                        ctxValue.put(VIEW, Id.valueOf(jsonObj.getString(VIEW)));

                    // RequesContext
                    if (jsonObj.has(REQUEST_CONTEXT))
                        ctxValue.put(REQUEST_CONTEXT, Id.valueOf(jsonObj.getString(REQUEST_CONTEXT)));

                    // Value
                    if (jsonObj.has(VALUE)) {
                        ctxValue.put(VALUE, jsonObj.getString(VALUE));
                        ctxValues.add(ctxValue);
                    }
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException(t.getMessage(), t);
        }

        return valueOf(ctxValues);
    }

    /**
     * Converts the following XML structure to a ContextObject: {@literal
     * <?xml version="1.0" encoding="UTF-8"?>
     * <context_object>
     *  <values>
     *    <value m="123" s="456" l="de" c="DE" v=
    "789">My context value #1</value>
     *    <value m="321" s="654" l="ru" c="RU" v=
    "987">My context value #2</value>
     *    <value l="en">My context value #3</value>
     *    <value>My context value #4</value>
     *  </values>
     * </context_object>
     * }
     */
    public static <T> ContextObject<T> fromXML(String xml) {
        if (xml == null)
            return null;

        if (!isXML(xml))
            throw new IllegalArgumentException(
                "The given string does not appear to be an XML string. Expected it to start with '<?xml' and contain the tag '<context_object>'.");

        List<Map<String, Object>> ctxValues = new ArrayList<>();

        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xPath.compile("context_object/values/value")
                .evaluate(new InputSource(new StringReader(xml)), XPathConstants.NODESET);

            int numValues = nodeList.getLength();

            for (int i = 0; i < numValues; i++) {
                Node n = nodeList.item(i);

                if (n != null) {
                    Map<String, Object> ctxValue = new LinkedHashMap<>();

                    NamedNodeMap nodeMap = n.getAttributes();

                    // Merchant
                    Node merchantAttr = nodeMap.getNamedItem(MERCHANT);
                    if (merchantAttr != null && merchantAttr.getNodeValue() != null)
                        ctxValue.put(MERCHANT, Id.valueOf(merchantAttr.getNodeValue()));

                    // Store
                    Node storeAttr = nodeMap.getNamedItem(STORE);
                    if (storeAttr != null && storeAttr.getNodeValue() != null)
                        ctxValue.put(STORE, Id.valueOf(storeAttr.getNodeValue()));

                    // Language
                    Node langAttr = nodeMap.getNamedItem(LANGUAGE);
                    if (langAttr != null && langAttr.getNodeValue() != null)
                        ctxValue.put(LANGUAGE, langAttr.getNodeValue());

                    // Country
                    Node countryAttr = nodeMap.getNamedItem(COUNTRY);
                    if (countryAttr != null && countryAttr.getNodeValue() != null)
                        ctxValue.put(COUNTRY, countryAttr.getNodeValue());

                    // View
                    Node viewAttr = nodeMap.getNamedItem(VIEW);
                    if (viewAttr != null && viewAttr.getNodeValue() != null)
                        ctxValue.put(VIEW, Id.valueOf(viewAttr.getNodeValue()));

                    // RequestContext
                    Node reqCtxAttr = nodeMap.getNamedItem(REQUEST_CONTEXT);
                    if (reqCtxAttr != null && reqCtxAttr.getNodeValue() != null)
                        ctxValue.put(REQUEST_CONTEXT, Id.valueOf(reqCtxAttr.getNodeValue()));

                    if (n.getTextContent() != null) {
                        ctxValue.put(VALUE, n.getTextContent());
                        ctxValues.add(ctxValue);
                    }
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException(t.getMessage(), t);
        }

        return valueOf(ctxValues);
    }

    protected static boolean isXML(String value) {
        if (value == null)
            return false;

        String normalized = value.trim().replaceAll(" ", "").replaceAll("\n", "").toLowerCase();

        return normalized.startsWith("<?xml") && normalized.indexOf("<context_object>") > -1;
    }

    protected static boolean isJSON(String value) {
        if (value == null)
            return false;

        String normalized = value.trim().replaceAll(" ", "").replaceAll("\n", "").toLowerCase();

        return normalized.startsWith("[{") && (normalized.indexOf("\"val\":") > -1 || normalized.indexOf("val:") > -1);
    }

    public final boolean isValid() {
        boolean isValid = true;

        for (Map<String, Object> entry : this) {
            if (!isEntryValid(entry)) {
                isValid = false;
                break;
            }
        }

        return isValid;
    }

    protected static final boolean isEntryValid(Map<String, Object> entry) {
        Set<String> keys = entry.keySet();

        for (String key : keys) {
            Object val = entry.get(key);

            if (MERCHANT.equals(key) || STORE.equals(key) || VIEW.equals(key) || REQUEST_CONTEXT.equals(key)) {
                if (val != null && !(val instanceof Id)) {
                    return false;
                }
            } else if (LANGUAGE.equals(key) || COUNTRY.equals(key)) {
                if (val != null && !(val instanceof String)) {
                    return false;
                }
            } else if (VALUE.equals(key)) {
                if (val == null) {
                    return false;
                }

                if (!(val instanceof String || val instanceof String[] || val instanceof Number
                    || val instanceof Number[] || val instanceof Date || val instanceof Date[]
                    || val instanceof Boolean || val instanceof Boolean[] || val instanceof Id
                    || val instanceof Id[] || val instanceof byte[] || val instanceof UUID
                    || val instanceof UUID[] || (val instanceof ArrayList<?> && ((ArrayList) val).get(0) instanceof Id))) {
                    return false;
                }
            } else {
                // Unknown key
                return false;
            }
        }

        return true;
    }

    public boolean isEmpty() {
        // No values in list, so no need to check further.
        if (super.isEmpty())
            return true;

        // Check the individual entries to make sure that there is at least one
        // valid value.
        for (Map<String, Object> entry : this) {
            if (entry != null && entry.get(VALUE) != null) {
                Object val = entry.get(VALUE);

                // If the value is a string, we check on the string level.
                if (val != null && val instanceof String && !"".equals(((String) val).trim())) {
                    return false;
                } else if (val != null && !(val instanceof String)) {
                    return false;
                }
            }
        }

        return true;
    }

    // @Override
    // public String toString()
    // {
    // return getString();
    // }
}
