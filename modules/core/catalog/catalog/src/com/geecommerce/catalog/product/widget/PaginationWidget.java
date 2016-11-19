package com.geecommerce.catalog.product.widget;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geecommerce.catalog.product.ProductListConstant;
import com.geecommerce.catalog.product.ProductListURIBuilder;
import com.geecommerce.catalog.product.model.ProductListFilterRule;
import com.geecommerce.core.system.attribute.model.Attribute;
import com.geecommerce.core.web.annotation.Widget;
import com.geecommerce.core.web.api.AbstractWidgetController;
import com.geecommerce.core.web.api.WidgetContext;
import com.geecommerce.core.web.api.WidgetController;

@Widget(name = "pagination")
public class PaginationWidget extends AbstractWidgetController implements WidgetController {
    private final ProductListFilterRule filterRule;
    private final Map<String, Attribute> filterAttributes;
    private final Map<String, Attribute> attributeAliasIndex;
    private final Map<String, Set<Object>> uriFilterParts;
    private final Map<String, Set<Object>> paramFilterParts;

    private static final String FIELD_KEY_ATTRIBUTE_CODE = "a";
    private static final String FIELD_KEY_LANGUAGE = "l";
    private static final String FIELD_KEY_COUNTRY = "c";

    private Map<String, Set<Object>> basicFilterParts;

    public PaginationWidget() {
        this.filterRule = app.registryGet(ProductListConstant.FILTER_RULE);
        this.filterAttributes = app.registryGet(ProductListConstant.FILTER_ATTRIBUTES);
        this.uriFilterParts = app.registryGet(ProductListConstant.URI_FILTER_PARTS);
        this.paramFilterParts = app.registryGet(ProductListConstant.PARAM_FILTER_PARTS);
        this.attributeAliasIndex = buildAttributeAliasIndex();
    }

    @Override
    public void execute(WidgetContext widgetCtx, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws Exception {
        this.basicFilterParts = extractBasicFilterParts(request);
        widgetCtx.render("pagination/pagination");
    }

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

    public String filterVanillaURI() {
        String baseURI = app.getRewrittenURI() == null ? app.getOriginalURI() : app.getRewrittenURI();

        String newURI = app.getInjectable(ProductListURIBuilder.class).newURI(baseURI).usingFilterRule(filterRule).withAttributeMetaData(filterAttributes).withAttributeAliasIndex(attributeAliasIndex)
            .havingURIFilterParts(uriFilterParts)
            .havingParamFilterParts(paramFilterParts).build();

        return newURI == null ? "#" : newURI;
    }

    public String filterBaseVanillaURI() {
        String baseURI = app.getRewrittenURI() == null ? app.getOriginalURI() : app.getRewrittenURI();

        String newURI = app.getInjectable(ProductListURIBuilder.class).newURI(baseURI).usingFilterRule(filterRule).withAttributeMetaData(filterAttributes).withAttributeAliasIndex(attributeAliasIndex)
            .havingURIFilterParts(uriFilterParts)
            .havingParamFilterParts(paramFilterParts).havingBasicFilterParts(basicFilterParts).build();

        return newURI == null ? "#" : newURI;
    }

    private Attribute findFilterAttribute(String fieldKey) {
        if (filterAttributes == null || fieldKey == null)
            return null;

        String attrCode = fieldKey;

        if (fieldKey.startsWith("att_")) {
            Map<String, String> keyParts = extractKeyParts(fieldKey);
            attrCode = keyParts.get(FIELD_KEY_ATTRIBUTE_CODE);
        }

        return filterAttributes.get(attrCode);
    }

    protected static Map<String, String> extractKeyParts(String key) {
        Map<String, String> keyParts = new HashMap<>();

        if (key.contains("_slug_")) {
            int pos = key.lastIndexOf("_slug_");
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
        } else if (key.endsWith("_hash")) {
            keyParts.put(FIELD_KEY_ATTRIBUTE_CODE, key.substring(4, key.length() - 5));
        } else if (key.contains("_raw_")) {
            int pos = key.lastIndexOf("_raw_");
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
        } else if (key.endsWith("_is_option")) {
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

                if (attr.getProductListFilterKeyAlias() != null && attr.getProductListFilterKeyAlias().getStr() != null) {
                    attributeAliasIndex.put(attr.getProductListFilterKeyAlias().getStr(), attr.getCode());
                }
            }
        }

        return filterAttributes;
    }
}
