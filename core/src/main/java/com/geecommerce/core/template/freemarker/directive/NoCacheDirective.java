package com.geecommerce.core.template.freemarker.directive;

import java.io.IOException;
import java.util.Map;

import com.geecommerce.core.App;
import com.geecommerce.core.system.repository.UrlRewrites;
import com.google.inject.Inject;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class NoCacheDirective implements TemplateDirectiveModel {

    @Inject
    private NoCacheDirective(UrlRewrites urlRewrites) {

    }

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
        throws TemplateException, IOException {
        App app = App.get();

        app.servletResponse().setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP
                                                                                                 // 1.1.
        app.servletResponse().setHeader("Pragma", "no-cache"); // HTTP 1.0.
        app.servletResponse().setDateHeader("Expires", 0);
    }
}
