package com.geecommerce.catalog.product.helper;

import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.core.App;
import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.system.ConfigurationKey;
import com.geecommerce.core.system.helper.UrlRewriteHelper;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.util.Strings;
import com.google.inject.Inject;

@Helper
public class DefaultProductListUrlHelper implements ProductListUrlHelper {
    @Inject
    protected App app;

    protected final UrlRewriteHelper urlRewriteHelper;

    protected static final String KEY_PRODUCT_LIST_URL_REWRITE_HTML_EXTENSION = "url_rewrite/product_list/append_html_extension";

    @Inject
    public DefaultProductListUrlHelper(UrlRewriteHelper urlRewriteHelper) {
        this.urlRewriteHelper = urlRewriteHelper;
    }

    @Override
    public void generateUniqueUri(ProductList productList, UrlRewrite urlRewrite, boolean empty) {
        if (!empty)
            urlRewrite.setRequestURI(new ContextObject<String>());

        ContextObject<String> nameCtx = productList.getLabel();

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

            StringBuilder baseURI = new StringBuilder("/");

            if (productList.isSale()) {
                baseURI.append("sale/");
            }

            baseURI.append(Strings.slugify(name));

            boolean addHtmlExtention = app.cpBool_(KEY_PRODUCT_LIST_URL_REWRITE_HTML_EXTENSION, false);
            String ext = addHtmlExtention ? ".html" : "/";

            String uri = baseURI.toString().trim() + ext;

            int c = 1;
            while (!urlRewriteHelper.isUriUnique(ObjectType.PRODUCT_LIST, productList.getId(), uri)) {
                uri = baseURI.toString().trim() + '-' + (c++) + ext;
            }

            urlRewrite.getRequestURI().addOrUpdate(lang, uri);
        }
    }
}
