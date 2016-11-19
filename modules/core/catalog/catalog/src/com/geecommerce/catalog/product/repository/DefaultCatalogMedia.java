package com.geecommerce.catalog.product.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.model.CatalogMediaAsset;
import com.geecommerce.catalog.product.model.CatalogMediaAsset.Col;
import com.geecommerce.catalog.product.model.CatalogMediaType;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.media.MimeType;
import com.geecommerce.core.service.AbstractRepository;
import com.geecommerce.core.service.QueryOptions;
import com.geecommerce.core.service.annotation.Repository;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.type.Ids;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryOperators;

@Repository
public class DefaultCatalogMedia extends AbstractRepository implements CatalogMedia {
    @Override
    public List<CatalogMediaAsset> allImagesFor(Product product, CatalogMediaType... catalogMediaTypes) {
        List<String> mimeTypes = new ArrayList<>();
        mimeTypes.add(MimeType.IMAGE_JPEG);
        mimeTypes.add(MimeType.IMAGE_GIF);
        mimeTypes.add(MimeType.IMAGE_PNG);
        mimeTypes.add(MimeType.IMAGE_BMP);
        mimeTypes.add(MimeType.IMAGE_TIFF);

        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(Col.PRODUCT_ID, product.getId());

        DBObject inClause = new BasicDBObject();
        inClause.put(QueryOperators.IN, mimeTypes);
        filter.put(Col.MIME_TYPE, inClause);

        if (catalogMediaTypes != null && catalogMediaTypes.length > 0) {
            List<Id> catMediaTypeIds = Ids.toIdList(catalogMediaTypes);

            DBObject inClause2 = new BasicDBObject();
            inClause2.put(QueryOperators.IN, catMediaTypeIds);
            filter.put(Col.MEDIA_TYPE_IDS, inClause2);
        }

        return simpleContextFind(CatalogMediaAsset.class, filter);
    }

    @Override
    public List<CatalogMediaAsset> enabledImagesFor(Product product, CatalogMediaType... catalogMediaTypes) {
        List<String> mimeTypes = new ArrayList<>();
        mimeTypes.add(MimeType.IMAGE_JPEG);
        mimeTypes.add(MimeType.IMAGE_GIF);
        mimeTypes.add(MimeType.IMAGE_PNG);
        mimeTypes.add(MimeType.IMAGE_BMP);
        mimeTypes.add(MimeType.IMAGE_TIFF);

        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(Col.ENABLED, true);
        filter.put(Col.PRODUCT_ID, product.getId());

        DBObject inClause = new BasicDBObject();
        inClause.put(QueryOperators.IN, mimeTypes);
        filter.put(Col.MIME_TYPE, inClause);

        if (catalogMediaTypes != null && catalogMediaTypes.length > 0) {
            List<Id> catMediaTypeIds = Ids.toIdList(catalogMediaTypes);

            DBObject inClause2 = new BasicDBObject();
            inClause2.put("$in", catMediaTypeIds);
            filter.put(Col.MEDIA_TYPE_IDS, inClause2);
        }

        return simpleContextFind(CatalogMediaAsset.class, filter);
    }

    @Override
    public List<CatalogMediaAsset> allImagesFor(Product product) {
        List<String> mimeTypes = new ArrayList<>();
        mimeTypes.add(MimeType.IMAGE_JPEG);
        mimeTypes.add(MimeType.IMAGE_GIF);
        mimeTypes.add(MimeType.IMAGE_PNG);
        mimeTypes.add(MimeType.IMAGE_BMP);
        mimeTypes.add(MimeType.IMAGE_TIFF);

        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(Col.PRODUCT_ID, product.getId());

        DBObject inClause = new BasicDBObject();
        inClause.put(QueryOperators.IN, mimeTypes);
        filter.put(Col.MIME_TYPE, inClause);

        return simpleContextFind(CatalogMediaAsset.class, filter);
    }

    @Override
    public List<CatalogMediaAsset> enabledImagesFor(Product product) {
        List<String> mimeTypes = new ArrayList<>();
        mimeTypes.add(MimeType.IMAGE_JPEG);
        mimeTypes.add(MimeType.IMAGE_GIF);
        mimeTypes.add(MimeType.IMAGE_PNG);
        mimeTypes.add(MimeType.IMAGE_BMP);
        mimeTypes.add(MimeType.IMAGE_TIFF);

        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(Col.ENABLED, true);
        filter.put(Col.PRODUCT_ID, product.getId());

        Map<String, Object> inClause = new LinkedHashMap<>();
        inClause.put(QueryOperators.IN, mimeTypes);
        filter.put(Col.MIME_TYPE, inClause);

        return simpleContextFind(CatalogMediaAsset.class, filter);
    }

    @Override
    public List<CatalogMediaAsset> enabledImagesFor(Id... productIds) {
        List<String> mimeTypes = new ArrayList<>();
        mimeTypes.add(MimeType.IMAGE_JPEG);
        mimeTypes.add(MimeType.IMAGE_GIF);
        mimeTypes.add(MimeType.IMAGE_PNG);
        mimeTypes.add(MimeType.IMAGE_BMP);
        mimeTypes.add(MimeType.IMAGE_TIFF);

        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(Col.ENABLED, true);

        Map<String, Object> inClause = new LinkedHashMap<>();
        inClause.put(QueryOperators.IN, productIds);
        filter.put(Col.PRODUCT_ID, inClause);

        Map<String, Object> inClause2 = new LinkedHashMap<>();
        inClause2.put(QueryOperators.IN, mimeTypes);
        filter.put(Col.MIME_TYPE, inClause2);

        return simpleContextFind(CatalogMediaAsset.class, filter, QueryOptions.builder().singleQueryField(Col.PRODUCT_ID).build());
    }

    @Override
    public List<CatalogMediaAsset> allVideosFor(Product product) {
        List<String> mimeTypes = new ArrayList<>();
        mimeTypes.add(MimeType.VIDEO_WEBM);
        mimeTypes.add(MimeType.VIDEO_MP4);
        mimeTypes.add(MimeType.VIDEO_OGG);

        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(Col.PRODUCT_ID, product.getId());

        DBObject inClause = new BasicDBObject();
        inClause.put(QueryOperators.IN, mimeTypes);
        filter.put(Col.MIME_TYPE, inClause);

        return simpleContextFind(CatalogMediaAsset.class, filter);
    }

    @Override
    public List<CatalogMediaAsset> enabledVideosFor(Product product) {
        List<String> mimeTypes = new ArrayList<>();
        mimeTypes.add(MimeType.VIDEO_WEBM);
        mimeTypes.add(MimeType.VIDEO_MP4);
        mimeTypes.add(MimeType.VIDEO_OGG);

        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(Col.ENABLED, true);
        filter.put(Col.PRODUCT_ID, product.getId());

        DBObject inClause = new BasicDBObject();
        inClause.put(QueryOperators.IN, mimeTypes);
        filter.put(Col.MIME_TYPE, inClause);

        return simpleContextFind(CatalogMediaAsset.class, filter);
    }

    @Override
    public List<CatalogMediaAsset> allDocumentsFor(Product product) {
        List<String> mimeTypes = new ArrayList<>();
        mimeTypes.add(MimeType.APPLICATION_PDF);

        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(Col.PRODUCT_ID, product.getId());

        DBObject inClause = new BasicDBObject();
        inClause.put(QueryOperators.IN, mimeTypes);
        filter.put(Col.MIME_TYPE, inClause);

        return simpleContextFind(CatalogMediaAsset.class, filter);
    }

    @Override
    public List<CatalogMediaAsset> enabledDocumentsFor(Product product) {
        List<String> mimeTypes = new ArrayList<>();
        mimeTypes.add(MimeType.APPLICATION_PDF);

        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(Col.ENABLED, true);
        filter.put(Col.PRODUCT_ID, product.getId());

        DBObject inClause = new BasicDBObject();
        inClause.put(QueryOperators.IN, mimeTypes);
        filter.put(Col.MIME_TYPE, inClause);

        return simpleContextFind(CatalogMediaAsset.class, filter);
    }

    @Override
    public List<CatalogMediaAsset> assetsThatBelongTo(Product product) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(Col.PRODUCT_ID, product.getId());

        return find(CatalogMediaAsset.class, filter);
    }

    @Override
    public List<CatalogMediaAsset> assetsThatBelongTo(Id... productIds) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put(Col.ENABLED, true);

        DBObject inClause = new BasicDBObject();
        inClause.put(QueryOperators.IN, productIds);
        filter.put(Col.PRODUCT_ID, inClause);

        return simpleContextFind(CatalogMediaAsset.class, filter);
    }

    @Override
    public List<CatalogMediaAsset> allAssetsThatBelongTo(Id... productIds) {
        Map<String, Object> filter = new LinkedHashMap<>();

        DBObject inClause = new BasicDBObject();
        inClause.put(QueryOperators.IN, productIds);
        filter.put(Col.PRODUCT_ID, inClause);

        return simpleContextFind(CatalogMediaAsset.class, filter);
    }

    @Override
    public Map<String, CatalogMediaType> mediaTypeMap() {
        Map<String, CatalogMediaType> mediaTypeMap = new LinkedHashMap<>();

        List<CatalogMediaType> mediaTypes = mediaTypes();

        for (CatalogMediaType catalogMediaType : mediaTypes) {
            mediaTypeMap.put(catalogMediaType.getKey(), catalogMediaType);
        }

        return mediaTypeMap;
    }

    @Override
    public List<CatalogMediaType> mediaTypes() {
        return simpleContextFind(CatalogMediaType.class, new HashMap<>());
    }
}
