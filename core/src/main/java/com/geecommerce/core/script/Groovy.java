package com.geecommerce.core.script;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.reflect.Reflect;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.merchant.model.View;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.util.NullableConcurrentHashMap;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

public class Groovy {
    private static final GroovyShell shell = new GroovyShell();
    private static final GroovyClassLoader GCL = new GroovyClassLoader(Groovy.class.getClassLoader());

    private static final String CACHE_NAME = "gc/groovy";
    private static final String CACHE_KEY_PREFIX_GROOVY_TYPE = "groovyType";
    private static final String CACHE_KEY_PREFIX_MATCHER_CONDITION = "matcherCondition";
    private static final String CACHE_KEY_PREFIX_EVAL_CLASS = "matcherCondition";
    private static final String CODE_BASE = "cb";

    private static final String MERCHANT_CODE_KEY = "m_code";
    private static final String STORE_CODE_KEY = "s_code";
    private static final String VIEW_CODE_KEY = "v_code";
    private static final String REQUEST_CTX_LANGUAGE_KEY = "rc_language";
    private static final String REQUEST_CTX_COUNTRY_KEY = "rc_country";
    private static final String REQUEST_CTX_LOCALE_KEY = "rc_locale";
    private static final String HTTP_SERVERNAME_KEY = "http_servername";
    private static final String HTTP_SERVERPORT_KEY = "http_serverport";
    private static final String HTTP_SCHEME_KEY = "http_scheme";
    private static final String HTTP_URI_KEY = "http_uri";
    private static final String HTTP_URL_KEY = "http_url";
    private static final String HTTP_QUERYSTRING_KEY = "http_querystring";
    private static final String HTTP_METHOD_KEY = "http_method";
    private static final String HTTP_SERVLETPATH_KEY = "http_servletpath";

    private static final Map<String, Object> cache = new NullableConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static final <T> T parseClass(String source) throws Exception {
        return (T) parseClass(source, GroovyClass.class, true, GCL);
    }

    @SuppressWarnings("unchecked")
    public static final <T> T parseClass(String source, GroovyClassLoader gcl) throws Exception {
        return (T) parseClass(source, GroovyClass.class, true, gcl);
    }

    public static final <T> T parseClass(String source, Class<T> returnType) throws Exception {
        return parseClass(source, returnType, true, GCL);
    }

    public static final <T> T parseClass(String source, Class<T> returnType, GroovyClassLoader gcl) throws Exception {
        return parseClass(source, returnType, true, gcl);
    }

    public static final <T> T parseClass(String source, Class<T> returnType, boolean singleton) throws Exception {
        return parseClass(source, returnType, singleton, GCL);
    }

    @SuppressWarnings("unchecked")
    public static final <T> T parseClass(String source, Class<T> returnType, boolean singleton, GroovyClassLoader gcl) throws Exception {
        if (source == null || returnType == null)
            return null;

        String hashCode = String.valueOf(source.hashCode());
        String cacheKey = new StringBuilder(CACHE_KEY_PREFIX_GROOVY_TYPE).append(Str.AT).append(hashCode).toString();

        Object objOrClass = cache.get(cacheKey);

        if (objOrClass == null) {
            GroovyCodeSource gcs = new GroovyCodeSource(source, hashCode, CODE_BASE);

            Class<T> clazz = null;

            // Use custom GroovyClassLoader if provided.
            if (gcl != null) {
                clazz = gcl.parseClass(gcs);
            } else {
                clazz = GCL.parseClass(gcs);
            }

            T obj = clazz.newInstance();

            // If we are in singleton mode, we cache the object.
            if (singleton) {
                T cachedObject = (T) cache.putIfAbsent(cacheKey, obj);

                if (cachedObject != null)
                    obj = cachedObject;
            }
            // Otherwise we just cache the parsed class.
            else {
                Class<T> cachedClass = (Class<T>) cache.putIfAbsent(cacheKey, clazz);

                if (cachedClass != null)
                    clazz = cachedClass;
            }

            return obj;
        } else {
            if (singleton) {
                return (T) objOrClass;
            } else {
                return ((Class<T>) objOrClass).newInstance();
            }
        }
    }

    public static final boolean conditionMatches(String matcherCondition, String key, Object value) {
        Map<String, Object> bindings = new HashMap<String, Object>();
        bindings.put(key, value);

        return conditionMatches(matcherCondition, bindings);
    }

    public static final boolean conditionMatches(String matcherCondition, Map<String, Object> bindings) {
        try {
            addContextBindings(bindings);

            Object conditionMatches = execute(matcherCondition, bindings);

            if (conditionMatches == null || !(conditionMatches instanceof Boolean))
                throw new RuntimeException("The return value of the matcherCondition '" + matcherCondition + "' cannot be null and must be of type Boolean!");

            return (Boolean) conditionMatches;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static <T> T eval(String expression) throws Exception {
        return eval(expression, (Map<String, Object>) null, (Set<String>) null, (GroovyClassLoader) null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T eval(String expression, Object arg) throws Exception {
        Map<String, Object> bindings = new HashMap<>();

        if (Model.class.isAssignableFrom(arg.getClass())) {
            bindings.put(Reflect.getModelVarName((Class<Model>) arg.getClass()), arg);
        } else {
            bindings.put(Reflect.getTypeVarName((Class<Model>) arg.getClass()), arg);
        }

        return eval(expression, bindings, null, null);
    }

    public static <T> T eval(String expression, String key, Object value) throws Exception {
        Map<String, Object> bindings = new HashMap<>();
        bindings.put(key, value);

        return eval(expression, bindings, null, null);
    }

    public static <T> T eval(String expression, String key, Object value, Set<String> imports) throws Exception {
        Map<String, Object> bindings = new HashMap<>();
        bindings.put(key, value);

        return eval(expression, bindings, imports, null);
    }

    public static <T> T eval(String expression, String key, Object value, Set<String> imports, GroovyClassLoader gcl) throws Exception {
        Map<String, Object> bindings = new HashMap<>();
        bindings.put(key, value);

        return eval(expression, bindings, imports, gcl);
    }

    public static <T> T eval(String expression, String key1, Object value1, String key2, Object value2) throws Exception {
        Map<String, Object> bindings = new HashMap<>();
        bindings.put(key1, value1);
        bindings.put(key2, value2);

        return eval(expression, bindings, null, null);
    }

    public static <T> T eval(String expression, String key1, Object value1, String key2, Object value2, Set<String> imports) throws Exception {
        Map<String, Object> bindings = new HashMap<>();
        bindings.put(key1, value1);
        bindings.put(key2, value2);

        return eval(expression, bindings, imports, null);
    }

    public static <T> T eval(String expression, String key1, Object value1, String key2, Object value2, Set<String> imports, GroovyClassLoader gcl) throws Exception {
        Map<String, Object> bindings = new HashMap<>();
        bindings.put(key1, value1);
        bindings.put(key2, value2);

        return eval(expression, bindings, imports, gcl);
    }

    public static <T> T eval(String expression, Map<String, Object> bindings) throws Exception {
        return eval(expression, bindings, null, null);
    }

    public static <T> T eval(String expression, Map<String, Object> bindings, Set<String> imports) throws Exception {
        return eval(expression, bindings, imports, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T eval(String expression, Map<String, Object> bindings, Set<String> imports, GroovyClassLoader gcl) throws Exception {
        if (expression == null)
            return null;

        String hashCode = String.valueOf(expression.hashCode());
        String cacheKey = new StringBuilder(CACHE_KEY_PREFIX_EVAL_CLASS).append(Str.AT).append(hashCode).toString();

        GroovyClass gc = (GroovyClass) cache.get(cacheKey);

        if (gc == null) {
            String groovyClass = buildEvalClass(expression, bindings, imports);

            gc = Groovy.parseClass(groovyClass, gcl == null ? GCL : gcl);

            GroovyClass cachedClass = (GroovyClass) cache.putIfAbsent(cacheKey, gc);

            if (cachedClass != null)
                gc = cachedClass;
        }

        Map<String, Object> allBindings = new HashMap<>(bindings);
        addContextBindings(allBindings);

        try {
            return (T) gc.execute(allBindings);
        } catch (Throwable t) {
            // Make sure that the error message is not swallowed somewhere
            // unnoticed
            // in dev-mode. This way it is clearly visible in the console.
            if (App.get().isDevPrintErrorMessages()) {
                t.printStackTrace();
            }

            throw t;
        }
    }

    private static final String buildEvalClass(String expression, Map<String, Object> bindings, Set<String> imports) {
        StringBuilder groovyClass = new StringBuilder("import com.geecommerce.core.script.GroovyClass\n").append("import com.geecommerce.core.*\n").append("import com.geecommerce.core.type.*\n")
            .append("import com.geecommerce.core.util.*\n").append("import com.geecommerce.core.enums.*\n")
            .append("import java.util.*\n").append("import groovy.json.JsonSlurper\n");

        if (imports != null && !imports.isEmpty()) {
            for (String imp : imports) {
                groovyClass.append("import ").append(imp).append(Char.NEWLINE);
            }
        }

        groovyClass.append("class GroovyEvaluation implements GroovyClass {\n\n").append("  Object execute(Map<String, Object> bindings) {\n\n");

        Set<String> keys = bindings.keySet();

        for (String key : keys) {
            groovyClass.append("def " + key + " = bindings['" + key + "']\n");
        }

        groovyClass.append(expression).append("\n").append("  }\n").append("}\n");

        return groovyClass.toString();
    }

    private static final Object execute(String methodBody, Map<String, Object> bindings) throws Exception {
        Binding binding = new Binding();

        // Bind parameters
        if (bindings != null && bindings.size() > 0) {
            Set<String> keys = bindings.keySet();

            for (String key : keys) {
                binding.setVariable(key, bindings.get(key));
            }
        }

        String cacheKey = new StringBuilder(CACHE_KEY_PREFIX_MATCHER_CONDITION).append(Str.AT).append(methodBody.hashCode()).toString();

        Script parsedScript = (Script) cache.get(cacheKey);

        if (parsedScript == null) {
            parsedScript = shell.parse(methodBody);
            Script cachedParsedScript = (Script) cache.putIfAbsent(cacheKey, parsedScript);

            if (cachedParsedScript != null)
                parsedScript = cachedParsedScript;
        }

        parsedScript.setBinding(binding);

        return parsedScript.run();
    }

    public static final void addContextBindings(Map<String, Object> bindings) {
        if (bindings == null)
            return;

        ApplicationContext appCtx = App.get().getApplicationContext();
        RequestContext reqCtx = appCtx.getRequestContext();
        Merchant merchant = appCtx.getMerchant();
        Store store = appCtx.getStore();
        View view = appCtx.getView();
        HttpServletRequest request = App.get().getServletRequest();

        bindings.put(MERCHANT_CODE_KEY, merchant.getCode());

        if (store != null)
            bindings.put(STORE_CODE_KEY, store.getCode());

        if (view != null)
            bindings.put(VIEW_CODE_KEY, view.getCode());

        if (reqCtx != null) {
            bindings.put(REQUEST_CTX_LANGUAGE_KEY, reqCtx.getLanguage());
            bindings.put(REQUEST_CTX_COUNTRY_KEY, reqCtx.getCountry());
            bindings.put(REQUEST_CTX_LOCALE_KEY, reqCtx.getLocale());
        }

        String name = Thread.currentThread().getName();

        // We do not add request information if we are in a separate thread that
        // has been started by CommerceBoard. An
        // example
        // of this would be the asynchronous observer thread, where the request
        // is null when the main http-thread ends.
        if (request != null && !name.startsWith("cb")) {
            bindings.put(HTTP_SERVERNAME_KEY, request.getServerName());
            bindings.put(HTTP_SERVERPORT_KEY, request.getServerPort());
            bindings.put(HTTP_SCHEME_KEY, request.getScheme());
            bindings.put(HTTP_URI_KEY, request.getRequestURI());
            bindings.put(HTTP_URL_KEY, request.getRequestURL());
            bindings.put(HTTP_QUERYSTRING_KEY, request.getQueryString());
            bindings.put(HTTP_METHOD_KEY, request.getMethod());
            bindings.put(HTTP_SERVLETPATH_KEY, request.getServletPath());
        }
    }
}
