package com.geecommerce.catalog.product.service;

import java.util.List;

import com.geecommerce.catalog.product.model.CatalogMediaAsset;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.api.Service;
import com.geecommerce.core.type.Id;

public interface CatalogMediaService extends Service {
    public CatalogMediaAsset createMediaAsset(CatalogMediaAsset catalogMediaAsset);

    public CatalogMediaAsset getMediaAsset(Id id);

    public List<CatalogMediaAsset> getProductImages(Product product, boolean enabledOnly);

    public List<CatalogMediaAsset> getProductVideos(Product product, boolean enabledOnly);

    public List<CatalogMediaAsset> getProductDocuments(Product product, boolean enabledOnly);

    public void updateMetadata(CatalogMediaAsset catalogMediaAsset);
}
