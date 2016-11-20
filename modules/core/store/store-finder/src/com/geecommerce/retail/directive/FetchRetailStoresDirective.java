package com.geecommerce.retail.directive;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.geecommerce.core.web.annotation.Directive;
import com.geecommerce.retail.model.RetailStore;
import com.geecommerce.retail.repository.RetailStores;
import com.google.inject.Inject;

import freemarker.core.Environment;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

@Directive("fetchRetailStores")
public class FetchRetailStoresDirective implements TemplateDirectiveModel {
    private final RetailStores retailStores;

    @Inject
    public FetchRetailStoresDirective(RetailStores retailStores) {
        this.retailStores = retailStores;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
        throws TemplateException, IOException {
        SimpleScalar pVar = (SimpleScalar) params.get("var");

        List<RetailStore> retailStoreList = retailStores.enabledRetailStores();

        if (pVar != null) {
            // Sets the result into the current template as if using <#assign
            // name=model>.
            env.setVariable(pVar.getAsString(), DefaultObjectWrapper.getDefaultInstance().wrap(retailStoreList));
        } else {
            // Sets the result into the current template as if using <#assign
            // name=model>.
            env.setVariable("retailStores", DefaultObjectWrapper.getDefaultInstance().wrap(retailStoreList));
        }
    }
}
