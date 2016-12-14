package com.geecommerce.catalog.product.batch.dataimport.action;

import java.util.Map;

import com.geecommerce.catalog.product.batch.dataimport.helper.ProductBeanHelper;
import com.geecommerce.catalog.product.batch.dataimport.helper.ProductImportHelper;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.App;
import com.geecommerce.core.batch.dataimport.ImportAction;
import com.geecommerce.core.batch.dataimport.ImportContext;
import com.geecommerce.core.batch.dataimport.enums.ImportStage;
import com.geecommerce.core.batch.dataimport.enums.MessageLevel;
import com.geecommerce.core.batch.dataimport.helper.ImportHelper;
import com.geecommerce.core.batch.dataimport.model.ImportMessage;
import com.google.inject.Inject;

public class CreateProduct implements ImportAction {

    @Inject
    protected App app;

    protected final ImportHelper importHelper;
    protected final ProductImportHelper productImportHelper;
    protected final ProductBeanHelper productBeanHelper;
    protected final Products products;

    @Inject
    public CreateProduct(ImportHelper importHelper, ProductImportHelper productImportHelper, ProductBeanHelper productBeanHelper, Products products) {
        this.importHelper = importHelper;
        this.productImportHelper = productImportHelper;
        this.productBeanHelper = productBeanHelper;
        this.products = products;
    }

    @Override
    public boolean canProcess(ImportContext importContext) {
        return false;
    }

    @Override
    public void process(ImportContext importContext) {
        Product p = (Product) importContext.model();

        if (p == null) {
            importContext.add(app.model(ImportMessage.class)
                .setToken(importContext.token())
                .setFileName(importContext.fileName())
                .setLineNumber(importContext.lineNumber())
                .setMessageLevel(MessageLevel.ERROR)
                .setImportStage(ImportStage.PROCESS)
                .setMessage("productNotCreated")
                .addArg(productImportHelper.productKeys(importContext.data())));

            return;
        }

        if (p.getId() != null) {
            importContext.add(app.model(ImportMessage.class)
                .setToken(importContext.token())
                .setFileName(importContext.fileName())
                .setLineNumber(importContext.lineNumber())
                .setMessageLevel(MessageLevel.ERROR)
                .setImportStage(ImportStage.PROCESS)
                .setMessage("productIdAlreadyExists")
                .addArg(productImportHelper.productKeys(importContext.data())));

            return;
        }

        productBeanHelper.setProductKeys(p, importContext.data());
        productBeanHelper.setTypeAndGroup(p, importContext.data());
        productBeanHelper.setSaleable(p, importContext.data());
        productBeanHelper.setVisibility(p, importContext.data());

        for (Map.Entry<String, String> entry : importContext.data().entrySet()) {
            System.out.println("[CREATE] Processinng key: " + entry.getKey());

        }
    }

    @Override
    public int order() {
        return 20;
    }
}
