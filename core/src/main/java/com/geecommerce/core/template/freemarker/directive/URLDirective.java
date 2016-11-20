package com.geecommerce.core.template.freemarker.directive;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.geecommerce.core.App;
import com.geecommerce.core.Str;
import com.geecommerce.core.service.TargetSupport;
import com.geecommerce.core.system.helper.TargetSupportHelper;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.repository.UrlRewrites;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.ContextObjects;
import com.google.inject.Inject;

import freemarker.core.Environment;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.utility.DeepUnwrap;

public class URLDirective implements TemplateDirectiveModel {
    private final UrlRewrites urlRewrites;

    @Inject
    private URLDirective(UrlRewrites urlRewrites) {
        this.urlRewrites = urlRewrites;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
        throws TemplateException, IOException {
        SimpleScalar pPath = (SimpleScalar) params.get("path");
        TemplateModel pTarget = (TemplateModel) params.get("target");
        SimpleScalar pVar = (SimpleScalar) params.get("var");

        String path = null;
        String varName = null;

        App app = App.get();

        // String path has precedence.
        if (pPath != null) {
            path = pPath.getAsString();

            if (!Str.isEmpty(path) && !Str.SLASH.equals(path.trim())) {
                UrlRewrite urlRewrite = urlRewrites.forTargetURI(path);

                if (urlRewrite != null) {
                    String requestPath = ContextObjects.findCurrentLanguage(urlRewrite.getRequestURI());

                    if (!Str.isEmpty(requestPath))
                        path = requestPath;
                }
            }
        }

        // If no path was set, attempt other objects.
        if (path == null && pTarget != null) {
            Object target = DeepUnwrap.unwrap(pTarget);

            if (target instanceof TargetSupport) {
                path = app.helper(TargetSupportHelper.class).findURI((TargetSupport) target);
            } else if (target instanceof ContextObject) {
                path = ContextObjects.findCurrentLanguageOrGlobal((ContextObject<String>) target);
            } else {
                path = target.toString();
            }
        }

        // Optionally put the result into a parameters map instead of outputting
        // it.
        if (pVar != null)
            varName = pVar.getAsString();

        if (path != null) {
            HttpServletResponse response = app.servletResponse();

            if (varName != null) {
                // Sets the result into the current template as if using
                // <#assign name=model>.
                env.setVariable(varName, DefaultObjectWrapper.getDefaultInstance().wrap(response.encodeURL(path)));
            } else {
                // Simply writes the result to the template.
                env.getOut().write(response.encodeURL(path));
            }
        }
    }
}
