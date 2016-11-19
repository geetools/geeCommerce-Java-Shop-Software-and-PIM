package com.geecommerce.core.template.freemarker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.cache.Cache;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.config.SystemConfig;
import com.geecommerce.core.system.merchant.model.Merchant;
import com.geecommerce.core.system.merchant.model.Store;
import com.geecommerce.core.system.merchant.model.View;

import freemarker.cache.TemplateLoader;

public class FreemarkerMerchantTemplateLoader implements TemplateLoader {
    private static final Logger log = LogManager.getLogger(FreemarkerMerchantTemplateLoader.class);

    private static final File NOT_FOUND = new File("__404__");

    public FreemarkerMerchantTemplateLoader() {
    }

    @Override
    public void closeTemplateSource(final Object templateSource) throws IOException {
    }

    @Override
    public Object findTemplateSource(final String name) throws IOException {
        CacheManager cm = App.get().inject(CacheManager.class);
        Cache<String, File> c = cm.getCache(getClass().getName());

        File templatePath = c.get(name);

        if (NOT_FOUND.equals(templatePath))
            return null;

        if (templatePath == null) {
            StringBuilder inName = new StringBuilder(name);

            if (!name.startsWith(Str.SLASH)) {
                inName.insert(0, Char.SLASH);
            }

            String templatesBasePath = getTemplatesBasePath();

            if (inName.indexOf(templatesBasePath) == 0) {
                inName.replace(0, templatesBasePath.length(), Str.EMPTY);
            } else {
                throw new SecurityException("The template path must begin with: " + templatesBasePath);
            }

            if (inName.indexOf(Str.SLASH) != 0) {
                inName.insert(0, Char.SLASH);
            }

            // -----------------------------------------------
            // View templates path.
            // -----------------------------------------------
            File viewFSTemplatePath = locateViewTemplateInFileSystem(inName.toString(), false);

            if (viewFSTemplatePath != null)
                templatePath = viewFSTemplatePath;

            // -----------------------------------------------
            // Store templates path.
            // -----------------------------------------------
            if (templatePath == null) {
                File storeFSTemplatePath = locateStoreTemplateInFileSystem(inName.toString(), false);

                if (storeFSTemplatePath != null)
                    templatePath = storeFSTemplatePath;
            }

            // -----------------------------------------------
            // Merchant templates path.
            // -----------------------------------------------
            if (templatePath == null) {
                File merchantFSTemplatePath = locateMerchantTemplateInFileSystem(inName.toString());

                if (merchantFSTemplatePath != null)
                    templatePath = merchantFSTemplatePath;
            }

            // -----------------------------------------------
            // View templates path from parent if it exists.
            // -----------------------------------------------
            if (templatePath == null) {
                viewFSTemplatePath = locateViewTemplateInFileSystem(inName.toString(), true);

                if (viewFSTemplatePath != null)
                    templatePath = viewFSTemplatePath;
            }

            // -----------------------------------------------
            // Store templates path from parent if it exists.
            // -----------------------------------------------
            if (templatePath == null) {
                File storeFSTemplatePath = locateStoreTemplateInFileSystem(inName.toString(), true);

                if (storeFSTemplatePath != null)
                    templatePath = storeFSTemplatePath;
            }

            if (templatePath != null) {
                c.put(name, templatePath);
            } else {
                c.put(name, NOT_FOUND);
            }
        }

        return templatePath;
    }

    private final File locateViewTemplateInFileSystem(final String templateName, boolean useParent) {
        Merchant merchant = App.get().getApplicationContext().getMerchant();
        View view = merchant.getViewFor(App.get().getApplicationContext().getRequestContext());

        if (view == null)
            return null;

        if (useParent && view.getParentViewId() != null) {
            view = merchant.getView(view.getParentViewId());
        }

        if (view == null)
            return null;

        File viewTemplateFile = new File(view.getTemplatesPath(), templateName);

        // System.out.println("Trying template: " + viewTemplateFile);

        if (viewTemplateFile.exists()) {
            if (!viewTemplateFile.isFile()) {
                if (log.isWarnEnabled())
                    log.warn("The located view template '" + viewTemplateFile.getAbsolutePath() + "' does not appear to be a regular file.");
            } else if (!viewTemplateFile.canRead()) {
                if (log.isWarnEnabled())
                    log.warn("The located view template '" + viewTemplateFile.getAbsolutePath() + "' does not have the necessary read permission.");
            } else {
                if (log.isDebugEnabled())
                    log.debug("Using view template '" + viewTemplateFile.getAbsolutePath() + "'.");

                return viewTemplateFile;
            }
        }

        return null;
    }

    private final File locateStoreTemplateInFileSystem(final String templateName, boolean useParent) {
        Merchant merchant = App.get().getApplicationContext().getMerchant();
        Store store = merchant.getStoreFor(App.get().getApplicationContext().getRequestContext());

        if (store == null)
            return null;

        if (useParent && store.getParentStoreId() != null) {
            store = merchant.getStore(store.getParentStoreId());
        }

        if (store == null)
            return null;

        File storeTemplateFile = new File(store.getTemplatesPath(), templateName);

        // System.out.println("Trying template: " + storeTemplateFile);

        if (storeTemplateFile.exists()) {
            if (!storeTemplateFile.isFile()) {
                if (log.isWarnEnabled())
                    log.warn("The located store template '" + storeTemplateFile.getAbsolutePath() + "' does not appear to be a regular file.");
            } else if (!storeTemplateFile.canRead()) {
                if (log.isWarnEnabled())
                    log.warn("The located store template '" + storeTemplateFile.getAbsolutePath() + "' does not have the necessary read permission.");
            } else {
                if (log.isDebugEnabled())
                    log.debug("Using store template '" + storeTemplateFile.getAbsolutePath() + "'.");

                return storeTemplateFile;
            }
        }

        return null;
    }

    private final File locateMerchantTemplateInFileSystem(final String templateName) {
        Merchant merchant = App.get().getApplicationContext().getMerchant();

        File merchantTemplateFile = new File(merchant.getTemplatesPath(), templateName);

        // System.out.println("Trying template: " + merchantTemplateFile);

        if (merchantTemplateFile.exists()) {
            if (!merchantTemplateFile.isFile()) {
                if (log.isWarnEnabled())
                    log.warn("The located merchant template '" + merchantTemplateFile.getAbsolutePath() + "' does not appear to be a regular file.");
            } else if (!merchantTemplateFile.canRead()) {
                if (log.isWarnEnabled())
                    log.warn("The located merchant template '" + merchantTemplateFile.getAbsolutePath() + "' does not have the necessary read permission.");
            } else {
                if (log.isDebugEnabled())
                    log.debug("Using merchant template '" + merchantTemplateFile.getAbsolutePath() + "'.");

                return merchantTemplateFile;
            }
        }

        return null;
    }

    private final String getTemplatesBasePath() {
        String templatesPath = SystemConfig.GET.val(SystemConfig.APPLICATION_TEMPLATE_PATH);

        if (templatesPath == null) {
            throw new IllegalStateException("The System.properties configuration element 'Application.Template.Path' cannot be null");
        }

        templatesPath = templatesPath.trim();

        StringBuilder sb = new StringBuilder(templatesPath);

        if (!templatesPath.endsWith(Str.SLASH)) {
            sb.append(Char.SLASH);
        }

        return sb.toString();
    }

    @Override
    public final long getLastModified(final Object templateSource) {
        if (!(templateSource instanceof File))
            throw new IllegalArgumentException("the parameter templateSource is of type '" + templateSource.getClass().getName() + "', java.io.File expected.");

        return new Long(((File) templateSource).lastModified());
    }

    @Override
    public final Reader getReader(final Object templateSource, final String encoding) throws IOException {
        if (!(templateSource instanceof File))
            throw new IllegalArgumentException("the parameter templateSource is of type '" + templateSource.getClass().getName() + "', java.io.File expected.");

        return new InputStreamReader(new FileInputStream((File) templateSource), encoding);
    }
}
