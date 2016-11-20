package com.geecommerce.navigation.cron;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.model.ProductConnectionIndex;
import com.geecommerce.catalog.product.repository.ProductConnectionIndexes;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.App;
import com.geecommerce.core.cron.Environment;
import com.geecommerce.core.cron.MisfireInstruction;
import com.geecommerce.core.cron.Taskable;
import com.geecommerce.core.service.annotation.Task;
import com.geecommerce.core.service.persistence.mongodb.Mongo;
import com.geecommerce.core.service.persistence.mongodb.MongoQueries;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

@DisallowConcurrentExecution
@Task(group = "CB/Catalog", name = "Refresh Product Connections Index", schedule = "0 0/3 * * * ?", onMisfire = MisfireInstruction.DO_NOTHING, enabled = false)
public class RefreshProductConnectionsIndex implements Taskable, Job {
    @Inject
    protected App app;

    protected final Products products;
    protected final ProductConnectionIndexes productConnectionIndexes;

    protected static final Logger log = LogManager.getLogger(RefreshProductConnectionsIndex.class);

    @Inject
    public RefreshProductConnectionsIndex(Products products, ProductConnectionIndexes productConnectionIndexes) {
        this.products = products;
        this.productConnectionIndexes = productConnectionIndexes;
    }

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        try {
            refreshProductConnectionsIndex();
        } catch (Throwable t) {
            // According to quartz documentation, exceptions should be caught in
            // a try-catch-block
            // wrapping the whole task. Only exceptions of type
            // JobExecutionException may be thrown.
            // http://quartz-scheduler.org/documentation/quartz-1.x/tutorials/TutorialLesson03#TutorialLesson3-JobExecutionException

            JobExecutionException e = new JobExecutionException(t.getMessage(), t, false);

            throw e;
        }
    }

    protected void refreshProductConnectionsIndex() {
        Environment.disableMessageBus();
        Environment.disableObservers();

        List<Id> productIds = products.enabledIdsForContext();

        if (productIds != null && productIds.size() > 0) {
            // Reset all the updated flags back to 0.
            // That way we know which documents to delete after the bulk
            // upserts.
            ProductConnectionIndex pciUpdFlag = app.model(ProductConnectionIndex.class).setUpdateFlag(0);
            productConnectionIndexes.update(pciUpdFlag,
                MongoQueries.newFilter(ProductConnectionIndex.Col.UPDATE_FLAG, 1), false, true,
                ProductConnectionIndex.Col.UPDATE_FLAG);

            Mongo.enableBulkMode();

            for (Id productId : productIds) {
                Product parentProduct = products.findById(Product.class, productId);

                if (parentProduct != null) {
                    Set<Id> allChildren = parentProduct.getAllChildProductIds(false);
                    Set<Id> allConnections = parentProduct.getAllConnectedProductIds(false);

                    if ((allChildren != null && !allChildren.isEmpty())
                        || (allConnections != null && !allConnections.isEmpty())) {
                        ProductConnectionIndex pci = app.model(ProductConnectionIndex.class)
                            .setProductId(parentProduct.getId()).setConnections(allConnections)
                            .setChildConnections(allChildren).setUpdateFlag(1);

                        if (allChildren != null && !allChildren.isEmpty()) {
                            List<Product> childProducts = products.findByIds(Product.class,
                                allChildren.toArray(new Id[allChildren.size()]));

                            for (Product p : childProducts) {
                                if (p.isValidForSelling())
                                    pci.appendSellableChildConnection(p);
                            }
                        }

                        Map<String, Object> query = new HashMap<>();
                        query.put(ProductConnectionIndex.Col.PRODUCT_ID, productId);

                        productConnectionIndexes.update(pci, query, true);
                    }
                }
            }

            Mongo.finalizeBulk();
            Mongo.disableBulkMode();

            // Remove all documents where the update flag was not set to 1,
            // which indicates that they are no longer needed.
            productConnectionIndexes.remove(ProductConnectionIndex.class,
                MongoQueries.newFilter(ProductConnectionIndex.Col.UPDATE_FLAG, 0));
        }

        Environment.enableMessageBus();
        Environment.enableObservers();
    }
}
