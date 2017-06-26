package com.geecommerce.guiwidgets.rest.v1;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.enums.ObjectType;
import com.geecommerce.core.rest.AbstractResource;
import com.geecommerce.core.rest.jersey.inject.FilterParam;
import com.geecommerce.core.rest.pojo.Filter;
import com.geecommerce.core.rest.pojo.Update;
import com.geecommerce.core.rest.service.RestService;
import com.geecommerce.core.system.ConfigurationKey;
import com.geecommerce.core.system.helper.UrlRewriteHelper;
import com.geecommerce.core.system.model.UrlRewrite;
import com.geecommerce.core.system.repository.UrlRewrites;
import com.geecommerce.core.template.model.Template;
import com.geecommerce.core.type.ContextObject;
import com.geecommerce.core.type.Id;
import com.geecommerce.core.util.Json;
import com.geecommerce.core.util.Strings;
import com.geecommerce.guiwidgets.helper.ContentUrlHelper;
import com.geecommerce.guiwidgets.model.Content;
import com.geecommerce.guiwidgets.service.ContentService;
import com.google.inject.Inject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.tidy.Tidy;

@Path("/v1/contents")
public class ContentResource extends AbstractResource {
    private final RestService service;
    private final ContentService contentService;
    private final UrlRewrites urlRewrites;
    private final UrlRewriteHelper urlRewriteHelper;
    private final ContentUrlHelper contentUrlHelper;

    @Inject
    public ContentResource(RestService service, ContentService contentService, UrlRewrites urlRewrites,
        UrlRewriteHelper urlRewriteHelper, ContentUrlHelper contentUrlHelper) {
        this.service = service;
        this.contentService = contentService;
        this.urlRewrites = urlRewrites;
        this.urlRewriteHelper = urlRewriteHelper;
        this.contentUrlHelper = contentUrlHelper;
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getContents(@FilterParam Filter filter) {
        return ok(service.get(Content.class, filter.getParams(), queryOptions(filter)));
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Content getContent(@PathParam("id") Id id) {
        return checked(service.get(Content.class, id));
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response createContent(Update update) throws IOException {
        String nodesJson = (String) update.getFields().get("content_nodes");
        ArrayList<HashMap> nodesMap = Json.fromJson(nodesJson, ArrayList.class);
        update.getFields().remove("content_nodes");
        update.getFields().put("content_nodes", nodesMap);

        nodesJson = (String) update.getFields().get("structure_nodes");
        nodesMap = Json.fromJson(nodesJson, ArrayList.class);
        update.getFields().remove("structure_nodes");
        update.getFields().put("structure_nodes", nodesMap);

        Content c = app.model(Content.class);
        c.fromMap(update.getFields());
        c.set(update.getFields());
        c.setTemplate(contentService.generateTemplate(c));

        updateTemplate(c);
        setContentKey(c);
        c = service.create(c);

        return created(c);
    }

    // TODO: check on uniqueness
    private void setContentKey(Content content) {
        String defaultLanguage = app.cpStr_(ConfigurationKey.I18N_CPANEL_DEFAULT_EDIT_LANGUAGE);
        if (content.getKey() == null || content.getKey().isEmpty()) {
            if (content.getName() != null) {
                String title = content.getName().getClosestValue(defaultLanguage);
                if (title == null) {
                    title = content.getName().getClosestValue();
                }
                if (title != null && !title.isEmpty()) {
                    String key = Strings.slugify2(title).replace(Char.MINUS, Char.UNDERSCORE);

                    while (key.indexOf(Str.UNDERSCORE_2X) != -1)
                        key = key.replace(Str.UNDERSCORE_2X, Str.UNDERSCORE);

                    if (key.endsWith(Str.UNDERSCORE))
                        key = key.substring(0, key.length() - 1);

                    content.setKey(key);
                }
            }
        }
    }

    @PUT
    @Path("{id}")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Content updateContent(@PathParam("id") Id id, Update update) throws IOException {
        Content c = null;
        try {
            if (id != null && update != null) {
                if (update.getFields().containsKey("content_nodes")) {
                    String nodesJson = (String) update.getFields().get("content_nodes");

                    ArrayList<HashMap> nodesMap = Json.fromJson(nodesJson, ArrayList.class);
                    update.getFields().remove("content_nodes");
                    update.getFields().put("content_nodes", nodesMap);
                }

                if (update.getFields().containsKey("structure_nodes")) {
                    String nodesJson = (String) update.getFields().get("structure_nodes");
                    ArrayList<HashMap> nodesMap = Json.fromJson(nodesJson, ArrayList.class);
                    update.getFields().remove("structure_nodes");
                    update.getFields().put("structure_nodes", nodesMap);
                }

                c = checked(service.get(Content.class, id));
                // c.fromMap(update.getFields());
                c.set(update.getFields());

                c.setTemplate(contentService.generateTemplate(c));

                updateTemplate(c);

                setContentKey(c);
                service.update(c);
            }
        } catch (Exception ex) {
            service.update(c);
            ex.printStackTrace();
        }
        return checked(service.get(Content.class, id));
    }


    private void updateTemplate(Content c){
        String parsedTemplate = c.getTemplate();



//        try{
//            Tidy tidy = new Tidy(); // obtain a new Tidy instance
//            tidy.setXHTML(true);
//            StringWriter stringWriter = new StringWriter();
//            tidy.parse(new StringReader(c.getTemplate()), stringWriter);
//            parsedTemplate = stringWriter.toString();
//        } catch (Exception ex){
//            System.out.println(ex);
//        }



       // FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();

        Document doc = Jsoup.parse(parsedTemplate);
        parsedTemplate = doc.html();

        if(c.getTemplateId() == null){
            Template template = app.model(Template.class);
            template.setTemplate(parsedTemplate);
            template.setLabel(c.getName());

            template = service.create(template);
            c.setTemplateId(template.getId());
        } else {
            Template template = service.get(Template.class, c.getTemplateId());
            if(template != null){
                template.setTemplate(parsedTemplate);
                template.setLabel(c.getName());

                service.update(template);
            } else {
                template = app.model(Template.class);
                template.setTemplate(parsedTemplate);
                template.setLabel(c.getName());

                template = service.create(template);
                c.setTemplateId(template.getId());
            }
        }
    }

    @SuppressWarnings("unchecked")
    @POST
    @Path("{id}/url/validation")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getIsUrlUnique(@PathParam("id") Id id, Update update) {
        ContextObject<String> urls = (ContextObject<String>) update.getFields().get("rewriteUrl");
        return ok(checked(urlRewriteHelper.isUriUnique(ObjectType.CMS, id, urls)));
    }

    @SuppressWarnings("unchecked")
    @POST
    @Path("/url/validation")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getIsUrlUnique(Update update) {
        ContextObject<String> urls = (ContextObject<String>) update.getFields().get("rewriteUrl");
        return ok(checked(urlRewriteHelper.isUriUnique(urls)));
    }

    @GET
    @Path("{id}/url")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getRewriteUrl(@PathParam("id") Id contentId) {
        UrlRewrite urlRewrite = urlRewrites.forCMS(contentId);
        if (urlRewrite == null) {
            urlRewrite = app.model(UrlRewrite.class);
        }
        return ok(checked(urlRewrite));
    }

    @SuppressWarnings("unchecked")
    @PUT
    @Path("{id}/url")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response updateRewriteUrl(@PathParam("id") Id contentId, Update update) {
        boolean autoGenerate = Boolean.parseBoolean((String) update.getFields().get("auto"));
        boolean empty = autoGenerate ? false : true;
        ContextObject<String> urls = (ContextObject<String>) update.getFields().get("rewriteUrl");

        Content content = checked(service.get(Content.class, contentId));
        UrlRewrite urlRewrite = urlRewrites.forCMS(contentId);
        if (urlRewrite == null) {
            urlRewrite = app.model(UrlRewrite.class);
            urlRewrite.setRequestURI(new ContextObject<String>());
            urlRewrite.setEnabled(true);
            urlRewrite.setRequestMethod("GET");
            urlRewrite.setTargetObjectId(contentId);
            urlRewrite.setTargetObjectType(ObjectType.CMS);
            urlRewrite.setTargetURL("/content/page/" + contentId);
        }

        urlRewrite.setRequestURI(urls);

        if (urlRewrite.getRequestURI() == null)
            urlRewrite.setRequestURI(new ContextObject<String>());

        contentUrlHelper.generateUniqueUri(content, urlRewrite, empty);

        if (urlRewrite.getId() == null)
            urlRewrites.add(urlRewrite);
        else
            urlRewrites.update(urlRewrite);

        if (urlRewrite != null && (urlRewrite.getRequestURI() == null || urlRewrite.getRequestURI().size() == 0)
            && urlRewrite.getId() != null) {
            urlRewrites.remove(urlRewrite);
        }

        return ok(checked(urlRewrite));
    }

}
