package com.geecommerce.core.template.freemarker.directive;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.geecommerce.core.App;
import com.geecommerce.core.ApplicationContext;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.cache.Cache;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.system.model.RequestContext;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class BundleDirective implements TemplateDirectiveModel {
    private static final String TYPE_CSS = "css";
    private static final String TYPE_JS = "js";

    private static final String KEY_WEB_CACHE_ENABLED = "general/web/cache/enabled";
    private static final String KEY_WEB_CACHE_BASE_PATH = "general/web/cache/base_path";
    private static final String KEY_WEB_BUNDLE_RESOURCES_ENABLED = "general/web/bundle_resources/enabled";

    private static final String CACHE_NAME = "gc/web/bundle_directive";

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
        throws TemplateException, IOException {
        long start = System.currentTimeMillis();

        App app = App.get();

        // Type of file that we want to process. Can only be "css" or "js".
        SimpleScalar pType = (SimpleScalar) params.get("type");
        SimpleScalar pName = (SimpleScalar) params.get("name");

        String type = null;
        String name = null;

        if (pType == null)
            throw new IllegalArgumentException("The type argument must be provided when using the bundle directive.");

        type = pType.getAsString().trim().toLowerCase();

        if (!TYPE_CSS.equals(type) && !TYPE_JS.equals(type))
            throw new IllegalArgumentException("The type argument can only be 'css' or 'js' in the bundle directive.");

        // No files to bundle.
        if (body == null)
            return;

        CacheManager cm = app.inject(CacheManager.class);
        Cache<String, String> c = cm.getCache(CACHE_NAME);

        StringWriter sw = new StringWriter();
        body.render(sw);
        String bodyStr = sw.toString();

        // No files to bundle.
        if (bodyStr == null || "".equals(bodyStr.trim()))
            return;

        if (pName != null)
            name = pName.getAsString();

        // In order to avoid browser caching problems, we add a version number
        // to the URLs.
        // The version number could be automatically generated during
        // deployment.
        String version = app.getVersion();
        // If in dev-mode, we append a timestamp instead, so that we always have
        // the newest version.
        boolean isDevMode = app.isDevMode();

        String cacheKey = new StringBuilder(name == null ? "" : name).append(Char.UNDERSCORE).append(bodyStr.hashCode())
            .append(Char.UNDERSCORE).append(version).toString();

        // Cache generated HTML.
        boolean isWebCacheEnabled = app.cpBool_(KEY_WEB_CACHE_ENABLED, false);
        // Should resources be bundled or have their own link and script tags.
        boolean isBundleResourcesEnabled = app.cpBool_(KEY_WEB_BUNDLE_RESOURCES_ENABLED, false);

        String html = null;

        if (isWebCacheEnabled)
            html = c.get(cacheKey);

        if (html == null) {
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;

            try {
                String targetPath = null;

                // Get the target path if one has been configured.
                String webCachePath = app.cpStr_(KEY_WEB_CACHE_BASE_PATH);

                if (webCachePath != null && !"".equals(webCachePath.trim()))
                    targetPath = new StringBuilder(webCachePath).append(File.separatorChar).append(type).toString();

                // If no target path exists, we use the standard skin path.
                if (targetPath == null)
                    targetPath = app.getWebappSkinPath();

                // Split all the comma-separated resource files.
                String[] resourcePaths = bodyStr.split(Str.COMMA);

                StringBuilder bundledFileContent = new StringBuilder();
                StringBuilder htmlBuilder = new StringBuilder();

                for (String resourcePath : resourcePaths) {
                    // If bundling is enabled, we collect all of the file
                    // contents.
                    if (isBundleResourcesEnabled) {
                        StringBuilder absPath = new StringBuilder(app.getBaseWebappPath()).append(resourcePath.trim());

                        fis = new FileInputStream(absPath.toString());
                        bis = new BufferedInputStream(fis);

                        bundledFileContent.append("\n\n/* ---------- ").append(resourcePath.trim())
                            .append(" ---------- */\n\n");
                        bundledFileContent.append(IOUtils.toString(bis, "UTF-8"));
                    }
                    // Otherwise we simply wrap each resource in a link or
                    // script tag accordingly.
                    else {
                        if (TYPE_CSS.equals(type)) {
                            htmlBuilder.append("<link href=\"").append(resourcePath.trim())
                                .append("?v=" + (isDevMode ? System.currentTimeMillis() : version))
                                .append("\" type=\"text/css\" rel=\"stylesheet\" />\n");
                        } else {
                            htmlBuilder.append("<script src=\"").append(resourcePath.trim())
                                .append("?v=" + (isDevMode ? System.currentTimeMillis() : version))
                                .append("\" type=\"text/javascript\"></script>\n");
                        }
                    }
                }

                // Store the contents from all files into one bundle-file if
                // bundling is enabled.
                if (isBundleResourcesEnabled) {
                    ApplicationContext appCtx = app.context();
                    RequestContext reqCtx = appCtx.getRequestContext();

                    StringBuilder absTargetPath = new StringBuilder(targetPath).append(File.separatorChar)
                        .append("bundle_");

                    // Optionally add a bundle-name to the target-filename if
                    // one exists.
                    if (name != null) {
                        absTargetPath.append(name).append(Char.UNDERSCORE);
                    }

                    absTargetPath.append(reqCtx.getId().str()).append(Char.DOT).append(type);

                    File targetFile = new File(absTargetPath.toString());
                    File targetDir = targetFile.getParentFile();

                    if (!targetDir.exists())
                        targetDir.mkdirs();

                    // Write the new bundle-file to disk.
                    fos = new FileOutputStream(targetFile);
                    bos = new BufferedOutputStream(fos);

                    IOUtils.write(bundledFileContent.toString(), bos, "UTF-8");

                    // Now we create the appropriate HTML tags for the new
                    // bundle-file.
                    if (TYPE_CSS.equals(type)) {
                        htmlBuilder.append("<link href=\"").append(toWebpath(targetFile.getAbsolutePath(), targetPath))
                            .append("?v=" + (isDevMode ? System.currentTimeMillis() : version))
                            .append("\" type=\"text/css\" rel=\"stylesheet\" />");
                    } else {
                        htmlBuilder.append("<script src=\"").append(toWebpath(targetFile.getAbsolutePath(), targetPath))
                            .append("?v=" + (isDevMode ? System.currentTimeMillis() : version))
                            .append("\" type=\"text/javascript\"></script>");
                    }
                }

                html = htmlBuilder.toString();

                if (isWebCacheEnabled)
                    c.put(cacheKey, html);
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                IOUtils.closeQuietly(bos);
                IOUtils.closeQuietly(fos);
                IOUtils.closeQuietly(bis);
                IOUtils.closeQuietly(fis);
            }
        }

        // Write the generated output to the stream.
        if (html != null && !"".equals(html.trim()))
            env.getOut().write(html);

        // System.out.println("BundleDirective took: " +
        // (System.currentTimeMillis() - start) + "ms");
    }

    public String toWebpath(String systemFilePath, String basePath) {
        File baseDir = new File(basePath);

        String webpath = systemFilePath.replace('\\', '/');

        if (webpath.indexOf("/cache") != -1) {
            int pos = webpath.indexOf("/cache");
            return webpath.substring(pos);
        } else {
            int pos = webpath.indexOf("/" + baseDir.getName());
            return webpath.substring(pos);
        }
    }
}
