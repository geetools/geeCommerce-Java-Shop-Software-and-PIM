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

public class AddCrossSell implements ImportAction {

    @Inject
    protected App app;

    protected final ImportHelper importHelper;
    protected final ProductImportHelper productImportHelper;
    protected final Products products;

    @Inject
    public AddCrossSell(ImportHelper importHelper, ProductImportHelper productImportHelper, Products products) {
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
                .setMessage("addCrossSellParentNotFound")
                .addArg(importContext.field("_id")));

            return;
        }

        Map<String, Object> productIds = productImportHelper.productIds(importContext.data(), "_crosssell");

        if (productIds == null || productIds.isEmpty()) {
            importContext.add(app.model(ImportMessage.class)
                .setToken(importContext.token())
                .setFileName(importContext.fileName())
                .setLineNumber(importContext.lineNumber())
                .setMessageLevel(MessageLevel.ERROR)
                .setImportStage(ImportStage.PROCESS)
                .setMessage("addCrossSellProductNotFound")
                .addArg(importContext.field("_crosssell")));

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
                .setMessage("addCrossSellProductNotFound")
                .addArg(importContext.field("_crosssell")));

            return;
        }

        Product crossSellProduct = products.findById(Product.class, (Id) productIds.get("_id"));

        if (crossSellProduct == null) {
            importContext.add(app.model(ImportMessage.class)
                .setToken(importContext.token())
                .setFileName(importContext.fileName())
                .setLineNumber(importContext.lineNumber())
                .setMessageLevel(MessageLevel.ERROR)
                .setImportStage(ImportStage.PROCESS)
                .setMessage("addCrossSellNotFound")
                .addArg(importContext.field("_crosssell")));

            return;
        }

        p.addCrossSellProduct(crossSellProduct);
    }

    @Override
    public int order() {
        return 50;
    }
}
