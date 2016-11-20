package com.geecommerce.catalog.product.widget;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.catalog.product.ProductListConstant;
import com.geecommerce.catalog.product.ProductListURIBuilder;
import com.geecommerce.catalog.product.model.ProductListFilterRule;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.enums.FilterType;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.util.Strings;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.google.common.collect.Maps;

@Widget(name = "product_list_filter")
public class ProductListFilterWidget extends AbstractWidgetController implements WidgetController {
    private final ProductListFilterRule filterRule;
    private final Map<String, Attribute> filterAttributes;
    private final Map<String, Attribute> attributeAliasIndex;
    private final Map<String, Set<Object>> uriFilterParts;
    private final Map<String, Set<Object>> paramFilterParts;

    private static final String FIELD_KEY_ATTRIBUTE_CODE = "a";
    private static final String FIELD_KEY_LANGUAGE = "l";
    private static final String FIELD_KEY_COUNTRY = "c";

    private static final String ATT_PREFIX = "att_";
    private static final String IS_OPTION_SUFFIX = "_is_option";
    private static final String HASH_SUFFIX = "_hash";
    private static final String SLUG_PART = "_slug_";
    private static final String RAW_PART = "_raw_";

    private static final Pattern noNumPattern = Pattern.compile("[^0-9]+");

    private Map<String, Set<Object>> basicFilterParts;

    public ProductListFilterWidget() {
        this.filterRule = app.registryGet(ProductListConstant.FILTER_RULE);
        this.filterAttributes = app.registryGet(ProductListConstant.FILTER_ATTRIBUTES);
        this.uriFilterParts = app.registryGet(ProductListConstant.URI_FILTER_PARTS);
        this.paramFilterParts = app.registryGet(ProductListConstant.PARAM_FILTER_PARTS);
        this.attributeAliasIndex = buildAttributeAliasIndex();
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response,
        ServletContext servletContext) throws Exception {
        this.basicFilterParts = extractBasicFilterParts(request);

        String view = widgetCtx.getParam("view");

        if (!Str.isEmpty(view)) {
            widgetCtx.render(view);
        } else {
            widgetCtx.render("product/list_filter");
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Map<String, Set<Object>> extractBasicFilterParts(HttpServletRequest request) {
        Map<String, String[]> parameterMap = new LinkedHashMap(request.getParameterMap());

        Map<String, Set<Object>> fixedParamFilterParts = new LinkedHashMap();
        for (String key : parameterMap.keySet()) {
            String[] strings = parameterMap.get(key);
            Set<Object> set = new HashSet<>();
            for (String string : strings) {
                set.add(string);
            }
            fixedParamFilterParts.put(key, set);
        }
        return fixedParamFilterParts;
    }

    public String filterURI(String attrCode, String filterLabel) {
        String baseURI = app.getRewrittenURI() == null ? app.getOriginalURI() : app.getRewrittenURI();

        String newURI = app.injectable(ProductListURIBuilder.class).newURI(baseURI).usingFilterRule(filterRule)
            .withAttributeMetaData(filterAttributes).withAttributeAliasIndex(attributeAliasIndex)
            .havingURIFilterParts(uriFilterParts).havingParamFilterParts(paramFilterParts)
            // .havingBasicFilterParts(basicFilterParts)
            .havingValue(attrCode, filterLabel).build();

        return newURI == null ? Str.HASH : newURI;
    }

    public String filterRemoveURI(String attrCode, String filterLabel) {
        String baseURI = app.getRewrittenURI() == null ? app.getOriginalURI() : app.getRewrittenURI();

        String newURI = app.injectable(ProductListURIBuilder.class).newURI(baseURI).usingFilterRule(filterRule)
            .withAttributeMetaData(filterAttributes).withAttributeAliasIndex(attributeAliasIndex)
            .havingURIFilterParts(uriFilterParts).havingParamFilterParts(paramFilterParts)
            // .havingBasicFilterParts(basicFilterParts)
            .removeValue(attrCode, filterLabel).build();

        return newURI == null ? Str.HASH : newURI;
    }

    public String filterRangeURI(String attrCode, Double fromRange, Double toRange) {
        String baseURI = app.getRewrittenURI() == null ? app.getOriginalURI() : app.getRewrittenURI();

        String newURI = app.injectable(ProductListURIBuilder.class).newURI(baseURI).usingFilterRule(filterRule)
            .withAttributeMetaData(filterAttributes).withAttributeAliasIndex(attributeAliasIndex)
            .havingURIFilterParts(uriFilterParts).havingParamFilterParts(paramFilterParts)
            .havingRangeValue(attrCode, fromRange, toRange).build();

        return newURI == null ? Str.HASH : newURI;
    }

    public String resetURI(String fieldKey) {
        String baseURI = app.getRewrittenURI() == null ? app.getOriginalURI() : app.getRewrittenURI();

        Map<String, Set<Object>> uriFilterPartsCopy = null;
        Object removed = null;
        String attrCode = fieldKey;

        if (fieldKey.startsWith(ATT_PREFIX)) {
            Map<String, String> keyParts = extractKeyParts(fieldKey);
            attrCode = keyParts.get(FIELD_KEY_ATTRIBUTE_CODE);
        }

        if (uriFilterParts != null && uriFilterParts.size() > 0) {
            uriFilterPartsCopy = Maps.newLinkedHashMap(uriFilterParts);
            removed = uriFilterPartsCopy.remove(attrCode);
        }

        Map<String, Set<Object>> paramFilterPartsCopy = null;

        if (removed == null && paramFilterParts != null && paramFilterParts.size() > 0) {
            paramFilterPartsCopy = Maps.newLinkedHashMap(paramFilterParts);
            paramFilterPartsCopy.remove(attrCode);
        }

        String newURI = app.injectable(ProductListURIBuilder.class).newURI(baseURI).usingFilterRule(filterRule)
            .withAttributeMetaData(filterAttributes).withAttributeAliasIndex(attributeAliasIndex)
            .havingURIFilterParts(uriFilterPartsCopy == null ? uriFilterParts : uriFilterPartsCopy)
            .havingParamFilterParts(paramFilterPartsCopy == null ? paramFilterParts : paramFilterPartsCopy).build();

        return newURI == null ? Str.HASH : newURI;
    }

    public String filterVanillaURI() {
        String baseURI = app.getRewrittenURI() == null ? app.getOriginalURI() : app.getRewrittenURI();

        String newURI = app.injectable(ProductListURIBuilder.class).newURI(baseURI).usingFilterRule(filterRule)
            .withAttributeMetaData(filterAttributes).withAttributeAliasIndex(attributeAliasIndex)
            .havingURIFilterParts(uriFilterParts).havingParamFilterParts(paramFilterParts).build();

        return newURI == null ? Str.HASH : newURI;
    }

    public int numActive(String fieldKey) {
        String attrCode = fieldKey;

        if (fieldKey.startsWith(ATT_PREFIX)) {
            Map<String, String> keyParts = extractKeyParts(fieldKey);
            attrCode = keyParts.get(FIELD_KEY_ATTRIBUTE_CODE);
        }

        Integer numActive = 0;

        if (uriFilterParts != null && uriFilterParts.size() > 0) {
            Set<Object> values = uriFilterParts.get(attrCode);

            if (values != null && values.size() > 0) {
                numActive = values.size();
            }
        }

        if (numActive == null && paramFilterParts != null && paramFilterParts.size() > 0) {
            Set<Object> values = paramFilterParts.get(attrCode);

            if (values != null && values.size() > 0) {
                Attribute filterAttribute = filterAttributes.get(fieldKey);
                FilterType filterType = filterAttribute == null ? null : filterAttribute.getProductListFilterType();

                if (filterType != null && filterType == FilterType.RANGE) {
                    String paramValue = String.valueOf(values.iterator().next());

                    Matcher m = noNumPattern.matcher(paramValue);
                    paramValue = m.replaceAll(Str.EMPTY);

                    numActive = 1;
                } else {
                    numActive = values.size();
                }
            }
        }

        return numActive == null ? 0 : numActive;

    }

    public boolean isActive(String fieldKey, String filterLabel) {
        String attrCode = fieldKey;
        String value = filterLabel;

        if (fieldKey.startsWith(ATT_PREFIX)) {
            Map<String, String> keyParts = extractKeyParts(fieldKey);
            attrCode = keyParts.get(FIELD_KEY_ATTRIBUTE_CODE);
            value = Strings.slugify(filterLabel).replace(Char.MINUS, Char.UNDERSCORE);
        } else {
            Attribute filterAttribute = filterAttributes.get(fieldKey);
            FilterType filterType = filterAttribute.getProductListFilterType();

            if (filterType != null && filterType == FilterType.RANGE) {
                Matcher m = noNumPattern.matcher(filterLabel);
                value = m.replaceAll(Str.EMPTY);
            }
        }

        Boolean isActive = null;

        if (uriFilterParts != null && uriFilterParts.size() > 0) {
            Set<Object> values = uriFilterParts.get(attrCode);

            if (values != null && values.size() > 0) {
                isActive = values.contains(value);
            }
        }

        if (isActive == null && paramFilterParts != null && paramFilterParts.size() > 0) {
            Set<Object> values = paramFilterParts.get(attrCode);

            if (values != null && values.size() > 0) {
                Attribute filterAttribute = filterAttributes.get(fieldKey);
                FilterType filterType = filterAttribute == null ? null : filterAttribute.getProductListFilterType();

                if (filterType != null && filterType == FilterType.RANGE) {
                    String paramValue = String.valueOf(values.iterator().next());

                    Matcher m = noNumPattern.matcher(paramValue);
                    paramValue = m.replaceAll(Str.EMPTY);

                    isActive = paramValue.equals(value);
                } else {
                    isActive = values.contains(value);
                }
            }
        }

        return isActive == null ? false : isActive;
    }

    public boolean isMultiFilter(String attrCode) {
        Boolean isMultiple = false;

        Attribute attr = findFilterAttribute(attrCode);

        if (attr != null) {
            isMultiple = attr.isProductListFilterMulti();
        }

        return isMultiple;
    }

    private Attribute findFilterAttribute(String fieldKey) {
        if (filterAttributes == null || fieldKey == null)
            return null;

        String attrCode = fieldKey;

        if (fieldKey.startsWith(ATT_PREFIX)) {
            Map<String, String> keyParts = extractKeyParts(fieldKey);
            attrCode = keyParts.get(FIELD_KEY_ATTRIBUTE_CODE);
        }

        return filterAttributes.get(attrCode);
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

    protected Map<String, Attribute> buildAttributeAliasIndex() {
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

        return filterAttributes;
    }
}
