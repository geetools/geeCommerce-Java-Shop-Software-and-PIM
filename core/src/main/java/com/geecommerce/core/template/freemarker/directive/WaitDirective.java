package com.geecommerce.core.template.freemarker.directive;

import java.io.IOException;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class WaitDirective implements TemplateDirectiveModel {
    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
        throws TemplateException, IOException {
        try {
            SimpleNumber pFor = (SimpleNumber) params.get("for");

            Number forMillis = null;

            if (pFor != null) {
                forMillis = pFor.getAsNumber();

                try {
                    Thread.sleep(forMillis.longValue());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
