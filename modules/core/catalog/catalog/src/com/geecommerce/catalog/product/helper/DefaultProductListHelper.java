package com.geecommerce.catalog.product.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;

import com.geecommerce.catalog.product.enums.ProductListQueryNodeType;
import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.model.ProductListFilterRule;
import com.geecommerce.catalog.product.model.ProductListQueryNode;
import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Strings;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

@Helper
public class DefaultProductListHelper implements ProductListHelper {
    @Inject
    protected App app;

    protected static final String CATEGORY_SEO_APPEND_PARENT_KEYWORDS = "category/seo/append_parent_keywords";

    protected static final String AND = "AND";
    protected static final String OR = "or";

    protected static final Pattern DOT_PATTERN = Pattern.compile("\\.");
    protected static final Pattern SLASH_PATTERN = Pattern.compile("\\/");
    protected static final Pattern UNDERSCORE_PATTERN = Pattern.compile("_");
    protected static final Pattern MINUS_PATTERN = Pattern.compile("\\-");

    protected static final String ATT_PREFIX = "att_";
    protected static final String HASH_SUFFIX = "_hash"; // _hash
    protected static final String IS_OPTION_SUFFIX = "_is_option"; // _is_option
    protected static final String RAW_SUFFIX = "_raw"; // _raw
    protected static final String RAW_PART = "_raw_"; // _raw_
    protected static final String SLUG_SUFFIX = "_slug"; // _slug
    protected static final String SLUG_PART = "_slug_"; // _slug_

    protected static final String IS_VISIBLE = "is_visible";
    protected static final String IS_VISIBLE_IN_PL = "is_visible_in_pl";

    @Override
    public List<FilterBuilder> getVisibilityFilters() {
        List<FilterBuilder> filterBuilders = new ArrayList<>();
        filterBuilders.add(FilterBuilders.termFilter(IS_VISIBLE, true));
        filterBuilders.add(FilterBuilders.termFilter(IS_VISIBLE_IN_PL, true));

        return filterBuilders;
    }

    @Override
    public FilterBuilder buildQuery(ProductListQueryNode queryNode) {
        if (queryNode == null)
            return null;
        if (queryNode.getType().equals(ProductListQueryNodeType.BOOLEAN)) {
            List<FilterBuilder> filterBuilders = new ArrayList<>();
            for (ProductListQueryNode node : queryNode.getNodes()) {
                filterBuilders.add(buildQuery(node));
            }
            if (queryNode.getOperator().equals(AND)) {
                FilterBuilder andFilterBuilder = FilterBuilders
                    .andFilter(filterBuilders.toArray(new FilterBuilder[filterBuilders.size()]));
                return andFilterBuilder;
            } else {
                FilterBuilder orFilterBuilder = FilterBuilders
                    .orFilter(filterBuilders.toArray(new FilterBuilder[filterBuilders.size()]));
                return orFilterBuilder;
            }
        } else {
            if (queryNode.getValue() != null && queryNode.getValue().getAttribute() != null) {
                String key = ATT_PREFIX + Strings.slugify(queryNode.getValue().getAttribute().getCode())
                    .replace(Char.MINUS, Char.UNDERSCORE) + HASH_SUFFIX;
                if (queryNode.getValue().getOptionIds() != null && queryNode.getValue().getOptionIds().size() > 1) {
                    List<String> values = new ArrayList<>();
                    for (Id id : queryNode.getValue().getOptionIds()) {
                        values.add(Str.UNDERSCORE_2X + id + Str.UNDERSCORE_2X);
                    }
                    return FilterBuilders.termsFilter(key, values).execution(OR);
                } else if (queryNode.getValue().getOptionIds() != null
                    && queryNode.getValue().getOptionIds().size() > 0) {
                    return FilterBuilders.termsFilter(key,
                        Str.UNDERSCORE_2X + queryNode.getValue().getOptionId() + Str.UNDERSCORE_2X);
                }
            }
        }
        return null;
    }

    @Override
    public Map<String, Set<Object>> getFilterPartsFromParameters(Map<String, String[]> parameterMap,
        ProductListFilterRule navFilterURLRule, Map<String, Attribute> filterAttributes,
        Map<String, String> attributesAliasIndex) {
        Map<String, Set<Object>> filterPartsMap = new LinkedHashMap<>();

        Set<String> keys = parameterMap.keySet();

        for (String paramName : keys) {
            String[] paramValue = parameterMap.get(paramName);

            if (isFilterAttribute(paramName, filterAttributes, attributesAliasIndex) && paramValue != null
                && paramValue.length > 0) {
                Set<Object> values = filterPartsMap.get(paramName);
                String attrCode = getRealAttributeCode(paramName, filterAttributes, attributesAliasIndex);

                if (values == null) {
                    values = Sets.newLinkedHashSet();
                    filterPartsMap.put(attrCode, values);
                }

                for (String filterPart : paramValue) {
                    if (filterPart.indexOf(Char.DOT) != -1) {
                        String[] filterPartArr = DOT_PATTERN.split(filterPart);

                        for (String val : filterPartArr)
                            values.add(val.replace(Char.MINUS, Char.UNDERSCORE));
                    } else {
                        values.add(filterPart.replace(Char.MINUS, Char.UNDERSCORE));
                    }
                }
            }
        }

        return filterPartsMap;
    }

    protected String getRealAttributeCode(String attrCode, Map<String, Attribute> filterAttributes,
        Map<String, String> attributesAliasIndex) {
        if (attrCode == null || filterAttributes == null || filterAttributes.size() == 0)
            return attrCode;

        if (filterAttributes.containsKey(attrCode)) {
            return attrCode;
        }

        if (attributesAliasIndex != null && attributesAliasIndex.size() > 0) {
            attrCode = attributesAliasIndex.get(attrCode/* alias */);
            if (filterAttributes.containsKey(attrCode)) {
                return attrCode;
            }
        }

        return attrCode;
    }

    protected boolean isFilterAttribute(String attrCode, Map<String, Attribute> filterAttributes,
        Map<String, String> attributesAliasIndex) {
        if (attrCode == null || filterAttributes == null || filterAttributes.size() == 0)
            return false;

        boolean isFilterAttribute = filterAttributes.containsKey(attrCode);

        if (!isFilterAttribute && attributesAliasIndex != null && attributesAliasIndex.size() > 0) {
            attrCode = attributesAliasIndex.get(attrCode/* alias */);
            isFilterAttribute = filterAttributes.containsKey(attrCode);
        }

        return isFilterAttribute;
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public Map<String, Set<Object>> getFilterPartsFromURI(String uri, ProductListFilterRule navFilterURLRule) {
        Map<String, Set<Object>> filterPartsMap = new LinkedHashMap<>();

        // Remove leading slash from URI it it exists.
        if (uri.startsWith("/")) {
            uri = uri.substring(1);
        }

        // Search index is lower case, so we make sure that filter parts are
        // too.
        uri = uri.toLowerCase();

        // With which attribute do we start (number of '_' gives us the hint).
        int attributeStartIndex = 0;
        int numLoops = 0;

        // If the URI now starts with an '_', we know that we are not starting
        // with the first attribute.
        while (uri.startsWith(Str.UNDERSCORE)) {
            // We keep on removing the first slash until none exist anymore,
            // counting as we go along.
            uri = uri.replaceFirst(Str.UNDERSCORE, Str.EMPTY);

            attributeStartIndex++;

            // Something could be wrong and we do not want to end up with an
            // endless loop, so break.
            if (numLoops > 100) {
                break;
            }

            numLoops++;
        }

        // The filter part is always the last part of a URL.
        String[] uriParts = SLASH_PATTERN.split(uri);
        String[] filterParts = UNDERSCORE_PATTERN.split(uriParts[uriParts.length - 1]);

        // We now iterate through all the filter-parts, so that we can create a
        // key-value map.
        for (int i = 0; i < filterParts.length; i++) {
            String filterPart = filterParts[i].trim();

            // If the string is empty, then most likely the current filter part
            // starts with more than one underscore.
            // All we need to do is increment the attributeStartIndex so that we
            // end up with the right
            // attribute-URL-position.
            if ("".equals(filterPart)) {
                attributeStartIndex++;
                continue;
            }

            Map<String, Object> filterAttribute = null;

            // Attempt to find with prefix, if one exists.
            if (i > 0 && filterPart.indexOf('-') != -1 && filterParts.length > 1) {
                filterAttribute = findFilterAttributeByPrefix(filterPart, navFilterURLRule);
            }

            // If filter attribute could not be found by prefix (as in most
            // cases), find by position.
            if (filterAttribute == null && navFilterURLRule != null) {
                filterAttribute = navFilterURLRule.findAttributeByPositionInURI(++attributeStartIndex);
            }

            if (filterAttribute != null) {
                String attributeCode = (String) filterAttribute
                    .get(ProductListFilterRule.AttributeField.ATTRIBUTE_CODE);
                ContextObject<String> prefix = ContextObject.valueOf(
                    (List<Map<String, Object>>) filterAttribute.get(ProductListFilterRule.AttributeField.PREFIX));

                // If the filter-part has a prefix, we remove it.
                if (prefix != null && prefix.getStr() != null && filterPart.startsWith(prefix.getStr())) {
                    // We remove the prefix and hyphen (= +1).
                    filterPart = filterPart.substring(prefix.getStr().length() + 1);
                }

                Set<Object> values = new HashSet<>();

                // Turn values into array if multiple values have been selected.
                if (filterPart.indexOf(Char.DOT) != -1) {
                    String[] filterPartArr = DOT_PATTERN.split(filterPart);

                    // if (multipleValuesAllowed)
                    {
                        for (String val : filterPartArr)
                            values.add(val.replace(Char.MINUS, Char.UNDERSCORE));
                    }
                    // else
                    // {
                    // values.add(filterPartArr[0].replace("-", "_"));
                    // }
                } else {
                    values.add(filterPart.replace(Char.MINUS, Char.UNDERSCORE));
                }

                filterPartsMap.put(attributeCode, values);
            }
        }

        return filterPartsMap;
    }

    protected Map<String, Object> findFilterAttributeByPrefix(String filterPart,
        ProductListFilterRule navFilterURLRule) {
        Map<String, Object> filterAttribute = null;

        String[] prefixParts = MINUS_PATTERN.split(filterPart);

        StringBuilder pp = new StringBuilder();

        for (int j = 0; j < prefixParts.length; j++) {
            if (j > 0) {
                pp.append(Char.MINUS);
            }

            pp.append(prefixParts[j]);

            filterAttribute = navFilterURLRule.findAttributeByPrefix(pp.toString());

            if (filterAttribute != null) {
                Boolean prefixMatchEnabled = (Boolean) filterAttribute
                    .get(ProductListFilterRule.AttributeField.PREFIX_MATCH_ENABLED);

                // If prefix match was not allowed, we reset the filter
                // attribute. We do no know this before as we have
                // to locate the attribute first.
                if (prefixMatchEnabled != null && prefixMatchEnabled == false) {
                    filterAttribute = null;
                }

                break;
            }
        }

        return filterAttribute;
    }

    @Override
    public String getAttributeOrConfigProperty(ProductList category, String attrName, String configPropertyName) {
        if (attrName.equals("meta_kw") && app.cpBool_(CATEGORY_SEO_APPEND_PARENT_KEYWORDS, false)) {
            String metaKeywords = getAppendMetaKeywords(category, attrName);
            return metaKeywords;
        }

        if (category.hasAttribute(attrName)) {
            return category.getAttribute(attrName).getStr();
        } else {
            return app.cpStr_(configPropertyName);
        }
    }

    private String getAppendMetaKeywords(ProductList category, String attrName) {
        String metaKeywords = Str.EMPTY;

        if (category.hasAttribute(attrName)) {
            metaKeywords += category.getAttribute(attrName).getStr() + Str.COMMA;
        }

        // TODO: FIX
        // NavigationItem currentNavItem = category;
        //
        // while (currentNavItem.getParent() != null)
        // {
        // currentNavItem = currentNavItem.getParent();
        // if (currentNavItem.hasAttribute(attrName))
        // {
        // metaKeywords += currentNavItem.getAttribute(attrName).getStr() + ",";
        // }
        // }
        //
        // HashSet<String> metaKeywordsHashSet = Sets.newHashSet();
        // String[] metaKeywordsArray = metaKeywords.split(",");
        //
        // for (String metaKeyword : metaKeywordsArray)
        // {
        // metaKeywordsHashSet.add(metaKeyword);
        // }
        //
        // metaKeywords = "";
        //
        // for (String metaKeyword : metaKeywordsHashSet)
        // {
        // metaKeywords += metaKeyword + ",";
        // }

        metaKeywords = metaKeywords.substring(0, metaKeywords.length() - 1);

        return metaKeywords.isEmpty() ? null : metaKeywords;
    }

    public void fixProductListQuery(ProductList productList) {
        if (productList.getQueryNode() == null)
            return;
        while (true) {
            ProductListQueryNode node = productList.getQueryNode();
            Boolean changed = false;
            ReturnValue n = fixQueryNode(node);
            if (n.node == null)
                productList.setQueryNode(null);
            if (!n.changed)
                break;
        }
    }

    private ReturnValue fixQueryNode(ProductListQueryNode node) {
        ReturnValue value = new ReturnValue();
        if (node == null) {
            value.changed = false;
            value.node = null;
            return value;
        }

        if (!node.isValid()) {
            value.changed = true;
            value.node = null;
            return value;
        }

        boolean changedInternal = false;
        if (node.getNodes() != null) {
            List<ProductListQueryNode> nodesForDelete = new ArrayList<>();
            for (ProductListQueryNode n : node.getNodes()) {
                ReturnValue t = fixQueryNode(n);
                if (t.node == null)
                    nodesForDelete.add(n);
                changedInternal = changedInternal || t.changed;
            }
            for (ProductListQueryNode n : nodesForDelete) {
                node.getNodes().remove(n);
            }
            value.changed = changedInternal;
            value.node = node;
            return value;
        }
        value.changed = false;
        value.node = node;
        return value;
    }

    class ReturnValue {
        Boolean changed;
        ProductListQueryNode node;
    }

}
