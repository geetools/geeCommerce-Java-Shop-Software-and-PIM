package com.geecommerce.catalog.product.helper;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import com.geecommerce.catalog.product.model.CatalogMediaAsset;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.service.api.Helper;
import com.geecommerce.core.type.Id;

public interface CatalogMediaHelper extends Helper {
    public void markAsDeleted(String path);

    public File saveToDisk(InputStream uploadedInputStream, String absSystemPath, Product product);

    public InputStream getStream(String absSystemPath);

    public byte[] getImage(String absSystemPath);

    public String getNewAbsoluteFilePath(String originalFilename, String customFilename, String mimeType, Product product, Id forStore);

    public String getRelativeAssetPath(String originalFilename, String customFilename, String mimeType, Product product, Id forStore);

    public String toRelativeAssetPath(String absoluteFilePath);

    public String toRelativeAssetPath(String absoluteFilePath, Id forStore);

    public String toWebURI(String relativePath);

    public String getBaseSystemPath();

    public String getBaseSystemPath(String mimeType);

    public String getBaseSystemPath(Id storeId);

    public String getBaseSystemPath(String mimeType, Id storeId);

    public String getSubdomain();

    public String getSubdomain(String mimeType);

    public String getSubdomain(Id storeId);

    public String getSubdomain(String mimeType, Id storeId);

    public String getServletPath();

    public String getServletPath(String mimeType);

    public String getServletPath(Id storeId);

    public String getServletPath(String mimeType, Id storeId);

    public String getSubpath();

    public String getSubpath(String mimeType);

    public String getSubpath(Id storeId);

    public String getSubpath(String mimeType, Id storeId);

    public String getSize(String sizeName);

    public String getSize(String sizeName, String mimeType);

    public String getSize(String sizeName, Id storeId);

    public String getSize(String sizeName, String mimeType, Id storeId);

    public boolean isCacheEnabled();

    public boolean isCacheEnabled(String mimeType);

    public boolean isCacheEnabled(Id storeId);

    public boolean isCacheEnabled(String mimeType, Id storeId);

    public String getBaseCacheSystemPath();

    public String getBaseCacheSystemPath(String mimeType);

    public String getBaseCacheSystemPath(Id storeId);

    public String getBaseCacheSystemPath(String mimeType, Id storeId);

    public String toAbsoluteSystemPath(String relativePath);

    public String toAbsoluteSystemPath(String relativePath, Id storeId);

    public String toAbsoluteSystemCachePath(String relativePath);

    public String toAbsoluteSystemCachePath(String relativePath, Id storeId);

    public List<File> getCacheFiles(CatalogMediaAsset mediaAsset);

    public List<File> getCacheFiles(String relativePath, Id storeId);

    public void cacheMediaAsset(InputStream is, String mediaAssetURI, boolean replace);

    public String buildURL(String uri);

    public String buildURL(String uri, Integer width, Integer height);

    public String toMediaAssetURL(String imageURI);

    public String toMediaAssetURL(String mediaAssetURI, Integer width, Integer height);

    public void removeFromCaches(CatalogMediaAsset mediaAsset);
}
