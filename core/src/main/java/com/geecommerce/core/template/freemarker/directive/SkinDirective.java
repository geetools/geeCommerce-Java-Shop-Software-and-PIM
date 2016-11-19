package com.geecommerce.core.template.freemarker.directive;

import java.io.IOException;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.cache.Cache;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.web.Assets;

import freemarker.core.Environment;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class SkinDirective implements TemplateDirectiveModel {
    private static final String CACHE_NAME = "gc/skin/paths";

    private static final String PARAM_PATH = "path";
    private static final String PARAM_TYPE = "type";
    private static final String PARAM_NAME = "name";
    private static final String PARAM_FETCH = "fetch";
    private static final String PARAM_VAR = "var";

    private static final String STYLES = "styles";
    private static final String MODULE = "module";
    private static final String MODULE_DEFAULT_STYLES_URI = "css/styles.css";

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        CacheManager cm = App.get().inject(CacheManager.class);
        Cache<String, String> c = cm.getCache(CACHE_NAME);

        SimpleScalar pPath = (SimpleScalar) params.get(PARAM_PATH);
        SimpleScalar pType = (SimpleScalar) params.get(PARAM_TYPE);
        SimpleScalar pName = (SimpleScalar) params.get(PARAM_NAME);
        SimpleScalar pFetch = (SimpleScalar) params.get(PARAM_FETCH);
        SimpleScalar pVar = (SimpleScalar) params.get(PARAM_VAR);

        String normalizedPath = null;
        String type = null;
        String name = null;
        String fetch = null;
        String var = null;

        if (pPath != null)
            normalizedPath = normalize(pPath.getAsString());

        if (pType != null)
            type = pType.getAsString();

        if (pName != null)
            name = pName.getAsString();

        if (pFetch != null)
            fetch = pFetch.getAsString();

        if (pVar != null)
            var = pVar.getAsString();

        if (MODULE.equals(type) && STYLES.equals(fetch)) {
            normalizedPath = MODULE_DEFAULT_STYLES_URI;
        }

        StringBuilder cacheKey = new StringBuilder(normalizedPath);

        if (MODULE.equals(type)) {
            cacheKey.append(Char.AT).append(Assets.getModuleCode());
        }

        String webpath = c.get(cacheKey.toString());

        if (webpath == null) {
            // ------------------------------------------------------------------
            // Module skin assets.
            // ------------------------------------------------------------------
            if (MODULE.equals(type)) {
                // Look for:
                // projects/<merchant>/web/skin/<store>/<view>/modules/<name>/...
                // Look for:
                // webapp/skin/<merchant>/<store>/<view>/modules/<name>/...
                webpath = Assets.skinModuleViewPath(normalizedPath, name);

                if (webpath == null) {
                    // Look for:
                    // projects/<merchant>/web/skin/<store>/modules/<name>/...
                    // Look for:
                    // webapp/skin/<merchant>/<store>/modules/<name>/...
                    webpath = Assets.skinModuleStorePath(normalizedPath, name);
                }

                if (webpath == null) {
                    // Look for: projects/<merchant>/web/skin/modules/<name>/...
                    // Look for: webapp/skin/<merchant>/modules/<name>/...
                    webpath = Assets.skinModuleMerchantPath(normalizedPath, name);
                }

                if (webpath == null) {
                    // Look for:
                    // projects/<merchant>/web/skin/<store>/<parent-view>/modules/<name>/...
                    // Look for:
                    // webapp/skin/<merchant>/<store>/<parent-view>/modules/<name>/...
                    webpath = Assets.skinModuleViewPath(normalizedPath, name, true);
                }

                if (webpath == null) {
                    // Look for:
                    // projects/<merchant>/web/skin/<parent-store>/modules/<name>/...
                    // Look for:
                    // webapp/skin/<merchant>/<parent-store>/modules/<name>/...
                    webpath = Assets.skinModuleStorePath(normalizedPath, name, true);
                }

                if (webpath == null) {
                    // Look for: modules/<name>/web/skin/...
                    webpath = Assets.skinModulePath(normalizedPath, name);
                }
            }
            // ------------------------------------------------------------------
            // Non-module skin assets.
            // ------------------------------------------------------------------
            else {
                // Look for: projects/<merchant>/web/skin/<store>/<view>/...
                // Look for: webapp/skin/<merchant>/<store>/<view>/...
                webpath = Assets.skinViewPath(normalizedPath);

                if (webpath == null) {
                    // Look for: projects/<merchant>/web/skin/<store>/...
                    // Look for: webapp/skin/<merchant>/<store>/...
                    webpath = Assets.skinStorePath(normalizedPath);
                }

                if (webpath == null) {
                    // Look for: projects/<merchant>/web/skin/...
                    // Look for: webapp/skin/<merchant>/...
                    webpath = Assets.skinMerchantPath(normalizedPath);
                }

                if (webpath == null) {
                    // Look for:
                    // projects/<merchant>/web/skin/<store>/<parent-view>/...
                    // Look for:
                    // webapp/skin/<merchant>/<store>/<parent-view>/...
                    webpath = Assets.skinViewPath(normalizedPath, true);
                }

                if (webpath == null) {
                    // Look for: projects/<merchant>/web/skin/<parent-store>/...
                    // Look for: webapp/skin/<merchant>/<parent-store>/...
                    webpath = Assets.skinStorePath(normalizedPath, true);
                }

                // Revert to default webapp path if none of the above exist.
                if (webpath == null) {
                    // Look for: webapp/default/skin/...
                    webpath = Assets.skinDefaultPath(normalizedPath);
                }
            }

            c.put(cacheKey.toString(), webpath);
        }

        if (webpath != null) {
            if (var != null) {
                env.setVariable(var, DefaultObjectWrapper.getDefaultInstance().wrap(webpath));
            } else {
                env.getOut().write(webpath);
            }
        }
    }

    public String normalize(String path) {
        int pos = path.indexOf(Str.QUESTION_MARK);

        if (pos != -1) {
            return path.substring(0, pos);
        } else {
            return path;
        }
    }
}
