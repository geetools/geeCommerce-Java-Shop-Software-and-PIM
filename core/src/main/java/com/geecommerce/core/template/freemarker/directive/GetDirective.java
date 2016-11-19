package com.geecommerce.core.template.freemarker.directive;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.geecommerce.core.Str;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.service.Models;
import com.geecommerce.core.service.api.Model;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.web.annotation.Directive;

import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.NumberModel;
import freemarker.ext.beans.StringModel;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Directive("getproduct")
public class GetDirective implements TemplateDirectiveModel {
    private static final Logger log = LogManager.getLogger(GetDirective.class);

    private final RestService restService;

    @Inject
    private GetDirective(RestService restService) {
	this.restService = restService;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
	if (log.isTraceEnabled()) {
	    log.trace(params);
	}

	SimpleScalar pType = (SimpleScalar) params.get("type");
	TemplateModel pId = (TemplateModel) params.get("id");
	SimpleScalar pVar = (SimpleScalar) params.get("var");

	String type = null;
	String varName = null;

	if (pType != null)
	    type = pType.getAsString();

	if (pVar != null)
	    varName = pVar.getAsString();

	if (pId != null && !Str.isEmpty(type)) {
	    Object beanModel = null;

	    if (pId instanceof SimpleScalar)
		beanModel = ((SimpleScalar) pId).getAsString();

	    if (pId instanceof SimpleNumber)
		beanModel = ((SimpleNumber) pId).getAsNumber();

	    else if (pId instanceof StringModel)
		beanModel = ((StringModel) pId).getAsString();

	    else if (pId instanceof NumberModel)
		beanModel = ((NumberModel) pId).getAsNumber();

	    else if (pId instanceof BeanModel)
		beanModel = ((BeanModel) pId).getWrappedObject();

	    Id id = Id.valueOf(beanModel);

	    Class<? extends Model> modelClass = Models.findBy(type);

	    if (modelClass != null) {
		Object obj = restService.get(modelClass, id);

		if (obj != null)
		    env.setVariable(Str.isEmpty(varName) ? modelClass.getSimpleName() : varName, DefaultObjectWrapper.getDefaultInstance().wrap(obj));
	    }
	}
    }
}
