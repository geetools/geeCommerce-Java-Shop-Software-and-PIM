package com.geecommerce.core.template.freemarker.directive;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class TimeDirective implements TemplateDirectiveModel {

    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
	long start = System.currentTimeMillis();

	StringWriter sw = new StringWriter();
	body.render(sw);

	env.getOut().write(sw.toString() + "<small>" + (System.currentTimeMillis() - start) + "ms</small>");
    }
}
