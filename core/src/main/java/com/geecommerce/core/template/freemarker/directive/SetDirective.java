package com.geecommerce.core.template.freemarker.directive;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import freemarker.core.Environment;
import freemarker.ext.beans.StringModel;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.utility.DeepUnwrap;

public class SetDirective implements TemplateDirectiveModel {
    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
	TemplateModel pValue = (TemplateModel) params.get("value");
	SimpleScalar pVar = (SimpleScalar) params.get("var");

	if (pVar == null)
	    throw new IllegalArgumentException("The var parameter cannot be null in set-directive");

	String var = pVar.getAsString();
	Object value = null;

	if (pValue != null) {
	    if (pValue instanceof StringModel) {
		value = ((StringModel) pValue).getWrappedObject();
	    } else if (pValue instanceof SimpleHash) {
		value = ((SimpleHash) pValue).toMap();
	    } else {
		value = DeepUnwrap.unwrap(pValue);
	    }
	} else if (body != null) {
	    StringWriter sw = new StringWriter();

	    try {
		body.render(sw);
		value = sw.toString().trim();
	    } finally {
		IOUtils.closeQuietly(sw);
	    }
	}

	env.setVariable(var, DefaultObjectWrapper.getDefaultInstance().wrap(value));
    }
}
