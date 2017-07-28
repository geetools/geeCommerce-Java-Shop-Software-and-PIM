package com.geecommerce.catalog.product.helper;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.core.Info;
import org.im4java.core.Stream2BufferedImage;

import com.geecommerce.catalog.product.model.CatalogMediaAsset;
import com.geecommerce.catalog.product.model.Product;
import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.cache.Cache;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.enums.ImageFilenameOrigin;
import com.geecommerce.core.media.MimeType;
import com.geecommerce.core.service.annotation.Helper;
import com.geecommerce.core.system.ConfigurationKey;
import com.geecommerce.core.system.attribute.model.AttributeValue;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Strings;
import com.geecommerce.core.utils.Filenames;
import com.google.inject.Inject;

@Helper
public class DefaultCatalogMediaHelper implements CatalogMediaHelper {
    @Inject
    protected App app;

    protected static final Pattern patternIncrement = Pattern.compile(".*__([\\d]+)\\.[a-zA-Z]+$");
    protected static final Pattern patternImageParameters = Pattern.compile("___(.+)\\..+$");

    protected static final String CONF_DEFAULT = "default";
    protected static final String CONF_PREFIX = "catalog/media/";
    protected static final String CONF_SUFFIX_BASEPATH = "/base_path";
    protected static final String CONF_SUFFIX_SUBDOMAIN = "/subdomain";
    protected static final String CONF_SUFFIX_SUBPATH = "/subpath";
    protected static final String CONF_SUFFIX_SERVLETPATH = "/servlet_path";
    protected static final String CONF_SUFFIX_SIZE = "_size";
    protected static final String CONF_SUFFIX_CACHE_ENABLED = "/cache/enabled";
    protected static final String CONF_SUFFIX_CACHE_BASE_PATH = "/cache/base_path";

    protected static final String IMAGE_PARAM_WIDTH = "width";
    protected static final String IMAGE_PARAM_HEIGHT = "height";
    protected static final String DELETED_SUFFIX = ".deleted";

    protected static final String CACHE_NAME = "gc/catalog/media_assets/paths";

    protected static final Map<String, String> imageArgKeys = new HashMap<>();
    static {
        imageArgKeys.put("s", "size");
        imageArgKeys.put("t", "transparent");
        imageArgKeys.put("i", "in");
        imageArgKeys.put("o", "out");
    }

    public static void main(String[] args) {
//        ".+___(.+?)\\..+$"
        String imageUri = "/c/media/products/images/_/_/12/34/7-/01/-12347-01___s:330x330.jpg";
        
        extractImageParameters(imageUri);
    }
    
    /**
     * Renames an image to *.jpg.{timestamp}.deleted.
     */
    @Override
    public void markAsDeleted(String path) {
        File f = new File(path);

        if (f.exists() && f.isFile()) {
            StringBuilder newName = new StringBuilder(f.getName()).append(Char.DOT).append(System.currentTimeMillis())
                .append(DELETED_SUFFIX);

            File newFile = new File(f.getParent(), newName.toString());
            f.renameTo(newFile);
        }
    }

    @Override
    public File saveToDisk(InputStream uploadedInputStream, String absSystemPath, Product product) {
        // --------------------------------------------------
        // Get filename and create directories.
        // --------------------------------------------------

        File f = null;

        // if (isAcceptedMimeType(MimeType.fromFilename(absSystemPath)))
        {
            f = new File(absSystemPath);
            File d = f.getParentFile();

            if (!d.exists()) {
                d.mkdirs();
            }

            int maxLoops = 200;
            int x = 0;

            // If file already exists, add a version number and try again.

            while (f.exists()) {
                String name = f.getName();

                Matcher m = patternIncrement.matcher(name);

                String groupItem = null;
                int incr = 1;

                if (m.matches()) {
                    groupItem = m.group(1);
                }

                if (groupItem != null) {
                    incr = Integer.parseInt(groupItem);
                }

                incr++;

                String newName = null;

                // See if the filename already has a version number (needed if
                // file already exists)
                if (name.matches(".+__[0-9]+\\.[a-zA-Z]+")) {
                    // Replace version number
                    newName = name.replaceFirst("(.+)+(?:__[0-9]+)+?(\\.[a-zA-Z]+)+$", "$1__" + (incr) + "$2");
                } else {
                    // Add version number
                    newName = name.replaceFirst("(.+)+(?:__[0-9]+)*?(\\.[a-zA-Z]+)+$", "$1__" + (incr) + "$2");
                }

                f = new File(d, newName);

                x++;

                if (x > maxLoops)
                    break;
            }

            // --------------------------------------------------
            // Write file to disk
            // --------------------------------------------------

            OutputStream out = null;

            try {

                int read = 0;
                byte[] bytes = new byte[1024];

                out = new FileOutputStream(f);
                while ((read = uploadedInputStream.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }

                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                IOUtils.closeQuietly(uploadedInputStream);
                IOUtils.closeQuietly(out);
            }
        }

        return f;
    }

    @Override
    public String getBaseSystemPath() {
        return getBaseSystemPath(null, null);
    }

    @Override
    public String getBaseSystemPath(String mimeType) {
        return getBaseSystemPath(mimeType, null);
    }

    @Override
    public String getBaseSystemPath(Id storeId) {
        return getBaseSystemPath(null, storeId);
    }

    @Override
    public String getBaseSystemPath(String mimeType, Id storeId) {
        String path = null;

        if (storeId != null) {
            if (mimeType != null)
                path = app.cpStr_S(
                    new StringBuilder(CONF_PREFIX).append(mimeType).append(CONF_SUFFIX_BASEPATH).toString(),
                    storeId);

            if (path == null && mimeType != null) {
                String wildcardMimeType = MimeType.toWildcard(mimeType);
                path = app.cpStr_S(
                    new StringBuilder(CONF_PREFIX).append(wildcardMimeType).append(CONF_SUFFIX_BASEPATH).toString(),
                    storeId);
            }

            if (path == null)
                path = app.cpStr_S(
                    new StringBuilder(CONF_PREFIX).append(CONF_DEFAULT).append(CONF_SUFFIX_BASEPATH).toString(),
                    storeId);
        }

        if (path == null) {
            if (mimeType != null)
                path = app.cpStr__(
                    new StringBuilder(CONF_PREFIX).append(mimeType).append(CONF_SUFFIX_BASEPATH).toString());

            if (path == null && mimeType != null) {
                String wildcardMimeType = MimeType.toWildcard(mimeType);
                path = app.cpStr__(new StringBuilder(CONF_PREFIX).append(wildcardMimeType).append(CONF_SUFFIX_BASEPATH)
                    .toString());
            }

            if (path == null)
                path = app.cpStr__(
                    new StringBuilder(CONF_PREFIX).append(CONF_DEFAULT).append(CONF_SUFFIX_BASEPATH).toString());
        }

        path = path.trim();

        if (path.endsWith(Str.SLASH) || path.endsWith(Str.BACKSLASH))
            path = path.substring(0, path.length() - 1);

        File pathFile = new File(path);

        if (pathFile.isAbsolute()) {
            return pathFile.getAbsolutePath();
        } else {
            ApplicationContext appCtx = app.context();
            Merchant m = appCtx.getMerchant();

            return new File(m.getWebPath(), path).getAbsolutePath();
        }
    }

    @Override
    public String getSubdomain() {
        return getSubdomain(null, null);
    }

    @Override
    public String getSubdomain(String mimeType) {
        return getSubdomain(mimeType, null);
    }

    @Override
    public String getSubdomain(Id storeId) {
        return getSubdomain(null, storeId);
    }

    @Override
    public String getSubdomain(String mimeType, Id storeId) {
        String subdomain = null;

        if (storeId != null) {
            if (mimeType != null)
                subdomain = app.cpStr_S(
                    new StringBuilder(CONF_PREFIX).append(mimeType).append(CONF_SUFFIX_SUBDOMAIN).toString(),
                    storeId);

            if (subdomain == null && mimeType != null) {
                String wildcardMimeType = MimeType.toWildcard(mimeType);
                subdomain = app.cpStr_S(new StringBuilder(CONF_PREFIX).append(wildcardMimeType)
                    .append(CONF_SUFFIX_SUBDOMAIN).toString(), storeId);
            }

            if (subdomain == null)
                subdomain = app.cpStr_S(
                    new StringBuilder(CONF_PREFIX).append(CONF_DEFAULT).append(CONF_SUFFIX_SUBDOMAIN).toString(),
                    storeId);
        }

        if (subdomain == null) {
            if (mimeType != null)
                subdomain = app.cpStr__(
                    new StringBuilder(CONF_PREFIX).append(mimeType).append(CONF_SUFFIX_SUBDOMAIN).toString());

            if (subdomain == null && mimeType != null) {
                String wildcardMimeType = MimeType.toWildcard(mimeType);
                subdomain = app.cpStr__(new StringBuilder(CONF_PREFIX).append(wildcardMimeType)
                    .append(CONF_SUFFIX_SUBDOMAIN).toString());
            }

            if (subdomain == null)
                subdomain = app.cpStr__(
                    new StringBuilder(CONF_PREFIX).append(CONF_DEFAULT).append(CONF_SUFFIX_SUBDOMAIN).toString());
        }

        return subdomain;
    }

    @Override
    public String getSize(String sizeName) {
        return getSize(sizeName, null, null);
    }

    @Override
    public String getSize(String sizeName, String mimeType) {
        return getSize(sizeName, mimeType, null);
    }

    @Override
    public String getSize(String sizeName, Id storeId) {
        return getSize(sizeName, null, storeId);
    }

    @Override
    public String getSize(String sizeName, String mimeType, Id storeId) {
        String size = null;

        if (storeId != null) {
            if (mimeType != null)
                size = app.cpStr_S(new StringBuilder(CONF_PREFIX).append(mimeType).append(Char.SLASH).append(sizeName)
                    .append(CONF_SUFFIX_SIZE).toString(), storeId);

            if (size == null && mimeType != null) {
                String wildcardMimeType = MimeType.toWildcard(mimeType);
                size = app.cpStr_S(new StringBuilder(CONF_PREFIX).append(wildcardMimeType).append(Char.SLASH)
                    .append(sizeName).append(CONF_SUFFIX_SIZE).toString(), storeId);
            }

            if (size == null)
                size = app.cpStr_S(new StringBuilder(CONF_PREFIX).append(CONF_DEFAULT).append(Char.SLASH)
                    .append(sizeName).append(CONF_SUFFIX_SIZE).toString(), storeId);
        }

        if (size == null) {
            if (mimeType != null)
                size = app.cpStr__(new StringBuilder(CONF_PREFIX).append(mimeType).append(Char.SLASH).append(sizeName)
                    .append(CONF_SUFFIX_SIZE).toString());

            if (size == null && mimeType != null) {
                String wildcardMimeType = MimeType.toWildcard(mimeType);
                size = app.cpStr__(new StringBuilder(CONF_PREFIX).append(wildcardMimeType).append(Char.SLASH)
                    .append(sizeName).append(CONF_SUFFIX_SIZE).toString());
            }

            if (size == null)
                size = app.cpStr__(new StringBuilder(CONF_PREFIX).append(CONF_DEFAULT).append(Char.SLASH)
                    .append(sizeName).append(CONF_SUFFIX_SIZE).toString());
        }

        return size;
    }

    @Override
    public String getServletPath() {
        return getServletPath(null, null);
    }

    @Override
    public String getServletPath(String mimeType) {
        return getServletPath(mimeType, null);
    }

    @Override
    public String getServletPath(Id storeId) {
        return getServletPath(null, storeId);
    }

    @Override
    public String getServletPath(String mimeType, Id storeId) {
        String servletPath = null;

        if (storeId != null) {
            if (mimeType != null)
                servletPath = app.cpStr_S(
                    new StringBuilder(CONF_PREFIX).append(mimeType).append(CONF_SUFFIX_SERVLETPATH).toString(),
                    storeId);

            if (servletPath == null && mimeType != null) {
                String wildcardMimeType = MimeType.toWildcard(mimeType);
                servletPath = app.cpStr_S(new StringBuilder(CONF_PREFIX).append(wildcardMimeType)
                    .append(CONF_SUFFIX_SERVLETPATH).toString(), storeId);
            }

            if (servletPath == null)
                servletPath = app.cpStr_S(
                    new StringBuilder(CONF_PREFIX).append(CONF_DEFAULT).append(CONF_SUFFIX_SERVLETPATH).toString(),
                    storeId);
        }

        if (servletPath == null) {
            if (mimeType != null)
                servletPath = app.cpStr__(
                    new StringBuilder(CONF_PREFIX).append(mimeType).append(CONF_SUFFIX_SERVLETPATH).toString());

            if (servletPath == null && mimeType != null) {
                String wildcardMimeType = MimeType.toWildcard(mimeType);
                servletPath = app.cpStr__(new StringBuilder(CONF_PREFIX).append(wildcardMimeType)
                    .append(CONF_SUFFIX_SERVLETPATH).toString());
            }

            if (servletPath == null)
                servletPath = app.cpStr__(
                    new StringBuilder(CONF_PREFIX).append(CONF_DEFAULT).append(CONF_SUFFIX_SERVLETPATH).toString());
        }

        servletPath = toWebURI(servletPath.trim());

        if (!servletPath.startsWith(Str.SLASH))
            servletPath = Str.SLASH + servletPath;

        if (servletPath.endsWith(Str.SLASH) || servletPath.endsWith(Str.BACKSLASH_ESCAPED))
            servletPath = servletPath.substring(0, servletPath.length() - 1);

        return servletPath;
    }

    @Override
    public String getSubpath() {
        return getSubpath(null, null);
    }

    @Override
    public String getSubpath(String mimeType) {
        return getSubpath(mimeType, null);
    }

    @Override
    public String getSubpath(Id storeId) {
        return getSubpath(null, storeId);
    }

    @Override
    public String getSubpath(String mimeType, Id storeId) {
        String subpath = null;

        if (storeId != null) {
            if (mimeType != null)
                subpath = app.cpStr_S(
                    new StringBuilder(CONF_PREFIX).append(mimeType).append(CONF_SUFFIX_SUBPATH).toString(),
                    storeId);

            if (subpath == null && mimeType != null) {
                String wildcardMimeType = MimeType.toWildcard(mimeType);
                subpath = app.cpStr_S(
                    new StringBuilder(CONF_PREFIX).append(wildcardMimeType).append(CONF_SUFFIX_SUBPATH).toString(),
                    storeId);
            }

            if (subpath == null)
                subpath = app.cpStr_S(
                    new StringBuilder(CONF_PREFIX).append(CONF_DEFAULT).append(CONF_SUFFIX_SUBPATH).toString(),
                    storeId);
        }

        if (subpath == null) {
            if (mimeType != null)
                subpath = app.cpStr__(
                    new StringBuilder(CONF_PREFIX).append(mimeType).append(CONF_SUFFIX_SUBPATH).toString());

            if (subpath == null && mimeType != null) {
                String wildcardMimeType = MimeType.toWildcard(mimeType);
                subpath = app.cpStr__(
                    new StringBuilder(CONF_PREFIX).append(wildcardMimeType).append(CONF_SUFFIX_SUBPATH).toString());
            }

            if (subpath == null)
                subpath = app.cpStr__(
                    new StringBuilder(CONF_PREFIX).append(CONF_DEFAULT).append(CONF_SUFFIX_SUBPATH).toString());
        }

        subpath = subpath.trim();

        if (subpath.endsWith(Str.SLASH) || subpath.endsWith(Str.BACKSLASH_ESCAPED))
            subpath = subpath.substring(0, subpath.length() - 1);

        return subpath;
    }

    @Override
    public boolean isCacheEnabled() {
        return isCacheEnabled(null, null);
    }

    @Override
    public boolean isCacheEnabled(String mimeType) {
        return isCacheEnabled(mimeType, null);
    }

    @Override
    public boolean isCacheEnabled(Id storeId) {
        return isCacheEnabled(null, storeId);
    }

    @Override
    public boolean isCacheEnabled(String mimeType, Id storeId) {
        Boolean isCacheEnabled = null;

        if (storeId != null) {
            if (mimeType != null)
                isCacheEnabled = app.cpBool_S(
                    new StringBuilder(CONF_PREFIX).append(mimeType).append(CONF_SUFFIX_CACHE_ENABLED).toString(),
                    storeId);

            if (isCacheEnabled == null && mimeType != null) {
                String wildcardMimeType = MimeType.toWildcard(mimeType);
                isCacheEnabled = app.cpBool_S(new StringBuilder(CONF_PREFIX).append(wildcardMimeType)
                    .append(CONF_SUFFIX_CACHE_ENABLED).toString(), storeId);
            }

            if (isCacheEnabled == null)
                isCacheEnabled = app.cpBool_S(new StringBuilder(CONF_PREFIX).append(CONF_DEFAULT)
                    .append(CONF_SUFFIX_CACHE_ENABLED).toString(), storeId);
        }

        if (isCacheEnabled == null) {
            if (mimeType != null)
                isCacheEnabled = app.cpBool__(
                    new StringBuilder(CONF_PREFIX).append(mimeType).append(CONF_SUFFIX_CACHE_ENABLED).toString());

            if (isCacheEnabled == null && mimeType != null) {
                String wildcardMimeType = MimeType.toWildcard(mimeType);
                isCacheEnabled = app.cpBool__(new StringBuilder(CONF_PREFIX).append(wildcardMimeType)
                    .append(CONF_SUFFIX_CACHE_ENABLED).toString());
            }

            if (isCacheEnabled == null)
                isCacheEnabled = app.cpBool__(new StringBuilder(CONF_PREFIX).append(CONF_DEFAULT)
                    .append(CONF_SUFFIX_CACHE_ENABLED).toString());
        }

        return isCacheEnabled == null ? false : isCacheEnabled.booleanValue();
    }

    @Override
    public String getBaseCacheSystemPath() {
        return getBaseCacheSystemPath(null, null);
    }

    @Override
    public String getBaseCacheSystemPath(String mimeType) {
        return getBaseCacheSystemPath(mimeType, null);
    }

    @Override
    public String getBaseCacheSystemPath(Id storeId) {
        return getBaseCacheSystemPath(null, storeId);
    }

    @Override
    public String getBaseCacheSystemPath(String mimeType, Id storeId) {
        String path = null;

        if (storeId != null) {
            if (mimeType != null)
                path = app.cpStr_S(
                    new StringBuilder(CONF_PREFIX).append(mimeType).append(CONF_SUFFIX_CACHE_BASE_PATH).toString(),
                    storeId);

            if (path == null && mimeType != null) {
                String wildcardMimeType = MimeType.toWildcard(mimeType);
                path = app.cpStr_S(new StringBuilder(CONF_PREFIX).append(wildcardMimeType)
                    .append(CONF_SUFFIX_CACHE_BASE_PATH).toString(), storeId);
            }

            if (path == null)
                path = app.cpStr_S(new StringBuilder(CONF_PREFIX).append(CONF_DEFAULT)
                    .append(CONF_SUFFIX_CACHE_BASE_PATH).toString(), storeId);
        }

        if (path == null) {
            if (mimeType != null)
                path = app.cpStr__(
                    new StringBuilder(CONF_PREFIX).append(mimeType).append(CONF_SUFFIX_CACHE_BASE_PATH).toString());

            if (path == null && mimeType != null) {
                String wildcardMimeType = MimeType.toWildcard(mimeType);
                path = app.cpStr__(new StringBuilder(CONF_PREFIX).append(wildcardMimeType)
                    .append(CONF_SUFFIX_CACHE_BASE_PATH).toString());
            }

            if (path == null)
                path = app.cpStr__(new StringBuilder(CONF_PREFIX).append(CONF_DEFAULT)
                    .append(CONF_SUFFIX_CACHE_BASE_PATH).toString());
        }

        if (path != null) {
            path = path.trim();

            if (path.endsWith(Str.SLASH) || path.endsWith(Str.BACKSLASH))
                path = path.substring(0, path.length() - 1);
        } else {
            path = getBaseSystemPath(mimeType, storeId) + "/cache";
        }

        File pathFile = new File(path);

        if (pathFile.isAbsolute()) {
            return pathFile.getAbsolutePath();
        } else {
            ApplicationContext appCtx = app.context();
            Merchant m = appCtx.getMerchant();

            return new File(m.getWebPath(), path).getAbsolutePath();
        }
    }

    @Override
    public String toAbsoluteSystemPath(String relativePath) {
        return toAbsoluteSystemPath(relativePath, null);
    }

    @Override
    public String toAbsoluteSystemPath(String relativePath, Id storeId) {
        if (relativePath == null)
            return null;

        relativePath = relativePath.trim();

        String mimeType = MimeType.fromFilename(relativePath);
        String baseSystemPath = getBaseSystemPath(mimeType, storeId);
        String servletPath = getServletPath(mimeType, storeId);

        String webURI = toWebURI(relativePath);

        if (webURI.startsWith(servletPath)) {
            return new File(
                new StringBuilder(baseSystemPath).append(File.separatorChar).append(relativePath).toString())
                    .getAbsolutePath();
        } else {
            return new File(new StringBuilder(baseSystemPath).append(servletPath).append(File.separatorChar)
                .append(relativePath).toString()).getAbsolutePath();
        }
    }

    @Override
    public String toAbsoluteSystemCachePath(String relativePath) {
        return toAbsoluteSystemCachePath(relativePath, null);
    }

    @Override
    public String toAbsoluteSystemCachePath(String relativePath, Id storeId) {
        if (relativePath == null)
            return null;

        relativePath = relativePath.trim();

        String mimeType = MimeType.fromFilename(relativePath);
        String baseCacheSystemPath = getBaseCacheSystemPath(mimeType, storeId);
        String servletPath = getServletPath(mimeType, storeId);

        // See if any parameters have been attached to the image.
        Map<String, Object> params = extractImageParameters(relativePath);

        Integer targetWidth = (Integer) params.get(IMAGE_PARAM_WIDTH);
        Integer targetHeight = (Integer) params.get(IMAGE_PARAM_HEIGHT);

        StringBuilder sizePart = new StringBuilder();
        if (targetWidth != null || targetHeight != null) {
            sizePart.append(File.separatorChar);

            if (targetWidth != null)
                sizePart.append(targetWidth.intValue());

            sizePart.append("x");

            if (targetHeight != null)
                sizePart.append(targetHeight.intValue());
        }

        String webURI = toWebURI(relativePath);

        if (webURI.startsWith(servletPath)) {
            File f = new File(new StringBuilder(baseCacheSystemPath).append(sizePart.toString())
                .append(File.separatorChar).append(relativePath).toString());
            String safeName = Filenames.ensureSafeName(f.getName(), true, true);
            return new File(f.getParent(), safeName).getAbsolutePath();
        } else {
            File f = new File(new StringBuilder(baseCacheSystemPath).append(sizePart.toString()).append(servletPath)
                .append(File.separatorChar).append(relativePath).toString());
            String safeName = Filenames.ensureSafeName(f.getName(), true, true);
            return new File(f.getParent(), safeName).getAbsolutePath();
        }
    }

    @Override
    public List<File> getCacheFiles(CatalogMediaAsset mediaAsset) {
        if (mediaAsset == null)
            return null;

        List<Id> storeIds = mediaAsset.getStoreIds();

        if (storeIds != null && storeIds.size() > 0) {
            Set<File> cacheFiles = new HashSet<>();

            for (Id storeId : storeIds) {
                List<File> storeCacheFiles = getCacheFiles(mediaAsset.getPath(), storeId);

                if (storeCacheFiles != null && cacheFiles.size() > 0)
                    cacheFiles.addAll(storeCacheFiles);
            }

            return new ArrayList<>(cacheFiles);
        } else {
            return getCacheFiles(mediaAsset.getPath(), null);
        }
    }

    @Override
    public List<File> getCacheFiles(String relativePath, Id storeId) {
        if (relativePath == null)
            return null;

        List<File> cachedFiles = new ArrayList<File>();

        relativePath = relativePath.trim();

        String mimeType = MimeType.fromFilename(relativePath);
        String baseCacheSystemPath = getBaseCacheSystemPath(mimeType, storeId);
        String servletPath = getServletPath(mimeType, storeId);

        String webURI = toWebURI(relativePath);

        if (!webURI.startsWith(servletPath))
            webURI = new StringBuilder(servletPath).append(Str.SLASH).append(webURI).toString();

        String baseRelativePath = webURI.substring(0, webURI.lastIndexOf(Char.SLASH));

        File baseCachrDir = new File(baseCacheSystemPath);

        if (baseCachrDir.exists()) {
            File[] cacheDirs = baseCachrDir.listFiles();

            if (cacheDirs != null && cacheDirs.length > 0) {
                for (File cacheDir : cacheDirs) {
                    File cachedFile = new File(cacheDir, baseRelativePath);

                    System.out.println("Checking: " + cachedFile);

                    if (cachedFile.exists())
                        cachedFiles.add(cachedFile);
                }
            }
        }

        return cachedFiles;
    }

    @Override
    public void cacheMediaAsset(InputStream is, String mediaAssetURI, boolean replace) {
        // --------------------------------------------------
        // Get filename and create directories.
        // --------------------------------------------------
        String systemCachePath = toAbsoluteSystemCachePath(mediaAssetURI);

        File f = new File(systemCachePath);

        if (!replace && f.exists()) {
            return;
        }

        File d = f.getParentFile();

        if (!d.exists()) {
            d.mkdirs();
        }

        // --------------------------------------------------
        // Write file to disk
        // --------------------------------------------------

        OutputStream out = null;

        try {
            int read = 0;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(f);
            while ((read = is.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(out);
        }
    }

    @Override
    public InputStream getStream(String absSystemPath) {
        try {
            return new FileInputStream(absSystemPath);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    @Override
    public byte[] getImage(String mediaAssetURI) {
        byte[] bytes = null;

        String absSystemPath = toAbsoluteSystemPath(mediaAssetURI);
        String absSystemCachePath = toAbsoluteSystemCachePath(mediaAssetURI);

        // See if any parameters have been attached to the image.
        Map<String, Object> params = extractImageParameters(absSystemPath);

        // If so, remove them to get the original image URI.
        if (params.size() > 0) {
            absSystemPath = removeImageParamsFromURI(absSystemPath);
        }

        if (exists(absSystemPath)) {
            try {
                // The disk cache can be enabled/disabled optionally. Note,
                // disabling the disk cache should only be done
                // if images are cached by a CDN or some kind of caching server
                // like Varnish.
                boolean isCacheEnabled = isCacheEnabled(MimeType.fromFilename(mediaAssetURI));
                boolean isPreview = app.previewHeaderExists();
                boolean isRefresh = app.refreshHeaderExists();

                File targetFile = new File(absSystemCachePath);

                // Only render the image if it does not exist yet or
                // refresh-flag is set.
                if (!isRefresh && targetFile.exists()) {
                    return Files.readAllBytes(targetFile.toPath());
                }

                // Make sure that the target directories exist. Only needed when
                // caching image on disk.
                if (isCacheEnabled && !isPreview) {
                    File d = targetFile.getParentFile();
                    if (!d.exists()) {
                        d.mkdirs();
                    }
                }

                File sourceFile = new File(absSystemPath);
                Info imageInfo = new Info(absSystemPath, false);

                Integer targetWidth = (Integer) params.get(IMAGE_PARAM_WIDTH);
                Integer targetHeight = (Integer) params.get(IMAGE_PARAM_HEIGHT);

                // Are parameters for resizing set?
                if (targetWidth != null || targetHeight != null) {
                    // If so, calculate new size maintaining the aspect-ratio.
                    double originalWidth = imageInfo.getImageWidth();
                    double originalHeight = imageInfo.getImageHeight();

                    // Now Calculate the Aspect Ratio
                    double aspectRatio = (double) originalWidth / (double) originalHeight;

                    if (targetWidth == null)
                        targetWidth = (int) (aspectRatio < 1 ? (targetHeight * aspectRatio)
                            : (targetHeight / aspectRatio));

                    if (targetHeight == null)
                        targetHeight = (int) (aspectRatio < 1 ? (targetWidth * aspectRatio)
                            : (targetWidth / aspectRatio));

                    // Check if the height of image is greater than the allowed
                    // height and reset it
                    double newHeight = (originalHeight > targetHeight) ? targetHeight : originalHeight;
                    // Check if the width of image is greater than the allowed
                    // width and reset it
                    double newWidth = (originalWidth > targetWidth) ? targetWidth : originalWidth;
                    // Calculate new height or width according to aspect ratio
                    // based on the orientation of the picture

                    // If width is greater than the height it’s a Landscape
                    if (newWidth > newHeight) {
                        // Calculate new Height by multiplying Width with aspect
                        // ratio
                        newHeight = (int) Math.round(newWidth / aspectRatio);
                        // in some cases newly calculated height can be greater
                        // than the Max Allowed Height so we trim
                        // down the height to maximum allowed limit limit and
                        // calculate the width

                        if (newHeight > targetHeight) {
                            newHeight = targetHeight;
                            newWidth = (int) Math.round(aspectRatio * newHeight);
                        }
                    }

                    // Otherwise it’s a portrait
                    else {
                        // Calculate new Width by multiplying Height with aspect
                        // ratio
                        newWidth = (int) Math.round(aspectRatio * newHeight);

                        if (newWidth > targetWidth) {
                            newWidth = targetWidth;
                            newHeight = (int) Math.round(newWidth / aspectRatio);
                        }
                    }

                    // Create the im4java operation.
                    IMOperation op = new IMOperation();

                    if (isCacheEnabled && !isPreview) {
                        // Just pass the source image path to the ImageMagick
                        // command.
                        op.addImage(absSystemPath);
                    } else {
                        // When not caching to disk we pass a BufferedImage to
                        // the convert command.
                        op.addImage();
                    }

                    op.quality(75.0);
                    op.interlace("Plane");
                    op.depth(8);
                    op.thumbnail((int) Math.floor(targetWidth), (int) Math.floor(targetHeight));

                    String originalColorspace = imageInfo.getProperty("Colorspace");

                    // Convert to RGB if necessary as the Internet Explorer < 9
                    // cannot cope with CMYK.
                    if (MimeType.fromFilename(sourceFile.getName()) == MimeType.IMAGE_JPEG
                        && "CMYK".equalsIgnoreCase(originalColorspace)) {
                        op.colorspace("RGB");
                    }

                    // Create command
                    ConvertCmd convert = new ConvertCmd();

                    if (isCacheEnabled && !isPreview) {
                        // Just pass the target path to the ImageMagick command.
                        op.addImage("jpeg:" + absSystemCachePath);
                        convert.run(op);

                        // After image has been converted we grab the bytes to
                        // return.
                        bytes = Files.readAllBytes(targetFile.toPath());
                    } else {
                        // When not caching to disk we to not specify a target
                        // path, but instead retrieve a
                        // BufferedImage after processing.
                        op.addImage("jpg:-");

                        BufferedImage srcImage = ImageIO.read(sourceFile);

                        Stream2BufferedImage s2b = new Stream2BufferedImage();
                        convert.setOutputConsumer(s2b);

                        convert.run(op, srcImage);
                        BufferedImage targetImage = s2b.getImage();

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(targetImage, "jpg", baos);
                        bytes = baos.toByteArray();
                    }
                } else {
                    return Files.readAllBytes(sourceFile.toPath());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return bytes;
    }

    private boolean exists(String systemPath) {
        return new File(systemPath).exists();
    }

    private String removeImageParamsFromURI(String imgUri) {
        return imgUri.replaceFirst("(.+)___(?:.+)\\.(.+)$", "$1.$2");
    }

    private static Map<String, Object> extractImageParameters(String imageUri) {
        Map<String, Object> params = new HashMap<>();

        Matcher m = patternImageParameters.matcher(imageUri);

        if (m.find()) {
            String group = m.group(1);

            String[] groupParts = group.split(",");

            for (String keyValue : groupParts) {
                String[] kv = keyValue.split(":");

                String key = imageArgKeys.get(kv[0]);
                String value = kv[1];

                if ("size".equals(key)) {
                    int xPos = value.indexOf("x");

                    Integer width = null;
                    Integer height = null;

                    if (xPos > 0) {
                        width = Integer.parseInt(value.substring(0, xPos));
                    }

                    if (xPos + 1 < value.length()) {
                        height = Integer.parseInt(value.substring(xPos + 1, value.length()));
                    }

                    if (width != null)
                        params.put("width", width);

                    if (height != null)
                        params.put("height", height);
                } else if (key != null && value != null) {
                    params.put(key, value);
                }
            }
        }

        return params;
    }

    private boolean isAcceptedMimeType(String mimeType) {
        List<String> acceptedMimeTypes = app.cpStrList_(ConfigurationKey.MEDIA_IMAGES_PRODUCT_ACCEPT_MIME_TYPES);

        if (acceptedMimeTypes == null)
            return false;

        return acceptedMimeTypes.contains(mimeType);
    }

    @Override
    public String getNewAbsoluteFilePath(String originalFilename, String customFilename, String mimeType,
        Product product, Id forStore) {
        String baseSystemPath = getBaseSystemPath(mimeType, forStore);
        String servletPath = getServletPath(mimeType, forStore);
        String relPath = getRelativeAssetPath(originalFilename, customFilename, mimeType, product, forStore);

        return new File(new StringBuilder(baseSystemPath).append(File.separatorChar).append(servletPath)
            .append(File.separatorChar).append(relPath).toString()).getAbsolutePath();
    }

    @Override
    public String toRelativeAssetPath(String absoluteFilePath) {
        return toRelativeAssetPath(absoluteFilePath, null);
    }

    @Override
    public String toRelativeAssetPath(String absoluteFilePath, Id forStore) {
        if (absoluteFilePath == null)
            return null;

        String mimeType = MimeType.fromFilename(absoluteFilePath);

        String baseSystemPath = getBaseSystemPath(mimeType, forStore);
        String servletPath = getServletPath(mimeType, forStore);

        String absPath = new File(absoluteFilePath).getAbsolutePath();
        String basePath = new File(baseSystemPath, servletPath).getAbsolutePath();

        String relPath = absPath.replace(basePath, Str.EMPTY).trim();

        if (relPath.startsWith(Str.SLASH) || relPath.startsWith(Str.BACKSLASH))
            relPath = relPath.substring(1);

        return relPath;
    }

    @Override
    public String toWebURI(String relativePath) {
        if (relativePath == null)
            return null;

        return relativePath.replaceAll(Str.BACKSLASH_ESCAPED, Str.SLASH);
    }

    @Override
    public String getRelativeAssetPath(String originalFilename, String customFilename, String mimeType, Product product,
        Id forStore) {
        ImageFilenameOrigin filenameOrigin = ImageFilenameOrigin.PRODUCT_NAME;

        if (customFilename != null)
            filenameOrigin = ImageFilenameOrigin.CUSTOM;

        AttributeValue productGroup = product.getAttribute("product_group");
        AttributeValue programme = product.getAttribute("programme");

        String productGroupStr = null;
        String programmeStr = null;

        if (productGroup != null)
            productGroupStr = productGroup.getAttributeOptions().values().iterator().next().getLabel().getStr();

        if (programme != null)
            programmeStr = programme.getAttributeOptions().values().iterator().next().getLabel().getStr();

        String filenamePart = null;

        switch (filenameOrigin) {
        case ARTICLE_NUMBER:
            filenamePart = product.getArticleNumber();
            break;
        case CUSTOM:
            if (customFilename != null)
                filenamePart = customFilename.substring(customFilename.lastIndexOf(Char.DOT));
            break;
        case ID:
            filenamePart = product.getId().str();
            break;
        case ORIGINAL_FILENAME:
            if (originalFilename != null)
                filenamePart = originalFilename.substring(originalFilename.lastIndexOf(Char.DOT));
            break;
        case PRODUCT_NAME:
            StringBuilder filename = new StringBuilder();

            AttributeValue name = product.getAttribute("name");
            AttributeValue name2 = product.getAttribute("name2");
            AttributeValue color = product.getAttribute("color");

            if (!Str.isEmpty(productGroupStr)) {
                filename.append(productGroupStr).append("-");
            }

            if (!Str.isEmpty(programmeStr)) {
                filename.append(programmeStr).append("-");
            }

            if (name2 != null && productGroupStr == null && programmeStr == null) {
                String n = name2.getStr();

                if (!Str.isEmpty(n))
                    filename.append("-").append(n);
            }

            if (name != null) {
                String n = name.getStr();

                if (!Str.isEmpty(n))
                    filename.append(n);
            }

            if (color != null && color.getAttributeOptions() != null && !color.getAttributeOptions().isEmpty()) {
                String c = color.getAttributeOptions().values().iterator().next().getLabel().getStr();

                if (!Str.isEmpty(c))
                    filename.append("-").append(c);
            }

            filename.append("-").append(product.getArticleNumber());

            filenamePart = Strings.slugify(filename.toString());
            break;
        }

        StringBuilder filename = new StringBuilder();

        String subpath = getSubpath(mimeType, forStore);

        if (subpath != null)
            filename.append(subpath).append(File.separatorChar);

        if (filenamePart == null || "".equals(filenamePart.trim())) {
            filenamePart = product.getArticleNumber();

            if (filenamePart == null) {
                filenamePart = product.getId().str();
            }
        }

        if (productGroupStr != null && !"".equals(productGroupStr)) {
            filename.append(Filenames.ensureSafeName(productGroupStr.toLowerCase()));
        } else if (programmeStr != null && !"".equals(programmeStr)) {
            filename.append(Filenames.ensureSafeName(programmeStr.toLowerCase()));
        } else {
            filename.append("_");
        }

        filename.append(File.separatorChar);

        String safeName = Filenames.ensureSafeName(filenamePart.replace('.', '-'), true);

        String dirNameSource = null;

        if (product.getArticleNumber() != null) {
            dirNameSource = Filenames.ensureSafeName(product.getArticleNumber().replace('.', '-'), true);
        } else {
            dirNameSource = safeName;
        }

        if (dirNameSource.length() > 1)
            filename.append(dirNameSource.substring(0, 2));

        if (dirNameSource.length() > 3)
            filename.append(File.separatorChar).append(dirNameSource.substring(2, 4));

        if (dirNameSource.length() > 5)
            filename.append(File.separatorChar).append(dirNameSource.substring(4, 6));

        if (dirNameSource.length() > 7)
            filename.append(File.separatorChar).append(dirNameSource.substring(6, 8));

        filename.append(File.separatorChar);

        if (filenameOrigin != ImageFilenameOrigin.CUSTOM && filenameOrigin != ImageFilenameOrigin.ORIGINAL_FILENAME) {
            filename.append(safeName).append(Char.DOT).append(getFileExtension(mimeType, originalFilename));
        } else {
            if (filenameOrigin == ImageFilenameOrigin.CUSTOM) {
                filename.append(customFilename);
            } else {
                filename.append(originalFilename);
            }
        }

        return filename.toString();
    }

    private String getFileExtension(String mimeType, String originalFilename) {
        String extension = MimeType.toFileExtension(mimeType);

        if (extension == null)
            extension = originalFilename.substring(originalFilename.lastIndexOf('.'));

        return extension;
    }

    @Override
    public String buildURL(String uri) {
        return buildURL(uri, null, null);
    }

    @Override
    public String buildURL(String uri, Integer width, Integer height) {
        if (uri == null)
            return null;

        String urlParams = null;

        if (MimeType.isImage(uri) && (width != null || height != null)) {
            String sizeParam = "s:";

            if (width != null && width > 0) {
                sizeParam += width;
            }

            sizeParam += 'x';

            if (height != null && height > 0) {
                sizeParam += height;
            }

            urlParams = "___";
            if (sizeParam.length() > 3) {
                urlParams += sizeParam;
            }
        }

        ApplicationContext appCtx = app.context();
        Store store = appCtx.getStore();
        String mimeType = MimeType.fromFilename(uri);

        String servletPath = getServletPath(mimeType, store.getId());
        String subdomain = getSubdomain(mimeType, store.getId());

        String absURI = servletPath + Str.SLASH + uri;

        if (urlParams != null && urlParams.length() > 3) {
            absURI = absURI.replaceFirst("(.*)\\.(jpg|jpeg|png|gif)$", "$1" + urlParams + ".$2");
        }

        absURI = absURI.replace(Str.BACKSLASH_ESCAPED, Str.SLASH);

        boolean isCacheEnabed = isCacheEnabled(mimeType, store.getId());
        boolean isPreview = app.previewHeaderExists();
        boolean isRefresh = app.refreshHeaderExists();

        if (isCacheEnabed && !isPreview && !isRefresh) {
            String systemCachePath = toAbsoluteSystemCachePath(absURI);

            File cachedFile = new File(systemCachePath);

            if (cachedFile.exists()) {
                absURI = systemCachePath.replace(Str.BACKSLASH, Str.SLASH);
                absURI = absURI.substring(absURI.indexOf("/cache"));
            }
        }

        return Str.SLASH_2X + subdomain + absURI;
    }

    @Override
    public String toMediaAssetURL(String imageURI) {
        return toMediaAssetURL(imageURI, null, null);
    }

    @Override
    public String toMediaAssetURL(String mediaAssetURI, Integer width, Integer height) {
        if (mediaAssetURI == null)
            return null;

        String mediaAssetURL = null;

        try {
            CacheManager cm = app.inject(CacheManager.class);
            Cache<String, String> c = cm.getCache(CACHE_NAME);

            String key = new StringBuilder(mediaAssetURI).append(Str.AT).append(width == null ? Str.EMPTY : width)
                .append('x').append(height == null ? Str.EMPTY : height).toString();

            mediaAssetURL = c.get(key);

            boolean isPreview = app.previewHeaderExists();
            boolean isRefresh = app.refreshHeaderExists();

            if (mediaAssetURL == null || isPreview || isRefresh) {
                ApplicationContext appCtx = app.context();
                Store store = appCtx.getStore();
                String mimeType = MimeType.fromFilename(mediaAssetURI);

                boolean isFileCacheEnabled = isCacheEnabled(mimeType, store.getId());

                mediaAssetURL = buildURL(mediaAssetURI, width, height);

                // If the file cache is enabled, we only cache the cache-path.
                // if ((isFileCacheEnabled && mediaAssetURL.contains("cache"))
                // || !MimeType.isImage(mediaAssetURI))
                if (!isPreview) {
                    c.put(key, mediaAssetURL);
                }
                // else if (!isFileCacheEnabled)
                // {
                // c.put(key, mediaAssetURL);
                // }
            }
        } catch (Throwable t) {
//            System.out.println("ERROR WHEN GETTING MEDIA-ASSET URL FOR URI: " + mediaAssetURI);
//            t.printStackTrace();
        }

        return mediaAssetURL;
    }

    @Override
    public void removeFromCaches(CatalogMediaAsset mediaAsset) {
        if (mediaAsset == null)
            return;

        List<File> cacheFiles = getCacheFiles(mediaAsset);

        for (File cacheFile : cacheFiles) {
            if (cacheFile.isDirectory()) {
                File[] filesInDir = cacheFile.listFiles();

                for (File fileInDir : filesInDir) {
                    System.out.println("Deleting cache-file: " + fileInDir.getAbsolutePath());
                    fileInDir.delete();
                }
            } else {
                System.out.println("Deleting cache-file: " + cacheFile.getAbsolutePath());
                cacheFile.delete();
            }
        }

        CacheManager cm = app.inject(CacheManager.class);
        Cache<String, String> c = cm.getCache(CACHE_NAME);

        System.out.println("ATTEMPTING TO DELETE VALUES FROM CACHE: " + CACHE_NAME + " --> " + c + " --> "
            + (c == null ? 0 : c.size()));

        if (c != null && c.size() > 0) {
            String[] keys = c.keySet();

            System.out.println("ATTEMPTING TO DELETE VALUES FROM CACHE: " + Arrays.asList(keys));

            String baseMediaAssetPath = mediaAsset.getPath().substring(0, mediaAsset.getPath().lastIndexOf(Char.SLASH));

            System.out.println("BASE MEDIA PATH IS: " + baseMediaAssetPath);

            int x = 0;
            for (String cacheKey : keys) {

                System.out.println("CHECKING IF CACHEKEY STARTS WITH: " + baseMediaAssetPath + " ---> " + cacheKey);

                if (cacheKey.startsWith(baseMediaAssetPath)) {
                    System.out.println((++x) + ": " + baseMediaAssetPath + " --> " + cacheKey);

                    c.remove(cacheKey);
                }
            }
        }
    }
}
