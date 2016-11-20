package com.geecommerce.catalog.product.cron;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.repository.Products;
import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.app.standalone.helper.MongoHelper;
import com.geecommerce.core.cron.MisfireInstruction;
import com.geecommerce.core.cron.Taskable;
import com.geecommerce.core.elasticsearch.SearchIndexer;
import com.geecommerce.core.elasticsearch.api.SearchIndexSupport;
import com.geecommerce.core.elasticsearch.helper.ElasticsearchIndexHelper;
import com.geecommerce.core.service.annotation.Task;
import com.geecommerce.core.service.api.GlobalColumn;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

@DisallowConcurrentExecution
@Task(group = "CB/Catalog", name = "Index Products", schedule = "0 0/3 * * * ?", onMisfire = MisfireInstruction.RETRY_ONE, enabled = false)
public class IndexProducts extends SearchIndexer implements Taskable, Job {
    @Inject
    protected App app;

    protected static final Logger log = LogManager.getLogger(IndexProducts.class);
    protected final Products products;

    @Inject
    public IndexProducts(Products products, ElasticsearchIndexHelper elasticsearchHelper) {
        super(elasticsearchHelper);
        this.products = products;
    }

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        try {
            Map<String, Object> indexerContext = getIndexerContext();

            if (indexerContext != null) {
                updateOrCreateIndex(indexerContext, Product.class);
            }
        } catch (Throwable t) {
            t.printStackTrace();

            // According to quartz documentation, exceptions should be caught in
            // a try-catch-block
            // wrapping the whole task. Only exceptions of type
            // JobExecutionException may be thrown.
            // http://quartz-scheduler.org/documentation/quartz-1.x/tutorials/TutorialLesson03#TutorialLesson3-JobExecutionException

            JobExecutionException e = new JobExecutionException(t.getMessage(), t, false);

            throw e;
        }
    }

    protected Map<String, Object> getIndexerContext() {
        ApplicationContext appCtx = app.context();
        Merchant merchant = appCtx.getMerchant();
        Store store = appCtx.getStore();

        Map<String, Object> query = new HashMap<>();
        query.put(GlobalColumn.ENABLED, Boolean.TRUE);
        query.put(GlobalColumn.MERCHANT_ID, merchant.getId());
        query.put(GlobalColumn.STORE_ID, store.getId());

        List<Map<String, Object>> indexerContexts = MongoHelper.find(MongoHelper.mongoSystemDB(), "search_indexer",
            query, null);

        return indexerContexts != null && indexerContexts.size() > 0 ? indexerContexts.get(0) : null;
    }

    @Override
    public List<Id> getIndexedIds() {
        return products.allIdsForContext();
    }

    @Override
    public SearchIndexSupport getIndexedItem(Id id) {
        return products.findById(Product.class, id);
    }
}
