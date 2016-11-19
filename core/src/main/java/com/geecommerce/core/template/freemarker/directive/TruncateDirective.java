package com.geecommerce.core.template.freemarker.directive;

import java.io.IOException;
import java.util.Map;

import com.geecommerce.core.util.Strings;

import freemarker.core.Environment;
import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.utility.DeepUnwrap;

public class TruncateDirective implements TemplateDirectiveModel {
    private static final String TYPE_PLAIN_TEXT = "plain-text";
    private static final String TYPE_HTML_LIST = "html-list";

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
	// SimpleScalar value = (SimpleScalar) params.get("value");
	TemplateModel value = (TemplateModel) params.get("value");
	SimpleScalar append = (SimpleScalar) params.get("append");
	SimpleNumber truncateAt = (SimpleNumber) params.get("truncateAt");
	SimpleNumber maxRows = (SimpleNumber) params.get("maxRows");

	SimpleScalar type = (SimpleScalar) params.get("type");

	if (value != null && truncateAt != null) {
	    String text = null;

	    if (value instanceof StringModel) {
		text = ((StringModel) value).getWrappedObject().toString();
	    } else {
		text = DeepUnwrap.unwrap(value).toString();
	    }

	    if (append == null)
		append = new SimpleScalar("...");

	    if (type == null)
		type = new SimpleScalar(TYPE_PLAIN_TEXT);

	    if (maxRows == null)
		maxRows = new SimpleNumber(0);

	    String truncatedText = text;

	    if (TYPE_HTML_LIST.equalsIgnoreCase(type.getAsString().trim())) {
		truncatedText = Strings.truncateHtmlList(text, truncateAt.getAsNumber().intValue(), maxRows.getAsNumber().intValue(), append.getAsString());
	    } else {
		truncatedText = Strings.truncateNicely(text, truncateAt.getAsNumber().intValue(), append.getAsString());
	    }

	    env.getOut().write(truncatedText);
	}
    }
}
