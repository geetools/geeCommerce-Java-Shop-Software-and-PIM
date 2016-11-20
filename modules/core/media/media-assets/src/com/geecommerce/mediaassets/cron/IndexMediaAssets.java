package com.geecommerce.mediaassets.cron;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

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
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.repository.MediaAssets;
import com.google.inject.Inject;

@DisallowConcurrentExecution
@Task(group = "CB/Media-assets", name = "Index Media Assets", schedule = "0 0/3 * * * ?", onMisfire = MisfireInstruction.RETRY_ONE, enabled = false)
public class IndexMediaAssets extends SearchIndexer implements Taskable, Job {
    @Inject
    protected App app;

    protected static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMddHHmm");
    protected static final Logger log = LogManager.getLogger(IndexMediaAssets.class);
    protected final MediaAssets mediaAssets;

    @Inject
    public IndexMediaAssets(MediaAssets mediaAssets, ElasticsearchIndexHelper elasticsearchHelper) {
        super(elasticsearchHelper);
        this.mediaAssets = mediaAssets;
    }

    @Override
    public void execute(JobExecutionContext ctx) throws JobExecutionException {
        System.out.println("Media Asset index started");
        try {
            Map<String, Object> indexerContext = getIndexerContext();

            if (indexerContext != null) {
                updateOrCreateIndex(indexerContext, MediaAsset.class);
            }
            System.out.println("Media Asset index done");
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
        return mediaAssets.simpleContextFindIdsOnly(MediaAsset.class, new LinkedHashMap<>());
    }

    @Override
    public SearchIndexSupport getIndexedItem(Id id) {
        return mediaAssets.findById(MediaAsset.class, id);
    }
}
