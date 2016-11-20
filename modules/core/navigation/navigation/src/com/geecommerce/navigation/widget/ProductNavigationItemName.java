package com.geecommerce.navigation.widget;

import java.io.IOException;
import java.util.Map;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.web.annotation.Directive;
import com.geecommerce.navigation.helper.NavigationHelper;
import com.geecommerce.navigation.model.NavigationItem;
import com.google.inject.Inject;

import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Directive(name = "product_navitem_name")
public class ProductNavigationItemName implements TemplateDirectiveModel {
    private final NavigationHelper navigationHelper;

    @Inject
    public ProductNavigationItemName(NavigationHelper navigationHelper) {
        this.navigationHelper = navigationHelper;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
        throws TemplateException, IOException {
        try {
            TemplateModel pSource = (TemplateModel) params.get("src");

            String label = null;

            if (pSource != null) {
                Product product = null;

                Object beanModel = ((BeanModel) pSource).getWrappedObject();

                if (beanModel instanceof Product) {
                    product = (Product) beanModel;
                } else {
                    throw new IllegalArgumentException("The source-object must be of type Product");
                }

                NavigationItem navItem = navigationHelper.findClosestNavigationItem(product);

                if (navItem != null)
                    label = navItem.getDisplayLabel();
            }

            env.getOut().write(label == null ? "" : label);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
