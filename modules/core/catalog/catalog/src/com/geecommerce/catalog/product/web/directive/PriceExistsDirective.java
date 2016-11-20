package com.geecommerce.catalog.product.web.directive;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.web.annotation.Directive;
import com.geecommerce.price.pojo.PriceResult;

import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Directive("price_exists")
public class PriceExistsDirective implements TemplateDirectiveModel {
    private static final Logger log = LogManager.getLogger(PriceExistsDirective.class);

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
        throws TemplateException, IOException {
        if (log.isTraceEnabled()) {
            log.trace(params);
        }

        TemplateModel pProduct = (TemplateModel) params.get("product");
        SimpleScalar pType = (SimpleScalar) params.get("type");

        if (pProduct != null && pProduct instanceof BeanModel && pType != null) {
            Product product = null;
            String type = null;

            Object beanModel = ((BeanModel) pProduct).getWrappedObject();

            if (beanModel instanceof Product) {
                product = (Product) beanModel;
            } else {
                throw new IllegalArgumentException("The source-object must be of type Product");
            }

            if (pType != null)
                type = pType.getAsString();

            PriceResult result = product.getPrice();

            if (result != null && type != null) {
                Double price = result.getPrice(type);

                if (price != null && price > 0 && body != null) {
                    body.render(env.getOut());
                }
            }
        }
    }
}
