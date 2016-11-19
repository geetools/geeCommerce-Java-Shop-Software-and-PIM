package com.geecommerce.catalog.product.web.directive;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.geecommerce.catalog.product.helper.CatalogMediaHelper;
import com.geecommerce.core.Str;
import com.geecommerce.core.web.annotation.Directive;

import freemarker.core.Environment;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
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

    @SuppressWarnings({"rawtypes", "deprecation"})
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        if (log.isTraceEnabled()) {
            log.trace(params);
        }

        SimpleNumber width = (SimpleNumber) params.get("width");
        SimpleNumber height = (SimpleNumber) params.get("height");
        SimpleScalar uri = (SimpleScalar) params.get("uri");
        SimpleScalar var = (SimpleScalar) params.get("var");

        if (uri != null) {
            String uriStr = uri.getAsString();
            String varStr = null;

            if (var != null)
                varStr = var.getAsString();

            if (!Str.isEmpty(uriStr)) {
                String mediaAssetURL = catalogMediaHelper.toMediaAssetURL(uriStr, width == null ? null : width.getAsNumber().intValue(), height == null ? null : height.getAsNumber().intValue());

                if (varStr != null) {
                    env.setVariable(varStr, DefaultObjectWrapper.getDefaultInstance().wrap(mediaAssetURL));
                } else {
                    env.getOut().write(mediaAssetURL);
                }
            }
        }
    }
}
