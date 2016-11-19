package com.geecommerce.catalog.product.repository;

import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.model.CatalogMediaAsset;
import com.geecommerce.catalog.product.model.CatalogMediaType;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.api.Repository;
import com.geecommerce.core.type.Id;

public interface CatalogMedia extends Repository {
    public List<CatalogMediaAsset> allImagesFor(Product product, CatalogMediaType... catalogMediaTypes);

    public List<CatalogMediaAsset> enabledImagesFor(Product product, CatalogMediaType... catalogMediaTypes);

    public List<CatalogMediaAsset> allImagesFor(Product product);

    public List<CatalogMediaAsset> enabledImagesFor(Product product);

    public List<CatalogMediaAsset> enabledImagesFor(Id... productIds);

    public List<CatalogMediaAsset> allVideosFor(Product product);

    public List<CatalogMediaAsset> enabledVideosFor(Product product);

    public List<CatalogMediaAsset> allDocumentsFor(Product product);

    public List<CatalogMediaAsset> enabledDocumentsFor(Product product);

    public List<CatalogMediaAsset> assetsThatBelongTo(Product product);

    public List<CatalogMediaAsset> assetsThatBelongTo(Id... productIds);

    public List<CatalogMediaAsset> allAssetsThatBelongTo(Id... productIds);

    public Map<String, CatalogMediaType> mediaTypeMap();

    public List<CatalogMediaType> mediaTypes();
}
