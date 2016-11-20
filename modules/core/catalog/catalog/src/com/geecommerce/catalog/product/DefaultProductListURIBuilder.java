package com.geecommerce.catalog.product;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.geecommerce.catalog.product.model.ProductListFilterRule;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.service.annotation.Injectable;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.util.Strings;
import com.google.common.collect.Sets;

@Injectable
public class DefaultProductListURIBuilder implements ProductListURIBuilder {
    private static final long serialVersionUID = 4007608661083881769L;

    private String baseURI = null;

    private ProductListFilterRule filterRule = null;

    private Map<String, Attribute> attributeMetaData = null;

    private Map<String, Attribute> attributeAliasIndex = null;

    private Map<String, Set<Object>> basicFilterParts = new HashMap<>();

    private Map<String, FilterValue> filterValues = new HashMap<>();

    private static final String FIELD_KEY_ATTRIBUTE_CODE = "a";
    private static final String FIELD_KEY_LANGUAGE = "l";
    private static final String FIELD_KEY_COUNTRY = "c";

    private static final String ATT_PREFIX = "att_";
    private static final String IS_OPTION_SUFFIX = "_is_option";
    private static final String HASH_SUFFIX = "_hash";
    private static final String SLUG_PART = "_slug_";
    private static final String RAW_PART = "_raw_";

    private static final String FROM_SUFFIX = "_from";
    private static final String TO_SUFFIX = "_to";

    private static final String PARAM_PAGE = "page";
    private static final String PARAM_LIMIT = "limit";

    class FilterValue {
        private String attrCode;
        private String attrAlias;
        private Set<Object> values = Sets.newLinkedHashSet();
        private Double rangeFrom = null;
        private Double rangeTo = null;
        private int position;
        private boolean isMulti;
        private boolean isRange;
        private String prefix;
        private int numUnderscores = 0;

        public FilterValue(Attribute attribute) {
            attrCode = attribute.getCode();
            position = attribute.getProductListFilterPosition();
            isMulti = attribute.isProductListFilterMulti();

            if (attribute.getProductListFilterKeyAlias() != null
                && attribute.getProductListFilterKeyAlias().getStr() != null)
                attrAlias = attribute.getProductListFilterKeyAlias().getStr();
        }

        public FilterValue(String attrCode, Set<Object> values, int position, boolean isMulti, String prefix) {
            this.attrCode = attrCode;
            this.values = values;
            this.position = position;
            this.isMulti = isMulti;
            this.prefix = prefix;
        }

        public FilterValue add(Object value) {
            values.add(value);
            return this;
        }

        public FilterValue addRange(Double rangeFrom, Double rangeTo) {
            this.rangeFrom = rangeFrom;
            this.rangeTo = rangeTo;
            isRange = true;
            return this;
        }

        public FilterValue remove(Object value) {
            values.remove(value);
            return this;
        }

        public boolean isEmpty() {
            return values.isEmpty();
        }

        public FilterValue setNumUnderscores(int numUnderscores) {
            this.numUnderscores = numUnderscores;
            return this;
        }

        public String toURIValue() {
            StringBuilder uriValue = new StringBuilder();

            for (int i = 0; i < numUnderscores; i++)
                uriValue.append(Char.UNDERSCORE);

            if (prefix != null) {
                uriValue.append(prefix).append(Char.MINUS);
            }

            if (values.size() > 1 && isMulti) {
                uriValue.append(implodeMultipleValues(values));
            } else {
                uriValue.append(values.iterator().next());
            }

            return uriValue.toString();
        }

        @Override
        public String toString() {
            return "FilterValue [attrCode=" + attrCode + ", attrAlias=" + attrAlias + ", values=" + values
                + ", rangeFrom=" + rangeFrom + ", rangeTo=" + rangeTo + ", position=" + position + ", isMulti="
                + isMulti + ", isRange=" + isRange + ", prefix=" + prefix + ", numUnderscores=" + numUnderscores
                + "]";
        }
    }

    protected DefaultProductListURIBuilder() {

    }

    protected DefaultProductListURIBuilder(String baseURI) {
        this.baseURI = new String(baseURI);
    }

    @Override
    public ProductListURIBuilder newURI(String baseURI) {
        return new DefaultProductListURIBuilder(baseURI);
    }

    @Override
    public ProductListURIBuilder usingFilterRule(ProductListFilterRule filterRule) {
        this.filterRule = filterRule;

        return this;
    }

    @Override
    public ProductListURIBuilder withAttributeMetaData(Map<String, Attribute> attributeMetaData) {
        this.attributeMetaData = attributeMetaData;
        return this;
    }

    @Override
    public ProductListURIBuilder withAttributeAliasIndex(Map<String, Attribute> attributeAliasIndex) {
        this.attributeAliasIndex = attributeAliasIndex;
        return this;
    }

    @Override
    public ProductListURIBuilder havingURIFilterParts(Map<String, Set<Object>> uriFilterParts) {
        if (uriFilterParts != null && uriFilterParts.size() > 0) {
            Set<String> keys = uriFilterParts.keySet();

            for (String attrCode : keys) {
                Set<Object> value = uriFilterParts.get(attrCode);

                if (value != null && value.size() > 0) {
                    havingValue(attrCode, value.toArray());
                }
            }
        }

        return this;
    }

    @Override
    public ProductListURIBuilder havingParamFilterParts(Map<String, Set<Object>> paramFilterParts) {
        if (paramFilterParts != null && paramFilterParts.size() > 0) {
            Set<String> keys = paramFilterParts.keySet();

            for (String attrCode : keys) {
                Set<Object> value = paramFilterParts.get(attrCode);

                if (value != null && value.size() > 0) {
                    havingValue(attrCode, value.toArray());
                }
            }
        }

        return this;
    }

    @Override
    public ProductListURIBuilder havingValue(String fieldKey, Object... value) {
        if (fieldKey != null && value != null) {
            Map<String, String> keyParts = extractKeyParts(fieldKey);
            String attrCode = keyParts.get(FIELD_KEY_ATTRIBUTE_CODE);

            FilterValue filterValue = filterValues.get(attrCode);

            if (filterValue == null) {
                Attribute attr = findAttributeInMetaData(attrCode);

                if (attr != null) {
                    filterValue = new FilterValue(attr);
                    filterValues.put(fieldKey, filterValue);
                }
            }

            if (filterValue != null) {
                for (Object v : value) {
                    filterValue.add(Strings.slugify(String.valueOf(v)));
                }
            }
        }

        return this;
    }

    @Override
    public ProductListURIBuilder removeValue(String fieldKey, String filterLabel) {
        if (fieldKey != null && filterLabel != null) {
            Map<String, String> keyParts = extractKeyParts(fieldKey);
            String attrCode = keyParts.get(FIELD_KEY_ATTRIBUTE_CODE);

            FilterValue filterValue = filterValues.get(attrCode);

            if (filterValue != null) {
                filterValue.remove(Strings.slugify(String.valueOf(filterLabel)));

                if (filterValue.isEmpty()) {
                    filterValues.remove(attrCode);
                }
            }
        }

        return this;
    }

    @Override
    public ProductListURIBuilder havingNewValue(String fieldKey, Object value) {
        if (fieldKey != null && value != null) {
            Map<String, String> keyParts = extractKeyParts(fieldKey);
            String attrCode = keyParts.get(FIELD_KEY_ATTRIBUTE_CODE);

            FilterValue filterValue = filterValues.get(attrCode);

            if (filterValue == null) {
                Attribute attr = findAttributeInMetaData(attrCode);

                if (attr != null) {
                    filterValue = new FilterValue(attr);
                    filterValues.put(fieldKey, filterValue);
                }
            }

            if (filterValue != null) {
                if (filterValue.isMulti) {
                    filterValue.add(Strings.slugify(String.valueOf(value)));
                } else {
                    Attribute attr = findAttributeInMetaData(attrCode);

                    if (attr != null) {
                        filterValue = new FilterValue(attr);
                        filterValues.put(fieldKey, filterValue);
                    }
                }
            }
        }

        return this;
    }

    @Override
    public ProductListURIBuilder havingRangeValue(String fieldKey, Double rangeFrom, Double rangeTo) {
        if (fieldKey != null && rangeFrom != null) {
            Map<String, String> keyParts = extractKeyParts(fieldKey);
            String attrCode = keyParts.get(FIELD_KEY_ATTRIBUTE_CODE);

            Attribute attr = findAttributeInMetaData(attrCode);

            if (attr == null)
                return this;

            FilterValue filterValue = filterValues.get(attrCode);

            if (filterValue == null) {
                if (attr != null) {
                    filterValue = new FilterValue(attr);
                    filterValues.put(fieldKey, filterValue);
                }
            }

            if (filterValue != null) {
                ContextObject<String> valueFormat = attr.getProductListFilterFormatValue();
                String format = valueFormat == null ? null : valueFormat.getStr();

                if (format != null) {
                    filterValue.add(String.format(format, rangeFrom, rangeTo));
                } else {
                    filterValue.addRange(rangeFrom, rangeTo);
                }
            }
        }

        return this;
    }

    @Override
    public ProductListURIBuilder havingBasicFilterParts(Map<String, Set<Object>> basicFilterParts) {
        if (basicFilterParts != null) {
            basicFilterParts.remove(PARAM_LIMIT);
            basicFilterParts.remove(PARAM_PAGE);
        }

        this.basicFilterParts = basicFilterParts;

        return this;
    }

    @Override
    public String build() {
        if (filterValues.size() == 0 && basicFilterParts.size() == 0)
            return baseURI;

        StringBuilder filterURI = new StringBuilder(baseURI);

        if (!baseURI.endsWith(Str.SLASH)) {
            filterURI.append(Char.SLASH);
        }

        // ---------------------------------------------------------
        // SEO URI filters.
        // ---------------------------------------------------------
        List<FilterValue> uriFilterValues = getURIFilterValues();

        if (uriFilterValues != null && uriFilterValues.size() > 0) {
            addUnderscores(uriFilterValues);

            for (FilterValue sortedFilterValue : uriFilterValues) {
                filterURI.append(sortedFilterValue.toURIValue());
            }

            filterURI.append(Char.SLASH);
        }

        // ---------------------------------------------------------
        // Parameter filters.
        // ---------------------------------------------------------

        List<FilterValue> paramFilterValues = getParamFilterValues();

        if (paramFilterValues.size() > 0) {
            appendParamFilterValues(filterURI, paramFilterValues);
        }

        // ---------------------------------------------------------
        // Sorting parameters.
        // ---------------------------------------------------------

        if (basicFilterParts != null && basicFilterParts.size() > 0) {
            appendBasicFilterValues(filterURI, basicFilterParts);
        }

        return filterURI.toString();
    }

    public Map<String, String> extractKeyParts(String key) {
        Map<String, String> keyParts = new HashMap<>();

        if (key == null || !key.startsWith(ATT_PREFIX)) {
            keyParts.put(FIELD_KEY_ATTRIBUTE_CODE, key);
            return keyParts;
        }

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

    private void addUnderscores(List<FilterValue> sortedURIFilterValues) {
        int previousPos = 0;
        int loop = 0;

        for (FilterValue sortedFilterValue : sortedURIFilterValues) {
            int pos = sortedFilterValue.position;

            if (loop == 0) {
                sortedFilterValue.setNumUnderscores(pos - 1);
            } else {
                // Is the next one or do we skip an attribute
                if ((pos - previousPos) == 1) {
                    sortedFilterValue.setNumUnderscores(1);
                } else {
                    sortedFilterValue.setNumUnderscores(pos - 1);
                }
            }

            previousPos = pos;

            loop++;
        }
    }

    @SuppressWarnings("unchecked")
    private List<FilterValue> getURIFilterValues() {
        List<FilterValue> sortedURIFilterValues = new ArrayList<>();

        if (filterRule == null || filterValues == null || filterValues.size() == 0)
            return sortedURIFilterValues;

        Set<String> keys = filterValues.keySet();

        for (String key : keys) {
            FilterValue filterValue = filterValues.get(key);
            Map<String, Object> filterRuleAttr = filterRule.findAttribute(filterValue.attrCode);

            if (filterRuleAttr != null) {
                Integer posInURI = (Integer) filterRuleAttr.get(ProductListFilterRule.AttributeField.POSITION_IN_URI);
                ContextObject<String> prefix = ContextObject.valueOf(
                    (List<Map<String, Object>>) filterRuleAttr.get(ProductListFilterRule.AttributeField.PREFIX));

                // Override values if they have been specified in the
                // filter-rules collection
                if (posInURI != null)
                    filterValue.position = posInURI;

                if (prefix != null)
                    filterValue.prefix = prefix.getStr();

                sortedURIFilterValues.add(filterValue);
            }
        }

        if (sortedURIFilterValues.size() > 1) {
            Collections.sort(sortedURIFilterValues, new Comparator<FilterValue>() {
                @Override
                public int compare(FilterValue o1, FilterValue o2) {
                    return (o1.position < o2.position ? -1 : (o1.position > o2.position ? 1 : 0));
                }
            });
        }

        return sortedURIFilterValues;
    }

    private List<FilterValue> getParamFilterValues() {
        List<FilterValue> sortedParamFilterValues = new ArrayList<>();

        Set<String> keys = filterValues.keySet();

        for (String key : keys) {
            FilterValue filterValue = filterValues.get(key);

            Map<String, Object> filterRuleAttr = null;

            if (filterRule != null) {
                filterRuleAttr = filterRule.findAttribute(filterValue.attrCode);
            }

            // If we cannot find the attribute in the filter-rule, it must be a
            // normal parameter filter.
            if (filterRuleAttr == null) {
                sortedParamFilterValues.add(filterValues.get(key));
            }
        }

        if (sortedParamFilterValues.size() > 1) {
            Collections.sort(sortedParamFilterValues, new Comparator<FilterValue>() {
                @Override
                public int compare(FilterValue o1, FilterValue o2) {
                    return (o1.position < o2.position ? -1 : (o1.position > o2.position ? 1 : 0));
                }
            });
        }

        return sortedParamFilterValues;
    }

    private String implodeMultipleValues(Collection<?> values) {
        StringBuilder sb = new StringBuilder();

        if (values != null && values.size() > 0) {
            int x = 0;

            for (Object v : values) {
                if (x > 0)
                    sb.append(Char.DOT);

                sb.append(Strings.slugify(String.valueOf(v)));

                x++;
            }
        }

        return sb.toString();
    }

    private Attribute findAttributeInMetaData(String attrCode) {
        if (attributeMetaData == null || attrCode == null)
            return null;

        return attributeMetaData.get(attrCode);
    }

    private void appendParamFilterValues(StringBuilder filterURL, List<FilterValue> paramFilterValues) {
        int x = 0;

        if (filterURL.indexOf(Str.QUESTION_MARK) < 0) {
            filterURL.append(Char.QUESTION_MARK);
        } else {
            x = 1;
        }

        for (FilterValue filterValue : paramFilterValues) {
            String attrCode = filterValue.attrAlias == null ? filterValue.attrCode : filterValue.attrAlias;
            Set<Object> values = filterValue.values;

            if (x > 0)
                filterURL.append(Char.AMPERSAND);

            if (filterValue.isRange) {
                filterURL.append(attrCode).append(FROM_SUFFIX).append(Char.EQUALS).append(filterValue.rangeFrom);
                if (filterValue.rangeTo != null)
                    filterURL.append(Char.AMPERSAND).append(attrCode).append(TO_SUFFIX).append(Char.EQUALS)
                        .append(filterValue.rangeTo);
            } else {
                if (values.size() > 1) {
                    filterURL.append(attrCode).append(Char.EQUALS).append(implodeMultipleValues(values));
                } else if (values.size() > 0) {
                    filterURL.append(attrCode).append(Char.EQUALS).append(values.iterator().next());
                }
            }

            x++;
        }
    }

    private void appendBasicFilterValues(StringBuilder filterURL, Map<String, Set<Object>> basicFilterParts) {
        int x = 0;

        if (filterURL.indexOf(Str.QUESTION_MARK) < 0) {
            filterURL.append(Char.QUESTION_MARK);
        } else {
            x = 1;
        }

        for (String attrCode : basicFilterParts.keySet()) {
            Set<Object> values = basicFilterParts.get(attrCode);

            if (x > 0)
                filterURL.append(Char.AMPERSAND);

            if (values.size() > 1) {
                filterURL.append(attrCode).append(Char.EQUALS).append(implodeMultipleValues(values));
            } else if (values.size() > 0) {
                filterURL.append(attrCode).append(Char.EQUALS).append(values.iterator().next());
            }

            x++;
        }
    }
}
