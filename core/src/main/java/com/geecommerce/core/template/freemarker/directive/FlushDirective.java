package com.geecommerce.core.template.freemarker.directive;

import java.io.IOException;
import java.util.Map;

import com.geecommerce.core.App;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class FlushDirective implements TemplateDirectiveModel {
    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
        throws TemplateException, IOException {
        env.getOut().flush();
        App.get().servletResponse().getOutputStream().flush();
    }
}
