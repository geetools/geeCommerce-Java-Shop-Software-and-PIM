package com.geecommerce.core.system.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import com.geecommerce.core.App;
import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.system.ConfigurationKey;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.repository.UrlRewrites;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Requests;
import com.google.inject.Inject;

public class DefaultUrlRewriteHelper implements UrlRewriteHelper {
    @Inject
    protected App app;

    protected final UrlRewrites urlRewrites;

    @Inject
    public DefaultUrlRewriteHelper(UrlRewrites urlRewrites) {
        this.urlRewrites = urlRewrites;
    }

    @Override
    public boolean isExcludedFromURLRewriting(String path) {
        ServletContext servletContext = app.servletContext();

        String skipUrlRewritingForPatterns = servletContext.getInitParameter("URLRewriteFilter.Skip");
        boolean skipUrlRewriting = false;

        // See if there are any URLs that we want to exclude from URL-rewriting
        if (skipUrlRewritingForPatterns != null && !"".equals(skipUrlRewritingForPatterns.trim())) {
            String[] skipUrlPatterns = skipUrlRewritingForPatterns.split(",");

            for (String skipUrlPattern : skipUrlPatterns) {
                if (Requests.uriPatternMatches(path.toLowerCase(), skipUrlPattern.trim().toLowerCase())) {
                    skipUrlRewriting = true;
                    break;
                }
            }
        }

        return skipUrlRewriting;
    }

    @Override
    public boolean isUriUnique(ObjectType targetObjectType, Id targetObjectId, String uri) {
        UrlRewrite url = urlRewrites.havingURI(uri);
        if (url == null)
            return true;
        if (targetObjectId != null && targetObjectType != null && url.getTargetObjectType().equals(targetObjectType)
            && url.getTargetObjectId().equals(targetObjectId))
            return true;
        return false;
    }

    @Override
    public Map<String, Boolean> isUriUnique(ObjectType targetObjectType, Id targetObjectId,
        ContextObject<String> uris) {
        Map<String, Boolean> result = new HashMap<>();
        List<String> availableLaguages = app.cpStrList_(ConfigurationKey.I18N_AVAILABLE_LANGUAGES);
        for (String lang : availableLaguages) {
            Map<String, Object> nameMap = uris.findEntryFor(lang);
            if (nameMap == null || nameMap.size() == 0)
                continue;

            String uri = (String) nameMap.get("val");
            if (uri == null || uri.isEmpty())
                continue;

            UrlRewrite url = urlRewrites.havingURI(uri);
            if (url == null) {
                result.put(lang, true);
                continue;
            }
            if (targetObjectId != null && targetObjectType != null && url.getTargetObjectType().equals(targetObjectType)
                && url.getTargetObjectId().equals(targetObjectId)) {
                result.put(lang, true);
                continue;
            }

            result.put(lang, false);
        }
        result.put("abs", true);
        return result;
    }

    @Override
    public Map<String, Boolean> isUriUnique(ContextObject<String> uris) {
        return isUriUnique(null, null, uris);
    }

    @Override
    public Id[] filterCompletedTargetObjectIds(List<UrlRewrite> urlRewrites, Id... allTargetObjectIds) {
        List<Id> newTargetObjectIds = new ArrayList<>();

        List<Id> completedTargetObjectIds = toTargetObjectIds(urlRewrites);

        for (Id id : allTargetObjectIds) {
            if (!completedTargetObjectIds.contains(id)) {
                newTargetObjectIds.add(id);
            }
        }

        return newTargetObjectIds.toArray(new Id[newTargetObjectIds.size()]);
    }

    private List<Id> toTargetObjectIds(List<UrlRewrite> urlRewrites) {
        List<Id> ids = new ArrayList<>();

        if (urlRewrites == null || urlRewrites.size() == 0)
            return ids;

        for (UrlRewrite urlRewrite : urlRewrites) {
            ids.add(urlRewrite.getTargetObjectId());
        }

        return ids;
    }
}
