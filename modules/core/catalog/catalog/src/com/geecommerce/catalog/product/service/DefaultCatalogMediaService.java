package com.geecommerce.catalog.product.service;

import java.util.List;
import java.util.Map;

import com.geecommerce.catalog.product.MediaType;
import com.geecommerce.catalog.product.helper.ProductHelper;
import com.geecommerce.catalog.product.model.CatalogMediaAsset;
import com.geecommerce.catalog.product.model.CatalogMediaType;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.catalog.product.repository.CatalogMedia;
import com.geecommerce.core.service.annotation.Service;
import com.geecommerce.core.type.Id;
import com.google.inject.Inject;

@Service
public class DefaultCatalogMediaService implements CatalogMediaService {
    private final CatalogMedia catalogMedia;
    private final ProductHelper productHelper;

    @Inject
    public DefaultCatalogMediaService(CatalogMedia catalogMedia, ProductHelper productHelper) {
        this.catalogMedia = catalogMedia;
        this.productHelper = productHelper;
    }

    // ----------------------------------------------------------------------
    // Product catalog media
    // ----------------------------------------------------------------------

    @Override
    public CatalogMediaAsset createMediaAsset(CatalogMediaAsset catalogMediaAsset) {
        return catalogMedia.add(catalogMediaAsset);
    }

    @Override
    public CatalogMediaAsset getMediaAsset(Id id) {
        return catalogMedia.findById(CatalogMediaAsset.class, id);
    }

    @Override
    public List<CatalogMediaAsset> getProductImages(Product product, boolean enabledOnly) {
        return enabledOnly ? catalogMedia.enabledImagesFor(product) : catalogMedia.allImagesFor(product);
    }

    @Override
    public List<CatalogMediaAsset> getProductVideos(Product product, boolean enabledOnly) {
        return enabledOnly ? catalogMedia.enabledVideosFor(product) : catalogMedia.allVideosFor(product);
    }

    @Override
    public List<CatalogMediaAsset> getProductDocuments(Product product, boolean enabledOnly) {
        return enabledOnly ? catalogMedia.enabledDocumentsFor(product) : catalogMedia.allDocumentsFor(product);
    }

    //
    // @Override
    // public Map<Id, List<ProductImage>> findImagesForProducts(Id...
    // productIds)
    // {
    // ApplicationContext appCtx = app.getApplicationContext();
    //
    // // First attempt to find the most specific product images for this
    // request-context
    // List<ProductImage> foundImages =
    // productImages.thatBelongTo(appCtx.getRequestContext(), productIds);
    //
    // // Filter out product-ids where we already have the images
    // Id[] nextIds = productHelper.filterCompletedProductIds(foundImages,
    // productIds);
    //
    // // For store
    // if (nextIds != null && nextIds.length > 0)
    // {
    // // Then attempt to find images on store-level
    // List<ProductImage> foundStoreImages =
    // productImages.thatBelongTo(appCtx.getStore(), nextIds);
    //
    // foundImages.addAll(foundStoreImages);
    //
    // // Filter out product-ids where we already have the images
    // nextIds = productHelper.filterCompletedProductIds(foundStoreImages,
    // nextIds);
    // }
    //
    // // For merchant
    // if (nextIds != null && nextIds.length > 0)
    // {
    // // Then attempt to find images on merchant-level
    // List<ProductImage> foundMerchantImages =
    // productImages.thatBelongTo(appCtx.getMerchant(), nextIds);
    //
    // foundImages.addAll(foundMerchantImages);
    //
    // // Filter out product-ids where we already have the images
    // nextIds = productHelper.filterCompletedProductIds(foundMerchantImages,
    // nextIds);
    // }
    //
    // // Now try the most global way which is for all, regardles of merchant,
    // store or request-context
    // if (nextIds != null && nextIds.length > 0)
    // {
    // // Then attempt to find images on global-level
    // List<ProductImage> foundGlobalImages =
    // productImages.thatBelongTo(nextIds);
    //
    // foundImages.addAll(foundGlobalImages);
    // }
    //
    // return productHelper.toProductIdListMap(foundImages);
    // }

    @Override
    public void updateMetadata(CatalogMediaAsset catalogMediaAsset) {
        if (catalogMediaAsset != null && catalogMediaAsset.getId() != null
            && catalogMediaAsset.getProductId() != null) {
            // If media asset has been disabled, we also set the other flags to
            // false.
            if (catalogMediaAsset.isProductImage() && !catalogMediaAsset.isEnabled()) {
                catalogMediaAsset.removeMediaType(MediaType.PRODUCT_MAIN_IMAGE);
                catalogMediaAsset.removeMediaType(MediaType.PRODUCT_LIST_IMAGE1);
                catalogMediaAsset.removeMediaType(MediaType.PRODUCT_LIST_IMAGE2);
                catalogMediaAsset.setVariantMasterDefault(false);
                catalogMediaAsset.setBundleDefault(false);
                catalogMediaAsset.setProgrammeDefault(false);
                catalogMediaAsset.setPosition(99);
            }

            Map<String, CatalogMediaType> mediaTypeMap = catalogMedia.mediaTypeMap();

            List<CatalogMediaAsset> productImages = catalogMedia.allImagesFor(catalogMediaAsset.getProduct(),
                mediaTypeMap.get(MediaType.PRODUCT_MAIN_IMAGE), mediaTypeMap.get(MediaType.PRODUCT_LIST_IMAGE1),
                mediaTypeMap.get(MediaType.PRODUCT_LIST_IMAGE2));

            for (CatalogMediaAsset productImage : productImages) {
                // Also update the meta-data of the others.
                if (!productImage.getId().equals(catalogMediaAsset.getId())) {
                    // Only 1 image can be the main-image.
                    if (catalogMediaAsset.isProductMainImage())
                        productImage.removeMediaType(MediaType.PRODUCT_MAIN_IMAGE);

                    // Only 1 image can be the list-image1.
                    if (catalogMediaAsset.isProductListImage1())
                        productImage.removeMediaType(MediaType.PRODUCT_LIST_IMAGE1);

                    // Only 1 image can be the list-image2.
                    if (catalogMediaAsset.isProductListImage1())
                        productImage.removeMediaType(MediaType.PRODUCT_LIST_IMAGE2);

                    // Only 1 image can be the default-variant-image.
                    if (catalogMediaAsset.isVariantMasterDefault())
                        productImage.setVariantMasterDefault(false);

                    // Only 1 image can be the default-bundle-image.
                    if (catalogMediaAsset.isBundleDefault())
                        productImage.setBundleDefault(false);

                    // Only 1 image can be the default-programme-image.
                    if (catalogMediaAsset.isProgrammeDefault())
                        productImage.setProgrammeDefault(false);
                }

                catalogMedia.update(productImage);
            }

            catalogMedia.update(catalogMediaAsset);
        }
    }
}
