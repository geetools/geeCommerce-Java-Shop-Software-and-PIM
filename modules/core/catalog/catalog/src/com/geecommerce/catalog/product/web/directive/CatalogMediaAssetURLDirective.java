package com.geecommerce.catalog.product.web.directive;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.catalog.product.helper.CatalogMediaHelper;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.Str;
import com.geecommerce.core.web.annotation.Directive;
import com.google.inject.Inject;

import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Directive("catMediaURL")
public class CatalogMediaAssetURLDirective implements TemplateDirectiveModel {
    private static final Logger log = LogManager.getLogger(CatalogMediaAssetURLDirective.class);

    private final CatalogMediaHelper catalogMediaHelper;

    @Inject
    public CatalogMediaAssetURLDirective(CatalogMediaHelper catalogMediaHelper) {
        this.catalogMediaHelper = catalogMediaHelper;
    }

    @SuppressWarnings({ "rawtypes", "deprecation" })
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
        throws TemplateException, IOException {
        if (log.isTraceEnabled()) {
            log.trace(params);
        }

        SimpleNumber width = (SimpleNumber) params.get("width");
        SimpleNumber height = (SimpleNumber) params.get("height");
        SimpleScalar uri = (SimpleScalar) params.get("uri");
        SimpleScalar var = (SimpleScalar) params.get("var");
        BeanModel pSource = (BeanModel) params.get("src");
        TemplateBooleanModel pParent = (TemplateBooleanModel) params.get("parent");

        if (pSource != null && uri != null)
            throw new IllegalArgumentException("The parameters 'src' and 'uri' cannot be set at the same time.");

        if (pSource == null && uri == null)
            throw new IllegalArgumentException("You must provide either a 'src' or 'uri' parameter.");

        Product product = null;
        String uriStr = null;
        boolean fallbackToParent = false;

        if (pParent != null) {
            fallbackToParent = pParent.getAsBoolean();
        }

        if (pSource != null) {

            if (!(pSource instanceof BeanModel))
                throw new IllegalArgumentException("The parameter 'src' must be of type BeanModel (Product)");

            Object beanModel = ((BeanModel) pSource).getWrappedObject();

            if (beanModel instanceof Product) {
                product = (Product) beanModel;

                if (product.getMainImage() != null) {
                    uriStr = product.getMainImage().getPath();
                }

                if (uriStr == null && product.isVariant() && fallbackToParent) {
                    Product parent = product.getParent();

                    if (parent != null && parent.getMainImage() != null) {
                        uriStr = parent.getMainImage().getPath();
                    }
                }
            } else {
                throw new IllegalArgumentException("The source-object must be of type Product");
            }
        }

        if (uri != null || uriStr != null) {
            if (uri != null) {
                uriStr = uri.getAsString();
            }

            String varStr = null;

            if (var != null)
                varStr = var.getAsString();

            if (!Str.isEmpty(uriStr)) {
                String mediaAssetURL = catalogMediaHelper.toMediaAssetURL(uriStr,
                    width == null ? null : width.getAsNumber().intValue(),
                    height == null ? null : height.getAsNumber().intValue());

                if (varStr != null) {
                    env.setVariable(varStr, DefaultObjectWrapper.getDefaultInstance().wrap(mediaAssetURL));
                } else {
                    env.getOut().write(mediaAssetURL);
                }
            }
        }
    }
}
