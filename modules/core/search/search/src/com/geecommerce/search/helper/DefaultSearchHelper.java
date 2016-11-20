package com.geecommerce.search.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.facet.FacetBuilder;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.Facets;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.elasticsearch.search.facet.terms.TermsFacet.Entry;

import com.geecommerce.core.elasticsearch.helper.ElasticsearchHelper;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.system.attribute.TargetObjectCode;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.system.attribute.model.AttributeTargetObject;
import com.geecommerce.core.system.attribute.service.AttributeService;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.search.model.Facet;
import com.geecommerce.search.model.FacetEntry;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

@Helper
public class DefaultSearchHelper implements SearchHelper {
    private static final Pattern ATTRIBUTE_CODE_PATTERN = Pattern
        .compile("^att_(.*?)_(?:option|hash|text_[a-z]{2}(?:_[A-Z]{2})?)$");

    private static final Pattern LANG_COUNTRY_PATTERN = Pattern.compile("^att_.+_(?:text_([a-z]{2})(?:_([A-Z]{2}))?)$");

    private final AttributeService attributeService;
    private final ElasticsearchHelper elasticsearchHelper;

    @Inject
    public DefaultSearchHelper(AttributeService attributeService, ElasticsearchHelper elasticsearchHelper) {
        this.attributeService = attributeService;
        this.elasticsearchHelper = elasticsearchHelper;
    }

    @Override
    public List<Facet> buildFacets(SearchResponse response) {
        SearchHits esHits = response.getHits();
        Facets esFacets = response.getFacets();

        List<Facet> facets = new ArrayList<>();

        if (esHits == null || esHits.getTotalHits() == 0 || esFacets == null || esFacets.facets() == null
            || esFacets.facets().size() == 0) {
            return facets;
        }

        Map<String, org.elasticsearch.search.facet.Facet> facetsMap = esFacets.facetsAsMap();

        List<Id> facetOptionIds = new ArrayList<>();

        for (String key : facetsMap.keySet()) {
            TermsFacet termsFacet = (TermsFacet) facetsMap.get(key);

            if (termsFacet.getTotalCount() == 0)
                continue;

            Facet facet = newFacet(key);

            for (Entry entry : termsFacet) {
                if (!isAttributeOption(response, key)) {
                    if (isHashValue(key)) {
                        String label = facetEntryLabel(response, key, entry.getTerm().string());
                        int count = facetEntryCount(response, key, entry.getTerm().string());

                        if (label != null && !"".equals(label.trim())) {
                            facet.addEntry(entry.getTerm().string(), label, count);
                        }
                    }
                } else {
                    if (isInSearchResult(response, key, entry.getTerm().string())) {
                        facetOptionIds.add(Id.parseId(entry.getTerm().string().replace('_', ' ').trim()));
                        int count = facetEntryCount(response, key, entry.getTerm().string());
                        facet.addEntry(entry.getTerm().string(), null, count);
                    }
                }
            }

            if (facet.getTotalCount() > 0) {
                facets.add(facet);
            }
        }

        if (facetOptionIds != null && facetOptionIds.size() > 0) {
            List<AttributeOption> attributeOptions = attributeService
                .getAttributeOptions(facetOptionIds.toArray(new Id[facetOptionIds.size()]));

            Map<String, AttributeOption> attributeOptionsMap = toAttributeOptionsMap(attributeOptions);

            for (Facet facet : facets) {
                if (isAttributeOption(response, facet.getCode())) {
                    for (FacetEntry facetEntry : facet.getEntries()) {
                        facetEntry.setLabel(attributeOptionsMap.get(facetEntry.getId()).getLabel().getString());
                    }
                }
            }
        }

        sortFacets(facets);

        return facets;
    }

    public String toStringId(Id id) {
        return new StringBuilder("__").append(id.num()).append("__").toString();
    }

    protected Map<String, AttributeOption> toAttributeOptionsMap(List<AttributeOption> attributeOptions) {
        Map<String, AttributeOption> optionsMap = new LinkedHashMap<>();

        for (AttributeOption attributeOption : attributeOptions) {
            optionsMap.put(toStringId(attributeOption.getId()), attributeOption);
        }

        return optionsMap;
    }

    protected String facetEntryLabel(SearchResponse response, String facetAttributeCode, String facetEntryId) {
        if (response == null || facetAttributeCode == null || facetEntryId == null)
            return facetEntryId;

        SearchHits esHits = response.getHits();

        ContextObject<Object> co = new ContextObject<>();

        for (SearchHit searchHit : esHits) {
            Map<String, Object> sourceMap = searchHit.sourceAsMap();

            if (!facetEntryId.equals(sourceMap.get(facetAttributeCode))) {
                continue;
            }

            String textAttributeCode = toFacetTextAttributeCode(facetAttributeCode);
            for (String sourceAttributeKey : sourceMap.keySet()) {
                if (sourceAttributeKey.startsWith(textAttributeCode)) {
                    Map<String, String> langCountryMap = getLangCountryMap(sourceAttributeKey);
                    if (langCountryMap.size() == 2) {
                        co.add(langCountryMap.get(ContextObject.LANGUAGE), ContextObject.COUNTRY,
                            sourceMap.get(sourceAttributeKey));
                    } else if (langCountryMap.size() == 1) {
                        co.add(langCountryMap.get(ContextObject.LANGUAGE), sourceMap.get(sourceAttributeKey));
                    }
                }
            }

            if (co.size() > 0)
                break;
        }

        return co == null || co.size() == 0 ? null : co.getString();
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

    protected boolean isInSearchResult(SearchResponse response, String facetAttributeCode, String facetEntryId) {
        if (response == null || facetAttributeCode == null || facetEntryId == null)
            return false;

        SearchHits esHits = response.getHits();

        boolean isInSearchResult = false;

        for (SearchHit searchHit : esHits) {
            Map<String, Object> sourceMap = searchHit.sourceAsMap();

            Object sourceValue = sourceMap.get(facetAttributeCode);

            if (sourceValue != null && sourceValue instanceof String && facetEntryId.equals(sourceValue)) {
                isInSearchResult = true;
                break;
            } else if (sourceValue != null && sourceValue instanceof List<?>
                && ((List<?>) sourceValue).contains(facetEntryId)) {
                isInSearchResult = true;
                break;
            }
        }

        return isInSearchResult;
    }

    protected boolean isAttributeOption(SearchResponse response, String facetAttributeCode) {
        if (response == null || facetAttributeCode == null)
            return false;

        SearchHits esHits = response.getHits();

        boolean isAttributeOption = false;

        for (SearchHit searchHit : esHits) {
            Map<String, Object> sourceMap = searchHit.sourceAsMap();

            Object sourceValue = sourceMap.get(facetAttributeCode);

            if (sourceValue != null) {
                String isOptionAttrCode = toIsOptionAttributeCode(facetAttributeCode);
                Boolean isOptionValue = (Boolean) sourceMap.get(isOptionAttrCode);

                if (isOptionValue != null) {
                    isAttributeOption = isOptionValue;
                } else {
                    isAttributeOption = false;
                }

                break;
            }
        }

        return isAttributeOption;
    }

    protected Map<String, String> getLangCountryMap(String facetTextAttributeCode) {
        Map<String, String> langCountryMap = new HashMap<>();

        Matcher m = LANG_COUNTRY_PATTERN.matcher(facetTextAttributeCode);

        if (m.find()) {
            if (m.group(1) != null) {
                langCountryMap.put(ContextObject.LANGUAGE, m.group(1));
            }

            if (m.group(2) != null) {
                langCountryMap.put(ContextObject.COUNTRY, m.group(2));
            }
        }

        return langCountryMap;
    }

    protected boolean isHashValue(String facetAttributeCode) {
        return facetAttributeCode.endsWith("_hash");
    }

    protected void sortFacets(List<Facet> facets) {
        if (facets == null || facets.size() == 0)
            return;

        Collections.sort(facets, new Comparator<Facet>() {
            public int compare(Facet f1, Facet f2) {
                return (f1.getPosition() < f2.getPosition() ? -1 : (f1.getPosition() > f2.getPosition() ? 1 : 0));
            }
        });
    }

    protected Facet newFacet(String facetAttributeCode) {
        AttributeTargetObject prdTargetObject = attributeService
            .getAttributeTargetObjectByCode(TargetObjectCode.PRODUCT);

        Attribute attribute = null;

        if (prdTargetObject != null)
            attribute = attributeService.getAttribute(prdTargetObject, toAttributeCode(facetAttributeCode));

        if (attribute != null) {
            return new Facet(facetAttributeCode, attribute.getFrontendLabel().getString(),
                attribute.getSearchFilterPosition());
        } else {
            return new Facet(facetAttributeCode, facetAttributeCode, 0);
        }
    }

    protected String toIsOptionAttributeCode(String facetAttributeCode) {
        return new StringBuilder("att_").append(toAttributeCode(facetAttributeCode)).append("_is_option").toString();
    }

    protected String toFacetTextAttributeCode(String facetAttributeCode) {
        return new StringBuilder("att_").append(toAttributeCode(facetAttributeCode)).append("_raw").toString();
    }

    protected String toAttributeCode(String facetAttributeCode) {
        Matcher m = ATTRIBUTE_CODE_PATTERN.matcher(facetAttributeCode);

        if (m.find()) {
            return m.group(1);
        } else {
            return facetAttributeCode;
        }
    }

    @Override
    public Map<String, Object> toFilterMap(String filterParam) {
        if (filterParam == null || "".equals(filterParam.trim()))
            return null;

        Map<String, Object> filterMap = new HashMap<>();

        String[] outerSP = filterParam.split(",");

        for (String keyValuePair : outerSP) {
            String[] innerSP = keyValuePair.split(":");
            if (innerSP != null && innerSP.length == 2) {
                filterMap.put(innerSP[0], innerSP[1]);
            }
        }

        return filterMap;
    }

    @Override
    public List<FacetBuilder> toFacetBuilders(Map<String, Attribute> facetAttributes) {
        List<FacetBuilder> facets = new ArrayList<>();

        if (facetAttributes == null || facetAttributes.size() == 0)
            return facets;

        Set<String> keys = facetAttributes.keySet();

        for (String key : keys) {
            Attribute attribute = facetAttributes.get(key);

            String attributeCode = toHashOrOptionFacetAttributeCode(attribute);

            if (attributeCode != null) {
                facets.add(FacetBuilders.termsFacet(attributeCode).fields(attributeCode));
            }
        }

        return facets;
    }

    protected String toHashOrOptionFacetAttributeCode(Attribute attribute) {
        if (attribute == null)
            return null;

        StringBuilder facetAttributeCode = new StringBuilder("att_").append(attribute.getCode());

        if (attribute.getOptions() != null && attribute.getOptions().size() > 0) {
            facetAttributeCode.append("_hash");
        } else {
            facetAttributeCode.append("_hash");
        }

        return facetAttributeCode.toString();
    }

    @Override
    public QueryBuilder toAndQuery(Map<String, Object> queryParams) {
        BoolQueryBuilder bqb = (BoolQueryBuilder) elasticsearchHelper.toAndQuery(queryParams);

        bqb.must(QueryBuilders.termQuery("is_visible", true));
        bqb.must(QueryBuilders.termQuery("is_visible_in_pl", true));

        return bqb;
    }

}
