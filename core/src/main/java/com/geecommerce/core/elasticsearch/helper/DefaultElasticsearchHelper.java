package com.geecommerce.core.elasticsearch.helper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.regex.Regex;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.facet.FacetBuilder;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.Facets;
import org.elasticsearch.search.facet.range.RangeFacet;
import org.elasticsearch.search.facet.terms.TermsFacet;

import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.elasticsearch.api.search.Facet;
import com.geecommerce.core.elasticsearch.search.FacetCount;
import com.geecommerce.core.elasticsearch.search.FieldKey;
import com.geecommerce.core.elasticsearch.search.FieldValue;
import com.geecommerce.core.elasticsearch.search.FilterValue;
import com.geecommerce.core.enums.FilterIndexField;
import com.geecommerce.core.enums.FilterType;
import com.geecommerce.core.enums.FrontendInput;
import com.geecommerce.core.enums.I18nBoolean;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

@Helper
public class DefaultElasticsearchHelper implements ElasticsearchHelper {
    @Inject
    protected App app;

    protected static final String FIELD_KEY_ATTRIBUTE_CODE = "a";
    protected static final String FIELD_KEY_LANGUAGE = "l";
    protected static final String FIELD_KEY_COUNTRY = "c";

    protected static final String ATT_PREFIX = "att_";
    protected static final String HASH_SUFFIX = "_hash"; // _hash
    protected static final String IS_OPTION_SUFFIX = "_is_option"; // _is_option
    protected static final String RAW_SUFFIX = "_raw"; // _raw
    protected static final String RAW_PART = "_raw_"; // _raw_
    protected static final String SLUG_SUFFIX = "_slug"; // _slug
    protected static final String SLUG_PART = "_slug_"; // _slug_

    protected static final Pattern DOT_PATTERN = Pattern.compile("\\.");
    protected static final Pattern SLASH_PATTERN = Pattern.compile("\\/");
    protected static final Pattern UNDERSCORE_PATTERN = Pattern.compile("_");
    protected static final Pattern MINUS_PATTERN = Pattern.compile("\\-");

    protected static final Pattern badCharsRegex = Pattern.compile("[\\{\\};:\\[\\]\"\\*]+");

    protected static final String INFINITY = "Infinity";

    @Override
    public Id[] toIds(Object[] documentIds) {
        List<Id> ids = new ArrayList<>();

        for (Object o : documentIds) {
            if (o instanceof Long) {
                ids.add(new Id((Long) o));
            } else if (o instanceof BigInteger) {
                ids.add(new Id((BigInteger) o));

            } else if (o instanceof Integer) {
                ids.add(new Id((Integer) o));
            } else {
                ids.add(Id.parseId(o.toString()));
            }
        }

        return ids.toArray(new Id[ids.size()]);
    }

    @Override
    public QueryBuilder buildQuery(List<FilterBuilder> builders, Map<String, FilterValue> filterParams) {
        List<FilterBuilder> filterBuilders = new ArrayList<>();

        for (String key : filterParams.keySet()) {
            FilterValue filterValue = filterParams.get(key);
            FilterType filterType = filterValue.getFilterType();
            Set<Object> value = filterValue.getValues();

            if (filterType == FilterType.RANGE) {
                RangeFilterBuilder rangeFilterBuilder = FilterBuilders.rangeFilter(key);

                Object[] values = value.toArray();

                if (value.size() > 1) {
                    rangeFilterBuilder.from(Double.parseDouble((String) values[0]));
                    rangeFilterBuilder.to(Double.parseDouble((String) values[1]));
                } else if (value.size() > 0) {
                    rangeFilterBuilder.from(Double.parseDouble((String) values[0]));
                } else {
                    rangeFilterBuilder.from(0);
                }

                filterBuilders.add(rangeFilterBuilder);
            } else {
                if (value.size() > 1) {
                    filterBuilders.add(FilterBuilders.termsFilter(key, value.toArray()).execution("or"));
                } else if (value.size() > 0) {
                    filterBuilders.add(FilterBuilders.termsFilter(key, value.iterator().next()));
                }
            }

        }

        if (builders != null && !builders.isEmpty())
            filterBuilders.addAll(builders);

        FilterBuilder andFilterBuilder = FilterBuilders
            .andFilter(filterBuilders.toArray(new FilterBuilder[filterBuilders.size()]));

        return QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), andFilterBuilder);
    }

    @Override
    public Map<String, FilterValue> toQueryMap(Map<String, Object> navFilterParts,
        Map<String, Attribute> filterAttributes) {
        return toQueryMap(navFilterParts, null, filterAttributes);
    }

    @Override
    public Map<String, FilterValue> toQueryMap(Map<String, Object> navFilterParts,
        Map<String, Set<Object>> uriFilterParts, Map<String, Attribute> filterAttributes) {
        // if (navFilterParts == null || navFilterParts.size() == 0)
        // throw new
        // IllegalArgumentException("The navigation filter parts are required in
        // order to build the query map");

        Map<String, FilterValue> queryMap = new HashMap<>();

        if (navFilterParts != null) {
            Set<String> navKeys = navFilterParts.keySet();

            for (String key : navKeys) {
                Object value = navFilterParts.get(key);

                FilterValue filterValue = new FilterValue(FilterType.DEFAULT);
                if (value.getClass().isArray()) {
                    for (Object obj : (Object[]) value) {
                        filterValue.add(obj);
                    }
                } else {
                    filterValue.add(value);
                }

                queryMap.put(key, filterValue);
            }
        }

        if (uriFilterParts != null && uriFilterParts.size() > 0) {
            Set<String> uriKeys = uriFilterParts.keySet();

            for (String key : uriKeys) {
                Attribute filterAttribute = filterAttributes.get(key);

                if (filterAttribute == null)
                    continue;

                List<FilterIndexField> indexFields = filterAttribute.getProductListFilterIndexFields();
                FilterType filterType = filterAttribute.getProductListFilterType();
                ContextObject<String> valuePattern = filterAttribute.getProductListFilterParseValue();

                Set<Object> uriValue = uriFilterParts.get(key);

                if (uriValue != null && uriValue.size() > 0) {
                    FilterValue filterValue = new FilterValue(filterType);

                    for (Object v : uriValue) {
                        if (indexFields != null && indexFields.contains(FilterIndexField.NATIVE)) {
                            if (filterType != null && filterType.equals(FilterType.RANGE) && valuePattern != null
                                && valuePattern.getStr() != null) {
                                Pattern p = Regex.compile(valuePattern.getStr(), null);
                                Matcher m = p.matcher(String.valueOf(v));

                                while (m.find()) {
                                    int groupCount = m.groupCount();

                                    if (groupCount > 1) {
                                        for (int i = 1; i <= groupCount; i++) {
                                            filterValue.add(m.group(i));
                                        }
                                    }
                                }
                            } else {
                                filterValue.add(v);
                            }
                        } else {
                            if (filterAttribute.getFrontendInput() == FrontendInput.BOOLEAN) {
                                filterValue.add(I18nBoolean.fromObject(v).booleanValue());
                            } else {
                                filterValue.add(new StringBuilder(Str.UNDERSCORE_2X).append(String.valueOf(v))
                                    .append(Str.UNDERSCORE_2X).toString());
                            }
                        }
                    }

                    if (indexFields != null && indexFields.contains(FilterIndexField.NATIVE)) {
                        queryMap.put(key, filterValue);
                    } else {
                        if (filterAttribute.getFrontendInput() == FrontendInput.BOOLEAN) {
                            queryMap.put(new StringBuilder(ATT_PREFIX).append(key).append(RAW_PART)
                                .append(app.context().getLanguage()).toString(), filterValue);
                        } else {
                            queryMap.put(new StringBuilder(ATT_PREFIX).append(key).append(SLUG_PART)
                                .append(app.context().getLanguage()).toString(), filterValue);
                        }
                    }
                }
            }
        }

        return queryMap;
    }

    @Override
    public List<Facet> retrieveFacets(SearchResponse responseAll, Map<FieldKey, FieldValue> fieldIndex,
        Map<FieldKey, FacetCount> facetCountIndex, Map<String, Attribute> filterAttributes) {
        List<Facet> facets = new ArrayList<>();

        Facets allFacets = responseAll.getFacets();

        if (allFacets == null)
            return facets;

        for (org.elasticsearch.search.facet.Facet facet : allFacets) {
            String attrCode = normalizeAttributeCode(facet.getName());
            Attribute filterAttribute = filterAttributes.get(attrCode);

            // We cannot continue if no matching attribute has been found.
            if (filterAttribute == null)
                continue;
            // TODO: change name to getFilterPosition, need refactoring for
            // Attribute admin
            Facet resultFacet = app.injectable(Facet.class).values(facet.getName(),
                filterAttribute.getFrontendLabel().getStr(), filterAttribute.getProductListFilterPosition());

            if (facet instanceof RangeFacet) {
                RangeFacet rangeFacet = (RangeFacet) facet;

                List<RangeFacet.Entry> entries = rangeFacet.getEntries();

                if (entries != null && entries.size() > 0) {
                    for (RangeFacet.Entry entry : entries) {
                        FieldKey fieldKey = new FieldKey(rangeFacet.getName(),
                            new StringBuilder(entry.getFromAsString()).append(Char.MINUS)
                                .append(entry.getToAsString()).toString());

                        FacetCount facetCount = facetCountIndex.get(fieldKey);

                        int count = 0;
                        int nonMultiCount = 0;

                        if (facetCount != null) {
                            count = facetCount.getCount();
                            nonMultiCount = facetCount.getNonMultiCount();
                        }

                        String from = entry.getFromAsString();
                        String to = entry.getToAsString();

                        ContextObject<String> labelFormat = filterAttribute.getProductListFilterFormatLabel();

                        String label = null;

                        if (labelFormat != null && labelFormat.getStr() != null) {
                            try {
                                label = String.format(labelFormat.getStr(), entry.getFrom(), entry.getTo());
                            } catch (Throwable t) {
                                label = new StringBuilder(entry.getFromAsString()).append(Char.MINUS)
                                    .append(entry.getToAsString()).toString();
                            }
                        } else {
                            label = new StringBuilder(entry.getFromAsString()).append(Char.MINUS)
                                .append(entry.getToAsString()).toString();
                        }

                        resultFacet.addRangeEntry(new StringBuilder(from).append(Char.MINUS).append(to).toString(),
                            label, entry.getFrom(), INFINITY.equalsIgnoreCase(to) ? null : entry.getTo(), count,
                            nonMultiCount);
                    }

                    resultFacet.sortEntries();

                    facets.add(resultFacet);
                }
            } else {
                TermsFacet termsFacet = (TermsFacet) facet;

                if (termsFacet.getTotalCount() > 0) {
                    for (TermsFacet.Entry entry : termsFacet) {
                        FieldKey fieldKey = new FieldKey(termsFacet.getName(), entry.getTerm().string());

                        FacetCount facetCount = facetCountIndex.get(fieldKey);

                        int count = 0;
                        int nonMultiCount = 0;

                        if (facetCount != null) {
                            count = facetCount.getCount();
                            nonMultiCount = facetCount.getNonMultiCount();
                        }

                        FieldValue fieldValue = fieldIndex.get(fieldKey);

                        if (fieldValue != null) {
                            Object val = fieldValue.getRaw();

                            if (val instanceof Boolean)
                                val = I18nBoolean.valueOf((Boolean) val).i18n();

                            resultFacet.addEntry(entry.getTerm().string(), String.valueOf(val), count, nonMultiCount);
                        }
                    }

                    resultFacet.sortEntries();

                    facets.add(resultFacet);
                }
            }
        }

        Collections.sort(facets);

        return facets;
    }

    public static String normalizeAttributeCode(String indexAttributeCode) {
        Map<String, String> keyParts = extractKeyParts(indexAttributeCode);

        if (keyParts != null && keyParts.containsKey(FIELD_KEY_ATTRIBUTE_CODE)) {
            return keyParts.get(FIELD_KEY_ATTRIBUTE_CODE);
        } else {
            return indexAttributeCode;
        }
    }

    @Override
    public String[] toFieldList(Map<String, Attribute> filterAttributes) {
        if (filterAttributes == null || filterAttributes.size() == 0)
            return new String[0];

        List<String> fields = new ArrayList<>();
        Set<String> keys = filterAttributes.keySet();

        for (String attrCode : keys) {
            String attrCodeSlug = toSlugAttributeCode(attrCode, true);
            String attrRawCode = toRawAttributeCode(attrCode, true);
            String attrHashCode = toHashAttributeCode(attrCode);
            String attrIsOptionCode = toIsOptionAttributeCode(attrCode);

            if (attrCodeSlug != null)
                fields.add(attrCodeSlug);

            if (attrRawCode != null)
                fields.add(attrRawCode);

            if (attrHashCode != null)
                fields.add(attrHashCode);

            if (attrIsOptionCode != null)
                fields.add(attrIsOptionCode);
        }

        return fields.toArray(new String[fields.size()]);
    }

    @Override
    public List<FacetBuilder> toFacetBuilders(Map<String, Attribute> filterAttributes) {
        List<FacetBuilder> facets = new ArrayList<>();

        if (filterAttributes == null || filterAttributes.size() == 0)
            return facets;

        Set<String> keys = filterAttributes.keySet();

        for (String key : keys) {
            Attribute attr = filterAttributes.get(key);
            String attrCode = null;

            List<FilterIndexField> indexFields = attr.getProductListFilterIndexFields();
            if (indexFields != null && indexFields.contains(FilterIndexField.ATT_SLUG)) {
                attrCode = toSlugAttributeCode(attr.getCode(), true);
            } else if (indexFields != null && indexFields.contains(FilterIndexField.NATIVE)) {
                attrCode = attr.getCode();
            }

            if (attrCode != null) {
                FilterType filterType = attr.getProductListFilterType();
                if (filterType == FilterType.RANGE) {
                    facets.add(FacetBuilders.rangeFacet(attrCode).addRange(0, 50).addRange(51, 100).addRange(101, 150)
                        .addRange(151, 200).addRange(201, 500).addRange(501, 1000).addRange(1001, 1500)
                        .addRange(1501, 2000).addRange(2001, 5000).addUnboundedTo(5001).field(attrCode));
                } else {
                    facets.add(FacetBuilders.termsFacet(attrCode).fields(attrCode));
                }
            }
        }

        return facets;
    }

    @Override
    public Map<FieldKey, FacetCount> toFlatFacetCountIndex(SearchResponse response, SearchResponse responseNonMulti,
        SearchResponse responseAll) {
        Map<FieldKey, FacetCount> facetCountIndex = new HashMap<>();

        Facets facets = response.getFacets();
        Facets nonMultiFacets = responseNonMulti.getFacets();
        Facets allFacets = responseAll.getFacets();

        if (allFacets == null)
            return facetCountIndex;

        for (org.elasticsearch.search.facet.Facet allFacet : allFacets) {
            if (allFacet instanceof RangeFacet) {
                RangeFacet allRangeFacet = (RangeFacet) allFacet;
                RangeFacet nonMultiRangeFacet = nonMultiFacets.facet(allFacet.getName());
                RangeFacet rangeFacet = facets.facet(allFacet.getName());

                List<RangeFacet.Entry> allEntries = allRangeFacet.getEntries();

                if (allEntries != null && allEntries.size() > 0) {
                    for (RangeFacet.Entry allEntry : allEntries) {
                        long allCount = allEntry.getCount();
                        long nonMultiCount = 0;
                        long count = 0;

                        for (RangeFacet.Entry nonMultiEntry : nonMultiRangeFacet) {
                            if (nonMultiEntry.getFrom() == allEntry.getFrom()
                                && nonMultiEntry.getTo() == allEntry.getTo()) {
                                nonMultiCount = nonMultiEntry.getCount();
                                break;
                            }
                        }

                        for (RangeFacet.Entry entry : rangeFacet) {
                            if (entry.getFrom() == allEntry.getFrom() && entry.getTo() == allEntry.getTo()) {
                                count = entry.getCount();
                            }
                        }

                        facetCountIndex.put(
                            new FieldKey(allFacet.getName(),
                                new StringBuilder(allEntry.getFromAsString()).append("-")
                                    .append(allEntry.getToAsString()).toString()),
                            new FacetCount((int) count, (int) nonMultiCount, (int) allCount));
                    }
                }
            } else {
                TermsFacet allTermsFacet = (TermsFacet) allFacet;

                if (allTermsFacet.getTotalCount() > 0) {
                    TermsFacet nonMultiTermsFacet = nonMultiFacets.facet(allFacet.getName());
                    TermsFacet termsFacet = facets.facet(allFacet.getName());

                    for (TermsFacet.Entry allEntry : allTermsFacet) {
                        int allCount = allEntry.getCount();
                        int nonMultiCount = 0;
                        int count = 0;

                        for (TermsFacet.Entry nonMultiEntry : nonMultiTermsFacet) {
                            if (nonMultiEntry.getTerm().string().equals(allEntry.getTerm().string())) {
                                nonMultiCount = nonMultiEntry.getCount();
                                break;
                            }
                        }

                        for (TermsFacet.Entry entry : termsFacet) {
                            if (entry.getTerm().string().equals(allEntry.getTerm().string())) {
                                count = entry.getCount();
                            }
                        }

                        facetCountIndex.put(new FieldKey(allFacet.getName(), allEntry.getTerm().string()),
                            new FacetCount(count, nonMultiCount, allCount));
                    }
                }

            }
        }

        return facetCountIndex;
    }

    @Override
    public Map<FieldKey, FieldValue> toFlatFieldIndex(SearchResponse r, SearchResponse responseAll,
        Map<String, Attribute> filterAttributes) {
        Map<FieldKey, FieldValue> fieldValues = new HashMap<>();

        SearchHits esHits = responseAll.getHits();

        for (SearchHit searchHit : esHits) {
            Map<String, Object> source = searchHit.getSource();

            Set<String> keys = source.keySet();

            for (String key : keys) {
                if (key.contains(SLUG_PART)) {
                    Map<String, String> keyParts = extractKeyParts(key);

                    String rawkey = toRawKey(keyParts);
                    String hashkey = toHashKey(keyParts);
                    String isOptionkey = toIsOptionKey(keyParts);

                    Object value = source.get(key);

                    Boolean isOption = (Boolean) source.get(isOptionkey);

                    if (value instanceof List) {
                        List<?> slugValues = (List<?>) value;
                        List<?> rawValues = (List<?>) source.get(rawkey);
                        List<?> hashValues = (List<?>) source.get(hashkey);

                        for (int i = 0; i < slugValues.size(); i++) {
                            String slugValue = (String) slugValues.get(i);
                            Object rawValue = null;

                            if (rawValues != null)
                                rawValue = rawValues.get(i);

                            String hashValue = (String) hashValues.get(i);

                            fieldValues.put(new FieldKey(key, (String) slugValues.get(i)), new FieldValue(rawValue,
                                slugValue, hashValue, isOption == null ? false : isOption.booleanValue()));
                        }
                    } else {
                        String slugValue = (String) value;
                        Object rawValue = source.get(rawkey);
                        String hashValue = (String) source.get(hashkey);

                        fieldValues.put(new FieldKey(key, (String) slugValue), new FieldValue(rawValue, slugValue,
                            hashValue, isOption == null ? false : isOption.booleanValue()));
                    }
                } else if (!key.startsWith(ATT_PREFIX)) {
                    Attribute attr = filterAttributes.get(key);

                    if (attr != null) {
                        List<FilterIndexField> indexFields = attr.getProductListFilterIndexFields();

                        if (indexFields != null && indexFields.contains(FilterIndexField.NATIVE)) {
                            Object value = source.get(key);

                            if (value instanceof List) {
                                List<?> values = (List<?>) value;

                                for (int i = 0; i < values.size(); i++) {
                                    Object rawValue = values.get(i);
                                    fieldValues.put(new FieldKey(key, String.valueOf(rawValue)),
                                        new FieldValue(rawValue, null, null, false));
                                }
                            } else {
                                fieldValues.put(new FieldKey(key, String.valueOf(value)),
                                    new FieldValue(value, null, null, false));
                            }
                        }
                    }
                }
            }
        }

        return fieldValues;
    }

    protected int facetEntryCount(SearchResponse response, String facetAttributeCode, String facetEntryId) {
        if (response == null || facetAttributeCode == null || facetEntryId == null)
            return 0;

        SearchHits esHits = response.getHits();

        HashSet<String> documentIds = Sets.newHashSet();

        for (SearchHit searchHit : esHits) {
            Map<String, Object> sourceMap = searchHit.sourceAsMap();

            Object sourceValue = sourceMap.get(facetAttributeCode);

            if (sourceValue != null && sourceValue instanceof String && facetEntryId.equals(sourceValue)) {
                documentIds.add(searchHit.getId());
            } else if (sourceValue != null && sourceValue instanceof List<?>
                && ((List<?>) sourceValue).contains(facetEntryId)) {
                documentIds.add(searchHit.getId());
            }
        }

        return documentIds.size();
    }

    protected boolean isInSearchResult(SearchResponse response, String attributeCode, String value) {
        if (response == null || attributeCode == null || value == null)
            return false;

        SearchHits esHits = response.getHits();

        boolean isInSearchResult = false;

        for (SearchHit searchHit : esHits) {
            Map<String, Object> sourceMap = searchHit.sourceAsMap();

            Object sourceValue = sourceMap.get(attributeCode);

            if (sourceValue != null && sourceValue instanceof String && value.equals(sourceValue)) {
                isInSearchResult = true;
                break;
            } else if (sourceValue != null && sourceValue instanceof List<?>
                && ((List<?>) sourceValue).contains(value)) {
                isInSearchResult = true;
                break;
            }
        }

        return isInSearchResult;
    }

    protected Attribute findAttibute(String code, List<Attribute> filterAttributes) {
        if (code.startsWith(ATT_PREFIX)) {
            Map<String, String> keyParts = extractKeyParts(code);
            if (keyParts != null && keyParts.size() > 0) {
                code = keyParts.get(FIELD_KEY_ATTRIBUTE_CODE);
            }
        }

        Attribute foundAttribute = null;

        for (Attribute attribute : filterAttributes) {
            if (attribute.getCode().equals(code)) {
                foundAttribute = attribute;
                break;
            }
        }

        return foundAttribute;
    }

    @Override
    public Map<String, String> buildAttributeAliasIndex(Map<String, Attribute> filterAttributes) {
        Map<String, String> attributeAliasIndex = new HashMap<>();

        if (filterAttributes != null && filterAttributes.size() > 0) {
            Set<String> keys = filterAttributes.keySet();

            for (String key : keys) {
                Attribute attr = filterAttributes.get(key);

                if (attr.getProductListFilterKeyAlias() != null
                    && attr.getProductListFilterKeyAlias().getStr() != null) {
                    attributeAliasIndex.put(attr.getProductListFilterKeyAlias().getStr(), attr.getCode());
                }
            }
        }

        return attributeAliasIndex;
    }

    @Override
    public <T> T sanitize(T o) {
        if (o == null)
            return o;

        if (!(o instanceof String))
            return o;

        String s = (String) o;

        Matcher m = badCharsRegex.matcher(s);

        if (m.find())
            s = m.replaceAll(Str.EMPTY);

        return (T) s;
    }

    @Override
    public QueryBuilder toAndQuery(Map<String, Object> queryParams) {
        BoolQueryBuilder bqb = QueryBuilders.boolQuery();

        for (String key : queryParams.keySet()) {
            bqb.must(QueryBuilders.termQuery(key, sanitize(queryParams.get(key))));
        }

        return bqb;
    }

    private String toRawKey(Map<String, String> keyParts) {
        StringBuilder key = new StringBuilder(ATT_PREFIX).append(keyParts.get(FIELD_KEY_ATTRIBUTE_CODE))
            .append(RAW_SUFFIX);

        String language = keyParts.get(FIELD_KEY_LANGUAGE);
        if (language != null) {
            key.append(Char.UNDERSCORE).append(language);
        }

        String country = keyParts.get(FIELD_KEY_COUNTRY);
        if (country != null) {
            key.append(Char.UNDERSCORE).append(country);
        }

        return key.toString();
    }

    private String toHashKey(Map<String, String> keyParts) {
        return new StringBuilder(ATT_PREFIX).append(keyParts.get(FIELD_KEY_ATTRIBUTE_CODE)).append(HASH_SUFFIX)
            .toString();
    }

    private String toIsOptionKey(Map<String, String> keyParts) {
        return new StringBuilder(ATT_PREFIX).append(keyParts.get(FIELD_KEY_ATTRIBUTE_CODE)).append(IS_OPTION_SUFFIX)
            .toString();
    }

    protected static Map<String, String> extractKeyParts(String key) {
        Map<String, String> keyParts = new HashMap<>();

        if (key.contains(SLUG_PART)) {
            int pos = key.lastIndexOf(SLUG_PART);
            int remaining = key.length() - pos;

            // Assuming language and country.
            if (remaining > 8) {
                keyParts.put(FIELD_KEY_ATTRIBUTE_CODE, key.substring(4, key.length() - remaining));
                keyParts.put(FIELD_KEY_LANGUAGE, key.substring(key.length() - 5, key.length() - 3));
                keyParts.put(FIELD_KEY_COUNTRY, key.substring(key.length() - 2, key.length()));
            }
            // Otherwise just the language in key.
            else {
                keyParts.put(FIELD_KEY_ATTRIBUTE_CODE, key.substring(4, key.length() - remaining));
                keyParts.put(FIELD_KEY_LANGUAGE, key.substring(key.length() - 2, key.length()));
            }
        } else if (key.endsWith(HASH_SUFFIX)) {
            keyParts.put(FIELD_KEY_ATTRIBUTE_CODE, key.substring(4, key.length() - 5));
        } else if (key.contains(RAW_PART)) {
            int pos = key.lastIndexOf(RAW_PART);
            int remaining = key.length() - pos;

            // Assuming language and country.
            if (remaining > 8) {
                keyParts.put(FIELD_KEY_ATTRIBUTE_CODE, key.substring(4, key.length() - remaining));
                keyParts.put(FIELD_KEY_LANGUAGE, key.substring(key.length() - 5, key.length() - 3));
                keyParts.put(FIELD_KEY_COUNTRY, key.substring(key.length() - 2, key.length()));
            }
            // Otherwise just the language in key.
            else {
                keyParts.put(FIELD_KEY_ATTRIBUTE_CODE, key.substring(4, key.length() - remaining));
                keyParts.put(FIELD_KEY_LANGUAGE, key.substring(key.length() - 2, key.length()));
            }
        } else if (key.endsWith(IS_OPTION_SUFFIX)) {
            keyParts.put(FIELD_KEY_ATTRIBUTE_CODE, key.substring(4, key.length() - 10));
        }

        return keyParts;
    }

    private String toSlugAttributeCode(String attributeCode, boolean appendLanguage) {
        StringBuilder slugAttrCode = null;

        if (attributeCode.startsWith(ATT_PREFIX)) {
            String attrCode = normalizeAttributeCode(attributeCode);
            slugAttrCode = new StringBuilder(ATT_PREFIX).append(attrCode).append(SLUG_SUFFIX);
        } else {
            slugAttrCode = new StringBuilder(ATT_PREFIX).append(attributeCode).append(SLUG_SUFFIX);
        }

        if (appendLanguage) {
            slugAttrCode.append(Char.UNDERSCORE).append(app.context().getLanguage());
        }

        return slugAttrCode.toString();
    }

    private String toRawAttributeCode(String attributeCode, boolean appendLanguage) {
        StringBuilder rawAttrCode = null;

        if (attributeCode.startsWith(ATT_PREFIX)) {
            String attrCode = normalizeAttributeCode(attributeCode);
            rawAttrCode = new StringBuilder(ATT_PREFIX).append(attrCode).append(RAW_SUFFIX);
        } else {
            rawAttrCode = new StringBuilder(ATT_PREFIX).append(attributeCode).append(RAW_SUFFIX);
        }

        if (appendLanguage) {
            rawAttrCode.append(Char.UNDERSCORE).append(app.context().getLanguage());
        }

        return rawAttrCode.toString();
    }

    private String toHashAttributeCode(String attributeCode) {
        StringBuilder hashAttrCode = null;

        if (attributeCode.startsWith(ATT_PREFIX)) {
            String attrCode = normalizeAttributeCode(attributeCode);
            hashAttrCode = new StringBuilder(ATT_PREFIX).append(attrCode).append(HASH_SUFFIX);
        } else {
            hashAttrCode = new StringBuilder(ATT_PREFIX).append(attributeCode).append(HASH_SUFFIX);
        }

        return hashAttrCode.toString();
    }

    private String toIsOptionAttributeCode(String attributeCode) {
        StringBuilder isOptionAttrCode = null;

        if (attributeCode.startsWith(ATT_PREFIX)) {
            String attrCode = normalizeAttributeCode(attributeCode);
            isOptionAttrCode = new StringBuilder(ATT_PREFIX).append(attrCode).append(IS_OPTION_SUFFIX);
        } else {
            isOptionAttrCode = new StringBuilder(ATT_PREFIX).append(attributeCode).append(IS_OPTION_SUFFIX);
        }

        return isOptionAttrCode.toString();
    }
}
