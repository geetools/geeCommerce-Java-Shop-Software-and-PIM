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
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class JsDirective implements TemplateDirectiveModel {
    private static final String CACHE_NAME = "gc/js/paths";

    private static final String PARAM_PATH = "path";
    private static final String PARAM_TYPE = "type";
    private static final String PARAM_NAME = "name";
    private static final String PARAM_FETCH = "fetch";
    private static final String PARAM_VAR = "var";
    private static final String PARAM_NO_EXTENSION = "noext";

    private static final String MODULE = "module";
    private static final String API = "api";
    private static final String MAIN = "main";
    private static final String PAGE = "page";

    private static final String MODULE_JS_API_DEFAULT_FILENAME = "api";
    private static final String MODULE_JS_MAIN_DEFAULT_FILENAME = "main";

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
        TemplateBooleanModel pNoExtension = (TemplateBooleanModel) params.get(PARAM_NO_EXTENSION);

        String normalizedPath = null;
        String type = null;
        String name = null;
        String fetch = null;
        String varName = null;
        Boolean noExtension = null;

        if (pPath != null)
            normalizedPath = normalize(pPath.getAsString());

        if (pType != null)
            type = pType.getAsString();

        if (pName != null)
            name = pName.getAsString();

        if (pFetch != null)
            fetch = pFetch.getAsString();

        // Optionally put the result into a parameters map instead of outputting
        // it.
        if (pVar != null)
            varName = pVar.getAsString();

        if (pNoExtension != null)
            noExtension = pNoExtension.getAsBoolean();

        if (noExtension == null)
            noExtension = false;

        if (MODULE.equals(type) && API.equals(fetch)) {
            normalizedPath = MODULE_JS_API_DEFAULT_FILENAME;
        } else if (MODULE.equals(type) && MAIN.equals(fetch)) {
            normalizedPath = MODULE_JS_MAIN_DEFAULT_FILENAME;
        } else if (PAGE.equals(type) && normalizedPath == null) {
            normalizedPath = App.get().getViewPath();
        }

        StringBuilder cacheKey = new StringBuilder(normalizedPath);

        if (MODULE.equals(type)) {
            cacheKey.append(Char.AT).append(Assets.getModuleCode());
        }

        String webpath = c.get(cacheKey.toString());

        if (webpath == null) {
            // ------------------------------------------------------------------
            // Look in module folders.
            // ------------------------------------------------------------------
            if (MODULE.equals(type)) {
                webpath = Assets.jsModuleViewPath(normalizedPath, name);

                if (webpath == null) {
                    webpath = Assets.jsModuleStorePath(normalizedPath, name);
                }

                if (webpath == null) {
                    webpath = Assets.jsModuleMerchantPath(normalizedPath, name);
                }

                if (webpath == null) {
                    webpath = Assets.jsModuleViewPath(normalizedPath, name, true);
                }

                if (webpath == null) {
                    webpath = Assets.jsModuleStorePath(normalizedPath, name, true);
                }

                if (webpath == null) {
                    webpath = Assets.jsModulePath(normalizedPath, name);
                }
            } else if (PAGE.equals(type)) {
                // ---------------------------------------------------------------------------
                // First we check if the file has been overridden in the
                // project's web folder.
                // ---------------------------------------------------------------------------

                webpath = Assets.jsPageViewPath(normalizedPath);

                if (webpath == null) {
                    webpath = Assets.jsPageStorePath(normalizedPath);
                }

                if (webpath == null) {
                    webpath = Assets.jsPageMerchantPath(normalizedPath);
                }

                if (webpath == null) {
                    webpath = Assets.jsPageViewPath(normalizedPath, true);
                }

                if (webpath == null) {
                    webpath = Assets.jsPageStorePath(normalizedPath, true);
                }

                if (webpath == null) {
                    webpath = Assets.jsPageModulePath(normalizedPath, name);
                }

                if (webpath == null) {
                    webpath = Assets.jsPageDefaultPath(normalizedPath);
                }
            } else {
                webpath = Assets.jsViewPath(normalizedPath);

                if (webpath == null) {
                    webpath = Assets.jsStorePath(normalizedPath);
                }

                if (webpath == null) {
                    webpath = Assets.jsMerchantPath(normalizedPath);
                }

                if (webpath == null) {
                    webpath = Assets.jsViewPath(normalizedPath, true);
                }

                if (webpath == null) {
                    webpath = Assets.jsStorePath(normalizedPath, true);
                }

                if (webpath == null) {
                    webpath = Assets.jsModulePath(normalizedPath, name);
                }

                if (webpath == null) {
                    webpath = Assets.jsDefaultPath(normalizedPath);
                }
            }

            if (webpath != null) {
                c.put(cacheKey.toString(), webpath);
            }
        }

        if (webpath != null) {
            if (noExtension && webpath.indexOf(Char.DOT) != -1) {
                if (varName != null) {
                    // Sets the result into the current template as if using
                    // <#assign name=model>.
                    env.setVariable(varName, DefaultObjectWrapper.getDefaultInstance().wrap(webpath.substring(0, webpath.lastIndexOf(Char.DOT))));
                } else {
                    env.getOut().write(webpath.substring(0, webpath.lastIndexOf(Char.DOT)));
                }
            } else {
                if (varName != null) {
                    // Sets the result into the current template as if using
                    // <#assign name=model>.
                    env.setVariable(varName, DefaultObjectWrapper.getDefaultInstance().wrap(webpath));
                } else {
                    env.getOut().write(webpath);
                }
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
