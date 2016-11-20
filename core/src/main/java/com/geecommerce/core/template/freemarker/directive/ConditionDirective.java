package com.geecommerce.core.template.freemarker.directive;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.cache.Cache;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.script.Groovy;
import com.geecommerce.core.service.api.Model;

import freemarker.core.Environment;
import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.utility.DeepUnwrap;

public class ConditionDirective implements TemplateDirectiveModel {

    private static final String SELF_ROOT_NAME = "self";
    private static final String KEY_VALUE = "value";

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
        throws TemplateException, IOException {
        TemplateModel pSource = (TemplateModel) params.get("src");

        SimpleScalar pRoot = (SimpleScalar) params.get("root");
        SimpleScalar pRootType = (SimpleScalar) params.get("srcType");
        SimpleScalar pCondition = (SimpleScalar) params.get("condition");
        SimpleScalar pScript = (SimpleScalar) params.get("script");
        TemplateModel pDefault = (TemplateModel) params.get("default");

        if (pSource == null || pCondition == null && pScript == null)
            throw new IllegalArgumentException("The parameters 'source' and 'value' cannot be null [src=" + pSource
                + ", conditon=" + pCondition + ", script=" + pScript + "].");

        Object source = null;
        String rootName = null;
        Class<?> rootType = null;
        String modelRootName = null;

        if (pSource instanceof StringModel) {
            source = ((StringModel) pSource).getWrappedObject();
        } else if (pSource instanceof SimpleHash) {
            source = ((SimpleHash) pSource).toMap();
        } else {
            source = DeepUnwrap.unwrap(pSource);
        }

        Object result = null;

        if (pCondition != null) {
            String expression = pCondition.getAsString();

            // Optionally set a root name to use instead of #this.
            if (pRoot != null) {
                rootName = pRoot.getAsString();
            } else if (Model.class.isAssignableFrom(source.getClass())) {
                int dotPos = expression.indexOf('.');

                if (dotPos != -1) {
                    rootName = expression.substring(0, dotPos);
                }

                modelRootName = Reflect.getModelVarName((Class<Model>) source.getClass());

                if (rootName == null) {
                    rootName = modelRootName;
                }
            }

            if (pRootType != null) {
                String _rootType = pRootType.getAsString();

                if (_rootType != null && !"".equals(_rootType.trim())) {
                    try {
                        rootType = Class.forName(_rootType);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            try {
                Map<String, Object> bindings = new HashMap<>();

                // Attempt to retrieve the value out of the map using the parsed
                // OGNL expression.
                if (rootName == null && modelRootName == null) {
                    bindings.put(SELF_ROOT_NAME, source);

                    result = Groovy.eval(expression, bindings);
                } else {
                    if (rootName != null)
                        bindings.put(rootName, source);

                    if (modelRootName != null)
                        bindings.put(modelRootName, source);

                    bindings.put(SELF_ROOT_NAME, source);

                    result = Groovy.eval(expression, bindings);
                }
            } catch (Throwable t) {
                t.printStackTrace();

                // Make sure that the error message is not swallowed somewhere
                // unnoticed
                // in dev-mode. This way it is clearly visible in the console.
                if (App.get().isDevPrintErrorMessages()) {
                    t.printStackTrace();
                }
            }
            if (result == null && pDefault != null)
                result = DeepUnwrap.unwrap(pDefault);
        }

        if (pScript != null) {

            String isConditionMatchedScript = App.get().cpStr_(pScript.getAsString());

            if (isConditionMatchedScript != null) {
                try {
                    result = Groovy.conditionMatches(isConditionMatchedScript, KEY_VALUE, source);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        if (result != null && result instanceof Boolean && body != null) {
            if ((Boolean) result) {
                body.render(env.getOut());
            }
        }

    }

    protected Cache<String, Object> cache() {
        CacheManager cm = App.get().injectable(CacheManager.class);
        return cm.getCache(ConditionDirective.class.getName());
    }
}
