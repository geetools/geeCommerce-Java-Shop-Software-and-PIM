package com.geecommerce.mediaassets.observer;

import com.geecommerce.core.elasticsearch.helper.ElasticsearchIndexHelper;
import com.geecommerce.core.event.Event;
import com.geecommerce.core.event.Observable;
import com.geecommerce.core.event.Observer;
import com.geecommerce.core.event.Run;
import com.geecommerce.core.event.annotation.Observe;
import com.geecommerce.core.type.Id;
import com.geecommerce.mediaassets.model.MediaAsset;
import com.geecommerce.mediaassets.repository.MediaAssets;
import com.google.inject.Inject;

@Observe(name = "mediaassets.model.MediaAsset", event = { Event.AFTER_NEW,
    Event.AFTER_UPDATE }, run = Run.ASYNCHRONOUSLY)
public class MediaAssetObserver implements Observer {
    private final MediaAssets mediaAssets;
    private final ElasticsearchIndexHelper elasticsearchHelper;

    @Inject
    public MediaAssetObserver(MediaAssets mediaAssets, ElasticsearchIndexHelper elasticsearchHelper) {
        this.mediaAssets = mediaAssets;
        this.elasticsearchHelper = elasticsearchHelper;
    }

    @Override
    public void onEvent(Event evt, Observable o) {
        if (o == null)
            return;

        Id mediaAssetId = ((MediaAsset) o).getId();

        System.out.println("[" + Thread.currentThread().getName() + "] Running asynchronous observer for: " + mediaAssetId
            + " -> " + evt.name());

        MediaAsset ma = mediaAssets.findById(MediaAsset.class, mediaAssetId);

        elasticsearchHelper.updateIndexedItem(ma, true);

    }
}
