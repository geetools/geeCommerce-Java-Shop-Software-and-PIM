package com.geecommerce.core.template.freemarker.directive;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.cache.Cache;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.repository.UrlRewrites;
import com.geecommerce.core.type.ContextObjects;
import com.geecommerce.core.web.DefaultServletRequestWrapper;
import com.google.inject.Inject;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class CacheDirective implements TemplateDirectiveModel {
    private static final String KEY_WEB_CONTENT_CACHE_ENABLED = "general/web/content_cache/enabled";

    private static final String CACHE_NAME = "gc/web/cache_directive";

    private static final List<String> paramWhiteList = new ArrayList<>();

    static {
        paramWhiteList.add("page");
        paramWhiteList.add("limit");
    }

    private static final List<String> adminParams = new ArrayList<>();

    static {
        adminParams.add("xpage");
        adminParams.add("xref");
        adminParams.add("xt");
    }

    private final UrlRewrites urlRewrites;

    @Inject
    private CacheDirective(UrlRewrites urlRewrites) {
        this.urlRewrites = urlRewrites;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        App app = App.get();

        SimpleScalar pKey = (SimpleScalar) params.get("key");

        String key = null;

        if (pKey != null)
            key = pKey.getAsString();
        else
            key = app.getOriginalURI();

        // Nothing to cache.
        if (body == null)
            return;

        // System.out.println("-------------------------------------------------");

        String content = null;

        if (!isCacheable() || app.previewHeaderExists()) {
            StringWriter sw = new StringWriter();
            body.render(sw);
            content = sw.toString();
        } else {
            CacheManager cm = app.inject(CacheManager.class);
            Cache<String, String> c = cm.getCache(CACHE_NAME);

            String path = app.getOriginalURI();

            if (path.endsWith(Str.SLASH))
                path = path.substring(0, path.lastIndexOf(Char.SLASH));

            // System.out.println("PATH BEFORE: " + path);

            if (!Str.isEmpty(path) && !Str.SLASH.equals(path.trim())) {
                UrlRewrite urlRewrite = urlRewrites.forTargetURI(path);

                if (urlRewrite != null) {
                    String requestPath = ContextObjects.findCurrentLanguage(urlRewrite.getRequestURI());

                    if (!Str.isEmpty(requestPath))
                        path = requestPath;
                }
            }

            // System.out.println("PATH AFTER: " + path);

            String queryString = normalize(app.getOriginalQueryString());

            String cacheKey = new StringBuilder(key).append(Char.AT).append(path).append(Str.isEmpty(queryString) ? Str.EMPTY : Str.QUESTION_MARK + queryString).toString();

            // System.out.println("Using cacheKey: " + cacheKey + " --- " +
            // app.getOriginalQueryString());

            content = c.get(cacheKey);

            if (content == null || app.refreshHeaderExists()) {
                // System.out.println("NOT FROM CACHE: " + path);

                StringWriter sw = new StringWriter();
                body.render(sw);
                content = sw.toString();

                // String cacheHeader =
                // app.getServletResponse().getHeader("X-CB-Cache-Page");

                // if (!(cacheHeader != null && "ban".equals(cacheHeader)))
                {
                    c.put(cacheKey, content);
                }
            } else {
                // System.out.println("FROM CACHE: " + path);
            }
        }

        // Write the generated output to the stream.
        if (!Str.isEmpty(content))
            env.getOut().write(content);
    }

    @SuppressWarnings("unchecked")
    private boolean isCacheable() {
        boolean isContentCacheEnabled = App.get().cpBool_(KEY_WEB_CONTENT_CACHE_ENABLED, true);

        if (!isContentCacheEnabled)
            return false;

        DefaultServletRequestWrapper request = (DefaultServletRequestWrapper) App.get().getServletRequest();

        Map<String, ?> paramMap = request.getUncheckedParameterMap();

        if (paramMap != null && paramMap.size() > 0) {
            Set<String> keys = paramMap.keySet();

            for (String key : keys) {
                if (!paramWhiteList.contains(key) && !adminParams.contains(key)) {
                    return false;
                }
            }
        }

        return true;
    }

    private String normalize(String queryString) {
        if (Str.isEmpty(queryString))
            return Str.EMPTY;

        if (queryString.contains("xpage=refresh"))
            queryString = queryString.replace("xpage=refresh", Str.EMPTY);

        if (queryString.contains("xpage=preview"))
            queryString = queryString.replace("xpage=preview", Str.EMPTY);

        if (queryString.contains("xref=adm"))
            queryString = queryString.replace("xref=adm", Str.EMPTY);

        if (queryString.contains("xt="))
            queryString = queryString.replaceFirst("xt=[0-9]+", Str.EMPTY);

        queryString = queryString.replace("&&", Str.AMPERSAND);

        if (queryString.endsWith(Str.AMPERSAND))
            queryString = queryString.substring(0, queryString.lastIndexOf(Char.AMPERSAND));

        if (queryString.length() == 1 && (Str.AMPERSAND.equals(queryString) || Str.QUESTION_MARK.equals(queryString)))
            queryString = Str.EMPTY;

        return queryString.startsWith(Str.AMPERSAND) ? queryString.substring(1) : queryString;
    }
}
