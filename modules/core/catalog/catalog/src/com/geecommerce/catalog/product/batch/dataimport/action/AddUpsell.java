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

public class AddUpsell implements ImportAction {

    @Inject
    protected App app;

    protected final ImportHelper importHelper;
    protected final ProductImportHelper productImportHelper;
    protected final Products products;

    @Inject
    public AddUpsell(ImportHelper importHelper, ProductImportHelper productImportHelper, Products products) {
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
                .setMessage("addUpsellParentNotFound")
                .addArg(importContext.field("_id")));

            return;
        }

        Map<String, Object> productIds = productImportHelper.productIds(importContext.data(), "_upsell");

        if (productIds == null || productIds.isEmpty()) {
            importContext.add(app.model(ImportMessage.class)
                .setToken(importContext.token())
                .setFileName(importContext.fileName())
                .setLineNumber(importContext.lineNumber())
                .setMessageLevel(MessageLevel.ERROR)
                .setImportStage(ImportStage.PROCESS)
                .setMessage("addUpsellProductNotFound")
                .addArg(importContext.field("_upsell")));

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
                .setMessage("addUpsellProductNotFound")
                .addArg(importContext.field("_upsell")));

            return;
        }

        Product upsellProduct = products.findById(Product.class, (Id) productIds.get("_id"));

        if (upsellProduct == null) {
            importContext.add(app.model(ImportMessage.class)
                .setToken(importContext.token())
                .setFileName(importContext.fileName())
                .setLineNumber(importContext.lineNumber())
                .setMessageLevel(MessageLevel.ERROR)
                .setImportStage(ImportStage.PROCESS)
                .setMessage("addUpsellNotFound")
                .addArg(importContext.field("_upsell")));

            return;
        }

        p.addUpsellProduct(upsellProduct);
    }

    @Override
    public int order() {
        return 50;
    }
}
