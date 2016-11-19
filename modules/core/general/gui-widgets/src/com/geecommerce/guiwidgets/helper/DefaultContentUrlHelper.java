package com.geecommerce.guiwidgets.helper;

import java.util.List;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.system.ConfigurationKey;
import com.geecommerce.core.system.helper.UrlRewriteHelper;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.util.Strings;
import com.geecommerce.guiwidgets.model.Content;
import com.google.inject.Inject;

@Helper
public class DefaultContentUrlHelper implements ContentUrlHelper {
    @Inject
    protected App app;

    protected static final String CMS_URI_BASE_URI_PREFIX_KEY = "cms/uri/base_uri_prefix";

    protected static final String CMS_URI_APPEND_HTML_EXTENSION_KEY = "cms/uri/append_html_extension";

    protected static final String CMS_DEFAULT_URI_PREFIX = "/pages/";
    protected static final String HTML_EXTENTION = ".html";

    protected final UrlRewriteHelper urlRewriteHelper;

    @Inject
    public DefaultContentUrlHelper(UrlRewriteHelper urlRewriteHelper) {
        this.urlRewriteHelper = urlRewriteHelper;
    }

    @Override
    public void generateUniqueUri(Content content, UrlRewrite urlRewrite, boolean empty) {
        if (!empty)
            urlRewrite.setRequestURI(new ContextObject<String>());

        ContextObject<String> nameCtx = content.getName();

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

            String cmsUriPrefix = app.cpStr_(CMS_URI_BASE_URI_PREFIX_KEY, CMS_DEFAULT_URI_PREFIX);

            StringBuilder baseURI = new StringBuilder(cmsUriPrefix);

            baseURI.append(Strings.slugify(name));

            boolean addHtmlExtention = app.cpBool_(CMS_URI_APPEND_HTML_EXTENSION_KEY, true);
            String ext = addHtmlExtention ? HTML_EXTENTION : Str.SLASH;

            String uri = baseURI.toString().trim() + ext;

            int c = 1;
            while (!urlRewriteHelper.isUriUnique(ObjectType.CMS, content.getId(), uri)) {
                uri = baseURI.toString().trim() + Char.MINUS + (c++) + ext;
            }

            urlRewrite.getRequestURI().addOrUpdate(lang, uri);
        }
    }
}
