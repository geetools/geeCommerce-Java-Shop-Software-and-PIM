package com.geecommerce.core.template.freemarker.directive;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class ParamDirective implements TemplateDirectiveModel {
    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
	SimpleScalar keyScalar = (SimpleScalar) params.get("key");
	SimpleScalar valueScalar = (SimpleScalar) params.get("value");

	String key = null;
	String value = null;

	// --------------------------------------------------------
	// Attempt to fetch the key.
	// --------------------------------------------------------

	if (keyScalar != null) {
	    key = keyScalar.getAsString().trim();
	}

	if (key == null || "".equals(key.trim())) {
	    throw new IllegalArgumentException("The key parameter cannot be null or empty when specifying an import parameter");
	}

	// --------------------------------------------------------
	// Attempt to get value from directive body.
	// --------------------------------------------------------

	if (body != null) {
	    StringWriter sw = new StringWriter();

	    try {
		body.render(sw);
		String bodyValue = sw.toString();

		if (bodyValue != null && !"".equals(bodyValue.trim())) {
		    value = bodyValue.trim();
		}
	    } finally {
		IOUtils.closeQuietly(sw);
	    }
	}

	// --------------------------------------------------------
	// Attempt to get value from directive attribute.
	// --------------------------------------------------------

	if (value == null && valueScalar != null) {
	    value = valueScalar.getAsString().trim();
	}

	if (key != null && value != null) {
	    env.getOut().write(new StringBuilder(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(value, "UTF-8")).append("\n").toString());
	}
    }
}
