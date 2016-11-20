package com.geecommerce.catalog.product.helper;

import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.system.ConfigurationKey;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.system.helper.UrlRewriteHelper;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.util.Strings;
import com.google.inject.Inject;

@Helper
public class DefaultProductUrlHelper implements ProductUrlHelper {
    @Inject
    protected App app;

    protected final UrlRewriteHelper urlRewriteHelper;

    protected static final String CATALOG_PRODUCT_URI_BASE_URI_PREFIX_KEY = "catalog/product/uri/base_uri_prefix";

    protected static final String CATALOG_PRODUCT_URI_APPEND_HTML_EXTENSION_KEY = "catalog/product/uri/append_html_extension";

    protected static final String CATALOG_PRODUCT_DEFAULT_URI_PREFIX = "/products/";
    protected static final String HTML_EXTENTION = ".html";

    @Inject
    public DefaultProductUrlHelper(UrlRewriteHelper urlRewriteHelper) {
        this.urlRewriteHelper = urlRewriteHelper;
    }

    @Override
    public void generateUniqueUri(Product product, UrlRewrite urlRewrite, boolean empty) {
        if (!empty)
            urlRewrite.setRequestURI(new ContextObject<String>());

        AttributeValue nameAV = product.getAttribute("name");
        AttributeValue name2AV = product.getAttribute("name2");
        AttributeValue productGroupAV = product.getAttribute("product_group");
        AttributeValue programmeAV = product.getAttribute("programme");

        if (nameAV == null || (productGroupAV == null && programmeAV == null && name2AV == null))
            return;

        ContextObject<String> nameCtx = nameAV.getValue();
        ContextObject<String> name2Ctx = null;
        ContextObject<String> productGroupCtx = null;
        ContextObject<String> programmeCtx = null;
        ContextObject<String> colorCtx = null;

        if (productGroupAV != null) {
            AttributeOption productGroupAO = productGroupAV.getFirstAttributeOption();
            if (productGroupAO != null)
                productGroupCtx = productGroupAO.getLabel();
        }

        if (programmeAV != null) {
            AttributeOption programmeAO = programmeAV.getFirstAttributeOption();
            if (programmeAO != null)
                programmeCtx = programmeAO.getLabel();
        }

        AttributeValue colorAV = product.getAttribute("color");
        if (colorAV != null) {
            AttributeOption colorAO = colorAV.getFirstAttributeOption();
            if (colorAO != null)
                colorCtx = colorAO.getLabel();
        }

        List<String> availableLaguages = app.cpStrList_(ConfigurationKey.I18N_AVAILABLE_LANGUAGES);

        for (String lang : availableLaguages) {
            if (empty) {
                Map<String, Object> urlMap = urlRewrite.getRequestURI().findEntryFor(lang);
                if (urlMap != null)
                    continue;
            }

            // Name
            Map<String, Object> nameMap = nameCtx.findEntryFor(lang);
            if (nameMap == null || nameMap.size() == 0)
                continue;

            String name = (String) nameMap.get(ContextObject.VALUE);
            if (name == null || name.isEmpty())
                continue;

            // Name2
            Map<String, Object> name2Map = null;
            if (name2AV != null) {
                name2Ctx = name2AV.getValue();
                if (name2Ctx != null && name2Ctx.size() > 0)
                    name2Map = name2Ctx.findEntryFor(lang);
            }

            // Product group
            Map<String, Object> productGroupMap = null;
            if (productGroupCtx != null)
                productGroupMap = productGroupCtx.findEntryFor(lang);

            // Programme
            Map<String, Object> programmeMap = null;
            if (programmeCtx != null)
                programmeMap = programmeCtx.findEntryFor(lang);

            // Make sure that at least one of the 3 name2, product_group or
            // programme exist, otherwise continue.
            if ((name2Map == null || name2Map.size() == 0) && (productGroupMap == null || productGroupMap.size() == 0)
                && (programmeMap == null || programmeMap.size() == 0))
                continue;

            String name2 = null;
            if (name2Map != null && name2Map.size() > 0)
                name2 = (String) name2Map.get(ContextObject.VALUE);

            String productGroup = null;
            String programme = null;

            // Only bother with product_group and programme if name2 does not
            // exist.
            if (name2 != null && !name2.isEmpty()) {
                if (productGroupMap != null && productGroupMap.size() > 0) {
                    productGroup = (String) productGroupMap.get(ContextObject.VALUE);
                } else if (programmeMap != null && programmeMap.size() > 0) {
                    programme = (String) programmeMap.get(ContextObject.VALUE);
                }
            }

            // Make sure that at least one of the 3 name2, product_group or
            // programme exist, otherwise continue.
            if ((name2 == null || name2.isEmpty()) && (productGroup == null || productGroup.isEmpty())
                && (programme == null || programme.isEmpty()))
                continue;

            // Optionally we can also add a color if one exists.
            String color = null;
            if (colorCtx != null && colorCtx.size() > 0) {
                Map<String, Object> colorMap = colorCtx.findEntryFor(lang);
                if (colorMap != null && colorMap.size() > 0)
                    color = (String) colorMap.get(ContextObject.VALUE);
            }

            String productUriPrefix = app.cpStr_(CATALOG_PRODUCT_URI_BASE_URI_PREFIX_KEY,
                CATALOG_PRODUCT_DEFAULT_URI_PREFIX);

            StringBuilder baseURI = new StringBuilder(productUriPrefix);

            if (name2 != null) {
                baseURI.append(Strings.slugify(name2));

                if (baseURI.charAt(baseURI.length() - 1) != Char.MINUS)
                    baseURI.append(Char.MINUS);
            } else if (productGroup != null) {
                baseURI.append(Strings.slugify(productGroup));

                if (baseURI.charAt(baseURI.length() - 1) != Char.MINUS)
                    baseURI.append(Char.MINUS);
            } else if (programme != null) {
                baseURI.append(Strings.slugify(programme));

                if (baseURI.charAt(baseURI.length() - 1) != Char.MINUS)
                    baseURI.append(Char.MINUS);
            }

            baseURI.append(Strings.slugify(name));

            if (color != null) {
                if (baseURI.charAt(baseURI.length() - 1) != Char.MINUS)
                    baseURI.append(Char.MINUS);

                baseURI.append(Strings.slugify(color));
            }

            boolean addHtmlExtention = app.cpBool_(CATALOG_PRODUCT_URI_APPEND_HTML_EXTENSION_KEY, true);
            String ext = addHtmlExtention ? HTML_EXTENTION : Str.SLASH;

            String uri = baseURI.toString().trim() + ext;

            int c = 1;
            while (!urlRewriteHelper.isUriUnique(ObjectType.PRODUCT, product.getId(), uri)) {
                uri = baseURI.toString().trim() + Char.MINUS + (c++) + ext;
            }

            urlRewrite.getRequestURI().addOrUpdate(lang, uri);
        }
    }
}
