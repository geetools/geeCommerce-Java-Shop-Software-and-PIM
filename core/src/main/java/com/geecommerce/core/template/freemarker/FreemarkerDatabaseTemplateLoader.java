package com.geecommerce.core.template.freemarker;

import com.geecommerce.core.App;
import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.cache.Cache;
import com.geecommerce.core.cache.CacheManager;
import com.geecommerce.core.config.SystemConfig;
import com.geecommerce.core.template.model.Template;
import com.geecommerce.core.template.repository.Templates;
import com.geecommerce.core.type.Id;
import freemarker.cache.TemplateLoader;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.time.Year;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FreemarkerDatabaseTemplateLoader implements TemplateLoader {
    @Override
    public Object findTemplateSource(String name) throws IOException {

        String relativeName = getRelativePath(name);

        CacheManager cm = App.get().inject(CacheManager.class);
        Cache<String, Template> c = cm.getCache(getClass().getName());

        Templates templates = App.get().inject(Templates.class);

        Template template = c.get(relativeName);
        if(template != null)
            return template;
        else {
            if(relativeName.contains("_template_id_")){

                String pattern = "_template_id_(\\d+)";
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(relativeName);
                if(m.find()) {
                    Id id = Id.parseId(m.group(1));
                    System.out.println("DB_LOADER: id " + id);
                    template = templates.findById(Template.class, id);
                    if(template != null){
                        c.put(relativeName, template);
                        return template;
                    }
                }
            } else {
                template = templates.getByUri(relativeName);
                if(template != null){
                    c.put(relativeName, template);
                    return template;
                }
            }

        }

        return null;
    }

    @Override
    public long getLastModified(Object templateSource) {
        if(templateSource instanceof Template){
            return ((Template) templateSource).getModifiedOn() != null? ((Template) templateSource).getModifiedOn().getTime(): 0;
        }
        return 0;
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        if(templateSource instanceof Template){
            return new StringReader(((Template) templateSource).getTemplate());
        }
        return null;
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {

    }

    private final String getTemplatesBasePath() {
        String templatesPath = SystemConfig.GET.val(SystemConfig.APPLICATION_TEMPLATE_PATH);

        if (templatesPath == null) {
            throw new IllegalStateException(
                    "The System.properties configuration element 'Application.Template.Path' cannot be null");
        }

        templatesPath = templatesPath.trim();

        StringBuilder sb = new StringBuilder(templatesPath);

        if (!templatesPath.endsWith(Str.SLASH)) {
            sb.append(Char.SLASH);
        }

        return sb.toString();
    }

    private String getRelativePath(String name){
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
        return inName.toString();
    }
}
