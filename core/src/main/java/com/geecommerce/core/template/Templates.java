package com.geecommerce.core.template;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.config.SystemConfig;
import com.geecommerce.core.system.model.RequestContext;
import com.geecommerce.core.template.freemarker.directive.AttributeDirective;
import com.geecommerce.core.template.freemarker.directive.AttributeEqualsDirective;
import com.geecommerce.core.template.freemarker.directive.AttributeExistsDirective;
import com.geecommerce.core.template.freemarker.directive.AttributeHasValueDirective;
import com.geecommerce.core.template.freemarker.directive.JsonDirective;
import com.geecommerce.core.template.freemarker.directive.MessageDirective;
import com.geecommerce.core.template.freemarker.directive.PrintDirective;
import com.geecommerce.core.template.freemarker.directive.SetDirective;
import com.geecommerce.core.template.freemarker.directive.SkinDirective;
import com.geecommerce.core.template.freemarker.directive.TruncateDirective;
import com.geecommerce.core.template.freemarker.directive.URLDirective;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class Templates {
    public static final String getBaseTemplatesPath() {
        String templatePath = SystemConfig.GET.val(SystemConfig.APPLICATION_TEMPLATE_PATH);

        if (templatePath == null) {
            throw new IllegalStateException(
                "The System.properties configuration element 'Application.Template.Path' cannot be null");
        }

        templatePath = templatePath.trim();

        StringBuilder sb = new StringBuilder(templatePath);

        if (!templatePath.endsWith("/")) {
            sb.append("/");
        }

        return sb.toString();
    }

    public static final String getSlicesPath() {
        return new StringBuilder(getBaseTemplatesPath()).append("slices").toString();
    }

    public static final String getPagesPath() {
        return new StringBuilder(getBaseTemplatesPath()).append("pages").toString();
    }

    public static final String getWidgetsPath() {
        return new StringBuilder(getBaseTemplatesPath()).append("widgets").toString();
    }

    public static final String getIncludesPath() {
        return new StringBuilder(getBaseTemplatesPath()).append("includes").toString();
    }

    public static final String getLayoutPath() {
        return new StringBuilder(getBaseTemplatesPath()).append("layout").toString();
    }

    public static String render(String templateContent, Map<String, Object> params)
        throws IOException, TemplateException {
        StringWriter sw = new StringWriter();

        App app = App.get();
        ApplicationContext appCtx = app.context();
        RequestContext reqCtx = appCtx.getRequestContext();

        // TODO: replace quick hack with configured value!
        Configuration conf = new Configuration();
        conf.setDefaultEncoding("UTF-8");

        if (reqCtx != null && reqCtx.getLocale() != null)
            conf.setLocale(reqCtx.getLocale());

        conf.setSharedVariable("set", new SetDirective());
        conf.setSharedVariable("url", app.inject(URLDirective.class));
        conf.setSharedVariable("attribute", new AttributeDirective());
        conf.setSharedVariable("attribute_exists", new AttributeExistsDirective());
        conf.setSharedVariable("attribute_equals", new AttributeEqualsDirective());
        conf.setSharedVariable("attribute_has_value", new AttributeHasValueDirective());
        conf.setSharedVariable("message", new MessageDirective());
        conf.setSharedVariable("print", new PrintDirective());
        conf.setSharedVariable("json", new JsonDirective());
        conf.setSharedVariable("skin", new SkinDirective());
        conf.setSharedVariable("truncate", new TruncateDirective());

        Template t = new Template("name", new StringReader(templateContent), conf);
        t.process(params, sw);
        return sw.toString();
    }

}
