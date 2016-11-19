package com.geecommerce.catalog.product.web.directive;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.Str;
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
public class ProductDirective implements TemplateDirectiveModel {
    private static final Logger log = LogManager.getLogger(ProductDirective.class);

    private final Products products;

    private static final String PRODUCT_KEY = "product";

    @Inject
    private ProductDirective(Products products) {
	this.products = products;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
	if (log.isTraceEnabled()) {
	    log.trace(params);
	}

	TemplateModel pId = (TemplateModel) params.get("id");
	SimpleScalar pArticleNumber = (SimpleScalar) params.get("article_number");
	SimpleScalar pVar = (SimpleScalar) params.get("var");

	String articleNumber = null;
	String varName = null;

	if (pVar != null)
	    varName = pVar.getAsString();

	if (pArticleNumber != null)
	    articleNumber = pArticleNumber.getAsString();

	if (!Str.isEmpty(articleNumber)) {
	    Product product = products.havingArticleNumber(articleNumber);

	    if (product != null)
		env.setVariable(Str.isEmpty(varName) ? PRODUCT_KEY : varName, DefaultObjectWrapper.getDefaultInstance().wrap(product));
	} else if (pId != null) {
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

	    Id productId = Id.valueOf(beanModel);

	    Product product = products.findById(Product.class, productId);

	    if (product != null)
		env.setVariable(Str.isEmpty(varName) ? PRODUCT_KEY : varName, DefaultObjectWrapper.getDefaultInstance().wrap(product));
	}
    }
}
