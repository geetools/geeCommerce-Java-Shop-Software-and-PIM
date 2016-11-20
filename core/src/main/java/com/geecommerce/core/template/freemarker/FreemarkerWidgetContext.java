package com.geecommerce.core.template.freemarker;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.core.Char;
import com.geecommerce.core.config.SystemConfig;
import com.geecommerce.core.system.widget.helper.WidgetHelper;
import com.geecommerce.core.template.Templates;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.AbstractWidgetContext;
import com.geecommerce.core.web.api.WidgetController;
import com.geemodule.api.Module;

import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNotFoundException;

public class FreemarkerWidgetContext extends AbstractWidgetContext {

    public static final String WIDGET_ID = "widgetId";

    private Module module;
    private Environment environment;
    private Map<String, Object> params;
    private Map<String, Object> jsParams = new HashedMap();
    private TemplateModel[] loopVars;
    private TemplateDirectiveBody body;

    private static final Logger log = LogManager.getLogger(FreemarkerWidgetContext.class);

    public FreemarkerWidgetContext() {
    }

    public void init(final Module module, final Environment env, final Map<String, Object> params,
        final TemplateModel[] loopVars, final TemplateDirectiveBody body, final HttpServletRequest request,
        final HttpServletResponse response, final ServletContext servletContext) {
        super.init(request, response, servletContext);

        this.environment = env;
        this.params = params;
        this.loopVars = loopVars;
        this.body = body;
        this.module = module;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public TemplateModel[] getLoopVars() {
        return loopVars;
    }

    public TemplateDirectiveBody getBody() {
        return body;
    }

    @Override
    public <T> T getParam(String name) {
        return getParam(name, null);
    }

    @Override
    public <T> T getParam(String name, Class<?> type) {
        return getParam(name, type, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getParam(String name, Class<?> type, Object defaultValue) {
        try {
            Object o = params.get(name);

            if (o != null) {
                if (o instanceof TemplateModel) {
                    return (T) eval(BeansWrapper.getDefaultInstance().unwrap((TemplateModel) o), type, defaultValue);
                } else {
                    return (T) eval(o, type, defaultValue);
                }
            } else {
                TemplateModel tm = hash().get(name);

                if (tm != null) {
                    return (T) eval(BeansWrapper.getDefaultInstance().unwrap(tm), type, defaultValue);
                }
            }
        } catch (TemplateModelException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private Object eval(Object value, Class<?> type, Object defaultValue) {
        if (value == null)
            return defaultValue;

        if (type == null || value.getClass().equals(type)) {
            return value;
        }

        if (type == String.class) {
            return String.valueOf(value);
        } else if (type == Integer.class) {
            if (value instanceof Number)
                return ((Number) value).intValue();
            else
                return Integer.parseInt(String.valueOf(value));
        } else if (type == Long.class) {
            if (value instanceof Number)
                return ((Number) value).longValue();
            else
                return Long.parseLong(String.valueOf(value));
        } else if (type == Double.class) {
            if (value instanceof Number)
                return ((Number) value).doubleValue();
            else
                return Double.parseDouble(String.valueOf(value));
        } else if (type == Float.class) {
            if (value instanceof Number)
                return ((Number) value).floatValue();
            else
                return Float.parseFloat(String.valueOf(value));
        } else if (type == Boolean.class) {
            if (value instanceof Number)
                return ((Number) value).intValue() == 1 ? true : false;
            else
                return String.valueOf(value).equalsIgnoreCase("true") ? true : false;
        } else if (type == Id.class) {
            return Id.valueOf(value);
        }

        return value;
    }

    @Override
    public void setParam(String name, Object value) {
        hash().put(name, value);
    }

    @Override
    public void setJsParam(String name, Object value) {
        if (value instanceof Id) {
            jsParams.put(name, ((Id) value).str());
        } else {
            jsParams.put(name, value);
        }
    }

    @Override
    public Map<String, Object> getJsParams() {
        return jsParams;
    }

    @Override
    public void renderContent(String content) {
        renderContent(content, null);
    }

    @Override
    public void renderContent(String content, TemplateModel data) {
        long start = System.currentTimeMillis();

        // String templateSuffix =
        // SystemConfig.GET.val(SystemConfig.APPLICATION_TEMPLATE_SUFFIX);

        // if (templateSuffix == null)
        // {
        // throw new
        // IllegalStateException("The System.properties configuration element
        // 'Application.Template.Suffix' cannot be null");
        // }

        // templateSuffix = templateSuffix.trim();

        // Configuration conf = new Configuration();
        Configuration conf = FreemarkerHelper.newConfig(getServletContext(), module);

        try {
            Template temp = new Template("templateName", new StringReader(content), conf);
            // Template temp = conf.getTemplate(new
            // StringBuilder(Templates.getWidgetsPath()).append("/").append(path).append(templateSuffix).toString());

            temp.process(data, environment.getOut());
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }

        // if (log.isTraceEnabled())
        // {
        // log.trace("Processing template '" + path + "' took: " +
        // (System.currentTimeMillis() - start) + "ms.");
        // }
    }

    @Override
    public void render(String path) {
        render(path, hash());
    }

    @Override
    public void render(TemplateModel data) {
        WidgetController widgetController = getParam(FreemarkerConstant.FREEMARKER_TEMPLATE_SELF_VAR);

        String view = widgetController.getView(this);

        render(new StringBuilder(widgetController.getCode()).append(Char.SLASH).append(view).toString(), data);
    }

    @Override
    public void render() {
        WidgetController widgetController = getParam(FreemarkerConstant.FREEMARKER_TEMPLATE_SELF_VAR);

        String view = widgetController.getView(this);

        render(new StringBuilder(widgetController.getCode()).append(Char.SLASH).append(view).toString(), hash());
    }

    @Override
    public void render(String path, TemplateModel data) {
        long start = System.currentTimeMillis();

        String templateSuffix = SystemConfig.GET.val(SystemConfig.APPLICATION_TEMPLATE_SUFFIX);

        if (templateSuffix == null) {
            throw new IllegalStateException(
                "The System.properties configuration element 'Application.Template.Suffix' cannot be null");
        }

        templateSuffix = templateSuffix.trim();

        // Configuration conf = new Configuration();
        Configuration conf = FreemarkerHelper.newConfig(getServletContext(), module);

        WidgetController widgetController = null;

        String widgetId = UUID.randomUUID().toString();
        setJsParam(WIDGET_ID, widgetId);
        setParam(WIDGET_ID, widgetId);

        try {
            widgetController = getParam(FreemarkerConstant.FREEMARKER_TEMPLATE_SELF_VAR);

            // System.out.println("Widget template path: " + new
            // StringBuilder(Templates.getWidgetsPath()).append("/").append(path).append(templateSuffix).toString());

            Template temp = conf.getTemplate(new StringBuilder(Templates.getWidgetsPath()).append("/").append(path)
                .append(templateSuffix).toString());

            temp.process(data, environment.getOut());

            if (widgetController.isCssEnabled()) {
                String cssStylesTag = WidgetHelper.getSkinStylesTag(widgetController, this);

                if (cssStylesTag != null)
                    environment.getOut().write(cssStylesTag);
            }

            if (widgetController.isJavascriptEnabled()) {
                String jsScriptTag = WidgetHelper.getJsScriptTag(widgetController, this);

                if (jsScriptTag != null)
                    environment.getOut().write(jsScriptTag);
            }
        } catch (TemplateNotFoundException e) {
            log.warn("Unable to render widget '" + (widgetController == null ? null : widgetController.getCode())
                + "' because the template could not be found", e);

            e.printStackTrace();
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }

        if (log.isTraceEnabled()) {
            log.trace("Processing template '" + path + "' took: " + (System.currentTimeMillis() - start) + "ms.");
        }
    }

    @Override
    public Writer getOut() {
        return environment.getOut();
    }

    @Override
    public void invokeBody(Writer writer) {
        try {
            body.render(writer);
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final SimpleHash hash() {
        return app.registryGet(FreemarkerConstant.FREEMARKER_REQUEST_TEMPLATE_MODEL);
    }
}
