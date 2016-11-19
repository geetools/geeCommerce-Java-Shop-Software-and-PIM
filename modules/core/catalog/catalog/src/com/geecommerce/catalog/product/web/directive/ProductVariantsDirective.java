package com.geecommerce.catalog.product.web.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.geecommerce.catalog.product.helper.CatalogMediaHelper;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.system.attribute.model.AttributeOption;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.web.annotation.Directive;

import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Directive("product_variants")
public class ProductVariantsDirective implements TemplateDirectiveModel {
    private final CatalogMediaHelper catalogMediaHelper;

    private static final String AS_CSV = "csv";
    private static final String AS_LIST = "list";

    private static final Logger log = LogManager.getLogger(ProductVariantsDirective.class);

    @Inject
    public ProductVariantsDirective(CatalogMediaHelper catalogMediaHelper) {
	this.catalogMediaHelper = catalogMediaHelper;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
	if (log.isTraceEnabled()) {
	    log.trace(params);
	}

	TemplateModel pSource = (TemplateModel) params.get("src");
	SimpleScalar pAs = (SimpleScalar) params.get("out");

	if (pSource == null)
	    throw new IllegalArgumentException("The parameter 'source' cannot be null");

	if (!(pSource instanceof BeanModel))
	    throw new IllegalArgumentException("The parameter 'source' must be of type BeanModel (Product)");

	Product product = null;
	String as = null;

	Object beanModel = ((BeanModel) pSource).getWrappedObject();

	if (beanModel instanceof Product) {
	    product = (Product) beanModel;
	} else {
	    throw new IllegalArgumentException("The source-object must be of type Product");
	}

	if (pAs != null)
	    as = pAs.getAsString();

	if (as == null)
	    as = "csv";

	if (!product.isVariantMaster())
	    return;

	List<Product> variants = product.getVariants();

	StringBuilder html = new StringBuilder();

	int o = 0;

	if (AS_LIST.equalsIgnoreCase(as)) {
	    html.append("<ul class=\"no_bullet\">\n");

	    for (Product variant : variants) {
		if (!variant.isValidForSelling())
		    continue;

		String imgURI = catalogMediaHelper.buildURL(variant.getMainImageURI(), 40, 40);

		html.append("<li class=\"img_bullet\" style=\"background: url(" + imgURI + ") no-repeat left top;\">");

		List<AttributeValue> attributes = variant.getVariantAttributes();

		int i = 0;

		for (AttributeValue av : attributes) {
		    if (i > 0)
			html.append(", ");

		    AttributeOption ao = av.getFirstAttributeOption();

		    if (ao != null) {
			ContextObject<String> label = ao.getLabel();

			if (label != null) {
			    String l = label.getString();

			    if (StringUtils.isNotBlank(l)) {
				html.append(l);
				i++;
			    }
			}
		    }
		}

		html.append("</li>\n");

		if (i > 0)
		    o++;
	    }

	    html.append("</ul>\n");
	} else if (AS_CSV.equalsIgnoreCase(as)) {
	    for (Product variant : variants) {
		if (!variant.isValidForSelling())
		    continue;

		List<AttributeValue> attributes = variant.getVariantAttributes();

		if (o > 0)
		    html.append(", ");

		int i = 0;

		for (AttributeValue av : attributes) {
		    if (i > 0)
			html.append(" / ");

		    AttributeOption ao = av.getFirstAttributeOption();

		    if (ao != null) {
			ContextObject<String> label = ao.getLabel();

			if (label != null) {
			    String l = label.getString();

			    if (StringUtils.isNotBlank(l)) {
				html.append(l);
				i++;
			    }
			}
		    }
		}

		if (i > 0)
		    o++;
	    }
	}

	env.getOut().write(html.toString());
    }
}
