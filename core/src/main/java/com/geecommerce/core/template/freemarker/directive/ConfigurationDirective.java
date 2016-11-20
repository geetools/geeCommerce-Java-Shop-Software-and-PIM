package com.geecommerce.core.template.freemarker.directive;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.cache.Cache;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.script.Groovy;
import com.geecommerce.core.system.model.RequestContext;

import freemarker.core.Environment;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNumberModel;
import freemarker.template.utility.DeepUnwrap;

public class ConfigurationDirective implements TemplateDirectiveModel {
    protected static final String FORMAT_TYPE_CURRENCY = "currency";
    protected static final String FORMAT_TYPE_PLAIN_TEXT = "plain-text";
    protected static final String FORMAT_TYPE_SHORT_TEXT = "short-text";
    protected static final String SELF_ROOT_NAME = "self";

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
        throws TemplateException, IOException {
        SimpleScalar pKey = (SimpleScalar) params.get("key");
        SimpleScalar pRoot = (SimpleScalar) params.get("root");
        SimpleScalar pValue = (SimpleScalar) params.get("value");
        TemplateModel pDefault = (TemplateModel) params.get("default");
        SimpleScalar pVar = (SimpleScalar) params.get("var");
        SimpleScalar pFormat = (SimpleScalar) params.get("format");
        SimpleScalar pMessage = (SimpleScalar) params.get("message");
        TemplateBooleanModel pStripTags = (TemplateBooleanModel) params.get("strip_tags");
        TemplateBooleanModel pStripNewlines = (TemplateBooleanModel) params.get("strip_newlines");
        SimpleScalar pReplaceNewlines = (SimpleScalar) params.get("replace_newlines");
        TemplateNumberModel pTruncate = (TemplateNumberModel) params.get("truncate");

        App app = App.get();

        String expression = null;
        String rootName = null;
        String varName = null;
        String format = null;
        String message = null;
        boolean stripTags = false;
        boolean stripNewlines = false;
        String replaceNewlines = null;
        Number truncateAt = null;

        Object confValue = null;

        if (pKey != null) {
            String key = pKey.getAsString();
            confValue = app.cpVal(key);
        }

        if (confValue == null && pDefault == null)
            return;

        if (confValue == null)
            confValue = DeepUnwrap.unwrap(pDefault);

        if (pValue != null)
            expression = pValue.getAsString();

        if (pRoot != null)
            rootName = pRoot.getAsString();

        // Optionally put the result into a parameters map instead of outputting
        // it.
        if (pVar != null)
            varName = pVar.getAsString();

        // Optionally format the string.
        if (pFormat != null)
            format = pFormat.getAsString();

        if (pReplaceNewlines != null)
            replaceNewlines = pReplaceNewlines.getAsString();

        // Optionally strip HTML tags
        if (pStripTags != null)
            stripTags = pStripTags.getAsBoolean();

        // Optionally strip newlines
        if (pStripNewlines != null)
            stripNewlines = pStripNewlines.getAsBoolean();

        // Optionally truncate string
        if (pTruncate != null)
            truncateAt = pTruncate.getAsNumber();

        // Optionally use context-message from DB.
        if (pMessage != null)
            message = pMessage.getAsString();

        Object result = null;

        try {
            if (expression != null) {
                Map<String, Object> bindings = new HashMap<>();

                // Attempt to retrieve the value out of the map using the parsed
                // MVEL expression.
                if (rootName == null) {
                    bindings.put(SELF_ROOT_NAME, confValue);

                    result = Groovy.eval(expression, bindings);
                } else {
                    if (rootName != null)
                        bindings.put(rootName, confValue);

                    bindings.put(SELF_ROOT_NAME, confValue);

                    result = Groovy.eval(expression, bindings);
                }
            } else {
                result = confValue;
            }
        } catch (Throwable t) {
            // Make sure that the error message is not swallowed somewhere
            // unnoticed
            // in dev-mode. This way it is clearly visible in the console.
            if (app.isDevPrintErrorMessages()) {
                t.printStackTrace();
            }
        }

        if (result != null) {
            if (format != null) {
                result = format(result, format);
            }

            if (stripTags) {
                result = com.geecommerce.core.util.Strings.stripTags(result.toString());
            }

            if (stripNewlines) {
                result = com.geecommerce.core.util.Strings.stripNewlines(result.toString());
            }

            if (replaceNewlines != null && !"".equals(replaceNewlines.trim())) {
                result = com.geecommerce.core.util.Strings.replaceNewlines(result.toString(), replaceNewlines);
            }

            if (truncateAt != null) {
                result = com.geecommerce.core.util.Strings.truncateNicely(result.toString(), truncateAt.intValue(),
                    "...");
            }

            if (message != null) {
                result = message(env, result, message);
            }

            if (varName != null) {
                // Sets the result into the current template as if using
                // <#assign name=model>.
                env.setVariable(varName, DefaultObjectWrapper.getDefaultInstance().wrap(result));
            } else {
                // Simply writes the result to the template.
                env.getOut().write(result.toString());
            }
        }

        // System.out.println("PRINT-TIME: " +
        // (System.currentTimeMillis()-start));
    }

    private String message(Environment env, Object result, String message) throws TemplateException, IOException {
        if (result == null && message == null)
            return null;

        if (result == null)
            return message;

        if (message == null)
            return result.toString();

        Map<String, Object> params = new HashMap<>();
        params.put("text", new SimpleScalar(message));
        params.put("param1", new SimpleScalar(result == null ? "" : result.toString()));

        String varKey = new StringBuilder("cb-rsd-").append(message.hashCode()).toString();
        params.put("var", new SimpleScalar(varKey));

        MessageDirective md = new MessageDirective();
        md.execute(env, params, null, null);

        SimpleScalar pVal = (SimpleScalar) env.getVariable(varKey);

        return pVal == null ? message : pVal.getAsString();
    }

    private String format(Object value, String format) {
        ApplicationContext appCtx = App.get().context();

        Locale locale = null;

        if (appCtx != null) {
            RequestContext reqCtx = appCtx.getRequestContext();
            locale = new Locale(reqCtx.getLanguage(), reqCtx.getCountry());
        }

        if (FORMAT_TYPE_CURRENCY.equalsIgnoreCase(format)) {
            return NumberFormat.getCurrencyInstance(locale).format(Double.valueOf(value.toString()));
        } else if (FORMAT_TYPE_PLAIN_TEXT.equalsIgnoreCase(format)) {
            return com.geecommerce.core.util.Strings.stripTags(value.toString());
        } else if (FORMAT_TYPE_SHORT_TEXT.equalsIgnoreCase(format)) {
            String plainText = com.geecommerce.core.util.Strings.stripTags(value.toString());
            plainText = com.geecommerce.core.util.Strings.replaceNewlines(plainText, ",");

            return com.geecommerce.core.util.Strings.truncateNicely(plainText, 150, "...");
        } else {
            return String.format(locale, format, value);
        }
    }

    protected Cache<String, Object> cache() {
        CacheManager cm = App.get().injectable(CacheManager.class);
        return cm.getCache(ConfigurationDirective.class.getName());
    }
}
