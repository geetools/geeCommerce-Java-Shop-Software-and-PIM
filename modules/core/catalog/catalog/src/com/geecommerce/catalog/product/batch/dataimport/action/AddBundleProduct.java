package com.geecommerce.catalog.product.batch.dataimport.action;

import java.util.Map;

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
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AddBundleProduct implements ImportAction {

    @Inject
    protected App app;

    protected final ImportHelper importHelper;
    protected final ProductImportHelper productImportHelper;
    protected final Products products;

    @Inject
    public AddBundleProduct(ImportHelper importHelper, ProductImportHelper productImportHelper, Products products) {
        this.importHelper = importHelper;
        this.productImportHelper = productImportHelper;
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
                .setMessage("addBundleParentNotFound")
                .addArg(importContext.field("_id")));

            return;
        }

        Map<String, Object> productIds = productImportHelper.productIds(importContext.data(), "_bundle_product");

        if (productIds == null || productIds.isEmpty()) {
            importContext.add(app.model(ImportMessage.class)
                .setToken(importContext.token())
                .setFileName(importContext.fileName())
                .setLineNumber(importContext.lineNumber())
                .setMessageLevel(MessageLevel.ERROR)
                .setImportStage(ImportStage.PROCESS)
                .setMessage("addBundleProductNotFound")
                .addArg(importContext.field("_bundle_product")));

            return;
        }

        productIds = products.productIds(importContext.property("tmpCollectionName"), productIds);

        if (productIds == null || productIds.get("_id") == null) {
            importContext.add(app.model(ImportMessage.class)
                .setToken(importContext.token())
                .setFileName(importContext.fileName())
                .setLineNumber(importContext.lineNumber())
                .setMessageLevel(MessageLevel.ERROR)
                .setImportStage(ImportStage.PROCESS)
                .setMessage("addBundleProductNotFound")
                .addArg(importContext.field("_bundle_product")));

            return;
        }

        Product bundleProduct = products.findById(Product.class, (Id) productIds.get("_id"));

        if (bundleProduct == null) {
            importContext.add(app.model(ImportMessage.class)
                .setToken(importContext.token())
                .setFileName(importContext.fileName())
                .setLineNumber(importContext.lineNumber())
                .setMessageLevel(MessageLevel.ERROR)
                .setImportStage(ImportStage.PROCESS)
                .setMessage("addBundleProductNotFound")
                .addArg(importContext.field("_bundle_product")));

            return;
        }

        //TODO: fix - function was removed
        // p.addBundleProduct(bundleProduct);

    }

    @Override
    public int order() {
        return 50;
    }
}
