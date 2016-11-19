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
import com.geemodule.api.Module;

import freemarker.cache.TemplateLoader;

public class FreemarkerModuleTemplateLoader implements TemplateLoader {
    private static final Logger log = LogManager.getLogger(FreemarkerModuleTemplateLoader.class);

    private static final File NOT_FOUND = new File("__404__");

    private final Module module;

    public FreemarkerModuleTemplateLoader(final Module module) {
        this.module = module;
    }

    @Override
    public void closeTemplateSource(final Object templateSource) throws IOException {
    }

    @Override
    public Object findTemplateSource(final String name) throws IOException {
        if (module == null)
            return null;

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

            File moduleTemplatePath = locateModuleTemplateInFileSystem(inName.toString());

            if (moduleTemplatePath != null) {
                templatePath = moduleTemplatePath;

                c.put(name, templatePath);
            } else {
                c.put(name, NOT_FOUND);
            }
        }

        return templatePath;
    }

    private final File locateModuleTemplateInFileSystem(final String templateName) {
        File moduleTemplateFile = new File(new File(module.getBasePath(), "templates"), templateName);

        if (moduleTemplateFile.exists()) {
            if (!moduleTemplateFile.isFile()) {
                if (log.isWarnEnabled()) {
                    log.warn("The located module template '" + moduleTemplateFile.getAbsolutePath() + "' does not appear to be a regular file.");
                }
            }

            if (!moduleTemplateFile.canRead()) {
                if (log.isWarnEnabled()) {
                    log.warn("The located module template '" + moduleTemplateFile.getAbsolutePath() + "' does not have the necessary read permission.");
                }

            }

            if (log.isDebugEnabled()) {
                log.debug("Using module template '" + moduleTemplateFile.getAbsolutePath() + "'.");
            }

            return moduleTemplateFile;
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
