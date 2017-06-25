package com.geecommerce.navigation.cron;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.geecommerce.core.system.query.model.QueryNode;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.geecommerce.catalog.product.model.ProductList;
import com.geecommerce.catalog.product.model.ProductNavigationIndex;
import com.geecommerce.catalog.product.repository.ProductLists;
import com.geecommerce.catalog.product.repository.ProductNavigationIndexes;
import com.geecommerce.catalog.product.service.ProductListService;
import com.geecommerce.core.App;
import com.geecommerce.core.cron.Environment;
import com.geecommerce.core.cron.MisfireInstruction;
import com.geecommerce.core.cron.Taskable;
import com.geecommerce.core.elasticsearch.search.SearchParams;
import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.service.annotation.Task;
import com.geecommerce.core.service.persistence.mongodb.Mongo;
import com.geecommerce.core.service.persistence.mongodb.MongoQueries;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Json;
import com.geecommerce.navigation.model.NavigationItem;
import com.geecommerce.navigation.service.NavigationService;
import com.google.inject.Inject;

@DisallowConcurrentExecution
@Task(group = "CB/Catalog", name = "Refresh Product Navigation Index", schedule = "0 0/3 * * * ?", onMisfire = MisfireInstruction.DO_NOTHING, enabled = false)
public class RefreshProductNavigationIndex implements Taskable, Job {
    @Inject
    protected App app;

    protected final ProductNavigationIndexes productNavigationIndexes;
    protected final NavigationService navigationService;
    protected final ProductListService productListService;
    protected final ProductLists productLists;

    @Inject
    public RefreshProductNavigationIndex(ProductNavigationIndexes productNavigationIndexes,
        NavigationService navigationService, ProductListService productListService, ProductLists productLists) {
        this.productNavigationIndexes = productNavigationIndexes;
        this.navigationService = navigationService;
        this.productListService = productListService;
        this.productLists = productLists;
    }

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        try {
            refreshProductNavigationIndex();
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

    @SuppressWarnings("unchecked")
    protected void refreshProductNavigationIndex() {
        long start = System.currentTimeMillis();

        Environment.disableMessageBus();
        Environment.disableObservers();

        NavigationItem rootNavItem = navigationService.findRootNavigationItem();

        List<ProductList> _productLists = productLists.findAll(ProductList.class);

        if (_productLists != null && _productLists.size() > 0) {
            // Reset all the updated flags back to 0.
            // That way we know which documents to delete after the bulk
            // upserts.
            ProductNavigationIndex pniUpdFlag = app.model(ProductNavigationIndex.class).setUpdateFlag(0);
            productNavigationIndexes.update(pniUpdFlag,
                MongoQueries.newFilter(ProductNavigationIndex.Col.UPDATE_FLAG, 1), false, true,
                ProductNavigationIndex.Col.UPDATE_FLAG);

            Mongo.enableBulkMode();

            for (ProductList productList : _productLists) {
                if (!productList.isEnabled())
                    continue;

                System.out.println(productList.getKey());

                QueryNode queryNode = productList.getQueryNode();

                Map<String, Object> queryMap = null;
                if (productList.getQuery() != null && !productList.getQuery().isEmpty()) {
                    queryMap = Json.fromJson(productList.getQuery(), HashMap.class);
                }

                Map<Id, Boolean> productIdMap = productListService.getProductIdsAndVisibility(queryNode, queryMap,
                    new HashMap<String, Set<Object>>(), new SearchParams());

                if (productIdMap != null && productIdMap.size() > 0) {
                    List<NavigationItem> currentNavItems = navigationService.getNavigationItemsByTargetObject(
                        ObjectType.PRODUCT_LIST, productList.getId(),
                        rootNavItem == null ? null : rootNavItem.getId());

                    if (currentNavItems != null && !currentNavItems.isEmpty()) {
                        Set<Id> productIds = productIdMap.keySet();

                        for (NavigationItem navigationItem : currentNavItems) {
                            NavigationItem parentNavigationItem = null;

                            if (navigationItem.hasParent())
                                parentNavigationItem = navigationItem.getParent();

                            for (Id productId : productIds) {
                                ProductNavigationIndex pni = app.model(ProductNavigationIndex.class)
                                    .setProductId(productId).setProductListId(productList.getId())
                                    .setNavigationItemId(navigationItem.getId())
                                    .setNavigationItemRootId(navigationItem.getRootId())
                                    .setNavigationLevel(navigationItem.getLevel())
                                    .setNavigationPosition(navigationItem.getPosition())
                                    .setParentNavigationLevel(
                                        parentNavigationItem == null ? 0 : parentNavigationItem.getLevel())
                                    .setParentNavigationPosition(
                                        parentNavigationItem == null ? 0 : parentNavigationItem.getPosition())
                                    .setVisible(productIdMap.get(productId)).setUpdateFlag(1);

                                Map<String, Object> query = new HashMap<>();
                                query.put(ProductNavigationIndex.Col.PRODUCT_ID, productId);
                                query.put(ProductNavigationIndex.Col.PRODUCT_LIST_ID, productList.getId());
                                query.put(ProductNavigationIndex.Col.NAVIGATION_ITEM_ID, navigationItem.getId());

                                productNavigationIndexes.update(pni, query, true);
                            }
                        }
                    }
                }
            }

            Mongo.finalizeBulk();
            Mongo.disableBulkMode();

            // Remove all documents where the update flag was not set to 1,
            // which indicates that they are no longer needed.
            productNavigationIndexes.remove(ProductNavigationIndex.class,
                MongoQueries.newFilter(ProductNavigationIndex.Col.UPDATE_FLAG, 0));
        }

        Environment.enableMessageBus();
        Environment.enableObservers();

        System.out.println("RefreshProductNavigationIndex - TIME ::: " + (System.currentTimeMillis() - start));
    }
}
